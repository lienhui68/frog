/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.core.annotation;

import java.lang.annotation.*;

/**
 * @author f90fd4n david
 * @version 1.0.0: FrogHook.java, v 0.1 2021-11-04 2:37 下午 david Exp $$
 */
@Target({ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FrogHook {
	String[] includeFilters() default {};

	String[] excludeFilters() default {};
}