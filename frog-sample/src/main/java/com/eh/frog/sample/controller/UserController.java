package com.eh.frog.sample.controller;


import com.eh.frog.sample.base.Response;
import com.eh.frog.sample.mq.model.UserMessage;
import com.eh.frog.sample.mq.producer.UserMqSender;
import com.eh.frog.sample.orm.bean.User;
import com.eh.frog.sample.orm.dao.UserMapper;
import com.eh.frog.sample.third.ThirdService;
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
	private final UserMqSender userMqSender;
	private final ThirdService thirdService;

	@GetMapping("/user/{id}")
	public User getUser(@PathVariable("id") Integer id) {
		return userMapper.selectByPrimaryKey(id);
	}

	@PostMapping("/user/create")
	public Response<Boolean> createUser(@Param("user") User user) {
		// 数据库
		userMapper.insertSelective(user);
		// 消息
		UserMessage userMessage = new UserMessage();
		userMessage.setUserId(user.getUserId());
		userMessage.setUserName(user.getUserName());
		userMqSender.sendMessage(userMessage);
		// mock
		User third = thirdService.third(1, "2");
		log.info(third.toString());
		return Response.success(true);
	}

}