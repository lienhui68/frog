/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.sample.mq.model;

import com.eh.frog.sample.enums.OrderEventType;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author f90fd4n david
 * @version 1.0.0: UserMessage.java, v 0.1 2021-09-29 2:06 下午 david Exp $$
 */
@ToString
@Data
public class OrderEventMessage {
	private Long orderId;
	private OrderEventType eventType;
	private BigDecimal orderAmount;
	private Date orderTime;
}