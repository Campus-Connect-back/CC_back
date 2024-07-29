package com.example.cc.service;

import com.example.cc.config.PrincipalDetails;
import com.example.cc.dto.accounts.*;
import com.example.cc.entity.availableLangEntity;
import com.example.cc.entity.desiredLangEntity;
import com.example.cc.entity.userAuthenticationEntity;
import com.example.cc.entity.usersEntity;
import com.example.cc.repository.UsersRepository;
import com.example.cc.repository.accounts.AvailableLangRepository;
import com.example.cc.repository.accounts.DesiredLangRepository;
import com.example.cc.repository.accounts.UserAuthRepository;
import com.example.cc.repository.accounts.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AvailableLangRepository availableLangRepository;
    private final DesiredLangRepository desiredLangRepository;
    private final UserAuthRepository userAuthRepository;
    private final UsersRepository usersRepository;

    // 유저 정보 띄우기(닉네임, 생년월일, 학과, 학번, 국적, 구사 가능 언어, 희망 학습 언어)
    public mypageDTO getUserInfo(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        // 로그인한 사용자의 studentId로 usersEntity에서 해당 객체 찾기
        usersEntity user = userRepository.findByStudentId_StudentId(principalDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다"));

        // 찾은 user 객체로 availableLang, desiredLang 찾기
        List<availableLangEntity> availableLangs = availableLangRepository.findByUserId(user);
        List<desiredLangEntity> desiredLangs = desiredLangRepository.findByUserId(user);

        mypageDTO mypage = mypageDTO.builder()
                .usersDTO(usersDTO.builder()
                        .userId(user.getUserId())
                        .nickName(user.getNickName())
                        .birthday(user.getBirthday())
                        .nationality(user.getNationality())
                        .imgUrl(user.getImgUrl())
                        .build())
                .availableLangDTO(availableLangs.stream()
                        .map(availableLang -> availableLangDTO.builder()
                                .availableLangId(availableLang.getAvailableLangId())
                                .lang(availableLang.getLang())
                                .build())
                        .collect(Collectors.toList()))
                .desiredLangDTO(desiredLangs.stream()
                        .map(desiredLang -> desiredLangDTO.builder()
                                .desiredLangId(desiredLang.getDesiredLangId())
                                .lang(desiredLang.getLang())
                                .build())
                        .collect(Collectors.toList()))
                .build();
        return mypage;
    }

    @Transactional
    public String saveFile(MultipartFile file, Long userId) throws IOException {
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

    public ResponseEntity<?> uploadImg(@AuthenticationPrincipal PrincipalDetails principalDetails,usersDTO userDto, MultipartFile file) {
        // 로그인한 사용자의 studentId로 usersEntity에서 해당 객체 찾기
        Optional<usersEntity> user = userRepository.findByStudentId_StudentId(principalDetails.getUsername());
        if (user.isPresent()) {
            usersEntity userEntity = user.get();
            Long userId = userEntity.getUserId();
            try {
                String imgUrl = saveFile(file, userId);
                if (imgUrl != null) {
                    // 기존에 설정한 이미지 있으면 지우고 새로 imgUrl 저장
                    if (userEntity.getImgUrl() != null) {
                        deleteUserImg(userId);
                    }
                    userEntity.setImgUrl(imgUrl);
                    userRepository.save(userEntity);
                    return ResponseEntity.ok().body("이미지가 저장되었습니다");
                } else {
                    return ResponseEntity.badRequest().body("1.이미지 파일 업로드 중에 오류가 발생하였습니다");
                }
            } catch (Exception e) {
                return ResponseEntity.badRequest().body("2,이미지 파일 업로드 중에 오류가 발생하였습니다");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없습니다");
        }
    }

    // 내 정보 수정하기(비밀번호, 닉네임, 학과, 생년월일 수정)
    @Transactional
    public void editInfo(@AuthenticationPrincipal PrincipalDetails principalDetails, JoinRequestDTO joinRequestDTO, String currentPassword) {
        // 로그인한 사용자의 studentId로 usersEntity에서 해당 객체 찾기
        usersEntity user = userRepository.findByStudentId_StudentId(principalDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다"));

        userAuthenticationEntity userAuth = user.getStudentId();

        // 닉네임 수정, null이면 기존 정보 유지
        if (joinRequestDTO.getUsersDTO().getNickName() != null  && !joinRequestDTO.getUsersDTO().getNickName().isEmpty()) {
            user.setNickName(joinRequestDTO.getUsersDTO().getNickName());
        }

        // 학과 수정
        if (joinRequestDTO.getUserAuthenticationDTO().getMajor() != null  && !joinRequestDTO.getUserAuthenticationDTO().getMajor().isEmpty()) {
            userAuth.setMajor(joinRequestDTO.getUserAuthenticationDTO().getMajor());
        }
        // 생년월일 수정
        if (joinRequestDTO.getUsersDTO().getBirthday() != null ) {
            user.setBirthday(joinRequestDTO.getUsersDTO().getBirthday());
        }

        // 입력한 비밀번호와 현재 비밀번호가 같은지 확인하고 덮어쓰기
        if (joinRequestDTO.getUsersDTO().getPassword() != null && !joinRequestDTO.getUsersDTO().getPassword().isEmpty()) {
            if (passwordEncoder.matches(currentPassword, user.getPassword())) {
                user.setPassword(passwordEncoder.encode(joinRequestDTO.getUsersDTO().getPassword()));
            }
        }

        // 덮어쓴 내용 저장
        userRepository.save(user);
        userAuthRepository.save(userAuth);
    }

    // 국적, 구사 가능 언어, 희망 학습 언어 수정
    @Transactional
    public void editLang(@AuthenticationPrincipal PrincipalDetails principalDetails, JoinRequestDTO joinRequestDTO) {
        // 로그인한 사용자의 studentId로 usersEntity에서 해당 객체 찾기
        usersEntity user = userRepository.findByStudentId_StudentId(principalDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다"));
        List<availableLangEntity> availableLangs = availableLangRepository.findByUserId(user);
        List<desiredLangEntity> desiredLangs = desiredLangRepository.findByUserId(user);

        // 국적 수정, null이면 기존 정보 유지
        if (joinRequestDTO.getUsersDTO().getNationality() != null  && !joinRequestDTO.getUsersDTO().getNationality().isEmpty()) {
            user.setNationality(joinRequestDTO.getUsersDTO().getNationality());
        }
        // 덮어쓴 내용 저장
        userRepository.save(user);

        // 구사 가능 언어 수정
        if (joinRequestDTO.getUsersDTO().getAvailableLang() != null) {
            if (!joinRequestDTO.getUsersDTO().getAvailableLang().isEmpty()) {
                // 새로운 구사 가능 언어 목록 설정
                List<availableLangDTO> newAvailableLangList = joinRequestDTO.getUsersDTO().getAvailableLang();

                // 기존 구사 가능 언어 삭제
                for (int i = 0; i < availableLangs.size(); i++) {
                    availableLangRepository.delete(availableLangs.get(i));
                }
                // 새로운 구사 가능 언어 목록 추가
                for (availableLangDTO langDTO : newAvailableLangList) {
                    availableLangRepository.save(availableLangEntity.builder()
                            .userId(user)
                            .lang(langDTO.getLang())
                            .build());
                }
            }
        }

        // 희망 학습 언어 수정
        if (joinRequestDTO.getUsersDTO().getDesiredLang() != null) {
            if (!joinRequestDTO.getUsersDTO().getDesiredLang().isEmpty()) {
                // 새로운 희망 학습 언어 목록 설정
                List<desiredLangDTO> newDesiredLangList = joinRequestDTO.getUsersDTO().getDesiredLang();

                // 기존 희망 학습 언어 삭제
                for (int i = 0; i < desiredLangs.size(); i++) {
                    desiredLangRepository.delete(desiredLangs.get(i));
                }
                // 새로운 희망 학습 언어 목록 추가
                for (desiredLangDTO langDTO : newDesiredLangList) {
                    desiredLangRepository.save(desiredLangEntity.builder()
                            .userId(user)
                            .lang(langDTO.getLang())
                            .build());
                }
            }
        }
    }

    //회원 탈퇴
    public void deleteUser(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        // 로그인한 사용자의 studentId로 usersEntity에서 해당 객체 찾기
        usersEntity user = userRepository.findByStudentId_StudentId(principalDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다"));

        userAuthenticationEntity userAuth = user.getStudentId();
        List<availableLangEntity> availableLangs = availableLangRepository.findByUserId(user);
        List<desiredLangEntity> desiredLangs = desiredLangRepository.findByUserId(user);

        for (int i = 0; i < availableLangs.size(); i++) {
            availableLangRepository.delete(availableLangs.get(i));
        }
        for (int i = 0; i < desiredLangs.size(); i++) {
            desiredLangRepository.delete(desiredLangs.get(i));
        }
        userRepository.delete(user);
        userAuthRepository.delete(userAuth);
    }
}
