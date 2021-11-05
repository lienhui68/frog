/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.core.annotation;

import java.lang.annotation.*;

/**
 * @author f90fd4n david
 * @version 1.0.0: EnablePlugin.java, v 0.1 2021-11-03 5:35 下午 david Exp $$
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface EnablePlugin {
	/**
	 * Returns method signatures to intercept.
	 *
	 * @return method signatures
	 */
	PluginSignature[] value();
}