/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.core.component.event;

import com.eh.frog.core.exception.FrogTestException;
import com.eh.frog.core.util.ObjectUtil;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;

/**
 * @author f90fd4n david
 * @version 1.0.0: SendMessageAdvice.java, v 0.1 2021-09-29 3:17 下午 david Exp $$
 */
@Slf4j
public class MessageEventAdvice implements MethodInterceptor {
	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		Method method = invocation.getMethod();

		log.info("拦截到消息发送,{}", method.getDeclaringClass().getSimpleName() + "#" + method.getName());
		// 获取待发送消息体
		Object[] args = invocation.getArguments();
		if (args.length == 0) {
			throw new FrogTestException("消息体参数不匹配");
		}
		// 默认第一个参数是消息体
		log.info("消息体:{}", ObjectUtil.toJson(args[0]));
		Object actual = args[0];
		Object result = invocation.proceed();
		EventContextHolder.setEvent(actual.getClass().getName(), actual);
		return result;
	}
}