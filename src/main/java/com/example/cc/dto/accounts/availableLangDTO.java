package com.example.cc.dto.accounts;

import com.example.cc.entity.userAuthenticationEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class availableLangDTO {
    private Long availableLangId;
    private String lang;
    private Long userId;
}
