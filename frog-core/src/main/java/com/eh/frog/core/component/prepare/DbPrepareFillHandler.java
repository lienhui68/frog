/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.core.component.prepare;

import com.eh.frog.core.component.db.DBDataProcessor;
import com.eh.frog.core.config.GlobalConfigurationHolder;
import com.eh.frog.core.context.FrogRuntimeContextHolder;
import com.eh.frog.core.model.PrepareData;
import com.eh.frog.core.model.VirtualTable;
import com.eh.frog.core.sqlparser.SqlParseResult;
import com.google.common.collect.Maps;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Assertions;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author f90fd4n david
 * @version 1.0.0: PrepareDb4UpdateStrategy.java, v 0.1 2021-11-05 7:17 下午 david Exp $$
 */
public class DbPrepareFillHandler {
	public static void fillInsertData(SqlParseResult sqlParseResult, List<Object> params) {
		Assertions.assertNotNull(sqlParseResult);
		Assertions.assertNotNull(sqlParseResult.getTableName());
		Assertions.assertNotNull(sqlParseResult.getInsertColumnValueMap());
		filterParseMap(sqlParseResult.getInsertColumnValueMap());
		//
		PrepareData prepareData = PrepareFillDataHolder.getPrepareData();

		List<VirtualTable> expectDataSet = prepareData.getExpectDataSet();
		if (CollectionUtils.isEmpty(expectDataSet)) {
			expectDataSet = Lists.newArrayList();
			prepareData.setExpectDataSet(expectDataSet);
		}
		// 表示拦截到的表在现有list中已经存在
		boolean occursFlag = false;
		for (VirtualTable virtualTable : expectDataSet) {
			if (virtualTable.getTableName().equalsIgnoreCase(sqlParseResult.getTableName())) {
				virtualTable.getTableData().add(convertInsertDbDataByFlags(virtualTable.getTableName(), sqlParseResult.getInsertColumnValueMap(), params));
				occursFlag = true;
			}
		}
		if (!occursFlag) {
			VirtualTable virtualTable = new VirtualTable();
			virtualTable.setTableName(sqlParseResult.getTableName());
			virtualTable.setTableData(Lists.newArrayList(convertInsertDbDataByFlags(virtualTable.getTableName(), sqlParseResult.getInsertColumnValueMap(), params)));
			prepareData.getExpectDataSet().add((virtualTable));
		}

	}

	/**
	 * @param strData
	 * @param params
	 * @return
	 */
	private static Map<String, Object> convertInsertDbDataByFlags(String tableName, LinkedHashMap<String, String> strData, List<Object> params) {
		Map<String, String> flags = null;
		if (GlobalConfigurationHolder.getFrogConfig().isPrepareFillFlagFilter()) {
			List<VirtualTable> depDataSet = FrogRuntimeContextHolder.getContext().getPrepareData().getExpectDataSet();
			if (!CollectionUtils.isEmpty(depDataSet)) {
				// 判断是否添加该表
				Optional<VirtualTable> first = depDataSet.stream().filter(t -> tableName.equals(t.getTableName())).findFirst();
				if (first.isPresent()) {
					flags = first.get().getFlags();
				}
			}
		}

		Map<String, Object> data = new LinkedHashMap<>();
		int i = 0;
		for (Map.Entry<String, String> entry : strData.entrySet()) {
			String key = entry.getKey();
			// 过滤
			if (!CollectionUtils.isEmpty(flags)) {
				String flag = flags.get(key);
				if (Objects.isNull(flag) || "N".equalsIgnoreCase(flag)) {
					continue;
				}
			}
			String val = entry.getValue();
			if (val.equalsIgnoreCase("today")) {
				data.put(key, val);
			} else {
				Object obj = params.get(i);
				if (obj instanceof Date) {
					obj = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(obj);
				}
				data.put(key, obj);
				i++;
			}

		}
		return data;
	}

	private static void filterParseMap(Map<String, String> columnMap) {
		columnMap.forEach((k, v) -> {
			if (Objects.equals("now()", v.trim())) {
				columnMap.put(k, "today");
			}
		});
	}


