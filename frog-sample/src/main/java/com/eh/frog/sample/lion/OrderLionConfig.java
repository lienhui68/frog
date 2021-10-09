/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.sample.lion;

import lombok.Getter;
import org.springframework.stereotype.Component;

/**
 * @author f90fd4n david
 * @version 1.0.0: OrderLionConfig.java, v 0.1 2021-10-05 12:51 下午 david Exp $$
 */
@Component
public class OrderLionConfig {

	@Getter
	private static volatile Boolean bizSwitch = true;
}