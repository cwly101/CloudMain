package com.caowei.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 发布消息控制器（订阅、发布模式）
 * @author cwly1
 *
 */
@RestController
public class RedisPublishController {

	StringRedisTemplate redisTemplate;
	
	public long id=33;
	
	@Autowired
	public RedisPublishController(StringRedisTemplate redisTemplate) {
		this.redisTemplate=redisTemplate;
	}
	
	@GetMapping(value="/publish")
	public String sendMessage(@RequestParam("msg") String msg) {
		System.out.println("send message...");
		//redisTemplate.convertAndSend("topic_chat", "发布一条消息...");
		return "ok";
	}
	
	@GetMapping(value="/aop")
	public String aopDemo() throws Exception {
		System.out.println("=======aop demo=======");
		
//		try {
//			throw new Exception("aop手动抛异常，测试Aop 异常捕获");
//		} catch (Exception e) {
//			System.out.println("异常发生了....");
//			return "500";
//		}
		throw new Exception("aop手动抛异常，测试Aop 异常捕获");
		//return "aop";
	}
}
