package com.example.cc.dto.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//게시글 수정위한 dto
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class updateDTO {
    private Long postId;
    private String postContent;
    private String postTitle;
}
