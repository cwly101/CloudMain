package com.caowei.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

import com.caowei.service.RedisReceiver;

/**
 * 消息侦听器配置（发布、订阅模式）
 * @author cwly1
 *
 */
@Configuration
public class RedisSubListenerConfig {
	
	@Bean
	public RedisMessageListenerContainer	 container(
			RedisConnectionFactory connectionFactory,
			MessageListenerAdapter listenerAdapter   //注入的即是下面的listenerAdapter()方法
			) {
		RedisMessageListenerContainer container=new RedisMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.addMessageListener(listenerAdapter, new PatternTopic("topic_chat"));
		return container;
	}
	
	@Bean
	public MessageListenerAdapter listenerAdapter() {
		return new MessageListenerAdapter(new RedisReceiver(), "receiveMessage");
	}
}


