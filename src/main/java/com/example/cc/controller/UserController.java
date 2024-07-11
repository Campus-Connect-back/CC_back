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
    @DeleteMapping("/mypage/{user_id}/delete")
    public ResponseEntity<String> deleteUser(@AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable Long user_id) {
        try{
            mypageService.deleteUser(user_id);
            return ResponseEntity.ok().body("회원 탈퇴 성공");
        }catch (Exception e) {
            return ResponseEntity.badRequest().body("회원 탈퇴 실패");
        }
    }

    // 마이페이지, 유저 정보 띄우기
    @GetMapping("/mypage/{user_id}")
    public mypageDTO getMypage(@AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable Long user_id) {

            mypageDTO response = mypageService.getUserInfo(user_id);
            return response;}


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

    // 유저 정보 수정(비밀번호, 닉네임, 학과)
    @PutMapping("/mypage/{user_id}/edit_userInfo")
    public ResponseEntity<?> editInfo( @PathVariable Long user_id, @RequestBody JoinRequestDTO joinRequestDTO, @RequestParam String currentPassword){
        try{
            mypageService.editInfo(user_id, joinRequestDTO, currentPassword);
            return ResponseEntity.ok("유저 정보 수정 완료");
        } catch(Exception e){
            return ResponseEntity.badRequest().body("유저 정보 수정에 실패하였습니다");
        }
    }
    // 유저 정보 수정(비밀번호, 닉네임, 학과)
    @PutMapping("/mypage/{user_id}/edit_userLangInfo")
    public ResponseEntity<?> editLang( @PathVariable Long user_id, @RequestBody JoinRequestDTO joinRequestDTO, @RequestParam String currentPassword){
        try{
            mypageService.editLang(user_id, joinRequestDTO, currentPassword);
            return ResponseEntity.ok("유저 언어 정보 수정 완료");
        } catch(Exception e){
            return ResponseEntity.badRequest().body("유저 언어 정보 수정에 실패하였습니다");
        }
    }
}
