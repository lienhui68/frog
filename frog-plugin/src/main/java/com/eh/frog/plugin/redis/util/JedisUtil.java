/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.plugin.redis.util;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author f90fd4n david
 * @version 1.0.0: JedisPoolUtil.java, v 0.1 2021-11-03 2:49 下午 david Exp $$
 */
public class JedisUtil {
	private static volatile JedisPool jedisPool = null;
	private static String host;
	private static Integer port;
	private static Integer maxTotal;
	private static Integer maxIdle;

	public static void init(String host, Integer port, Integer maxTotal, Integer maxIdle) {
		JedisUtil.host = host;
		JedisUtil.port = port;
		JedisUtil.maxTotal = maxTotal;
		JedisUtil.maxIdle = maxIdle;
	}

	private JedisUtil() {
	}

	public static JedisPool getJedisPoolInstance() {
		if (null == jedisPool) {
			synchronized (JedisUtil.class) {
				if (null == jedisPool) {
					// 配置
					JedisPoolConfig poolConfig = new JedisPoolConfig();
					poolConfig.setMaxTotal(maxTotal);
					poolConfig.setMaxIdle(maxIdle);
					jedisPool = new JedisPool(poolConfig, host, port);
				}
			}
		}
		return jedisPool;
	}

}