/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.core.context;

/**
 * @author f90fd4n david
 * @version 1.0.0: TestDataFilePathHolder.java, v 0.1 2021-11-11 11:00 上午 david Exp $$
 */
public class TestDataFilePathHolder {
	public static ThreadLocal<String> context = new ThreadLocal<>();

	/**
	 * Gets context.
	 *
	 * @return the context
	 */
	public static String getContext() {
		return context.get();
	}

	/**
	 * Sets context.
	 *
	 * @param testDataFilePath the frog test data file absolute path
	 */
	public static void setContext(String testDataFilePath) {
		context.set(testDataFilePath);
	}
}