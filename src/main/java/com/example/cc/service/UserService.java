package com.example.cc.service;

import com.example.cc.dto.accounts.userAuthenticationDTO;
import com.example.cc.dto.accounts.usersDTO;
import com.example.cc.dto.accounts.availableLangDTO;
import com.example.cc.dto.accounts.desiredLangDTO;
import com.example.cc.entity.availableLangEntity;
import com.example.cc.entity.desiredLangEntity;
import com.example.cc.entity.userAuthenticationEntity;
import com.example.cc.entity.usersEntity;
import com.example.cc.repository.accounts.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserAuthRepository userAuthRepository;
    private final StudentDataRepository studentDataRepository;
    private final AvailableLangRepository availableLangRepository;
    private final DesiredLangRepository desiredLangRepository;

    // 재학생 인증
    public String authStudent(userAuthenticationDTO userAuthenticationDTO) {
        // student database에 일치하는 데이터 있는지 확인
        boolean isStudentExist = studentDataRepository.existsByStudentIdAndStudentNameAndMajor(
                userAuthenticationDTO.getStudentId(),
                userAuthenticationDTO.getStudentName(),
                userAuthenticationDTO.getMajor()
        );

        if (!isStudentExist) { // db에 없음, 재학생 인증 실패
            return "재학생 인증 실패: 학번, 이름, 전공이 일치하지 않습니다."; // 회원가입 실패
        } else { // 재학생인 경우
            boolean alreadyJoin = userAuthRepository.existsByStudentIdAndStudentNameAndMajor(
                    userAuthenticationDTO.getStudentId(),
                    userAuthenticationDTO.getStudentName(),
                    userAuthenticationDTO.getMajor()
            );
            if (alreadyJoin) { // 이미 회원가입한 사용자인지 확인
                return "이미 회원가입한 사용자입니다.";// 회원가입 실패
            }
        }
        return "재학생 인증 성공"; // 회원가입 페이지로 넘어감
    }

    public userAuthenticationEntity saveAuth(userAuthenticationDTO userAuthenticationDTO) {
        userAuthenticationEntity userAuth = userAuthenticationEntity.builder()
                .studentId(userAuthenticationDTO.getStudentId())
                .studentName(userAuthenticationDTO.getStudentName())
                .major(userAuthenticationDTO.getMajor())
                .build();
        userAuth = userAuthRepository.save(userAuth);
        return userAuth;
    }

    //비밀번호 암호화해서 저장
    public usersEntity createUser(userAuthenticationEntity userAuth, usersDTO usersDTO) {
        usersEntity user = usersEntity.builder()
                .nickName(usersDTO.getNickName())
                .password(passwordEncoder.encode(usersDTO.getPassword()))
                .birthday(usersDTO.getBirthday())
                .nationality(usersDTO.getNationality())
                .imgUrl(usersDTO.getImgUrl())
                .studentId(userAuth)
                .build();
        user = userRepository.save(user);
        return user;
    }
    public availableLangEntity availableLang(usersEntity user, availableLangDTO availableLangDTO) {
        availableLangEntity availableLang = availableLangEntity.builder()
                .lang(availableLangDTO.getLang())
                .userId(user)
                .build();
        return availableLangRepository.save(availableLang);
    }
    public desiredLangEntity desiredLang(usersEntity user, desiredLangDTO desiredLangDTO) {
        desiredLangEntity desiredLang = desiredLangEntity.builder()
                .lang(desiredLangDTO.getLang())
                .userId(user)
                .build();
        return desiredLangRepository.save(desiredLang);
    }

}
