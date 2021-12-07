package cn.aimengmeng.tool.pdf.util;

import java.io.File;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public class FileUtil {
	
	/**
	 * 判断str的文件是否存在，增加缓存的准确
	 * */
	public static boolean judgeFileExist(String str){
		JSONObject json=new JSONObject(str);
		String path=json.getString("path");
		JSONArray pages=(JSONArray) json.get("pages");
		File folder = new File("/ts-cache/"+path+"/"+pages.get(0));  
	
		if(folder.exists()){
			return true;
		}	
		return false;
	}
	
	/**
	 * 判断str的文件是否存在，并创建
	 * */
	public static boolean dirExists(String directory){
    	File dir=new File(directory);
        if(!dir.exists()){
        	return dir.mkdirs();
        }else{
    		return true;
        }
    }

}
