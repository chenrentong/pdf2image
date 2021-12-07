package cn.aimengmeng.tool.pdf.app;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;



@Aspect
@Component
public class WebLogAspect {
	
	private Logger logger =  LogManager.getLogger(getClass());
	
	@Pointcut("execution(public * cn.aimengmeng.tool.pdf.controller..*.*(..))")
	public void webLog() {
			
	}
	
	@Before("webLog()")
	public void doBefore(JoinPoint joinPoint) throws Throwable {
		// 接收到请求，记录请求内容
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpServletRequest request = attributes.getRequest();
		// 记录下请求内容
		logger.info("************************请求开始*******************************");
		
		logger.info("请求url : " + request.getRequestURL().toString());
		
		logger.info("请求方法 : " + request.getMethod());
		
		/*for(Object obj:joinPoint.getArgs()){
			logger.info("参数："+ obj);	
		}*/
		
	
		Enumeration<String> enu = request.getParameterNames();
		while (enu.hasMoreElements()) {
			String name = (String) enu.nextElement();
			logger.info("参数:{},值:{}", name, request.getParameter(name));
			
		}
	}
	
	@AfterReturning(returning = "ret", pointcut = "webLog()")
	public void doAfterReturning(Object ret) throws Throwable {
		// 处理完请求，返回内容
		logger.info("返回 : " + ret);	
		logger.info("************************请求结束*******************************");
		
	}
}
