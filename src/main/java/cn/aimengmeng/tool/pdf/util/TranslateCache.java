package cn.aimengmeng.tool.pdf.util;

import java.util.HashMap;

public class TranslateCache {
	
	public static HashMap<String,String> map=new HashMap<String, String>();
	

	public static void put(String key,String value) {
		
		map.put(key, value);
	}

	public static String get(String key) {
		
		return map.get(key);
		
	}
	
	public static String remove(String key) {
		
		return map.remove(key);
		
	}

	public  static HashMap<String, String> getMap() {
		
		return map;
	}
	
}
