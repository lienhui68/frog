/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.core.model;

import lombok.Data;

/**
 * @author f90fd4n david
 * @version 1.0.0: VirtualConfigObject.java, v 0.1 2021-10-05 1:11 下午 david Exp $$
 */
@Data
public class VirtualConfigObject {
	private String desc;
	// 配置Bean类路径
	private String container;
	// 配置项的属性名称
	private String configField;
	// 配置项的值
	private Object configValue;
}