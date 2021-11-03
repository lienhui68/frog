/**
 * ymm56.com Inc.
 * Copyright (c) 2013-2021 All Rights Reserved.
 */
package com.eh.frog.core.util;

/**
 * @author f90fd4n david
 * @version 1.0.0: StringUtil.java, v 0.1 2021-10-11 10:34 上午 david Exp $$
 */
public class StringUtil {
	//32为是char类型大小写的差数，-32是小写变大写，+32是大写变小写

	/**
	 * 首字母小写
	 *
	 * @param str
	 * @return
	 */
	public static String lowerFirstCase(String str) {
		char[] chars = str.toCharArray();
		//首字母小写方法，大写会变成小写，如果小写首字母会消失
		chars[0] += 32;
		return String.valueOf(chars);
	}

	/**
	 * 首字母大写
	 *
	 * @param str
	 * @return
	 */
	public static String upperFirstCase(String str) {
		char[] chars = str.toCharArray();
		//首字母小写方法，大写会变成小写，如果小写首字母会消失
		chars[0] -= 32;
		return String.valueOf(chars);
	}

	public static String buildMessage(String format, Object... params) {
		if (format == null) {
			throw new NullPointerException("format");
		}
		StringBuilder sb = new StringBuilder();
		final String delimiter = "{}"; //定界符
		int i = 0;
		if (params != null) {
			for (; ; ) {
				int tmpIndex = format.indexOf(delimiter);
				if (tmpIndex == -1) {//不存在赋值
					sb.append(format);
					break;
				} else {//存在则进行赋值拼接
					String str = format.substring(0, tmpIndex);
					format = format.substring(tmpIndex + 2);
					String valStr = params[i++].toString();
					sb.append(str).append(valStr);
				}
			}
		} else {//param为空时
			sb.append(format);
		}
		return sb.toString();
	}

	public static void main(String[] args) {
		System.out.println(buildMessage("插件:{}执行init出错", "Name"));
	}
}