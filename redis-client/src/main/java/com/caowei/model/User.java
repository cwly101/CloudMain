package com.caowei.model;

import com.alibaba.fastjson.JSON;

public class User {

	private String uid;
	private String name;
	private long funds;   //资金（拥有的钱)
	private String email;
	
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public long getFunds() {
		return funds;
	}
	public void setFunds(long funds) {
		this.funds = funds;
	}
	
	public String getMail() {
		return email;
	}
	public void setMail(String email) {
		this.email = email;
	}
	@Override
	public String toString() {
		// 两种方式等同
		return String.format("{\"uid\":\"%s\",\"name\": \"%s\",\"funds\": \"%s\",\"email\":\"%s\"}", uid,name,funds,email);  //完全返回字符串的方式
		//return JSON.toJSONString(this);   //注：属性是什么类型，返回的json中对应的属性即是什么类型。
	}
}
