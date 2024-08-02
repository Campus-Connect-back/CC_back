package com.example.cc.testService;

import com.example.cc.entity.availableLangEntity;
import com.example.cc.entity.desiredLangEntity;
import com.example.cc.entity.usersEntity;
import com.example.cc.repository.accounts.AvailableLangRepository;
import com.example.cc.repository.accounts.DesiredLangRepository;
import com.example.cc.repository.accounts.UserRepository;
import com.example.cc.service.MatchService;
import com.example.cc.testConfig.EmbeddedRedisConfig;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
@ActiveProfiles("local")
@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
public class MatchServiceTest {
    @Autowired
    private MatchService matchService;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {

    }

    @Test
    public void testStartMatch() {
        // 첫 번째 사용자 생성
        usersEntity user1 = createUser("가", "password1", new Date(), "Korean", "image1.jpg",
                List.of("EN"), List.of("ES"));
        userRepository.save(user1);

        // 두 번째 사용자 생성
        usersEntity user2 = createUser("나", "password2", new Date(), "American", "image2.jpg",
                List.of("JP"), List.of("CH"));
        userRepository.save(user2);

        // 세 번째 사용자 생성
        usersEntity user3 = createUser("다", "password3", new Date(), "British", "image3.jpg",
                List.of("FR","EN"), List.of("DE"));
        userRepository.save(user3);

        usersEntity user4 = createUser("라", "password3", new Date(), "British", "image3.jpg",
                List.of("FR", "CH"), List.of("EN","JP"));
        userRepository.save(user4);

        // 첫 번째 사용자 매칭 시작
        matchService.startMatch(user1);
        matchService.startMatch(user2);
        matchService.startMatch(user3);
        matchService.startMatch(user4);
    }

    private usersEntity createUser(String nickName, String password, Date birthday, String nationality,
                                   String imgUrl, List<String> availableLangs, List<String> desiredLangs) {
        usersEntity user = new usersEntity();
        user.setNickName(nickName);
        user.setPassword(password);
        user.setBirthday(birthday);
        user.setNationality(nationality);
        user.setImgUrl(imgUrl);

        // 구사 언어 및 희망 학습 언어 설정
        List<availableLangEntity> availableLangEntities = new ArrayList<>();
        for (String lang : availableLangs) {
            availableLangEntity availableLang = new availableLangEntity();
            availableLang.setLang(lang);
            availableLang.setUserId(user); // user와 연결
            availableLangEntities.add(availableLang);
        }
        user.setAvailableLang(availableLangEntities);

        List<desiredLangEntity> desiredLangEntities = new ArrayList<>();
        for (String lang : desiredLangs) {
            desiredLangEntity desiredLang = new desiredLangEntity();
            desiredLang.setLang(lang);
            desiredLang.setUserId(user); // user와 연결
            desiredLangEntities.add(desiredLang);
        }
        user.setDesiredLang(desiredLangEntities);

        return user;
    }
}