	/**
	 * 填充更新后的数据，不支持对一张表多次update db，好的做法是加事务，在内存中更新，不是多次更新同一张表
	 *
	 * @param sqlParseResult
	 * @param dbDataProcessor
	 */
	public static void fillUpdateData(SqlParseResult sqlParseResult, DBDataProcessor dbDataProcessor) {
		Assertions.assertNotNull(sqlParseResult);
		Assertions.assertNotNull(sqlParseResult.getTableName());
		Assertions.assertNotNull(sqlParseResult.getUpdateConditionColumnValueMap());

		PrepareData prepareData = PrepareFillDataHolder.getPrepareData();

		List<VirtualTable> expectDataSet = prepareData.getExpectDataSet();
		if (CollectionUtils.isEmpty(expectDataSet)) {
			expectDataSet = Lists.newArrayList();
			prepareData.setExpectDataSet(expectDataSet);
		}
		// 表示拦截到的表在现有list中已经存在
		boolean occursFlag = false;
		for (VirtualTable virtualTable : expectDataSet) {
			if (virtualTable.getTableName().equalsIgnoreCase(sqlParseResult.getTableName())) {
				// 分组过滤
				virtualTable.getTableData().addAll(convertUpdateDbDataByFlags(sqlParseResult.getTableName(), sqlParseResult.getUpdateConditionColumnValueMap(), dbDataProcessor));
				occursFlag = true;
			}
		}
		if (!occursFlag) {
			VirtualTable virtualTable = new VirtualTable();
			virtualTable.setTableName(sqlParseResult.getTableName());
			virtualTable.setTableData(convertUpdateDbDataByFlags(sqlParseResult.getTableName(), sqlParseResult.getUpdateConditionColumnValueMap(), dbDataProcessor));
			prepareData.getExpectDataSet().add((virtualTable));
		}

	}

	/**
	 * 查询更新后的数据
	 * 这儿开始想复杂了， where条件语句可以直接复用update后的where语句
	 *
	 * @param tableName
	 * @param whereCondition
	 * @param dbDataProcessor
	 * @return
	 */
	private static List<Map<String, Object>> convertUpdateDbDataByFlags(String tableName, LinkedHashMap<String, String> whereCondition, DBDataProcessor dbDataProcessor) {
		StringBuilder wherePartBuilder = new StringBuilder();
		for (Map.Entry<String, String> entry : whereCondition.entrySet()) {
			String key = entry.getKey();
			String val = entry.getValue();
			if (val.equalsIgnoreCase("null")) {
				wherePartBuilder.append("(" + key + " is null " + " ) and ");
			} else {
				wherePartBuilder.append("(" + key + " = " + val + " ) and ");
			}
		}
		String wherePart = wherePartBuilder.substring(0, wherePartBuilder.length() - 4);
		String selectSql = "select * from " + tableName + " where " + wherePart;
		List<Map<String, Object>> maps = dbDataProcessor.queryForList(selectSql);
		// 过滤
		Map<String, String> flags = null;
		if (GlobalConfigurationHolder.getFrogConfig().isPrepareFillFlagFilter()) {
			List<VirtualTable> depDataSet = FrogRuntimeContextHolder.getContext().getPrepareData().getExpectDataSet();
			if (!CollectionUtils.isEmpty(depDataSet)) {
				// 判断是否添加该表
				Optional<VirtualTable> first = depDataSet.stream().filter(t -> tableName.equals(t.getTableName())).findFirst();
				if (first.isPresent()) {
					flags = first.get().getFlags();
				}
			}
		}


		Map<String, String> finalFlags = flags;
		List<Map<String, Object>> result = Lists.newArrayList();
		maps.forEach(map -> {
			Map<String, Object> m = Maps.newHashMap();
			map.forEach((k, v) -> {
				// 过滤
				if (!CollectionUtils.isEmpty(finalFlags)) {
					String flag = finalFlags.get(k);
					if (Objects.isNull(flag) || "N".equalsIgnoreCase(flag)) {
						return;
					}
				}
				if (v instanceof Date) {
					v = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(v);
				}
				m.put(k, v);
			});
			result.add(m);
		});
		return result;
	}


}