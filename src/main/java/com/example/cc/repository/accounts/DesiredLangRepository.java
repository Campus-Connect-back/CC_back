package com.example.cc.repository.accounts;

import com.example.cc.entity.availableLangEntity;
import com.example.cc.entity.desiredLangEntity;
import com.example.cc.entity.usersEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DesiredLangRepository extends JpaRepository<desiredLangEntity, Long> {
    List<desiredLangEntity> findByUserId(usersEntity userId);
}
