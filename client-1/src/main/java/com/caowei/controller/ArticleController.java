package com.caowei.controller;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.caowei.entity.Article;
import com.caowei.service.ArticleService;

/**
 * 文章评分控制器
 * @author Administrator
 *
 */
@RestController
public class ArticleController {

	/**
	 * 发布新文章
	 */
	private final String url_new_article="/article/new";
	/**
	 * 文章加分
	 */
	private final String url_article_add_score="/article/addscore";
	/**
	 * 获取按评分（从大到小）排序的文章列表
	 */
	private final String url_articles_by_score_sort="/article/articlesbyscore";
	/**
	 * 获取按发布时间（从大到小）排序的文章列表
	 */
	private final String url_articles_by_datetime_sort="/article/articlesbydate";
	
	@Autowired
	ArticleService articleService;
	
	/**
	 * 获取文章列表，按评分（从大到小）排序
	 * @return
	 */
	@GetMapping(value=url_articles_by_score_sort)
	@ResponseBody
	public List<Article> getArticles(
			@RequestParam(value="page",required=false,defaultValue="0") int page,
			@RequestParam(value="size",required=false,defaultValue="10") int size
			) {
		System.out.println("page:"+page+"size:"+size);
		return articleService.getArticlesByScoreSort(page,size);
	}
	
	/**
	 * 发布文章
	 * @param aid
	 * @param title
	 * @param link
	 * @param poster
	 * @return
	 */
	@GetMapping(value=url_new_article)
	@ResponseBody
	public boolean newArticle(
			@RequestParam("aid") String aid,
			@RequestParam("title") String title,
			@RequestParam("link") String link,
			@RequestParam("poster") String poster
			) {
		Article article=new Article();
		article.setAid(aid);
		article.setTitle(title);
		article.setLink(link);
		article.setPoster(poster);
		article.setTime(new Date().getTime());
		article.setVotes(1);
		
		return articleService.addArticle(article);
	}
	
	/**
	 * 给指定文章加分
	 * @param article_id 文章编号
	 * @param userid 用户id
	 * @return
	 */
	@GetMapping(value=url_article_add_score)
	@ResponseBody
	public String addScore(
			@RequestParam("article_id") String article_id,
			@RequestParam("userid") String userid) {
		boolean result= articleService.vote(article_id, userid);
		if(result)
			return "{\"result\":\"ok\"}";  //投票成功。
		return "{\"result\":\"error\"}";  //通常表示已投过票，不允许重复投票。
	}
}
