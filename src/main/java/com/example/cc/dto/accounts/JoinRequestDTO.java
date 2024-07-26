package com.example.cc.dto.accounts;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JoinRequestDTO {
    private userAuthenticationDTO userAuthenticationDTO;
    private usersDTO usersDTO;
    private List<availableLangDTO> availableLangDTO;
    private List<desiredLangDTO> desiredLangDTO;
}
