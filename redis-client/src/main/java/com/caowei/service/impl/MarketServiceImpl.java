package com.caowei.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.caowei.service.market.MarketService;

import io.lettuce.core.RedisException;

@Service
public class MarketServiceImpl implements MarketService{

	@Autowired
	StringRedisTemplate stringRedisTemplate;
	
	@Override
	public void saleUserArticle(String pid, long price, String uid) throws Exception {
		String inventory_key="inventory:"+uid;
		stringRedisTemplate.watch(inventory_key);  //开始监视玩家包裹物品列表（set），看是否发生变化
		//检查用户是否仍然持有将被销售的物品
		boolean article_exist=stringRedisTemplate.opsForSet().isMember(inventory_key, pid);
		if(!article_exist) {  //如果包裹中没有被销售物品，停止监控，中止逻辑，返回。
			stringRedisTemplate.unwatch();
			System.out.println(uid+",包裹中没有被销售物品");
			return;
		}
		System.out.println(article_exist);
		//推荐的redis事务使用方式。哪里使用，哪里开启。
		//强烈不推荐在全局配置开启事务，这会引发多个意外问题。详见我写的RedisConfig.java注解。
		stringRedisTemplate.setEnableTransactionSupport(true);  
		stringRedisTemplate.multi();
		System.out.println("multi 事务开始");
		stringRedisTemplate.opsForZSet().add("market:", pid+"-"+uid, price);
		if(uid.equals("27"))
			throw new Exception("27号玩家物品禁止销售！！！");
		stringRedisTemplate.opsForSet().remove(inventory_key, pid);
		stringRedisTemplate.exec();
		System.out.println(pid+"物品寄售成功！");
	}

	@Override
	public void buyArticle(String buyerid, String pid, String sellerid, long price) 
			throws InterruptedException {
		long end=new Date().getTime()+(1000*3);  
		String buyer="users:"+buyerid;
		String seller="users:"+sellerid;
		String market="market:";
		List<String> watchList=new ArrayList<>();
		watchList.add(buyer);
		watchList.add(market);
		
		while (new Date().getTime()<end) {
			try {
				stringRedisTemplate.watch(watchList);  //监控市场和买家。注：watch只能针对一个key，hash里面的key不行的
				double real_price=stringRedisTemplate.opsForZSet().score(market, pid);  //核实商品价格
				double funds=Double.parseDouble(stringRedisTemplate.opsForHash().get(buyer, "funds").toString());  //买家资金
				
				if(price!=real_price || funds<price ) { //如果发生核实商品价格不一致，或者买家资金不够情况，不允许购买
					stringRedisTemplate.unwatch();					
					System.out.println("无法购买！商品变更价格，或资金不足！");
					System.out.println("商品价格："+real_price+"，持有资金："+funds);
					return;
				}
				
				stringRedisTemplate.setEnableTransactionSupport(true);  //事务
				stringRedisTemplate.multi();  //事务开始
				//购买开始... To do...  1. 买家付款 2. 打钱给卖家 3. 商品从拍卖行下架 4. 商品到买家包裹
				stringRedisTemplate.opsForHash().increment(buyer, "funds", -price);  //买家支付
				stringRedisTemplate.opsForHash().increment(seller, "funds", price);    //打款给卖家
				stringRedisTemplate.opsForZSet().remove(market, pid);    //商品从拍卖行下架
				// pid的实际格式是：商品编号-卖家编号，如：itemN-17    这里只要商品编号。
				stringRedisTemplate.opsForSet().add("inventory:"+buyerid, pid.split("-")[0]);   //商品到买家包裹
				stringRedisTemplate.exec();  //提交
				System.out.println("购买成功！");
				return;
			} catch (RedisException ex) {
				System.err.println(ex.getMessage());
				Thread.sleep(100);
				System.out.println("重试...");
				continue;
			}
			
		}		
	}

}
