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
    private Long availableLangId;
    private String lang;
    private Long studentId;
}
