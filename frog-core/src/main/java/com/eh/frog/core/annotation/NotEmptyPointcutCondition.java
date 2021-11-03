/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.core.annotation;

import com.eh.frog.core.constants.FrogConfigConstants;
import com.eh.frog.core.util.FrogFileUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * @author f90fd4n david
 * @version 1.0.0: NotEmptyCondition.java, v 0.1 2021-11-02 8:35 下午 david Exp $$
 */
public class NotEmptyPointcutCondition implements Condition {
	@Override
	public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
		final String[] pointcut = new String[1];
		FrogFileUtil.loadGlobalConfigFromYaml().getBaseConfig().ifPresent(baseConfig -> {
			pointcut[0] = baseConfig.get(FrogConfigConstants.MESSAGE_EVENT_POI);
			if (StringUtils.isNotEmpty(pointcut[0])) {
				System.getProperties().setProperty(FrogConfigConstants.MESSAGE_EVENT_POINTCUT_KEY, pointcut[0]);
			}
		});

		// 非空
		return StringUtils.isNotEmpty(pointcut[0]);
	}
}