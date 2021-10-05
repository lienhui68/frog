/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * @author f90fd4n david
 * @version 1.0.0: VirtualArgs.java, v 0.1 2021-09-15 2:41 下午 david Exp $$
 */
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class VirtualArgs {

	@Setter
	// Ordered input list
	public List<VirtualObject> inputArgs;

	/**
	 * Gets input args.
	 *
	 * @return the input args
	 */
	public List<Object> getInputArgs() {
		if (inputArgs == null) {
			return null;
		}
		List<Object> args = new ArrayList<Object>();
		for (VirtualObject virtualObject : inputArgs) {
			args.add(virtualObject.getObject());
		}
		return args;
	}
}