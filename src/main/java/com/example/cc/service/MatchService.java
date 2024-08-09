package com.example.cc.service;

import com.example.cc.config.PrincipalDetails;
import com.example.cc.dto.chating.chatRoomDTO;
import com.example.cc.dto.chating.messageDTO;
import com.example.cc.entity.*;
import com.example.cc.repository.ChatRoomRepository;
import com.example.cc.repository.ParticipateRepository;
import com.example.cc.repository.UsersRepository;
import com.example.cc.repository.accounts.UserRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.util.Date;
import java.util.Set;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MatchService {
    private final RedisTemplate<String,String> redisTemplate;
    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ParticipateRepository participateRepository;
    private  SetOperations<String, String> setOps;
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    @PostConstruct
    private void init() {
        setOps = redisTemplate.opsForSet();
    }

    @Transactional
    // 매칭 버튼 누르면
    public chatRoomDTO startMatch(@AuthenticationPrincipal PrincipalDetails principalDetails){
        //  로그인한 사용자의 userId 반환하기
        usersEntity user = userRepository.findByStudentId_StudentId(principalDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다"));

        String userId = user.getUserId().toString();
        // 사용자의 구사 가능 언어 set으로 저장하기
        for (availableLangEntity lang : user.getAvailableLang()){
            setOps.add("availableLang:" + lang.getLang(), userId);
        }
        // 사용자의 희망 학습 언어 set으로 저장하기
        for (desiredLangEntity lang : user.getDesiredLang()){
            setOps.add("desiredLang:" + lang.getLang(), userId);
        }
        // 유저 매칭하기
        CompletableFuture<chatRoomDTO> future = CompletableFuture.supplyAsync(() -> matchUser(user), executorService);

        try {
            // 20초 동안 매칭 시도
            return future.get(20, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            // 매칭 실패 시 처리
            future.cancel(true); // 매칭이 20초 동안 이루어지지 않으면 작업 취소
            System.out.println("매칭 실패: 시간이 초과되었습니다.");
            return null; // 또는 매칭 실패를 나타내는 다른 처리
        } catch (Exception e) {
            throw new RuntimeException("매칭 중 오류가 발생했습니다.", e);
        }
    }

    // 유저 매칭하기
    private chatRoomDTO matchUser(usersEntity user){
            //  로그인한 사용자의 userId 반환하기
            String userId = String.valueOf(user.getUserId());

            // redis 대기열 돌면서 매칭되는 사람이 있는지 검사
            for (desiredLangEntity desiredLang : user.getDesiredLang()) {
                Set<String> matchedUserIds = setOps.members("availableLang:" + desiredLang.getLang());
                matchedUserIds.remove(userId);  // 자기 자신을 제거

                for (String matchedUserId : matchedUserIds) {
                    Long matchUserId = Long.valueOf(matchedUserId);
                    usersEntity matchedUser = getUserFromRedis(matchUserId);
                    System.out.println("Matching candidates for language " + desiredLang.getLang() + ": " + matchedUserIds);
                    // 매칭되는 사람이 있으면
                    if (isMatch(matchedUser, user)) {
                        // 매칭 성사
                        System.out.println("Matching successful between: " + user.getNickName() + " and " + matchedUser.getNickName());
                        // 채팅방 만들기
                        chatRoomDTO createdRoom = createChatRoom(matchedUser, user);
                        // 대기열에서 매칭된 사용자 정보 지우기
                        removeUserFromQueue(matchedUser);
                        removeUserFromQueue(user);
                        return createdRoom;
                    }
                }
            }
        return null;

    }

    // 매칭 조건 검사하기
    private boolean isMatch(usersEntity user1, usersEntity user2){
        // 내가 선택한 희망 구사언어-상대방의 구사 가능 언어 하나라도 일치하면 매칭됨
        Set<String> user1DesiredLangs = user1.getDesiredLang().stream()
                .map(desiredLangEntity::getLang)
                .collect(Collectors.toSet());

        Set<String> user2AvailableLangs = user2.getAvailableLang().stream()
                .map(availableLangEntity::getLang)
                .collect(Collectors.toSet());

        Set<String> user2DesiredLangs = user2.getDesiredLang().stream()
                .map(desiredLangEntity::getLang)
                .collect(Collectors.toSet());

        Set<String> user1AvailableLangs = user1.getAvailableLang().stream()
                .map(availableLangEntity::getLang)
                .collect(Collectors.toSet());

        return user1AvailableLangs.stream().anyMatch(user2DesiredLangs::contains) &&
                user2AvailableLangs.stream().anyMatch(user1DesiredLangs::contains);
    }

    // 유저 정보 가져오기
    private usersEntity getUserFromRedis(Long userId){
        return userRepository.findById(userId).orElse(null);
    }

    // 채팅방 생성
    private chatRoomDTO createChatRoom(usersEntity user1, usersEntity user2){
        //채팅방 만들기
        chatRoomEntity chatRoom =  chatRoomRepository.save(chatRoomEntity.builder()
                .createDate(new Date())
                .roomName(user1.getNickName()+user2.getNickName())
                .peopleNum(2L)
                .roomType(0L)
                .build());

        // participateEntity에 추가하기
        participateEntity participate1 =  participateRepository.save(participateEntity.builder()
                .roomId(chatRoom)
                .userId(user1)
                .build());

        participateEntity participate2 =  participateRepository.save(participateEntity.builder()
                .roomId(chatRoom)
                .userId(user2)
                .build());
        return convertDTO(chatRoom);
    }
    public chatRoomDTO convertDTO(chatRoomEntity chatRoom) {
        chatRoomDTO chatRoomDTO = new chatRoomDTO();
        chatRoomDTO.setRoomId(chatRoom.getRoomId());
        chatRoomDTO.setRoomName(chatRoom.getRoomName());
        chatRoomDTO.setRoomType(chatRoom.getRoomType());
        return chatRoomDTO;
    }

    // 매칭 성사되면 대기큐에서 삭제하기
    private void removeUserFromQueue(usersEntity user) {
        String userId = user.getUserId().toString();

        for (availableLangEntity lang : user.getAvailableLang()) {
            setOps.remove("availableLang:" + lang.getLang(), userId);
        }

        for (desiredLangEntity lang : user.getDesiredLang()) {
            setOps.remove("desiredLang:" + lang.getLang(), userId);
        }
    }

}
