package com.caowei.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.caowei.common.UnicodeUtil;
import com.caowei.service.autocomplete.AutoCompleteService;

@Service
public class AutoCompleteServiceImpl implements AutoCompleteService {

	@Autowired
	StringRedisTemplate stringRedisTemplate;

	/**
	 * 预先排好序的字符 作用：在预先排好序的字符里面找到前缀的最后一个字符，并据此来查找第一个排在该字符前面的字符
	 */
	private static final String VALID_CHARACTERS = ",-0123456789abcdefg";

	/**
	 * 根据给出的前缀字符串计算出查找发范围 原理：
	 * 在redis的有序集合里面，当所有成员的分值都相等时，有序集合将按照成员的名字进行排序；而当所有成员的分值都是0时，成员按照字符串的二进制顺序排序；
	 * 因为假系人的姓名先转换为了16进制字符串，那么比-小的字符是',',比'f'大的字符是'g'；
	 * 所以假如prefix='-00ff'，那么所有已-00ff开头的字符串都在'-00feg'到'-00ffg'之间
	 * 
	 * @param prefix
	 * @return
	 */
	private String[] findPrefixRange(String prefix) {
		int posn = VALID_CHARACTERS.indexOf(prefix.charAt(prefix.length() - 1)); // 查找出前缀字符串最后一个字符在列表中的位置
		char suffix = VALID_CHARACTERS.charAt(posn > 0 ? posn - 1 : 0); // 找出前驱字符
		String start = prefix.substring(0, prefix.length() - 1) + suffix + 'g'; // 生成前缀字符串的前驱字符串
		String end = prefix + 'g'; // 生成前缀字符串的后继字符串
		return new String[] { start, end };
	}

	
	
	@SuppressWarnings("unchecked")
	@Override
	public void autocomplete_prefix(String prefix) {
//		// 此处假设prefix的值为：t
//		//1. 获取前缀的最后一个字符。
//		String last_str= prefix.substring(prefix.length()-1);	
//		
//		//2.获取前缀最后一个字符 在 预先排好序字符中的前一个字符
//		int index=Valid_Characters.indexOf(last_str);
//		System.out.println(last_str+","+index);
//		String position_str=Valid_Characters.substring(index-1, index);
//		System.out.println(position_str);
//		
//		String start=position_str+"{";
//		String end=last_str+"{";
//		
//		stringRedisTemplate.opsForZSet().add("members:101", start, 0.0);
//		stringRedisTemplate.opsForZSet().add("members:101", end, 0.0);
		List<String> list=new ArrayList<String>();
		String key="members:101";

		prefix = UnicodeUtil.codeUnicode(prefix); // 把输入的字符转换成16进制字符串，因为redis里面存的是每个字符对应的16进制字符串
		String[] range = findPrefixRange(prefix);
		String start = range[0];
		String end = range[1];
		
		//System.out.println(start+","+end);
		String identifier = UUID.randomUUID().toString();	
		start += identifier;
		end += identifier;		//	防止多个群成员可以同时操作有序集合,将相同的前驱字符串和后继字符串插入有序集合
		
		stringRedisTemplate.setEnableTransactionSupport(true);
		stringRedisTemplate.multi();
		stringRedisTemplate.opsForZSet().add(key, start, 0.0);
		stringRedisTemplate.opsForZSet().add(key, end, 0.0);
		stringRedisTemplate.exec();
		
		while (true) {
			
			//watch命令可以监控一个或多个键，一旦其中有一个键被修改（或删除），之后的事务就不会执行。
			stringRedisTemplate.watch(key);
			//找出两个插入元素的位置
			int start_index=stringRedisTemplate.opsForZSet().rank(key, start).intValue();
			int end_index=stringRedisTemplate.opsForZSet().rank(key, end).intValue();  		

			stringRedisTemplate.multi();
			stringRedisTemplate.opsForZSet().remove(key, start);
			stringRedisTemplate.opsForZSet().remove(key, end);
			stringRedisTemplate.opsForZSet().range(key, start_index, end_index-2);  //添加的起始和结束index已经删除，故范围查询结束索引的值要减2。
			List<Object> results = stringRedisTemplate.exec();
			if(results!=null) {		
				Set<String> sets= (Set<String>) results.get(results.size()-1);
				System.out.println(sets);
				for (String value : sets) {
					list.add(UnicodeUtil.decodeUnicode(value.toString()));
				}				
				break;
			}
		}
		System.out.println(list);
		
	}

}
