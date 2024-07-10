package com.example.cc.controller;

import com.example.cc.config.PrincipalDetails;
import com.example.cc.dto.accounts.*;
import com.example.cc.entity.*;
import com.example.cc.service.UserService;
import com.example.cc.service.mypageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static com.mysql.cj.conf.PropertyKey.logger;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final mypageService mypageService;
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

    // 마이페이지, 유저 정보 띄우기
    @GetMapping("/mypage/{user_id}")
    public ResponseEntity<?> getMypage(@AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable Long user_id) {
        //user_id = Long.valueOf(principalDetails.getUsername());
        return mypageService.getUserInfo(user_id);
    }


    // 프로필 사진 업로드
    @PostMapping("/mypage/{user_id}/edit_profileImg")
    public ResponseEntity<?> uploadImg(@RequestPart(value="key", required=false) usersDTO userDto,
                                       @RequestPart(value="file", required=false) MultipartFile file){
        try{
            mypageService.uploadImg(userDto.getUserId(), file);
            return ResponseEntity.ok("dto = " + userDto + " file = " + file);
        } catch(Exception e){
            return ResponseEntity.badRequest().body("이미지 파일 업로드에 실패하였습니다");
        }
    }
}
