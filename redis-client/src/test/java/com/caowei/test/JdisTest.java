package com.caowei.test;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@RunWith(SpringRunner.class)
@SpringBootTest
public class JdisTest {

	@Autowired
	JedisPool jedisPool;
	
	@Test
	public void demo() {
		//String[] queues= {"queue_frank","queue_bill"};
		Jedis jedis=jedisPool.getResource();
		System.out.println(jedis==null);
		List<String> list= jedis.brpop(0, "queue_frank","queue_bill","test");
		System.out.println(list);
		
		
	}
}
