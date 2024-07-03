package com.example.cc.entity;

import jakarta.persistence.*;
import lombok.*;

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

    private String weeklyParticipation;

    private String faceToFace;

    private String DayOfWeek;

    private String TimeInfo;

}
