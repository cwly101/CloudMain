package com.caowei.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * 控制器全局异常处理器类
 * @author cwly1
 *
 */
@ControllerAdvice
@RestController
public class ExceptionController {
	
	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ExceptionHandler(NoHandlerFoundException.class)
	public String exception404(NoHandlerFoundException exception) {
		System.out.println("【exception】"+exception.getMessage());
		return "404";
	}
	
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MissingServletRequestParameterException.class)
	public String exception400(MissingServletRequestParameterException exception) {
		//参见：https://www.cnblogs.com/nosqlcoco/p/5844160.html
		System.out.println("【exception】"+exception.getMessage());
		return "坏请求，参数解析失败";
	}

	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(Exception.class)
	public String exception500(Exception exception) {
		System.out.println("【exception】"+exception.getMessage());		
		return "服务器500异常";
	}
	

}
