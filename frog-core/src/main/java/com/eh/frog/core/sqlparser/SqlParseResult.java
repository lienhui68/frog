/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.core.sqlparser;

import com.eh.frog.core.enums.PrepareFillDbType;
import lombok.Data;
import lombok.ToString;

import java.util.LinkedHashMap;

/**
 * @author f90fd4n david
 * @version 1.0.0: SqlParseResult.java, v 0.1 2021-11-05 4:15 下午 david Exp $$
 */
@ToString
@Data
public class SqlParseResult {
	private String tableName;
	private PrepareFillDbType prepareFillDbType;
	/**
	 * insert 预跑反填使用原始语句中的提取值，因为如果使用select的话存在幻读
	 */
	private LinkedHashMap<String, String> insertColumnValueMap;

	/**
	 * update set list
	 */
	private LinkedHashMap<String, String> updateColumnValueMap;

	/**
	 * update where list
	 */
	private LinkedHashMap<String, String> updateConditionColumnValueMap;
}