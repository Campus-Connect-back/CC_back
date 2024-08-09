package com.example.cc.controller;

import com.example.cc.config.PrincipalDetails;
import com.example.cc.dto.chating.MatchingDTO;
import com.example.cc.dto.chating.chatRoomDTO;
import com.example.cc.entity.chatRoomEntity;
import com.example.cc.entity.usersEntity;
import com.example.cc.service.MatchService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/match")
public class MatchController {
    private final MatchService matchService;
   // 매칭 시작
    @PostMapping("/enqueue")
    public ResponseEntity<chatRoomDTO> startMatch(@AuthenticationPrincipal PrincipalDetails principalDetails){
        chatRoomDTO chatRoom = matchService.startMatch(principalDetails);

        if (chatRoom == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }
        return ResponseEntity.ok(chatRoom);
    }
    // 매칭 취소
    @PostMapping("/cancel")
    public ResponseEntity<Void> cancelMatch(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        matchService.cancelMatch(); // 매칭 취소 메서드 호출
        return ResponseEntity.ok().build(); // 200 OK 응답
    }
    // 매칭 결과
    @GetMapping("/result")
    public ResponseEntity<MatchingDTO> matchingResult(@RequestParam Long roomId){
        MatchingDTO result = matchService.matchingResult(roomId);
        return ResponseEntity.ok(result);
    }

}
