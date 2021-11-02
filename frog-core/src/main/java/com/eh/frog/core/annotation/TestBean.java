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
 * @version 1.0.0: TestBean.java, v 0.1 2021-10-11 9:16 上午 david Exp $$
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface TestBean {
	/**
	 * 测试Bean，如果不填则表示容器中的bean名称和xxxTest中的xxx一致
	 *
	 * @return
	 */
	String value() default "";

}