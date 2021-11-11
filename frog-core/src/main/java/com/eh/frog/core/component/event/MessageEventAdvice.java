/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.core.component.event;

import com.eh.frog.core.component.prepare.PrepareFillDataHolder;
import com.eh.frog.core.config.GlobalConfigurationHolder;
import com.eh.frog.core.context.FrogRuntimeContextHolder;
import com.eh.frog.core.exception.FrogTestException;
import com.eh.frog.core.model.PrepareData;
import com.eh.frog.core.model.VirtualEventGroup;
import com.eh.frog.core.util.CollectionUtil;
import com.eh.frog.core.util.ObjectUtil;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.assertj.core.util.Lists;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
		String clazzName = actual.getClass().getName();
		EventContextHolder.setEvent(clazzName, actual);
		// 预跑反填
		if (GlobalConfigurationHolder.getFrogConfig().isEnablePrepareFill()) {
			// 获取待填PrepareData
			PrepareData prepareData = PrepareFillDataHolder.getPrepareData();
			List<VirtualEventGroup> expectEventSet = prepareData.getExpectEventSet();
			if (CollectionUtils.isEmpty(expectEventSet)) {
				expectEventSet = Lists.newArrayList();
				prepareData.setExpectEventSet(expectEventSet);
			}
			// 表示拦截到的表在现有list中已经存在
			boolean occursFlag = false;

			Map<String, Map<String, String>> msgFlags = getMsgFlags(clazzName);
			for (VirtualEventGroup virtualEventGroup : expectEventSet) {
				if (virtualEventGroup.getMsgClass().equalsIgnoreCase(clazzName)) {
					virtualEventGroup.getObjects().add(CollectionUtil.filterObjByFlags(actual, GlobalConfigurationHolder.getFrogConfig().isPrepareFillFlagFilter(), msgFlags));
					occursFlag = true;
				}
			}
			if (!occursFlag) {
				VirtualEventGroup virtualEventGroup = new VirtualEventGroup();
				virtualEventGroup.setMsgClass(clazzName);
				virtualEventGroup.setObjects(Lists.newArrayList(CollectionUtil.filterObjByFlags(actual, GlobalConfigurationHolder.getFrogConfig().isPrepareFillFlagFilter(), msgFlags)));
				prepareData.getExpectEventSet().add((virtualEventGroup));
			}
		}
		return result;
	}

	private Map<String, Map<String, String>> getMsgFlags(String msgClass) {
		Map<String, Map<String, String>> flags = Maps.newHashMap();
		if (GlobalConfigurationHolder.getFrogConfig().isPrepareFillFlagFilter()) {
			List<VirtualEventGroup> expectEventSet = FrogRuntimeContextHolder.getContext().getPrepareData().getExpectEventSet();
			if (!CollectionUtils.isEmpty(expectEventSet)) {
				// 判断是否添加该msg
				Optional<VirtualEventGroup> first = expectEventSet.stream().filter(m -> msgClass.equals(m.getMsgClass())).findFirst();
				if (first.isPresent()) {
					flags = first.get().getFlags();
				}
			}
		}
		return flags;
	}

}