/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.core.component.prepare;

import com.eh.frog.core.model.PrepareData;

/**
 * @author f90fd4n david
 * @version 1.0.0: PrepareRuntimeContextHolder.java, v 0.1 2021-11-05 10:57 上午 david Exp $$
 */
public class PrepareRuntimeContextHolder {
	public static ThreadLocal<PrepareData> context = new ThreadLocal<>();

	/**
	 * Gets context.
	 *
	 * @return the context
	 */
	public static PrepareData getContext() {
		return context.get();
	}

	/**
	 * Sets context.
	 *
	 * @param prepareData the frog runtime context
	 */
	public static void setContext(PrepareData prepareData) {
		context.set(prepareData);
	}
}