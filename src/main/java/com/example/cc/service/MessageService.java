package com.example.cc.service;

import com.example.cc.dto.chating.messageDTO;
import com.example.cc.entity.chatRoomEntity;
import com.example.cc.entity.messageEntity;
import com.example.cc.entity.usersEntity;
import com.example.cc.repository.ChatRoomRepository;
import com.example.cc.repository.MessageRepository;
import com.example.cc.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    private final UsersRepository usersRepository;
    private final ChatRoomRepository chatRoomRepository;

    // 메시지 저장
    public messageDTO saveMessage(messageDTO messageDTO) {
        usersEntity user = usersRepository.findById(messageDTO.getUserId()).get();
        chatRoomEntity room = chatRoomRepository.findById(messageDTO.getRoomId()).get();
        messageEntity message = messageRepository.save(messageEntity.builder()
                .messageContent(messageDTO.getMessageContent())
                .sendTime(new Date()) // 현재 시간으로 전송 시간 설정
                .userId(user)
                .roomId(room)
                .build()
        );
        return convertDTO(message);
    }

    public messageDTO convertDTO(messageEntity message) {
        messageDTO messageDTO = new messageDTO();
        messageDTO.setMessageId(message.getMessageId());
        messageDTO.setMessageContent(message.getMessageContent());
        messageDTO.setSendTime(message.getSendTime());
        messageDTO.setUserId(message.getUserId().getUserId());
        messageDTO.setRoomId(message.getRoomId().getRoomId());
        return messageDTO;
    }
}
