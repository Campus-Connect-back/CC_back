package com.example.cc.repository.accounts;

import com.example.cc.entity.userAuthenticationEntity;
import com.example.cc.entity.usersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<usersEntity, Long> {
    // user 존재하는지
    usersEntity findByUserId(Long userId);
    // 학번 찾기
    Optional<usersEntity> findByStudentId_StudentId(Long studentId);
}
