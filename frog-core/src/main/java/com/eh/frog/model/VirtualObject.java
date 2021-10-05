/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
	// class name
	private String objClass;
	// Object instance
	private Object object;

	/**
	 * Constructor.
	 *
	 * @param obj the obj
	 */
	public VirtualObject(Object obj) {
		if (obj != null) {
			this.objClass = obj.getClass().getName();
		} else {
			this.objClass = null;
		}
		this.object = obj;
	}
}