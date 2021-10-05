/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.model;

import lombok.Data;

/**
 * @author f90fd4n david
 * @version 1.0.0: PrepareData.java, v 0.1 2021-09-15 2:27 下午 david Exp $$
 */
@Data
public class PrepareData {
	// description
	private String desc;
	// request parameters
	private VirtualArgs args;
	// response expectation
	private VirtualResult expectResult;
	// database preparation
	private VirtualDataSet depDataSet;
	// database expectation
	private VirtualDataSet expectDataSet;
	// message expectation
	private VirtualEventSet expectEventSet;
	// exception expectation
	private VirtualException expectException;
	// context parameters
	private VirtualParams virtualParams;
	// mock
	private VirtualMockSet virtualMockSet;
	// config
	private VirtualConfigSet virtualConfigSet;
}