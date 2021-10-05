/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.config;

import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author f90fd4n david
 * @version : GlobalConfigurationHolder.java, v 0.1 2021-09-28 11:31 上午 david Exp $$
 */
public class GlobalConfigurationHolder {

	public static ThreadLocal<Properties> globalConfigurationThreadLocal = new ThreadLocal<>();

	/**
	 * 表名,清除时依据的keys
	 */
	public static ThreadLocal<Map<String, List<String>>> selectKeysThreadLocal = new ThreadLocal<>();

	/**
	 * Gets configuration.
	 *
	 * @return the configuration
	 */
	public static Properties getGlobalConfiguration() {
		return globalConfigurationThreadLocal.get();
	}

	/**
	 * Sets configuration.
	 *
	 * @param globalConfiguration
	 */
	public static void setGlobalConfiguration(Properties globalConfiguration) {
		globalConfigurationThreadLocal.set(globalConfiguration);
	}

	public static Map<String, List<String>> getSelectKeys() {
		return selectKeysThreadLocal.get();
	}

	public static void setSelectKeys(Map<String, List<String>> selectKeys) {
		selectKeysThreadLocal.set(selectKeys);
	}

}