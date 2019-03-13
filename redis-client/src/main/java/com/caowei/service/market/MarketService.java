package com.caowei.service.market;

public interface MarketService {
	
	/**
	 * 玩家向拍卖行寄售物品
	 * @param pid 物品编号
	 * @param price 寄售价格
	 * @param uid  用户编号
	 */
	void saleUserArticle(String pid,long price,String uid) throws Exception;

	/**
	 * 购买拍卖行物品
	 * @param buyerid 买家编号
	 * @param pid  商品编号 
	 * @param sellerid 卖家编号
	 * @param price 物品售价
	 */
	void buyArticle(String buyerid,String pid,String sellerid,long price) throws InterruptedException;
}
