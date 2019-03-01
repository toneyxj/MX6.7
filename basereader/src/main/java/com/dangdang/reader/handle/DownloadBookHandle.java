package com.dangdang.reader.handle;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import android.content.Context;
import android.text.TextUtils;

import com.dangdang.reader.dread.data.ReadInfo;
import com.dangdang.reader.dread.jni.DrmWarp;
import com.dangdang.reader.dread.service.MarkService;
import com.dangdang.reader.dread.service.NoteService;
import com.dangdang.reader.personal.domain.ShelfBook;
import com.dangdang.reader.personal.domain.ShelfBook.BookType;
import com.dangdang.reader.utils.AccountManager;
import com.dangdang.reader.utils.DangdangFileManager;
import com.dangdang.zframework.log.LogM;
import com.dangdang.zframework.network.download.DownloadConstant.Status;

public class DownloadBookHandle {

	private final static LogM logger = LogM.getLog(DownloadBookHandle.class);
	
	
	public void setDownloadFinish(boolean isFull, String productId){
		String productDir = getProductDir(isFull, productId);
		DangdangFileManager.writeDownloadFinishFile(productDir);
		
	}
	
	public boolean hasDownloadFinish(boolean isFull, String productId){
		String productDir = getProductDir(isFull, productId);
		return DangdangFileManager.hasDownloadFinish(productDir);
	}
	
	public void setFileTotalSize(boolean isFull, String productId, long fileSize){
		String productDir = getProductDir(isFull, productId);
		DangdangFileManager.writeDownLoadSize(productDir, (int)fileSize);
	}
		
	public File getBookDest(boolean isFull, String productId, BookType type){
		String productDir = getProductDir(isFull, productId);
		return DangdangFileManager.getBookDest(productDir, productId, type);
	}

	public static boolean isValidDownloadUsername(String tmp){
		if(TextUtils.isEmpty(tmp))
			return false;
		if(tmp.contains("*") || tmp.contains("\\") || tmp.contains("/") 
    			|| tmp.contains("|") || tmp.contains("?") || tmp.contains(">") 
    			|| tmp.contains("<") || tmp.contains("\"") || tmp.contains(":"))
			return false;
		return true;
	}
	
	public boolean saveBookKey(ShelfBook info, byte[] keydata) {
		try {
			//keydata = DRCompress.encrypt(keydata);
			
			String keyStr = new String(keydata, "UTF-8");
			printLog(" GetFullReadKey saveBookKey keyStr = " + keyStr + ", keydata.len = " + keydata.length);
			
			DrmWarp drmWarp = DrmWarp.getInstance();
			drmWarp.enCrypt(keydata);
			byte[] enCryptData = drmWarp.getEnCryptData();
			if(enCryptData != null){
				printLog(" GetFullReadKey saveBookKey enCryptData.len = " + enCryptData.length);
			}
			
			/*mShelfService.updateBookKey(info.getmBookId(), enCryptData);
			info.setmBookKey(enCryptData);*/
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/*public boolean saveBookKey(boolean isFull, String productId, byte[] keydata){
		try {
			keydata = DRCompress.encrypt(keydata);
			String productDir = getProductDir(isFull, productId);
			File keyFile = DangdangFileManager.getBookKey(productDir);
			return saveData2File(keyFile, keydata);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}*/
	
	public boolean checkBookKey(boolean isFull, String productId){
		String productDir = getProductDir(isFull, productId);
		File keyFile = DangdangFileManager.getBookKey(productDir);
		return keyFile.exists();
	}
	
	public void deleteBookKey(boolean isFull, String productId){
		
		String productDir = getProductDir(isFull, productId);
		File keyFile = DangdangFileManager.getBookKey(productDir);
		if(keyFile.exists()){
			keyFile.delete();
		}
		
	}
	
	public boolean saveBookJson(boolean isFull, String productId, String bookJson) {
		String productDir = getProductDir(isFull, productId);
		return DangdangFileManager.saveBookJson(productDir, bookJson);
	}
	
	public boolean checkBookJson(boolean isFull, String productId){
		String productDir = getProductDir(isFull, productId);
		return DangdangFileManager.checkBookJson(productDir);
	}
	
	public long getBookStartPosition(boolean isFull, String productId){
		String productDir = getProductDir(isFull, productId);
//		JSONObject jsonO = DangdangFileManager.getJsonObject(productDir);
		long start = DangdangFileManager.getDownLoadStart(productDir, productId);
		/*long size = 0;
		try {
			size = jsonO.getLong("fullEpubSize");
		} catch (JSONException e) {
			e.printStackTrace();
		}*/
		return start;
	}
	
	
	
	public String getProductDir(boolean isFull, String productId) {
		String productDir = null;
		if(isFull){
			String username = new AccountManager(context).getDownloadUsername();
			productDir = DangdangFileManager.getFullProductDir(context, productId, username);
		} else {
			productDir = DangdangFileManager.getTryProductDir(context, productId);
		}
		return productDir;
	}
 	
	public boolean isPauseDownloading(Status status){
		return status == Status.DOWNLOADING 
				|| status == Status.PENDING 
				|| status == Status.RESUME;
	}
	
	public String getBookJson(Map<String, ShelfBook> bookDataMap, String productId) {
		ShelfBook book = bookDataMap.get(productId);
		if(book != null){
			return book.getBookJson() == null ? "" : book.getBookJson();
		}
		return "";
	}
	
	private boolean saveData2File(File file, byte[] data){
		FileOutputStream outStream = null;
		boolean ret = false;
		try {
			File parentFile = file.getParentFile();
			if(!parentFile.exists()){
				parentFile.mkdirs();
			}
			if(!file.exists()){
				file.createNewFile();
			}
			printLog("[file.createNewFile()="+ ret +"]");
			
			outStream = new FileOutputStream(file);
			outStream.write(data, 0, data.length);
			ret = true;
		} catch (Exception e) {
			e.printStackTrace();
			ret = false;
		} finally{
			if(outStream != null){
				try {
					outStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return ret;
	}
	
	public void copyTryBookDataToFullBook(final String bookId){
		
		try {
			MarkService markService = new MarkService(context);
			final int tryStatus = ReadInfo.BSTATUS_TRY;
			final int fullStatus = ReadInfo.BSTATUS_FULL;
			markService.copyTryMarkToFull(bookId, tryStatus, fullStatus);
			
			NoteService noteService = new NoteService(context);
			noteService.copyTryNoteToFull(bookId, tryStatus, fullStatus);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private void printLog(String log) {
		logger.d(false, log);
		
	}

	private Context context;
	//private ShelfService mShelfService;
	private DownloadBookHandle(Context context){
		this.context = context;
		//mShelfService = ShelfService.getInstance(context);
	}
	
	public static DownloadBookHandle getHandle(Context context){
		return new DownloadBookHandle(context);
	}

	
}
