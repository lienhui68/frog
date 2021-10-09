/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.sample.mq.producer;

import com.alibaba.fastjson.JSON;
import com.eh.frog.sample.mq.model.OrderEventMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.common.message.Message;
import org.springframework.stereotype.Service;

/**
 * @author f90fd4n david
 * @version 1.0.0: UserMqSender.java, v 0.1 2021-09-29 2:04 下午 david Exp $$
 */
@Slf4j
@Service
public class OrderEventSender {
	public void sendMessage(OrderEventMessage orderEventMessage) {
		try {
			Message message = new Message("tp_order_change", JSON.toJSONString(orderEventMessage).getBytes("UTF-8"));
			MQProducer mqProducer = new DefaultMQProducer();
			mqProducer.send(message);
		} catch (Exception e) {
			// do nothing.
		}
	}
}