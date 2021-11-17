/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.core.model;

import lombok.Data;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author f90fd4n david
 * @version 1.0.0: VirtualInvocationGroup.java, v 0.1 2021-11-17 4:06 下午 david Exp $$
 */
@Data
public class VirtualInvocationGroup {
	//描述
	private String desc;
	// mock的Bean所在bean的class全路径
	private String container;
	// 如果所在bean的类型对应多个实例时需要设置在容器中的beanName
	private String containerBeanName;
	// 如果mock的bean属性名称不是首字母小写需要显示声明
	private String fieldName;
	// target bean
	private String target;
	/**
	 * flag,<class, <field name, flag value>>
	 */
	public Map<String, Map<String, String>> flags = new LinkedHashMap<>();
	// 期望调用入参内容list，外层list表示可能存在多次调用，内层List表示入参可能有多个
	private List<List<Object>> objects;
}