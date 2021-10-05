/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.model;

import com.eh.frog.exception.FrogTestException;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

import java.util.Optional;

/**
 * @author f90fd4n david
 * @version 1.0.0: FieldFlag.java, v 0.1 2021-09-29 11:00 上午 david Exp $$
 */
@Data
@AllArgsConstructor
public class FieldFlag {
	public static final String DATE_FLAG = "D";
	public static final String REGEX_FLAG = "R";

	private String flag;
	private String val;

	public static Optional<FieldFlag> of(String field) {
		try {
			if (field.startsWith(DATE_FLAG)) {
				return Optional.of(new FieldFlag(DATE_FLAG, StringUtils.substringBetween(field, "\\(", "\\)")));
			}
			if (field.startsWith(REGEX_FLAG)) {
				return Optional.of(new FieldFlag(REGEX_FLAG, StringUtils.substringBetween(field, "\\(", "\\)")));
			}
		} catch (Exception e) {
			throw new FrogTestException("字段解析出错:" + field, e);
		}
		return Optional.empty();

	}
}