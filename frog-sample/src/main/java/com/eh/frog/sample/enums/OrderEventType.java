/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.sample.enums;

import com.eh.frog.core.util.ObjectUtil;

/**
 * @author f90fd4n david
 * @version 1.0.0: OrderEventType.java, v 0.1 2021-10-08 1:43 下午 david Exp $$
 */
public enum OrderEventType {
	CREATE, PAY_TODO, PAY_FINISH;

	public static void main(String[] args) {
		System.out.println(ObjectUtil.isNonNullBasicType(OrderEventType.CREATE.getClass()));
		System.out.println(ObjectUtil.isNonNullBasicType(OrderEventType.CREATE.getClass()));
	}
}