package com.example.cc.dto.chating;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomWithMessageDTO {
    private Long roomId;
    private String roomName;
    private Long roomType;
    private String messageContent;
    private Date mesaageTimestamp;
}
