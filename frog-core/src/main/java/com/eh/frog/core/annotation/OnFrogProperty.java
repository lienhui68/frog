/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.core.annotation;

import com.eh.frog.core.util.FrogFileUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author f90fd4n david
 * @version 1.0.0: OnFrogConfig.java, v 0.1 2021-11-05 11:10 上午 david Exp $$
 */
public class OnFrogProperty implements Condition {
	@Override
	public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
		Map<String, Object> annotationAttributes = metadata.getAnnotationAttributes(FrogConditionalOnProperty.class.getName());
		String key = (String) annotationAttributes.get("key");
		Boolean matchIfMissing = (Boolean) annotationAttributes.get("matchIfMissing");
		if (matchIfMissing) {
			return true;
		}

		String havingValue = String.valueOf(annotationAttributes.get("havingValue"));
		AtomicReference<String> value = new AtomicReference<>();
		FrogFileUtil.loadGlobalConfigFromYaml().getBaseConfig().ifPresent(baseConfig -> {
			value.set(baseConfig.get(key));
		});

		if (StringUtils.isEmpty(value.get())) {
			return false;
		}

		if (StringUtils.isEmpty(havingValue)) {
			return true;
		}
		if (havingValue.trim().equals(value.get().trim())) {
			return true;
		}
		return false;
	}
}