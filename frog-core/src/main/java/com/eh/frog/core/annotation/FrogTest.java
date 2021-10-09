/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author f90fd4n david
 * @version 1.0.0: TestPath.java, v 0.1 2021-09-15 12:39 上午 david Exp $$
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FrogTest {
	String[] selected() default {};

	String target() default "";

	String fileName() default "";
}