/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.core.model.ext;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author f90fd4n david
 * @version 1.0.0: MockUnit.java, v 0.1 2021-11-15 4:25 下午 david Exp $$
 */
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Data
public class MockUnit {
	// spel布尔表达式
	private String when;
	// mock返回
	private Object thenReturn;
}