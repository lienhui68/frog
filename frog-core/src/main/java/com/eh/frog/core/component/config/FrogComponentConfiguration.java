/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.core.component.config;

import com.eh.frog.core.annotation.NotEmptyPointcutCondition;
import com.eh.frog.core.component.event.MessageEventAdvice;
import com.eh.frog.core.constants.FrogConfigConstants;
import org.springframework.aop.aspectj.AspectJExpressionPointcutAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * @author f90fd4n david
 * @version 1.0.0: FrogComponentConfiguration.java, v 0.1 2021-10-11 3:01 下午 david Exp $$
 */
@Configuration
public class FrogComponentConfiguration {

	@Autowired
	private Environment environment;

	@Conditional(NotEmptyPointcutCondition.class)
	@Bean
	public AspectJExpressionPointcutAdvisor messageAdvisor() {
		AspectJExpressionPointcutAdvisor advisor = new AspectJExpressionPointcutAdvisor();
		advisor.setExpression(environment.getProperty(FrogConfigConstants.MESSAGE_EVENT_POINTCUT_KEY));
		advisor.setAdvice(new MessageEventAdvice());
		return advisor;
	}
}