/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.sample;

import com.eh.frog.component.event.MessageEventAdvisorConfig;
import com.eh.frog.sample.base.Response;
import com.eh.frog.sample.controller.UserController;
import com.eh.frog.sample.orm.bean.User;
import com.eh.frog.sample.orm.dao.UserMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author f90fd4n david
 * @version 1.0.0: dao.java, v 0.1 2021-09-14 9:11 下午 david Exp $$
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {FrogSampleApplication.class, MessageEventAdvisorConfig.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerTest {
	@Autowired
	private UserMapper userMapper;
	@Autowired
	private UserController userController;

	@Test
	public void test() {
		User user = userController.getUser(1);
		System.out.println(user);
	}

	/**
	 * 创建用户
	 */
	@Test
	public void createUser() {
		Response response = userController.createUser(new User(1025229, "宋江"));
		System.out.println(response);
	}
}
