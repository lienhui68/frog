/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.core.annotation;

import org.springframework.context.annotation.Conditional;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * @author f90fd4n david
 * @version 1.0.0: FrogConditionalOnMissKey.java, v 0.1 2021-11-05 10:34 上午 david Exp $$
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Conditional(OnFrogProperty.class)
public @interface FrogConditionalOnProperty {
	@AliasFor("key")
	String value() default "";

	@AliasFor("value")
	String key() default "";

	String havingValue() default "";

	boolean matchIfMissing() default false;
}

