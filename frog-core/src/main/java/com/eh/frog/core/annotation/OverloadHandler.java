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
 * @version 1.0.0: TestMethod.java, v 0.1 2021-10-11 9:17 上午 david Exp $$
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OverloadHandler {

	/**
	 * 用于重载方法时表示原方法名字
	 * 比如一个接口有两个create方法，则test方法名与原方法名不同，需要使用target记录原方法名
	 *
	 * @return
	 */
	String target() default "";

	/**
	 * 重载方法参数
	 *
	 * @return
	 */
	String params() default "";
}