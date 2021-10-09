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

}