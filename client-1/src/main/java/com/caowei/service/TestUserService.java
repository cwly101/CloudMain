package com.caowei.service;

import java.util.List;

import com.caowei.entity.User;

//@CacheConfig(cacheNames="user")
public interface TestUserService {

	//value、cacheNames：两个等同的参数（cacheNames为Spring 4新增，作为value的别名），用于指定缓存存储的集合名
	
	/**
	   *     获取用户列表
	 * @param page 当前页
	 * @param size 每页多少条
	 * @return
	 */
	List<User> getUsers(int page,int size);
	
	/**
	 * 查询用户通过登录名
	 * @param loginname
	 * @return
	 */
	//@Cacheable(key="#p0")  //SpEL中 p0表示参数1，即loginname的值
	User getUserByLoingname(String loginname);
	
	/**
	 * 编辑用户信息
	 * @param user
	 * @return
	 */
	//@CachePut(key="#user.loginname")  //表示user对象中loginname的属性值
	User updateUser(User user);
}
