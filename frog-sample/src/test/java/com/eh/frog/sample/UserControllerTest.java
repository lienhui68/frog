/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.sample;

import com.eh.frog.core.component.config.FrogComponentConfiguration;
import com.eh.frog.sample.base.Response;
import com.eh.frog.sample.controller.UserController;
import com.eh.frog.sample.orm.bean.User;
import com.eh.frog.sample.orm.dao.UserMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * @author f90fd4n david
 * @version 1.0.0: dao.java, v 0.1 2021-09-14 9:11 下午 david Exp $$
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {FrogSampleApplication.class, FrogComponentConfiguration.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerTest  {
	@Autowired
	private UserMapper userMapper;
	@Autowired
	private UserController userController;

	@Test
	public void test() {
		User user = userController.getUser(1L);
		System.out.println(user);
	}

	/**
	 * 创建用户
	 */
	@Test
	public void createUser() {
		User user = new User();
		user.setUserId(2L);
		user.setUserName("武松");
		Response response = userController.createUser(user);
		System.out.println(response);
	}
}
