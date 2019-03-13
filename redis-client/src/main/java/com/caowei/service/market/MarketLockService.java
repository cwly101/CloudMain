package com.caowei.service.market;

/**
 * 市场服务接口（加锁版）
 * @author cwly1
 *
 */
public interface MarketLockService {

	/**
	 * 购买市场中商品（加锁操作版）
	 * @param buyerid
	 * @param pid
	 * @param sellerid
	 * @param price
	 */
	void purchase_with_lock(String buyerid,String pid,String sellerid);
}
