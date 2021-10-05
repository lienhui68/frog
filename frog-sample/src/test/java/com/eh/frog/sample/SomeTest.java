/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.sample;

import com.eh.frog.model.PrepareData;
import com.eh.frog.model.VirtualMockObject;
import com.eh.frog.model.VirtualMockSet;
import com.eh.frog.sample.lion.OrderLionConfig;
import com.eh.frog.sample.orm.bean.User;
import com.eh.frog.util.FrogFileUtil;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

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
		LinkedHashMap<String, List<String>> tableSelectKeys = FrogFileUtil.getTableSelectKeys();
		System.out.println(tableSelectKeys);
	}

	@Test
	public void test() throws ClassNotFoundException {
		System.out.println("   aaaa  ".trim());
	}


	@Test
	public void test2() {
		PrepareData prepareData = new PrepareData();
		VirtualMockSet virtualMockSet = new VirtualMockSet();
		VirtualMockObject virtualMockObject = new VirtualMockObject();
		virtualMockObject.setTarget("com.eh.frog.sample.third.impl.ThirdServiceImpl.third(java.lang.Integer, java.lang.String)");
		User user = new User();
		user.setUserId(9999);
		user.setUserName("mock用户");
		virtualMockObject.setObject(user);
		virtualMockObject.setDesc("mock第三方服务");
		virtualMockSet.setMockList(Lists.newArrayList(virtualMockObject));
		prepareData.setVirtualMockSet(virtualMockSet);
		String yaml = new Yaml().dump(prepareData);
		log.info("\n" + yaml);
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
		System.out.println(OrderLionConfig.getIntentionClosedLoopSwitch());
		Field f = OrderLionConfig.class.getDeclaredField("intentionClosedLoopSwitch");
		f.setAccessible(true);
		Field modifiersField = Field.class.getDeclaredField("modifiers");

		modifiersField.setAccessible(true);
		modifiersField.setInt(f, f.getModifiers() & ~Modifier.FINAL);
		f.set(null, "222");
		System.out.println(OrderLionConfig.getIntentionClosedLoopSwitch());
	}
}