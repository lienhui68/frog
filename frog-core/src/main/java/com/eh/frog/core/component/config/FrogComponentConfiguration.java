/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.core.component.config;

import com.eh.frog.core.annotation.FrogConditionalOnProperty;
import com.eh.frog.core.component.event.MessageEventAdvice;
import com.eh.frog.core.constants.FrogConfigConstants;
import com.eh.frog.core.util.FrogFileUtil;
import org.springframework.aop.aspectj.AspectJExpressionPointcutAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author f90fd4n david
 * @version 1.0.0: FrogComponentConfiguration.java, v 0.1 2021-10-11 3:01 下午 david Exp $$
 */
@Configuration
public class FrogComponentConfiguration {

	@FrogConditionalOnProperty(FrogConfigConstants.MESSAGE_EVENT_POI)
	@Bean
	public AspectJExpressionPointcutAdvisor messageAdvisor() {
		AspectJExpressionPointcutAdvisor advisor = new AspectJExpressionPointcutAdvisor();
		String pointcut = FrogFileUtil.loadGlobalConfigFromYaml().getBaseConfig().get().get(FrogConfigConstants.MESSAGE_EVENT_POI);
		advisor.setExpression(pointcut);
		advisor.setAdvice(new MessageEventAdvice());
		return advisor;
	}
}