package com.example.cc.repository;

import com.example.cc.entity.chatRoomEntity;
import com.example.cc.entity.messageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<messageEntity, Long> {
    List<messageEntity> findByRoomId(chatRoomEntity chatRoom);
}
