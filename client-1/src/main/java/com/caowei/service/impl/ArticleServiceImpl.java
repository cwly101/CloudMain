package com.caowei.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.caowei.entity.Article;
import com.caowei.service.ArticleService;

@Service
public class ArticleServiceImpl implements ArticleService {

	/**
	 * 一周时间（单位：秒）
	 */
	final long ONE_WEEK_IN_SECONDS=86400*7;
	/**
	 * 每个用户评分增加的分值。
	 */
	final double VOTE_SCORE=1;
	
	@Resource(name = "redisTemplate")
	RedisTemplate<String, Article> redisTemplate;
	@Autowired
	StringRedisTemplate stringRedisTemplete;
	
	@Override
	public boolean addArticle(Article article) {
		boolean op_result=false;
		String article_id="article"+article.getAid();
		//1. 更新文章的hash集合（添加新文章）
		System.out.println(article.toString());
		op_result=stringRedisTemplete.opsForHash().putIfAbsent("articles", article.getAid(), article.toString());
		
		//2. 创建该文章对应的用户投票set列表
		stringRedisTemplete.opsForSet().add(article_id, article.getPoster());
		//一周后过期
		stringRedisTemplete.expire(article_id, ONE_WEEK_IN_SECONDS, TimeUnit.SECONDS);
		
		//3. 更新对应zset排序集合
		double now= new Date().getTime()/1000;
		//发布时间排序的有序集合
		stringRedisTemplete.opsForZSet().add("time", article.getAid(),now);
		//评分排序的有序集合		
		stringRedisTemplete.opsForZSet().add("score", article.getAid(), VOTE_SCORE);
		
		return op_result;
	}

	@Override
	public boolean vote(String article_id,String userid) {		
		//用户投票 （操作文章用户投票列表）
		//1. 文章投票是否已关闭（超过一周后自动关闭）
		//System.out.println(article_id);
		//当前时间点的一周之前
		long before_week=new Date().getTime()/1000-ONE_WEEK_IN_SECONDS;		
		double addtime=stringRedisTemplete.opsForZSet().score("time", article_id);  //文章发布时间。
//		System.out.println(before_week+","+ addtime);
//		System.out.println(before_week>addtime);
		if(before_week>addtime)  //一周之前的时间点大于文章发布时间，表示投票已截止。（逻辑没问题）
			return false;
		
				
		//2. 判断用户是否已经投过票。 true表示该用户存在于set中，即投过票。
		boolean is_exist= stringRedisTemplete.opsForSet().isMember("article"+article_id, userid);
		//System.out.println(is_exist);
		if(is_exist==false) { //false不存在，表示可以评分。
			stringRedisTemplete.opsForSet().add("article"+article_id, userid); 
			stringRedisTemplete.opsForZSet().incrementScore("score", article_id, VOTE_SCORE);
		}
		return !is_exist;  //反转
	}

	@Override
	public List<Article> getArticlesByScoreSort(int page,int size) {
		List<Article> articles=new ArrayList<Article>();
		
		Set<TypedTuple<String>> tuples= stringRedisTemplete.opsForZSet().reverseRangeWithScores("score", page, size);
		Article article=null;
		for (TypedTuple<String> typedTuple : tuples) {
			System.out.println(typedTuple.getValue()+","+typedTuple.getScore());
			String jonString= stringRedisTemplete.opsForHash().get("articles", typedTuple.getValue()).toString();
			//alibaba提供的json字符串转指定java对象的方式。
			article=JSON.parseObject(jonString).toJavaObject(Article.class);
			article.setVotes(typedTuple.getScore());
			articles.add(article);	
		}
		return articles;
	}
	
	
}
