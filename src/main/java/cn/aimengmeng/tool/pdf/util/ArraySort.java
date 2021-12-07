package cn.aimengmeng.tool.pdf.util;

import java.util.Comparator;

public class ArraySort implements Comparator<String>{

	@Override
	public int compare(String point1, String point2) {
		
         String a= point1.substring(0,point1.indexOf("."));
         String b= point2.substring(0,point2.indexOf(".")); 

         if (Integer.valueOf(a) > Integer.valueOf(b)) {
        	 
             return 1;
         }else if(Integer.valueOf(a) < Integer.valueOf(b)) {
        	 
        	 return -1;
         }else {
        	 
             return 0;
         }
	}
	
}
