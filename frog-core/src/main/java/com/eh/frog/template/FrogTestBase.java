/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.template;

import com.eh.frog.annotation.FrogTest;
import com.eh.frog.component.db.DBDataProcessor;
import com.eh.frog.component.event.EventContextHolder;
import com.eh.frog.component.handler.TestUnitHandler;
import com.eh.frog.component.mock.MockContextHolder;
import com.eh.frog.config.GlobalConfigurationHolder;
import com.eh.frog.context.FrogRuntimeContext;
import com.eh.frog.context.FrogRuntimeContextHolder;
import com.eh.frog.exception.FrogCheckException;
import com.eh.frog.exception.FrogTestException;
import com.eh.frog.model.PrepareData;
import com.eh.frog.util.FrogFileUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author f90fd4n david
 * @version 1.0.0: FrogTestBase.java, v 0.1 2021-09-15 12:49 上午 david Exp $$
 */
@Slf4j
public abstract class FrogTestBase extends AbstractJUnit4SpringContextTests {

	private String fullName;

	private String testPath;
	/**
	 * 测试数据文件名称
	 */
	private String testFileName;
	/**
	 * 测试类名称
	 */
	private String simpleClassName;
	/**
	 * 测试方法名称
	 */
	private String methodName;
	/**
	 * 测试对象
	 */
	private Object testObj;
	/**
	 * 测试对象的方法
	 */
	private Method testMethod;
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
	public void test() throws NoSuchMethodException {
		FrogTest frogTest = this.getClass().getDeclaredMethod("test", String.class, PrepareData.class).getAnnotation(FrogTest.class);
		testPath = frogTest.target();
		// 测试上下文
		initTestContext(testPath);
		// 初始化配置
		initConfiguration();
		String dsName = GlobalConfigurationHolder.getGlobalConfiguration().getProperty("datasource.bean.name");
		Assert.assertNotNull(dsName, "数据源名称未配置");
		// 初始化数据处理器
		if (dbDataProcessor == null) {
			dbDataProcessor = new DBDataProcessor(applicationContext.getBean(dsName, DataSource.class));
		}
		// 加载测试数据
		testFileName = frogTest.fileName();
		if (StringUtils.isEmpty(testFileName)) {
			testFileName = simpleClassName + "_" + methodName;
		}
		LinkedHashMap<String, PrepareData> prepareDataMap = FrogFileUtil.loadFromYaml(testFileName);
		// case过滤
		String[] selected = frogTest.selected();
		List<String> caseIds = Arrays.asList(selected).stream().map(s -> testFileName + "_" + s).collect(Collectors.toList());
		// 执行case
		prepareDataMap.forEach((caseId, prepareData) -> {
			if (CollectionUtils.isEmpty(caseIds) || caseIds.contains(caseId)) {
				runTest(caseId, prepareData);
			}
		});
	}

	private void initConfiguration() {
		// 全局配置
		GlobalConfigurationHolder.setGlobalConfiguration(FrogFileUtil.getGlobalProperties());
		// 表selectKeys
		GlobalConfigurationHolder.setSelectKeys(FrogFileUtil.getTableSelectKeys());
	}

	private void initTestContext(String testPath) {
		try {
			String[] ss = testPath.split("#");
			fullName = ss[0];
			String[] sss1 = ss[0].split("\\.");
			simpleClassName = sss1[sss1.length - 1];
			String[] sss2 = ss[1].split("\\(");
			methodName = sss2[0];
			String[] ssss = sss2[1].substring(0, sss2[1].length() - 1).split(",");
			Class<?>[] paramClasses = new Class[ssss.length];
			for (int i = 0; i < ssss.length; i++) {
				paramClasses[i] = Class.forName(ssss[i].trim());
			}
			// 方法
			testMethod = Class.forName(ss[0]).getDeclaredMethod(methodName, paramClasses);
			testMethod.setAccessible(true);
			// bean
			testObj = applicationContext.getBean(Class.forName(ss[0]));
		} catch (Exception e) {
			log.error("解析路径出错:{}", testPath, e);
		}
	}

	/**
	 * 测试方法
	 *
	 * @param caseId
	 * @param prepareData
	 */
	protected abstract void test(String caseId, PrepareData prepareData);

	public void runTest(String caseId, PrepareData prepareData) {
		log.info("=============================Start executing TestCase caseId:" + caseId + " "
				+ prepareData.getDesc() + "=================");
		initRuntimeContext(caseId, prepareData, testPath);
		initTestUnitHandler();
		try {
			initComponentsBeforeTest();
			// before all tests, the method will be executed
			beforeActsTest(frogRuntimeContext);
			process(frogRuntimeContext);
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
	 * @param testPath
	 */
	public void initRuntimeContext(String caseId, PrepareData prepareData, String testPath) {
		try {
			frogRuntimeContext = new FrogRuntimeContext(caseId, prepareData, testMethod, testObj, dbDataProcessor, applicationContext);
			FrogRuntimeContextHolder.setContext(frogRuntimeContext);
		} catch (Exception e) {
			log.error("解析路径出错:{}", testPath, e);
		}

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
		// custome para
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