package com.example.cc.mapper;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class TimeMapperTest {

    @Autowired
    TimeMapper timeMapper;

    @Test
    void TimeTest(){
       log.info(timeMapper.getTime());
    }
}