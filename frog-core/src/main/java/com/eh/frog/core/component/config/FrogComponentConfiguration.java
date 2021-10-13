/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.core.component.config;

import com.eh.frog.core.component.event.MessageEventAdvice;
import com.eh.frog.core.util.FrogFileUtil;
import org.springframework.aop.aspectj.AspectJExpressionPointcutAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

/**
 *
 * @author f90fd4n david
 * @version 1.0.0: FrogComponentConfiguration.java, v 0.1 2021-10-11 3:01 下午 david Exp $$
 */
@Configuration
public class FrogComponentConfiguration {
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

	private static String getRootFolder(String dataProvider) {
		StringBuilder sb = new StringBuilder(System.getProperty("user.dir"));
		sb.append("/src/test/resources/");
		if (!StringUtils.isEmpty(dataProvider)) {
			sb.append(dataProvider);
		}
		return sb.toString();
	}
}