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
//   신고id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long repostId;

//   신고자당한사람 id
    @ManyToOne
    @JoinColumn(name = "reportedUser", referencedColumnName = "userId")
    private usersEntity reportedUser;

//   신고자 id
    @ManyToOne
    @JoinColumn(name = "reportUser", referencedColumnName = "userId")
    private usersEntity reportUser;

    private String reportReason;

}
