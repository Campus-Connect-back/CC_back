package com.example.cc.entity;

import jakarta.persistence.*;
import lombok.*;

@ToString
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "desired_lang")
public class desiredLangEntity {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long desiredleLangId;

    // 언어
    @Column(nullable = false)
    private String lang;

    // 학번(PK, FK)
    @Id
    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private usersEntity userId;
}
