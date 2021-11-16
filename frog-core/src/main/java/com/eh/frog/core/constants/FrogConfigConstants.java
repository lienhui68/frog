/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.core.constants;

/**
 * 统一维护配置项，避免魔数
 *
 * @author f90fd4n david
 * @version 1.0.0: FrogConfigConstants.java, v 0.1 2021-11-02 5:30 下午 david Exp $$
 */
public final class FrogConfigConstants {
	private FrogConfigConstants() {
	}

	//==================基础配置KEY===============//
	/**
	 * 项目里使用的数据源id，目前仅支持单数据源
	 */
	public static final String DATASOURCE_BEAN_NAME = "datasourceBeanName";
	/**
	 * 消息拦截坐标，如果是多个则使用 ||, e1 || e2
	 */
	public static final String MESSAGE_EVENT_POI = "messageEventPoi";
	/**
	 * 测试数据文件夹相对路径
	 */
	public static final String TEST_DATA_FOLDER = "testDataFolder";
	/**
	 * 是否开启预跑反填，支持db/msg/rpc call反填，db目前插入不支持批量插入，等遇到批量插入场景再考虑
	 */
	public static final String PREPARE_RUN_BACK_FILL = "prepareRunBackFill";
	/**
	 * 预跑反填是否根据flag过滤(应付宽表、大POJO)
	 */
	public static final String PREPARE_FILL_FLAG_FILTER = "prepareFillFlagFilter";
	/**
	 * 筛选用例执行
	 */
	public static final String TEST_ONLY = "testOnly";
	/**
	 * 是否执行后置清理
	 */
	public static final String POST_PROCESS_CLEAN = "postProcessClean";


	//==================默认值===============//
	/**
	 * 测试数据文件夹相对路径默认值
	 */
	public static final String DEFAULT_TEST_DATA_FOLDER = "data";
	/**
	 * 表查询字段 虚拟设置,用来映射通用表查询字段
	 */
	public static final String FROG_VIRTUAL_COMMON_TABLE = "frog_virtual_common";
	/**
	 * 预跑反填临时文件默认路径
	 */
	public static final String DEFAULT_PREPARE_FILL_TMP_FILE = "data/frogPrepareFillData.yaml";

	//==================环境配置===============//
	/**
	 * 消息连接点环境变量KEY
	 */
	public static final String MESSAGE_EVENT_POINTCUT_KEY = "pointcut";


}