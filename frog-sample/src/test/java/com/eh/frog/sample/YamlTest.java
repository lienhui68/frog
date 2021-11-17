/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.sample;

import com.eh.frog.core.config.FrogConfig;
import com.eh.frog.core.model.*;
import com.eh.frog.core.model.ext.MockUnit;
import com.eh.frog.core.util.FrogFileUtil;
import com.eh.frog.plugin.redis.model.RedisDataUnit;
import com.eh.frog.plugin.redis.model.RedisPluginPrepareData;
import com.eh.frog.sample.enums.OrderEventType;
import com.eh.frog.sample.mq.model.OrderEventMessage;
import com.eh.frog.sample.orm.bean.Order;
import com.eh.frog.sample.rpc.response.Coupon;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.math.BigDecimal;
import java.util.*;

/**
 *
 * @author f90fd4n david
 * @version 1.0.0: YamlTest.java, v 0.1 2021-10-08 2:29 下午 david Exp $$
 */
@Slf4j
public class YamlTest {
	@Test
	public void test1() {
		PrepareData prepareData = new PrepareData();
		// 入参
		List<VirtualObject> args = Lists.newArrayList();
		Order order = new Order();
		order.setBuyerId(1025229L);
		order.setOrderAmount(new BigDecimal("225.25"));
		order.setOrderTime(new Date());
//		args.add(new VirtualObject("入参", order));
		prepareData.setArgs(args);
		// 消息体
		OrderEventMessage orderEventMessage = new OrderEventMessage();
		orderEventMessage.setOrderId(111L);
		orderEventMessage.setEventType(OrderEventType.CREATE);
		VirtualEventGroup eventGroup = new VirtualEventGroup();
		eventGroup.setDesc("订单创建成功消息");
		eventGroup.setMsgClass("com.eh.frog.sample.mq.model.OrderEventMessage");
		eventGroup.setObjects(Lists.newArrayList(orderEventMessage));
		Map<String, Map<String, String>> flags = new LinkedHashMap<>();
		prepareData.setExpectEventSet(Lists.newArrayList(eventGroup));
		// 添加缓存
		Map<String, Object> pluginParams = Maps.newHashMap();
		RedisPluginPrepareData redisPluginPrepareData = new RedisPluginPrepareData();
		RedisDataUnit redisDataUnit1 = new RedisDataUnit();
		redisDataUnit1.setKey("lock-1025229");
		redisDataUnit1.setValue("123");
		redisDataUnit1.setExpire(3L);
		redisDataUnit1.setMicroSecond(false);
		RedisDataUnit redisDataUnit2 = new RedisDataUnit();
		redisDataUnit2.setKey("lock-1025229");
		redisDataUnit2.setValue("123");
		redisDataUnit2.setExpire(3L);
		redisDataUnit2.setMicroSecond(false);
		redisPluginPrepareData.setDeptRedisData(Lists.newArrayList(redisDataUnit1));
		redisPluginPrepareData.setExpectRedisData(Lists.newArrayList(redisDataUnit2));
		pluginParams.put("frog-cluster-redis-plugin", redisPluginPrepareData);
		prepareData.setExtendParams(pluginParams);
		// mock第三方服务
		VirtualMockObject mock = new VirtualMockObject();
		Coupon coupon = new Coupon();
		coupon.setUserId(1025229L);
		MockUnit mockUnit1 = new MockUnit("aaaa", coupon);
		Coupon coupon2 = new Coupon();
		coupon2.setUserId(1025229L);
		MockUnit mockUnit2 = new MockUnit("aaaa", coupon2);
		mock.setMockUnits(Lists.newArrayList(mockUnit1, mockUnit2));
		prepareData.setVirtualMockSet(Lists.newArrayList(mock));
		// mock接口调用
		VirtualInvocationGroup virtualInvocationGroup = new VirtualInvocationGroup();
		virtualInvocationGroup.setDesc("virtualInvocationGroup");
		List<Object> invocationArgs = Lists.newArrayList("aaaa", "bbb");
		List<Object> invocationArgs2 = Lists.newArrayList("aaaa2", "bbb2");
		List<List<Object>> lists = Lists.newArrayList(invocationArgs, invocationArgs2);
		virtualInvocationGroup.setObjects(lists);
		prepareData.setExpectInvocationSet(Lists.newArrayList(virtualInvocationGroup));
		String yaml = new Yaml().dump(prepareData);
		log.info("\n" + yaml);
	}

	/**
	 * 日期格式
	 */
	@Test
	public void test2() {
		String dateYaml = "2015-11-17T15:30:30z";
		DumperOptions options = new DumperOptions();
		options.setTimeZone(TimeZone.getTimeZone("GMT+7:30"));
		Yaml yaml = new Yaml(options);
		Object load = yaml.load(dateYaml);
		System.out.println(load);
	}

	/**
	 * 初始化配置文件
	 */
//	@Test
	public void test3() {
//		FrogSerializationSupporter.initConfigFile();
		FrogConfig frogConfig = FrogFileUtil.loadGlobalConfigFromYaml();
		System.out.println();
	}
}