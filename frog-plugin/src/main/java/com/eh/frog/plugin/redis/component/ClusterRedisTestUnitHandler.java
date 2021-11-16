/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.plugin.redis.component;

import com.eh.frog.core.util.ObjectUtil;
import com.eh.frog.core.util.StringUtil;
import com.eh.frog.plugin.redis.model.RedisDataUnit;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Assertions;
import org.springframework.util.CollectionUtils;
import redis.clients.jedis.JedisCluster;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author f90fd4n david
 * @version 1.0.0: TestRedisUnitHandler.java, v 0.1 2021-11-04 9:34 上午 david Exp $$
 */
@Slf4j
@AllArgsConstructor
public class ClusterRedisTestUnitHandler {
	private JedisCluster jedisCluster;

	public void prepareData(List<RedisDataUnit> deptStringUnits) {
		deptStringUnits.forEach(this::prepareData);
	}

	public void checkData(List<RedisDataUnit> expectStringUnits) {
		expectStringUnits.forEach(this::checkData);
	}

	public void cleanData(List<RedisDataUnit> deptStringUnits, List<RedisDataUnit> expectStringUnits) {
		List<String> keys = Lists.newArrayList();
		if (!CollectionUtils.isEmpty(deptStringUnits)) {
			List<String> prepareKeys = deptStringUnits.stream().map(u -> u.getKey()).collect(Collectors.toList());
			keys.addAll(prepareKeys);
		}
		if (!CollectionUtils.isEmpty(expectStringUnits)) {
			List<String> expectKeys = expectStringUnits.stream().map(u -> u.getKey()).collect(Collectors.toList());
			keys.addAll(expectKeys);
		}
		keys.stream().distinct().forEach(this::cleanData);

	}

	private void prepareData(RedisDataUnit redisDataUnit) {
		Assertions.assertNotNull(redisDataUnit.getKey(), "redis key can't be null");
		Object value = redisDataUnit.getValue();
		Assertions.assertNotNull(value, "redis value can't be null");
		Assertions.assertNotNull(redisDataUnit.getExpire(), "redis expire can't be null");
		Boolean micro = Objects.isNull(redisDataUnit.getMicroSecond()) ? false : redisDataUnit.getMicroSecond();
		if (value instanceof String) {
			if (micro) {
				jedisCluster.psetex(redisDataUnit.getKey(), redisDataUnit.getExpire(), String.valueOf(value));
			} else {
				jedisCluster.setex(redisDataUnit.getKey(), redisDataUnit.getExpire().intValue(), String.valueOf(value));
			}
		}
		log.info("已完成数据准备:{}", redisDataUnit);
	}

	private void checkData(RedisDataUnit redisDataUnit) {
		Assertions.assertNotNull(redisDataUnit.getKey(), "redis key can't be null");
		Object value = redisDataUnit.getValue();
		Assertions.assertNotNull(value, "redis value can't be null");
		if (value instanceof String) {
			String actual = jedisCluster.get(redisDataUnit.getKey());
			Assertions.assertEquals(String.valueOf(value), actual, StringUtil.buildMessage("key:{}实际与期望比较不一致", redisDataUnit.getKey()));
		}
		log.info("已完成数据校验,key:{},value:{}", redisDataUnit.getKey(), ObjectUtil.toJson(redisDataUnit.getValue()));
	}

	private void cleanData(String key) {
		Assertions.assertNotNull(key);
		jedisCluster.del(key);
		log.info("已完成数据清理, key:{}", key);
	}

}