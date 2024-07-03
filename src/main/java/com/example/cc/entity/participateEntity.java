package com.example.cc.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "participate")
public class participateEntity {
    // 학번(PK, FK)
    @Id
    @ManyToOne
    @JoinColumn(name = "studentId")
    private userAuthenticationEntity studentId;

    // 채팅방 id
    @Id
    @ManyToOne
    @JoinColumn(name = "roomId")
    private chatRoomEntity roomId;
}
