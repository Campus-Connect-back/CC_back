package com.example.cc.repository;

import com.example.cc.entity.postEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<postEntity, Long> {
    List<postEntity> findByLanguage(String Language);
    postEntity findByPostId(Long postId);
}
