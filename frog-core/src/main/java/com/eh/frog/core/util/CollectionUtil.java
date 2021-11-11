/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.core.util;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.springframework.util.ReflectionUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

/**
 * @author f90fd4n david
 * @version 1.0.0: CollectionUtil.java, v 0.1 2021-11-10 9:08 下午 david Exp $$
 */
public class CollectionUtil {
	/**
	 * 过滤预跑反填结果
	 * 原则上来说应该在统一在序列化的时候选择是否展示，现阶段先使用Map模拟pojo的方式简单实现
	 *
	 * @param obj
	 * @param objFlags
	 * @return
	 */
	public static Map<String, Object> filterObjByFlags(Object obj, boolean filterFlag, Map<String, Map<String, String>> objFlags) {
		if (ObjectUtil.isBasicType(obj)) {
			return Maps.newHashMap(ImmutableMap.of(StringUtil.lowerFirstCase(obj.getClass().getSimpleName()), obj));
		}
		Map<String, Object> result = Maps.newHashMap();
		final Map<String, String> fieldFlags = objFlags.get(obj.getClass().getName());
		// 使用Map模拟Object对象返回
		ReflectionUtils.doWithFields(obj.getClass(), f -> {
			f.setAccessible(true);
			Object o = f.get(obj);
			if (ObjectUtil.isBasicType(o)) {
				if (o instanceof Date) {
					o = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(o);
				}
				result.put(f.getName(), o);
			} else {
				result.put(f.getName(), filterObjByFlags(o, filterFlag, objFlags));
			}
		}, f -> {
			if (!filterFlag) {
				return true;
			}
			String fn = f.getName();
			String flag = fieldFlags.get(fn);
			boolean ignore = Objects.isNull(flag) || "N".equalsIgnoreCase(flag);
			return !ignore;
		});
		return result;
	}
}