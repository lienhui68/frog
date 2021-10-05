package com.eh.frog.sample.base;

/**
 * Created by David Li on 2016/10/18.
 */
public enum ReturnCodeEnum {
	OK(200, "服务调用成功"),
	FAIL(4000006, "系统内部错误");

	int code;

	String message;

	ReturnCodeEnum(int code, String message) {
		this.code = code;
		this.message = message;
	}

	public String getCodeStr() {
		return String.valueOf(code);
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return this.message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
