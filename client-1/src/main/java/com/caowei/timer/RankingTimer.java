package com.caowei.timer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class RankingTimer {

	@Autowired
	StringRedisTemplate redisTemplate;
	
	
	@Scheduled(cron="0 0/1 * * * ? ")  //1分钟执行一次 0 0/1 * * * ?      // 每10秒  0/10 0/1 * * * ? 
	public void RankingUpdate() {
		//排名刷新名，移除不在排名内的商品的数量
		redisTemplate.opsForZSet().removeRange("viewed:", 0, -4); 
		
		//分值减半的逻辑代码。将viewed:集合数据取出，所有分值减半后存入（或覆盖）viewed:rank集合。
		Set<TypedTuple<String>> tuples_map = new HashSet<TypedTuple<String>>();
		Set<TypedTuple<String>> tuples= redisTemplate.opsForZSet().rangeWithScores("viewed:", 0, -1);
		TypedTuple<String> typedTuple=null;
		for (TypedTuple<String> tuple : tuples) {
			//double score=tuple.getScore()<=1?tuple.getScore():tuple.getScore()/2;
			System.out.println(tuple.getValue()+","+tuple.getScore());
			typedTuple = new DefaultTypedTuple<String>(tuple.getValue(),tuple.getScore()/2);
			tuples_map.add(typedTuple);
		}
		
		//生成最新的热门商品排名。 
		//viewed: 有序集合记录了排名榜内所有商品的总浏览次数
		//viewed:rank 有序集合记录了刷新所有商品浏览次数减半后的最新排名情况。
		redisTemplate.opsForZSet().add("viewed:rank", tuples_map);
		System.out.println("热门商品最新排行已刷新...");
	}
}
