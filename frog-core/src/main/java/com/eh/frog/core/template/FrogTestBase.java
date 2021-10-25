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
import com.eh.frog.core.config.GlobalConfigurationHolder;
import com.eh.frog.core.context.FrogRuntimeContext;
import com.eh.frog.core.context.FrogRuntimeContextHolder;
import com.eh.frog.core.exception.FrogCheckException;
import com.eh.frog.core.exception.FrogTestException;
import com.eh.frog.core.model.PrepareData;
import com.eh.frog.core.util.ClassHelper;
import com.eh.frog.core.util.FrogFileUtil;
import com.eh.frog.core.util.StringUtil;
import com.tngtech.java.junit.dataprovider.DataProvider;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.junit.Assert;
import org.junit.Before;
import org.springframework.beans.BeansException;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

	/**
	 * caseId and data preparation
	 */
	protected static Map<String, PrepareData> prepareDatas = new HashMap<>();

	/**
	 * annotationMethods map
	 */
	public Map<String, List<IFrogMethod>> annotationMethods = new HashMap<>();

	/**
	 * annotationFactory
	 */
	protected FrogAnnotationFactory annotationFactory;

	@Before
	public void setUp() throws Exception {
		try {
			// 初始化配置
			initConfiguration();
			String dsName = GlobalConfigurationHolder.getGlobalConfiguration().getProperty("datasource.bean.name");
			Assert.assertNotNull(dsName, "数据源名称未配置");
			// 初始化数据处理器
			if (dbDataProcessor == null) {
				dbDataProcessor = new DBDataProcessor(applicationContext.getBean(dsName, DataSource.class));
			}
			//annotation init
			if (annotationFactory == null) {
				annotationFactory = new FrogAnnotationFactory(annotationMethods);
				Set<Method> allMethod = ClassHelper.getDeclaredAvailableMethods(this.getClass());
				annotationFactory.initAnnotationMethod(allMethod, this);
			}
		} catch (BeansException e) {
			log.error("Exception raised during setup process");
			throw new RuntimeException(e);
		}
	}

	public static void getTestData(String testFacadeSimpleName, String testedMethodName) {
		// 加载测试数据
		String testFileName = testFacadeSimpleName + "_" + testedMethodName;
		String dataFolder = "data";
		String dataFolderFromConfig = GlobalConfigurationHolder.getGlobalConfiguration().getProperty("data.model.dir");
		if (!StringUtils.isEmpty(dataFolderFromConfig)) {
			dataFolder = dataFolderFromConfig;
		}
		prepareDatas = FrogFileUtil.loadFromYaml(dataFolder, testFileName);
	}

	/**
	 * Frog DataProvider
	 *
	 * @param method
	 * @return
	 * @throws IOException
	 */
	@DataProvider
	public static Iterator<Object[]> dataProvider(Method method) throws IOException {
		try {
			testMethod = method;
			String testedMethodName = method.getName();
			final Class testClass = method.getDeclaringClass();
			String testFacadeSimpleName = testClass.getSimpleName().replace("Test", "");
			// 测试方法
			OverloadHandler overloadHandlerAnnotation = method.getAnnotation(OverloadHandler.class);
			getTestData(testFacadeSimpleName, testedMethodName);
			if (CollectionUtils.isEmpty(prepareDatas)) {
				return null;
			}
			List<Object[]> prepareDataList = Lists.newArrayList();
			String rexStr = GlobalConfigurationHolder.getGlobalConfiguration().getProperty("test_only");
			if (org.apache.commons.lang.StringUtils.isBlank(rexStr)) {
				rexStr = ".*";
			} else {
				rexStr = rexStr + ".*";
			}
			log.info("Run cases matching regex: [" + rexStr + "]");
			Pattern pattern = Pattern.compile(rexStr);
			// 排序
			TreeMap<String, PrepareData> treeMap;
			treeMap = new TreeMap<>(
					Comparator.naturalOrder());
			treeMap.putAll(prepareDatas);
			for (String caseId : treeMap.keySet()) {
				if (prepareDatas.get(caseId).getDesc() != null) {
					Matcher matcher = pattern.matcher(prepareDatas.get(caseId).getDesc());
					if (!matcher.find()) {
						log.info("[" + prepareDatas.get(caseId).getDesc()
								+ "] does not match [" + rexStr + "], skip it");
						continue;
					}
				}
				String desc = prepareDatas.get(caseId).getDesc();
				desc = (desc == null) ? "" : desc;
				Object[] args = new Object[]{caseId, desc, prepareDatas.get(caseId)};
				prepareDataList.add(args);
			}
			return prepareDataList.iterator();
		} catch (Exception e) {
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

	public void runTest(String caseId, PrepareData prepareData) {
		log.info("\n=============================Start executing, TestCase caseId:" + caseId + " "
				+ prepareData.getDesc() + "=================");
		try {
			initRuntimeContext(caseId, prepareData);
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
	 */
	public void initRuntimeContext(String caseId, PrepareData prepareData) throws Exception {
		Object testObj = getTestedObj();
		Method testMethod = getTestedMethod(testObj);

		frogRuntimeContext = new FrogRuntimeContext(caseId, prepareData, testMethod, testObj, dbDataProcessor, applicationContext);
		FrogRuntimeContextHolder.setContext(frogRuntimeContext);
	}

	private Method getTestedMethod(Object testObj) throws Exception {
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

}