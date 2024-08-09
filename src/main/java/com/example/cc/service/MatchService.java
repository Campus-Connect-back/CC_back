package com.example.cc.service;

import com.example.cc.config.PrincipalDetails;
import com.example.cc.dto.accounts.*;
import com.example.cc.dto.chating.MatchingDTO;
import com.example.cc.dto.chating.chatRoomDTO;
import com.example.cc.dto.chating.messageDTO;
import com.example.cc.dto.chating.participateDTO;
import com.example.cc.entity.*;
import com.example.cc.repository.ChatRoomRepository;
import com.example.cc.repository.ParticipateRepository;
import com.example.cc.repository.UsersRepository;
import com.example.cc.repository.accounts.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MatchService {
    private final RedisTemplate<String, String> redisTemplate;
    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ParticipateRepository participateRepository;
    private SetOperations<String, String> setOps;
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private volatile boolean isMatching = false;
    private Map<String, Boolean> userMatchingStatus = new ConcurrentHashMap<>();
    private final TransactionTemplate transactionTemplate;

    @PostConstruct
    private void init() {
        setOps = redisTemplate.opsForSet();
    }

    @Transactional
    // 매칭 버튼 누르면
    public chatRoomDTO startMatch(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        // 로그인한 사용자의 userId 반환하기
        usersEntity user = userRepository.findByStudentId_StudentId(principalDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다"));

        String userId = user.getUserId().toString();

        // 구사 가능 언어 set으로 저장
        for (availableLangEntity lang : user.getAvailableLang()) {
            setOps.add("availableLang:" + lang.getLang(), userId);
        }

        // 희망 학습 언어 set으로 저장하기
        for (desiredLangEntity lang : user.getDesiredLang()) {
            setOps.add("desiredLang:" + lang.getLang(), userId);
        }

        // 유저 매칭하기
        isMatching = true;
        userMatchingStatus.put(userId, true);
        // 비동기로 사용자 매칭
        CompletableFuture<chatRoomDTO> future = CompletableFuture.supplyAsync(() -> {
            try {
                return transactionTemplate.execute(status -> {
                    try{
                        return matchUser(user);
                    }catch (InterruptedException e){
                        Thread.currentThread().interrupt();
                        throw new RuntimeException(e);
                    }
                });
            } catch (Exception e) {
                System.out.println("매칭이 취소되었습니다.");
                return null;
            }
        }, executorService);
        // 20초 타임아웃
        try {
            return future.get(20, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            future.cancel(true);
            System.out.println("매칭 실패: 시간이 초과되었습니다.");
            return null;
        } catch (Exception e) {
            throw new RuntimeException("매칭 중 오류가 발생했습니다.", e);
        } finally {
            userMatchingStatus.put(userId, false);
            removeUserFromQueue(user);
        }
    }

    // 유저 매칭하기
    private chatRoomDTO matchUser(usersEntity user) throws InterruptedException {
        String userId = String.valueOf(user.getUserId());
        long startTime = System.currentTimeMillis();
        long timeout = 20000; // 20초

        // 20초 동안 매칭되는 사람 있는지
        while (System.currentTimeMillis() - startTime < timeout) {
            for (desiredLangEntity desiredLang : user.getDesiredLang()) {
                Set<String> matchedUserIds = setOps.members("availableLang:" + desiredLang.getLang());
                matchedUserIds.remove(userId); // 현재 사용자 제거
                // 대기열에 있는 사용자만 매칭을 시도
                if (matchedUserIds.isEmpty()) {
                    continue; // 대기열에 아무도 없으면 다음 반복
                }

                for (String matchedUserId : matchedUserIds) {
                    Long matchUserId = Long.valueOf(matchedUserId);
                    usersEntity matchedUser = getUserFromRedis(matchUserId);
                    // 매칭된 사용자가 null인 경우, 로그를 남기고 다음 사용자로 넘어감
                    if (matchedUser == null) {
                        System.out.println("매칭된 사용자 정보를 찾을 수 없습니다: " + matchedUserId);
                        continue; // 다음 사용자로 넘어가기
                    }
                    // 매칭되는 사람이 있으면
                    if (isMatch(matchedUser, user)) {
                        // 채팅방 만들기
                        chatRoomDTO createdRoom = createChatRoom(matchedUser, user);
                        removeUserFromQueue(user);
                        return createdRoom;
                    }
                }
            }

            // 매칭이 취소되었는지 확인
            if (!isMatching) {
                removeUserFromQueue(user);
                System.out.println("매칭이 취소되었습니다.");
                throw new InterruptedException("매칭 작업이 취소되었습니다.");
            }

            try {
                Thread.sleep(1000); // 1초마다 반복
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new InterruptedException("매칭 시도가 중단되었습니다.");
            }
        }

        return null;
    }

    // 매칭 조건 검사하기
    private boolean isMatch(usersEntity user1, usersEntity user2) {
        // 내가 선택한 희망 구사언어-상대방의 구사 가능 언어 하나라도 일치하면 매칭됨
        Set<String> user1DesiredLangs = user1.getDesiredLang().stream()
                .map(desiredLangEntity::getLang)
                .collect(Collectors.toSet());
        Set<String> user2DesiredLangs = user2.getDesiredLang().stream()
                .map(desiredLangEntity::getLang)
                .collect(Collectors.toSet());

        Set<String> user2AvailableLangs = user2.getAvailableLang().stream()
                .map(availableLangEntity::getLang)
                .collect(Collectors.toSet());
        Set<String> user1AvailableLangs = user1.getAvailableLang().stream()
                .map(availableLangEntity::getLang)
                .collect(Collectors.toSet());

        return user1AvailableLangs.stream().anyMatch(user2DesiredLangs::contains) &&
                user2AvailableLangs.stream().anyMatch(user1DesiredLangs::contains);
    }

    // 유저 정보 가져오기
    private usersEntity getUserFromRedis(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    // 채팅방 생성

    private chatRoomDTO createChatRoom(usersEntity user1, usersEntity user2) {
        // 동시성 제어->동일한 채팅방 생기지 않게
        Optional<chatRoomEntity> existingRoom = chatRoomRepository.findByRoomName(user1.getNickName(), user2.getNickName());
        if (existingRoom.isPresent()) {
            return convertDTO(existingRoom.get());
        }
        //채팅방 만들기
        chatRoomEntity chatRoom = chatRoomRepository.save(chatRoomEntity.builder()
                .createDate(new Date())
                .roomName(user1.getNickName() + user2.getNickName())
                .peopleNum(2L)
                .roomType(0L)
                .build());

        // participateEntity에 추가하기
        participateRepository.findByUserAndRoomId(user1,chatRoom).orElseGet(()->
                participateRepository.save(participateEntity.builder()
                        .roomId(chatRoom)
                        .userId(user1)
                        .build())
        );

        participateRepository.findByUserAndRoomId(user2, chatRoom).orElseGet(() ->
                participateRepository.save(participateEntity.builder()
                        .roomId(chatRoom)
                        .userId(user2)
                        .build())
        );

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
    // 매칭 취소
    public void cancelMatch() {
        isMatching = false;
    }
    // 매칭 결과
    public MatchingDTO matchingResult(@RequestParam long roomId) {
        chatRoomEntity chatRoom = chatRoomRepository.findByRoomId(roomId);
        List<participateEntity> participates = participateRepository.findByRoomId(chatRoom);
        participateEntity participate1 = participates.get(0);
        participateEntity participate2 = participates.get(1);
        usersEntity user1 = participate1.getUserId();
        usersEntity user2 = participate2.getUserId();

        MatchingDTO result = MatchingDTO.builder()
                .chatRoomDTO(chatRoomDTO.builder()
                        .roomId(chatRoom.getRoomId())
                        .build())
                .user1DTO(usersDTO.builder()
                        .userId(user1.getUserId())
                        .nationality(user1.getNationality()) // 국적 추가
                        .languages(user1.getDesiredLang().stream()
                                .map(desiredLangEntity::getLang) // 사용 가능한 언어 리스트
                                .collect(Collectors.toList()))
                        .build())
                .user2DTO(usersDTO.builder()
                        .userId(user2.getUserId())
                        .nationality(user2.getNationality()) // 국적 추가
                        .languages(user2.getDesiredLang().stream()
                                .map(desiredLangEntity::getLang) // 사용 가능한 언어 리스트
                                .collect(Collectors.toList()))
                        .build())
                .build();
        return result;
    }
}