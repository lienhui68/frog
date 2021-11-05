/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.plugin.redis;

import com.eh.frog.core.plugin.PersistencePlugin;
import com.eh.frog.plugin.redis.component.ClusterRedisTestUnitHandler;
import com.eh.frog.plugin.redis.constants.RedisPluginConstants;
import com.eh.frog.plugin.redis.model.RedisDataUnit;
import com.eh.frog.plugin.redis.model.RedisPluginPrepareData;
import com.eh.frog.plugin.redis.util.JedisClusterUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 目前只支持一些简单的操作，设置string参数
 * 复杂操作请使用frog提供的注解执行
 * 设计原则：五大数据结构自动判断类型，负责set、get，并且都需要有超时时间
 *
 * @author f90fd4n david
 * @version 1.0.0: RedisPersistencePluginImpl.java, v 0.1 2021-11-03 1:41 下午 david Exp $$
 */
@Slf4j
public class ClusterRedisPersistencePluginImpl implements PersistencePlugin {

	private ClusterRedisTestUnitHandler testUnitHandler;

	@Override
	public void init(Map<String, Object> pluginMap) {
		log.info("插件[RedisPersistencePluginImpl]执行初始化,config:{}", pluginMap);
		if (CollectionUtils.isEmpty(pluginMap)) {
			throw new IllegalArgumentException("插件[RedisPersistencePluginImpl]缺少必要配置信息");
		}
		try {
			String host = (String) pluginMap.get(RedisPluginConstants.CONNECTION_HOST);
			Integer port = (Integer) pluginMap.get(RedisPluginConstants.CONNECTION_PORT);
			Integer maxTotal = (Integer) pluginMap.get(RedisPluginConstants.CONNECTION_MAX_TOTAL);
			Integer maxIdle = (Integer) pluginMap.get(RedisPluginConstants.CONNECTION_MAX_IDLE);
			JedisClusterUtil.init(host, port, maxTotal, maxIdle);
			// 初始化执行器
			testUnitHandler = new ClusterRedisTestUnitHandler(JedisClusterUtil.getJedisCluster());
		} catch (Exception e) {
			throw new IllegalArgumentException("插件[RedisPersistencePluginImpl]配置信息有误", e);
		}
		log.info("插件[RedisPersistencePluginImpl]完成初始化");
	}

	@Override
	public String getPluginSymbol() {
		return RedisPluginConstants.CLUSTER_REDIS_PLUGIN_PROVIDER_SYMBOL;
	}

	@Override
	public void prepare(Optional<Object> pluginParam) {
		if (pluginParam.isPresent()) {

			RedisPluginPrepareData prepareData = (RedisPluginPrepareData) pluginParam.get();
			List<RedisDataUnit> deptRedisDatas = prepareData.getDeptRedisData();
			if (!CollectionUtils.isEmpty(deptRedisDatas)) {
				log.info("插件[RedisPersistencePluginImpl]开始准备数据");
				testUnitHandler.prepareData(deptRedisDatas);
				log.info("插件[RedisPersistencePluginImpl]完成准备数据");
			} else {
				log.info("插件[RedisPersistencePluginImpl] no prepare data");
			}
		} else {
			log.info("插件[RedisPersistencePluginImpl] no config data");
		}

	}

	@Override
	public void check(Optional<Object> pluginParam) {
		if (pluginParam.isPresent()) {
			RedisPluginPrepareData prepareData = (RedisPluginPrepareData) pluginParam.get();
			List<RedisDataUnit> expectRedisDatas = prepareData.getExpectRedisData();
			if (!CollectionUtils.isEmpty(expectRedisDatas)) {
				log.info("插件[RedisPersistencePluginImpl]开始检查数据");
				testUnitHandler.checkData(expectRedisDatas);
				log.info("插件[RedisPersistencePluginImpl]完成检查数据");
			} else {
				log.info("插件[RedisPersistencePluginImpl] no check data");
			}
		} else {
			log.info("插件[RedisPersistencePluginImpl] no config data");
		}
	}

	@Override
	public void clean(Optional<Object> pluginParam) {
		if (pluginParam.isPresent()) {
			RedisPluginPrepareData prepareData = (RedisPluginPrepareData) pluginParam.get();
			List<RedisDataUnit> deptRedisDatas = prepareData.getDeptRedisData();
			List<RedisDataUnit> expectRedisDatas = prepareData.getExpectRedisData();
			if (!CollectionUtils.isEmpty(deptRedisDatas) || !CollectionUtils.isEmpty(expectRedisDatas)) {
				log.info("插件[RedisPersistencePluginImpl]开始清理数据");
				testUnitHandler.cleanData(deptRedisDatas, expectRedisDatas);
				log.info("插件[RedisPersistencePluginImpl]完成清理数据");
			} else {
				log.info("插件[RedisPersistencePluginImpl] no clean data");
			}
		} else {
			log.info("插件[RedisPersistencePluginImpl] no config data");
		}
	}

}