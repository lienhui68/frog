/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.plugin.redis;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import java.util.HashSet;
import java.util.Set;

/**
 * @author f90fd4n david
 * @version 1.0.0: JedisClusterUtil.java, v 0.1 2021-11-03 5:13 下午 david Exp $$
 */
public class JedisClusterUtil {
	private static volatile JedisCluster jedisCluster = null;
	private static Set<HostAndPort> nodes = new HashSet<>();
	private static Integer maxTotal;
	private static Integer maxIdle;

	public static void init(String host, Integer port, Integer maxTotal, Integer maxIdle) {
		JedisClusterUtil.maxTotal = maxTotal;
		JedisClusterUtil.maxIdle = maxIdle;
		// 解析host:port
		nodes.add(new HostAndPort(host, port));
	}

	private JedisClusterUtil() {
	}

	public static JedisCluster getJedisCluster() {
		if (null == jedisCluster) {
			synchronized (JedisClusterUtil.class) {
				if (null == jedisCluster) {
					// 配置
					GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
					poolConfig.setMaxTotal(maxTotal);
					poolConfig.setMaxIdle(maxIdle);
					jedisCluster = new JedisCluster(nodes, poolConfig);
				}
			}
		}
		return jedisCluster;
	}
}