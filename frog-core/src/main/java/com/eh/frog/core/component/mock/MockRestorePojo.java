/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.core.component.mock;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author f90fd4n david
 * @version 1.0.0: MockRestorePojo.java, v 0.1 2021-10-04 1:04 下午 david Exp $$
 */
@AllArgsConstructor
@Data
public class MockRestorePojo {
	private Object container;
	private Object target;
	private String fieldName;
}