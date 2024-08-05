package com.example.cc.dto.accounts;

import com.example.cc.entity.availableLangEntity;
import com.example.cc.entity.desiredLangEntity;
import com.example.cc.entity.userAuthenticationEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class usersDTO {
    private Long userId;
    private String nickName;
    private String password;
    private Date birthday;
    private String nationality;
    private String imgUrl;
    private String studentId;
    private List<availableLangDTO> availableLang;
    private List<desiredLangDTO> desiredLang;
}
