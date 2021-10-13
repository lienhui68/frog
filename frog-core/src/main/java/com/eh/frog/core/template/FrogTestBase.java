/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.core.template;

import com.eh.frog.core.annotation.TestBean;
import com.eh.frog.core.annotation.TestMethod;
import com.eh.frog.core.component.db.DBDataProcessor;
import com.eh.frog.core.component.event.EventContextHolder;
import com.eh.frog.core.component.handler.TestUnitHandler;
import com.eh.frog.core.component.mock.MockContextHolder;
import com.eh.frog.core.config.GlobalConfigurationHolder;
import com.eh.frog.core.context.FrogRuntimeContext;
import com.eh.frog.core.context.FrogRuntimeContextHolder;
import com.eh.frog.core.exception.FrogCheckException;
import com.eh.frog.core.exception.FrogTestException;
import com.eh.frog.core.model.PrepareData;
import com.eh.frog.core.util.FrogFileUtil;
import com.eh.frog.core.util.ReflectionUtil;
import com.eh.frog.core.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.BeansException;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author f90fd4n david
 * @version 1.0.0: FrogTestBase.java, v 0.1 2021-09-15 12:49 上午 david Exp $$
 */
@Slf4j
public abstract class FrogTestBase extends AbstractJUnit4SpringContextTests {

	/**
	 * case上下文
	 */
	private FrogRuntimeContext frogRuntimeContext;

	/**
	 * data processing
	 */
	public static DBDataProcessor dbDataProcessor;

	public TestUnitHandler testUnitHandler;


	@Test
	public void test() throws Exception {
		final Class testClass = this.getClass();
		// 测试Bean
		final TestBean testBeanAnnotation = (TestBean) testClass.getAnnotation(TestBean.class);
		String beanName = testBeanAnnotation.value();
		String testFacadeSimpleName = testClass.getSimpleName().replace("Test", "");
		if (StringUtils.isEmpty(beanName)) {
			beanName = StringUtil.lowerFirstCase(testFacadeSimpleName);
		}
		final Object testObj;
		try {
			testObj = applicationContext.getBean(beanName);
		} catch (BeansException e) {
			log.error("there is no test bean in the container with name:{}", beanName, e);
			return;
		}
		// 初始化配置
		initConfiguration();
		String dsName = GlobalConfigurationHolder.getGlobalConfiguration().getProperty("datasource.bean.name");
		Assert.assertNotNull(dsName, "数据源名称未配置");
		// 初始化数据处理器
		if (dbDataProcessor == null) {
			dbDataProcessor = new DBDataProcessor(applicationContext.getBean(dsName, DataSource.class));
		}
		ReflectionUtil.doWithMethods(this.getClass(), method -> {
			log.info("开始执行测试类:{},测试方法:{}", testClass.getSimpleName(), method.getName());
			// 测试方法
			TestMethod testMethodAnnotation = method.getAnnotation(TestMethod.class);
			Method testMethod;
			List<Method> methods = findMethods(method.getName(), testObj);
			if (methods.size() == 0) {
				Assert.assertNotNull(testMethodAnnotation.target());
				// 存在多个同名方法，需要进一步确认
				testMethod = testObj.getClass().getDeclaredMethod(testMethodAnnotation.target(), method.getParameterTypes());
			} else if (methods.size() == 1) {
				testMethod = methods.get(0);
			} else {
				log.error("No concrete bean method mapping to this test method，test class:{}, test method:{}", testClass.getSimpleName(), method.getName());
				return;
			}
			testMethod.setAccessible(true);
			// 加载测试数据
			String testFileName = testMethodAnnotation.fileName();
			if (StringUtils.isEmpty(testFileName)) {
				testFileName = testFacadeSimpleName + "_" + method.getName();
			}
			final String tf = testFileName;
			String dataFolder = "data";
			String dataFolderFromConfig = GlobalConfigurationHolder.getGlobalConfiguration().getProperty("data.model.dir");
			if (!StringUtils.isEmpty(dataFolderFromConfig)) {
				dataFolder = dataFolderFromConfig;
			}
			LinkedHashMap<String, PrepareData> prepareDataMap = FrogFileUtil.loadFromYaml(dataFolder, testFileName);
			if (Objects.isNull(prepareDataMap)) {
				return;
			}
			// case过滤
			List<String> selected = Arrays.asList(testMethodAnnotation.selected()).stream().map(s -> tf + "_" + s).collect(Collectors.toList());
			List<String> ignored = Arrays.asList(testMethodAnnotation.ignored()).stream().map(s -> tf + "_" + s).collect(Collectors.toList());
			// 执行case
			prepareDataMap.forEach((caseId, prepareData) -> {
				if ((CollectionUtils.isEmpty(selected) && !ignored.contains(caseId)) || selected.contains(caseId)) {
					runTest(caseId, prepareData, testObj, testMethod);
				}
			});
		}, (method) -> {
			TestMethod testMethod = method.getAnnotation(TestMethod.class);
			List<String> methodSelected = Arrays.asList(testBeanAnnotation.selected());
			List<String> ignoredMethods = Arrays.asList(testBeanAnnotation.ignored());
			if (testMethod != null) {
				if (CollectionUtils.isEmpty(methodSelected)) {
					if (!ignoredMethods.contains(method.getName())) {
						return true;
					}
				} else if (methodSelected.contains(method.getName())) {
					return true;
				}
			}
			return false;
		});
	}


