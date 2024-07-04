package com.example.cc.dto.post;

import com.example.cc.entity.usersEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class postDTO {
        private Long postId;
        private Long dayOfWeek;
        private LocalDateTime timeInfo;
        private String faceToFace;
        private String language;
        private String postContent;
        private String postTitle;
        private Long studentId;
        private Long peopleNum;
        private String chatRoomName;
        private Long roomType;
        private List<weeklyDTO> weeklyInfos;

}
