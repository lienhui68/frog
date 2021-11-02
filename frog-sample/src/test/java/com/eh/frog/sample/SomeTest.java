/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.sample;

import com.eh.frog.core.util.FrogFileUtil;
import com.eh.frog.sample.lion.OrderLionConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author f90fd4n david
 * @version 1.0.0: SomeTest.java, v 0.1 2021-09-28 11:25 上午 david Exp $$
 */
@Slf4j
public class SomeTest {
	/**
	 * 读取selectKeys
	 */
	@Test
	public void test1() {
		LinkedHashMap<String, List<String>> tableSelectKeys = FrogFileUtil.getTableSelectKeys("");
		System.out.println(tableSelectKeys);
	}

	@Test
	public void test() throws ClassNotFoundException {
		System.out.println("   aaaa  ".trim());
	}


	/**
	 * http://stackoverflow.com/questions/3301635/change-private-static-final-field-using-java-reflection
	 * https://bbs.csdn.net/topics/390051904
	 *
	 * @throws NoSuchFieldException
	 * @throws IllegalAccessException
	 */
	@Test
	public void test3() throws NoSuchFieldException, IllegalAccessException {
		System.out.println(OrderLionConfig.getBizSwitch());
		Field f = OrderLionConfig.class.getDeclaredField("bizSwitch");
		f.setAccessible(true);
		Field modifiersField = Field.class.getDeclaredField("modifiers");

		modifiersField.setAccessible(true);
		modifiersField.setInt(f, f.getModifiers() & ~Modifier.FINAL);
		f.set(null, false);
		System.out.println(OrderLionConfig.getBizSwitch());
	}

	@Test
	public void test4() {
		String url2 = this.getClass().getResource("/").toString();
		System.out.println(System.getProperty("user.dir")
		);
	}

}