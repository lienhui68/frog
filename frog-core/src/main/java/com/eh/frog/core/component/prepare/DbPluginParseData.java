/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.core.component.prepare;

import lombok.Data;

import java.util.List;

/**
 * @author f90fd4n david
 * @version 1.0.0: DbPluginParseData.java, v 0.1 2021-11-08 11:22 上午 david Exp $$
 */
@Data
public class DbPluginParseData {
	private String sql;
	// 问号填充
	private List<Object> params;
}