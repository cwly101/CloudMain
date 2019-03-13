package com.caowei.service;

import org.springframework.stereotype.Service;

/**
 * 消息接收器（发布、订阅模式）
 * @author cwly1
 *
 */
@Service
public class RedisReceiver {
	public void receiveMessage(String message) {
		System.out.println("Received:"+message);
	}
}
