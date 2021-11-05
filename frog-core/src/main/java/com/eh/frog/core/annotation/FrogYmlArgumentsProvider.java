/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.core.annotation;

import com.eh.frog.core.config.GlobalConfigurationHolder;
import com.eh.frog.core.constants.FrogConfigConstants;
import com.eh.frog.core.model.PrepareData;
import com.eh.frog.core.util.FrogFileUtil;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.support.AnnotationConsumer;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * @author f90fd4n david
 * @version 1.0.0: FrogYmlArgumentsProvider.java, v 0.1 2021-11-02 1:47 下午 david Exp $$
 */
@Slf4j
public class FrogYmlArgumentsProvider implements ArgumentsProvider, AnnotationConsumer<FrogTest> {
	private List<String> yamlFiles;

	@Override
	public void accept(FrogTest FrogTest) {
		this.yamlFiles = Lists.newArrayList(FrogTest.value());
	}

	@Override
	public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {
		try {
			Class<?> testClass = extensionContext.getRequiredTestClass();
			Method testMethod = extensionContext.getRequiredTestMethod();
			LinkedHashMap<String, PrepareData> prepareDatas = getTestData(testClass, testMethod.getName());
			if (CollectionUtils.isEmpty(prepareDatas)) {
				return Stream.empty();
			}
			List<Object[]> prepareDataList = Lists.newArrayList();
			String rexStr = GlobalConfigurationHolder.getGlobalConfiguration().get(FrogConfigConstants.TEST_ONLY);
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
			return prepareDataList.stream().map(this::toArguments);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private LinkedHashMap<String, PrepareData> getTestData(Class testClass, String testedMethodName) {
		LinkedHashMap<String, PrepareData> datas = new LinkedHashMap<>();
		String dataFolder = FrogConfigConstants.DEFAULT_TEST_DATA_FOLDER;
		String dataFolderFromConfig = GlobalConfigurationHolder.getGlobalConfiguration().get(FrogConfigConstants.TEST_DATA_FOLDER);
		if (!StringUtils.isEmpty(dataFolderFromConfig)) {
			dataFolder = dataFolderFromConfig;
		}
		if (CollectionUtils.isEmpty(yamlFiles)) {
			String testFacadeSimpleName = testClass.getSimpleName().replace("Test", "");
			// 加载测试数据
			String testFileName = testFacadeSimpleName + "_" + testedMethodName + ".yaml";
			datas = FrogFileUtil.loadPrepareDataFromYaml(dataFolder, testFileName, getClass().getClassLoader());
		} else {
			// 从指定yaml文件中提取用例
			for (String f : yamlFiles) {
				datas.putAll(FrogFileUtil.loadPrepareDataFromYaml(dataFolder, f, getClass().getClassLoader()));
			}
		}
		return datas;
	}

	private Arguments toArguments(Object item) {
		if (item instanceof Arguments) {
			return (Arguments) item;
		} else {
			return item instanceof Object[] ? Arguments.of((Object[]) item) : Arguments.of(new Object[]{item});
		}
	}
}