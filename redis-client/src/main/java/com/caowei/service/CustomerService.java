package com.caowei.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;


/**
 * 消息消费者（消息队列）
 * @author cwly1
 *
 */
@Service
public class CustomerService implements Runnable {

	Jedis jedis;
	
	@Autowired
	public CustomerService(JedisPool jedisPool) {
		this.jedis=jedisPool.getResource();
	}
	
	@Override
	public void run() {
		
		while (true) {
			System.out.println("等待消息中...");
			System.out.println("message："+readMessage());		
		}
		
	}
	
	private String readMessage() {
		String message=null;
		List<String> list= jedis.brpop(0, "queue_frank","queue_bill","test");
		if(list.size()>1)
			message=list.get(1);
		return message;
	}

	
}
