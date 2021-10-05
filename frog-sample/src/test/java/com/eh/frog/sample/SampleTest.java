/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.sample;

import com.eh.frog.annotation.FrogTest;
import com.eh.frog.component.event.MessageEventAdvisorConfig;
import com.eh.frog.model.PrepareData;
import com.eh.frog.template.FrogTestBase;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author f90fd4n david
 * @version 1.0.0: dao.java, v 0.1 2021-09-14 9:11 下午 david Exp $$
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {FrogSampleApplication.class, MessageEventAdvisorConfig.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SampleTest extends FrogTestBase {


	@Override
	@FrogTest(target = "com.eh.frog.sample.controller.UserController#createUser(com.eh.frog.sample.orm.bean.User)", selected = {"001", "002"})
	protected void test(String caseId, PrepareData prepareData) {
		runTest(caseId, prepareData);
	}
}
