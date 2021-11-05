/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.core.annotation;

import com.eh.frog.core.context.FrogRuntimeContext;

import java.util.List;

/**
 * Extension method and implementation of Frog
 *
 * @author f90fd4n david
 * @version 1.0.0: IFrogMethod.java, v 0.1 2021-10-20 7:49 下午 david Exp $$
 */
public interface IFrogMethod {
	/**
	 * Invoke.
	 *
	 * @param frogRuntimeContext the frog runtime context
	 */
	void invoke(FrogRuntimeContext frogRuntimeContext);

	/**
	 * 执行
	 *
	 * @return
	 */
	List<String> getIncludeCaseIds();

	/**
	 * 排除执行
	 *
	 * @return
	 */
	List<String> getExcludeCaseIds();

	/**
	 * 执行顺序
	 *
	 * @return
	 */
	default int getOrder() {
		return 0;
	}
}