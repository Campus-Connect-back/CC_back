package com.example.cc.controller.post;

import com.example.cc.dto.post.postDTO;
import com.example.cc.dto.post.updateDTO;
import com.example.cc.entity.postEntity;
import com.example.cc.service.postService;
import jakarta.persistence.PostUpdate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class postController {

//    서비스 주입
    private final postService postService;

//    스터디게시판 게시글 작성
    @PostMapping("/writePost")
    public void insertPost(@RequestBody postDTO postDTO){

        postService.insertPost(postDTO);
    }


//  해당 post 삭제
    @DeleteMapping("/writedDelete/{postId}")
    public void deletePost(@PathVariable Long postId){
        postService.deletePost(postId);

    }

//  작성글 세부정보/채팅방 정보도 여기서 받아서 띄움
    @GetMapping("/MyPost/{postId}")
    public postEntity selectPost(@PathVariable Long postId){
       return postService.selectById(postId);
    }

// 작성글 List 띄우기
    @GetMapping("/postList")
    public List<postEntity> selectPosts(@RequestParam(name = "language", defaultValue = "전체") String language){
        return postService.selectPostList(language);
    }

// 작성글 수정
    @PutMapping("/writePost")
    public void updatePost(@RequestBody updateDTO updateDTO){
        postService.updatePost(updateDTO);
    }

//  게시글 검색
    @GetMapping("/searchPost")
    public List<postEntity> searchPost(@RequestParam String title){
        return postService.searchPost(title);
    }
}
