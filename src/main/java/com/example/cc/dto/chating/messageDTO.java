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
    private Long messageId;
    private String messageContent;
    private Date sendTime;
    private Long studentId;
    private Long roomId;
}
