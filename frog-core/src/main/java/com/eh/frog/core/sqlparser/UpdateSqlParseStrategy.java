/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.core.sqlparser;

import com.eh.frog.core.enums.PrepareFillDbType;
import com.eh.frog.core.exception.FrogTestException;
import com.google.common.collect.Maps;
import org.springframework.util.CollectionUtils;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author f90fd4n david
 * @version 1.0.0: UpdateSqlParser.java, v 0.1 2021-11-05 2:15 下午 david Exp $$
 */
public class UpdateSqlParseStrategy extends BaseSqlParseStrategy {

	public UpdateSqlParseStrategy(String originalSql) {
		super(originalSql);

	}

	//update(table_name) set (key = value) where()；

	@Override
	protected void initializeSegments() {

		segments.add(new SqlParseSegment("(update)(.+)(set)", "[.]"));
		segments.add(new SqlParseSegment("(set)(.+?)( where.+ENDOFSQL)", "[,]"));
		segments.add(new SqlParseSegment("(where)(.+)(ENDOFSQL)", "(\\s+and\\s+|\\s+or\\s+)"));
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
		LinkedHashMap<String, String> updateColumnValueMap = Maps.newLinkedHashMap();
		List<String> setColumns = segments.get(1).getBodyPieces();
		setColumns.forEach(c -> {
			String[] ss = c.split("=");
			updateColumnValueMap.put(ss[0].trim(), ss[1].trim());
		});
		LinkedHashMap<String, String> updateConditionColumnValueMap = Maps.newLinkedHashMap();
		List<String> conditionColumns = segments.get(2).getBodyPieces();
		if (!CollectionUtils.isEmpty(conditionColumns)) {
			conditionColumns.forEach(c -> {
				String[] ss = c.split("=");
				updateConditionColumnValueMap.put(ss[0].trim(), ss[1].trim());
			});
		}
		// 组装返回数据
		SqlParseResult result = new SqlParseResult();
		result.setTableName(tableName);
		result.setPrepareFillDbType(PrepareFillDbType.UPDATE);
		result.setUpdateColumnValueMap(updateColumnValueMap);
		result.setUpdateConditionColumnValueMap(updateConditionColumnValueMap);
		return result;
	}

}