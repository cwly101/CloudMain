package com.caowei.timer;

import java.util.Set;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Session清理定时器。（后台守护进程） 
 * 注：这种方式最原始，建议采用 @EnableScheduling 的方式。一会重构一下。
 * @author Administrator
 *
 */
public class SessionCleanTimer implements Runnable {

	/**
	 * 允许保存已登录用户的上限数量。
	 */
	private final long LIMIT=1;
		
	
	StringRedisTemplate stringRedisTemplate;
	
	/**
	 * 必须通过构造传递StringRedisTemplate对象，这里自动注入不行。
	 * 应该是独立线程的原因，是不是不同线程间资源限制的原因？
	 * @param stringRedisTemplate
	 */
	public SessionCleanTimer(StringRedisTemplate stringRedisTemplate) {
		this.stringRedisTemplate=stringRedisTemplate;
	}
	
	@Override
	public void run() {
		
		while (true) {
			System.out.println(stringRedisTemplate==null);
			System.out.println("Session清理侦听器开始扫描...");
			//已登录用户的数量
			long size= stringRedisTemplate.opsForZSet().zCard("recent:"); 	
			//System.out.println(size);
			if(size>LIMIT) {
				//超过上限后，每次要清理几个最早登录的用户。
				int remove_count=1-1;  //减1是因为索引从0开始。1表示如果超过用户保存的上限数量后，每次清理1个用户，。
				//要移除的令牌集合
				Set<String> tokens= stringRedisTemplate.opsForZSet().range("recent:", 0, remove_count);
				//删除单个，传递String字符串。删除多个，传递数组。tokens.toArray()返回Object[]数组，内部最终也会转换为String[]
				stringRedisTemplate.opsForHash().delete("login:", tokens.toArray());  
				stringRedisTemplate.opsForZSet().remove("recent:",tokens.toArray());								
			}
			
			try {				
				Thread.sleep(1000*60);  //休眠1分钟。 等于1分钟执行一次。
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
