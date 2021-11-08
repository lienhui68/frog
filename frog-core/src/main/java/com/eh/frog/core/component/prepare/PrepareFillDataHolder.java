/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.core.component.prepare;

import com.eh.frog.core.context.FrogRuntimeContextHolder;
import com.eh.frog.core.model.PrepareData;

import java.util.Objects;

/**
 * @author f90fd4n david
 * @version 1.0.0: PrepareFillDataHolder.java, v 0.1 2021-11-08 10:43 上午 david Exp $$
 */
public class PrepareFillDataHolder {
	public static ThreadLocal<PrepareData> prepareDataThreadLocal = new ThreadLocal<PrepareData>();

	/**
	 * Gets context.
	 *
	 * @return the context
	 */
	public static PrepareData getPrepareData() {
		PrepareData prepareData = prepareDataThreadLocal.get();
		if (Objects.isNull(prepareData)) {
			prepareData = new PrepareData();
			prepareData.setDesc(FrogRuntimeContextHolder.getContext().getPrepareData().getDesc());
			setPrepareData(prepareData);
		}
		return prepareData;
	}

	/**
	 * Sets context.
	 *
	 * @param prepareData
	 */
	public static void setPrepareData(PrepareData prepareData) {
		prepareDataThreadLocal.set(prepareData);
	}

	public static void clear() {
		setPrepareData(null);
	}

}