package com.dangdang.reader.handle;

import java.io.File;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.dangdang.reader.utils.DROSUtility;
import com.dangdang.reader.utils.DangdangFileManager;

public class PdfCacheHandle {

	
	public static String getBookCachePath(String bookPath){
		
		final String bookCachePath = DangdangFileManager.getBookCachePath() + DROSUtility.getMd5(bookPath.getBytes()) + File.separator;
		final File cacheFile = new File(bookCachePath);
		if(!cacheFile.exists()){
			cacheFile.mkdirs();
		}
		
		return bookCachePath;
	}
	
	public static String getPageCachePath(String bookPath, String bookName, int pageIndex){
		
		final String pageCachePath = getBookCachePath(bookPath) + DROSUtility.getMd5(bookName.getBytes()) + "_" + pageIndex;
		
		return pageCachePath;
	}
	
	public static boolean checkPageCacheExist(String bookPath, String bookName, int pageIndex){
		
		final String pageCachePath = getPageCachePath(bookPath, bookName, pageIndex);
		final File pageCacheFile = new File(pageCachePath);
		if(pageCacheFile.exists()){
			return true;
		}
		return false;
	}
	
	
	public static Bitmap getPageCacheBitmap(String path){
		try{
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inPreferredConfig = Bitmap.Config.RGB_565;
			return BitmapFactory.decodeFile(path, options);
		}catch(Exception e){
			e.printStackTrace();
		}catch(OutOfMemoryError e){
			e.printStackTrace();
		}
		return null;
	}
	
	
	
}
