package com.caowei.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.caowei.entity.User;
import com.caowei.service.TestUserService;

/**
 * 
 * @author Administrator
 * @deprecated 这是刚学习redis时，测试的写法
 */
@RestController
public class TestUserController {
	
	/**
	 * http://localhost:8080/getUser?loginname=zhaohai
	 * http://localhost:8080/users
	 * http://localhost:8080/updateUser?loginname=zhaohai&nickname=test232
	 */
	
	private TestUserService userService;
	
	@Autowired
	public TestUserController(TestUserService userService) {
		this.userService=userService;
	}
	
	/**
	 * 获取登录用户列表
	 * @return
	 */
	@GetMapping(value="/users")
	@ResponseBody
	public List<User> getUsers() {
		System.out.println("查询所有用户");
		return userService.getUsers(0,10);
	}
	
	/**
	 * 根据用户名或者用户信息
	 * @param loginname
	 * @return
	 */
	@GetMapping(value="/getUser")
	@ResponseBody
	public User getUserByLoginname(
			@RequestParam("loginname")String loginname) {
		System.out.println("根据登录名获取用户信息"+loginname);
		return userService.getUserByLoingname(loginname);
	}
	
	/**
	 * 编辑用户信息
	 * @param loginname
	 * @param nickname
	 * @return
	 */
	@GetMapping(value="/updateUser")
	@ResponseBody
	public User updateUser(
			@RequestParam("loginname") String loginname,
			@RequestParam("nickname") String nickname
			) {
		User user=userService.getUserByLoingname(loginname);
		user.setNickname(nickname);
		return userService.updateUser(user);
	}

}
