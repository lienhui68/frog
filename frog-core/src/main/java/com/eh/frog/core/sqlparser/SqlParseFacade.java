/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.core.sqlparser;

import com.eh.frog.core.exception.FrogTestException;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 思路：将语句分块(start,body,end)，再将body切割
 *
 * @author f90fd4n david
 * @version 1.0.0: SingleSqlParserFactory.java, v 0.1 2021-11-05 2:14 下午 david Exp $$
 */
@Slf4j
public class SqlParseFacade {


	public final static String SQL_EOF = "ENDOFSQL";

	private final static String UPDATE_REGEX = "(update)(.+)(set)(.+)";
	private final static String INSERT_REGEX = "(insert into)(.+)(values)(.+)";

	/**
	 * update/insert 解析入口
	 *
	 * @param sql
	 * @return
	 */
	public static SqlParseResult route(String sql) {
		BaseSqlParseStrategy parser;
		String handledSql = prepareHandleSql(sql);
		if (contains(handledSql, UPDATE_REGEX)) {
			parser = new UpdateSqlParseStrategy(handledSql);
		} else if (contains(handledSql, INSERT_REGEX)) {
			parser = new InsertSqlParseStrategy(handledSql);
		} else {
			throw new FrogTestException("sql:{}不支持解析", sql);
		}

		return parser.parse();
	}

	/**
	 * 看word是否在lineText中存在，支持正则表达式
	 *
	 * @param sql:要解析的sql语句
	 * @param regExp:正则表达式
	 * @return
	 */
	private static boolean contains(String sql, String regExp) {
		Pattern pattern = Pattern.compile(regExp, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(sql);
		return matcher.find();
	}

	private static String prepareHandleSql(String sql) {
		sql = sql.trim()
				.toLowerCase()
				.replaceAll("\\s+", " ")
				.replace(";", "")
				.replaceAll("`", "");
		return appendSqlEOF(sql);
	}

	private static String appendSqlEOF(String sql) {
		return sql + SQL_EOF;
	}


}