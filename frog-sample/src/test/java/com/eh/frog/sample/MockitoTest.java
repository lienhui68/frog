/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.sample;

import com.eh.frog.sample.controller.UserController;
import com.eh.frog.sample.orm.bean.User;
import com.eh.frog.sample.third.impl.ThirdServiceImpl;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.LinkedList;
import java.util.List;

import static org.mockito.Mockito.*;

/**
 * @author f90fd4n david
 * @version 1.0.0: MockitoTest.java, v 0.1 2021-09-18 9:52 上午 david Exp $$
 */
public class MockitoTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	@Mock
	private UserController mUserController;

	/**
	 * 检验调对象相关行为是否被调用
	 */
	@Test
	public void test1() {
		// Mock creation
		List mockedList = mock(List.class);

		// Use mock object - it does not throw any "unexpected interaction" exception
		mockedList.add("one"); //调用了add("one")行为
		mockedList.clear(); //调用了clear()行为

		// Selective, explicit, highly readable verification
		verify(mockedList).add("one"); // 检验add("one")是否已被调用
		verify(mockedList).clear(); // 检验clear()是否已被调用
	}

	/**
	 * 配置/方法行为
	 */
	@Test
	public void test2() {
		// you can mock concrete classes, not only interfaces
		LinkedList mockedList = mock(LinkedList.class);
		// stubbing appears before the actual execution
		when(mockedList.get(0)).thenReturn("first");
		// the following prints "first"
		System.out.println(mockedList.get(0));
		// the following prints "null" because get(999) was not stubbed
		System.out.println(mockedList.get(999));
	}

	/**
	 * 通过注解 @Mock 也模拟出一个实例：
	 */
	@Test
	public void test3() {

		UserController userController = mockUserController();
		System.out.println(userController.getUser(1));
		;

	}

	private UserController mockUserController() {
		when(mUserController.getUser(1)).thenReturn(new User(1, "宋江"));
		return mUserController;
	}

	/**
	 * 实例化虚拟对象
	 */
	@Test
	public void test4() {
		// You can mock concrete classes, not just interfaces
		LinkedList mockedList = mock(LinkedList.class);

		// Stubbing
		when(mockedList.get(0)).thenReturn("first");
		when(mockedList.get(1)).thenThrow(new RuntimeException());

		// Following prints "first"
		System.out.println(mockedList.get(0));
		// Following throws runtime exception
		System.out.println(mockedList.get(1));
		// Following prints "null" because get(999) was not stubbed
		System.out.println(mockedList.get(999));

		// Although it is possible to verify a stubbed invocation, usually it's just redundant
		// If your code cares what get(0) returns, then something else breaks (often even before verify() gets executed).
		// If your code doesn't care what get(0) returns, then it should not be stubbed. Not convinced? See here.
		verify(mockedList).get(0);
	}

	/**
	 * 参数匹配
	 */
	@Test
	public void test5() {
		LinkedList mockedList = mock(LinkedList.class);

		// Stubbing using built-in anyInt() argument matcher
		when(mockedList.get(anyInt())).thenReturn("element");
		// Following prints "element"
		System.out.println(mockedList.get(999));
		// You can also verify using an argument matcher
		verify(mockedList).get(anyInt());
	}

	@Test
	public void test6() {
		UserController userController = mock(UserController.class);
		when(userController.getUser(any())).thenReturn(new User(2, "武松"));
		System.out.println(userController.getUser(999));
	}

	/**
	 * 构造mock的函数抛出异常，当然我们可以在junit中设置expected以显示声明会抛出指定类型的异常，这样该条case执行的时候就会成功
	 */
	@Test(expected = RuntimeException.class)
	public void test7() {
		LinkedList mockedList = mock(LinkedList.class);
		when(mockedList.get(1)).thenThrow(new RuntimeException());
		mockedList.get(1);
	}

	@Test
	public void test8() {
		LinkedList mockedList = mock(LinkedList.class);
		when(mockedList.get(anyInt())).thenReturn(1).thenReturn(2);
		System.out.println(mockedList.get(1)); // 1
		System.out.println(mockedList.get(1)); // 2
		System.out.println(mockedList.get(1)); // 2
		System.out.println(mockedList.get(1)); // 2

	}



}