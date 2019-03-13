package com.caowei.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;

import com.alibaba.fastjson.JSONObject;
import com.caowei.service.email.EmailSender;

public class SoldEmailSenderImpl extends EmailSender {
	
	public SoldEmailSenderImpl(JSONObject jsonObject, StringRedisTemplate redisTemplate, JavaMailSender mailSender) {
		super(jsonObject, redisTemplate, mailSender);
	}

	@Override
	public Map<String, String> emailContent() {
		String sellerid=jsonObject.getString("sellerid");
		String product=jsonObject.getString("pid");   //系统目前只有商品编号，没有商品名称。故，目前假设商品编号和商品名称是同一个。
		String price=jsonObject.getString("price");
		long millsen=Long.parseLong(jsonObject.getString("date").toString());
		String sellername=redisTemplate.opsForHash().get("users:"+sellerid, "name").toString();	
		String time=getTimeString(millsen);  //当前时间的字符串
		String content=String.format("亲爱的%s,您在拍卖行寄售的：%s,已售出，成交价格：%s，成交时间：%s，货款已打入您的账号，请登录游戏查看", 
				sellername,product,price,time);
		String sendTo=jsonObject.get("email").toString();
		String subject="主题：Springboot发送测试邮件";
		
		Map<String, String> map=new HashMap<String,String>();
		map.put("sendTo", sendTo);
		map.put("subject", subject);
		map.put("content", content);
		System.out.println("邮件内容："+content);
		return map;
	}   
}
