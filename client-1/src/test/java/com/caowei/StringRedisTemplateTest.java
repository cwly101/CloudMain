package com.caowei;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.SortParameters.Order;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.query.SortQuery;
import org.springframework.data.redis.core.query.SortQueryBuilder;
import org.springframework.test.context.junit4.SpringRunner;

import com.caowei.dao.UserDao;
import com.caowei.entity.User;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@SpringBootTest
public class StringRedisTemplateTest {

	@Autowired
	StringRedisTemplate redisTemplate;

	// =================Redis 字符串的操作 ====================

	// @Test
	public void setStringOffsetTest() {
		redisTemplate.opsForValue().set("key", "hello world!");
		System.out.println("原覆写偏移前字符串：" + redisTemplate.opsForValue().get("key"));
		redisTemplate.opsForValue().set("key", "redis", 6);
		System.out.println("覆写后字符串：" + redisTemplate.opsForValue().get("key"));

		// 如果存在返回false,如果不存在，添加，返回true
		boolean keyIsExist = redisTemplate.opsForValue().setIfAbsent("app", "caowei");
		System.out.println(keyIsExist);
		if (keyIsExist)
			System.out.println(redisTemplate.opsForValue().get("app"));
	}

	// @Test
	public void muiltSetOpTest() {
		Map<String, String> maps = new HashMap<String, String>();
		maps.put("a", "a1");
		maps.put("b", "a2");
		maps.put("c", "a3");
		// redisTemplate.opsForValue().multiSet(maps);
		List<String> keys = new ArrayList<String>();
		keys.add("a");
		keys.add("b");
		keys.add("c");
		List<String> values = redisTemplate.opsForValue().multiGet(keys);
		System.out.println(values);

		// 如果存在返回false,如果不存在，添加，返回true
		boolean mapIsExist = redisTemplate.opsForValue().multiSetIfAbsent(maps);
		System.out.println(mapIsExist); // false , 因为上面注释掉的代码之前已经写入了。
	}

	/**
	 * 设置新值同时返回之前的旧值
	 */
	// @Test
	public void setNewValueAndReturnOldValue() {

		redisTemplate.opsForValue().set("name", "test"); // 初始化一个值，即原始值（旧值）
		System.out.println("原始值:" + redisTemplate.opsForValue().get("name"));
		String old = redisTemplate.opsForValue().getAndSet("name", "admin");
		System.out.println("设置新值后返回的旧值:" + old);
		System.out.println("新值:" + redisTemplate.opsForValue().get("name"));
	}

	// @Test
	public void appandString() {
		String value = ",hello world!";
		redisTemplate.opsForValue().append("name", value); // 存在追加，不存在创建。
		System.out.println("name追加后的字符串：" + redisTemplate.opsForValue().get("name"));
	}

	// ================ Redis List数组或列表的操作 ==============

	// @Test
	public void redisArrayOrListTest() {
//		List<String> list=new ArrayList<String>();
//		list.add("a1");
//		list.add("a2");
//		list.add("a3");
//		list.add("a4");
//		redisTemplate.opsForList().leftPushAll("list", list);
//		System.out.println(redisTemplate.opsForList().range("list", 0, -1));

		// 把value值放到key对应列表中pivot值的左面，如果pivot值存在的话。 即把“java”追加到“a2”元素的左面。
//		redisTemplate.opsForList().leftPush("list", "a2", "java");  
//		System.out.println(redisTemplate.opsForList().range("list", 0, -1));

//		redisTemplate.opsForList().set("list", 1, "C#");   //在列表中index的位置设置value值
//		System.out.println(redisTemplate.opsForList().range("list", 0, -1));

//		redisTemplate.opsForList().remove("list", 1, "a1");   //删除“a1”第一次出现的元素
//		System.out.println(redisTemplate.opsForList().range("list", 0, -1));

//		String index_value=redisTemplate.opsForList().index("list", 2);  //获取下标对应的值
//		System.out.println(index_value);

		// 弹出最左面的元素，弹出后，该值将消失。
//		System.out.println("弹出前列表："+redisTemplate.opsForList().range("list", 0, -1));
//		String value= redisTemplate.opsForList().leftPop("list");  //弹出，并获取弹出的值。
//		System.out.println("弹出前列后："+redisTemplate.opsForList().range("list", 0, -1));
		/**
		 * 输出： 弹出前列表：[a4, C#, java, a2] 弹出前列后：[C#, java, a2]
		 */
		// leftPop(K key, long timeout, TimeUnit unit);
		// 移出并获取列表的第一个元素， 如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止。
		// 思考： 可用于多线程中一个独立线程对列表的侦听，及侦听到后续的逻辑延伸。 不过，超时是否可设置成永远等待中，如果可以上述的思路才可能。

		// 用于移除列表的最后一个元素，并将该元素添加到另一个列表并返回。
//		System.out.println("操作前的list：" + redisTemplate.opsForList().range("list", 0, -1));
//		redisTemplate.opsForList().rightPopAndLeftPush("list", "leftlist");
//		System.out.println("list:" + redisTemplate.opsForList().range("list", 0, -1));
//		System.out.println("leftlist:" + redisTemplate.opsForList().range("leftlist", 0, -1));
		/**
		 * 输出： 操作前的list：[C#, java, a2] list:[C#, java] leftlist:[a2]
		 */
		// V rightPopAndLeftPush(K sourceKey, K destinationKey, long timeout, TimeUnit
		// unit);
		// 用于移除列表的最后一个元素，并将该元素添加到另一个列表并返回，如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止。
		// 思考：参考同上。使用场景略有区别。可用于过滤，如原始数据，到第一层过滤后的数据。
	}

