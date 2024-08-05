package com.example.cc.dto.accounts;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class studentDatabaseDTO {
    private String studentId;
    private String studentName;
    private String major;
}
