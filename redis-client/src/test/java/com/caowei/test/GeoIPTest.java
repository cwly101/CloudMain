package com.caowei.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.alibaba.fastjson.JSON;
import com.caowei.service.iplocation.IPAreaService;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
//import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.model.CountryResponse;
import com.maxmind.geoip2.record.Country;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GeoIPTest {
	
	/**
	 * redis缓存肯定使用这种。
	 * @throws IOException
	 */
	//@Test
	public void readCVS() throws IOException {
		String path="E:\\java\\GeoLite2-Country-CSV_20190122\\GeoLite2-Country-Blocks-IPv4.csv";
		//path="E:\\java\\GeoLite2-Country-CSV_20190122\\GeoLite2-Country-Locations-zh-CN.csv";
		BufferedReader reader=new BufferedReader(new FileReader(path));
		System.out.println(reader.readLine());
		String line=null;
		int i=0;
		while ((line=reader.readLine())!=null) {
			System.out.println(line);	
			
			if(i>300)
				break;
			i++;
		}
	}

	/**
	 * 不可能每解析一个ip，都读取一遍这个数据库文件，疯了？！
	 * @throws IOException
	 * @throws GeoIp2Exception
	 */
	//@Test
	public void ipCountryTest() 
			throws IOException, GeoIp2Exception {
		System.out.println(InetAddress.getLocalHost());
		
		String path="E:\\java\\GeoLite2-Country_20190122\\GeoLite2-Country.mmdb";
		File database=new File(path);
		
		DatabaseReader reader=new DatabaseReader.Builder(database).build();		
		InetAddress ipAddress=InetAddress.getByName("183.197.22.100");
		
		CountryResponse response=reader.country(ipAddress);
		Country country=response.getCountry();
		System.out.println(country.getIsoCode()+","+country.getName()+","+country.getNames().get("zh-CN"));
	}
	
	/**
	 * ip地址转成long型数字
	 */
	//@Test
	public void ipToScore() {
		String ipString="183.197.22.100";
		String[] ips=ipString.split("\\.");
		long value=(Long.parseLong(ips[0])<<24)+(Long.parseLong(ips[1])<<16)+(Long.parseLong(ips[2])<<8)+(Long.parseLong(ips[3]));
		System.out.println(value);  // 3083146852
	}
	
	//@Test
	public void scoreToIp() {
		long longIp=3083146852L;
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
		System.out.println(sb.toString());
	}
	
	@Autowired
	IPAreaService areaService;
	
	@Test
	public void test() {
//		String ip="2.16.0.0/22".split("/")[0];
//		System.out.println(ip);
		
		String jsonString= areaService.getCountyByIP("183.197.22.100");
		System.out.println(jsonString);
		String country= JSON.parseObject(jsonString).get("country").toString();
		System.out.println(country);
	}
}
