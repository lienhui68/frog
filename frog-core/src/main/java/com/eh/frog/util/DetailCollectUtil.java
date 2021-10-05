/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author f90fd4n david
 * @version 1.0.0: DetailCollectUtil.java, v 0.1 2021-09-16 3:39 下午 david Exp $$
 */
@Slf4j
public class DetailCollectUtil {
	private static StringBuffer sb = new StringBuffer();

	/**
	 * Append detail.
	 *
	 * @param content the content
	 */
	public static void appendDetail(String content) {
		sb.append(content);
		sb.append("\r\n");
	}

	/**
	 * Append and log.
	 *
	 * @param content the content
	 */
	public static void appendAndLog(String content) {
		log.info(content);
		sb.append(content);
		sb.append("\r\n");
	}
	/**
	 * Buffer to bytes byte [ ].
	 *
	 * @return the byte [ ]
	 */
	public static byte[] bufferToBytes() {
		return sb.toString().getBytes();
	}

	/**
	 * Clear buffer.
	 */
	public static void clearBuffer() {
		sb = new StringBuffer();
	}

	/**
	 * Save buffer.
	 *
	 * @param logPath the log path
	 */
	public static void saveBuffer(String logPath) {
		try {
			if (StringUtils.isEmpty(logPath)) {

				log.debug("logpath is empty, skip");
				return;
			}

			logPath = StringUtils.replace(logPath, "yaml", "log");
			File caseDetail = new File(logPath);
			FileOutputStream fop = new FileOutputStream(caseDetail);
			byte[] contentInBytes = bufferToBytes();
			clearBuffer();
			fop.write(contentInBytes);
			fop.flush();
			fop.close();
		} catch (FileNotFoundException e) {

		} catch (IOException e) {

		}
	}
}