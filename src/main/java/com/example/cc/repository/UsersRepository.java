package com.example.cc.repository;

import com.example.cc.entity.usersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepository extends JpaRepository<usersEntity, Long> {
}
