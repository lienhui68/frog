/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.core.sqlparser;

import com.eh.frog.core.enums.PrepareFillDbType;
import com.eh.frog.core.exception.FrogTestException;
import com.google.common.collect.Maps;
import org.junit.jupiter.api.Assertions;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * 单句插入语句解析器
 *
 * @author f90fd4n david
 * @version 1.0.0: InsertSqlParser.java, v 0.1 2021-11-05 2:17 下午 david Exp $$
 */
public class InsertSqlParseStrategy extends BaseSqlParseStrategy {

	public InsertSqlParseStrategy(String originalSql) {
		super(originalSql);

	}

	//insert into table_name (name,age,sex) values ("小明","28","女");
	@Override
	protected void initializeSegments() {
		segments.add(new SqlParseSegment("(insert into)(.+?)([(])", "[.]"));
		segments.add(new SqlParseSegment("([(])(.+?)([)]\\s*values\\s*[(])", "[,]"));
		segments.add(new SqlParseSegment("([)]\\s*values\\s*[(])(.+)([)]ENDOFSQL)", "[,]"));
	}

	/**
	 * 将originalSql劈分成一个个片段
	 *
	 * @return
	 */
	@Override
	public SqlParseResult parse() {
		// 解析
		for (SqlParseSegment sqlParseSegment : segments) {
			sqlParseSegment.parse(originalSql);
		}
		// 表名
		String tableName = segments.get(0).getBodyPieces().stream().reduce((f, s) -> s).orElseThrow(() -> new FrogTestException("no last element"));
		// 字段名与值映射
		List<String> cs = segments.get(1).getBodyPieces();
		List<String> vs = segments.get(2).getBodyPieces();
		Assertions.assertTrue(cs.size() == vs.size(), "insert sql， columns'size not eq values'size");
		LinkedHashMap<String, String> columnMap = Maps.newLinkedHashMap();
		for (int i = 0; i < cs.size(); i++) {
			columnMap.put(cs.get(i), vs.get(i));
		}
		// 组装返回数据
		SqlParseResult result = new SqlParseResult();
		result.setTableName(tableName);
		result.setPrepareFillDbType(PrepareFillDbType.INSERT);
		result.setInsertColumnValueMap(columnMap);
		return result;
	}

}