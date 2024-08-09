package com.example.cc.repository;

import com.example.cc.entity.chatRoomEntity;
import com.example.cc.entity.usersEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<chatRoomEntity,Long> {
    chatRoomEntity findByRoomId(Long roomId);
    chatRoomEntity findByRoomIdAndRoomType(Long roomId, Long roomType);
    // 비관적락 걸기
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c " +
            "from chatRoomEntity  c " +
            "where c.roomName = CONCAT(:nickname1, :nickname2) or c.roomName = CONCAT(:nickname2, :nickname1)")
    Optional<chatRoomEntity> findByRoomName(@Param("nickname1") String nickname1,@Param("nickname2") String nickname2);
}
