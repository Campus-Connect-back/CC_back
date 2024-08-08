package com.example.cc.controller;

import com.example.cc.config.PrincipalDetails;
import com.example.cc.dto.accounts.*;
import com.example.cc.entity.*;
import com.example.cc.service.UserService;
import com.example.cc.service.mypageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.net.MalformedURLException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final mypageService mypageService;


    // 파일 저장 경로, application.properties에서 설정
    @Value("${file.upload-dir}")
    private String uploadDir;
    // 프로필 사진 업로드
    @PostMapping(value= "/mypage/edit_profileImg", produces = "application/json", consumes = "multipart/form-data")
    public void uploadImg(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                       @RequestParam("file") MultipartFile file) {
        mypageService.uploadImg(principalDetails, file);

    }
    @GetMapping("/images/{imgUrl}")
    public UrlResource getImage(@PathVariable String imgUrl) throws MalformedURLException {
        File file = new File(uploadDir + "/"+ imgUrl);
        return new UrlResource(file.toURI());
    }



    // 재학생 인증
    @PostMapping("/auth")
    public String  authStudent(@RequestBody studentDatabaseDTO studentDatabaseDTO) {
        return userService.auth(studentDatabaseDTO);

    }

    // 회원가입
    @PostMapping("/join")
    public ResponseEntity<String> createUser(@RequestBody JoinRequestDTO joinRequestDTO) {
        try{
            String response = userService.join(joinRequestDTO);
            return ResponseEntity.ok(response);
        } catch (Exception e){
            return ResponseEntity.badRequest().body("실패하였습니다");
        }
    }
    // 회원 탈퇴
    @DeleteMapping("/mypage/delete")
    public ResponseEntity<String> deleteUser(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        try{
            mypageService.deleteUser(principalDetails);
            return ResponseEntity.ok().body("회원 탈퇴 성공");
        }catch (Exception e) {
            return ResponseEntity.badRequest().body("회원 탈퇴 실패");
        }
    }

    // 마이페이지, 유저 정보 띄우기
    @GetMapping("/mypage")
    public JoinRequestDTO getMypage(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        JoinRequestDTO response = mypageService.getUserInfo(principalDetails);
            return response;
    }

    // 유저 정보 수정(비밀번호, 닉네임, 학과)
    @PutMapping("/mypage/edit_userInfo")
    public JoinRequestDTO editInfo(@AuthenticationPrincipal PrincipalDetails principalDetails, @RequestBody JoinRequestDTO joinRequestDTO, @RequestParam String password){

            return mypageService.editInfo(principalDetails, joinRequestDTO, password);

    }
    // 유저 정보 수정(비밀번호, 닉네임, 학과)
    @PutMapping("/mypage/edit_userLangInfo")
    public JoinRequestDTO editLang(@AuthenticationPrincipal PrincipalDetails principalDetails, @RequestBody JoinRequestDTO joinRequestDTO) {

        return mypageService.editLang(principalDetails, joinRequestDTO);
    }
}
