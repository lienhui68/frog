/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.sample;

import com.eh.frog.core.model.PrepareData;
import com.eh.frog.core.model.VirtualEventGroup;
import com.eh.frog.core.model.VirtualObject;
import com.eh.frog.sample.enums.OrderEventType;
import com.eh.frog.sample.mq.model.OrderEventMessage;
import com.eh.frog.sample.orm.bean.Order;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.junit.Test;
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
}