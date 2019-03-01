package com.dangdang.reader.request;

import java.io.Serializable;

/**
 * Created by liuboyu on 2014/12/10.
 */
public class ResultExpCode implements Serializable{

	public int responseCode;
	public String statusCode;
	public String errorCode;
	public String errorMessage;

	public boolean getResultStatus() {
		return "0".equals(statusCode);
	}

	public String getResultErrorCode() {
		return errorCode;
	}

	public String getResultErrorMessage() {
		return errorMessage;
	}

	public int getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	@Override
	public String toString() {
		StringBuilder buff = new StringBuilder("  responseCode[");
		buff.append(responseCode);
		buff.append("], statusCode[");
		buff.append(statusCode);
		buff.append("], errorCode[");
		buff.append(errorCode);
		buff.append("], errorMessage[");
		buff.append(errorMessage);
		buff.append("]");

		return buff.toString();
	}

	/**
	 * 没有网络
	 */
	public final static String ERRORCODE_NONET = "9998";

	/**
	 * 请求超时
	 */
	public final static String ERRORCODE_TIME_OUT = "408";
	
	/**
	 * 意见反馈内容为空 
	 */
	public final static String ERRORCODE_6 = "10006";

}
