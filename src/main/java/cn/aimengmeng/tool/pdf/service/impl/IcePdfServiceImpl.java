package cn.aimengmeng.tool.pdf.service.impl;

	/* * Copyright 2006-2013 ICEsoft Technologies Inc. 
	 * 
	 * Licensed under the Apache License, Version 2.0 (the "License"); 
	 * you may not use this file except in compliance with the 
	 * License. You may obtain a copy of the License at 
	 * 
	 *        http://www.apache.org/licenses/LICENSE-2.0 
	 * 
	 * Unless required by applicable law or agreed to in writing, 
	 * software distributed under the License is distributed on an "AS 
	 * IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either 
	 * express or implied. See the License for the specific language 
	 * governing permissions and limitations under the License. 
	   */

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.icepdf.core.pobjects.Document;  
import org.icepdf.core.pobjects.PDimension;  
import org.icepdf.core.pobjects.Page;  
import org.icepdf.core.util.GraphicsRenderingHints;  
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import cn.aimengmeng.tool.pdf.service.PdfPageCaptureService;
import cn.aimengmeng.tool.pdf.util.ArraySort;
import cn.aimengmeng.tool.pdf.util.FileUtil;

import javax.imageio.ImageIO;  
	  














import java.awt.*;  

import java.awt.image.BufferedImage;  
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;  
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;  
import java.util.concurrent.ExecutorService;  
import java.util.concurrent.Executors;  
import java.util.concurrent.Future;
	  
	/** 
	 * The <code>PageCapture</code> class is an example of how to save page 
	 * captures to disk.  A file specified at the command line is opened and every 
	 * page in the document is captured as an image and saved to disk as a 
	 * PNG graphic file. 
	 * 
	 * @since 5.0 
	 */  
	@Service
	public class IcePdfServiceImpl implements PdfPageCaptureService{  
		
		@Value("${outputFilePath}")
	    private  String outputFilePath ;  
		
		@Value("${threadNumber}")
	    private  Integer threadNumber ;  
	   	
		private static final Logger logger =  LogManager.getLogger(IcePdfServiceImpl.class);
	    
		
		public Map<String,Object> capturePages(InputStream in) throws Exception {
			HashMap<String,Object> map=new HashMap<String,Object>();
			ArrayList<String> list=new ArrayList<String>();
	    	String uuid=UUID.randomUUID().toString();
	    	map.put("path", "pdfToImage/"+uuid);
	    	if(outputFilePath==null) {
	    		outputFilePath="file:/ts-cache/";
	    	}
	    	
	    	String dirPath=outputFilePath+"pdfToImage/"+uuid+"/";
	    	System.out.println(dirPath);
			if(FileUtil.dirExists(dirPath)){
				
				// open the url  
		        Document document = new Document();  
		        // setup two threads to handle image extraction.  
		        if(threadNumber==null) {
		        	threadNumber=2;
		        }
		        ExecutorService executorService = Executors.newFixedThreadPool(threadNumber);  
		       
		        long beginTime = System.nanoTime();  
	        	document.setInputStream(in, null);
	            // create a list of callables.  
	            int pages = document.getNumberOfPages(); 

	            java.util.List<Callable<String>> callables = new ArrayList<Callable<String>>();  
	               
	            for (int i = 0; i < pages; i++) {  
	                callables.add(new CapturePage(document,dirPath, i));  
	            }  
	            
	            ArrayList<Future<String>> listFuture=(ArrayList<Future<String>>) executorService.invokeAll(callables);   
	                   
	            for(Future<String> future:listFuture){
	            	
	            	list.add(future.get());

	            }

	            Collections.sort(list,new ArraySort());
	            map.put("pages", list);
	            
	            executorService.submit(new DocumentCloser(document)).get();   
	            executorService.shutdown();  
	  
	            long endTime = System.nanoTime();  
		        logger.info("elapsed time: " + (endTime - beginTime) / 1000000000 + " 秒" );
		        	   
			}else{
				logger.error("Failed to create directory" );  
	    		
			}
			return map;
	       
	    }  
	    
	    /** 
	     * Captures images found in a page  parse to file. 
	     */  
	    public class CapturePage implements Callable<String> {  
	        private Document document;  
	        private int pageNumber;  
	        private float scale = 2f;  
	        private float rotation = 0f;  
	        private String format="png";
	        private String dirPath;
	  
	        private CapturePage(Document document,String dirPath, int pageNumber) {  
	            this.document = document;  
	            this.pageNumber = pageNumber; 
	            this.dirPath=dirPath;
	            
	        }  
	  
	        public String call() throws IOException {  
	            Page page = document.getPageTree().getPage(pageNumber);  
	            page.init();  
	            PDimension sz = page.getSize(Page.BOUNDARY_CROPBOX, rotation, scale);  
	  
	           
	            
	            //+1解决四舍五入取整
	            BufferedImage image = new BufferedImage((int)sz.getWidth(),  
	            		(int)sz.getHeight(),  
	                    BufferedImage.TYPE_INT_RGB);  
	            Graphics g = image.createGraphics();  
	            
	            page.paint(g, GraphicsRenderingHints.PRINT,  
	                    Page.BOUNDARY_CROPBOX, rotation, scale);  
	            g.dispose();  
	            
	            // capture the page image to file  
	           
	            logger.info("Capturing page " + pageNumber);  
        		
                File file = new File(dirPath +pageNumber + "."+format);  
              
                ImageIO.write(image, format, file);  
	  
	            image.flush();  
	            return pageNumber + "."+format;  
	        }  
	    }  
	    
	    /** 
	     * Disposes the document. 
	     */  
	    public class DocumentCloser implements Callable<Void> {  
	        private Document document;  
	  
	        private DocumentCloser(Document document) {  
	            this.document = document;  
	        }  
	  
	        public Void call() {  
	            if (document != null) {  
	                document.dispose();  
	                logger.info("Document disposed");  
	            }  
	            return null;  
	        }  
	    }  
	    	      
}
