package com.caowei.service;

import com.caowei.model.Message;

public interface UserService {

	/**
	 * 用户登录 
	 * @param username 用户名
	 * @param pwd 密码
	 * @return
	 */
	Message userLogin(String loginname,String pwd);
	
	/**
	 * 更新用户最后访问时间（重载2）
	 * @param token
	 * @param product_id 商品编号 (可以为空，如：null 或 “”）
	 */
	void updateUserRecent(String token,String product_id);
	 
}
