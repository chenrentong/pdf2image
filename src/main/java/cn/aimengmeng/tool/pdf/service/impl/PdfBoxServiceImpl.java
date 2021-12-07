package cn.aimengmeng.tool.pdf.service.impl;

import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.imageio.ImageIO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import cn.aimengmeng.tool.pdf.service.PdfPageCaptureService;
import cn.aimengmeng.tool.pdf.service.impl.IcePdfServiceImpl.DocumentCloser;
import cn.aimengmeng.tool.pdf.util.ArraySort;
import cn.aimengmeng.tool.pdf.util.FileUtil;


@Service
public class PdfBoxServiceImpl implements PdfPageCaptureService{

	@Value("${outputFilePath}")
    private  String outputFilePath ; 
	
	@Value("${threadNumber}")
    private  Integer threadNumber ;  
	
	private static final Logger logger =  LogManager.getLogger(PdfBoxServiceImpl.class);
	
	public Map<String,Object> capturePages(InputStream in) throws Exception {
		
		HashMap<String,Object> map=new HashMap<String,Object>();
		ArrayList<String> list=new ArrayList<String>();
    	String uuid=UUID.randomUUID().toString();
    	map.put("path", "pdfToImage/"+uuid);
    	
    	String dirPath=outputFilePath+"pdfToImage/"+uuid+"/";
    	
    	long beginTime = System.nanoTime();  
    	PDDocument document=PDDocument.load(in);
		
		int	end = document.getNumberOfPages();

		//设置线程池	
		ExecutorService executorService = Executors.newFixedThreadPool(threadNumber); 
			 
	    java.util.List<Callable<String>> callables = new ArrayList<Callable<String>>();  
			
        for (int num=0;num<end;num++){ 
        	
        	callables.add(new CapturePage(dirPath,num,document)); 
        }
        
        ArrayList<Future<String>> listFuture=(ArrayList<Future<String>>) executorService.invokeAll(callables);   
           
        for(Future<String> future:listFuture){
        	
        	list.add(future.get());

        }

        executorService.submit(new DocumentCloser(document)).get(); 
        executorService.shutdown();  
        
        long endTime = System.nanoTime();  
        Collections.sort(list,new ArraySort());
        map.put("pages", list);
        
        logger.info("耗时: " + (endTime - beginTime) / 1000000000 + " s" );

		return map;
			
	}
	
	public class CapturePage implements Callable<String> {
        
		private String dirPath;
		private PDDocument document = null;
		private  String format = "png";
		private float scale = 2f;  
		private Integer num;
		
		 private CapturePage(String dirPath,int num,PDDocument document) {  
	         this.dirPath = dirPath;
			 this.num = num;
			 this.document = document;
	    } 
		
		@Override
		public String call() throws Exception {
			PDFRenderer pdfRenderer = new PDFRenderer(document);
			
		    //pdfbox转换方法
            BufferedImage img = pdfRenderer.renderImageWithDPI(num,scale*72,ImageType.RGB);
            //建立路径
            if(FileUtil.dirExists(dirPath)){
            	logger.info("Capturing page " + num);  
            	OutputStream  os = new FileOutputStream(dirPath + num +"."+ format);;
                
            	ImageIO.write(img, format, os); 
    	
            }
			return num +"."+ format;
				
		}
	}
	
	/** 
     * Disposes the document. 
     */  
    public class DocumentCloser implements Callable<Void> {  
        private PDDocument document;  
  
        private DocumentCloser(PDDocument document) {  
            this.document = document;  
        }  
  
        public Void call() throws IOException {  
            if (document != null) {  
            	document.close();
                logger.info("Document disposed");  
            }  
            return null;  
        }  
    }  
}
