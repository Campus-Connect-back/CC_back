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
import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.coyote.Response;
import org.hibernate.mapping.Join;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.UrlResource;
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
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.UUID;

import static org.aspectj.weaver.tools.cache.SimpleCacheFactory.path;

@Service
@RequiredArgsConstructor
public class mypageService {
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AvailableLangRepository availableLangRepository;
    private final DesiredLangRepository desiredLangRepository;
    private final UserAuthRepository userAuthRepository;
    private final UsersRepository usersRepository;
    // 이미지 저장 경로
    @Value("${file.upload-dir}")
    private String uploadDir;
    // 이미지 파일은 최대 5MB
    private static final long MAX_IMAGE_SIZE = 5242880;

    // 유저 정보 띄우기(닉네임, 생년월일, 학과, 학번, 국적, 구사 가능 언어, 희망 학습 언어)
    public JoinRequestDTO getUserInfo(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        // 로그인한 사용자의 studentId로 usersEntity에서 해당 객체 찾기
        usersEntity user = userRepository.findByStudentId_StudentId(principalDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다"));

        userAuthenticationEntity userAuth = userAuthRepository.findByStudentId(principalDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다"));
        // 찾은 user 객체로 availableLang, desiredLang 찾기
        List<availableLangEntity> availableLangs = availableLangRepository.findByUserId(user);
        List<desiredLangEntity> desiredLangs = desiredLangRepository.findByUserId(user);

        JoinRequestDTO mypage = JoinRequestDTO.builder()
                .userAuthenticationDTO(userAuthenticationDTO.builder()
                        .studentId(userAuth.getStudentId())
                        .major(userAuth.getMajor())
                        .studentName(userAuth.getStudentName())
                        .build())
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
    // 이미지 저장
    private String saveFile(MultipartFile file) throws IOException {
        String extension;
        if (!file.isEmpty()) {
            String contentType = file.getContentType();

            // 타입에 따른 확장자 결정
            if (ObjectUtils.isEmpty(contentType)) {
                // 타입 없으면 null
                return null;
            } else {
                if (contentType.contains("image/jpeg")) {
                    extension = ".jpg";
                } else if (contentType.contains("image/png")) {
                    extension = ".png";
                } else {
                    throw new IOException("지원하지 않는 이미지 파일 형식입니다.");
                }
            }
            // 파일저장 이름
            String fileName = UUID.randomUUID().toString() + extension;
            byte[] fileContent = file.getBytes();
            String filePath = uploadDir + "/" + fileName;
            Path path = Paths.get(filePath);
            Files.write(path, fileContent);
            return fileName;
        }
        return null;
    }
    // 이미지 삭제하기
    private void deleteExistingImage(String imgUrl) {
        try {
            // 이미지 파일 경로 생성
            Path path = Paths.get(uploadDir + "/" + imgUrl);
            // 파일 삭제
            Files.deleteIfExists(path);
        } catch (IOException e) {
            // 파일 삭제 중 예외 처리
            System.err.println("기존 이미지 삭제 중 오류 발생: " + e.getMessage());
        }
    }
    // 이미지 업로드
    public void uploadImg(@AuthenticationPrincipal PrincipalDetails principalDetails, MultipartFile file) {
        // 로그인한 사용자의 studentId로 usersEntity에서 해당 객체 찾기
        usersEntity user = userRepository.findByStudentId_StudentId(principalDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다"));

        if(file.getSize() > MAX_IMAGE_SIZE) {
            throw new RuntimeException("이미지 크기가 너무 큽니다");
        }
        // 이미지 저장하기
        try {
            String imgUrl = saveFile(file);
            // 기존 이미지 URL 확인
            String existingImgUrl = user.getImgUrl();
            if (existingImgUrl != null) {
                deleteExistingImage(existingImgUrl);
            }
            if (imgUrl != null) {
                user.setImgUrl(imgUrl);
                userRepository.save(user);
            }
        } catch (Exception e) {
            ResponseEntity.badRequest().body("이미지 파일 업로드 중에 오류가 발생하였습니다");
        }

    }

    // 내 정보 수정하기(비밀번호, 닉네임, 학과, 생년월일 수정)
    @Transactional
    public JoinRequestDTO editInfo(@AuthenticationPrincipal PrincipalDetails principalDetails, JoinRequestDTO joinRequestDTO, String currentPassword) {
        // 로그인한 사용자의 studentId로 usersEntity에서 해당 객체 찾기
        usersEntity user = userRepository.findByStudentId_StudentId(principalDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다"));

        userAuthenticationEntity userAuth = user.getStudentId();
        // 현재 비밀번호와 입력한 비밀번호가 일치해야만 정보 수정할 수 있음
        if (passwordEncoder.matches(currentPassword, user.getPassword())) {

            // 닉네임 수정, null이면 기존 정보 유지
            if (joinRequestDTO.getUsersDTO().getNickName() != null && !joinRequestDTO.getUsersDTO().getNickName().isEmpty()) {
                user.setNickName(joinRequestDTO.getUsersDTO().getNickName());
            }

            // 학과 수정
            if (joinRequestDTO.getUserAuthenticationDTO().getMajor() != null && !joinRequestDTO.getUserAuthenticationDTO().getMajor().isEmpty()) {
                userAuth.setMajor(joinRequestDTO.getUserAuthenticationDTO().getMajor());
            }
            // 생년월일 수정
            if (joinRequestDTO.getUsersDTO().getBirthday() != null) {
                user.setBirthday(joinRequestDTO.getUsersDTO().getBirthday());
            }

            // 입력한 비밀번호와 현재 비밀번호가 같은지 확인하고 덮어쓰기
            if (joinRequestDTO.getUsersDTO().getPassword() != null && !joinRequestDTO.getUsersDTO().getPassword().isEmpty()) {

                user.setPassword(passwordEncoder.encode(joinRequestDTO.getUsersDTO().getPassword()));

            }
        }
        // 덮어쓴 내용 저장
        userRepository.save(user);
        userAuthRepository.save(userAuth);
        // joinRequestDTO 업데이트
        joinRequestDTO.getUsersDTO().setNickName(user.getNickName());
        joinRequestDTO.getUserAuthenticationDTO().setMajor(userAuth.getMajor());
        joinRequestDTO.getUsersDTO().setBirthday(user.getBirthday());
        return joinRequestDTO;
    }

    // 국적, 구사 가능 언어, 희망 학습 언어 수정
    @Transactional
    public JoinRequestDTO editLang(@AuthenticationPrincipal PrincipalDetails principalDetails, JoinRequestDTO joinRequestDTO) {
        // 로그인한 사용자의 studentId로 usersEntity에서 해당 객체 찾기
        usersEntity user = userRepository.findByStudentId_StudentId(principalDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다"));
        List<availableLangEntity> availableLangs = availableLangRepository.findByUserId(user);
        List<desiredLangEntity> desiredLangs = desiredLangRepository.findByUserId(user);

        // 국적 수정, null이면 기존 정보 유지
        if (joinRequestDTO.getUsersDTO().getNationality() != null && !joinRequestDTO.getUsersDTO().getNationality().isEmpty()) {
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
        // joinRequestDTO 업데이트
        joinRequestDTO.getUsersDTO().setNationality(user.getNationality());
        joinRequestDTO.getUsersDTO().setAvailableLang(availableLangRepository.findByUserId(user).stream()
                .map(lang -> new availableLangDTO(lang.getLang()))
                .collect(Collectors.toList()));
        joinRequestDTO.getUsersDTO().setDesiredLang(desiredLangRepository.findByUserId(user).stream()
                .map(lang -> new desiredLangDTO(lang.getLang()))
                .collect(Collectors.toList()));

        return joinRequestDTO;
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