/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.sample.rpc.response;

import lombok.Data;

import java.math.BigDecimal;

/**
 *
 * @author f90fd4n david
 * @version 1.0.0: ThirdResponse.java, v 0.1 2021-10-08 1:52 下午 david Exp $$
 */
@Data
public class Coupon {
	private Long userId;
	private Long couponId;
	private BigDecimal couponAmount;
}