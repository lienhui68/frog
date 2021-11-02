/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.lang.reflect.Method;

/**
 * @author f90fd4n david
 * @version 1.0.0: FrogExtensionContext.java, v 0.1 2021-11-02 2:02 下午 david Exp $$
 */
@AllArgsConstructor
@Data
public class FrogExtensionContext {
	Class testClass;
	Method testMethod;
}