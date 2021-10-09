package com.eh.frog.sample.controller;


import com.eh.frog.sample.base.Response;
import com.eh.frog.sample.orm.bean.User;
import com.eh.frog.sample.orm.dao.UserMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@AllArgsConstructor
@RestController
public class UserController {

	private final UserMapper userMapper;

	@GetMapping("/user/{id}")
	public User getUser(@PathVariable("id") Long id) {
		return userMapper.selectByPrimaryKey(id);
	}

	@PostMapping("/user/create")
	public Response<Boolean> createUser(@Param("user") User user) {
		// 数据库
		userMapper.insertSelective(user);
		return Response.success(true);
	}

}