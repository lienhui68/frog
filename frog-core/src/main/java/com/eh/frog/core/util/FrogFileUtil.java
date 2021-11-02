/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.core.util;

import com.eh.frog.core.config.FrogConfig;
import com.eh.frog.core.enums.YamlSerializeMode;
import com.eh.frog.core.exception.FrogTestException;
import com.eh.frog.core.model.PrepareData;
import com.eh.frog.core.yaml.DateYamlConstructor;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
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

	private static final String CONFIG_FILE_PATH = "config/frog.yaml";

	private static final String DEFAULT_PATH = "/src/test/resources/";

	private static String DIR_PATH = System.getProperty("user.dir");

	/**
	 * Based on Test Bundle, Get relative path file from src/test/resources
	 *
	 * @param fileRelativePath
	 * @return
	 */
	public static File getTestResourceFile(String fileRelativePath) {
		String fileFullPath = DIR_PATH + DEFAULT_PATH + fileRelativePath;
		File file = new File(fileFullPath);
		return file;
	}

	public static LinkedHashMap<String, PrepareData> loadPrepareDataFromYaml(String folder, String fileName) {
		File yamlFile = null;
		InputStream is;
		try {
			String fileFullPath = folder + "/" + fileName + ".yaml";
			yamlFile = getTestResourceFile(fileFullPath);
			is = new FileInputStream(yamlFile);
			InputStreamReader reader = new InputStreamReader(is);
			Yaml yaml = new Yaml(new DateYamlConstructor());
			yaml.setBeanAccess(BeanAccess.FIELD);
			Iterator<Object> iterator = yaml.loadAll(reader).iterator();
			LinkedHashMap<String, PrepareData> rawData = (LinkedHashMap<String, PrepareData>) iterator.next();
			return rawData;
		} catch (FileNotFoundException e) {
			log.error("Can't find file:" + yamlFile.getAbsolutePath(), e);
			return null;
		} catch (IOException e) {
			log.error("IO error:" + yamlFile.getAbsolutePath(), e);
			return null;
		} catch (Exception e) {
			log.error("Wrong file format:" + yamlFile.getAbsolutePath(), e);
			return null;
		}
	}

	public static FrogConfig loadGlobalConfigFromYaml() {
		File yamlFile = null;
		InputStream is;
		try {
			yamlFile = getTestResourceFile(CONFIG_FILE_PATH);
			is = new FileInputStream(yamlFile);
			InputStreamReader reader = new InputStreamReader(is);
			Yaml yaml = new Yaml(new Constructor(FrogConfig.class));
			yaml.setBeanAccess(BeanAccess.FIELD);
			FrogConfig frogConfig = yaml.load(reader);
			return frogConfig;
		} catch (FileNotFoundException e) {
			log.error("Can't find file:" + yamlFile.getAbsolutePath(), e);
			return null;
		} catch (IOException e) {
			log.error("IO error:" + yamlFile.getAbsolutePath(), e);
			return null;
		} catch (Exception e) {
			log.error("Wrong file format:" + yamlFile.getAbsolutePath(), e);
			return null;
		}
	}

	/**
	 * @param path
	 * @param fileContent
	 * @return
	 */
	public static boolean writeNewFile(String path, String fileContent) {
		return writeFile(path, fileContent, null);
	}

	/**
	 * @param path
	 * @param fileContent
	 * @param mode
	 * @return
	 */
	public static boolean writeFile(String path, String fileContent, YamlSerializeMode mode) {
		File file = getTestResourceFile(path);
		try {
			if (file.exists()) {
				switch (mode) {
					case SKIP:
						log.debug("File already exists, skip");
						return true;
					case APPEND:
						log.debug("The file already exists, add a separator to continue writing");
						fileContent = "\n\n========================================================================\n\n"
								+ fileContent;
						break;
					case CREATE:
						log.debug("File already exists, delete and rewrite");
						file.delete();
						file.createNewFile();
						break;
					default:
						return false;
				}
			} else {
				file.createNewFile();
			}
			Writer writer = new OutputStreamWriter(new FileOutputStream(file, true), "UTF-8");
			writer.write(fileContent);
			writer.close();
			log.debug("written successfully");
			return true;
		} catch (Exception e) {
			log.error("written failed", e);
			return false;
		}
	}


	public static Properties getGlobalProperties() {
		//读取资源配置文件
		File file = getTestResourceFile("/config/frog-config.properties");
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

	public static LinkedHashMap<String, List<String>> getTableSelectKeys(String relativeDir) {
		//读取资源配置文件
		InputStream is;
		File yamlFile;
		try {
			yamlFile = getTestResourceFile(relativeDir + ".yaml");
			is = new FileInputStream(yamlFile);
			InputStreamReader reader = new InputStreamReader(is);
			Iterator<Object> iterator = new Yaml().loadAll(reader).iterator();
			LinkedHashMap<String, List<String>> rawData = (LinkedHashMap<String, List<String>>) iterator.next();
			return rawData;
		} catch (FileNotFoundException e) {
			log.warn("Can't find file:" + relativeDir);
			return null;
		} catch (IOException e) {
			log.warn("IO error:" + relativeDir);
			return null;
		} catch (Exception e) {
			log.warn("Wrong file format:" + relativeDir);
			return null;
		}
	}

}