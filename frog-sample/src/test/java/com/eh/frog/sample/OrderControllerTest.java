/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.sample;

import com.eh.frog.core.annotation.TestBean;
import com.eh.frog.core.annotation.TestMethod;
import com.eh.frog.core.component.config.FrogComponentConfiguration;
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
}
