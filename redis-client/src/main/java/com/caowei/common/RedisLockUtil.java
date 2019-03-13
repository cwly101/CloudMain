package com.caowei.common;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.StringRedisTemplate;

import io.lettuce.core.RedisException;

public class RedisLockUtil {

	/**
	 * 获取锁，并设置锁失效时间
	 * 说明：获取失败后，会循环返回尝试获取锁，直到成功获取，或超时。
	 * @param lockname
	 * @return
	 */
	public static boolean acquire_lock_with_timeout(StringRedisTemplate stringRedisTemplete,String lockname, String uuid) {
		lockname="lock:"+lockname;
		long end = new Date().getTime() + (1000 * 8);

		while (new Date().getTime() < end) {
			// setIfAbsent 相当于SETNX, 如果key不存在，添加key value，返回true. 如果存在，不执行任何操作，直接返回false
			boolean islocked = stringRedisTemplete.opsForValue().setIfAbsent(lockname, uuid);  //尝试获取锁
			if (islocked) {  //锁定成功后
				stringRedisTemplete.expire(lockname, 15, TimeUnit.MILLISECONDS);   //设置锁15毫秒后失效
				System.out.println("锁定成功");
				return true; //锁定成功
			}
			//在锁定失败并且锁确定存在时
			else if(!islocked && stringRedisTemplete.getExpire(lockname)!=-2){  //当 key 不存在时，返回 -2.
				stringRedisTemplete.expire(lockname, 15, TimeUnit.MILLISECONDS);   //设置锁15毫秒后失效
			}
			
			try {
				System.out.println("锁定尝试...");
				Thread.sleep(10); // 循环一次，等待10毫秒。
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return false;
	}
	
	
	/**
	 * 释放锁
	 * 
	 * @param lockname
	 * @return true:成功释放。 false:失败，进程已失去了锁。
	 */
	public static boolean releaseLock(StringRedisTemplate stringRedisTemplete,String lockname, String uuid) {

		lockname = "lock:" + lockname;

		// 这里也使用了事务。
		stringRedisTemplete.setEnableTransactionSupport(true);
		while (true) {
			try {
				stringRedisTemplete.watch(lockname); // 监听“锁”的key
				// 检查进程是否仍然持有锁
				boolean progress_has_lock = stringRedisTemplete.opsForValue().get(lockname).equals(uuid);
				if (progress_has_lock) {
					stringRedisTemplete.multi();
					// 执行释放锁
					stringRedisTemplete.delete(lockname);
					stringRedisTemplete.exec();
					System.out.println("解锁完成");
					return true;
				}
				stringRedisTemplete.unwatch();
				break;
			} catch (RedisException ex) { 
				//有其它客户端修改了锁，重试
				continue;
			}			
		}

		return false;  //进程已失去了锁。
	}

}
