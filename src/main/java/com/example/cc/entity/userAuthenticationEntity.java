package com.example.cc.entity;

import jakarta.persistence.*;
import lombok.*;

@ToString
@Getter
@Builder
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_authentication")
public class userAuthenticationEntity {
    // 학번(PK)
    @Id
    @Column
    private String studentId;

    // 이름
    @Column(nullable = false)
    private String studentName;

    //전공
    @Column(nullable = false)
    private String major;
}
