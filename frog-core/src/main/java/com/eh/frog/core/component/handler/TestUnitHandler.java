/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.core.component.handler;

import com.eh.frog.core.component.event.EventContextHolder;
import com.eh.frog.core.component.mock.MockContextHolder;
import com.eh.frog.core.component.mock.MockRestorePojo;
import com.eh.frog.core.context.FrogRuntimeContext;
import com.eh.frog.core.exception.FrogCheckException;
import com.eh.frog.core.exception.FrogTestException;
import com.eh.frog.core.model.VirtualEventGroup;
import com.eh.frog.core.model.VirtualObject;
import com.eh.frog.core.util.ObjectCompareUtil;
import com.eh.frog.core.util.ObjectUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.lang.reflect.*;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author f90fd4n david
 * @version 1.0.0: TestUnitHandler.java, v 0.1 2021-09-28 10:00 上午 david Exp $$
 */
@Slf4j
public class TestUnitHandler {
	/**
	 * Runtime context
	 */
	FrogRuntimeContext frogRuntimeContext;

	public TestUnitHandler(FrogRuntimeContext frogRuntimeContext) {
		this.frogRuntimeContext = frogRuntimeContext;
	}

	/**
	 * 准备数据
	 */
	public void prepareDepData() {
		try {

			if (null != frogRuntimeContext.getPrepareData().getDepDataSet()
					&& null != frogRuntimeContext.getPrepareData().getDepDataSet()) {

				log.info("Preparing DB data...");
				frogRuntimeContext.getDbDataProcessor().importDepDBData(
						frogRuntimeContext.getPrepareData().getDepDataSet());

			} else {
				log.info("None DB preparation");
			}
		} catch (FrogTestException fe) {
			throw new FrogTestException("准备预备数据发生错误", fe);
		} catch (Exception e) {
			throw new FrogTestException(
					"Unknown exception while preparing DB data. ", e);
		}
	}

	/**
	 * 准备MOck数据
	 */
	public void prepareMockData() {
		try {

			if (null != frogRuntimeContext.getPrepareData().getVirtualMockSet()) {

				log.info("Preparing MOCK data...");
				// init mock
				frogRuntimeContext.getPrepareData().getVirtualMockSet().forEach(m -> {
					String target = m.getTarget();
					Object obj = m.getObject();
					log.info("mock: {}\n 返回:{}", target, ObjectUtil.toJson(obj));
					mockBeanService(target, m.getTargetBeanName(), m.getContainer(), m.getFieldName(), obj);
				});
				// 刷新bean,防止container就是测试对象
				frogRuntimeContext.setTestedObj(frogRuntimeContext.getApplicationContext().getBean(frogRuntimeContext.getTestedObj().getClass()));
			} else {
				log.info("None mock preparation");
			}
		} catch (FrogTestException fe) {
			throw new FrogTestException("准备MOCK数据发生错误", fe);
		} catch (Exception e) {
			throw new FrogTestException(
					"Unknown exception while preparing mock data. ", e);
		}
	}

	private void mockBeanService(String target, String targetBeanName, String container, String fieldName, Object obj) {
		try {
			String[] ss = target.split("#");
			String fullClassName = ss[0];
			String[] sss2 = ss[1].split("\\(");
			String methodName = sss2[0];
			String[] ssss = sss2[1].substring(0, sss2[1].length() - 1).split(",");
			Class<?>[] paramClasses = new Class[ssss.length];
			for (int i = 0; i < ssss.length; i++) {
				paramClasses[i] = Class.forName(ssss[i].trim());
			}
			// 类
			Class clazz = Class.forName(fullClassName);
			// 方法
			Method method = Class.forName(ss[0]).getDeclaredMethod(methodName, paramClasses);
			method.setAccessible(true);
			// mock
			mockBeanService(clazz, targetBeanName, container, fieldName, method, obj);
		} catch (Exception e) {
			throw new FrogTestException("解析目标服务出错:" + target, e);
		}
	}

