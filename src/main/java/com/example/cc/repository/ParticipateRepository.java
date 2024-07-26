package com.example.cc.repository;

import com.example.cc.entity.chatRoomEntity;
import com.example.cc.entity.participateEntity;
import com.example.cc.entity.usersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ParticipateRepository extends JpaRepository<participateEntity, Long> {
    List<participateEntity> findByUserId(usersEntity user);
    List<participateEntity> findByRoomId(chatRoomEntity user);
    // participate 테이블에서 로그인한 사용자의 userId와 입장해 있는 roomId가 같은 거 찾아서 삭제
    @Modifying
    @Query("DELETE FROM participateEntity p WHERE p.userId.userId = :userId AND p.roomId.roomId = :roomId")
    void deleteByUserIdAndRoomId(@Param("userId") Long userId, @Param("roomId") Long roomId);
}
