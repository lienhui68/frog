/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.sample;

import com.eh.frog.core.annotation.AfterPrepare;
import com.eh.frog.core.annotation.FrogTest;
import com.eh.frog.core.component.config.FrogComponentAutoConfiguration;
import com.eh.frog.core.context.FrogRuntimeContext;
import com.eh.frog.core.model.PrepareData;
import com.eh.frog.sample.base.SampleTestBase;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * @author f90fd4n david
 * @version 1.0.0: dao.java, v 0.1 2021-09-14 9:11 下午 david Exp $$
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {FrogSampleApplication.class, FrogComponentAutoConfiguration.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OrderControllerTest extends SampleTestBase {

	@FrogTest
	public void create(String caseId, String desc, PrepareData prepareData) {
		runTest(caseId, prepareData);
	}

	@FrogTest("OrderController_pay.yaml")
	public void pay(String caseId, String desc, PrepareData prepareData) {
		runTest(caseId, prepareData);
	}

	@AfterPrepare
	public void prepare11(FrogRuntimeContext frogRuntimeContext) {
		System.out.println("prepare11 order:0");
	}

	@AfterPrepare(includes = {"001"}, order = 1)
	public void prepare12(FrogRuntimeContext frogRuntimeContext) {
		System.out.println("includes = {\"001\"}, order = 1");
	}

	@AfterPrepare(includes = {"001", "002"}, order = 2)
	public void prepare13(FrogRuntimeContext frogRuntimeContext) {
		System.out.println("includes = {\"001\", \"002\"}, order = 2");
	}


}
