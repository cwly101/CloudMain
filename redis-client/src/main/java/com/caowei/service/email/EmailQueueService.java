package com.caowei.service.email;

public interface EmailQueueService {

	/**
	 * 将新邮件到添加到待发送邮件队列
	 * @param queuename 队列名称
	 * @param jsonString 新邮件的json格式字符串
	 */
	void addEmailToQueue(String queuename, String jsonString);
}
