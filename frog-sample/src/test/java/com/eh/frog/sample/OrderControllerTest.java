/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.sample;

import com.eh.frog.core.annotation.*;
import com.eh.frog.core.component.config.FrogComponentConfiguration;
import com.eh.frog.core.context.FrogRuntimeContext;
import com.eh.frog.core.template.FrogTestBase;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author f90fd4n david
 * @version 1.0.0: dao.java, v 0.1 2021-09-14 9:11 下午 david Exp $$
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {FrogSampleApplication.class, FrogComponentConfiguration.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestBean
public class OrderControllerTest extends FrogTestBase {

	@TestMethod(selected = {"001", "002"})
	public void create() {
	}

	@Override
	public void beforeFrogTest(FrogRuntimeContext frogRuntimeContext) {
		System.out.println("process前执行!!");
	}

	@Override
	public void afterFrogTest(FrogRuntimeContext frogRuntimeContext) {
		System.out.println("process后执行!!");
	}

	@BeforeClean
	public void beforeClean(FrogRuntimeContext frogRuntimeContext) {
		System.out.println("数据清理前执行!!");
	}

	@AfterClean
	public void afterClean(FrogRuntimeContext frogRuntimeContext) {
		System.out.println("数据清理后执行!!");
	}

	@BeforePrepare
	public void beforePrepare(FrogRuntimeContext frogRuntimeContext) {
		System.out.println("数据准备前执行!!");
	}

	@AfterPrepare
	public void afterPrepare(FrogRuntimeContext frogRuntimeContext) {
		System.out.println("数据准备后执行!!");
	}

	@BeforeCheck
	public void beforeCheck(FrogRuntimeContext frogRuntimeContext) {
		System.out.println("数据check前执行!!");
	}

	@AfterCheck
	public void afterCheck(FrogRuntimeContext frogRuntimeContext) {
		System.out.println("数据check之后执行!!");
	}
}