	// =========== Redis Hash数据结构 ==========

	// @Test
	public void pushHashTest() {
//		Map<String,Object> testMap = new HashMap<String,Object>();
//        testMap.put("name","jack");
//        testMap.put("age","27");
//        testMap.put("class","1");

//        redisTemplate.opsForHash().putAll("redisHash", testMap);  //存储map集合
//        System.out.println(redisTemplate.opsForHash().entries("redisHash"));
//        
//        redisTemplate.opsForHash().delete("redisHash", "name");  //删除
//        System.out.println(redisTemplate.opsForHash().entries("redisHash"));

//		System.out.println(redisTemplate.opsForHash().hasKey("redisHash","age"));  //给定的key是否存在
//		System.out.println(redisTemplate.opsForHash().get("redisHash","age"));

//		List<Object> kes = new ArrayList<Object>();
//		kes.add("class");
//		kes.add("age");
//		System.out.println(redisTemplate.opsForHash().multiGet("redisHash", kes));  //从哈希中获取给定hashKey的值
//		//通过给定的delta增加散列hashKey的值
//		System.out.println(redisTemplate.opsForHash().increment("redisHash","age",1));

//		System.out.println(redisTemplate.opsForHash().keys("redisHash"));  //获取key所对应的散列表的key

		// 设置散列hashKey的值
//		redisTemplate.opsForHash().put("redisHash","age","33");
//		redisTemplate.opsForHash().put("redisHash","class","6");
//		System.out.println(redisTemplate.opsForHash().entries("redisHash"));

		// 仅当hashKey不存在时才设置散列hashKey的值。
//		System.out.println(redisTemplate.opsForHash().putIfAbsent("redisHash","age","30"));
//		System.out.println(redisTemplate.opsForHash().putIfAbsent("redisHash","kkk","kkk"));
//		System.out.println(redisTemplate.opsForHash().entries("redisHash"));

		// 使用Cursor在key的hash中迭代，相当于迭代器
//		Cursor<Map.Entry<Object, Object>> cursor=redisTemplate.opsForHash().scan("redisHash", ScanOptions.NONE);
//		while (cursor.hasNext()) {
//			Map.Entry<Object, Object> entry=cursor.next();
//			System.out.println(entry.getKey()+","+entry.getValue());
//		}
		// 个人建议将对象转换成json格式的字符串，再存储到Redis中。直接存储java对象需要序列化和反序列化。

//		Map<Object, Object> map2= redisTemplate.opsForHash().entries("persons");
//		for (Object key : map2.keySet()) {
//			System.out.println(key.toString());
//			Person p= (Person)map2.get(key);
//			System.out.println(p.getName()+"，"+ p.getAge());
//		}
	}

	// @Test
	public void JsonToRedis() {
		// 1. 对象初始化
//		Person person=new Person();
//		person.setName("张宝瑞");
//		person.setAge(31);
//		String json=objectToJsonString(person);
//		System.out.println(json);
//		//2. 对象到集合内
//		Map<String, String> maps=new HashMap<String,String>();
//		maps.put(person.getName(), json);
//		//3. 集合到redis
//		redisTemplate.opsForHash().putAll("p_maps", maps);
		// 4. 读取
//		System.out.println("集合全部数据："+redisTemplate.opsForHash().entries("p_maps"));
//		String jsonString= redisTemplate.opsForHash().get("p_maps", "张宝瑞").toString();
//		System.out.println("key精确查询："+jsonString);
//
//		Person p1=(Person)jsonTObject(jsonString,Person.class);
//		System.out.println(p1.getName()+","+p1.getAge());

	}

