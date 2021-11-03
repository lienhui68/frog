/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author f90fd4n david
 * @version 1.0.0: Signature.java, v 0.1 2021-11-03 5:36 下午 david Exp $$
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({})
public @interface PluginSignature {
	/**
	 * Returns the plugin interface type.
	 *
	 * @return the plugin interface type
	 */
	Class<?> type();

	/**
	 * Returns concrete plugin types for plugin interface.
	 *
	 * @return concrete plugin types for plugin interface
	 */
	Class<?>[] plugins();
}