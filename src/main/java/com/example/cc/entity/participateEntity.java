package com.example.cc.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@IdClass(participateId.class)
@Table(name = "participate")
public class participateEntity {
    // 유저(FK)
    @Id
    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private usersEntity userId;

    // 채팅방 id
    @Id
    @ManyToOne
    @JoinColumn(name = "roomId")
    private chatRoomEntity roomId;
}
