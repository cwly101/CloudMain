package com.caowei.service.email;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import com.alibaba.fastjson.JSONObject;

public abstract class EmailSender {
	
	private JavaMailSender mailSender;
	protected StringRedisTemplate redisTemplate;
	protected JSONObject jsonObject;
	
	public EmailSender(
			JSONObject jsonObject,
			StringRedisTemplate redisTemplate,
			JavaMailSender mailSender
			) {
		this.jsonObject=jsonObject;
		this.redisTemplate=redisTemplate;
		this.mailSender=mailSender;
		//System.out.println("EmailSender构造执行...");
	}
	
	/**
	 * 要发送的邮件内容
	 * @param jsonObject 要解析的json对象
	 * @return map包含3个键值对：sendTo（收件人）、subject（主题）、content（内容）
	 */
	public abstract Map<String, String> emailContent();
	
	/**
	 * 发送邮件
	 * @param sendTo 收件人
	 * @param subject 邮件标题
	 * @param content 内容
	 */
	public void sendEmail(String sendTo,String subject,String content) {
		SimpleMailMessage message=new SimpleMailMessage();
		message.setFrom("blackrain.cw@qq.com");
		message.setTo(sendTo);
		message.setSubject(subject);
		message.setText(content);
		
		try {
			mailSender.send(message);
			System.out.println("邮件发送成功");   //稍后改日志记录 To do...
		} catch (MailException e) {
			System.out.println("邮件发送失败！原因："+e.getMessage());   //日志记录To do...
		}
	}

	/**
	 * 获取时间，根据1970年距今的毫秒数，格式：yyyy-MM-dd HH:mm:ss
	 * @param millsen 1970年距今的毫秒数
	 * @return 格式：yyyy-MM-dd HH:mm:ss
	 */
	protected String getTimeString(long millsen) {		
		Date date=new Date();
		date.setTime(millsen);
		SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time=simpleDateFormat.format(date);  //当前时间的字符串
		
		return time;
	}
}
