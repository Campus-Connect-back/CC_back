package com.example.cc.dto.chating;

import com.example.cc.entity.chatRoomEntity;
import com.example.cc.entity.userAuthenticationEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class messageDTO {
    // 메시지 타입: 입장, 채팅, 퇴장
    public enum MessageType{
        ENTER, TALK, LEAVE;
    }
    private Long messageId;
    private String messageContent;
    private Date sendTime;
    private String studentId;
    private Long roomId;
    private MessageType messageType;
}
