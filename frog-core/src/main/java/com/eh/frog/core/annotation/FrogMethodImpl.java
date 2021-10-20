/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.core.annotation;

import com.eh.frog.core.context.FrogRuntimeContext;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author f90fd4n david
 * @version 1.0.0: FrogMethodImpl.java, v 0.1 2021-10-20 8:01 下午 david Exp $$
 */
@Slf4j
@NoArgsConstructor
public class FrogMethodImpl implements IFrogMethod {

	private Method invoker;

	protected Object instance;

	@Getter
	/* the invoke order */
	private int order = 0;

	@Override
	public void invoke(FrogRuntimeContext frogRuntimeContext) {
		try {
			if (this.invoker.getParameterTypes().length == 0) {
				this.invoker.setAccessible(true);
				this.invoker.invoke(instance, new Object[]{});
				return;
			}

			if (this.invoker.getParameterTypes()[0].equals(FrogRuntimeContext.class)) {
				this.invoker.setAccessible(true);
				this.invoker.invoke(instance, new Object[]{frogRuntimeContext});
				return;
			}

		} catch (IllegalAccessException e) {
			if (log.isInfoEnabled()) {
				log.info("error ", e);
			}
		} catch (IllegalArgumentException e) {
			if (log.isInfoEnabled()) {
				log.info("error ", e);
			}
		} catch (InvocationTargetException e) {
			if (log.isInfoEnabled()) {
				log.info("error ", e);
			}
		}
	}

	/**
	 * Constructor.
	 *
	 * @param method   the method
	 * @param instance the instance
	 */
	public FrogMethodImpl(Method method, Object instance) {
		this.invoker = method;
		this.instance = instance;
	}

	@Override
	public int getOrder() {
		return 0;
	}
}