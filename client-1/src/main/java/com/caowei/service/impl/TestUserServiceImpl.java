package com.caowei.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.caowei.dao.UserDao;
import com.caowei.entity.User;
import com.caowei.service.TestUserService;

@Service
public class TestUserServiceImpl implements TestUserService {

	@Autowired
	UserDao userDao;

	@Resource(name = "redisTemplate")
	RedisTemplate<String, User> redisTemplate;

	@Autowired
	StringRedisTemplate stringRedisTemplate;

	@Override
	public List<User> getUsers(int page, int size) {
		System.out.println("执行getUsers方法!");

		Set<User> users = redisTemplate.opsForZSet().range("user::users", page, size - 1);
		List<User> list = new ArrayList<User>(users); // set to list
		System.out.println("users size:" + users.size());
		/**
		 * question: 1. 用户表数据应该存储在一个zset中，还是一条一条存储？ 答：zset 2.
		 * 单条数据缓存是否应设置有效期？因为使用频繁不大。答：设置有效期。 3.redis中数据按条件查询如何实现？
		 * 答：（方案1）提前按指定好的条件存储在zset中，而后直接调用。 4.RedisTemplate<String, User> zset数据，应如何解析？
		 * 答：摸索中... 5. 存储在zset中，查询单条数据怎么办？ 可走数据库，然后缓存redis，但设置有效期。
		 */
		return list;
	}

	@Override
	public User getUserByLoingname(String loginname) {
		String userid = stringRedisTemplate.opsForHash().get("user::keys", loginname).toString();
		System.out.println("userid:" + userid);
		//redis中list的方式
		User user =redisTemplate.opsForList().index("users", Integer.parseInt(userid) - 1);
		
		//redis中zset的方式
//		Set<User> users = redisTemplate.opsForZSet()
//				// range函数参数start和end索引从0开始，而userid从1开始。故减1
//				.range("user::users", Integer.parseInt(userid) - 1, Integer.parseInt(userid) - 1);
//		System.out.println(users.size());
//		User user = null;
//		for (User u : users) {
//			user = u;
//		}
//		if (user == null) {
//			System.out.println("执行getUserByLoingname方法，查询数据库！");
//			user = userDao.findByLoginname(loginname);
//		}
		return user;
	}

	@Override
	public User updateUser(User user) {
		System.out.println(user.getUserid()-1);
		//redis中list的方式
		redisTemplate.opsForList().set("users", user.getUserid()-1, user);
		
		//redis中zset的方式
		// 获取原始user
//		User user2 = getUserByLoingname(user.getLoginname());
//		
//		// 更新数据库
//		user = userDao.save(user);
//		if (user == null)
//			return null;
//
//		// 更新user缓存
//		// 注意：有序集合的成员是唯一的，但分数（score）却可以重复； 故，如此更新，只会新加一条，不会更新。
//		boolean result = redisTemplate.opsForZSet().add("user::users", user, (double) user.getUserid());
//		// redisTemplate.opsForZSet().add("user::users", user2, user2.getUserid()+0.1);
//		redisTemplate.opsForZSet().remove("user::users", user2); // 将原来的原始user删除
//
//		// 我是看到这句话得到的启发：
//		// “如果某个成员已经是有序集的成员，那么更新这个成员的分数值，并通过重新插入这个成员元素，来保证成员在正确的位置上；”
//		// 从而想到了以上的方案。
//
//		/**
//		 * 3种更新有方案： 
//		    *    第一种分三步： 1). 修改原始对象的分数值 2). 插入新对象 3). 删除原始对象 
//		    *    第二种: 1). 插入新对象 2).删除原始对象 第三种：1). 都可以。
//		 * 
//		 */
//		if (result == false)
//			return null;
		return user;
	}

}
