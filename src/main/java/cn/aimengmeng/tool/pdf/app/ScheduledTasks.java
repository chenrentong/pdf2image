package cn.aimengmeng.tool.pdf.app;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;









import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import cn.aimengmeng.tool.pdf.util.TranslateCache;


@Component
public class ScheduledTasks {
	
	@Value("${fileMaxTime}")
	private Long fileMaxTime;
	
	@Value("${outputFilePath}")
    private  String outputFilePath ;
	
	private Logger logger =  LogManager.getLogger(getClass());
	
	//5分钟执行一次
	@Scheduled(fixedRate=300000)
	public void reportCurrent(){	
		logger.debug("定时器触发！");
		
		HashMap<String, String> map1=TranslateCache.getMap();
		Set<Entry<String, String>> set2 =map1.entrySet();
		for(Entry<String,String> entry:set2){
			logger.debug("缓存key:"+entry.getKey());
			logger.debug("缓存value:"+entry.getValue());
		}
		
		Date date = new Date(System.currentTimeMillis() - fileMaxTime);  
		File folder = new File(outputFilePath+"/pdfToImage/");  
		
		if(!folder.exists()){
			
			folder.mkdirs();
		}
		File[] files = folder.listFiles(); 

		for (int i=0;i<files.length;i++){  
		    File file = files[i];  
		    if (new Date(file.lastModified()).before(date)){ 	
		    	//文件删除
		    	for (File f : file.listFiles()) {
		    		 f.delete();  				     
		        }
		    	//文件夹删除
		    	Boolean result=file.delete();
		    	
		    	if(result){
		    		logger.info("文件删除成功:"+file.getName());
		    		
		    		//删除缓存
		    		HashMap<String, String> map=TranslateCache.getMap();
		    		Set<Entry<String, String>> set =map.entrySet();
		    		Iterator<Entry<String, String>> iter=set.iterator();
		    		
		    		while(iter.hasNext()){
		    			Entry<String,String> entry=iter.next(); 
		    			String value= entry.getValue();
		    			JSONObject json=new JSONObject(value);
		    			if(json.get("path").toString().indexOf(file.getName())!=-1){
		    				TranslateCache.remove(entry.getKey());		
		    				logger.info("删除缓存:"+entry.getKey());
		    				return;
		    			}	
		    		}
		    					
		    	}else{
		    		logger.info("文件删除失败:"+file.getName());
					
		    	}
		    }  
		}  
		
	}
}