	/**
	 * @param clazz
	 * @param container
	 * @param fieldName
	 * @param method
	 * @param rt
	 * @param <T>
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	private <T> void mockBeanService(Class<T> clazz, String targetBeanName, String container, String fieldName, Method method, T rt) throws InvocationTargetException, IllegalAccessException {

		Object target;
		if (StringUtils.isEmpty(targetBeanName)) {
			target = frogRuntimeContext.getApplicationContext().getBean(clazz);
		} else {
			target = frogRuntimeContext.getApplicationContext().getBean(targetBeanName);
		}
		// 动态代理
		// 代理对象的方法最终都会被JVM导向它的invoke方法
		Object mock = Proxy.newProxyInstance(
				target.getClass().getClassLoader(), // 类加载器
				target.getClass().getInterfaces(), // 让代理对象和目标对象实现相同接口
				(proxy, method1, args) -> {
					if (method1.equals(method)) {
						return rt;
					} else {
						return method1.invoke(target, args);
					}

				});

		try {
			// 获取container
			Object containerBean = frogRuntimeContext.getApplicationContext().getBean(Class.forName(container));
			if (StringUtils.isEmpty(fieldName)) {
				fieldName = clazz.getSimpleName();
				char[] chars = fieldName.toCharArray();
				chars[0] += 32;
				fieldName = String.valueOf(chars);
			}
			// 保护现场
			MockContextHolder.setMockRestorePojo(new MockRestorePojo(containerBean, target, fieldName));
			Field f = containerBean.getClass().getDeclaredField(fieldName);
			f.setAccessible(true);
			f.set(containerBean, mock);
		} catch (NoSuchFieldException | ClassNotFoundException e) {
			throw new FrogTestException("mock数据error", e);
		}

	}

	/**
	 * 准备MOck数据
	 */
	public void prepareConfigData() {
		try {
			if (null != frogRuntimeContext.getPrepareData().getVirtualConfigSet()) {
				log.info("Preparing Config data...");
				// init mock
				frogRuntimeContext.getPrepareData().getVirtualConfigSet().forEach(m -> {
					try {
						String container = m.getContainer();
						String configField = m.getConfigField();
						Object configVal = m.getConfigValue();
						Class clazz = Class.forName(container);
						Field f = clazz.getDeclaredField(configField);
						f.setAccessible(true);
						Field modifiersField = Field.class.getDeclaredField("modifiers");
						modifiersField.setAccessible(true);
						modifiersField.setInt(f, f.getModifiers() & ~Modifier.FINAL);
						f.set(null, configVal);
						log.info("设置配置项, container:{}, field:{}, value:{}", container, configField, ObjectUtil.toJson(configVal));
					} catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
						e.printStackTrace();
					}
				});
			} else {
				log.info("None config preparation");
			}
		} catch (FrogTestException fe) {
			throw new FrogTestException("准备Config数据发生错误", fe);
		} catch (Exception e) {
			throw new FrogTestException(
					"Unknown exception while preparing config data. ", e);
		}
	}

	/**
	 * 执行用例
	 */
	public void execute() {
		try {
			List<VirtualObject> args = frogRuntimeContext.getPrepareData().getArgs();

			Object[] paramObjs = null;
			if (args != null) {
				paramObjs = args.stream().map(a -> a.getObject()).collect(Collectors.toList()).toArray(new Object[0]);
			}
			if (frogRuntimeContext.getTestedMethod() != null) {
				Object resultObj = null;
				try {
					log.info("Start to invoke method:" + frogRuntimeContext.getTestedMethod().getName());
					resultObj = frogRuntimeContext.getTestedMethod().invoke(frogRuntimeContext.getTestedObj(), paramObjs);
					log.info("Invocation result: " + ObjectUtil.toJson(resultObj));
				} catch (Exception e) {
					log.info("bad getErrorInfoFromException");
					frogRuntimeContext.setExceptionObj(e.getCause());
				}
				frogRuntimeContext.setResultObj(resultObj);

			} else {
				log.info("Test method not found, interrupt invocation");
			}
		} catch (Exception e) {
			throw new RuntimeException("unknown exception while invocation", e);
		}
	}

	/**
	 * 异常check
	 */
	public void checkException() {
		// Abnormal contrast
		if (frogRuntimeContext.getPrepareData().getExpectException() != null
				&& frogRuntimeContext.getPrepareData().getExpectException().getObject() != null) {
			if (frogRuntimeContext.getExceptionObj() != null) {
				log.info("Checking Exception");
				Object actualExp = frogRuntimeContext.getExceptionObj();
				VirtualObject expectedExp = frogRuntimeContext.getPrepareData().getExpectException();
				ObjectCompareUtil.compare(actualExp, expectedExp.getObject(), expectedExp.getFlags());

			} else {
				throw new FrogCheckException("None Exception raised during invocation");

			}
		} else {
			if (frogRuntimeContext.getExceptionObj() != null) {
				throw new FrogTestException("unknown exception raised during invocation",
						(Throwable) frogRuntimeContext.getExceptionObj());
			}
			log.info("None exception to check");
		}
	}

	/**
	 * 持久化check
	 */
	public void checkExpectDbData() {
		try {

			if (null != frogRuntimeContext.getPrepareData().getExpectDataSet()) {
				log.info("Checking DB, tables checked");

				frogRuntimeContext.getDbDataProcessor().compare2DBData(
						frogRuntimeContext.getPrepareData().getExpectDataSet());
			} else {
				log.info("None DB expectation");
			}
		} catch (FrogCheckException e) {
			throw new FrogCheckException("\n======>期望DB数据校验失败, 失败原因:{}", e.getMessage());
		} catch (Exception e) {
			throw new FrogTestException("unknown exception while checking DB", e);
		}

	}

	/**
	 * 结果check
	 */
	public void checkExpectResult() {
		try {
			if (frogRuntimeContext.getPrepareData().getExpectResult() != null
					&& frogRuntimeContext.getPrepareData().getExpectResult().getObject() != null) {
				log.info("Checking invocation result");
				VirtualObject expect = frogRuntimeContext.getPrepareData().getExpectResult();
				Object actual = frogRuntimeContext.getResultObj();

				ObjectCompareUtil.compare(actual, expect.getObject(), expect.getFlags());
			} else {
				log.info("None result expectation");
			}
		} catch (FrogCheckException e) {
			throw new FrogCheckException("\n======>期望结果数据校验失败, 失败原因:{}", e.getMessage());
		} catch (Exception e) {
			throw new FrogTestException("unknown exception while checking invocation result", e);
		}
	}

	/**
	 * 消息check
	 */
	public void checkExpectEvent() {
		try {
			log.info("Checking Events");
			List<VirtualEventGroup> eventGroups = frogRuntimeContext.getPrepareData().getExpectEventSet();
			if (Objects.nonNull(eventGroups) && !eventGroups.isEmpty()) {
				Map<String, List<Object>> actualEvents = EventContextHolder.getBizEvent();
				eventGroups.forEach(group -> {
					List<Object> expectObjs = group.getObjects();
					String key = group.getMsgClass();
					Map<String, Map<String, String>> flags = group.getFlags();
					ObjectCompareUtil.compare(actualEvents.get(key), expectObjs, flags);
				});
			} else {
				log.info("Skip event check in rpc mode");
			}
		} catch (FrogCheckException e) {
			throw new FrogCheckException("\n======>期望消息数据校验失败, 失败原因:{}", e.getMessage());
		} catch (
				Exception e) {
			throw new FrogTestException("unknown exception raised while checking events", e);
		}

	}

	/**
	 * clean up data already prepared
	 */
	public void clearDepData() {
		try {
			if (null != frogRuntimeContext.getPrepareData().getDepDataSet()) {
				log.info("Cleaning up DB data preparations...");
				frogRuntimeContext.getDbDataProcessor().cleanDBData(
						frogRuntimeContext.getPrepareData().getDepDataSet());

			} else {
				log.info("None DB preparation to clean");
			}
		} catch (FrogTestException fe) {
			throw new FrogTestException("清理预备数据发生错误", fe);
		} catch (Exception e) {
			throw new FrogTestException("Unknown exception raised while cleaning DB preparations",
					e);
		}
	}

	/**
	 * Clean up expected data
	 */
	public void clearExpectDBData() {
		try {
			if (null != frogRuntimeContext.getPrepareData().getExpectDataSet()) {
				log.info("Cleaning up DB expectation data...");
				frogRuntimeContext.getDbDataProcessor().cleanDBData(
						frogRuntimeContext.getPrepareData().getExpectDataSet());

			} else {
				log.info("None DB expectation to clean");
			}
		} catch (FrogTestException fe) {
			throw new FrogTestException("清理期望数据发生错误", fe);
		} catch (Exception e) {
			throw new FrogTestException("unknown exception raised while cleaning DB expectations",
					e);
		}
	}

	/**
	 * Replace custom input
	 */
	public void prepareUserPara() {

	}

}