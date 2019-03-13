package com.caowei.service.iplocation;

/**
 * IP获取所在区域的服务接口
 * @author Administrator
 *
 */
public interface IPAreaService {
	
	/**
	 * 获取IP所属国家
	 * @param ip
	 * @return
	 */
	String getCountyByIP(String ip);

}
