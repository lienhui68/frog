/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.core.annotation;

import org.apiguardian.api.API;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.lang.annotation.*;

/**
 * @author f90fd4n david
 * @version 1.0.0: FrogTest.java, v 0.1 2021-11-02 3:30 下午 david Exp $$
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@API(
		status = API.Status.STABLE,
		since = "5.0"
)
@ArgumentsSource(FrogYmlArgumentsProvider.class)
@ParameterizedTest(name = "[{index}] {0} / {1}")
public @interface FrogTest {
	String[] value() default {};
}