	// ========= Redis Set数据结构 ==========
	// Redis 的 Set 是 String 类型的无序集合。集合成员是唯一的，这就意味着集合中不能出现重复的数据。

	// @Test
	public void RedisSetTest() {
//		String[] arr1= {"aa","bb","cc","dd"};
//		String[] arr2= {"a1","a2","cc","dd"};
//		redisTemplate.opsForSet().add("arr1", arr1);
//		redisTemplate.opsForSet().add("arr2", arr2);
		System.out.println("arr1:" + redisTemplate.opsForSet().members("arr1"));
		System.out.println("arr2:" + redisTemplate.opsForSet().members("arr2"));
//		//两个set集合的交集
		Set<String> set = redisTemplate.opsForSet().intersect("arr1", "arr2");
		System.out.println("arr1与arr2的交集：" + set);
//		
//		//存储交集到另一个set集合中
//		long size =redisTemplate.opsForSet().intersectAndStore("arr1", "arr2", "otherset");
//		System.out.println("交集数量："+size);
//		System.out.println("otherset:"+redisTemplate.opsForSet().members("otherset"));

//		//并集
//		Set<String> union_set=redisTemplate.opsForSet().union("arr1", "arr2");
//		System.out.println(union_set);

//		//并集存储另一个set集合
//		redisTemplate.opsForSet().unionAndStore("arr1", "arr2", "union_set");
//		System.out.println("union_set:"+redisTemplate.opsForSet().members("union_set"));

		// 差集
		Set<String> diff_set = redisTemplate.opsForSet().difference("arr1", "arr2");
		System.out.println("差集：" + diff_set);
	}

//	@Autowired
//	UserDao userDao;
//	@Resource(name = "redisTemplate") 
//	RedisTemplate<String, User> redisTemplate1;
//	
//	@Test
//	public void ListToRedisTest() {
//		List<User> users = userDao.findAll();
//		redisTemplate1.opsForList().rightPushAll("users", users);
//		System.out.println("--- done ---");
//	}

	@Test
	public void sort() {
		String key = "sort-list";
		String[] values = { "32", "61", "8", "77", "12" };

		redisTemplate.setEnableTransactionSupport(true);   //redis事务，必须先启用事务。程序启动时，注解启动也可以。

		//redisTemplate.multi();
		boolean is_exsit=redisTemplate.hasKey(key);
		System.out.println(is_exsit);
		//事务开始。这行代码不能放到验证key是否存在上边 redisTemplate.hasKey(key); 。因为exec()之前代码根本不会提交执行。
		//所以is_exsit 一定为null。
		redisTemplate.multi(); 
		if (!is_exsit)
			redisTemplate.opsForList().rightPushAll(key, values);
		redisTemplate.opsForValue().increment("value1");		
		System.out.println(redisTemplate.exec());

		//这行代码放到redisTemplate.exec() 外边。放到里边的所有操作结果，均由redisTemplate.exec() 附带返回（包括查询）。
		//故，放到里边的话，list为空，因为结果由exec() 函数存放返回。
		List<String> list = redisTemplate.opsForList().range(key, 0, -1);
		System.out.println("原始输出：" + list);
		//排序方式1：
		//取出原始结果，由程序排序。 建议使用排序方式2。
		list.sort(new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				boolean result = Integer.parseInt(o1) > Integer.parseInt(o2);
				return result ? 1 : -1;
			}
		});
		System.out.println("排序后：" + list);

		//排序方式2： 
		//redis根据排序语句，在内部排好序之后返回。  参见：https://www.cnblogs.com/shamo89/p/8622152.html
		//一定程度支持正则表达式
		SortQuery<String> query=SortQueryBuilder.sort(key)
				//.get("")                  //在value里过滤正则，可以连续写多个get
				.order(Order.DESC)
				.alphabetical(true)  //ALPHA修饰符用于对字符串进行排序，false的话只针对数字排序 
				.limit(0, -1)              //分页，和mysql一样
				.build()
				;
		/**
		 * 重要提示：
		 * 1. 当集合计算合集、并集、差集的能力与Sort能力相结合，sort命令将变得非常强大。
		 * 2. redis事务同样可以在一连串不间断执行的命令里面操作多种不同类型的数据。
		 */
		List<String> list1 =redisTemplate.sort(query);
		System.out.println("排序方式2：" + list1);
	}
}
