package com.example.cc.controller.post;

import com.example.cc.dto.post.postDTO;
import com.example.cc.entity.postEntity;
import com.example.cc.service.postService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class postController {

    private final postService postService;

    @PostMapping("/writePost")
    public void insertPost(@RequestBody postDTO postDTO){

        postService.insertPost(postDTO);
    }
}
