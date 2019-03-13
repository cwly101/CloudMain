package com.caowei.timer;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caowei.service.email.EmailQueueService;
import com.caowei.service.email.EmailSender;
import com.caowei.service.email.EmailSenderFactory;

/**
 * 发送邮件定时器
 * @author cwly1
 *
 */
//@Service   //注：一定要先注入到spring框架内，定时器才能生效。
public class SendEmailTimer {

	@Autowired
	StringRedisTemplate stringredisTemplate;
	@Autowired
	EmailQueueService emailQueueService;
	@Autowired
	EmailSenderFactory factory;
	
	/**
	 * 每隔30秒监测邮件队列是否有待发送的邮件
	 */
	@Scheduled(cron="0/6 * * * * ?")
	void sendEmailByQueue() {
		System.out.println("定时器触发，6秒...");
		
		//带优化先级的消息队列数组，前一个优先级高于后一个。
		String[] queues= {"queue:notice","queue:sold"};	
		String jsonString=null;  //存储读取到的消息
		for (String queue : queues) {  //遍历消息队列数组
			jsonString= stringredisTemplate.opsForList().leftPop(queue);		
			if(jsonString!=null)  //如果读取到消息，即刻返回，本次不再继续读取。
				break;
		}	
		System.out.println(jsonString);
		if(jsonString==null) 
			return;
		
		//发送邮件
		JSONObject jsonObject=JSON.parseObject(jsonString);
		String type=jsonObject.get("type").toString();    //（要发送的）邮箱类型
		
		EmailSender sender=factory.getEmailSender(jsonObject, type);
		Map<String, String> map= sender.emailContent();
		System.out.println(map);
		sender.sendEmail(map.get("sendTo"), map.get("subject"), map.get("content"));
	};
}
