package com.example.cc.repository.accounts;

import com.example.cc.entity.userAuthenticationEntity;
import com.example.cc.entity.usersEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAuthRepository extends JpaRepository<userAuthenticationEntity, Long> {
    // 가입해서 학생 정보 있는지 확인
    boolean existsByStudentIdAndStudentNameAndMajor(String studentId, String studentName, String major);

    Optional<userAuthenticationEntity> findByStudentId(String studentId);
}
