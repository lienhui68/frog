/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.core.model;

import lombok.Data;

/**
 * @author f90fd4n david
 * @version 1.0.0: VirtualMockObject.java, v 0.1 2021-09-30 5:22 下午 david Exp $$
 */
@Data
public class VirtualMockObject {
	// 描述
	private String desc;
	private String container;
	private String fieldName;
	// target service
	private String target;
	private String targetBeanName;
	// mock obj
	private Object object;
}