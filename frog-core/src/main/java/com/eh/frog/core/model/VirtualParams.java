/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.core.model;

import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author f90fd4n david
 * @version 1.0.0: VirtualParams.java, v 0.1 2021-09-15 3:04 下午 david Exp $$
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VirtualParams {
	// custom parameter
	public Map<String, VirtualObject> params = Maps.newHashMap();
}