package com.example.cc.controller;

import com.example.cc.config.PrincipalDetails;
import com.example.cc.entity.usersEntity;
import com.example.cc.service.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/match")
public class MatchController {
    private final MatchService matchService;

    @PostMapping("/enqueue")
    public void startMatch(@AuthenticationPrincipal PrincipalDetails principalDetails){
        matchService.startMatch(principalDetails);
    }
}
