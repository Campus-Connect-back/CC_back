package com.example.cc.mapper;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TimeMapper {

    String getTime();
}
