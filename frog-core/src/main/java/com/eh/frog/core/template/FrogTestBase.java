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
import com.eh.frog.core.component.prepare.PrepareFillDataHolder;
import com.eh.frog.core.config.FrogConfig;
import com.eh.frog.core.config.GlobalConfigurationHolder;
import com.eh.frog.core.constants.FrogConfigConstants;
import com.eh.frog.core.context.FrogRuntimeContext;
import com.eh.frog.core.context.FrogRuntimeContextHolder;
import com.eh.frog.core.context.TestDataFilePathHolder;
import com.eh.frog.core.enums.YamlSerializeMode;
import com.eh.frog.core.exception.FrogCheckException;
import com.eh.frog.core.exception.FrogTestException;
import com.eh.frog.core.model.PrepareData;
import com.eh.frog.core.plugin.PersistencePlugin;
import com.eh.frog.core.util.ClassHelper;
import com.eh.frog.core.util.FrogFileUtil;
import com.eh.frog.core.util.StringUtil;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.platform.commons.util.AnnotationUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Tag;

import javax.sql.DataSource;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author f90fd4n david
 * @version 1.0.0: FrogTestBase.java, v 0.1 2021-09-15 12:49 上午 david Exp $$
 */
@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FrogTestBase implements ApplicationContextAware {

	/**
	 * case上下文
	 */
	private FrogRuntimeContext frogRuntimeContext;

	/**
	 * data processing
	 */
	public static DBDataProcessor dbDataProcessor;

	/**
	 * execute unit for case
	 */
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

	/**
	 * 持久化插件
	 */
	private List<PersistencePlugin> persistencePlugins;

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
			Set<Method> allMethod = ClassHelper.getAvailableMethods(this.getClass()).stream().filter(m -> AnnotationUtils.findAnnotation(m, FrogHook.class).isPresent()).collect(Collectors.toSet());
			annotationFactory.initAnnotationMethod(allMethod, this);
			//Load the initial persistence plugins
			loadInitialPersistencePlugin();
			//init plugin
			initPersistencePlugin();
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
		GlobalConfigurationHolder.setFrogConfigThreadLocal(frogConfig);
		// 全局配置
		frogConfig.getBaseConfig().ifPresent(GlobalConfigurationHolder::setGlobalConfiguration);
		// 表selectKeys
		final Map<String, List<String>> selectKeys = Maps.newHashMap();
		frogConfig.getTableQueryConfig().ifPresent(tableQueryConfig -> {
			tableQueryConfig.getCommonTableQueryConfig().ifPresent(commonTableQueryConfig -> selectKeys.put(FrogConfigConstants.FROG_VIRTUAL_COMMON_TABLE, commonTableQueryConfig));
			tableQueryConfig.getSpecialTableQueryConfig().ifPresent(specialTableQueryConfig -> selectKeys.putAll(specialTableQueryConfig));
		});
		// 扩展配置
		frogConfig.getExtensionConfig().ifPresent(GlobalConfigurationHolder::setExtensionConfig);
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
			String err = StringUtil.buildMessage("case数据校验失败,id:{},失败原因:{}", caseId, fe.getMessage());
			throw new AssertionError(err, fe);
		} catch (FrogTestException fe) {
			String err = StringUtil.buildMessage("case执行发生错误,id:{},错误原因:{}", caseId, fe.getMessage());
			throw new AssertionError(err, fe);
		} catch (Throwable t) {
			String err = StringUtil.buildMessage("case执行发生系统错误,id:{}", caseId);
			throw new AssertionError(err, t);
		} finally {
			try {
				// After all tests, the method will be executed
				afterFrogTest(frogRuntimeContext);
				// clean up thread variable
				EventContextHolder.clear();
				MockContextHolder.restore();
			} catch (FrogTestException fe) {
				String err = StringUtil.buildMessage("case执行发生错误,id:{},错误原因:{}", caseId, fe.getMessage());
				throw new AssertionError(err, fe);
			} catch (Throwable t) {
				String err = StringUtil.buildMessage("case执行发生系统错误,id:{}", caseId);
				throw new AssertionError(err, t);
			}
		}
	}

	/**
	 * @param frogRuntimeContext
	 */
	public void process(FrogRuntimeContext frogRuntimeContext) {
		// clear
		clear(frogRuntimeContext);

		try {
			// prepare
			prepare(frogRuntimeContext);
			// execute
			execute(frogRuntimeContext);
			if (GlobalConfigurationHolder.getFrogConfig().isEnablePrepareFill()) {
				// prepare fill
				log.info("==============>预跑反填, 填充预跑结果数据");
				prepareFill();
				log.info("==============>预跑反填, 清理结果数据缓存");
				PrepareFillDataHolder.clear();
			} else {
				// check
				check(frogRuntimeContext);
			}
		} finally {
			// 后置清理
			String postProcessCleanStr = GlobalConfigurationHolder.getGlobalConfiguration().get(FrogConfigConstants.POST_PROCESS_CLEAN);
			boolean postProcessClean = Boolean.parseBoolean(postProcessCleanStr);
			if (postProcessClean) {
				clear(frogRuntimeContext);
			}
		}
	}

	private void prepareFill() {
		PrepareData prepareData = PrepareFillDataHolder.getPrepareData();
		// 获取反填临时文件路径
		String path = org.apache.commons.lang.StringUtils.replace(TestDataFilePathHolder.getContext(), ".yaml", "_result.yaml");
		if (StringUtils.isEmpty(path)) {
			path = FrogConfigConstants.DEFAULT_PREPARE_FILL_TMP_FILE;
		}
		// 创建文件
		Yaml yaml = new Yaml();
		String content = yaml.dumpAs(prepareData, Tag.YAML, DumperOptions.FlowStyle.AUTO);
		FrogFileUtil.writeFile(path, content, YamlSerializeMode.CREATE);
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
		doPersistencePluginPrepare();

		invokeIFrogMethods(AfterPrepare.class, frogRuntimeContext);
		log.info("=============================[frog prepare end]=============================\r\n");
	}

	/**
	 * @param frogRuntimeContext
	 * @throws FrogTestException
	 */
	public void execute(FrogRuntimeContext frogRuntimeContext) throws FrogTestException {
		log.info("=============================[frog execute begin]=============================\r\n");
		invokeIFrogMethods(BeforeExecute.class, frogRuntimeContext);

		testUnitHandler.execute();

		invokeIFrogMethods(AfterExecute.class, frogRuntimeContext);
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
		doPersistencePluginCheck();

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
		doPersistencePluginClean();

		invokeIFrogMethods(AfterClean.class, frogRuntimeContext);

		log.info("=============================[frog clear end]=============================\r\n");
	}


	/**
	 * Initialize the Frog context
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
		String methodName;
		if (Objects.isNull(overloadHandlerAnnotation) || StringUtils.isEmpty(overloadHandlerAnnotation.target())) {
			methodName = testMethod.getName();
			method = findMethod(methodName, testObj);
		} else {
			Class clazz = testObj.getClass();
			methodName = overloadHandlerAnnotation.target();
			String[] params = overloadHandlerAnnotation.params().split(",");
			Class[] paramClasses = new Class[params.length];
			for (int i = 0; i < params.length; i++) {
				paramClasses[i] = Class.forName(params[i]);
			}
			method = clazz.getMethod(methodName, paramClasses);
		}
		if (Objects.isNull(method)) {
			throw new FrogTestException("method not exits by name:{},class:{}", methodName, testObj.getClass().getSimpleName());
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

	private void loadInitialPersistencePlugin() {
		persistencePlugins = Lists.newArrayList();
		final EnablePlugin enablePlugin = (EnablePlugin) this.getClass().getAnnotation(EnablePlugin.class);
		if (Objects.isNull(enablePlugin)) {
			return;
		}

		PluginSignature[] value = enablePlugin.value();
		if (Objects.isNull(value)) {
			return;
		}

		Arrays.stream(value).forEach(ps -> {
			if (PersistencePlugin.class.equals(ps.type())) {
				Class<?>[] plugins = ps.plugins();
				if (Objects.isNull(plugins)) {
					return;
				}
				Arrays.stream(plugins).forEach(p -> {
					try {
						persistencePlugins.add((PersistencePlugin) p.newInstance());
					} catch (Exception e) {
						String err = StringUtil.buildMessage("加载插件:{}出错", p.getClass().getSimpleName());
						throw new FrogTestException(err, e);
					}
				});
			}
		});
	}

	private void initPersistencePlugin() {
		persistencePlugins.forEach(p -> {
			try {
				Assertions.assertTrue(!StringUtils.isEmpty(p.getPluginSymbol()), StringUtil.buildMessage("插件标识不能为空,class:{}", p.getClass().getSimpleName()));
				final Map<String, Object>[] pluginConfig = new Map[]{Maps.newHashMap()};
				Optional<Map<String, Map<String, Object>>> extensionConfigOpt = GlobalConfigurationHolder.getExtensionConfig();
				extensionConfigOpt.ifPresent(extensionConfig -> {
					Map<String, Object> pluginConfigFromYaml = extensionConfig.get(p.getPluginSymbol());
					if (!CollectionUtils.isEmpty(pluginConfigFromYaml)) {
						pluginConfig[0] = pluginConfigFromYaml;
					}
				});
				log.info("=====>plugin:{} call init...", p.getPluginSymbol());
				p.init(pluginConfig[0]);
				log.info("=====>plugin:{} call init end...", p.getPluginSymbol());
			} catch (Throwable t) {
				String err = StringUtil.buildMessage("插件:{}执行init出错", p.getClass().getSimpleName());
				throw new FrogTestException(err, t);
			}
		});
	}

	private void doPersistencePluginPrepare() {
		persistencePlugins.forEach(p -> {
			try {
				Map<String, Object> extendParams = frogRuntimeContext.getPrepareData().getExtendParams();
				log.info("=====>plugin:{} call prepare...", p.getPluginSymbol());
				if (Objects.nonNull(extendParams)) {
					p.prepare(Optional.ofNullable(extendParams.get(p.getPluginSymbol())));
				} else {
					p.prepare(Optional.empty());
				}
				log.info("=====>plugin:{} call prepare end...", p.getPluginSymbol());
			} catch (Throwable t) {
				String err = StringUtil.buildMessage("插件:{}执行prepare出错", p.getClass().getSimpleName());
				throw new FrogTestException(err, t);
			}
		});
	}

	private void doPersistencePluginCheck() {
		persistencePlugins.forEach(p -> {
			try {
				Map<String, Object> extendParams = frogRuntimeContext.getPrepareData().getExtendParams();
				log.info("=====>plugin:{} call check...", p.getPluginSymbol());
				if (Objects.nonNull(extendParams)) {
					p.check(Optional.ofNullable(extendParams.get(p.getPluginSymbol())));
				} else {
					p.check(Optional.empty());
				}
				log.info("=====>plugin:{} call check end...", p.getPluginSymbol());
			} catch (AssertionError e) {
				throw new FrogCheckException("插件:{}执行check失败,失败信息:{}", p.getClass().getSimpleName(), e.getMessage());
			} catch (Throwable t) {
				String err = StringUtil.buildMessage("插件:{}执行check出错", p.getClass().getSimpleName());
				throw new FrogTestException(err, t);
			}
		});
	}

	private void doPersistencePluginClean() {
		persistencePlugins.forEach(p -> {
			try {
				Map<String, Object> extendParams = frogRuntimeContext.getPrepareData().getExtendParams();
				log.info("=====>plugin:{} call clean...", p.getPluginSymbol());
				if (Objects.nonNull(extendParams)) {
					p.clean(Optional.ofNullable(extendParams.get(p.getPluginSymbol())));
				} else {
					p.clean(Optional.empty());
				}
				log.info("=====>plugin:{} call clean end...", p.getPluginSymbol());
			} catch (Throwable t) {
				String err = StringUtil.buildMessage("插件:{}执行clean出错", p.getClass().getSimpleName());
				throw new FrogTestException(err, t);
			}
		});
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
	 * Universal<code>beforeFrogTest</code>，can be overridden in subclasses
	 *
	 * @param frogRuntimeContext
	 */
	public void beforeFrogTest(FrogRuntimeContext frogRuntimeContext) {

	}

	/**
	 * Universal<code>afterFrogTest</code>, can be overridden in subclasses
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

		list.sort(Comparator.comparing(IFrogMethod::getOrder));

		for (IFrogMethod method : list) {
			List<String> includes = method.getIncludeCaseIds();
			List<String> excludes = method.getExcludeCaseIds();
			if (CollectionUtils.isEmpty(includes) && CollectionUtils.isEmpty(excludes)) {
				method.invoke(frogRuntimeContext);
			} else {
				String[] ss = frogRuntimeContext.getCaseId().split("_");
				boolean anyMatch = includes.stream().filter(c -> !excludes.contains(c)).anyMatch(c -> c.equals(ss[ss.length - 1]));
				if (anyMatch) {
					method.invoke(frogRuntimeContext);
				}
			}
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