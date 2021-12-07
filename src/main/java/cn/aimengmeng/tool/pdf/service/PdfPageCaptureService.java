package cn.aimengmeng.tool.pdf.service;

import java.io.InputStream;
import java.util.Map;

public interface PdfPageCaptureService {
	
 
	Map<String,Object> capturePages(InputStream in) throws Exception;
}
