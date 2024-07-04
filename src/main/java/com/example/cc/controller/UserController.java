package com.example.cc.controller;

import com.example.cc.dto.accounts.*;
import com.example.cc.entity.availableLangEntity;
import com.example.cc.entity.desiredLangEntity;
import com.example.cc.entity.userAuthenticationEntity;
import com.example.cc.entity.usersEntity;
import com.example.cc.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    // 재학생 인증
    @PostMapping("/auth")
    public String  authStudent(@RequestBody userAuthenticationDTO userAuthenticationDTO) {
        return userService.authStudent(userAuthenticationDTO);

    }
    // 회원가입
    @PostMapping("/join")
    public ResponseEntity<?> createUserAndSaveLang(@RequestBody JoinRequestDTO joinRequestDTO) {
        // 사용자 생성
        userAuthenticationEntity userAuth = userService.saveAuth(joinRequestDTO.getUserAuthenticationDTO());

        // 사용자 정보 저장
        usersEntity user = userService.createUser(userAuth, joinRequestDTO.getUsersDTO());

        // availableLangEntity 저장
        availableLangEntity savedAvailableLang = userService.availableLang(user, joinRequestDTO.getAvailableLangDTO());

        // desiredLangEntity 저장
        desiredLangEntity savedDesiredLang = userService.desiredLang(user, joinRequestDTO.getDesiredLangDTO());

        return ResponseEntity.ok("사용자 생성 및 언어 저장 완료");
    }
}
