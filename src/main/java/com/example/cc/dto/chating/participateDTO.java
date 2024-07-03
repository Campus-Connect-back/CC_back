package com.example.cc.dto.chating;

import com.example.cc.entity.chatRoomEntity;
import com.example.cc.entity.userAuthenticationEntity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class participateDTO {
    private Long studentId;
    private Long roomId;
}
