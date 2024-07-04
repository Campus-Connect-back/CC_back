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
@Table(name = "chat_room")
public class chatRoomEntity {
    //채팅방 id(PK)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomId;

    //채팅방 이름
    @Column(length = 100, nullable = false)
    private String roomName;

    //채팅방 정원
    @Column(columnDefinition = "integer default 2", nullable = false)
    private Long peopleNum;

    //생성 날짜
    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;

    //채팅방 종류
    @Column(nullable = false, columnDefinition = "int default 0")
    private Long roomType;


}
