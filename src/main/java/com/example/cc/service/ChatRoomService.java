package com.example.cc.service;

import com.example.cc.config.PrincipalDetails;
import com.example.cc.entity.chatRoomEntity;
import com.example.cc.entity.messageEntity;
import com.example.cc.entity.participateEntity;
import com.example.cc.entity.usersEntity;
import com.example.cc.repository.ChatRoomRepository;
import com.example.cc.repository.MessageRepository;
import com.example.cc.repository.ParticipateRepository;
import com.example.cc.repository.accounts.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final MessageRepository messageRepository;
    private final ParticipateRepository participateRepository;
    private final UserRepository userRepository;

    public chatRoomEntity createChatRoom(@RequestParam Long user1, Long user2){
        return chatRoomRepository.save(new chatRoomEntity());
    }

    // 1:1 랜덤 채팅 목록 반환
    public List<chatRoomEntity> getMatchRooms(@AuthenticationPrincipal PrincipalDetails principalDetails){
        usersEntity user = userRepository.findByStudentId_StudentId(principalDetails.getUsername())
             .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다"));

        List<participateEntity> getAllRooms = participateRepository.findByUserId(user); // 로그인한 사용자의 모든 room 찾기

        List<chatRoomEntity> matchRooms = new ArrayList<>();
        for(int i = 0; i <getAllRooms.size(); i++){
            chatRoomEntity matchRoom = chatRoomRepository.findByRoomId(getAllRooms.get(i).getRoomId().getRoomId());
            // roomType이 0(1대1 매칭)인 채팅방 찾아서 list에 넣기
            if(matchRoom.getRoomType()==0){
                matchRooms.add(matchRoom);
            }
        }
        return matchRooms;
    }

    // 그룹 채팅 목록 반환
    public List<chatRoomEntity> getGroupRooms(@AuthenticationPrincipal PrincipalDetails principalDetails){
        usersEntity user = userRepository.findByStudentId_StudentId(principalDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다"));

        List<participateEntity> getAllRooms = participateRepository.findByUserId(user); // 로그인한 사용자의 모든 room 찾기

        List<chatRoomEntity> groupRooms = new ArrayList<>();
        for(int i = 0; i <getAllRooms.size(); i++){
            chatRoomEntity groupRoom = chatRoomRepository.findByRoomId(getAllRooms.get(i).getRoomId().getRoomId());
            // roomType이 1(스터디 그룹)인 채팅방 찾아서 list에 넣기
            if(groupRoom.getRoomType()==1){
                groupRooms.add(groupRoom);
            }
        }
        return groupRooms;
    }
    // 채팅방 상세 보기
    public List<messageEntity> roomDetail(Long roomId){
        chatRoomEntity chatRoom = chatRoomRepository.findByRoomId(roomId);
        return messageRepository.findByRoomId(chatRoom);
    }

    // 채팅방 나가기
    @Transactional
    public void exitRoom(@AuthenticationPrincipal PrincipalDetails principalDetails, Long roomId){
        usersEntity user = userRepository.findByStudentId_StudentId(principalDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다"));
        chatRoomEntity chatRoom = chatRoomRepository.findByRoomId(roomId);

        participateRepository.deleteByUserIdAndRoomId(user.getUserId(), roomId);
        // 채팅방에 있는 모든 사람이 방을 나가게 되면 chatRoomEntity에서도 삭제됨
        List<participateEntity> remainingParticipants = participateRepository.findByRoomId(chatRoom);
        if(remainingParticipants.isEmpty()){
            chatRoomRepository.delete(chatRoom);
        }
    }

    // 채팅방 참여자 목록
    // 그룹 채팅 목록 반환
    public List<usersEntity> getMembers(@AuthenticationPrincipal PrincipalDetails principalDetails, Long roomId){
        // 로그인한 사용자의 학번으로 usersEntity에서 객체 찾기
        usersEntity user = userRepository.findByStudentId_StudentId(principalDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다"));
        // roomId로 채팅방 객체 찾기
        chatRoomEntity room = chatRoomRepository.findByRoomId(roomId);
        // 해당 채팅방에 참여한 모든 유저 찾기
        List<participateEntity> getAllUsers = participateRepository.findByRoomId(room);
        List<usersEntity> allUsers = new ArrayList<>();
        for(int i = 0; i <getAllUsers.size(); i++){
            usersEntity getUser = userRepository.findByUserId(getAllUsers.get(i).getUserId().getUserId());
            // roomType이 1(스터디 그룹)인 채팅방 찾아서 list에 넣기
            allUsers.add(getUser);
        }
        return allUsers;
    }
}
