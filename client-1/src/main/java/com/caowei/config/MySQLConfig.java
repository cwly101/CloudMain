package com.caowei.config;

import org.hibernate.dialect.MySQL57Dialect;

/**
 * 自定义Hibernate关于MySQL的配置
 * @author Administrator
 *
 */
public class MySQLConfig extends MySQL57Dialect {
	
	/**
	 * 使DDL创建的新表默认编码为utf8,即支持中文。 Hibernate默认的编码为latin1。
	 */
	@Override
	public String getTableTypeString() {
		return " ENGINE=InnoDB DEFAULT CHARSET=utf8";
	}
}
