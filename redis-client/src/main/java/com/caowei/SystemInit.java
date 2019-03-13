package com.caowei;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.DefaultTuple;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisZSetCommands.Tuple;
import org.springframework.data.redis.core.DefaultTypedTuple;
//import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.RedisCallback;
//import org.springframework.data.redis.core.RedisOperations;
//import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
//import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caowei.common.IPtoLongUtil;
import com.caowei.common.UnicodeUtil;
import com.caowei.model.User;
import com.caowei.service.CustomerService;

@Component
public class SystemInit implements ApplicationRunner {

	@Override
	public void run(ApplicationArguments args) throws Exception {
		System.out.println("系统初始函数开始执行...");
		//Init();
		System.out.println("初始化完成！");
	}
	
	@Autowired
	CustomerService customerService;

	private void Init() throws Exception {		
		loadGeoIPCountry();
		loadCountryZh();
		//autoCompleteTestData();
		//loadMarketData();
		//loadChat();
//		Thread t1=new Thread(customerService);
//		t1.start();
	}

	@Autowired
	StringRedisTemplate stringRedisTemplate;

	/**
	 * 加载GeoIP国家信息到Redis
	 */
	@SuppressWarnings("rawtypes")
	private void loadGeoIPCountry() {
		if (stringRedisTemplate.hasKey("ip2country:"))
			return;

		String path = "E:\\java\\GeoLite2-Country-CSV_20190122\\GeoLite2-Country-Blocks-IPv4.csv";
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(path));
			System.out.println(reader.readLine()); // 跳过第一行标题行
			String line = null;
			int i = 0;

//			Set<TypedTuple<String>> tuples = new HashSet<TypedTuple<String>>();
//			TypedTuple<String> tuple=null;

			Set<Tuple> set = new HashSet<Tuple>();
			Tuple tuple1 = null;

			while ((line = reader.readLine()) != null) {
				i++;
				String[] geo_info = line.split(",");
				if (geo_info[2].isEmpty())
					continue; // registered_country_geoname_id 为空的跳过。
				String ip = geo_info[0].split("/")[0];
				long ipScore = IPtoLongUtil.ipToLong(ip);
				String country_id = geo_info[2] + "_" + i;

//				tuple=new DefaultTypedTuple<String>(country_id,(double) ipScore);
//				tuples.add(tuple);

				tuple1 = new DefaultTuple(country_id.getBytes(), (double) ipScore);
				set.add(tuple1);
			}

			Long time = System.currentTimeMillis();
			// 如果就这一个redis交互，此种方式与下面的非事务流水线方式执行效率相等。tuples也是只交互一次，一次性提交。
			// Long size= stringRedisTemplate.opsForZSet().add("ip2country:", tuples);
			// 非事务流水线方式。
			// 注：一定要使用execute函数的operations参数，进行流水线操作，否则流水线不起作用。
//			stringRedisTemplate.executePipelined(new SessionCallback() {
//
//				@SuppressWarnings("unchecked")
//				@Override
//				public Object execute(RedisOperations operations) throws DataAccessException {
//					operations.opsForZSet().add("ip2country:", tuples);
//					return null;
//				}
//			});
			// System.out.println("tuples耗时：" + (System.currentTimeMillis() - time));
			// //1392毫秒写入完成，大概此范围内波动。
			// 总数量（含无效数据）：300598 reids添加数量：295814
			// System.out.println("总数量（含无效数据）："+i+" reids添加数量："+size);

