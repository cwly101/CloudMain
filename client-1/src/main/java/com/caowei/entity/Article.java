package com.caowei.entity;

import java.io.Serializable;

/**
 * 文章（Redis测试使用）
 * @author Administrator
 *
 */
public class Article implements Serializable {
	
	private static final long serialVersionUID = -9138676316427949966L;
	
	private String aid;
	private String title;
	private String link;
	private String poster;
	/**
	 * UTC时区到现在经过的秒
	 */
	private double time;
	/**
	 * 投票
	 */
	private double votes;
	
	public String getAid() {
		return aid;
	}
	public void setAid(String aid) {
		this.aid = aid;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getPoster() {
		return poster;
	}
	public void setPoster(String poster) {
		this.poster = poster;
	}
	public double getTime() {
		return time;
	}
	public void setTime(double time) {
		this.time = time;
	}
	public double getVotes() {
		return votes;
	}
	public void setVotes(double votes) {
		this.votes = votes;
	}
	
	/**
	 * 返回对象的json字符串
	 */
	@Override
	public String toString() {
		return String.format("{\"aid\":\"%s\",\"title\":\"%s\",\"link\":\"%s\",\"poster\":\"%s\",\"time\":\"%s\",\"votes\":\"%s\"}", 
				aid,title,link,poster,time,votes);
	}
}
