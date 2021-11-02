/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.core.template;

import com.eh.frog.core.annotation.*;
import com.eh.frog.core.component.db.DBDataProcessor;
import com.eh.frog.core.component.event.EventContextHolder;
import com.eh.frog.core.component.handler.TestUnitHandler;
import com.eh.frog.core.component.mock.MockContextHolder;
import com.eh.frog.core.config.FrogConfig;
import com.eh.frog.core.config.GlobalConfigurationHolder;
import com.eh.frog.core.constants.FrogConfigConstants;
import com.eh.frog.core.context.FrogRuntimeContext;
import com.eh.frog.core.context.FrogRuntimeContextHolder;
import com.eh.frog.core.exception.FrogCheckException;
import com.eh.frog.core.exception.FrogTestException;
import com.eh.frog.core.model.PrepareData;
import com.eh.frog.core.util.ClassHelper;
import com.eh.frog.core.util.FrogFileUtil;
import com.eh.frog.core.util.StringUtil;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author f90fd4n david
 * @version 1.0.0: FrogTestBase.java, v 0.1 2021-09-15 12:49 上午 david Exp $$
 */
@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class FrogTestBase implements ApplicationContextAware {

	/**
	 * case上下文
	 */
	private FrogRuntimeContext frogRuntimeContext;

	/**
	 * data processing
	 */
	public static DBDataProcessor dbDataProcessor;

	public TestUnitHandler testUnitHandler;

	/**
	 * annotationMethods map
	 */
	public Map<String, List<IFrogMethod>> annotationMethods = new HashMap<>();

	/**
	 * annotationFactory
	 */
	protected FrogAnnotationFactory annotationFactory;

	/**
	 * The {@link ApplicationContext} that was injected into this test instance
	 * via {@link #setApplicationContext(ApplicationContext)}.
	 */
	@Nullable
	protected ApplicationContext applicationContext;

	@BeforeAll
	public void setUp() throws Exception {
		try {
			// 初始化配置
			initConfiguration();
			String dsName = GlobalConfigurationHolder.getGlobalConfiguration().get(FrogConfigConstants.DATASOURCE_BEAN_NAME);
			Assertions.assertNotNull(dsName, "数据源名称未配置");
			// 初始化数据处理器
			if (dbDataProcessor == null) {
				dbDataProcessor = new DBDataProcessor(applicationContext.getBean(dsName, DataSource.class));
			}
			//annotation init
			annotationFactory = new FrogAnnotationFactory(annotationMethods);
			Set<Method> allMethod = ClassHelper.getDeclaredAvailableMethods(this.getClass());
			annotationFactory.initAnnotationMethod(allMethod, this);
		} catch (BeansException e) {
			log.error("Exception raised during setup process");
			throw new RuntimeException(e);
		}
	}
	/**
	 * Obtain the tested method of the tested object
	 *
	 * @param methodName
	 * @param testedObj
	 * @return
	 */
	private Method findMethod(String methodName, Object testedObj) {
		Class clazz = testedObj.getClass();
		if (clazz != null) {
			while (clazz != null && !clazz.equals(Object.class)) {
				Method[] methods = clazz.getDeclaredMethods();
				// 过滤代理类,eg: XXX$$EnhancerBySpringCGLIB$$58cd8660
				if (!clazz.getName().contains("EnhancerBySpringCGLIB") && methods != null) {
					for (Method method : methods) {
						if (method != null
								&& org.apache.commons.lang.StringUtils.equalsIgnoreCase(method.getName(), methodName)) {
							return method;
						}
					}
				}
				clazz = clazz.getSuperclass();
			}
		}
		return null;
	}

	private void initConfiguration() {
		if (Objects.nonNull(GlobalConfigurationHolder.getGlobalConfiguration())) {
			return;
		}
		FrogConfig frogConfig = FrogFileUtil.loadGlobalConfigFromYaml();
		if (Objects.isNull(frogConfig)) {
			throw new FrogTestException("全局配置文件");
		}
		// 全局配置
		frogConfig.getBaseConfig().ifPresent(GlobalConfigurationHolder::setGlobalConfiguration);
		// 表selectKeys
		final Map<String, List<String>> selectKeys = Maps.newHashMap();
		frogConfig.getTableQueryConfig().ifPresent(tableQueryConfig -> {
			tableQueryConfig.getCommonTableQueryConfig().ifPresent(commonTableQueryConfig -> selectKeys.put(FrogConfigConstants.FROG_VIRTUAL_COMMON_TABLE, commonTableQueryConfig));
			tableQueryConfig.getSpecialTableQueryConfig().ifPresent(specialTableQueryConfig -> selectKeys.putAll(specialTableQueryConfig));
		});
		GlobalConfigurationHolder.setSelectKeys(selectKeys);
	}

	public void runTest(String caseId, PrepareData prepareData) {
		log.info("\n=============================Start executing, TestCase caseId:" + caseId + " "
				+ prepareData.getDesc() + "=================");
		try {
			String testMethodName = Thread.currentThread().getStackTrace()[2].getMethodName();
			initRuntimeContext(caseId, prepareData, testMethodName);
			initTestUnitHandler();
			initComponentsBeforeTest();
			// before all tests, the method will be executed
			beforeFrogTest(frogRuntimeContext);
			process(frogRuntimeContext);
			log.info("=============================Execute success, TestCase caseId:" + caseId + " "
					+ prepareData.getDesc() + "=================");
		} catch (FrogCheckException fe) {
			log.error("case数据校验失败,id:{},失败原因:{}", caseId, fe.getMessage());
		} catch (FrogTestException fe) {
			log.error("case执行发生错误,id:{},错误原因:{}", caseId, fe.getMessage(), fe);
		} catch (Throwable t) {
			log.error("case执行发生系统错误,id:{}", caseId, t);
		} finally {
			try {
				// After all tests, the method will be executed
				afterFrogTest(frogRuntimeContext);
				// clean up thread variable
				EventContextHolder.clear();
				MockContextHolder.restore();
			} catch (FrogTestException fe) {
				log.error("case执行发生错误,id:{},错误原因:{}", caseId, fe.getMessage(), fe);
			} catch (Throwable t) {
				log.error("case执行发生系统错误,id:{}", caseId, t);
			}
		}
	}

	/**
	 * @param frogRuntimeContext
	 */
	public void process(FrogRuntimeContext frogRuntimeContext) {
		// clear
		clear(frogRuntimeContext);
		// prepare
		prepare(frogRuntimeContext);
		// execute
		execute(frogRuntimeContext);
		// check
		check(frogRuntimeContext);
	}

	/**
	 * @param frogRuntimeContext
	 * @throws FrogTestException
	 */
	public void prepare(FrogRuntimeContext frogRuntimeContext) throws FrogTestException {

		log.info("=============================[frog prepare begin]=============================\r\n");
		invokeIFrogMethods(BeforePrepare.class, frogRuntimeContext);

		testUnitHandler.prepareDepData();
		testUnitHandler.prepareMockData();
		testUnitHandler.prepareConfigData();

		invokeIFrogMethods(AfterPrepare.class, frogRuntimeContext);
		log.info("=============================[frog prepare end]=============================\r\n");
	}

	/**
	 * @param frogRuntimeContext
	 * @throws FrogTestException
	 */
	public void execute(FrogRuntimeContext frogRuntimeContext) throws FrogTestException {
		log.info("=============================[frog execute begin]=============================\r\n");
		testUnitHandler.execute();
		log.info("=============================[frog execute end]=============================\r\n");
	}

	/**
	 * @param frogRuntimeContext
	 * @throws FrogTestException
	 */
	public void check(FrogRuntimeContext frogRuntimeContext) throws FrogTestException {

		log.info("=============================[frog check begin]=============================\r\n");
		invokeIFrogMethods(BeforeCheck.class, frogRuntimeContext);

		testUnitHandler.checkException();
		testUnitHandler.checkExpectDbData();
		testUnitHandler.checkExpectEvent();
		testUnitHandler.checkExpectResult();

		invokeIFrogMethods(AfterCheck.class, frogRuntimeContext);
		log.info("=============================[frog check end]=============================\r\n");
	}

	/**
	 * @param frogRuntimeContext
	 * @throws FrogTestException
	 */
	public void clear(FrogRuntimeContext frogRuntimeContext) throws FrogTestException {
		log.info("=============================[frog clear begin]=============================\r\n");
		invokeIFrogMethods(BeforeClean.class, frogRuntimeContext);

		testUnitHandler.clearDepData();
		testUnitHandler.clearExpectDBData();

		invokeIFrogMethods(AfterClean.class, frogRuntimeContext);

		log.info("=============================[frog clear end]=============================\r\n");
	}


	/**
	 * Initialize the ACTS context
	 *
	 * @param caseId
	 * @param prepareData
	 * @param testMethodName
	 */
	public void initRuntimeContext(String caseId, PrepareData prepareData, String testMethodName) throws Exception {
		Object testObj = getTestedObj();
		Method testMethod = getTestedMethod(testObj, getTestMethod(testMethodName));

		frogRuntimeContext = new FrogRuntimeContext(caseId, prepareData, testMethod, testObj, dbDataProcessor, applicationContext);
		FrogRuntimeContextHolder.setContext(frogRuntimeContext);
	}

	private Method getTestMethod(String testMethodName) {
		Class<?> clz = getClass();
		try {
			Method m = clz.getDeclaredMethod(testMethodName, String.class, String.class, PrepareData.class);
			return m;
		} catch (NoSuchMethodException e) {
			log.error("there is no test method in the test class with name:{}", testMethodName, e);
			throw new RuntimeException(e);
		}
	}

	private Method getTestedMethod(Object testObj, Method testMethod) throws Exception {
		// 测试方法
		OverloadHandler overloadHandlerAnnotation = testMethod.getAnnotation(OverloadHandler.class);
		Method method;
		if (Objects.isNull(overloadHandlerAnnotation) || StringUtils.isEmpty(overloadHandlerAnnotation.target())) {
			String methodName = testMethod.getName();
			method = findMethod(methodName, testObj);
		} else {
			Class clazz = testObj.getClass();
			String methodName = overloadHandlerAnnotation.target();
			String[] params = overloadHandlerAnnotation.params().split(",");
			Class[] paramClasses = new Class[params.length];
			for (int i = 0; i < params.length; i++) {
				paramClasses[i] = Class.forName(params[i]);
			}
			method = clazz.getMethod(methodName, paramClasses);
		}
		method.setAccessible(true);
		return method;
	}

	private Object getTestedObj() {
		final Class testClass = getClass();
		// 测试Bean
		String beanName;
		final TestBean testBeanAnnotation = (TestBean) testClass.getAnnotation(TestBean.class);
		String testFacadeSimpleName = testClass.getSimpleName().replace("Test", "");
		if (Objects.nonNull(testBeanAnnotation) && !StringUtils.isEmpty(testBeanAnnotation.value())) {
			beanName = testBeanAnnotation.value();
		} else {
			beanName = StringUtil.lowerFirstCase(testFacadeSimpleName);
		}
		final Object testObj;
		try {
			testObj = applicationContext.getBean(beanName);
		} catch (BeansException e) {
			log.error("there is no test bean in the container with name:{}", beanName, e);
			throw new RuntimeException(e);
		}
		return testObj;
	}

	/**
	 * Initialize the handle object
	 */
	public void initTestUnitHandler() {
		this.testUnitHandler = new TestUnitHandler(frogRuntimeContext);
	}

	/**
	 * Initialize execution components and custom parameters
	 */
	public void initComponentsBeforeTest() {
		// custom para
		testUnitHandler.prepareUserPara();
	}

	/**
	 * Universal<code>beforeActsTest</code>，can be overridden in subclasses
	 *
	 * @param frogRuntimeContext
	 */
	public void beforeFrogTest(FrogRuntimeContext frogRuntimeContext) {

	}

	/**
	 * Universal<code>afterActsTest</code>, can be overridden in subclasses
	 *
	 * @param frogRuntimeContext
	 */
	public void afterFrogTest(FrogRuntimeContext frogRuntimeContext) {

	}

	/**
	 *
	 * @param clsz
	 * @param frogRuntimeContext
	 */
	public void invokeIFrogMethods(Class<? extends Annotation> clsz,
	                               FrogRuntimeContext frogRuntimeContext) {
		List<IFrogMethod> list = this.annotationMethods.get(clsz.getSimpleName());

		if (list == null) {
			return;
		}

		for (IFrogMethod method : list) {
			method.invoke(frogRuntimeContext);
		}
	}

	/**
	 * Set the {@link ApplicationContext} to be used by this test instance,
	 * provided via {@link ApplicationContextAware} semantics.
	 *
	 * @param applicationContext the ApplicationContext that this test runs in
	 */
	@Override
	public final void setApplicationContext(final ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

}