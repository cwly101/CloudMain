package com.caowei.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
public class RedisConfig {

	//@Bean
	public StringRedisTemplate redisTemplate(RedisConnectionFactory connectionFactory) {
		
		StringRedisTemplate template=new StringRedisTemplate();
		template.setConnectionFactory(connectionFactory);
		//注：强烈不建议全局配置StringRedisTemplate直接开启事务。这会直接导致需要用到.watch()监控时，抛如下异常：
		//UnsupportedOperationException
		//at org.springframework.data.redis.connection.lettuce.LettuceConnection.watch(LettuceConnection.java:725)
		//template.setEnableTransactionSupport(true);
		//System.out.println("------ redis 开启事务支持 ------");
		//说明：比如此处只是为了开启StringRedisTemplate事务，完全没必要写这个配置了。 哪里用到事务，直接在哪里开启即可。
		//其实，不仅会导致这个异常。所有在.multi()函数后面，需要redis即刻执行操作，全部失效！！！
		return template;
	}
	
	@Autowired
	RedisProperties properties;
	
	@Bean
	public JedisPool redisPoolFactory() {
		int max_idle=properties.getJedis().getPool().getMaxIdle();
		int max_active=properties.getJedis().getPool().getMaxActive();
		long max_wait_millis=properties.getJedis().getPool().getMaxWait().toMillis();
		String host=properties.getHost();
		System.out.println("max_idle:"+max_idle+",max_active:"+max_active+",wait_millis:"+max_wait_millis);
		
		JedisPoolConfig config=new JedisPoolConfig();
		config.setMaxIdle(max_idle);
		config.setMaxTotal(max_active);
		config.setMaxWaitMillis(max_wait_millis);
		JedisPool pool=new JedisPool(config, host,properties.getPort(),100);
		return pool;
	}
}
