/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.core.model;

import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author f90fd4n david
 * @version 1.0.0: VirtualTable.java, v 0.1 2021-09-15 2:54 下午 david Exp $$
 */
@Data
public class VirtualTable {
	// if DO, the class name of the DO
	private String dataObjClazz;

	// table name
	private String tableName;

	// description of table template
	private String tableBaseDesc;

	// table rowRecords
	private List<Map<String, Object>> tableData;

	/** flags */
	private Map<String /* fieldName*/, String /* Flag */> flags = new HashMap<>();

	/**
	 * 获取表字段对应的flag，兼容表字段名称大小写
	 * @param fieldName
	 * @return flag标识
	 */
	public String getFlagByFieldNameIgnoreCase(String fieldName) {
		if (this.flags == null || this.flags.isEmpty()) {
			return null;
		}

		String currentFlag = this.flags.get(fieldName); // 尝试原始字段名称
		if (currentFlag == null) {
			currentFlag = this.flags.get(fieldName.toLowerCase()); // 尝试原始字段小写
			if (currentFlag == null) {
				currentFlag = this.flags.get(fieldName.toUpperCase()); // 尝试原始字段大写
			}
		}

		return currentFlag;

	}
}