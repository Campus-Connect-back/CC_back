package com.example.cc.dto.accounts;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JoinRequestDTO {
    private userAuthenticationDTO userAuthenticationDTO;
    private usersDTO usersDTO;
    private availableLangDTO availableLangDTO;
    private desiredLangDTO desiredLangDTO;
}
