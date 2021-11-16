/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.core.model;

/**
 * 设计原则，对象比较：flag非空以flag为主，flag为空，则如果期望对象有值设置成Y，为空则设置成N
 * 数据库表比较：由于数据库比较不存在复杂对象，所以可以偷懒，从expect compare to actual,如果expect中没有字段（key不存在）默认无需比较，减少工作量，当然全字段比较才是正确方式
 *
 * @author f90fd4n david
 * @version 1.0.0: CheckFlag.java, v 0.1 2021-11-15 7:00 下午 david Exp $$
 */
public final class CheckFlag {
	private CheckFlag() {
	}

	/**
	 * 时间偏移值比较，如 D200
	 */
	public static final String DATE_FLAG = "D";

	/**
	 * 正则表达式校验，字段类型必须是string
	 */
	public static final String REGEX_FLAG = "R";

	/**
	 * 数值校验，字段类型必须是number，如L100,表示字段值必须大于100
	 */
	public static final String DYNAMIC_NUMBER_FLAG = "L";

	/**
	 * 校验，当字段值非空时默认校验
	 */
	public static final String CHECK_FLAG = "Y";

	/**
	 * 不校验，当字段值为空时默认不校验
	 */
	public static final String NOT_CHECK_FLAG = "N";


}