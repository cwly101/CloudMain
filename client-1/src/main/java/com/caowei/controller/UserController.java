package com.caowei.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.caowei.model.Message;
import com.caowei.service.UserService;

@RestController
public class UserController {

	/**
	 * 注意：请先启动Mysql
	 * E:\java\mysql-5.7.23-winx64\bin>mysqld --console
	 */
	
	/**
	 * 用户登录
	 * http://localhost:8080/login?loginname=zhaohai&pwd=123
	 */
	private final String url_login="/login"; 
	/**
	 * 浏览商品
	 * http://localhost:8080/product?token=4344b66a63bece8c7b9455e029e667aa&pid=1
	 */
	private final String url_product_details="/product";
	
	@Autowired
	UserService userService;
	
	@GetMapping(value=url_login)
	@ResponseBody
	public Message login(
			@RequestParam("loginname") String loginname,
			@RequestParam("pwd") String pwd) {
		return userService.userLogin(loginname, pwd);
	}
	
	@GetMapping(value=url_product_details)
	@ResponseBody
	public String viewProduct(
			@RequestParam("token") String token,
			@RequestParam("pid") String pid) {
		userService.updateUserRecent(token, pid);
		return "ok";
	}
}
