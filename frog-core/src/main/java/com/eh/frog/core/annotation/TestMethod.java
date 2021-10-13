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
 *
 * @author f90fd4n david
 * @version 1.0.0: TestMethod.java, v 0.1 2021-10-11 9:17 上午 david Exp $$
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TestMethod {
	String[] selected() default {};

	String[] ignored() default {};

	/**
	 * 用于重载方法时表示原方法名字
	 *
	 * @return
	 */
	String target() default "";

	String fileName() default "";
}