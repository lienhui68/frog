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
public @interface TestMethod {
	/**
	 * 执行选中的测试用例，eg:001,002
	 *
	 * @return
	 */
	String[] selected() default {};

	/**
	 * 不执行部分测试用例，在selected为空时生效
	 *
	 * @return
	 */
	String[] ignored() default {};

	/**
	 * 用于重载方法时表示原方法名字
	 * 比如一个接口有两个create方法，则test方法名与原方法名不同，需要使用target记录原方法名
	 *
	 * @return
	 */
	String target() default "";

	/**
	 * 测试数据文件名，一般使用默认即可
	 *
	 * @return
	 */
	String fileName() default "";
}