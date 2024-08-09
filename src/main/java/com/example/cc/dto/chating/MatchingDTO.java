package com.example.cc.dto.chating;

import com.example.cc.dto.accounts.usersDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchingDTO {
    private chatRoomDTO chatRoomDTO;
    private usersDTO user1DTO;
    private usersDTO user2DTO;
}
