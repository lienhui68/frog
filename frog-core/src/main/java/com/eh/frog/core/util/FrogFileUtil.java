/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.core.util;

import com.eh.frog.core.exception.FrogTestException;
import com.eh.frog.core.model.PrepareData;
import com.eh.frog.core.yaml.DateYamlConstructor;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeId;
import org.yaml.snakeyaml.nodes.ScalarNode;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author f90fd4n david
 * @version 1.0.0: FrogDataProvider.java, v 0.1 2021-09-15 4:10 下午 david Exp $$
 */
@Slf4j
public class FrogFileUtil {

	private static final String DEFAULT_PATH = "/src/test/resources/";

	private static String DIR_PATH = System.getProperty("user.dir");

	public static LinkedHashMap<String, PrepareData> loadFromYaml(String fileName) {
		File yamlFile = null;
		InputStream is;
		try {
			yamlFile = getTestResourceFile("/data/" + fileName + ".yaml");
			is = new FileInputStream(yamlFile);
			InputStreamReader reader = new InputStreamReader(is);
			Iterator<Object> iterator = new Yaml(new DateYamlConstructor()).loadAll(reader).iterator();
			LinkedHashMap<String, PrepareData> rawData = (LinkedHashMap<String, PrepareData>) iterator.next();
			return rawData;
		} catch (FileNotFoundException e) {
			log.warn("Can't find file" + yamlFile.getAbsolutePath());
			return null;
		} catch (IOException e) {
			log.warn("IO error" + yamlFile.getAbsolutePath());
			return null;
		} catch (Exception e) {
			log.error("Wrong file format" + yamlFile.getAbsolutePath(), e);
			return null;
		}
	}

	private static File getTestResourceFile(String fileRelativePath) {
		String fileFullPath = DIR_PATH + DEFAULT_PATH + fileRelativePath;
		File file = new File(fileFullPath);
		return file;
	}

	public static Properties getGlobalProperties() {
		//读取资源配置文件
		String fileFullPath = DIR_PATH + DEFAULT_PATH + "/config/frog-config.properties";
		File file = new File(fileFullPath);
		Properties prop;
		try {
			InputStream is = new FileInputStream(file);
			prop = new Properties();
			prop.load(is);
		} catch (Exception e) {
			throw new FrogTestException("read config file occurs error.");
		}
		return prop;
	}

	public static LinkedHashMap<String, List<String>> getTableSelectKeys() {
		//读取资源配置文件
		String fileFullPath = DIR_PATH + DEFAULT_PATH + "/config/table_select_key.yaml";

		File yamlFile = null;
		InputStream is;
		try {
			yamlFile = new File(fileFullPath);
			is = new FileInputStream(yamlFile);
			InputStreamReader reader = new InputStreamReader(is);
			Iterator<Object> iterator = new Yaml().loadAll(reader).iterator();
			LinkedHashMap<String, List<String>> rawData = (LinkedHashMap<String, List<String>>) iterator.next();
			return rawData;
		} catch (FileNotFoundException e) {
			log.warn("Can't find file" + yamlFile.getAbsolutePath());
			return null;
		} catch (IOException e) {
			log.warn("IO error" + yamlFile.getAbsolutePath());
			return null;
		} catch (Exception e) {
			log.warn("Wrong file format" + yamlFile.getAbsolutePath());
			return null;
		}
	}



}