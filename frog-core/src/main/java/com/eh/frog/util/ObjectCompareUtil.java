/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.eh.frog.util;

import com.eh.frog.exception.FrogCheckException;
import com.eh.frog.exception.FrogTestException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author david
 */
@Slf4j
public class ObjectCompareUtil {

	private static final String[] comparableTypes = {"int", "float", "double",
			"long", "short", "byte", "boolean", "char", "java.lang.Integer", "java.lang.Float",
			"java.lang.Double", "java.lang.Long", "java.lang.Short", "java.lang.Byte",
			"java.lang.Boolean", "java.lang.Character", "java.lang.String", "java.math.BigDecimal",
			"java.util.Date"};

	public static void compareByFields(Object actual, Object expect) {

		if (actual == null) {
			if (expect != null) {
				throw new FrogCheckException("\n======>actual is null but expect is not null");
			}
		}
		if (expect == null) {
			if (actual != null) {
				throw new FrogCheckException("\n======>expect is null but actual is not null");
			}
		}

		if (actual.getClass().equals(Throwable.class)
				|| actual.getClass().equals(StackTraceElement[].class)) {

			log.info("\n" + "Matching, skip the current type check"
					+ actual.getClass().getName());
			return;
		}


		String objName = actual.getClass().getName();
		Class<?> objType = actual.getClass();


		if (isComparable(objType)) { // 常规类型
			compare(actual, expect);
		} else if (objType.isArray()) { // 数组
			Object[] targetArray = (Object[]) actual;
			Object[] expectArray = (Object[]) expect;
			if (targetArray.length != expectArray.length) {
				throw new FrogCheckException("\n======>The length of array is different:" + objName);
			}
			for (int i = 0; i < targetArray.length; i++) {
				try {
					compareByFields(targetArray[i], expectArray[i]);
				} catch (FrogCheckException e) {
					throw new FrogCheckException("\n======>数组元素比对失败 index:{}, 失败原因:{}", i, e.getMessage());
				} catch (Exception e) {
					throw new FrogTestException("element in array check failed,index=" + i, e);
				}
			}
		} else if (actual instanceof Map) { // map

			Map<Object, Object> actualMap = (Map) actual;
			Map<Object, Object> expectMap = (Map) expect;

			if (actualMap.size() != expectMap.size()) {
				throw new FrogCheckException("\n======>The size of hashMap is different:" + objName);
			}

			//Two-way check, support out of order
			//Validate the actual value based on the expected Map
			for (Entry<Object, Object> entry : expectMap.entrySet()) {
				Object expectVal = entry.getValue();
				Object actualVal = actualMap.get(entry.getKey());
				try {
					compareByFields(actualVal, expectVal);
				} catch (FrogCheckException e) {
					throw new FrogCheckException("\n======>map元素比对失败 key:{}, 失败原因:{}", entry.getKey(), e.getMessage());
				} catch (Exception e) {
					throw new FrogTestException("element of hashmap check failed, key="
							+ entry.getKey(), e);
				}
			}

			//Verification of the expected value based on the actual map
			for (Entry<Object, Object> entry : actualMap.entrySet()) {
				Object targetVal = entry.getValue();
				Object expectVal = expectMap.get(entry.getKey());
				try {
					compareByFields(targetVal, expectVal);
				} catch (FrogCheckException e) {
					throw new FrogCheckException("\n======>map元素比对失败 key:{}, 失败原因:{}", entry.getKey(), e.getMessage());
				} catch (Exception e) {
					throw new FrogTestException("element in hashmap check failed, key="
							+ entry.getKey());
				}
			}

		} else if (actual instanceof List) {  // list是有序的，逐个比较
			List targetList = (List) actual;
			List expectList = (List) expect;
			if (targetList.size() != expectList.size()) {
				throw new FrogCheckException("\n======>The length of the list is different: " + objName);
			}
			for (int i = 0; i < targetList.size(); i++) {
				try {
					compareByFields(targetList.get(i), expectList.get(i));
				} catch (FrogCheckException e) {
					throw new FrogCheckException("\n======>list元素比对失败 index:{}, 失败原因:{}", i, e.getMessage());
				} catch (Exception e) {
					throw new FrogTestException("element in list check failed, index=" + i);
				}
			}
		} else { // 对象比较
			List<Field> fields = new ArrayList<>();

			for (Class<?> c = objType; c != null; c = c.getSuperclass()) {
				for (Field field : c.getDeclaredFields()) {
					int modifiers = field.getModifiers();
					if (!Modifier.isStatic(modifiers) && !Modifier.isTransient(modifiers)
							&& !fields.contains(field)) {
						fields.add(field);
					}
				}
			}
			for (Field field : fields) {
				String fieldName = field.getName();

				//if class type is Throwable/StackTraceElement[],pass
				if (StringUtils.equals(fieldName, "suppressedExceptions")
						|| field.getType().equals(Throwable.class)) {
					log.info("\n" + "Matching, skip the current type check"
							+ actual.getClass().getName());
					continue;
				}

				if (StringUtils.equals(fieldName, "stackTrace")
						&& field.getType().equals(StackTraceElement[].class)) {
					log.info("\n"
							+ "Matching, skip the current type check, type of object:"
							+ actual.getClass().getName() + ",field:" + field.getName());
					continue;
				}

				field.setAccessible(true);
				Object objTarget = null;
				Object objExpect = null;

				try {
					objTarget = field.get(actual);
					objExpect = field.get(expect);
					compareByFields(objTarget, objExpect);
				} catch (FrogCheckException e) {
					throw new FrogCheckException("\n======>对象属性比对失败 field:{}, 失败原因:{}", fieldName, e.getMessage());
				} catch (Exception e) {
					throw new FrogTestException(fieldName + "object check failed,expected value"
							+ ObjectUtil.toJson(objExpect) + "actual value"
							+ ObjectUtil.toJson(objTarget));
				}

			}
		}
	}

	public static boolean isComparable(Class<?> objType) {
		for (String comparableType : comparableTypes) {
			if (comparableType.equals(objType.getName())) {
				return true;
			}
		}
		return false;
	}

	public static void compare(Object actual, Object expect) {
		if (actual.getClass().isPrimitive()) {
			if (!(actual == expect)) {
				throw new FrogCheckException("\n======>基础类型数据比较结果不一致,acutal:{},expect:{}", actual, expect);
			}
		} else if (StringUtils.equals(actual.getClass().getName(), "java.math.BigDecimal")) {
			BigDecimal bitActual = (BigDecimal) actual;
			BigDecimal bitExpect = (BigDecimal) expect;
			if (0 != bitActual.compareTo(bitExpect)) {
				throw new FrogCheckException("\n======>BigDecimal类型数据比较结果不一致,acutal:{},expect:{}", actual, expect);
			}
		} else if (actual.getClass() == String.class) {
			if (!actual.equals(expect)) {
				throw new FrogCheckException("\n======>String类型数据比较结果不一致,acutal:{},expect:{}", actual, expect);
			}
		} else {
			if (!actual.equals(expect)) {
				throw new FrogCheckException("\n======>其他基础数据类型比较结果不一致,acutal:{},expect:{}", actual, expect);
			}
		}
	}

}