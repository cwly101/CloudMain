package com.caowei.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.caowei.entity.User;

@Repository
public interface UserDao extends JpaRepository<User, String> {

	User findByLoginname(String loginname);
	
	User findByLoginnameAndPassword(String loginname,String pwd);
}
