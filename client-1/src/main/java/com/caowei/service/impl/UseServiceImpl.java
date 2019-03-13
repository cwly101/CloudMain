package com.caowei.service.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import com.caowei.dao.UserDao;
import com.caowei.entity.User;
import com.caowei.model.Message;
import com.caowei.service.UserService;

@Service
public class UseServiceImpl implements UserService {

	@Autowired
	UserDao userDao;
	@Autowired
	StringRedisTemplate stringRedisTemplate;

	@Override
	public Message userLogin(String loginname, String pwd) {
		// 生成token码
		String str = loginname + pwd;
		String token = DigestUtils.md5DigestAsHex(str.getBytes());

		// 登录验证，从Redis中查询。
		Message message = new Message();
		boolean is_exist = stringRedisTemplate.opsForHash().hasKey("login:", token);
		System.out.println(is_exist);
		// 登录验证，如果Redis中不存在，读数据库查询。
		if (!is_exist) {		
			System.out.println("redis不存在，查询数据库");
			User user = userDao.findByLoginnameAndPassword(loginname, pwd);			
			if (user == null) {
				message.setState("error");
				message.setMsg("用户不存在或登录名密码错误");
				return message;
			}

			// 存入redis的Hash中，key：token, value: user的json字符串
			stringRedisTemplate.opsForHash().putIfAbsent("login:", token, user.toString());
			// token的最后一次更新时间
			updateUserRecent(token, null);
		}
		message.setState("ok");
		message.setMsg(token);
		return message;
	}

	@Override
	public void updateUserRecent(String token, String product_id) {
		long timestamp = new Date().getTime(); // 时间戳(NWYA）
		
		
		stringRedisTemplate.opsForZSet().add("recent:", token, timestamp);
		if (product_id == null || product_id.isEmpty())
			return;
		stringRedisTemplate.opsForZSet().add("viewed:" + token, product_id, timestamp); // 记录用户浏览过的商品
		// 移除旧的记录，只保留最新的。
		// 说明：这里的-4表示什么？ 表示你想保留几条，具体值减1，就是实际被保留下来的数量。
		// 以负4举例，就表示会保留最新的3条。负26，就保留最新的25条。
		stringRedisTemplate.opsForZSet().removeRange("viewed:" + token, 0, -4);
		//统计商品浏览次数，用于后台数据分析
		stringRedisTemplate.opsForZSet().incrementScore("viewed:", product_id, 1);
	}

}
