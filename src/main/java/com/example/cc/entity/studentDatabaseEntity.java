package com.example.cc.entity;

import jakarta.persistence.*;
import lombok.*;

@ToString
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "student_database")
public class studentDatabaseEntity {
    // 학번(PK)
    @Id
    @Column
    private Long studentId;

    // 이름
    @Column(nullable = false)
    private String studentName;

    //전공
    @Column(nullable = false)
    private String major;
}
