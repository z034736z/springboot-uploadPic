package com.study.hello.spring.boot.mapper;

import com.study.hello.spring.boot.entity.User;
import tk.mybatis.MyMapper;

import java.util.List;
import java.util.Map;

public interface UserMapper extends MyMapper<User> {
    List<Map<String,Object>> getPicInfo(Map<String, Object> map);
}