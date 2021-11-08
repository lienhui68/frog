/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.core.sqlparser;

import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Sql语句片段
 *
 * @author f90fd4n david
 * @version 1.0.0: SqlSegment.java, v 0.1 2021-11-05 2:12 下午 david Exp $$
 */
@Data
public class SqlParseSegment {
	private static final String Crlf = "|";

	/**
	 * Sql语句片段开头部分
	 */
	private String start;

	/**
	 * Sql语句片段中间部分
	 */
	private String body;

	/**
	 * Sql语句片段结束部分
	 */
	private String end;

	/**
	 * 用于分割中间部分的正则表达式
	 */
	private String bodySplitPattern;

	/**
	 * 表示片段的正则表达式
	 */
	private String segmentRegExp;


	/**
	 * 分割后的Body小片段
	 */
	private List<String> bodyPieces;

	/**
	 * constructor
	 *
	 * @param segmentRegExp    表示这个Sql片段的正则表达式
	 * @param bodySplitPattern 用于分割body的正则表达式
	 */
	public SqlParseSegment(String segmentRegExp, String bodySplitPattern) {
		start = "";
		body = "";
		end = "";
		this.segmentRegExp = segmentRegExp;
		this.bodySplitPattern = bodySplitPattern;
		this.bodyPieces = new ArrayList<>();

	}

	/** */
	/**
	 * 　* 从sql中查找符合segmentRegExp的部分，并赋值到start,body,end等三个属性中
	 * 　* @param sql
	 */
	public void parse(String sql) {
		Pattern pattern = Pattern.compile(segmentRegExp, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(sql);
		while (matcher.find()) {
			start = matcher.group(1);
			body = matcher.group(2);
			end = matcher.group(3);
			parseBody();

		}
	}

	/**
	 * 解析body部分
	 */
	private void parseBody() {
		// 先清除掉前后空格
		body = body.trim();
		// 添加
		String[] ss = body.split(bodySplitPattern);
		Arrays.stream(ss).forEach(s -> {
			bodyPieces.add(s.trim());
		});
	}

}