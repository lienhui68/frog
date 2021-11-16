/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.sample.base;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.support.GenericApplicationContext;

/**
 * @author f90fd4n david
 * @version 1.0.0: DisableAutowireRequireInitializer.java, v 0.1 2021-09-22 11:01 上午 david Exp $$
 */
public class DisableAutowireRequireInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

	@Override
	public void initialize(ConfigurableApplicationContext applicationContext) {

		GenericApplicationContext ctx = (GenericApplicationContext) applicationContext;
		ctx.registerBeanDefinition(AnnotationConfigUtils.AUTOWIRED_ANNOTATION_PROCESSOR_BEAN_NAME,
				BeanDefinitionBuilder
						.rootBeanDefinition(InternalAutowiredAnnotationBeanPostProcessor.class)
						.addPropertyValue("requiredParameterValue", false)
						.getBeanDefinition());
	}

}