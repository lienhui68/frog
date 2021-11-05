/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.plugin.redis.constants;

/**
 * @author f90fd4n david
 * @version 1.0.0: RedisPluginConstants.java, v 0.1 2021-11-03 3:03 下午 david Exp $$
 */
public final class RedisPluginConstants {
	private RedisPluginConstants() {
	}

	public final static String STANDALONE_REDIS_PLUGIN_PROVIDER_SYMBOL = "frog-standalone-redis-plugin";
	public final static String CLUSTER_REDIS_PLUGIN_PROVIDER_SYMBOL = "frog-cluster-redis-plugin";
	public final static String CONNECTION_HOST = "host";
	public final static String CONNECTION_PORT = "port";
	public final static String CONNECTION_MAX_TOTAL = "maxTotal";
	public final static String CONNECTION_MAX_IDLE = "maxIdle";
}