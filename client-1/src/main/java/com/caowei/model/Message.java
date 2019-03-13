package com.caowei.model;

/**
 * 服务器响应的对象
 * @author Administrator
 *
 */
public class Message {

	/**
	 * 状态。可选值：ok、error
	 */
	private String state;
	/**
	 * 描述信息。ok时，可能为null，或者必要的返回信息。error时，出错原因描述。
	 */
	private String msg;
	
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	
}
