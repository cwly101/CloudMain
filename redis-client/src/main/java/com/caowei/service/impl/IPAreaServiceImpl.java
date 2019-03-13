package com.caowei.service.impl;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.caowei.common.IPtoLongUtil;
import com.caowei.service.iplocation.IPAreaService;

@Service
public class IPAreaServiceImpl implements IPAreaService {

	@Autowired
	StringRedisTemplate stringRedisTemplate;
	
	@Override
	public String getCountyByIP(String ip) {
		long ipScore=IPtoLongUtil.ipToLong(ip);
		// 返回有序集中指定分数区间内的所有的成员。有序集成员按分数值递减(从大到小)的次序排列。
		//结果集里边ipScore分值最大，倒序排列，它排在第一位。
		//offset=0,表示不偏移。count=1,表示只取第一个。set中其实只有一个值。这个值就是ipScore分值对应的value.
		Set<String> set=stringRedisTemplate.opsForZSet().reverseRangeByScore("ip2country:", 0, ipScore, 0, 1);
		String[] arr=new String[set.size()];
		set.toArray(arr);
		String country_id=arr[0].split("_")[0];  //国家编号
		System.out.println(country_id);
		String country= stringRedisTemplate.opsForHash().get("countryid2county:", country_id).toString();
		return country;
	}

}
