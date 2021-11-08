/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.core.sqlparser;

import java.util.ArrayList;
import java.util.List;

/**
 * @author f90fd4n david
 * @version 1.0.0: BaseSingleSqlParser.java, v 0.1 2021-11-05 2:13 下午 david Exp $$
 */
public abstract class BaseSqlParseStrategy {
	//原始Sql语句
	protected String originalSql;

	//Sql语句片段
	protected List<SqlParseSegment> segments;


	/**
	 * 构造函数，传入原始Sql语句，进行劈分。
	 *
	 * @param originalSql
	 */
	public BaseSqlParseStrategy(String originalSql) {
		this.originalSql = originalSql;
		segments = new ArrayList<>();
		initializeSegments();
	}

	/**
	 * 初始化segments，强制子类实现
	 */
	protected abstract void initializeSegments();


	/**
	 * 将originalSql劈分成一个个片段
	 *
	 * @return
	 */
	protected abstract SqlParseResult parse();

}