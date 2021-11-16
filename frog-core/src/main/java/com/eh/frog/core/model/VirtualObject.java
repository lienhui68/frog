/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.core.model;

import lombok.Data;
import lombok.ToString;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author f90fd4n david
 * @version 1.0.0: VirtualObject.java, v 0.1 2021-09-15 2:49 下午 david Exp $$
 */
@Data
@ToString
public class VirtualObject {

	private static final VirtualObject EMPTY = new VirtualObject();


	// 描述
	private String desc;
	// Object instance
	private Object object;

	/**
	 * 如果flags不为空则以flag为主，如果为空则以expect是否为null为主
	 * flag,<class, <field name, flag value>>
	 */
	public Map<String, Map<String, String>> flags = new LinkedHashMap<>();

	/**
	 * Constructor.
	 */
	public VirtualObject() {
	}

	public static VirtualObject empty() {
		return EMPTY;
	}

	public static VirtualObject of(Object value) {
		return new VirtualObject(value);
	}

	/**
	 * Constructor.
	 *
	 * @param obj the obj
	 */
	public VirtualObject(Object obj) {
		this.object = obj;
	}

	/**
	 * Constructor.
	 *
	 * @param obj the obj
	 * @param desc the desc
	 */
	public VirtualObject(Object obj, String desc) {
		this.object = obj;
		this.desc = desc;
	}

}