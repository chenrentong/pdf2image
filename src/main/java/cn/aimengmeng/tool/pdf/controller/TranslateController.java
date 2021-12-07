package cn.aimengmeng.tool.pdf.controller;



import java.util.HashMap;
import java.util.Map;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import org.springframework.web.multipart.MultipartFile;

import cn.aimengmeng.tool.pdf.dto.ResultCode;
import cn.aimengmeng.tool.pdf.service.impl.IcePdfServiceImpl;
import cn.aimengmeng.tool.pdf.service.impl.PdfBoxServiceImpl;
import cn.aimengmeng.tool.pdf.util.FileUtil;
import cn.aimengmeng.tool.pdf.util.Md5Util;
import cn.aimengmeng.tool.pdf.util.TranslateCache;


@RestController
@RequestMapping("/pdf")
public class TranslateController {
	
	@Autowired
	private PdfBoxServiceImpl pdfpagePdfboxService;
		
	@Autowired
	private IcePdfServiceImpl pdfCaptureService;
	
	
	private static final Logger logger =  LogManager.getLogger(TranslateController.class);
	
	@Value("${fileMaxTime}")
	private Long fileMaxTime;
	
	@Value("${host}")
	private String host;
	
	@RequestMapping(value="/clear",method=RequestMethod.GET, produces="application/json;charset=utf-8")
	public Object clear(HttpServletResponse response) throws Exception{	
		HashMap<String, String> map=TranslateCache.getMap();
		map.clear();	
		return new ResultCode(1002,"删除缓存成功");
	}
	
	@RequestMapping(value="/icePdfToImage",method=RequestMethod.POST, produces="application/json;charset=utf-8")
	public Object icePdfToImage(@RequestParam(required=true) MultipartFile file,HttpServletRequest request,HttpServletResponse response) throws Exception{
		logger.debug("文件类型："+file.getContentType());
		
		if(! (file.getContentType().equals("application/pdf") || file.getContentType().equals("application/octet-stream"))) {
			
			return new ResultCode(1001,"文件类型不正确");
		}
		
		String filemd5=Md5Util.getMd5ByByte(file.getBytes());
		
		logger.info("File MD5:"+filemd5);
		String key=filemd5+"-icepdf";
		
		String value=TranslateCache.get(key);
		if(value==null || !FileUtil.judgeFileExist(value)){	
			
			Map<String, Object> map= pdfCaptureService.capturePages(file.getInputStream());
			JSONObject json=new JSONObject(map);
			json.put("host", host);
			json.put("fileName", file.getOriginalFilename());
			TranslateCache.put(key,json.toString());	
			return json.toString();
			
		}
				
		return value;
	}
	
	@RequestMapping(value="/pdfBoxToImage",method=RequestMethod.POST, produces="application/json;charset=utf-8")
	public Object pdfBoxToImage(@RequestParam(required=true) MultipartFile file,HttpServletRequest request,HttpServletResponse response) throws Exception{
		logger.debug("文件类型："+file.getContentType());
		
		if(! (file.getContentType().equals("application/pdf") || file.getContentType().equals("application/octet-stream"))) {
			
			return new ResultCode(1001,"文件类型不正确");
		}
		
		String filemd5=Md5Util.getMd5ByByte(file.getBytes());
		
		logger.info("File MD5:"+filemd5);
		String key=filemd5+"-pdfbox";
		
		String value=TranslateCache.get(key);
		if(value==null || !FileUtil.judgeFileExist(value)){	
			
			Map<String, Object> map = pdfpagePdfboxService.capturePages(file.getInputStream());
			JSONObject json=new JSONObject(map);
			json.put("host", host);
			json.put("fileName", file.getOriginalFilename());
			TranslateCache.put(key,json.toString());	
			return json.toString();
			
		}
				
		return value;
	}
}
