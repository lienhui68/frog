/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.core.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author f90fd4n david
 * @version 1.0.0: PrepareData.java, v 0.1 2021-09-15 2:27 下午 david Exp $$
 */
@Data
public class PrepareData {
	// description
	private String desc;
	// request parameters
	private List<VirtualObject> args;
	// response expectation
	private VirtualObject expectResult;
	// database preparation
	private List<VirtualTable> depDataSet;
	// database expectation
	private List<VirtualTable> expectDataSet;
	// message expectation
	private List<VirtualEventGroup> expectEventSet;
	// invocation expectation
	private List<VirtualInvocationGroup> expectInvocationSet;
	// exception expectation
	private VirtualObject expectException;
	// mock
	private List<VirtualMockObject> virtualMockSet;
	// config center
	private List<VirtualConfigObject> virtualConfigSet;
	// extend parameters
	private Map<String, Object> extendParams;

}