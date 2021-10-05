package com.eh.frog.sample.base;


import org.apache.commons.lang.StringUtils;

import java.io.Serializable;

/**
 * Created by David Li on 2016/10/18.
 */
public class Response<T> implements Serializable {
	int status;

	String error;

	String msg;

	T data;

	private Response() {
	}

	public static Response success() {
		return success(null);
	}

	public static <T> Response<T> success(T t) {
		return create(BaseConstants.SC_OK, ReturnCodeEnum.OK.getCodeStr(), ReturnCodeEnum.OK.getMessage(), t);
	}

	public static <T> Response<T> error(Throwable t) {
		return create(BaseConstants.SC_FAIL, ReturnCodeEnum.FAIL.getCodeStr(), t.getMessage(), null);
	}

	public static <T> Response<T> error(String message) {
		return create(BaseConstants.SC_FAIL, StringUtils.EMPTY, message, null);
	}

	public static <T> Response<T> error(String error, String message) {
		return create(BaseConstants.SC_FAIL, error, message, null);
	}

	public static <T> Response<T> create(int status, String error, String message, T t) {
		Response response = new Response();
		response.setStatus(status);
		response.setError(error);
		response.setMsg(message);
		response.setData(t);
		return response;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "Response{" +
				"status=" + status +
				", error='" + error + '\'' +
				", msg='" + msg + '\'' +
				", data=" + data +
				'}';
	}
}
