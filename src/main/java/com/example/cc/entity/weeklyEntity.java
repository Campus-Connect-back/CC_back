package com.example.cc.entity;

import jakarta.persistence.*;
import lombok.*;

@ToString
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "weekly")
public class weeklyEntity {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long weeklyId;

    @ManyToOne
    @JoinColumn(name = "postId", nullable = false)
    private postEntity postId;

    private String week;
}
