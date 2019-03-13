package com.caowei.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.caowei.service.email.EmailQueueService;

@Service
public class SoldTypeEmailQueueServiceImpl implements EmailQueueService {

	@Autowired
	StringRedisTemplate redisTemplate;
	@Autowired
	JavaMailSender mailSender;
	
	@Override
	public void addEmailToQueue(String queuename,String jsonString) {
		//System.out.println("新邮件json字符串："+jsonString);
		redisTemplate.opsForList().rightPush(queuename, jsonString);		
	}
}
