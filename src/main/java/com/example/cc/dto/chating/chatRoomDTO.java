package com.example.cc.dto.chating;

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

public class chatRoomDTO {
    private Long roomId;
    private String roomName;
    private Long peopleNum;
    private Date createDate;
    private Long roomType;
}
