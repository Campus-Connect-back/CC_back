package com.example.cc.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@ToString
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Setter
@Table(name = "post")
public class postEntity {
//   postid
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

//  유저 fk
    @ManyToOne
    @JoinColumn(name = "userId", referencedColumnName = "userId")
    private usersEntity userId;

//   채팅룸 fk
    @OneToOne
    @JoinColumn(name = "chatRoomId", referencedColumnName = "roomId")
    private chatRoomEntity chatRoomId;

//   타이틀
    private String postTitle;

//   내용
    private String postContent;

//   모집언어
    private String language;

//   대면비대면
    private String faceToFace;

//   주에만날 횟수
    private Long DayOfWeek;

//   몇시에시작할지
    private LocalDateTime TimeInfo;

}
