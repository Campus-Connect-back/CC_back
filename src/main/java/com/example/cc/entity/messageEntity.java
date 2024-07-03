package com.example.cc.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@ToString
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "message")
public class messageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long messageId;

    //메시지 내용
    @Column(columnDefinition = "TEXT")
    private String messageContent;

    // 보낸 시간
    @Temporal(TemporalType.TIMESTAMP)
    private Date sendTime;

    // 유저(FK)
    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private usersEntity userId;

    // 채팅방 id(FK)
    @ManyToOne
    @JoinColumn(name = "roodmId", nullable = false)
    private chatRoomEntity roomId;
}
