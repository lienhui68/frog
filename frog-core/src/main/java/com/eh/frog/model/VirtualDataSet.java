/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author f90fd4n david
 * @version 1.0.0: VirtualDataSet.java, v 0.1 2021-09-15 2:56 下午 david Exp $$
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VirtualDataSet {
	// List of database tables
	private List<VirtualTable> virtualTables = new ArrayList();
}