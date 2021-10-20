/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.core.model;

import lombok.Data;

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
	// 当容器中mock的bean类型对应多个实例时需要设置容器中beanName
	private String targetBeanName;
	// mock obj
	private Object object;
}