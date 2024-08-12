package com.example.cc.controller;

import com.example.cc.config.PrincipalDetails;
import com.example.cc.dto.chating.messageDTO;
import com.example.cc.entity.messageEntity;
import com.example.cc.entity.usersEntity;
import com.example.cc.repository.accounts.UserRepository;
import com.example.cc.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MessageController {
    // SimpMessageSendingOperations: 메시지를 특정 목적지로 전송하는 기능 제공
    private final SimpMessageSendingOperations template;
    private final UserRepository userRepository;
    private final MessageService messageService;

    /* 클라이언트가 메시지를 send하는 경로
    * WebSocketConfig에서 정의한 applicationDestinationPrefixes와 @MessageMapping 경로가 병합됨
    * /pub 으로 발행자가 메시지를 보내면 브로커가 /sub 경로로 구독자에게 메시지를 보냄
    */


    // 채팅방에 입장했을 때
    @MessageMapping(value = "/chat/enter")
    public void enterUser(messageDTO chat,@AuthenticationPrincipal PrincipalDetails principal) {
        usersEntity user = userRepository.findByStudentId_StudentId(principal.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다"));
        chat.setMessageContent(user.getNickName() + "님이 채팅방에 참여하였습니다.");
        // "/sub/chat/room/" + chat.getRoomId() 해당 경로로 메시지를 전송함
        template.convertAndSend("/sub/chat/room/" + chat.getRoomId(), chat);
    }

    // 채팅방에서 퇴장했을 때
    @MessageMapping(value = "/chat/exit")
    public void exitUser(messageDTO chat,@AuthenticationPrincipal PrincipalDetails principal) {
        usersEntity user = userRepository.findByStudentId_StudentId(principal.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다"));

        chat.setMessageContent(user.getNickName() + "님이 퇴장하였습니다.");
        template.convertAndSend("/sub/chat/room/" + chat.getRoomId(), chat);
    }

    // 기본 채팅
    @MessageMapping(value = "/chat/message")
    public void sendMessage(messageDTO message, @AuthenticationPrincipal PrincipalDetails principal) {
        messageDTO savedMessage = messageService.saveMessage(message, principal); // 메시지 db에 저장
        template.convertAndSend("/sub/chat/room/"+savedMessage.getRoomId(), savedMessage);
    }

}
