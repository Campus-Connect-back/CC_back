package com.example.cc.dto.accounts;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class userAuthenticationDTO {
    private String studentId;
    private String studentName;
    private String major;
}
