/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.core.model;

import com.eh.frog.core.model.ext.MockUnit;
import lombok.Data;

import java.util.List;

/**
 * @author f90fd4n david
 * @version 1.0.0: VirtualMockObject.java, v 0.1 2021-09-30 5:22 下午 david Exp $$
 */
@Data
public class VirtualMockObject {
	// 描述
	private String desc;
	// mock的Bean所在bean的class全路径
	private String container;
	// 如果所在bean的类型对应多个实例时需要设置在容器中的beanName
	private String containerBeanName;
	// 如果mock的bean属性名称不是首字母小写需要显示声明
	private String fieldName;
	// target bean
	private String target;
	// mock obj, any() -> returnObj
	private Object defaultObj;
	// mock obj, when(xxx).thenReturn(obj)，使用Spel表达式，变量使用args
	private List<MockUnit> mockUnits;
}