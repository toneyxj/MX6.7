package com.dangdang.reader.dread.util;

import java.io.File;

import android.content.Context;

import com.dangdang.reader.dread.config.IFactor;
import com.dangdang.reader.dread.config.ReadConfig;
import com.dangdang.reader.dread.format.IBookCache;
import com.dangdang.reader.dread.format.epub.EpubBookCache;
import com.dangdang.reader.dread.util.BookStructConvert.ComposingSeriBook;
import com.dangdang.reader.utils.DangdangFileManager;
import com.dangdang.zframework.log.LogM;
import com.dangdang.zframework.utils.MemoryStatus;

public class BookCacheHandle {

	public final static int CACHE_MINSPACE = 1024*1024*10;
	
	/**
	 * @param bookId 如果是内置的包含haskey
	 * @param isFull
	 * @param bookCache
	 */
	public static void seriBookCache(Context context, String bookId, int isFull, IBookCache bookCache){
		
		try {
			IFactor factor = getComposingFactor(context);
			File file = getCacheFile(bookId, isFull, factor);//TODO pdf考虑修改存储目录
			if(file.exists()&&file.length()>0){
				printLog(" seriBookCache exists true ");
				return;
			}
			if(!hasMemAvailable()){
				printLogE(" seriBookCache hasMemAvailable=false ");
				return;
			}
			
			Object seriObj = createSeriObj(bookCache);
			byte[] datas = BookStructConvert.convertObjectToByte(seriObj);
			printLog(" seriBookCache " + datas.length);
			File parentFile = file.getParentFile();
			if(parentFile != null && !parentFile.exists()){
				parentFile.mkdirs();
			}
			writeByteToFile(file, datas);
			printLog(" seriBookCache writeByteToFile ");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	protected static Object createSeriObj(IBookCache bookCache) {
		Object seriObj = null;
		EpubBookCache epubCache = (EpubBookCache) bookCache;
		ComposingSeriBook composSb = new ComposingSeriBook();
		composSb.setPageCount(epubCache.getPageCount());
		composSb.setPageInfoCache(epubCache.getPageInfoCache());
		seriObj = composSb;
		return seriObj;
	}
	
	private static boolean hasMemAvailable(){
		return MemoryStatus.getAvailableInternalMemorySize() > CACHE_MINSPACE;
	}

	protected static File getCacheFile(String bookId, int isFull, IFactor factor) {
		String cacheFileName = factor.uniqueId();
		String bookCacheDir = getBookCacheDir(bookId, isFull);
		File file = new File(bookCacheDir, cacheFileName);
		
		return file;
	}
	
	public static boolean isBookCache(String bookId, int isFull, IFactor factor){
		
		File file = getCacheFile(bookId, isFull, factor);
		
		return file.exists();
	}
	
	public static Object deSeriBookCache(Context context, String bookId, int isFull){
		Object composingSeri = null;
		try {
			IFactor factor = getComposingFactor(context);
			File file = getCacheFile(bookId, isFull, factor);
			byte[] seriDatas = readFile(file);
			if(seriDatas == null || seriDatas.length <= 0){
				return null;
			}
			printLog(" deSeriBookCache " + seriDatas.length);
			composingSeri = BookStructConvert.convertByteToObject(seriDatas);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return composingSeri;
	}
	
	/**
	 * @param bookId 如果是内置的包含haskey
	 * @param isFull
	 * @return
	 */
	public static boolean deleteBookCache(String bookId, int isFull){
		boolean delete = false;
		try {
			String bkCacheDir = getBookCacheDir(bookId, isFull);
			File file = new File(bkCacheDir);
			if(file.exists()){
				delete = DangdangFileManager.deleteCurrFile(file);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return delete;
	}
	
	public static IFactor getComposingFactor(Context context){
		ReadConfig readConfig = ReadConfig.getConfig();
		return readConfig.getComposingFactor(context);
	}
	
	public static String getBookCacheDir(String bookId, int isFull){
		return DangdangFileManager.getReadComposingCacheDir() + bookId + "_" + isFull + File.separator;
	}
	
	private static void writeByteToFile(File file, byte[] datas){
		DangdangFileManager.writeDataToFile(datas, file);
	}
	
	private static byte[] readFile(File file){
		if(!file.exists()){
			return null;
		}
		return DangdangFileManager.getBytesFromFile(file);
	}
	
	static void printLog(String log){
		LogM.i(BookCacheHandle.class.getSimpleName(), log);
	}
	
	static void printLogE(String log){
		LogM.e(BookCacheHandle.class.getSimpleName(), log);
	}

}
