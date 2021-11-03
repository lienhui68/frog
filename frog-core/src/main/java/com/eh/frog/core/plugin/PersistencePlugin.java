/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.core.plugin;

import com.eh.frog.core.context.FrogRuntimeContext;

import java.util.Map;
import java.util.Optional;

/**
 * @author f90fd4n david
 * @version 1.0.0: PersistencePlugin.java, v 0.1 2021-11-03 11:08 上午 david Exp $$
 */
public interface PersistencePlugin {

	/**
	 * 初始化插件
	 *
	 * @param pluginConfig
	 */
	default void init(Map<String, Object> pluginConfig) {
	}

	/**
	 * 插件标识，用于提取配置文件中该插件的配置信息
	 *
	 * @return
	 */
	String getPluginSymbol();

	/**
	 * 数据准备
	 *
	 * @param pluginParam
	 */
	void prepare(Optional<Object> pluginParam) throws Exception;

	/**
	 * 数据check
	 *
	 * @param pluginParam
	 */
	void check(Optional<Object> pluginParam) throws Exception;

	/**
	 * 数据清理
	 *
	 * @param pluginParam
	 */
	void clean(Optional<Object> pluginParam) throws Exception;
}