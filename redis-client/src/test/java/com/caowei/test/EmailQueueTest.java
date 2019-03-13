package com.caowei.test;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.junit4.SpringRunner;

import com.caowei.config.ReadConfigInfo;
import com.caowei.service.email.EmailQueueService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EmailQueueTest {

	@Autowired
	EmailQueueService emailService;
	@Autowired
	StringRedisTemplate redisTemplete;
	
	@Test
	public void addEmail() throws InterruptedException {
		
		String json_sold=String.format("{\"type\":\"%s\",\"sellerid\":\"%s\",\"pid\":\"%s\",\"price\":\"%s\",\"buyerid\":\"%s\",\"email\":\"%s\",\"date\":\"%s\"}", 
				"sold","17","ItmeA",35.0,"27","cwly101@yeah.net",new Date().getTime());
		
		String json_notice=String.format("{\"type\":\"%s\",\"content\":\"%s\",\"email\":\"%s\",\"date\":\"%s\"}", 
				"notice","Game Fake有新道具上线","cwly101@yeah.net",new Date().getTime());

		emailService.addEmailToQueue("queue:sold", json_sold);
		emailService.addEmailToQueue("queue:notice", json_notice);
		
		
		while (true) {
			System.out.println("...");
			Thread.sleep(2000);		
		}
	}
	
	@Autowired
	JavaMailSender mailSender;
	
	//@Test
	public void sendEmail() {
		SimpleMailMessage message=new SimpleMailMessage();
		message.setFrom("blackrain.cw@qq.com");
		message.setTo("cwly101@yeah.net");
		message.setSubject("主题：测试邮件");
		message.setText("这是一封使用springboot发送的测试邮件");
		try {
			mailSender.send(message);
			System.out.println("发送成功");
		} catch (MailException ex) {
			System.out.println("发送失败！原因："+ex.getMessage());
		}
	    
	    
	}
	
	//@Test
	public void timeTest() {
		long millsen=new Date().getTime();
		Date date=new Date();
		date.setTime(millsen);
		System.out.println(date);
		
		SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time=simpleDateFormat.format(date);
		System.out.println(time);
	}
	
	@Autowired
	ReadConfigInfo configInfo;
	
	//@Test
	public void demo() {
		//EmailSenderFactory.getEmailSender(null, null).emailConent();
		System.out.println(configInfo.getTypes());
		System.out.println(configInfo.getTypes().get("sold"));
	}
}
