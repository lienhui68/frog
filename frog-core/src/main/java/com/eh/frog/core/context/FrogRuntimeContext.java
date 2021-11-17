/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.core.context;

import com.eh.frog.core.component.db.DBDataProcessor;
import com.eh.frog.core.model.PrepareData;
import lombok.Data;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author f90fd4n david
 * @version 1.0.0: FrogRuntimeContext.java, v 0.1 2021-09-16 3:19 下午 david Exp $$
 */
@Data
public class FrogRuntimeContext {
	/**
	 * caseId
	 */
	public String caseId;
	/**
	 * test data
	 */
	public PrepareData prepareData;
	/**
	 * data processor
	 */
	public DBDataProcessor dbDataProcessor;
	/**
	 * Tested method
	 */
	public Method testedMethod;
	/**
	 * Tested object
	 */
	public Object testedObj;
	/**
	 * Return result，after execute, it will be generated
	 */
	public Object resultObj;
	/**
	 * Expectations of exception results
	 */
	public Object exceptionObj;

	public ApplicationContext applicationContext;

	/** parameter list，can be specified by $ */
	public Map<String, Object> paramMap             = new LinkedHashMap<>();

	/**
	 * Constructor.
	 *
	 * @param caseId          the case id
	 * @param prepareData     the prepare data
	 * @param testedMethod    the tested method
	 * @param testedObj       the tested obj
	 * @param dbDataProcessor the db datas processor
	 */
	public FrogRuntimeContext(String caseId, PrepareData prepareData, Method testedMethod,
	                          Object testedObj, DBDataProcessor dbDataProcessor, ApplicationContext applicationContext) {
		super();
		this.caseId = caseId;
		this.prepareData = prepareData;
		this.dbDataProcessor = dbDataProcessor;
		this.testedMethod = testedMethod;
		this.testedObj = testedObj;
		this.applicationContext = applicationContext;
	}

	/**
	 * Add one param.
	 *
	 * @param paraName the para name
	 * @param paraObj the para obj
	 */
	public void addOneParam(String paraName, Object paraObj) {
		paramMap.put(paraName, paraObj);
	}

	/**
	 * Gets param by name.
	 *
	 * @param paraName the para name
	 * @return the param by name
	 */
	public Object getParamByName(String paraName) {
		if (null == paraName) {
			return null;
		}

		return paramMap.get(paraName);
	}

}