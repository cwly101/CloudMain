package com.caowei.util;

import java.nio.charset.Charset;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
   *    实现对象的缓存，定义自己的序列化和反序列化器。使用阿里的fastjson来实现的比较多。
 * @author Administrator
 *
 * @param <T>
 */
public class FastJsonRedisSerializer<T> implements RedisSerializer<T> {

	private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
	private Class<T> clazz;

	public FastJsonRedisSerializer(Class<T> clazz) {
		super();
		this.clazz = clazz;
	}

	/**
	 * 序列化
	 */
	@Override
	public byte[] serialize(T t) throws SerializationException {
		if (null == t) {
			return new byte[0];
		}
		return JSON.toJSONString(t, SerializerFeature.WriteClassName).getBytes(DEFAULT_CHARSET);
	}

	/**
	 * 反序列化
	 */
	@Override
	public T deserialize(byte[] bytes) throws SerializationException {
		if (null == bytes || bytes.length <= 0) {
			return null;
		}
		String str = new String(bytes, DEFAULT_CHARSET);
		return (T) JSON.parseObject(str, clazz);
	}

}
