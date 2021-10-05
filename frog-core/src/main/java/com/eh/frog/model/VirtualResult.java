/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author f90fd4n david
 * @version 1.0.0: VirtualResult.java, v 0.1 2021-09-15 2:52 下午 david Exp $$
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VirtualResult {
	// result object
	private VirtualObject result;

	/**
	 * Constructor.
	 *
	 * @param resultObj the result obj
	 */
	public VirtualResult(Object resultObj) {

		this.result = new VirtualObject(resultObj);

	}
}