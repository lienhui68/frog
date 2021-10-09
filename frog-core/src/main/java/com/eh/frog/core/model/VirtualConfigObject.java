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
	private String configClass;
	private String configKey;
	private Object configValue;
}