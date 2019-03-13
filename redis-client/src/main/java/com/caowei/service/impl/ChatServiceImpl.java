package com.caowei.service.impl;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caowei.common.RedisLockUtil;
import com.caowei.service.chat.ChatService;

@Service
public class ChatServiceImpl implements ChatService {

	@Autowired
	StringRedisTemplate redisTemplete;
	
	@Override
	public void sendMessage(String chat_id, String sender, String message) throws Exception {
		String uuid=UUID.randomUUID().toString();
		String lockname="lock:chat:"+chat_id;   //细粒度锁。即只锁定当前聊天群，对其它聊天群不影响。
		boolean islocked= RedisLockUtil.acquire_lock_with_timeout(redisTemplete, lockname, uuid);  //加锁
		if(islocked==false)
			throw new Exception("Couldn't get the lock");  //锁定失败，手动抛异常
		
		try {
			long message_id=redisTemplete.opsForValue().increment("msg_ids:"+chat_id);   //该群消息使用的自增编号。有多少个群就有多个少消息自增编号。
			System.out.println("mid:"+message_id);
			long time=new Date().getTime();
			String json_msg=String.format("{\"chatid\":\"%s\",\"mid\":\"%s\",\"time\":\"%s\",\"sender\":\"%s\",\"message\":\"%s\"}"
					,chat_id, message_id,time,sender,message);
			redisTemplete.opsForZSet().add("msg:"+chat_id, json_msg, message_id);
			System.out.println("消息发送成功！chat_id:"+chat_id+",sender:"+sender);
		} finally {
			RedisLockUtil.releaseLock(redisTemplete, lockname, uuid);  //释放锁
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void receiveMessage(String receiver) {
		//获取该用户所在的各聊天群中最后接收到的消息的ID.  value:chat_id （聊天群id），score:msg_id （最后接收到的消息的id）
		Set<TypedTuple<String>> set=redisTemplete.opsForZSet().rangeWithScores("seen:"+receiver, 0, -1);
		
		//获取所有未读的消息
		//事务。 方法1
//		redisTemplete.setEnableTransactionSupport(true);
//		redisTemplete.multi();
//		for (TypedTuple<String> typedTuple : set) {			
//			String chat_id=typedTuple.getValue();
//			long msg_id=typedTuple.getScore().longValue();
//			System.out.println("chat_id:"+chat_id+",msg_id:"+msg_id);			
//			redisTemplete.opsForZSet().rangeByScore("msg:"+chat_id, msg_id+1, Double.MAX_VALUE);			
//		}
//		List<Object> list=redisTemplete.exec();
		
		//非事务型流水线。方法2
		List<Object> list=
		redisTemplete.executePipelined(new RedisCallback() {
			@Override
			public Object doInRedis(RedisConnection connection) throws DataAccessException {
				for (TypedTuple<String> typedTuple : set) {			
					String chat_id=typedTuple.getValue();
					long msg_id=typedTuple.getScore().longValue();
					System.out.println("chat_id:"+chat_id+",msg_id:"+msg_id);
					String key="msg:"+chat_id;
					connection.zRangeByScore(key.getBytes(), String.valueOf(msg_id+1), "+inf");					
				}
				return null;
			}
		});
		System.out.println("===============");
		System.out.println(list.size());
		if(list.size()<=0) {
			System.out.println("没有新消息...");
			return;
		}
		//该用户在各个群中所有未读的消息集
		Set<String> sets= (Set<String>) list.get(0);
		System.out.println(sets);
		
		redisTemplete.executePipelined(new RedisCallback() {
			@Override
			public Object doInRedis(RedisConnection connection) throws DataAccessException {							
				for (String json : sets) {
					if(json==null) continue;								
					JSONObject jsonObject= JSON.parseObject(json);
					String chat_id=jsonObject.getString("chatid");
					String mid=jsonObject.getString("mid");
					String key_chat="chat:"+chat_id;
					//更新聊天群有序集合内当前用户读取的最新消息id.  value:用户（如：bill)，score:mid （用户已读）消息id。
					//该集合作用：看用户所在的某个群，是否有用户未读的消息，读取。
					connection.zAdd(key_chat.getBytes(), Double.parseDouble(mid), receiver.getBytes());				
					String key_seen="seen:"+receiver;
					//用户所有已加入群有序集合。vaule:群ID，score:mid。   该集合作用：找出所有群成员都已经阅读过的消息，删除这些消息，释放空间。
					connection.zAdd(key_seen.getBytes(), Double.parseDouble(mid), chat_id.getBytes());
				}
				return null;
			}
		});
		System.out.println("===开始移除已读信息===");
		deleteAllReadMessage(sets);
	}
	
	/**
	 * 删除所有的已读消息
	 * @param sets
	 */
	private void deleteAllReadMessage(Set<String> sets) {
		for (String json_msg : sets) {
			JSONObject jsonObject= JSON.parseObject(json_msg);
			String chat_id=jsonObject.getString("chatid");
			String mid=jsonObject.getString("mid");
			deleteChatReadMessage(chat_id, mid);
		}
	}

	/**
	 * 删除聊天群中所有人都已读的消息
	 * @param chat_id 群id
	 * @param mid 消息id
	 */
	private void deleteChatReadMessage(String chat_id,String mid) {
			
		long everybody_read_mid=0;  //群内所有人都已读的消息ID.		
		Set<TypedTuple<String>> set=redisTemplete.opsForZSet().rangeWithScores("chat:"+chat_id, 0, 0);
		for (TypedTuple<String> typedTuple : set) {
			everybody_read_mid=typedTuple.getScore().longValue();
		}
		System.out.println(everybody_read_mid);
		//删除此消息
		long delete_size= redisTemplete.opsForZSet().removeRangeByScore("msg:"+chat_id, 0, Double.parseDouble(mid));
		System.out.println("成功删除的数量："+delete_size);
	}
}
