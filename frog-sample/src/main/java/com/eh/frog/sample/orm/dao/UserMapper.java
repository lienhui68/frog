package com.eh.frog.sample.orm.dao;

import com.eh.frog.sample.orm.bean.User;

public interface UserMapper {
    int deleteByPrimaryKey(Long id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Long id);

    User selectByUserId(Long userId);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);
}