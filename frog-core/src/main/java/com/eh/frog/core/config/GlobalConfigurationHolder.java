/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.core.config;

import com.eh.frog.core.constants.FrogConfigConstants;
import org.junit.jupiter.api.Assertions;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author f90fd4n david
 * @version : GlobalConfigurationHolder.java, v 0.1 2021-09-28 11:31 上午 david Exp $$
 */
public class GlobalConfigurationHolder {

	public static ThreadLocal<Map<String, String>> globalConfigurationThreadLocal = new ThreadLocal<>();

	/**
	 * 表名,清除时依据的keys
	 */
	public static ThreadLocal<Map<String, List<String>>> selectKeysThreadLocal = new ThreadLocal<>();

	public static ThreadLocal<Map<String, Map<String, Object>>> extensionConfigThreadLocal = new ThreadLocal<>();

	/**
	 * Gets configuration.
	 *
	 * @return the configuration
	 */
	public static Map<String, String> getGlobalConfiguration() {
		return globalConfigurationThreadLocal.get();
	}


	/**
	 * Sets configuration.
	 *
	 * @param globalConfiguration
	 */
	public static void setGlobalConfiguration(Map<String, String> globalConfiguration) {
		globalConfigurationThreadLocal.set(globalConfiguration);
	}

	public static void setSelectKeys(Map<String, List<String>> selectKeys) {
		selectKeysThreadLocal.set(selectKeys);
	}

	public static List<String> getSelectKeys(String tableName) {
		Assertions.assertNotNull(tableName);
		List<String> selectKeys = selectKeysThreadLocal.get().get(tableName);
		if (CollectionUtils.isEmpty(selectKeys)) {
			selectKeys = selectKeysThreadLocal.get().get(FrogConfigConstants.FROG_VIRTUAL_COMMON_TABLE);
		}
		return selectKeys;
	}

	public static Optional<Map<String, Map<String, Object>>> getExtensionConfig() {
		Map<String, Map<String, Object>> configMap = extensionConfigThreadLocal.get();
		return Optional.ofNullable(configMap);
	}

	public static void setExtensionConfig(Map<String, Map<String, Object>> extensionConfig) {
		extensionConfigThreadLocal.set(extensionConfig);
	}
}