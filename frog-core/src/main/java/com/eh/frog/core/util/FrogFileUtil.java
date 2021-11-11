/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.core.util;

import com.eh.frog.core.config.FrogConfig;
import com.eh.frog.core.config.GlobalConfigurationHolder;
import com.eh.frog.core.context.TestDataFilePathHolder;
import com.eh.frog.core.enums.YamlSerializeMode;
import com.eh.frog.core.exception.FrogTestException;
import com.eh.frog.core.model.PrepareData;
import com.eh.frog.core.yaml.DateYamlConstructor;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.introspector.BeanAccess;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Properties;

import static com.eh.frog.core.constants.FrogConfigConstants.TEST_DATA_FOLDER;
import static java.util.stream.Collectors.toList;

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

	/**
	 * @param fileRelativePath classpath相对路径
	 * @param filename         文件名，不含后缀
	 * @return
	 */
	public static File walkTestResourceFile(String fileRelativePath, String filename, ClassLoader classLoader) throws URISyntaxException, IOException {
		Path configFilePath = Paths.get(classLoader.getResource(fileRelativePath).toURI());

		List<Path> searchResult = Files.walk(configFilePath)
				.filter(s -> s.endsWith(filename))
				.collect(toList());
		if (searchResult.size() < 1) {
			throw new FrogTestException("{}路径下不存在文件:{}", fileRelativePath, filename);
		}
		if (searchResult.size() > 1) {
			log.warn("{}路径下文件名:{}存在多个", fileRelativePath, filename);
		}
		return searchResult.get(0).toFile();
	}

	public static LinkedHashMap<String, PrepareData> loadPrepareDataFromYaml(String folder, String fileName, ClassLoader classLoader) {
		File yamlFile;
		InputStream is;
		try {
			yamlFile = walkTestResourceFile(folder, fileName, classLoader);
			// 记录文件路径
			String relativePath = yamlFile.getAbsolutePath().substring(yamlFile.getAbsolutePath().indexOf(GlobalConfigurationHolder.getGlobalConfiguration().get(TEST_DATA_FOLDER)));
			TestDataFilePathHolder.setContext(relativePath);
			is = new FileInputStream(yamlFile);
			InputStreamReader reader = new InputStreamReader(is);
			Yaml yaml = new Yaml(new DateYamlConstructor());
			yaml.setBeanAccess(BeanAccess.FIELD);
			Iterator<Object> iterator = yaml.loadAll(reader).iterator();
			LinkedHashMap<String, PrepareData> rawData = (LinkedHashMap<String, PrepareData>) iterator.next();
			return rawData;
		} catch (FileNotFoundException e) {
			String err = StringUtil.buildMessage("Can't find file, folder:{}, filename:{}", folder, fileName);
			throw new FrogTestException(err, e);
		} catch (IOException e) {
			String err = StringUtil.buildMessage("IO error, folder:{}, filename:{}", folder, fileName);
			throw new FrogTestException(err, e);
		} catch (Exception e) {
			String err = StringUtil.buildMessage("Wrong file format, folder:{}, filename:{}", folder, fileName);
			throw new FrogTestException(err, e);
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
			String err = StringUtil.buildMessage("Can't find file, filename:{}", CONFIG_FILE_PATH);
			throw new FrogTestException(err, e);
		} catch (IOException e) {
			String err = StringUtil.buildMessage("IO error, filename:{}", CONFIG_FILE_PATH);
			throw new FrogTestException(err, e);
		} catch (Exception e) {
			String err = StringUtil.buildMessage("Wrong file format, filename:{}", CONFIG_FILE_PATH);
			throw new FrogTestException(err, e);
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

	public static void main(String[] args) {

	}

}