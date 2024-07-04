package com.example.cc.repository;

import com.example.cc.entity.postEntity;
import com.example.cc.entity.weeklyEntity;
import org.hibernate.type.descriptor.converter.spi.JpaAttributeConverter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WeeklyRepository extends JpaRepository<weeklyEntity,Long> {
    List<weeklyEntity> findByPostId(postEntity postId);
}
