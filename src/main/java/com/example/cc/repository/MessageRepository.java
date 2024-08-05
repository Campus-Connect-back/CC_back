package com.example.cc.repository;

import com.example.cc.entity.chatRoomEntity;
import com.example.cc.entity.messageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MessageRepository extends JpaRepository<messageEntity, Long> {
    List<messageEntity> findByRoomId(chatRoomEntity chatRoom);
    Optional<messageEntity> findFirstByRoomIdOrderBySendTime(chatRoomEntity roomId);
}
