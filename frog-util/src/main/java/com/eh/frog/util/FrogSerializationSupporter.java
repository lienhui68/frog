/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.util;

import com.eh.frog.core.config.FrogConfig;
import com.eh.frog.core.constants.FrogConfigConstants;
import com.eh.frog.core.enums.YamlSerializeMode;
import com.eh.frog.core.util.FrogFileUtil;
import com.google.common.collect.Maps;
import org.assertj.core.util.Lists;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Tag;

import java.io.StringWriter;
import java.util.List;
import java.util.Map;

/**
 * @author f90fd4n david
 * @version 1.0.0: FrogSerializationSupporter.java, v 0.1 2021-11-02 5:47 下午 david Exp $$
 */
public final class FrogSerializationSupporter {
	private FrogSerializationSupporter() {
	}

	public static void initConfigFile() {
		String filepath = "config/frog.yaml";
		FrogConfig frogConfig = new FrogConfig();
		// 基础配置
		Map<String, String> baseConfig = Maps.newHashMap();
		baseConfig.put(FrogConfigConstants.DATASOURCE_BEAN_NAME, "dataSource");
		baseConfig.put(FrogConfigConstants.MESSAGE_EVENT_POI, "execution(public * com.eh.frog.sample.mq.producer.*Sender.send*(..))");
		baseConfig.put(FrogConfigConstants.TEST_DATA_FOLDER, "data");
		baseConfig.put(FrogConfigConstants.PREPARE_RUN_BACK_FILL, "false");
		baseConfig.put(FrogConfigConstants.TEST_ONLY, "^T");
		// 表查询配置
		FrogConfig.TableQueryConfig tableQueryConfig = new FrogConfig.TableQueryConfig();
		// 通用表查询字段配置
		List<String> commonTableQueryConfig = Lists.newArrayList();
		commonTableQueryConfig.add("user_id");
		// 特殊表查询字段配置
		Map<String, List<String>> specialTableQueryConfig = Maps.newHashMap();
		specialTableQueryConfig.put("tbl_order", Lists.newArrayList("buyer_id"));
		// set
		frogConfig.setBaseConfig(baseConfig);
		tableQueryConfig.setCommonTableQueryConfig(commonTableQueryConfig);
		tableQueryConfig.setSpecialTableQueryConfig(specialTableQueryConfig);
		frogConfig.setTableQueryConfig(tableQueryConfig);

		Yaml yaml = new Yaml();
		String content = yaml.dumpAs(frogConfig, Tag.MAP, DumperOptions.FlowStyle.BLOCK);
		FrogFileUtil.writeFile(filepath, content, YamlSerializeMode.CREATE);
	}
}