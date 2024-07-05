package com.example.cc.repository.accounts;

import com.example.cc.entity.studentDatabaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentDataRepository extends JpaRepository<studentDatabaseEntity, Long> {
    boolean existsByStudentIdAndStudentNameAndMajor(Long studentId, String studentName, String major);
}
