package com.caowei.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="user")
public class User implements Serializable {

	private static final long serialVersionUID = 5681012682861190838L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer userid;
	
	@Column(name="loginname")
	private String loginname;
	
	@Column(name="nickname")
	private String nickname;
	
	@Column(name="pwd")
	private String password;

	public Integer getUserid() {
		return userid;
	}

	public void setUserid(Integer userid) {
		this.userid = userid;
	}

	public String getLoginname() {
		return loginname;
	}

	public void setLoginname(String loginname) {
		this.loginname = loginname;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	@Override
	public String toString() {
		return String.format("{\"userid\":\"%s\",\"loginname\":\"%s\",\"nickname\":\"%s\",\"password\":\"*****\"}", 
				this.getUserid(),this.getLoginname(),this.getNickname());
	}
}
