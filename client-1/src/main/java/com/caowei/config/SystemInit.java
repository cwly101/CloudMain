package com.caowei.config;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.caowei.dao.UserDao;
import com.caowei.entity.Article;
import com.caowei.entity.User;
import com.caowei.service.ArticleService;
import com.caowei.timer.SessionCleanTimer;

/**
 * 系统初始化
 * @author Administrator
 *
 */
@Component
public class SystemInit implements ApplicationRunner {

	@Autowired
	UserDao userDao;
	/**
	 * 注：
	 * 要搞清楚@Resource与@Autowired的区别。此处使用@Autowired报异常。 @Resource要按名称注入，@Autowired是按类型注入的。
	 */
	@Resource(name = "redisTemplate")
	RedisTemplate<String, User> redisTemplate;

	@Autowired
	StringRedisTemplate stringRedisTemplate;

	/**
	 * Users列表放入Redis缓存
	 */
	private void CacheUsersToRedis() {
		List<User> users = userDao.findAll();
		System.out.println("User表内数据数量：" + users.size());
		// user表的zset(全部的)
		// Set<ZSetOperations.TypedTuple<User>> tuples_users = new
		// HashSet<ZSetOperations.TypedTuple<User>>();
		// user表的zset(查询条件1的) To do...
		// user表的zset(查询条件2的) To do...
		// 备注：总有常用的数据集和不常用的数据集，不常用的缓存后设置一个有效期。常用的永久驻留。 数据库表设计时，肯定有字段列能反应出常用与不常用。

		// TypedTuple<String> tuple=null; //单个key
		// TypedTuple<User> tuple_user=null; //单位User
		for (User user : users) {
			// 将用户表信息存于Redis中，集合名称为user (弃用的)
			// redisTemplate.opsForValue().getAndSet("user::" + user.getLoginname(), user);

			// user表的keys集合。用于通过用户名获取用户的id。拥有id便拥有用户的一切信息。
			stringRedisTemplate.opsForHash().put("user::keys", user.getLoginname(), user.getUserid().toString());
			System.out.println(user.toString());
			// redisTemplate.opsForHash().put("userhash", user.getLoginname(),
			// user.toString());
//			System.out.println((double)user.getUserid());
//			tuple_user =new DefaultTypedTuple<User>(user, (double)user.getUserid());
//			tuples_users.add(tuple_user);

		}
		//list不会检查重复，执行多次添加多个。（这是刚学习redis时，测试的写法，不可取）
		redisTemplate.opsForList().rightPushAll("users", users);

		// redisTemplate.opsForZSet().add("user::users", tuples_users);

	}

	@Autowired
	ArticleService articleService;

	private void ArticleInit() {
		// 文章1
		Article article = new Article();
		article.setAid("101");
		article.setTitle("什么是Redis");
		article.setLink("https://www.baidu.com");
		article.setPoster("1");
		article.setTime(new Date().getTime());
		article.setVotes(200);
		articleService.addArticle(article);
		// 文章2
		Article article2 = new Article();
		article2.setAid("102");
		article2.setTitle("文章2");
		article2.setLink("https://news.163.com");
		article2.setPoster("1");
		article2.setTime(new Date().getTime());
		article2.setVotes(308);
		articleService.addArticle(article2);

		// 文章3
		Article article3 = new Article();
		article3.setAid("103");
		article3.setTitle("文章2");
		article3.setLink("https://news.163.com");
		article3.setPoster("1");
		article3.setTime(new Date().getTime());
		article3.setVotes(389);
		articleService.addArticle(article3);
		
		// 文章4
		Article article4 = new Article();
		article4.setAid("104");
		article4.setTitle("时政新闻");
		article4.setLink("https://news.163.com");
		article4.setPoster("1");
		article4.setTime(new Date().getTime());
		article4.setVotes(389);
		articleService.addArticle(article4);
	}

	
	private void sessionClean() {
		Runnable sessionClean=new SessionCleanTimer(stringRedisTemplate);
		Thread t1=new Thread(sessionClean);
		t1.start();
	}
	
	@Override
	public void run(ApplicationArguments args) throws Exception {
		System.out.println("-------SystemInit--------");
		//CacheUsersToRedis();
		//ArticleInit();
		//sessionClean();
	}

}
