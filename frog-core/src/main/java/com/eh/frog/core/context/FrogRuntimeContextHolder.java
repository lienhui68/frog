/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.core.context;

/**
 * @author f90fd4n david
 * @version 1.0.0: FrogRuntimeContextThreadHolder.java, v 0.1 2021-09-16 4:22 下午 david Exp $$
 */
public class FrogRuntimeContextHolder {
	public static ThreadLocal<FrogRuntimeContext> context = new ThreadLocal<FrogRuntimeContext>();

	/**
	 * Gets context.
	 *
	 * @return the context
	 */
	public static FrogRuntimeContext getContext() {
		return context.get();
	}

	/**
	 * Sets context.
	 *
	 * @param actsRuntimeContext the acts runtime context
	 */
	public static void setContext(FrogRuntimeContext actsRuntimeContext) {
		context.set(actsRuntimeContext);
	}
}