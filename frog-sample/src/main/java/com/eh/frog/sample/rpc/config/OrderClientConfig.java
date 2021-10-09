/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.sample.rpc.config;

import com.eh.frog.sample.rpc.virtual.CouponRpcService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author f90fd4n david
 * @version 1.0.0: OrderClientConfig.java, v 0.1 2021-10-08 2:05 下午 david Exp $$
 */
@Configuration
public class OrderClientConfig {
	@Bean
	public CouponRpcService couponRpcService() {
		// 连接，获取代理bean,这里假设没有获取到
		return (userId, orderAmount) -> null;
	}
}