/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.sample.third.impl;

import com.eh.frog.sample.orm.bean.User;
import com.eh.frog.sample.third.ThirdService;
import org.springframework.stereotype.Service;

/**
 * @author f90fd4n david
 * @version 1.0.0: ThirdServiceImpl.java, v 0.1 2021-09-30 5:26 下午 david Exp $$
 */
@Service
public class ThirdServiceImpl implements ThirdService {
	@Override
	public User third(Integer condition1, String condition2) {
		User user = new User();
		user.setId(10000);
		user.setUserId(10000);
		user.setUserName("第三者");
		return user;
	}
}