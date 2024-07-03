package com.example.cc.entity.post;

import jakarta.persistence.*;
import lombok.*;

@ToString
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "TBL_post")
public class postEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    private Long studyNumber;

    private Long chatRoomId;

    private String postTitle;

    private String postContent;

    private String language;

    private int recruitmentLimit;

    private String weeklyParticipation;

    private String faceToFace;

    private String DayOfWeek;

    private String TimeInfo;

}
