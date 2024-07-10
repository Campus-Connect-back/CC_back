package com.example.cc.service;

import com.example.cc.config.PrincipalDetails;
import com.example.cc.dto.accounts.availableLangDTO;
import com.example.cc.dto.accounts.desiredLangDTO;
import com.example.cc.dto.accounts.mypageDTO;
import com.example.cc.dto.accounts.usersDTO;
import com.example.cc.entity.availableLangEntity;
import com.example.cc.entity.desiredLangEntity;
import com.example.cc.entity.usersEntity;
import com.example.cc.repository.accounts.AvailableLangRepository;
import com.example.cc.repository.accounts.DesiredLangRepository;
import com.example.cc.repository.accounts.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class mypageService {
    private final UserRepository userRepository;
    private final AvailableLangRepository availableLangRepository;
    private final DesiredLangRepository desiredLangRepository;

    /*
    * 유저 정보 띄우기
    * usersEntity - 닉네임, 생년월일, 학과, 학번 정보 표시
    * userEntity - 국적, availableLangEntity, desiredLangEntity 정보 표시
     */
    public usersDTO getUser(usersEntity user) {
        return usersDTO.builder()
                .userId(user.getUserId())
                .nickName(user.getNickName())
                .birthday(user.getBirthday())
                .nationality(user.getNationality())
                .imgUrl(user.getImgUrl())
                .build();
    }
    public List<availableLangDTO> getAvailableLang(List<availableLangEntity> availableLangs) {
        return availableLangs.stream()
                .map(availableLang -> availableLangDTO.builder()
                        .availableLangId(availableLang.getAvailableLangId())
                        .lang(availableLang.getLang())
                        .build())
                .collect(Collectors.toList());
    }
    private List<desiredLangDTO> getDesiredLang(List<desiredLangEntity> desiredLangs) {
        return desiredLangs.stream()
                .map(desiredLang -> desiredLangDTO.builder()
                        .desiredLangId(desiredLang.getDesiredLangId())
                        .lang(desiredLang.getLang())
                        .build())
                .collect(Collectors.toList());
    }
    public ResponseEntity<?> getUserInfo(Long user_id){
        // 로그인한 사용자의 user_id로 usersEntity에서 해당 객체 찾기
        usersEntity user = userRepository.findByStudentId_StudentId(user_id)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다"));
        // 찾은 user 객체로 availableLang, desiredLang 찾기
        List<availableLangEntity> availableLangs = availableLangRepository.findByUserId(user);
        List<desiredLangEntity> desiredLangs = desiredLangRepository.findByUserId(user);

        mypageDTO mypage = mypageDTO.builder()
                .usersDTO(getUser(user))
                .availableLangDTO(getAvailableLang(availableLangs))
                .desiredLangDTO((getDesiredLang(desiredLangs)))
                .build();
        return ResponseEntity.ok(mypage);


    }
    // 프로필 사진 업로드
    public String saveFile(MultipartFile file, Long userId)  throws IOException {
        String absolutePath = new File("").getAbsolutePath() + File.separator;
        // 이미지 저장 경로
        String path = "src" + File.separator + "main" + File.separator + "resources" + File.separator + "static"
                + File.separator + "images" + File.separator + "userImg";

        File imgUrl = new File(path);
        // 폴더없으면 생성
        if (!imgUrl.exists()) {
            imgUrl.mkdirs();
        }
        // 파일이 비어있지 않으면
        if (!file.isEmpty()) {
            String contentType = file.getContentType();
            String originalFileExtension;

            // 타입에 따른 확장자 결정
            if (ObjectUtils.isEmpty(contentType)) {
                // 타입 없으면 null
                return null;
            } else {
                if (contentType.contains("image/jpeg")) {
                    originalFileExtension = ".jpg";
                } else if (contentType.contains("image/png")) {
                    originalFileExtension = ".png";
                } else {
                    throw new IOException("지원하지 않는 이미지 파일 형식입니다.");
                }
            }

            // 파일저장 이름
            String originalFileName = file.getOriginalFilename();
            // 확장자를 제외한 파일 이름과 확장자 추출
            int lastIndex = originalFileName.lastIndexOf('.');
            String fileName = originalFileName.substring(0, lastIndex);

            String userImgName = fileName + System.nanoTime() + originalFileExtension;

            // 파일 저장
            File savedFile = new File(absolutePath + path + File.separator + userImgName);
            System.out.println("파일 저장경로:" + savedFile.getAbsolutePath());
            file.transferTo(savedFile);

           return path + File.separator + userImgName;

        }

        return null;
    }
    // 기존에 존재하던 이미지 경로 삭제
    public void deleteUserImg(Long userId) {
        Optional<usersEntity> userEntity = userRepository.findById(userId);
        if (userEntity.isPresent()) {
            usersEntity user = userEntity.get();
            String imgUrl = user.getImgUrl();
            if (imgUrl != null) {
                File file = new File(imgUrl);
                if (file.exists()) {
                    file.delete();
                }
            }
        }
    }
    public ResponseEntity<?> uploadImg(Long userId, MultipartFile file) {
        Optional<usersEntity> user = userRepository.findByStudentId_StudentId(userId);
        if (user.isPresent()) {
            usersEntity userEntity = user.get();
            try{
                String imgUrl = saveFile(file, userId);
                if (imgUrl != null) {
                    // 기존에 설정한 이미지 있으면 지우고 새로 imgUrl 저장
                    if(userEntity.getImgUrl() != null){
                        deleteUserImg(userId);
                    }
                    usersEntity updateUser = usersEntity.builder()
                            .nickName(userEntity.getNickName())
                            .birthday(userEntity.getBirthday())
                            .nationality(userEntity.getNationality())
                            .imgUrl(imgUrl)
                            .build();
                    userRepository.save(updateUser);
                    return ResponseEntity.ok().body("이미지가 저장되었습니다");
                } else {
                    return ResponseEntity.badRequest().body("1.이미지 파일 업로드 중에 오류가 발생하였습니다");
                }
            } catch (Exception e){
                return ResponseEntity.badRequest().body("2,이미지 파일 업로드 중에 오류가 발생하였습니다");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없습니다");
        }
    }
    // 내 정보 수정하기
    // 국적, 구사 가능 언어, 희망 학습 언어 변경하기
    // 탈퇴하기
}
