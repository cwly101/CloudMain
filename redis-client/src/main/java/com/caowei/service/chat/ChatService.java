package com.caowei.service.chat;

/**
 * 聊天服务接口
 * @author cwly1
 *
 */
public interface ChatService {
	
	/**
	 * 发送聊天消息
	 * @param chat_id 聊天群id
	 * @param sender 发送人
	 * @param message 消息内容
	 */
	void sendMessage(String chat_id,String sender,String message) throws Exception;
	
	/**
	 * 接收所有未读的群聊天消息
	 * @param receiver 接收人。 如:bill
	 */
	void receiveMessage(String receiver);

}
