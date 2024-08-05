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
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long participateId;
    // 유저(FK)
    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private usersEntity userId;

    // 채팅방 id
    @ManyToOne
    @JoinColumn(name = "roomId")
    private chatRoomEntity roomId;
}
