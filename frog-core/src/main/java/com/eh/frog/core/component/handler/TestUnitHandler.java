/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.core.component.handler;

import com.eh.frog.core.component.event.EventContextHolder;
import com.eh.frog.core.component.mock.MockContextHolder;
import com.eh.frog.core.component.mock.MockRestorePojo;
import com.eh.frog.core.component.mock.reflect.Invoker;
import com.eh.frog.core.component.prepare.PrepareFillDataHolder;
import com.eh.frog.core.config.GlobalConfigurationHolder;
import com.eh.frog.core.context.FrogRuntimeContext;
import com.eh.frog.core.context.FrogRuntimeContextHolder;
import com.eh.frog.core.exception.FrogCheckException;
import com.eh.frog.core.exception.FrogTestException;
import com.eh.frog.core.model.CheckFlag;
import com.eh.frog.core.model.PrepareData;
import com.eh.frog.core.model.VirtualEventGroup;
import com.eh.frog.core.model.VirtualObject;
import com.eh.frog.core.model.ext.MockUnit;
import com.eh.frog.core.util.CollectionUtil;
import com.eh.frog.core.util.ObjectCompareUtil;
import com.eh.frog.core.util.ObjectUtil;
import com.eh.frog.core.util.StringUtil;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.util.Date;
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
					Object obj = m.getDefaultObj();
					List<MockUnit> mockUnits = m.getMockUnits();
					if (Objects.nonNull(obj)) {
						log.info("mock: {}\n 返回:{}", target, ObjectUtil.toJson(obj));
					} else if (Objects.nonNull(mockUnits)) {
						log.info("mock: {}\n 返回:{}", target, ObjectUtil.toJson(mockUnits));
					}

					mockBeanService(target, m.getContainerBeanName(), m.getContainer(), m.getFieldName(), obj, mockUnits);
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

	private void mockBeanService(String target, String containerBeanName, String container, String fieldName, Object obj, List<MockUnit> mockUnits) {
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
			Method method = clazz.getDeclaredMethod(methodName, paramClasses);
			method.setAccessible(true);
			// mock
			mockBeanService(clazz, containerBeanName, container, fieldName, method, obj, mockUnits);
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
	private <T> void mockBeanService(Class<T> clazz, String containerBeanName, String container, String fieldName, Method method, T rt, List<MockUnit> mockUnits) throws InvocationTargetException, IllegalAccessException {


		try {
			// 获取container
			Object containerBean;
			if (StringUtils.isEmpty(containerBeanName)) {
				containerBean = frogRuntimeContext.getApplicationContext().getBean(Class.forName(container));
			} else {
				containerBean = frogRuntimeContext.getApplicationContext().getBean(containerBeanName);
			}

			// 属性名
			if (StringUtils.isEmpty(fieldName)) {
				fieldName = clazz.getSimpleName();
				char[] chars = fieldName.toCharArray();
				chars[0] += 32;
				fieldName = String.valueOf(chars);
			}

			// 生成代理类
			Object target = Invoker.getInstance(method, rt, mockUnits);

			// 保护现场
			MockContextHolder.setMockRestorePojo(new MockRestorePojo(containerBean, target, fieldName));
			// 替换
			Field ff = containerBean.getClass().getDeclaredField(fieldName);
			ff.setAccessible(true);
			ff.set(containerBean, target);
		} catch (NoSuchFieldException | ClassNotFoundException e) {
			throw new FrogTestException("mock数据error", e);
		}

	}

	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		//如果传进来是一个已实现的具体类（本次演示略过此逻辑)
		if (Object.class.equals(method.getDeclaringClass())) {
			try {
				return method.invoke(this, args);
			} catch (Throwable t) {
				t.printStackTrace();
			}
			//如果传进来的是一个接口（核心)
		} else {
			return run(method, args);
		}
		return null;
	}

	/**
	 * 实现接口的核心方法
	 *
	 * @param method
	 * @param args
	 * @return
	 */
	public Object run(Method method, Object[] args) {
		//TODO
		//如远程http调用
		//如远程方法调用（rmi)
		//....
		return "method call success!";
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
				} catch (InvocationTargetException e) {
					// 处理反射时抛出frog异常被wrap掉的情况
					if (Objects.nonNull(e.getTargetException()) && e.getTargetException() instanceof FrogTestException) {
						throw (FrogTestException) e.getTargetException();
					}
				} catch (FrogTestException e) {
					throw e;
				} catch (Exception e) {
					log.info("bad getErrorInfoFromException");
					frogRuntimeContext.setExceptionObj(e.getCause());
				}
				frogRuntimeContext.setResultObj(resultObj);
				// 预跑反填
				if (GlobalConfigurationHolder.getFrogConfig().isEnablePrepareFill()) {
					// 获取待填PrepareData
					PrepareData prepareData = PrepareFillDataHolder.getPrepareData();
					VirtualObject resultVirtualObject = VirtualObject.of(resultObj);
					prepareData.setExpectResult(resultVirtualObject);

					Map<String, Map<String, String>> resultFlags = getResultFlags();
					prepareData.setExpectResult(VirtualObject.of(CollectionUtil.filterObjByFlags(resultObj, GlobalConfigurationHolder.getFrogConfig().isPrepareFillFlagFilter(), resultFlags)));
				}

			} else {
				log.info("Test method not found, interrupt invocation");
			}
		} catch (Exception e) {
			throw new RuntimeException("unknown exception while invocation", e);
		}
	}

	private Map<String, Map<String, String>> getResultFlags() {
		Map<String, Map<String, String>> flags = Maps.newHashMap();
		if (GlobalConfigurationHolder.getFrogConfig().isPrepareFillFlagFilter()) {
			VirtualObject expectResult = FrogRuntimeContextHolder.getContext().getPrepareData().getExpectResult();
			if (Objects.nonNull(expectResult)) {
				flags = expectResult.getFlags();
			}
		}
		return flags;
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
			String err = StringUtil.buildMessage("\n======>期望DB数据校验失败, 失败原因:{}", e.getMessage());
			throw new FrogCheckException(err, e);
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
			String err = StringUtil.buildMessage("\n======>期望结果数据校验失败, 失败原因:{}", e.getMessage());
			throw new FrogCheckException(err, e);
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
			String err = StringUtil.buildMessage("\n======>期望消息数据校验失败, 失败原因:{}", e.getMessage());
			throw new FrogCheckException(err, e);
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