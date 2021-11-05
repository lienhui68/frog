/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.plugin.redis.model;

import lombok.Data;

import java.util.List;

/**
 * @author f90fd4n david
 * @version 1.0.0: ClusterRedisPluginPrepareData.java, v 0.1 2021-11-03 8:58 下午 david Exp $$
 */
@Data
public class RedisPluginPrepareData {
	/**
	 * 准备string数据
	 */
	private List<RedisDataUnit> deptRedisData;

	/**
	 * 期望string数据
	 */
	private List<RedisDataUnit> expectRedisData;
}