package com.example.cc.entity;

import jakarta.persistence.*;
import lombok.*;

@ToString
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "report")
public class reportEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long repostId;

    @ManyToOne
    @JoinColumn(name = "reportedUser", referencedColumnName = "studentId")
    private usersEntity reportedUser;

    @ManyToOne
    @JoinColumn(name = "reportUser", referencedColumnName = "studentId")
    private usersEntity reportUser;

    private String reportReason;

}
