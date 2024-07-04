package com.example.cc.entity;

import jakarta.persistence.*;
import lombok.*;

@ToString
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "available_lang")
public class availableLangEntity {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long availableLangId;

    // 언어
    @Column(nullable = false)
    private String lang;

    // 유저id
    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private usersEntity userId;
}