	/**
	 * Obtain the tested method of the tested object
	 *
	 * @param methodName
	 * @param testedObj
	 * @return
	 */
	private List<Method> findMethods(String methodName, Object testedObj) {

		List<Method> result = Lists.newArrayList();
		Class clazz = testedObj.getClass();
		if (clazz != null) {
			while (clazz != null && !clazz.equals(Object.class)) {
				Method[] methods = clazz.getDeclaredMethods();
				// 过滤代理类,eg: XXX$$EnhancerBySpringCGLIB$$58cd8660
				if (!clazz.getName().contains("EnhancerBySpringCGLIB") && methods != null) {
					for (Method method : methods) {
						if (method != null
								&& org.apache.commons.lang.StringUtils.equalsIgnoreCase(method.getName(), methodName)) {
							result.add(method);
						}
					}
				}
				clazz = clazz.getSuperclass();
			}
		}
		return result;
	}

	private void initConfiguration() {
		// 全局配置
		GlobalConfigurationHolder.setGlobalConfiguration(FrogFileUtil.getGlobalProperties());
		// 表selectKeys
		String dbSelectKeyDir = "config/table_select_key";
		String dbSelectKeyDirFromConfig = GlobalConfigurationHolder.getGlobalConfiguration().getProperty("db.selectKey.dir");
		if (!StringUtils.isEmpty(dbSelectKeyDirFromConfig)) {
			dbSelectKeyDir = dbSelectKeyDirFromConfig;
		}
		GlobalConfigurationHolder.setSelectKeys(FrogFileUtil.getTableSelectKeys(dbSelectKeyDir));
	}

	public void runTest(String caseId, PrepareData prepareData, Object testObj, Method testMethod) {
		log.info("\n=============================Start executing, TestCase caseId:" + caseId + " "
				+ prepareData.getDesc() + "=================");
		initRuntimeContext(caseId, prepareData, testObj, testMethod);
		initTestUnitHandler();
		try {
			initComponentsBeforeTest();
			// before all tests, the method will be executed
			beforeActsTest(frogRuntimeContext);
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
				afterActsTest(frogRuntimeContext);
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

		testUnitHandler.prepareDepData();
		testUnitHandler.prepareMockData();
		testUnitHandler.prepareConfigData();

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

		testUnitHandler.checkException();
		testUnitHandler.checkExpectDbData();
		testUnitHandler.checkExpectEvent();
		testUnitHandler.checkExpectResult();

		log.info("=============================[frog check end]=============================\r\n");
	}

	/**
	 * @param frogRuntimeContext
	 * @throws FrogTestException
	 */
	public void clear(FrogRuntimeContext frogRuntimeContext) throws FrogTestException {
		log.info("=============================[frog clear begin]=============================\r\n");

		testUnitHandler.clearDepData();
		testUnitHandler.clearExpectDBData();

		log.info("=============================[frog clear end]=============================\r\n");
	}


	/**
	 * Initialize the ACTS context
	 *
	 * @param caseId
	 * @param prepareData
	 */
	public void initRuntimeContext(String caseId, PrepareData prepareData, Object testObj, Method testMethod) {
		frogRuntimeContext = new FrogRuntimeContext(caseId, prepareData, testMethod, testObj, dbDataProcessor, applicationContext);
		FrogRuntimeContextHolder.setContext(frogRuntimeContext);
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
	public void beforeActsTest(FrogRuntimeContext frogRuntimeContext) {

	}

	/**
	 * Universal<code>afterActsTest</code>, can be overridden in subclasses
	 *
	 * @param frogRuntimeContext
	 */
	public void afterActsTest(FrogRuntimeContext frogRuntimeContext) {

	}

}