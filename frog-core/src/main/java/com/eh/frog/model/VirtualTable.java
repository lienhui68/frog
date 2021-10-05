/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.model;

import lombok.Data;

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

	private Map<String, String> dynamicData;
}