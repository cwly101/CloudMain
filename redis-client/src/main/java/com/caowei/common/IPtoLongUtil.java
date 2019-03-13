package com.caowei.common;

/**
 * IP转换为整数工具类
 * @author Administrator
 *
 */
public class IPtoLongUtil {

	/**
	 * IP地址转换成整数
	 * @param ip
	 * @return
	 */
	public static long ipToLong(String ip) {
		String[] ips=ip.split("\\.");
		long value=(Long.parseLong(ips[0])<<24)+(Long.parseLong(ips[1])<<16)+(Long.parseLong(ips[2])<<8)+(Long.parseLong(ips[3]));
		return value;
	}
	
	/**
	 * 整数转换为IP地址
	 * @param longIp
	 * @return
	 */
	public static String longToIP(long longIp) {
		StringBuffer sb=new StringBuffer("");
		// 直接右移24位
		sb.append(String.valueOf(longIp>>>24));
		sb.append(".");
		// 将高8位置0，然后右移16位
		sb.append(String.valueOf((longIp & 0x00FFFFFF) >>> 16));
		sb.append(".");
		// 将高16位置0，然后右移8位
		sb.append(String.valueOf((longIp & 0x0000FFFF) >>> 8));
		sb.append(".");
		// 将高24位置0
		sb.append(String.valueOf((longIp & 0x000000FF)));
		
		return sb.toString();
	}
}
