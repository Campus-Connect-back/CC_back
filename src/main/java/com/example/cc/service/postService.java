package com.example.cc.service;

import com.example.cc.dto.post.postDTO;
import com.example.cc.entity.*;
import com.example.cc.repository.ChatRoomRepository;
import com.example.cc.repository.PostRepository;
import com.example.cc.repository.UsersRepository;
import com.example.cc.repository.WeeklyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class postService {

    private final PostRepository postRepository;
    private final WeeklyRepository weeklyRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UsersRepository usersRepository;

    public void insertPost(postDTO postDTO){

        usersEntity user = usersRepository.findByStudentId(userAuthenticationEntity.builder().studentId(postDTO.getStudentId()).build());
        log.info("유저"+user);
//        채팅룸 save
        chatRoomEntity chatRoom =  chatRoomRepository.save(chatRoomEntity.builder()
            .createDate(new Date())
            .peopleNum(postDTO.getPeopleNum())
            .roomName(postDTO.getChatRoomName())
            .roomType(postDTO.getRoomType())
            .build());
//      post save
     postEntity post = postRepository.save(postEntity.builder()
                .DayOfWeek(postDTO.getDayOfWeek())
                .TimeInfo(postDTO.getTimeInfo())
                .faceToFace(postDTO.getFaceToFace())
                .language(postDTO.getLanguage())
                .postContent(postDTO.getPostContent())
                .postTitle(postDTO.getPostTitle())
                .studentId(user)
                .chatRoomId(chatRoom)
                .build()
        );

//     요일정보 save
    for(int i=1; i<=postDTO.getWeeklyInfos().size(); i++){
      weeklyRepository.save(weeklyEntity.builder()
              .postId(post)
              .week(postDTO.getWeeklyInfos().get(i).getWeek()).build());
    }

    }

}
