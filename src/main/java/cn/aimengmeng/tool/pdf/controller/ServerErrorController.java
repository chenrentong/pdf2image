package cn.aimengmeng.tool.pdf.controller;


import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import cn.aimengmeng.tool.pdf.dto.ResultCode;


/**
 * 服务器内部错误Controller
 * @author crt
 *
 */
@ControllerAdvice(basePackages={"cn.aimengmeng.tool.pdf"})
@RestController
public class ServerErrorController {
	
	private static final Logger logger =  LogManager.getLogger(ServerErrorController.class);

	
	@ExceptionHandler(Exception.class)
	@ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR)
	public Object ServerError(HttpServletRequest request, Exception e)
	{

		log(e, request);
		return new ResultCode(1000,"转换错误");
	}
	
    private void log(Exception ex, HttpServletRequest request) {
    	

        logger.error("************************异常开始*******************************");
      
        logger.error(request);
        logger.error("请求url : " + request.getRequestURL().toString());
      
		logger.error("请求方法: " + request.getMethod());
		
        logger.error("Content-Type:" + request.getContentType());
        
        Enumeration<String> enu = request.getParameterNames();
		while (enu.hasMoreElements()) {
			String name = (String) enu.nextElement();
			logger.error("参数:{},值:{}", name, request.getParameter(name));
			
		}
		
		logger.error("错误信息:"+ex);
       
        StackTraceElement[] error = ex.getStackTrace();
        for (StackTraceElement stackTraceElement : error) {
            logger.error(stackTraceElement.toString());
        }
        logger.error("************************异常结束*******************************");
       
    }
}
