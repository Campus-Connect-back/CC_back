package com.example.cc.service;

import com.example.cc.dto.accounts.*;
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

import java.util.List;
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


    public String join(JoinRequestDTO joinRequestDTO) {
        // userAuthenticationDTO에서 학생 정보 가져오기
        userAuthenticationDTO userAuthDTO = joinRequestDTO.getUserAuthenticationDTO();
        // student database에 일치하는 데이터 있는지 확인
        boolean isStudentExist = studentDataRepository.existsByStudentIdAndStudentNameAndMajor(
                userAuthDTO.getStudentId(),
                userAuthDTO.getStudentName(),
                userAuthDTO.getMajor()
        );

        if (!isStudentExist) { // db에 없음, 재학생 인증 실패
            return "재학생 인증 실패: 학번, 이름, 전공이 일치하지 않습니다."; // 회원가입 실패
        } else { // 재학생인 경우
            boolean alreadyJoin = userAuthRepository.existsByStudentIdAndStudentNameAndMajor(userAuthDTO.getStudentId(), userAuthDTO.getStudentName(), userAuthDTO.getMajor());
            if (alreadyJoin) { // 이미 회원가입한 사용자인지 확인
                return "이미 회원가입한 사용자입니다.";// 회원가입 실패
            } else {
                // 회원 인증 객체 저장
                userAuthenticationEntity userAuth = userAuthenticationEntity.builder()
                        .studentId(userAuthDTO.getStudentId())
                        .studentName(userAuthDTO.getStudentName())
                        .major(userAuthDTO.getMajor())
                        .build();
                userAuth = userAuthRepository.save(userAuth);

                //비밀번호 암호화해서 저장
                usersDTO usersDTO = joinRequestDTO.getUsersDTO();
                usersEntity user = usersEntity.builder()
                        .nickName(usersDTO.getNickName())
                        .password(passwordEncoder.encode(usersDTO.getPassword()))
                        .birthday(usersDTO.getBirthday())
                        .nationality(usersDTO.getNationality())
                        .imgUrl(usersDTO.getImgUrl())
                        .studentId(userAuth)
                        .build();
                user = userRepository.save(user);
                // 구사 가능 언어 저장
                for (int i = 0; i < usersDTO.getAvailableLang().size(); i++) {
                    availableLangRepository.save(availableLangEntity.builder()
                            .userId(user)
                            .lang(usersDTO.getAvailableLang().get(i).getLang())
                            .build()
                    );
                }
                // 희망 학습 언어 저장
                for (int i = 0; i < usersDTO.getDesiredLang().size(); i++) {
                    desiredLangRepository.save(desiredLangEntity.builder()
                            .userId(user)
                            .lang(usersDTO.getDesiredLang().get(i).getLang())
                            .build()
                    );
                }
            }
            return "회원가입이 완료되었습니다.";
        }
    }
}