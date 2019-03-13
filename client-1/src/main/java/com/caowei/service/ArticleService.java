package com.caowei.service;

import java.util.List;

import com.caowei.entity.Article;

public interface ArticleService {
	
	/**
	 * 获取按评分（从大到小）排序的文章列表
	 * @return
	 */
	List<Article> getArticlesByScoreSort(int page,int size);

	/**
	 * 发布文章
	 * @param article
	 * @return
	 */
	boolean addArticle(Article article);
	
	/**
	 * 投票
	 * @param userid
	 * @return
	 */
	boolean vote(String article_id, String userid);
}
