package com.caowei.test;

//import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

//import com.alibaba.fastjson.JSON;
import com.caowei.model.User;
import com.caowei.service.market.MarketService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MarketTest {

	@Autowired
	MarketService marketService;
	
	//@Test
	public void saleUserArticleTest() throws Exception {
		System.out.println("---test---");
		marketService.saleUserArticle("itemO", 240, "27");
	}
	
	@Autowired
	StringRedisTemplate stringRedisTemplate;
	
	@Test
	public void addHashTest() {
		
//		String jsonString=stringRedisTemplate.opsForHash().get("users:","27").toString();
//		System.out.println(JSON.parseObject(jsonString).get("funds"));
		
//		System.out.println(jsonString);
//		User user= JSON.parseObject(jsonString).toJavaObject(User.class);
//		System.out.println("funds:"+user.getFunds());
		
//		boolean isExist= stringRedisTemplate.hasKey("users:");
//		
//		System.out.println(isExist);
//		System.out.println(stringRedisTemplate.opsForHash().hasKey("users:", "27"));
		
//		List<Object> list= stringRedisTemplate.opsForHash().values("users:");
//		System.out.println(list.size());
		
		Map<String, String> map=new HashMap<String, String>();
		User user=new User();
		user.setUid("17");
		user.setName("frank");
		user.setFunds(45);
		map.put("name", user.getName());
		map.put("funds", String.valueOf(user.getFunds()));
		stringRedisTemplate.opsForHash().putAll("users:"+user.getUid(), map);
		
		map.clear();
		
		User user2=new User();
		user2.setUid("27");
		user2.setName("bill");
		user2.setFunds(125);
		map.put("name", user2.getName());
		map.put("funds", String.valueOf(user2.getFunds()));		
		stringRedisTemplate.opsForHash().putAll("users:"+user2.getUid(), map);
		
		map.clear();
		/**
		 * hash中两种存储对象的方式均可。但是各有利弊。
		 * 方式1：将一个对象存储为一个hash，对象中所有的属性与值，作为key和value存储。
		 * 优点：更新某个属性的值方便。watch监控方便。
		 * 缺点：初始化时相对较慢，得一条一条的添加的缓存，与redis交互N次，而且是在已经使用putAll()函数的情况下。使用put交互更多。
		 * 方式2：将所有对象存储为一个hash, 将一个对象转换为一个json字符串，key通常为对象编号，value为对象的json字符串。
		 * 优点：初始化相对较快，调用putAll()一次交互添加完成。
		 * 缺点：更新某个值很麻烦，得将json字符串取出，转成对象，更新某个值完成后，再转换成json，将整个json重新写入redis。不能监控
		 * 某个单条对象，因为所有对象存储一个hash中。watch不支持监控hash中个key.
		 * 
		 * 建议：我倾向于方式1。方式1缺点可以避免，某个用户登录后，在向redis中增加该用户，而非系统启动时就添加所有用户。
		 */
		
//		System.out.println(map);	
//		stringRedisTemplate.opsForHash().putAll("users:", map);
	}
    
	@SuppressWarnings("rawtypes")
	//@Test
	public void test() throws InterruptedException {
//		long funds=Long.parseLong(stringRedisTemplate.opsForHash().get("users:27", "funds").toString());
//		System.out.println(funds);

//		stringRedisTemplate.opsForHash().increment("users:27", "funds", 600);  //用户27充值600		
//		marketService.buyArticle("27", "itemN-17", "17", 240);  //购买itemN商品
		
		//不使用redis流水线的操作
//		Long time = System.currentTimeMillis();
//		for (int i = 0; i < 10000; i++) {
//			stringRedisTemplate.opsForValue().increment("pipline", 1);
//		}
//		System.out.println("耗时：" + (System.currentTimeMillis() - time));
//		time = System.currentTimeMillis();
		
		
		//流水线的两种方式：
		
		/**
		 * 方式2：executePipelined
		 * 无返回值。执行效率略高于方式1。
		 */
		Long time = System.currentTimeMillis();		
		List<Object> list2=
		stringRedisTemplate.executePipelined(new RedisCallback() {

			/**
			 * RedisConnection是原生的redis命令方式。如：zadd,sadd
			 */
			@Override
			public Object doInRedis(RedisConnection connection) throws DataAccessException {
				stringRedisTemplate.opsForValue().increment("pipline", 1);
				stringRedisTemplate.opsForValue().setIfAbsent("pipline2", "967");
				return null;
			}
		});
		System.out.println("RedisCallback耗时：" + (System.currentTimeMillis() - time));
		System.out.println(list2);
		
		/**
		 * 方式1：SessionCallback
		 * 有返回值，流水线中执行的每条redis请求对应的返回值以List<Object>对象方式返回。
		 */
		Long time2 = System.currentTimeMillis();
		List<Object> list3=
		stringRedisTemplate.executePipelined(new SessionCallback() {

			/**
			 * RedisOperations是redisTemplate模板方式。
			 */
			@SuppressWarnings("unchecked")
			@Override
			public Object execute(RedisOperations operations) throws DataAccessException {
				operations.opsForValue().increment("pipline", 1);
				operations.opsForValue().setIfAbsent("pipline2", "967");
				return null;
			}
		});
		System.out.println("SessionCallback耗时：" + (System.currentTimeMillis() - time2));
		System.out.println(list3);
	}
}
