package com.example.cc.dto.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class chatRoomDTO {
    private Long roomId;
    private LocalDateTime createDate;
    private Long peopleNum;
    private Long roomType;
}
