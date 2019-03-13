package com.caowei.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;

import com.alibaba.fastjson.JSONObject;
import com.caowei.service.email.EmailSender;

public class NoticeEmailSenderImpl extends EmailSender {

	public NoticeEmailSenderImpl(JSONObject jsonObject, StringRedisTemplate redisTemplate, JavaMailSender mailSender) {
		super(jsonObject, redisTemplate, mailSender);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Map<String, String> emailContent() {
		
		String content=jsonObject.getString("content").toString();
		String sendTo=jsonObject.get("email").toString();
		String subject="主题：我编写的Springboot发送的通知邮件";
		
		Map<String, String> map=new HashMap<String,String>();
		map.put("sendTo", sendTo);
		map.put("subject", subject);
		map.put("content", content);
		System.out.println("Notice："+content+",subject："+subject);
		return map;
	}

}
