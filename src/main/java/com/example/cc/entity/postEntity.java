package com.example.cc.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

@ToString
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "post")
public class postEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    @ManyToOne
    @JoinColumn(name = "studentId", referencedColumnName = "studentId")
    private usersEntity studentId;

    @OneToOne
    @JoinColumn(name = "chatRoomId", referencedColumnName = "roomId")
    private chatRoomEntity chatRoomId;

    private String postTitle;

    private String postContent;

    private String language;

    private String faceToFace;

    private Long DayOfWeek;

    private LocalDateTime TimeInfo;

}
