package com.example.cc.repository.accounts;

import com.example.cc.entity.userAuthenticationEntity;
import com.example.cc.entity.usersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<usersEntity, Long> {
    // userId로 사용자 찾기
    usersEntity findByUserId(Long userId);
    // 학번으로 사용자 찾기
    Optional<usersEntity> findByStudentId_StudentId(String studentId);

}