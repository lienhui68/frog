/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.core.component.config;

import com.eh.frog.core.annotation.FrogConditionalOnProperty;
import com.eh.frog.core.component.event.MessageEventAdvice;
import com.eh.frog.core.component.prepare.MybatisPlugin4Prepare;
import com.eh.frog.core.constants.FrogConfigConstants;
import com.eh.frog.core.util.FrogFileUtil;
import org.apache.ibatis.plugin.Interceptor;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.aop.aspectj.AspectJExpressionPointcutAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.util.ObjectUtils;

/**
 * @author f90fd4n david
 * @version 1.0.0: FrogComponentConfiguration.java, v 0.1 2021-10-11 3:01 下午 david Exp $$
 */
@Configuration
public class FrogComponentAutoConfiguration {

	@Autowired(required = false)
	private SqlSessionFactoryBean sqlSessionFactoryBean;

	@FrogConditionalOnProperty(FrogConfigConstants.MESSAGE_EVENT_POI)
	@Bean
	public AspectJExpressionPointcutAdvisor messageAdvisor() {
		AspectJExpressionPointcutAdvisor advisor = new AspectJExpressionPointcutAdvisor();
		String pointcut = FrogFileUtil.loadGlobalConfigFromYaml().getBaseConfig().get().get(FrogConfigConstants.MESSAGE_EVENT_POI);
		advisor.setExpression(pointcut);
		advisor.setAdvice(new MessageEventAdvice());
		return advisor;
	}

	@FrogConditionalOnProperty(value = FrogConfigConstants.PREPARE_RUN_BACK_FILL, havingValue = "true")
	@Bean
	public Interceptor mybatisPlugin4Prepare() throws Exception {
		MybatisPlugin4Prepare mybatisPlugin4Prepare = new MybatisPlugin4Prepare();
		// 兼容自定义sqlSessionFactoryBean（没有使用MybatisAutoConfiguration方式注入）时，设置plugins没有扫描容器中Interceptor，做到对业务代码无侵入
		if (!ObjectUtils.isEmpty(sqlSessionFactoryBean)) {
			org.apache.ibatis.session.Configuration configuration = sqlSessionFactoryBean.getObject().getConfiguration();
			configuration.addInterceptor(mybatisPlugin4Prepare);
		}
		return mybatisPlugin4Prepare;
	}
}