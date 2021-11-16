/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.sample;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * @author f90fd4n david
 * @version 1.0.0: NestedTestDemo.java, v 0.1 2021-11-16 5:05 下午 david Exp $$
 */
public class NestedTestDemo {

	@Test
	@DisplayName("Nested")
	void isInstantiatedWithNew() {
		System.out.println("最一层--内嵌单元测试");
	}

	@Nested
	@DisplayName("Nested2")
	class Nested2 {

		@BeforeEach
		void Nested2_init() {
			System.out.println("Nested2_init");
		}

		@Test
		void Nested2_test() {
			System.out.println("第二层-内嵌单元测试");
		}


		@Nested
		@DisplayName("Nested3")
		class Nested3 {

			@BeforeEach
			void Nested3_init() {
				System.out.println("Nested3_init");
			}

			@Test
			void Nested3_test() {
				System.out.println("第三层-内嵌单元测试");
			}
		}
	}
}