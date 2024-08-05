package com.example.cc.dto.accounts;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class desiredLangDTO {
    private Long desiredLangId;
    private String lang;
    private Long userId;
    // 생성자 추가
    public desiredLangDTO(String lang) {
        this.lang = lang;
    }
}
