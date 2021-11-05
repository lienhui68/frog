/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.plugin.redis.model;

import lombok.Data;
import lombok.ToString;

/**
 * @author f90fd4n david
 * @version 1.0.0: RedisStringUnit.java, v 0.1 2021-11-03 9:02 下午 david Exp $$
 */
@ToString
@Data
public class RedisDataUnit {
	private String key;
	private Object value;
	private Long expire;
	private Boolean microSecond;
}