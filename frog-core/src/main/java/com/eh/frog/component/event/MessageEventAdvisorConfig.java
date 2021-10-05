/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.component.event;

import com.eh.frog.util.FrogFileUtil;
import org.springframework.aop.aspectj.AspectJExpressionPointcutAdvisor;
import org.springframework.context.annotation.Bean;

/**
 * @author f90fd4n david
 * @version 1.0.0: ConfigurableAdvisorConfig.java, v 0.1 2021-09-29 3:18 下午 david Exp $$
 */
public class MessageEventAdvisorConfig {
	private static final String pointcut;

	static {
		pointcut = FrogFileUtil.getGlobalProperties().getProperty("message.event.pointCut");
	}

	@Bean
	public AspectJExpressionPointcutAdvisor messageAdvisor() {
		AspectJExpressionPointcutAdvisor advisor = new AspectJExpressionPointcutAdvisor();
		advisor.setExpression(pointcut);
		advisor.setAdvice(new MessageEventAdvice());
		return advisor;
	}
}