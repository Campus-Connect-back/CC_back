package com.example.cc.service;

import com.example.cc.dto.post.postDTO;
import com.example.cc.dto.post.updateDTO;
import com.example.cc.entity.*;
import com.example.cc.repository.ChatRoomRepository;
import com.example.cc.repository.PostRepository;
import com.example.cc.repository.UsersRepository;
import com.example.cc.repository.WeeklyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class postService {

    private final PostRepository postRepository;
    private final WeeklyRepository weeklyRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UsersRepository usersRepository;

//    하나의 트렌젝션 => 롤백
    @Transactional
    public void insertPost(postDTO postDTO){

        usersEntity user = usersEntity.builder().userId(postDTO.getPostId()).build();

//        채팅룸 save
        chatRoomEntity chatRoom =  chatRoomRepository.save(chatRoomEntity.builder()
            .createDate(new Date()) //dto 에 담아온 정보를 엔티티에 담아서 저장, autoincriment는 불포함
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
                .userId(user)
                .chatRoomId(chatRoom)
                .build()
        );

//     요일정보 save
    for(int i=0; i<postDTO.getWeeklyInfos().size(); i++){
      weeklyRepository.save(weeklyEntity.builder()
              .postId(post)
              .week(postDTO.getWeeklyInfos().get(i).getWeek()).build());
    }

  }

//  post 삭제
  @Transactional
    public void deletePost(Long postId){
      postEntity post =  postRepository.getReferenceById(postId);
     List<weeklyEntity> postedWeekly = weeklyRepository.findByPostId(post);

//     post 에딸린 weekly 삭제
     for(int i=0; i<postedWeekly.size(); i++) {
         weeklyRepository.delete(postedWeekly.get(i));
     }
      postRepository.delete(post);

  }

//  게시글 상세보기/ 채팅방 상세보기
  public postEntity selectById(Long postId){
       return postRepository.findByPostId(postId);
  }

//  작성한 게시글 목록 언어별로 띄워줌
  public List<postEntity> selectPostList(String Language){
        if(Language.equals("전체")){
            return postRepository.findAll();
        }
      return postRepository.findByLanguage(Language);
  }

//  작성한 게시글 수정
    public void updatePost(updateDTO updateDTO){
//        id 로 post객체 찾기
        postEntity myPost = postRepository.findByPostId(updateDTO.getPostId());

//        해당 객체의 entity set
        myPost.setPostContent(updateDTO.getPostContent());
        myPost.setPostTitle(updateDTO.getPostTitle());

        postRepository.save(myPost);

    }

//   게시글 검색
    public List<postEntity> searchPost(String title){
        return postRepository.findByPostTitleContaining(title);
    }
}
