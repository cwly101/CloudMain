package com.caowei.service.impl;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.caowei.common.RedisLockUtil;
import com.caowei.service.email.EmailQueueService;
import com.caowei.service.market.MarketLockService;


@Service
public class MarketLockServiceImpl implements MarketLockService {

	@Autowired
	StringRedisTemplate stringRedisTemplete;
	
	@Autowired
	EmailQueueService emailService;

	@Override
	public void purchase_with_lock(String buyerid, String pid, String sellerid) {
		String lockname = "market";   //如果要进行细粒度锁，这里可以替换了市场里某个商品的id，如：lockname=pid.  这行锁定的是整个市场。  
		String item=pid+"."+sellerid;
		String buyer="users:"+buyerid;
		String seller="users:"+sellerid;
		String uuid = UUID.randomUUID().toString();
		lockname=item; //锁定要操作的商品。  这行锁定的是市场里item对应的商品。  细粒度锁性能更高，因为锁定范围越小，产生竞争的机率越小。
		/**
		 * 注意：当要锁定的数据不是独一份的时候，就要防止出现的死锁的可能。此种情况，建议锁住整个结构（如市场）
		 */
		
		
		// 锁定
		boolean islocked = RedisLockUtil.acquire_lock_with_timeout(stringRedisTemplete,lockname, uuid);
		if (!islocked) {
			System.out.println("锁定失败！");
			return;
		}
		System.out.println("锁定成功！" + islocked);

		
		// 用事务执行购买逻辑
		try {		
			
			stringRedisTemplete.setEnableTransactionSupport(true);
			stringRedisTemplete.multi();
			// 检查指定的商品是否仍然在售，以及买家是否有足够的钱来购买。
			stringRedisTemplete.opsForZSet().score("market", item);
			stringRedisTemplete.opsForHash().get(buyer, "funds");			
			List<Object> price_funds= stringRedisTemplete.exec();
			//System.out.println(price_funds);
			System.out.println(price_funds.get(0)+"  "+price_funds.get(1));
			double price=Double.valueOf(price_funds.get(0).toString());
			double funds=Double.valueOf(price_funds.get(1).toString());
			if(price==0 || price>funds) {
				System.out.println("资金不足，无法购买");
			}
			stringRedisTemplete.multi();
			stringRedisTemplete.opsForHash().increment(seller, "funds", price);  //卖家收款
			stringRedisTemplete.opsForHash().increment(buyer, "funds", -price);  //买家付款
			stringRedisTemplete.opsForSet().add("inventory:"+buyerid, pid);      //商品到买家包裹中
			stringRedisTemplete.opsForZSet().remove("market", item);            //从市场中将该商品移除
			List<Object> resultList=stringRedisTemplete.exec();
			System.out.println("商品购买执行结果:"+resultList);
			if(resultList!=null && resultList.size()>1) {
				String sendTo=stringRedisTemplete.opsForHash().get(seller, "email").toString();
				String jsonString=String.format("{\"type\":\"%s\",\"sellerid\":\"%s\",\"pid\":\"%s\",\"price\":\"%s\",\"buyerid\":\"%s\",\"email\":\"%s\",\"date\":\"%s\"}", 
						"sold",sellerid,pid,price,buyerid,sendTo,new Date().getTime());
				emailService.addEmailToQueue("queue:sold",jsonString);
			}
			
		} finally {
			// TODO: 释放锁。 任何情况下，都必须释放锁。
			RedisLockUtil.releaseLock(stringRedisTemplete,lockname, uuid);			
		}

	}

	/**
	 * 获取锁，并设置锁失效时间
	 * 说明：获取失败后，会循环返回尝试获取锁，直到成功获取，或超时。
	 * @param lockname
	 * @return
	 */
//	private boolean acquire_lock_with_timeout(String lockname, String uuid) {
//		lockname="lock:"+lockname;
//		long end = new Date().getTime() + (1000 * 8);
//
//		while (new Date().getTime() < end) {
//			// setIfAbsent 相当于SETNX, 如果key不存在，添加key value，返回true. 如果存在，不执行任何操作，直接返回false
//			boolean islocked = stringRedisTemplete.opsForValue().setIfAbsent(lockname, uuid);  //尝试获取锁
//			if (islocked) {  //锁定成功后
//				stringRedisTemplete.expire(lockname, 15, TimeUnit.MILLISECONDS);   //设置锁15毫秒后失效
//				return true; //锁定成功
//			}
//			//在锁定失败并且锁确定存在时
//			else if(!islocked && stringRedisTemplete.getExpire(lockname)!=-2){  //当 key 不存在时，返回 -2.
//				stringRedisTemplete.expire(lockname, 15, TimeUnit.MILLISECONDS);   //设置锁15毫秒后失效
//			}
//			
//			try {
//				System.out.println("锁定尝试...");
//				Thread.sleep(10); // 循环一次，等待10毫秒。
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//
//		return false;
//	}

	/**
	 * 释放锁
	 * 
	 * @param lockname
	 * @return true:成功释放。 false:失败，进程已失去了锁。
	 */
//	private boolean releaseLock(String lockname, String uuid) {
//
//		lockname = "lock:" + lockname;
//
//		// 这里也使用了事务。
//		stringRedisTemplete.setEnableTransactionSupport(true);
//		while (true) {
//			try {
//				stringRedisTemplete.watch(lockname); // 监听“锁”的key
//				// 检查进程是否仍然持有锁
//				boolean progress_has_lock = stringRedisTemplete.opsForValue().get(lockname).equals(uuid);
//				if (progress_has_lock) {
//					stringRedisTemplete.multi();
//					// 执行释放锁
//					stringRedisTemplete.delete(lockname);
//					stringRedisTemplete.exec();
//					System.out.println("解锁完成");
//					return true;
//				}
//				stringRedisTemplete.unwatch();
//				break;
//			} catch (RedisException ex) { 
//				//有其它客户端修改了锁，重试
//				continue;
//			}			
//		}
//
//		return false;  //进程已失去了锁。
//	}

}
