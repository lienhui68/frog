/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.sample;

import com.eh.frog.core.annotation.FrogTest;
import com.eh.frog.core.component.config.FrogComponentConfiguration;
import com.eh.frog.core.model.PrepareData;
import com.eh.frog.core.template.FrogTestBase;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * @author f90fd4n david
 * @version 1.0.0: dao.java, v 0.1 2021-09-14 9:11 下午 david Exp $$
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {FrogSampleApplication.class, FrogComponentConfiguration.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OrderControllerTest extends FrogTestBase {

	@FrogTest
	public void create(String caseId, String desc, PrepareData prepareData) {
		runTest(caseId, prepareData);
	}

}
