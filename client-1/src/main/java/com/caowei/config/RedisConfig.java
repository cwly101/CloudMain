package com.caowei.config;

import java.lang.reflect.Method;
import java.time.Duration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.alibaba.fastjson.parser.ParserConfig;
//import com.alibaba.fastjson.support.spring.FastJsonRedisSerializer;  //使用alibaba的会导致反序列化失败。
import com.caowei.util.FastJsonRedisSerializer;  //注：一定要使用自定义的序列化器。

@Configuration
@Order(1)
public class RedisConfig extends CachingConfigurerSupport {

	/**
	 * 定义缓存数据key生成策略的bean 包名+类名+方法名+所有参数
	 * 
	 * @return
	 */
	@Bean
	public KeyGenerator wiselykKeyGenerator() {
		KeyGenerator keyGenerator = new KeyGenerator() {

			@Override
			public Object generate(Object target, Method method, Object... params) {
				StringBuilder str = new StringBuilder();
				str.append(target.getClass().getName()); // 获取类名。
				str.append(method.getName()); // 获取方法名.
				for (Object object : params) {
					str.append(object.toString());
				}
				System.out.println("缓存key:" + str.toString());
				return str.toString();
			}
		};
		return keyGenerator;
	}

	/**
	 * 要启用spring缓存支持，需要创建一个CacheManager的bean,CacheManager接口有很多实现类，这里使用Redis的集成，
	 * 故使用RedisCacheManager这个实现类。Redis不是应用的共享内存（Ehcahe是应用共享内存），它是一个内存服务器，就像Mysql似的。
	 * 我们需要将应用连接到Redis，并使用某种“语言”进行交互，因为需要一个连接工厂,
	 * 这些都是Redis缓存所必需的配置，它们都在springboot内置类CachingConfigurerSupport中。 注：springboot1.x
	 * 与 springboot2.x 关于 RedisCacheManager的配置有所不同。
	 * 参考：https://blog.csdn.net/Mirt_/article/details/80934312
	 * 
	 * @param redisTemplate
	 * @return
	 */
	@Bean
	public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {

		RedisCacheWriter redisCacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory);
		// 序列化方式1 （默认的方式）
		// 设置CacheManager的值序列化方式为JdkSerializationRedisSerializer,但其实RedisCacheConfiguration
		// 默认就是使用StringRedisSerializer序列化key，JdkSerializationRedisSerializer序列化value,为默认实现
		// RedisCacheConfiguration
		// defaultConfiguration=RedisCacheConfiguration.defaultCacheConfig();

		
		// 序列化方式2
		FastJsonRedisSerializer<Object> fastJsonRedisSerializer = new FastJsonRedisSerializer<>(Object.class);
		RedisSerializationContext.SerializationPair<Object> pair = RedisSerializationContext.SerializationPair
				.fromSerializer(fastJsonRedisSerializer);
		// 设置序列化值的方式为pair，使用fastJsonRedisSerializer序列化器
		RedisCacheConfiguration defaultCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
				.serializeValuesWith(pair);

		// 序列化方式3
		// 即：Jackson2JsonRedisSerializer
		// 代码暂时省略 To do...

		// 缓存过期时间
		defaultCacheConfiguration = defaultCacheConfiguration.entryTtl(Duration.ofDays(1));

		// 初始化RedisCacheManager
		RedisCacheManager cacheManager = new RedisCacheManager(redisCacheWriter, defaultCacheConfiguration);

		// 设置白名单---非常重要********
		/*
		 * 使用fastjson的时候：序列化时将class信息写入，反解析的时候， fastjson默认情况下会开启autoType的检查，相当于一个白名单检查，
		 * 如果序列化信息中的类路径不在autoType中， 反解析就会报com.alibaba.fastjson.JSONException: autoType
		 * is not support的异常 可参考
		 * https://blog.csdn.net/u012240455/article/details/80538540
		 */
		ParserConfig.getGlobalInstance().addAccept("com.caowei.entity");

		return cacheManager;
	}

	
	@Bean(name="redisTemplate")
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@ConditionalOnMissingBean(name="redisTemplate") //仅仅在当前上下文中不存在某个对象时，才会实例化一个Bean
	public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
		System.out.println("redisTemplate不存在，给他实例化一个");
		RedisTemplate<Object, Object> template=new RedisTemplate<>();
		
		FastJsonRedisSerializer fastJsonRedisSerializer=new FastJsonRedisSerializer(Object.class);
		//value值的序列化采用fastJsonRedisSerializer
		template.setValueSerializer(fastJsonRedisSerializer);
		template.setHashKeySerializer(fastJsonRedisSerializer);
		//key的序列化采用StringRedisSerializer
		template.setKeySerializer(new StringRedisSerializer());
		template.setHashKeySerializer(new StringRedisSerializer());
		template.setConnectionFactory(redisConnectionFactory);
		return template;
	}
}
