package com.example.cc.repository.accounts;

import com.example.cc.entity.availableLangEntity;
import com.example.cc.entity.usersEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AvailableLangRepository extends JpaRepository<availableLangEntity, Long> {
    List<availableLangEntity> findByUserId(usersEntity userId);
}
