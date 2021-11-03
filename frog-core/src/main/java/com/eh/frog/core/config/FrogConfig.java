/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.core.config;

import com.eh.frog.core.constants.FrogConfigConstants;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author f90fd4n david
 * @version 1.0.0: FrogConfig.java, v 0.1 2021-11-02 5:27 下午 david Exp $$
 */
@Setter
public class FrogConfig {
	/**
	 * 基础配置
	 *
	 * @see FrogConfigConstants
	 */
	private Map<String, String> baseConfig;

	private TableQueryConfig tableQueryConfig;

	/**
	 * 扩展配置
	 * <来源标识,<k,v>>
	 */
	Map<String, Map<String, Object>> extensionConfig;

	public Optional<Map<String, String>> getBaseConfig() {
		return Optional.ofNullable(baseConfig);
	}

	public Optional<TableQueryConfig> getTableQueryConfig() {
		return Optional.ofNullable(tableQueryConfig);
	}

	public Optional<Map<String, Map<String, Object>>> getExtensionConfig() {
		return Optional.ofNullable(extensionConfig);
	}

	/**
	 * 表查询配置
	 */
	@Setter
	public static class TableQueryConfig {
		List<String> commonTableQueryConfig;
		Map<String, List<String>> specialTableQueryConfig;

		public Optional<List<String>> getCommonTableQueryConfig() {
			return Optional.ofNullable(commonTableQueryConfig);
		}

		public Optional<Map<String, List<String>>> getSpecialTableQueryConfig() {
			return Optional.ofNullable(specialTableQueryConfig);
		}
	}
}