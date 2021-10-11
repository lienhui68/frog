/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.core.util;

import com.eh.frog.core.exception.FrogTestException;
import com.eh.frog.core.model.PrepareData;
import com.eh.frog.core.yaml.DateYamlConstructor;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.introspector.BeanAccess;

import java.io.*;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Properties;

/**
 * @author f90fd4n david
 * @version 1.0.0: FrogDataProvider.java, v 0.1 2021-09-15 4:10 下午 david Exp $$
 */
@Slf4j
public class FrogFileUtil {


	public static LinkedHashMap<String, PrepareData> loadFromYaml(String rootFolder, String fileName) {
		File yamlFile = null;
		InputStream is;
		try {
			String fileFullPath = rootFolder + "/data/" + fileName + ".yaml";
			yamlFile = new File(fileFullPath);
			is = new FileInputStream(yamlFile);
			InputStreamReader reader = new InputStreamReader(is);
			Yaml yaml = new Yaml(new DateYamlConstructor());
			yaml.setBeanAccess(BeanAccess.FIELD);
			Iterator<Object> iterator = yaml.loadAll(reader).iterator();
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

	public static Properties getGlobalProperties(String rootFolder) {
		//读取资源配置文件
		String fileFullPath = rootFolder + "/config/frog-config.properties";
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

	public static LinkedHashMap<String, List<String>> getTableSelectKeys(String rootFolder) {
		//读取资源配置文件
		String fileFullPath = rootFolder + "/config/table_select_key.yaml";

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