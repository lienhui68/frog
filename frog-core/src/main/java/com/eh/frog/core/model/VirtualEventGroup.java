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
 *
 * @author f90fd4n david
 * @version 1.0.0: VirtualEventGroup.java, v 0.1 2021-10-09 9:42 上午 david Exp $$
 */
@Data
public class VirtualEventGroup {
	//描述
	private String desc;
	//消息体类路径
	private String msgClass;
	/** flag,<class, <field name, flag value>> */
	public Map<String, Map<String, String>> flags = new LinkedHashMap<>();
	//期望消息体内容list
	private List<Object> objects;
}