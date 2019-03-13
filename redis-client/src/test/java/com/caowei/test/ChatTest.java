package com.caowei.test;

import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.test.context.junit4.SpringRunner;

import com.caowei.service.chat.ChatService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ChatTest {

	@Autowired
	ChatService chatService;
	
	//@Test
	public void sendMessage() throws Exception {
		String chat_id="1";
		String sender="bill";
		String message="大家好sfsaf，我是比尔，初来乍到！";
		chatService.sendMessage(chat_id, sender, message);
	}
	
	@Test
	public void reviceMessage() {
		chatService.receiveMessage("frank");
	}
	
//	@Autowired
//	StringRedisTemplate redisTemplate;
//	
//	@Test
//	public void demo() {
//		Set<TypedTuple<String>> set=
//		redisTemplate.opsForZSet().rangeWithScores("chat:1", 0, 0);
//		for (TypedTuple<String> typedTuple : set) {
//			System.out.println(typedTuple.getValue()+",score:"+typedTuple.getScore().longValue());
//		}   
//		
//		double score= redisTemplate.opsForZSet().score("chat:1", "frank");
//		System.out.println(score);
//	}
}
