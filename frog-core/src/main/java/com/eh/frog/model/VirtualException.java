/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.model;

import lombok.Data;

/**
 * @author f90fd4n david
 * @version 1.0.0: VirtualException.java, v 0.1 2021-09-30 3:57 下午 david Exp $$
 */
@Data
public class VirtualException {
	/**
	 * Exception object
	 */
	private VirtualObject expectException;

	/**
	 * Gets exception class.
	 *
	 * @return the exception class
	 */
	public String getExceptionClass() {
		if (expectException == null) {
			return null;
		}
		return expectException.getObjClass();
	}

	/**
	 * Gets expect exception object.
	 *
	 * @return the expect exception object
	 */
	public Object getExpectExceptionObject() {
		if (expectException == null) {
			return null;
		}
		return expectException.getObject();
	}
}