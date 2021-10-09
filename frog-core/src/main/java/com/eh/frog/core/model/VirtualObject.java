/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author f90fd4n david
 * @version 1.0.0: VirtualObject.java, v 0.1 2021-09-15 2:49 下午 david Exp $$
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class VirtualObject {
	// 描述
	private String desc;
	// Object instance
	private Object object;

	/** flag,<class, <field name, flag value>> */
	public Map<String, Map<String, String>> flags = new LinkedHashMap<>();

}