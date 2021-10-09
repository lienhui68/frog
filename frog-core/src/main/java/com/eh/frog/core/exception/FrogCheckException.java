/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.eh.frog.core.exception;

/**
 * Test abnormal base class, exception declaration in the framework must inherit this interface
 *
 * @author dasong.jds
 * @version $Id: ActsException.java, v 0.1 2015年4月19日 下午9:07:58 dasong.jds Exp $
 */
public class FrogCheckException extends RuntimeException {

	private static final long serialVersionUID = 4670114874512893276L;

	/**
	 * <code>ActsException</code> Constructor
	 */
	public FrogCheckException() {
		super();
	}

	/**
	 * <code>ActsException</code> Constructor
	 *
	 * @param message exception description
	 */
	public FrogCheckException(String message) {
		super(message);
	}

	/**
	 * <code>ActsException</code> Constructor
	 *
	 * @param cause exception reason
	 */
	public FrogCheckException(Throwable cause) {
		super(cause);
	}

	/**
	 * <code>ActsException</code> Constructor
	 *
	 * @param message exception description
	 * @param cause   exception reason
	 */
	public FrogCheckException(String message, Throwable cause) {
		super(message, cause);
	}

	public FrogCheckException(String format, Object... params) {
		this(buildMessage(format, params));
	}

	private static String buildMessage(String format, Object... params) {
		if (format == null) {
			throw new NullPointerException("format");
		}
		StringBuilder sb = new StringBuilder();
		final String delimiter = "{}"; //定界符
		int cnt = 0; //括号出现的计数值
		if (params != null) {
			for (int i = 0; i < params.length; i++) {
				int tmpIndex = format.indexOf(delimiter);
				if (tmpIndex == -1) {//不存在赋值
					if (cnt == 0) {
						sb.append(format);
					}
					break;
				} else {//存在则进行赋值拼接
					String str = format.substring(0, tmpIndex);
					format = format.substring(tmpIndex + 2);
					String valStr = params[i].toString();
					sb.append(str)
							.append(valStr);
					cnt++;

				}
			}
		} else {//param为空时
			sb.append(format);
		}
		return sb.toString();
	}

	public static void main(String[] args) {
		System.out.println(buildMessage("Hello{}, world{}", 1, 2));
	}

}
