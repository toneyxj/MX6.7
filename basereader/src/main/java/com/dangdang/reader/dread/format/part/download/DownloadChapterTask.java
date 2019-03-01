package com.dangdang.reader.dread.format.part.download;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import android.text.TextUtils;

import com.dangdang.zframework.log.LogM;
import com.dangdang.zframework.network.download.DownloadQueue.DownloadCallback;
import com.dangdang.zframework.network.download.DownloadTask;
import com.dangdang.zframework.network.download.IDownload;
import com.dangdang.zframework.network.download.IDownloadManager.DownloadExp;

public class DownloadChapterTask extends DownloadTask {

    protected DownloadCallback mCallback;
    protected IDownload mRequest;

    public DownloadChapterTask(IDownload request, DownloadCallback callback) {
        super(request, callback);
        mCallback = callback;
        mRequest = request;
    }

    protected Map<String, String> getHeaderMap(HttpURLConnection response){
    	Map<String, String> tmp = new HashMap<String, String>();
		Map<String, List<String>> header = response.getHeaderFields();
		if (header != null) {
			Iterator<Map.Entry<String, List<String>>> iter = header.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry<String, List<String>> entry = (Map.Entry<String, List<String>>) iter.next();
				String key = entry.getKey();
				List<String> val = entry.getValue();
				StringBuffer sb = new StringBuffer();
				for(int i=0; i<val.size(); i++){
					sb.append(val.get(i));
					if(i != val.size() - 1)
						sb.append(",");
				}
				tmp.put(key, sb.toString());
			}
		}    		
		return tmp;
    }
    
    protected boolean processStatusCode(String val, HttpURLConnection response) throws Exception{
    	if(val != null && !val.equalsIgnoreCase("0")){
    		DownloadExp exp = new DownloadExp();
            exp.responseCode = response.getResponseCode();
            exp.headers = getHeaderMap(response);
            exp.statusCode = 1;
            if (exp.responseCode == 200) {
            	String encode = response.getContentEncoding();
    			InputStream in;
    			if(!TextUtils.isEmpty(encode) && encode.equalsIgnoreCase("gzip")){
    				in = new GZIPInputStream(response.getInputStream());
    			}else{
    				in = response.getInputStream();
    			}
    			
    			BufferedInputStream is = new BufferedInputStream(in);
    			// 创建字节输出流对象  
                ByteArrayOutputStream os = new ByteArrayOutputStream();  
                // 定义读取的长度  
                int len = 0;  
                // 定义缓冲区  
                byte buffer[] = new byte[1024];  
                // 按照缓冲区的大小，循环读取  
                while ((len = is.read(buffer)) != -1) {  
                    // 根据读取的长度写入到os对象中  
                    os.write(buffer, 0, len);  
                }  
                // 释放资源  
                is.close();
                in.close();  
                os.close();
                
                byte[] data = os.toByteArray();
                exp.errMsg = new String(data);
            }
            mCallback.onDownloadFailed(exp.responseCode, exp.statusCode, exp.errMsg, mRequest);
            return true;
    	}
    	return false;
    }
    
    @Override
    protected boolean processHeader(HttpURLConnection response) {
    	try{
    		String url = mRequest.getUrl();
            if (url == null)
                return true;
            if (url.contains("downloadMedia")) {
            	String val = response.getHeaderField("statusCode");
            	if(processStatusCode(val, response))
            		return true;
                String fileType = response.getHeaderField("file-type");
                if (fileType != null) {
                    if (fileType.equalsIgnoreCase("zip")) {
                        LogM.d("is zip file");
                    }
                }
                return false;
            } else if (url.contains("downloadMediaWhole")) {
                // TODO 全本包月
                String deadline = response.getHeaderField("deadline");
                return false;
            }
            return false;
    	}catch(Exception e){
    		e.printStackTrace();
    	}
        return false;
    }
}
