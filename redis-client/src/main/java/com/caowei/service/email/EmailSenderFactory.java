package com.caowei.service.email;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.caowei.config.ReadConfigInfo;

/**
 * 邮件发送器工厂
 * @author cwly1
 *
 */
@Component  //spring框架初始化时即加载到容器内
public class EmailSenderFactory {	
	
	@Autowired
	ReadConfigInfo configInfo;
	@Autowired
	StringRedisTemplate redisTemplate;
	@Autowired
	JavaMailSender mailSender;
	
	@SuppressWarnings("rawtypes")
	public EmailSender getEmailSender(JSONObject jsonObject,String type) {
		EmailSender sender=null;
		try {
			String fullname=configInfo.getTypes().get(type);
			//System.out.println("fullname:"+fullname);
			Class<?> c= Class.forName(fullname);
			//反射有参构造方式1
//			Constructor<?>[] cons=c.getConstructors();
//			sender=(EmailSender)cons[0].newInstance(jsonObject);
			//反射有参构造方式2
			Constructor constructor=c.getConstructor(JSONObject.class,StringRedisTemplate.class,JavaMailSender.class);		
			sender=(EmailSender)constructor.newInstance(jsonObject,redisTemplate,mailSender);
			//注：反射采用动态实例化的方式，即使用时才实例化。所以，EmailSender中所有用到的对象必须在实例化时提供，
			//而不能用spring提供的@Autowired获取。
			
		}  catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		return sender;
	}
	
}