			// time = System.currentTimeMillis();
			// 注：一定要使用doInRedis函数的connection参数进行流水线操作，严禁使用外部的stringRedisTemplate，否则流水线不起作用。
			stringRedisTemplate.executePipelined(new RedisCallback() {
				/**
				 * 原生的redis直接操作
				 */
				@Override
				public Object doInRedis(RedisConnection connection) throws DataAccessException {
					connection.openPipeline(); // 写不写这行，都走流水线操作，效果一致。
					connection.zAdd("ip2country:".getBytes(), set); // 执行效率非常之高。
					connection.closePipeline(); // 要写都写，不然都不写。
					return null;
				}
			});
			// 效率惊人。 29.5814万条数据，809毫秒写入完成。
			System.out.println("RedisCallback耗时：" + (System.currentTimeMillis() - time));
			// tuples.clear();
			set.clear();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 加载GeoIPLocation对应的中文信息到Redis
	 * @throws Exception 
	 */
	@SuppressWarnings("rawtypes")
	private void loadCountryZh() throws Exception {
		if (stringRedisTemplate.hasKey("countryid2county:"))
			return;
		String path = "E:\\java\\GeoLite2-Country-CSV_20190122\\GeoLite2-Country-Locations-zh-CN.csv";
		if(!new File(path).exists()) {
			throw new Exception("GeoLite2-Country-Locations-zh-CN.csv 文件不存在");
		}
		Long time = System.currentTimeMillis();
		stringRedisTemplate.executePipelined(new RedisCallback() {

			@SuppressWarnings("resource")
			@Override
			public Object doInRedis(RedisConnection connection) throws DataAccessException {				
				try {
					BufferedReader reader = new BufferedReader(new FileReader(path));
					System.out.println(reader.readLine());
					String line = null;		
					String format="{\"continent\":%s,\"country\":%s}";
					while ((line = reader.readLine()) != null) {
						String[] country_info = line.split(",");
						String country_id = country_info[0];
						//如果hash的各属性值，永远不会变化，建议存储为json格式。
						//如果可能经常变化，建议以key,value的方式存储。方便查询或变更。
						connection.hSet("countryid2county:".getBytes(), country_id.getBytes(), String.format(format, country_info[3],country_info[5]).getBytes());
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}
		});
		System.out.println("RedisCallback耗时：" + (System.currentTimeMillis() - time));
	}
	
	@SuppressWarnings("rawtypes")
	private void autoCompleteTestData() throws UnsupportedEncodingException {
		
		
		String key="members:101";  
		if(stringRedisTemplate.hasKey(key)) {
			System.out.println("===="+key+",已经初始化到Redis中====");
			return;
		}
		
		Set<Tuple> tuples=new HashSet<Tuple>();
		tuples.add(new DefaultTuple(UnicodeUtil.codeUnicode("Tom").getBytes(), 0.0));
		tuples.add(new DefaultTuple("Jack".toLowerCase().getBytes("utf-8"), 0.0));
		tuples.add(new DefaultTuple("TaTa".toLowerCase().getBytes("utf-8"), 0.0));
		tuples.add(new DefaultTuple("陶宏卓".toLowerCase().getBytes("utf-8"), 0.0));
		tuples.add(new DefaultTuple("Tokyo".toLowerCase().getBytes("utf-8"), 0.0));
		tuples.add(new DefaultTuple("NaNa".toLowerCase().getBytes("utf-8"), 0.0));
		tuples.add(new DefaultTuple("Will".toLowerCase().getBytes("utf-8"), 0.0));
		tuples.add(new DefaultTuple("John".toLowerCase().getBytes("utf-8"), 0.0));
		tuples.add(new DefaultTuple("J".toLowerCase().getBytes("utf-8"), 0.0));
		tuples.add(new DefaultTuple("T".toLowerCase().getBytes("utf-8"), 0.0));
		tuples.add(new DefaultTuple("安非".toLowerCase().getBytes("utf-8"), 0.0));
		tuples.add(new DefaultTuple("陶晶晶".toLowerCase().getBytes("utf-8"), 0.0));
		
		
		stringRedisTemplate.executePipelined(new RedisCallback() {

			@Override
			public Object doInRedis(RedisConnection connection) throws DataAccessException {
				//connection.zAdd(key.getBytes(), tuples);
				connection.zAdd(key.getBytes(), 0.0, UnicodeUtil.codeUnicode("王健林").getBytes());
				connection.zAdd(key.getBytes(), 0.0, UnicodeUtil.codeUnicode("王思聪").getBytes());
				connection.zAdd(key.getBytes(), 0.0, UnicodeUtil.codeUnicode("马芳").getBytes());
				connection.zAdd(key.getBytes(), 0.0, UnicodeUtil.codeUnicode("马天宇").getBytes());
				connection.zAdd(key.getBytes(), 0.0, UnicodeUtil.codeUnicode("马步芳").getBytes());
				connection.zAdd(key.getBytes(), 0.0, UnicodeUtil.codeUnicode("黄渤").getBytes());
				connection.zAdd(key.getBytes(), 0.0, UnicodeUtil.codeUnicode("黄立行").getBytes());
				connection.zAdd(key.getBytes(), 0.0, UnicodeUtil.codeUnicode("will").getBytes());
				return null;
			}
		});
	}
		
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void loadMarketData() {
		//Map<String, String> map=new HashMap<String, String>();
		if(!stringRedisTemplate.hasKey("users:17")) {
			
			User user=new User();
			user.setUid("17");
			user.setName("frank");
			user.setFunds(45);
			user.setMail("cwly101@yeah.net");
			System.out.println(user.toString());
			Map userMap= JSONObject.parseObject(user.toString(), Map.class);   
			for (Object keyString : userMap.keySet()) {
				System.out.println(keyString+",value:"+userMap.get(keyString));
			}
			//注：若userMap集合中有不能强制转换成string的类型，那么将导致添加失败。
			stringRedisTemplate.opsForHash().putAll("users:"+user.getUid(), userMap);		
			userMap.clear();
			System.out.println("users:17 添加");
		}	
		
		if(!stringRedisTemplate.hasKey("users:27")) {
			User user2=new User();
			user2.setUid("27");
			user2.setName("bill");
			user2.setFunds(125);
			user2.setMail("cwly101@yeah.net");
			Map userMap= JSONObject.parseObject(user2.toString(), Map.class);	
			stringRedisTemplate.opsForHash().putAll("users:"+user2.getUid(), userMap);		
			userMap.clear();
			System.out.println("users:27 添加");
		}
		
		
		
		if(!stringRedisTemplate.hasKey("market")) {
			stringRedisTemplate.executePipelined(new RedisCallback() {

				@Override
				public Object doInRedis(RedisConnection connection) throws DataAccessException {
					connection.zAdd("market".getBytes(), 35, "ItemA.17".getBytes());
					connection.zAdd("market".getBytes(), 48, "ItemC.7".getBytes());
					connection.zAdd("market".getBytes(), 60, "ItemE.2".getBytes());
					connection.zAdd("market".getBytes(), 73, "ItemG.3".getBytes());
					return null;
				}
			});
			System.out.println("market 添加");
		}		
	}
	
	/**
	 * 初始化聊天群信息
	 */
	private void loadChat() {
		String key="chat:incrNo";
		//初始化聊天群使用的自增编号
//		if(!stringRedisTemplate.hasKey(key)) {
//			stringRedisTemplate.opsForValue().set(key, "0");
//		}
		stringRedisTemplate.opsForValue().setIfAbsent(key, "0");   //同上等效
		long chat_no= stringRedisTemplate.opsForValue().increment(key);  //创建新聊天群时，获取编号的加1值
		
		stringRedisTemplate.setEnableTransactionSupport(true);  //事务开启
		stringRedisTemplate.multi();  //事务
		
		Set<TypedTuple<String>> tuples=new HashSet<TypedTuple<String>>();
		tuples.add(new DefaultTypedTuple<String>("frank", 0d));
		tuples.add(new DefaultTypedTuple<String>("bill",0d));
		stringRedisTemplate.opsForZSet().add("chat:"+chat_no, tuples);   //步骤1，创建聊天群，添加群成员
		//key:群号，value:最后接收的该聊天群消息的id
		stringRedisTemplate.opsForZSet().add("seen:frank", ""+chat_no,0d);  //步骤2，用户frank，所在的群有序列表增加上该群
		stringRedisTemplate.opsForZSet().add("seen:bill", ""+chat_no,0d); //步骤3，用户bill，所在的群有序列表增加上该群
		
		List<Object> resultList= stringRedisTemplate.exec();
		System.out.println("聊天群初始化结果："+resultList);
	}
	
	
	
	
	
}
