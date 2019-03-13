package com.caowei.aop;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 定义AOP切面类
 * @author cwly1
 *
 */
//@Component
@Aspect
public class AspectWebLog {

	/**
	 * 切入点表达式的格式：execution([可见性]返回类型[声明类型].方法名(参数)[异常]) 
	 * 其中[]内的是可选的
	 * 表达式用例：
	 * execution(public * com.caowei.controller..*.*(..))
	 * public（可见性）、*（返回类型）、com.caowei.controller..*（包及其子包）.*（所有方法）(..)（所有参数）
	 * 
	 * 
	 *                 返回类型                                                                                         参数类型
	 * execution(String com.caowei.controller.RedisPublishController.sendMessage(String))
	 * 定义切入点
	 *  *：匹配任何数量字符；
	 *  ..：匹配任何数量字符的重复，如在类型模式中匹配任何数量子包；而在方法参数模式中匹配任何数量参数。
	 *  +：匹配指定类型的子类型；仅能作为后缀放在类型模式后边。
	 * execution：
	 * 1。public * *(..) 任何公共方法的执行 
	 * 2。* cn.javass..IPointcutService.*() cn.javass包及所有子包下IPointcutService接口中的任何无参方法
	 * 3。* cn.javass..*.*(..) cn.javass包及所有子包下任何类的任何方法
	 */
	@Pointcut("execution(public * com.caowei.controller..*.*(..))")
	public void log() {}
	
//	@Autowired
//	IPAreaService ipService;
	
	/**
	 * 注意：任何通知方法都可以将第一个参数定义为org.aspectj.lang.JoinPoint类型（环绕通知需要定义第一个参数为ProceedingJoinPoint类型，
	 * 它是 JoinPoint 的一个子类）。JoinPoint接口提供了一系列有用的方法，比如 getArgs()（返回方法参数）、getThis()（返回代理对象）、
	 * getTarget()（返回目标）、getSignature()（返回正在被通知的方法相关信息）和 toString()（打印出正在被通知的方法的有用信息）。
	 * @param joinPoint
	 */
	@Before("log()")   //&& args(msg,..)
	public void doBefore(JoinPoint joinPoint) {  //,String msg
		System.out.println("\n");
		System.out.println("dobefor()执行... ");
		ServletRequestAttributes atrAttributes= (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpServletRequest request=atrAttributes.getRequest();
			
		String url=request.getRequestURL().toString();
		//关于获取ip地址的说明，如果url使用localhost请求，那么ip值为：0:0:0:0:0:0:0:1。如果url使用ip请求，则正常获取ip地址。
		String ip=request.getRemoteAddr();
		String method=request.getMethod();
		String class_method=joinPoint.getSignature().getDeclaringTypeName()+"."+joinPoint.getSignature().getName();
		String outputString=String.format("url：%s, method:%s, ip：%s, class_method：%s", url,method,ip,class_method);
		System.out.println(outputString);
		System.out.println("方法参数如下：");
		Object[] args=joinPoint.getArgs();
		for (Object arg : args) {
			System.out.println(arg.toString());
		}
		//打印出正在被通知的方法的有用信息
		//System.out.println(joinPoint.toString());
		//返回目标对象（即，被代理对象）
//		RedisPublishController controller=(RedisPublishController)joinPoint.getTarget();
//		System.out.println(controller.id);   //而id是被代理对象中的一个公共属性
		//说明：这里只是举个例子而已。切面是用于controller包及所有子包的，里边包含各种控制器对象，所以不能单纯强转为RedisPublishController。
		
		//返回代理对象joinPoint.getThis()。这里spring使用的CGLIB，而非JDK动态代理。
		//System.out.println(joinPoint.getThis().getClass().toString());  
		//输出 ：class com.caowei.controller.RedisPublishController$$EnhancerBySpringCGLIB$$aa8deda
		
		
		//if(ip==null || ip.equals("0:0:0:0:0:0:0:1")) return;
		//注：局域网IP查询所属国家没有意义，返回值肯定是错误的。
		//System.out.println("ip所属国家："+ipService.getCountyByIP(ip)); //183.197.22.16（本机外网IP）
	}
	
	@After("log()")
	public void doAfter() {
		System.out.println("doAfter()执行...");
	}
	
	@AfterThrowing(value="log()",throwing="exception") 
	public String doAfterAdvice(JoinPoint joinPoint,Throwable exception) {
		System.out.println("doAfterAdvice()执行，异常发生在："+joinPoint.getSignature().getDeclaringTypeName()+"."+joinPoint.getSignature().getName());
		System.out.println("doAfterAdvice捕获异常，信息："+exception.getMessage());
		System.out.println("\n");
		return "错误发生";
	}
}
