/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.core.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 *
 * @author f90fd4n david
 * @version 1.0.0: BeforeExecute.java, v 0.1 2021-11-03 10:02 上午 david Exp $$
 */
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target(java.lang.annotation.ElementType.METHOD)
@FrogHook
public @interface AfterExecute {
	String[] includes() default {};

	String[] excludes() default {};

	int order() default 0;
}