/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.plugin.redis;

import com.eh.frog.core.plugin.PersistencePlugin;
import org.springframework.util.CollectionUtils;
import redis.clients.jedis.JedisCluster;

import java.util.Map;
import java.util.Optional;

/**
 * @author f90fd4n david
 * @version 1.0.0: RedisPersistencePluginImpl.java, v 0.1 2021-11-03 1:41 下午 david Exp $$
 */
public class ClusterRedisPersistencePluginImpl implements PersistencePlugin {

	@Override
	public void init(Map<String, Object> pluginMap) {
		if (CollectionUtils.isEmpty(pluginMap)) {
			throw new IllegalArgumentException("插件[RedisPersistencePluginImpl]缺少必要配置信息");
		}
		try {
			String host = (String) pluginMap.get(RedisPluginConstants.CONNECTION_HOST);
			Integer port = (Integer) pluginMap.get(RedisPluginConstants.CONNECTION_PORT);
			Integer maxTotal = (Integer) pluginMap.get(RedisPluginConstants.CONNECTION_MAX_TOTAL);
			Integer maxIdle = (Integer) pluginMap.get(RedisPluginConstants.CONNECTION_MAX_IDLE);
			JedisClusterUtil.init(host, port, maxTotal, maxIdle);
		} catch (Exception e) {
			throw new IllegalArgumentException("插件[RedisPersistencePluginImpl]配置信息有误", e);
		}
	}

	@Override
	public String getPluginSymbol() {
		return RedisPluginConstants.CLUSTER_REDIS_PLUGIN_PROVIDER_SYMBOL;
	}

	@Override
	public void prepare(Optional<Object> pluginParam) {
		JedisCluster jedisCluster = JedisClusterUtil.getJedisCluster();
		jedisCluster.set("test", "111");
		System.out.println("pre");
	}

	@Override
	public void check(Optional<Object> pluginParam) {
		JedisCluster jedisCluster = JedisClusterUtil.getJedisCluster();
		String test = jedisCluster.get("test");
		if ("111".equals(test)) {
			System.out.println("相等");
		}
		jedisCluster.close();
		System.out.println("chec");
	}

	@Override
	public void clean(Optional<Object> pluginParam) {
		System.out.println("cle");
	}
}