package com.example.cc.controller;

import com.example.cc.config.PrincipalDetails;
import com.example.cc.dto.chating.ChatRoomWithMessageDTO;
import com.example.cc.entity.chatRoomEntity;
import com.example.cc.entity.messageEntity;
import com.example.cc.entity.usersEntity;
import com.example.cc.repository.ChatRoomRepository;
import com.example.cc.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatRoomController {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomService chatRoomService;

    // 로그인한 사용자의 1대1 채팅방 목록 반환
    @GetMapping("/roomList/match")
    public List<ChatRoomWithMessageDTO> getMatchRooms(@AuthenticationPrincipal PrincipalDetails principal) {
        return chatRoomService.getMatchRooms(principal);
    }

    // 로그인한 사용자의 그룹 채팅방 목록 반환
    @GetMapping("/roomList/group")
    public List<ChatRoomWithMessageDTO> getGroupRooms(@AuthenticationPrincipal PrincipalDetails principal) {
        return chatRoomService.getGroupRooms(principal);
    }

    // 특정 채팅방 조회(이전 채팅 기록 보여줌)
    @GetMapping("/room/{roomId}")
    public List<messageEntity> getRoom(@PathVariable Long roomId) {
        return chatRoomService.roomDetail(roomId);
    }

    // 채팅방 생성
    @PostMapping("/room")
    public chatRoomEntity createRoom(@RequestParam Long user1, Long user2) {
        return chatRoomService.createChatRoom(user1, user2);
    }

    // 채팅방 나가기
    @DeleteMapping("/room/{roomId}")
    public void exitRoom(@AuthenticationPrincipal PrincipalDetails principal, @PathVariable Long roomId) {
        chatRoomService.exitRoom(principal, roomId);
    }
    // 채팅방 참여자 목록
    @GetMapping("/room/{roomId}/member")
    public List<usersEntity> getMembers(@AuthenticationPrincipal PrincipalDetails principal, @PathVariable Long roomId) {
        return chatRoomService.getMembers(principal, roomId);
    }
}
