/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.component.mock;

import com.eh.frog.exception.FrogTestException;
import org.assertj.core.util.Lists;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @author f90fd4n david
 * @version 1.0.0: MockContextHolder.java, v 0.1 2021-10-04 1:06 下午 david Exp $$
 */
public class MockContextHolder {

	private static ThreadLocal<List<MockRestorePojo>> restorePojoLocal = new ThreadLocal<>();

	public static void setMockRestorePojo(MockRestorePojo pojo) {
		if (restorePojoLocal.get() == null) {
			restorePojoLocal.set(Lists.newArrayList());
		}
		if (pojo != null) {
			restorePojoLocal.get().add(pojo);
		}
	}

	public static void restore() {
		restorePojoLocal.get().forEach(pojo -> {
			try {
				Field f = pojo.getContainer().getClass().getDeclaredField(pojo.getFieldName());
				f.setAccessible(true);
				f.set(pojo.getContainer(), pojo.getTarget());
			} catch (NoSuchFieldException | IllegalAccessException e) {
				throw new FrogTestException("mock数据恢复现场出错", e);
			}
		});
	}

	public static ThreadLocal<List<MockRestorePojo>> getRestorePojoLocal() {
		return restorePojoLocal;
	}
}