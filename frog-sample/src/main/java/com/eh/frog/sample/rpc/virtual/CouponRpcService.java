/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.sample.rpc.virtual;

import com.eh.frog.sample.orm.bean.User;
import com.eh.frog.sample.rpc.response.Coupon;

import java.math.BigDecimal;

/**
 *
 * @author f90fd4n david
 * @version 1.0.0: ThirdService.java, v 0.1 2021-09-30 5:25 下午 david Exp $$
 */
public interface CouponRpcService {
	Coupon getCoupon(Long userId, BigDecimal orderAmount, CouponReq couponReq);
}