package com.example.cc.repository;

import com.example.cc.entity.chatRoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRoomRepository extends JpaRepository<chatRoomEntity,Long> {
    chatRoomEntity findByRoomId(Long roomId);
    chatRoomEntity findByRoomIdAndRoomType(Long roomId, Long roomType);
}
