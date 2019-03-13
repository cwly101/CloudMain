package com.caowei.service.autocomplete;

public interface AutoCompleteService {

	/**
	 * 通过字符进行自动完成搜索
	 * @param prefix
	 */
	void autocomplete_prefix(String prefix);
}
