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
	// exception expectation
	private VirtualObject expectException;
	// context parameters
	private Map<String, VirtualObject> virtualParams;
	// mock
	private List<VirtualMockObject> virtualMockSet;
	// config center
	private List<VirtualConfigObject> virtualConfigSet;

	/**
	 * Gets params.
	 *
	 * @return the params
	 */
	public Map<String, VirtualObject> getParams() {
		return virtualParams;
	}

	/**
	 * Sets params.
	 *
	 * @param params the params
	 */
	public void setParams(Map<String, VirtualObject> params) {
		this.virtualParams = params;
	}

	/**
	 * Add param.
	 *
	 * @param key the key
	 * @param obj the obj
	 */
	public void addParam(String key, Object obj) {
		virtualParams.put(key, new VirtualObject(obj));
	}

	/**
	 * Gets by para name.
	 *
	 * @param paraName the para name
	 * @return the by para name
	 */
	public Object getByParaName(final String paraName) {
		if (virtualParams == null || null == paraName) {
			return null;
		}
		return virtualParams.get(paraName);
	}
}