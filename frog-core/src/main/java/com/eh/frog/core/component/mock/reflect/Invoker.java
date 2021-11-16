/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.core.component.mock.reflect;

import com.eh.frog.core.model.ext.MockUnit;
import org.junit.jupiter.api.Assertions;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

/**
 * @author f90fd4n david
 * @version 1.0.0: Invoker.java, v 0.1 2021-11-11 3:56 下午 david Exp $$
 */
public class Invoker {
	public static Object getInstance(Method method, Object obj, List<MockUnit> mockUnits) {
		Assertions.assertNotNull(method);
		MockMethodProxy invocationHandler = new MockMethodProxy(method, obj, mockUnits);
		Object newProxyInstance = Proxy.newProxyInstance(
				method.getDeclaringClass().getClassLoader(),
				new Class[]{method.getDeclaringClass()},
				invocationHandler);
		return newProxyInstance;
	}
}