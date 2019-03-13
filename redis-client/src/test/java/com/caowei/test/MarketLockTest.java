package com.caowei.test;

import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import com.caowei.service.market.MarketLockService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MarketLockTest {
	
	@Autowired
	MarketLockService marketLockService;
	
	//@Test
	public void purchase_with_lock_test() {
		String buyerid="27";  //买家
		String pid="ItemA";
		String sellerid="17";
		//long price=35;
		marketLockService.purchase_with_lock(buyerid, pid, sellerid);
	}
	
	@Autowired
	StringRedisTemplate redis;
	
	@Test
	public void demo_test() {
		System.out.println("==============");
		long value= redis.getExpire("lock:test", TimeUnit.MILLISECONDS);
		System.out.println(value);
	}

}
