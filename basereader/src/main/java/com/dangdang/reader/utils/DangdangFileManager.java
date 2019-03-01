package com.dangdang.reader.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.StatFs;
import android.support.v4.util.LongSparseArray;
import android.text.TextUtils;

import com.dangdang.execption.UnZipException;
import com.dangdang.reader.DDApplication;
import com.dangdang.reader.R;
import com.dangdang.reader.domain.ProductDomain;
import com.dangdang.reader.dread.config.ReadConfig;
import com.dangdang.reader.dread.font.FontListHandle;
import com.dangdang.reader.dread.util.ParserEpubN;
import com.dangdang.reader.dread.util.ZipUtil;
import com.dangdang.reader.personal.domain.ShelfBook.BookType;
import com.dangdang.zframework.log.LogM;
import com.dangdang.zframework.utils.MemoryStatus;
import com.dangdang.zframework.utils.ZipExecutor.UnZipOperator;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Map;

public class DangdangFileManager {
	public static final String TAG = "DangdangFileManager";

	public static final String[] PRE_IMPORT_BOOKS = { ".txt", ".pdf", ".epub" };
	public static final String TXT_BOOK_ID_PRE = "txt_id_";
	public static final String PDF_BOOK_ID_PRE = "pdf_id_";
	public static final String EPUB_BOOK_THIRD_ID_PRE = "epub_third_id_";
	public static final String PDF_RESOURCES_PATH = "plugin";
	public static final String OLD_ROOT_NAME = "dangdang";

	/**
	 * application里赋值
	 */
	public static String APP_ROOT_PATH;
	public static String APP_START_IMG_PATH;

	/**
	 * 文件存储目录
	 */
	public static final String APP_DIR = "ddReader_offprint";

	public static final String OLD_ROOT_PATH = File.separator + OLD_ROOT_NAME
			+ File.separator;

	public static final String ROOT_PATH = File.separator + APP_DIR
			+ File.separator+ReadConfig.PreSet_OffPrint_ProductId+File.separator;

	public static final String USER_UNDEFINE = "undefine";
	public static final String USER_UNDEFINE_DIR = File.separator
			+ USER_UNDEFINE + File.separator;
	public static final String USER_READ_BOOK = "readbook";
	public static final String USER_READ_BOOK_DIR = USER_READ_BOOK
			+ File.separator;
	public static final String USER_BOOK_DIR = "book" + File.separator;
	public static final String BOOK_ERROR = "book_error";
	public static final String ERROR_LOG_PATH = File.separator + "errlog"
			+ File.separator;

	public final static String FONT_DIR = USER_BOOK_DIR + "font"
			+ File.separator;
	public final static String FONT_EXTEN = ".zip";
	public final static String PRE_SET_DIR = "preread" + File.separator;

//	public final static String PLUGINAPK_NAME = "DDLightReadPlugin.apk";

	public final static String BOOK_CACHE = "bookcache" + File.separator;
	public final static String BOOK_STORE_CACHE = "storecache" + File.separator;// 书城cache
	public final static String BOOK_SHELF_RECOMMAND_CACHE = "shelf_recommand" + File.separator;//  书架cache
	public final static String READCOMPOSING_CACHE = "readm" + File.separator;

	public static final String DANGDANG_WIFI = "dangdangwifi";
	public static final String DANGDANG_WIFI_PATH = "/" + DANGDANG_WIFI;

	public static int IMAGE_DECODER_SIZE = 200;

	public static int IMAGE_DEFAULT_WIDTH = 320;
	public static int IMAGE_DEFAULT_HEIGHT = 480;

	private static DangdangFileManager mFileManager = null;
	private static Context mContext;
	static ConfigManager mConfigManager;
	private static Bitmap sDefaultCover = null;
	private static Bitmap mClipDefaultCover = null;

	private final static String PreSet_Ttf = "default_blue_font.ttf";
	private final static String PreSet_Ttfzip = "default_blue_font.zip";

	public final static int DEFAULT_FONTSIZE = 16;

	/**
	 * 预置英文字体
	 */
	private final static String[] PreSet_EnTtf = { "DroidSerif-Regular.ttf" };// "opensans-light.ttf",
	private final static String PreSet_EnMonoTtf = "DroidSansMono.ttf";
	
	// 不好看
	// private final static String PreSet_Css = "style.css";

	private final static String PreSet_DictZip = "dicts.zip";
	private final static String PreSet_DictDir = "dicts";
	private final static String PreSet_DictXdb = "raw/dictgbk.xdb";
	private final static String PreSet_DictRule = "raw/rules.ini";

	private final static String PreSet_ReadZip = "readfile.zip";
	private final static String PreSet_ReadDir = "readfile";

	private final static String PreSet_OffPrintZip = "offprint.zip";
	private final static String PreSet_OffPrintDir = "offprint";
	private final static String PreSet_OffPrint_Rsa_Public = "public";
	private final static String PreSet_OffPrint_Rsa_Private = "private";
	private final static String PreSet_OffPrint_Certification = "cert";

	private final static String ReadDict = "readdict";

	private final static String Ext_SDcardPath = "SECONDARY_STORAGE";
	private final static String Sdcard = "sdcard";
	private final static String BookStore_Dir = "bookstore";
	private final static String BookStore_Zip = "bookstore.zip";
	private final static String BOOKNAME_SIGN = "(电子书)";

	private DangdangFileManager() {

	}

	public void setContext(Context context) {
		if (mContext != null)
			return;
		mContext = context.getApplicationContext();
		mConfigManager = new ConfigManager(mContext);
	}

	public synchronized static DangdangFileManager getFileManagerInstance() {
		if (null == mFileManager) {
			mFileManager = new DangdangFileManager();
		}
		return mFileManager;
	}

	public static boolean checkMounted() {
		return Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState());
	}

	public static String getWifiFilePath() {
		if (checkMounted()) {
			return Environment.getExternalStorageDirectory().getAbsolutePath()
					+ DANGDANG_WIFI_PATH;
		}
		return "";
	}

	public static String getAppApkDir() {
		String path = DangdangFileManager.APP_ROOT_PATH + "/apk/";
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}
		return path;
	}

	public static String getSdcardApkDir() {
		String path = "";
		if (DangdangFileManager.checkMounted()) {
			path = DangdangFileManager.getRootPathOnSdcard() + "apk/";
			File file = new File(path);
			if (!file.exists()) {
				file.mkdirs();
			}
		}
		return path;
	}

	public static File getApkFile(String path, String filename) {
		File file = new File(path, filename);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return file;
	}

	public static String getRootPath(Context appContext) {
		if (sAccountManager == null) {
			sAccountManager = new AccountManager(appContext);
		}
		boolean currentDataInSdcard = true;// sAccountManager.getConfig().isCurrentDataInSdcard();
		String rootPath = null;
		if (currentDataInSdcard && checkMounted()) {
			rootPath = getRootPathOnSdcard(appContext);
		} else {
			rootPath = getRootPathOnPhone(appContext);
		}
		// LogM.d(TAG, "rootPath:" + currentDataInSdcard + ", " + rootPath);
		return rootPath;

	}

	public static String getOldRootPath(Context appContext) {
		if (sAccountManager == null) {
			sAccountManager = new AccountManager(appContext);
		}
		boolean currentDataInSdcard = sAccountManager.getConfig()
				.isCurrentDataInSdcard();
		String rootPath = null;
		if (currentDataInSdcard && checkMounted()) {
			rootPath = getOLDRootPathOnSdcard();
		} else {
			rootPath = getRootPathOnPhone(appContext);
		}
		LogM.d(TAG, "rootPath:" + currentDataInSdcard + ", " + rootPath);
		return rootPath;

	}

	public static String getRootPathOnPhone(Context appContext) {
		File phoneFiles = appContext.getFilesDir();
		String rootPath = phoneFiles.getAbsolutePath() + OLD_ROOT_PATH;
		return rootPath;
	}

	public static final int STORAGE_MIN_SIZE = 15 * 1024 * 1024; // 存储空间必须大于15M
	public static final String PDF_TEMP_RESOURCES = "pdf_resources.zip";

	public static String getPdfResourceRootPath(Context appContext) {
		if (MemoryStatus.getAvailableInternalMemorySize() >= STORAGE_MIN_SIZE) {
			return appContext.getFilesDir().getAbsolutePath() + File.separator
					+ PDF_RESOURCES_PATH + File.separator;
		}
		if (MemoryStatus.getAvailableExternalMemorySize() >= STORAGE_MIN_SIZE) {
			return getRootPathOnSdcard() + PDF_RESOURCES_PATH + File.separator;
		}
		return "";
	}

	public static String getRootPathOnSdcard() {
		File sdcard = Environment.getExternalStorageDirectory();
		String rootPath = sdcard.getAbsolutePath() + ROOT_PATH;
		return rootPath;
	}

	public static String getOLDRootPathOnSdcard() {
		File sdcard = Environment.getExternalStorageDirectory();
		String rootPath = sdcard.getAbsolutePath() + OLD_ROOT_PATH;
		return rootPath;
	}

	public static String getRootPathOnSdcard(Context appContext) {
		File sdcard = Environment.getExternalStorageDirectory();
		String rootPath = sdcard.getAbsolutePath() + ROOT_PATH;
		return rootPath;
	}

	public static String getSdcardPath() {
		File sdDir = null;
		boolean sdCardExist = checkMounted(); // 判断sd卡是否存在
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
			return sdDir.getPath();
		}
		return "/";
	}

	public static String getExtSdcardPath() {
		Map<String, String> map = System.getenv();
		String path = null;
		try {
			if (map.containsKey(Ext_SDcardPath)) {
				String pathValue = map.get(Ext_SDcardPath);

				String[] tmpStrs = pathValue.split(":");
				path = tmpStrs[0];
				if (!path.toLowerCase().contains(Sdcard)) {
					for (int i = 0, len = tmpStrs.length; i < len; i++) {
						if (tmpStrs[i].contains(Sdcard)) {
							path = tmpStrs[i];
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return path;
	}

	public static String getSystemCachePath() {
		return mContext.getCacheDir().getAbsolutePath();
	}

	public static String getBookCachePath() {
		String path = APP_ROOT_PATH + File.separator + BOOK_CACHE;
		if (checkMounted()) {
			path = getRootPathOnSdcard() + BOOK_CACHE;
		}
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}
		return path;
	}

	public static String getImportBooksPreIndex(String path) {
		String suffix = path.toLowerCase();
		if (suffix.endsWith(PRE_IMPORT_BOOKS[0])) {
			return TXT_BOOK_ID_PRE;
		} else if (suffix.endsWith(PRE_IMPORT_BOOKS[1])) {
			return PDF_BOOK_ID_PRE;
		} else {
			return EPUB_BOOK_THIRD_ID_PRE;
		}

	}

	public static String getBookNameFromPath(String path) {

		String str = path.substring(path.lastIndexOf("/") + 1);
		int loc = str.lastIndexOf(".");
		if (loc != -1) {
			str = str.substring(0, str.lastIndexOf("."));
		}
		return str;
	}

	public static boolean isImportBook(String bookid) {
		if(TextUtils.isEmpty(bookid))
			return false;
		return bookid.startsWith(TXT_BOOK_ID_PRE)
				|| bookid.startsWith(PDF_BOOK_ID_PRE)
				|| bookid.startsWith(EPUB_BOOK_THIRD_ID_PRE);
	}

	public static boolean isInbuildBook(String bookid){
		if(TextUtils.isEmpty(bookid))
			return false;
		return bookid.startsWith(InbuildBooks.PUBLIC_KEY_PREFIX);
	}
	
	/**
	 * 根据epub里的dangdang文件是否为空，判定是否是当当线上数据
	 * 
	 * @param path
	 * @return
	 */
	public static boolean isDangdangInnerEpubBook(String path) {
		ParserEpubN parse = new ParserEpubN();
		String version = parse.readInnerZipFile(path, "dangdang");
		return !version.equals("");
	}

	public static boolean isImportBookEndsWith(File file) {
		String filename = file.getName();
		// pdf txt 只判断扩展名字
		if (filename.endsWith(PRE_IMPORT_BOOKS[0])
				|| filename.endsWith(PRE_IMPORT_BOOKS[1])) {
			return true;
		} else if (filename.endsWith(PRE_IMPORT_BOOKS[2])) {// epub
			// 需要判断dangdang
			return !isDangdangInnerEpubBook(file.getAbsolutePath());
		} else {
			return false;
		}
	}

	public static File getRootDir(Context appContext) {
		String rootPath = getRootPath(appContext);
		return new File(rootPath);
	}

	public static AccountManager sAccountManager;

	public static File getUserBookPath(Context appContext, String username) {
		String rootPath = getRootPath(appContext);
		File rootFile = new File(rootPath);
		File file = new File(rootFile.getAbsolutePath() + File.separator
				+ username);
		return file;
	}

	public static File getOldUserBookPath(Context appContext, String username) {
		String rootPath = getOldRootPath(appContext);
		File rootFile = new File(rootPath);
		File file = new File(rootFile.getAbsolutePath() + File.separator
				+ username);
		return file;
	}

	public static void recurrenceDeleteFile(File rootFile) {
		if (rootFile == null) {
			return;
		}
		if (rootFile.isFile()) {
			rootFile.delete();
			return;
		}
		File[] listFile = rootFile.listFiles();
		if (listFile != null && listFile.length > 0) {
			for (int i = 0; i < listFile.length; ++i) {
				recurrenceDeleteFile(listFile[i]);
			}
		}
		rootFile.delete();
	}

//	public static boolean hasAllBookDown(Context appContext, String productID) {
//		if (sAccountManager == null) {
//			sAccountManager = new AccountManager(appContext);
//		}
//		String userName = sAccountManager.getDownloadUsername();
//		String token = sAccountManager.getToken();
//		if (sAccountManager.checkTokenValid() && userName != null
//				&& !TextUtils.isEmpty(token)) {
//			String rootPath = getRootPath(appContext);
//			File rootFile = new File(rootPath);
//			if (!rootFile.exists()) {
//				rootFile.mkdirs();
//			}
//			File bookFile = new File(rootFile.getAbsolutePath()
//					+ File.separator + userName + File.separator
//					+ USER_BOOK_DIR + productID);
//			if (bookFile.exists() && bookFile.isDirectory()) {
//				File file = new File(bookFile.getAbsolutePath()
//						+ File.separator + BOOK_JSON);
//				if (file.exists()) {
//					return true;
//				}
//			}
//		}
//		return false;
//	}

	public static String getCoverPath(String bookDir) {
		if (bookDir == null) {
			return null;
		}
		return bookDir + File.separator + ITEM_BOOK_COVER;
	}

	public static void deleteBook(File product) {
		try {
			if (product == null) {
				return;
			}
			if (!product.exists()) {
				return;
			}
			recurrenceDeleteFile(product);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static int computeSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;
		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
				.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
				Math.floor(w / minSideLength), Math.floor(h / minSideLength));

		if (upperBound < lowerBound) {
			// return the larger one when there is no overlapping zone.
			return lowerBound;
		}

		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}

	public static final String BOOK_ENCODING = "utf-8";
	public static final String BOOK_SUFFIX = ".epub";
	public static final String BOOK_SUFFIX_CACHE = ".epub.cache";
	public static final String BOOK_FINISH = "book_finish";
	public static final String BOOK_SIZE = "book_size";
	public static final String BOOK_JSON = "book_json";
	public static final String BOOK_KEY = "book_key";
	public static final String BOOK_DECODE_KEY = "book_decode_key";
	public static final char REPLACE = '#';
	public static final char DELI = '|';
	public static final long BOOK_DOWN_TIMEOUT = 60 * 60 * 1000; // 1 小时超时时间
	public static final String ITEM_BOOK_COVER = "cover.jpg";
	public static final String ITEM_BOOK_READ_PROGRESS = "book_progress";

	public static final JSONObject getJsonObject(String productDir) {
		File file = new File(productDir + File.separator + BOOK_JSON);
		if (file.exists()) {
			try {
				return new JSONObject(getStringFromFile(file));
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

	/**
	 * @param productDir
	 * @return
	 * @deprecated 可能判断错误 /.dangdang/readbook/read/....
	 */
	public static boolean isReadBook(String productDir) {
		if (productDir == null) {
			return true;
		}
		int index = productDir.lastIndexOf(USER_READ_BOOK);
		if (index > 0) {
			File file = new File(productDir.substring(0,
					index + USER_READ_BOOK.length()));
			return file.isDirectory();
		}
		return false;
	}

	public static int stringToInt(String text, int dfault) {
		if (text == null) {
			return dfault;
		}
		text = text.trim();
		if (text.length() == 0) {
			return dfault;
		}
		int value = dfault;
		try {
			value = Integer.parseInt(text);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return dfault;
		}
		return value;
	}

	public static final int getBookSize(String productDir) {
		File file = new File(productDir + File.separator + BOOK_SIZE);
		if (file.exists()) {
			return stringToInt(getStringFromFile(file), 0);
		}
		return -1;
	}

	public static final int getEpubBookSize(String productDir, String productID) {
		File file = new File(productDir + File.separator + productID
				+ BOOK_SUFFIX);
		if (file.exists()) {
			return (int) file.length();
		}
		return -1;
	}

	public static final long getImportBookSize(String filepath) {
		File file = new File(filepath);
		if (file.exists()) {
			return (long) file.length();
		}
		return -1;
	}

	public static final String getFileSize(File file) {
		String fileSize = "0.00K";
		if (file.exists()) {
			fileSize = FormetFileSize(file.length());
			return fileSize;
		}
		return fileSize;
	}

	public static String FormetFileSize(long fileS) {// 转换文件大小
		DecimalFormat df = new DecimalFormat("0.00");
		String fileSizeString = "";
		if (fileS < 1024) {
			fileSizeString = df.format((double) fileS) + "B";
		} else if (fileS < 1048576) {
			fileSizeString = df.format((double) fileS / 1024) + "K";
		} else if (fileS < 1073741824) {
			fileSizeString = df.format((double) fileS / 1048576) + "M";
		} else {
			fileSizeString = df.format((double) fileS / 1073741824) + "G";
		}
		return fileSizeString;
	}

//	public static final boolean hasDownReadBook(Context appContext,
//			String productID) {
//		// File sdcard = Environment.getExternalStorageDirectory();
//		// File file = new File(sdcard.getAbsolutePath() + ROOT_PATH +
//		// USER_UNDEFINE_DIR + USER_READ_BOOK_DIR + productID);
//		File file = new File(getRootPath(appContext) + USER_UNDEFINE_DIR
//				+ USER_READ_BOOK_DIR + productID);
//
//		// FIXME:
//
//		if (file.exists() && file.isDirectory()) {
//			File absFile = new File(file.getAbsolutePath() + File.separator
//					+ BOOK_JSON);
//			return absFile.exists();
//		} else {
//			if (sAccountManager == null) {
//				sAccountManager = new AccountManager(appContext);
//			}
//			if (sAccountManager.checkTokenValid()) {
//				// file = new File(sdcard.getAbsolutePath() + ROOT_PATH +
//				// File.separator + sAccountManager.getUsername() +
//				// File.separator + USER_READ_BOOK_DIR + productID);
//				file = new File(getRootPath(appContext) + File.separator
//						+ sAccountManager.getDownloadUsername() + File.separator
//						+ USER_READ_BOOK_DIR + productID);
//				if (file.exists() && file.isDirectory()) {
//					File absFile = new File(file.getAbsolutePath()
//							+ File.separator + BOOK_JSON);
//					return absFile.exists();
//				}
//			}
//		}
//		return false;
//	}

//	public static final boolean hasDownBook(Context appContext, String productID) {
//		File rootFile = new File(getRootPath(appContext));
//		if (!rootFile.exists() || !rootFile.isDirectory()) {
//			return false;
//		}
//		File[] usersDir = rootFile.listFiles(); // 用户列表
//		if (usersDir == null) {
//			return false;
//		}
//		for (int i = 0; i < usersDir.length; ++i) {
//			File readDir = new File(usersDir[i].getAbsolutePath()
//					+ File.separator + USER_BOOK_DIR + productID);
//			if (readDir.exists() && readDir.isDirectory()) {
//				File absFile = new File(readDir.getAbsolutePath()
//						+ File.separator + BOOK_JSON);
//				if (absFile.exists()) {
//					return true;
//				}
//			}
//		}
//		return false;
//	}

	public static boolean writeStringToFile(String text, File file) {
		try {
			return writeDataToFile(text.getBytes(BOOK_ENCODING), file);
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
			return false;
		}
	}

	public static boolean writeUTF8ToFile(byte[] datas, File file) {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);

			// OutputStreamWriter osw = new
			// OutputStreamWriter(fos,BOOK_ENCODING);
			// osw.write(new String(datas,
			// BOOK_ENCODING).getBytes(BOOK_ENCODING));
			// osw.flush();
			fos.write(new String(datas, BOOK_ENCODING).getBytes(BOOK_ENCODING));
			fos.flush();
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			close(fos);
		}
		return false;
	}

	public static boolean writeDataToFile(byte[] datas, File file) {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			fos.write(datas);
			fos.flush();
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			close(fos);
		}
		return false;
	}

	public static final int MAX_LENGTH = 10 * 1024; // 10k

	public static boolean writeStringToFile(InputStream is, File file) {
		FileOutputStream fos = null;
		DataInputStream dis = null;
		try {
			File f = file.getParentFile();
			if (f.exists() && f.isDirectory())
				;
			else
				f.mkdirs();
			fos = new FileOutputStream(file);
			dis = new DataInputStream(is);
			byte[] data = new byte[MAX_LENGTH];
			int len = dis.read(data);
			while (len > 0) {
				fos.write(data, 0, len);
				/* fos.write(data); */
				fos.flush();
				len = dis.read(data);
			}
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			close(dis);
			close(fos);
		}
		return false;
	}

	public static byte[] getBytesFromFile(File file) {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			return StreamUtils.getBytesFromStream(fis);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			close(fis);
		}
		return null;
	}

	public static String getStringFromFile(File file) {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			byte[] datas = StreamUtils.getBytesFromStream(fis);

			return new String(datas, BOOK_ENCODING);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} finally {
			close(fis);
		}
		return null;
	}

	public static byte[] getBytesFromCompressFile(File file) throws Exception {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			return StreamUtils.getBytesFromStream(fis);
		} finally {
			fis.close();
		}
	}

	/*
	 * public static String getStringFromCompressFile(File file) throws
	 * Exception { FileInputStream fis = null; try { fis = new
	 * FileInputStream(file); byte[] datas =
	 * DRKeyEncry.decrypt(StreamUtils.getBytesFromStream(fis)); return new
	 * String(datas, BOOK_ENCODING); } finally { fis.close(); } }
	 */

	static void close(Closeable c) {
		if (c != null) {
			try {
				c.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * 删除单个文件夹下文件
	 * 
	 * @param file
	 * @return
	 */
	public static boolean deleteCurrFile(File file) {
		try {
			boolean delete = false;
			if (file.exists()) {
                File[] files = file.listFiles();
                if (files != null && files.length > 0) {
                    for (int i = 0; i < files.length; i++) {
                        File oldFile  = files[i];
                        File newFile  = new File(oldFile.getAbsolutePath()+"temp");
                        if(oldFile.renameTo(newFile)){
                            newFile.delete();
                        }else{
                            oldFile.delete();
                        }
    //					files[i].delete();
                    }
                }
                file.delete();
                File oldFile  = file;
                File newFile  = new File(oldFile.getAbsolutePath()+"temp");
                if(oldFile.renameTo(newFile)){
                    newFile.delete();
                }else{
                    oldFile.delete();
                }
                delete = true;
            }
			return delete;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static void copyAllFiles(String fromDir, String toDir)
			throws IOException {
		LogM.d("copyAllFiles()\nfrom=" + fromDir + "\bto=" + toDir);
		File fromDirFile = new File(fromDir);
		File[] filelist = fromDirFile.listFiles();
		if (filelist != null) {
			for (File f : filelist) {
				File to = new File(toDir + File.separator + f.getName());
				if (f.isDirectory()) {
					to.mkdirs();
					copyAllFiles(f.getAbsolutePath(), to.getAbsolutePath());
				} else {
					if (f.canRead()) {
						to.getParentFile().mkdirs();
						byte[] buffer = new byte[1024];
						InputStream fis = new FileInputStream(f);
						OutputStream fos = new FileOutputStream(to);
						int b = fis.read(buffer);
						while (b != -1) {
							fos.write(buffer, 0, b);
							b = fis.read(buffer);
						}
						fis.close();
						fos.flush();
						fos.close();
					}
				}
			}
		}
	}

	public static void copyFile(File sourceFile, File desFile,
			boolean isDeleteSourceFile) throws IOException {
		if (sourceFile.isFile() && sourceFile.exists()) {
			byte[] buffer = new byte[1024];
			InputStream fis = new FileInputStream(sourceFile);
			OutputStream fos = new FileOutputStream(desFile);
			int b = fis.read(buffer);
			while (b != -1) {
				fos.write(buffer, 0, b);
				b = fis.read(buffer);
			}
			fis.close();
			fos.flush();
			fos.close();
			if (isDeleteSourceFile) {
				sourceFile.delete();
			}
		}
	}

	public static void moveAllFiles(String fromDir, String toDir) {
		try {
			copyAllFiles(fromDir, toDir);
		} catch (IOException e) {
			LogM.e("ERROR", "moveAllFiles()\n" + e.toString());
		}
		recurrenceDeleteFile(new File(fromDir));
	}

	public static String getExternalRootPath(Context context){
		if (sAccountManager == null) {
			sAccountManager = new AccountManager(context);
		}
		String path = sAccountManager.getUsrDir();
		if(!TextUtils.isEmpty(path)){
			return path;
		}
		LongSparseArray<String> array = Utils.getExterPath();
		int len = array.size();
		if(len <= 0)
			return getRootPath(context);
		return array.valueAt(len - 1) + ROOT_PATH;
	}
	
	/**
	 * ------------------------------------------------ 获取该书全本存储目录
	 * 
	 * @param context
	 * @param productId
	 * @param userName
	 * @return
	 */
	public static String getFullProductDir(Context context, String productId,
			String userName) {
		try {
			String rootPath = getExternalRootPath(context);
			File rootFile = new File(rootPath);
			if (!rootFile.exists()) {
				rootFile.mkdirs();
			}
			File bookFile = new File(rootFile.getAbsolutePath()
					+ File.separator + userName + File.separator
					+ USER_BOOK_DIR + productId);
			if (!bookFile.exists()) {
				bookFile.mkdirs();
			}
			return bookFile.getAbsolutePath();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取该书试读存储目录
	 * 
	 * @param context
	 * @param productId
	 * @return
	 */
	public static String getTryProductDir(Context context, String productId) {
		try {
			String rootPath = getExternalRootPath(context);
			File rootFile = new File(rootPath);
			if (!rootFile.exists()) {
				rootFile.mkdirs();
			}

			File trybookFile = new File(rootFile.getAbsolutePath()
					+ USER_UNDEFINE_DIR + USER_READ_BOOK_DIR + productId);
			if (!trybookFile.exists()) {
				trybookFile.mkdirs();
			}
			return trybookFile.getAbsolutePath();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取书的Key保存路径
	 * 
	 * @param productDir
	 * @return
	 */
	public static File getBookKey(String productDir) {
		File file = new File(productDir + File.separator + BOOK_KEY);
		return file;
	}

	/**
	 * 获取下载书所要保存的文件
	 * 
	 * @param productDir
	 * @param productId
	 * @return
	 */
	public static File getBookDest(String productDir, String productId,
			BookType type) {
		File destBook;
		if (type == BookType.BOOK_TYPE_NOT_NOVEL)
			destBook = new File(productDir + File.separator + productId
					+ BOOK_SUFFIX);
		else if (type == BookType.BOOK_TYPE_IS_FULL_YES) {
			destBook = new File(getPartBookDir(productId) + productId + ".zip");
		} else
			destBook = new File(getPartBookDir(productId) + productId);
		if (!destBook.exists()) {
			try {
				destBook.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return destBook;
	}

	public static boolean saveBookJson(String productDir, String bookJson) {
		// 测试
		File jsonFile = new File(productDir + File.separator + BOOK_JSON);
		if (!jsonFile.exists() && bookJson != null) {
			// JSONObject jsonObj = downTag.mJsonObject;
			// jsonObj.toString();
			return writeStringToFile(bookJson, jsonFile);
		}
		return true;
	}

	public static boolean checkBookJson(String productDir) {
		File jsonFile = new File(productDir + File.separator + BOOK_JSON);
		return jsonFile.exists();
	}

	public static long getDownLoadStart(String productDir, String productId) {
		File bookFile = new File(productDir + File.separator + productId
				+ BOOK_SUFFIX);
		if (bookFile.exists()) {
			return bookFile.length();
		} else {
			try {
				bookFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return 0;
		}
	}

	public static void writeDownLoadSize(String productDir, int fileSize) {
		File downSize = new File(productDir + File.separator + BOOK_SIZE);
		if (downSize.exists()) {
			downSize.delete();
		}
		writeStringToFile(String.valueOf(fileSize), downSize);
	}

	public static void writeDownloadFinishFile(String productDir) {
		File downSize = new File(productDir + File.separator + BOOK_FINISH);
		if (!downSize.exists()) {
			try {
				downSize.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static boolean hasDownloadFinish(String productDir) {
		File file = new File(productDir + File.separator + BOOK_FINISH);
		return file.exists();
	}

	public static String getFontProductDir(Context context, String indentityId,
			String userName) {
		try {
			String rootPath = getRootPath(context);
			File rootFile = new File(rootPath);
			if (!rootFile.exists()) {
				rootFile.mkdirs();
			}
			File bookFile = new File(rootFile.getAbsolutePath()
					+ File.separator + userName + File.separator + FONT_DIR
					+ indentityId);
			if (!bookFile.exists()) {
				bookFile.mkdirs();
			}
			return bookFile.getAbsolutePath();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static File getFontDownloadSaveFile(String productDir,
			String indentityId) {
		String filename = indentityId + FONT_EXTEN;
		File fontFile = new File(productDir, filename);
		try {
			if (!fontFile.exists()) {
				fontFile.createNewFile();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fontFile;
	}

	public static String getCoverPath(String productid, String bookdir) {
		/*
		 * if (productid.contains(InbuildBooks.PUBLIC_KEY_PREFIX)) { return
		 * bookdir + File.separator + DangdangFileManager.ITEM_BOOK_COVER; //
		 * return getSubString(mBookDir, File.separator, false) + //
		 * File.separator + DangdangFileManager.ITEM_BOOK_COVER; } else {
		 * ShelfService service = ShelfService.getInstance(mContext);
		 * ShelfBookInfo info = service.getShelfBookByBookId(productid); if
		 * (info == null) return ""; return info.getmCoverUrl();//
		 * ResourceManager.getManager().getClipImagPath(info.getmCoverUrl()); }
		 */
		return null;
	}

	public static String getErrorMessageDir(Context context) {
		try {
			String rootPath = getRootPath(context);
			File rootFile = new File(rootPath);
			if (!rootFile.exists()) {
				rootFile.mkdirs();
			}

			File trybookFile = new File(rootFile.getAbsolutePath()
					+ ERROR_LOG_PATH);
			if (!trybookFile.exists()) {
				trybookFile.mkdirs();
			}
			return trybookFile.getAbsolutePath();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 错误数据保存（时间+errormessage）
	 */
	public static boolean saveErrorMessage(String message, boolean isAddTime) {
		String productDir = DangdangFileManager.getErrorMessageDir(mContext);
		String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS")
				.format(new Date());
		if (isAddTime)
			message = currentTime + "   " + message;
		if (productDir != null && message != null) {
			FileWriter out;
			try {
				out = new FileWriter(productDir + File.separator + BOOK_ERROR,
						true);
				out.write(message);
				out.flush();
				out.close();
				return true;
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			return false;
		}
		return false;
	}

	private static void updateErrorLine(int lineNum) {
		if (mConfigManager != null) {
			SharedPreferences sp = mConfigManager.getPreferences();
			SharedPreferences.Editor editor = sp.edit();
			editor.putInt(ConfigManager.KEY_ERROR_LOG_LINE, lineNum);
			editor.commit();
		}
	}

	private static int getErrorLine() {
		int line = 0;
		if (mConfigManager != null) {
			SharedPreferences sp = mConfigManager.getPreferences();
			line = sp.getInt(ConfigManager.KEY_ERROR_LOG_LINE, 0);
		}
		return line;
	}

	// 向上传服务器的统计文件写数据
	public static void writeUpLoadFile(String... param) {
		String rootPath = getRootPath(mContext);
		File rootFile = new File(rootPath);
		if (!rootFile.exists()) {
			rootFile.mkdirs();
		}
		String productDir = DangdangFileManager.getErrorMessageDir(mContext);
		File upLoadFile = new File(productDir + File.separator + BOOK_ERROR);
		StringBuffer sb = new StringBuffer("");
		if (!upLoadFile.exists()) {
			try {
				upLoadFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (param == null) {
			System.out.println("upLoadParam == null");
			return;
		}
		if (param.length == 0) {
			System.out.println("upLoadParam.length == 0");
			return;
		}
		String fileContent = getStringFromFile(upLoadFile);
		if (fileContent != null) {
			sb.append(fileContent);
		}
		for (int i = 0; i < param.length; i++) {
			sb.append(param[i]);
		}

		writeStringToFile(sb.toString(), upLoadFile);
	}

	// 获取错误日记内容
	public static String getUpLoadFileContent() {
		FileReader fileReader;
		int line = 0;
		String temp;
		String fileContent = "";
		StringBuffer sb = new StringBuffer();
		try {
			fileReader = new FileReader(getErrorMessageDir(mContext)
					+ File.separator + BOOK_ERROR);
			if (fileReader != null) {
				BufferedReader buffReader = new BufferedReader(fileReader);
				while ((temp = buffReader.readLine()) != null) {
					line++;
					sb.append(temp).append("\r\n");
				}
				fileReader.close();
				updateErrorLine(line);
				fileContent = sb.toString();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return fileContent;
	}

	// 清空部分统计文件数据
	public static void clearSectionOfUpLoadFile() {
		String temp;
		int line = 0;
		FileReader fileReader;
		try {
			fileReader = new FileReader(getErrorMessageDir(mContext)
					+ File.separator + BOOK_ERROR);
			BufferedReader buffReader = new BufferedReader(fileReader);
			// 此处已经实现了删除一条给定记录
			StringBuffer sb = new StringBuffer();
			boolean isWriteFlag = false;
			while ((temp = buffReader.readLine()) != null) {
				line++;
				if (isWriteFlag) {
					sb.append(temp).append("\r\n");
				}
				if (line == getErrorLine()) {
					isWriteFlag = true;
					continue;
				}

			}
			fileReader.close();
			clearUpLoadFile();
			saveErrorMessage(sb.toString(), false);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 清空统计文件数据
	public static void clearUpLoadFile() {
		File upLoadFile = new File(getErrorMessageDir(mContext)
				+ File.separator + BOOK_ERROR);
		if (!upLoadFile.exists()) {
			return;
		} else {
			writeStringToFile("", upLoadFile);
		}
	}

	public static Bitmap getBitmap(String path, int minSideLength,
			int maxNumOfPixels) {
		Bitmap bm = null;
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inJustDecodeBounds = true;
		opt.inSampleSize = 1;
		bm = BitmapFactory.decodeFile(path, opt);
		// pad拉伸 太难看了
		opt.inSampleSize = computeSampleSize(opt, minSideLength, maxNumOfPixels);
		opt.inJustDecodeBounds = false;
		opt.inDither = false;
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		try {
			bm = BitmapFactory.decodeFile(path, opt);
		} catch (OutOfMemoryError err) {
			LogM.e(TAG, "bitmap decoder failed");
			bm = null;
		}
		return bm;
	}

	public static Bitmap getClipBitmap(String path, int minSideLength,
			int maxNumOfPixels) {
		Bitmap bitmap = getBitmap(path, minSideLength, maxNumOfPixels);
		if (bitmap == null)
			return bitmap;
		Bitmap result = null;
		int bitmapHeight = bitmap.getHeight();
		int bitmapWidth = bitmap.getWidth();
		try {
			result = Bitmap.createBitmap(bitmap, bitmapWidth / 7, 0,
					bitmapWidth * 5 / 7, bitmapHeight);
		} catch (OutOfMemoryError err) {
			result = null;
			LogM.w(TAG, "bitmap decoder failed");
		} finally {
			bitmap.recycle();
		}
		return result;
	}

	public static String switchDateToStr(Date readDate) {
		int month = (readDate.getMonth() + 1); // 月
		int day = readDate.getDate(); // 天
		return month + "/" + day;
	}

	public static String getBookNameWithoutSuffix(String name) {
		String result = name;
		int pos = name.lastIndexOf(BOOKNAME_SIGN);
		if (pos != -1) {
			result = name.substring(0, pos);
		}
		return result;
	}

	public static Bitmap getClipDefaultCover(Context appContext) {
		if (mClipDefaultCover == null) {
			BitmapFactory.Options opt = new BitmapFactory.Options();
			opt.inJustDecodeBounds = true;
			opt.inSampleSize = 1;
			mClipDefaultCover = BitmapFactory.decodeResource(
					appContext.getResources(), R.drawable.default_cover, opt);

			opt.inSampleSize = computeSampleSize(opt, -1, 150 * 150);
			opt.inJustDecodeBounds = false;
			opt.inDither = false;
			opt.inPreferredConfig = Bitmap.Config.RGB_565;
			try {
				mClipDefaultCover = BitmapFactory.decodeResource(
						appContext.getResources(), R.drawable.default_cover,
						opt);
			} catch (OutOfMemoryError err) {
				LogM.e(TAG, "bitmap decoder failed");
				mClipDefaultCover = null;
			}
		}
		return mClipDefaultCover;
	}

	/**
	 * @param mProductID
	 */
	public static void deleteBookmark(Context appContext, String mProductID) {
		// try {
		// BookMarkService bMarkService = new BookMarkService(appContext);
		// bMarkService.deleteBookMark(mProductID);
		// } catch (Exception e) {
		// LogM.e("ERROR:\n" + "deleteBookmark().." + mProductID + "\n"
		// + e.toString());
		// }
	}

	public static boolean isFileExist(String dir) {
		File file = new File(dir);
		return file.exists();
	}

	public static boolean isFileHasChild(String dir) {
		File file = new File(dir);
		if (file.exists() && file.list() != null) {
			if (file.list().length > 0) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 获取预置文件夹,测试环境预置到sd卡，生产环境预置到应用目录files中
	 * 
	 * @return 预置文件夹file
	 */
	public static File getPreSetFile() {
		String preSetDir = "";
//		if (DangdangConfig.isDevelopEnv() && checkMounted()) {
		if (checkMounted()) {
			preSetDir = getRootPath(mContext) + PRE_SET_DIR;
		} else {
//			preSetDir = getRootPath(mContext) + PRE_SET_DIR;
			preSetDir = mContext.getFilesDir() + File.separator + PRE_SET_DIR;
		}
		File file = new File(preSetDir);
		if (!file.exists()) {
			file.mkdirs();
		}
		return file;
	}

	/**
	 * 获取预置书城压缩包的拷贝路径
	 * 
	 * @return
	 */
	public static String getPreSetBookStoreZip() {
		return getPreSetFile().toString() + File.separator + BookStore_Zip;
	}

	/**
	 * 预置字体文件路径
	 * 
	 * @return
	 */
	public static String getPreSetTTF() {
		return getPreSetReadDir() + File.separator + PreSet_Ttf;
	}
	
	public static String getPreSetEnMonoTTF() {

		return getPreSetReadDir() + File.separator + PreSet_EnMonoTtf;
	}


	public static String[] getPreSetEnTTF() {
		final String[] enttfNames = PreSet_EnTtf;
		String[] enTTfPaths = new String[enttfNames.length];
		for (int i = 0; i < enttfNames.length; i++) {
			enTTfPaths[i] = getPreSetReadDir() + File.separator + enttfNames[i];
		}
		return enTTfPaths;
	}

	/*
	 * public static String getPreSetTTFZip(){
	 * 
	 * return getPreSetFile().toString() + File.separator + PreSet_Ttfzip; }
	 */

	/**
	 * 阅读相关文件目录
	 * 
	 * @return
	 */
	public static String getPreSetReadDir() {

		return getPreSetFile().toString() + File.separator + PreSet_ReadDir;
	}

	/**
	 * 阅读相关文件zip文件路径
	 * 
	 * @return
	 */
	public static String getPreSetReadZip() {

		return getPreSetFile().toString() + File.separator + PreSet_ReadZip;
	}

	public static String getPreSetOffPrintDir() {

		return getPreSetFile().toString() + File.separator + PreSet_OffPrintDir;
	}

	public static String getPreSetOffPrintZip() {

		return getPreSetOffPrintDir() + File.separator + PreSet_OffPrintZip;
	}

	public static String getPreSetOffPrintRsaPublic() {
		return getPreSetOffPrintDir() + File.separator + PreSet_OffPrint_Rsa_Public;
	}

	public static String getPreSetOffPrintRsaPrivate() {
		return getPreSetOffPrintDir() + File.separator + PreSet_OffPrint_Rsa_Private;
	}

	public static String getPreSetOffPrintCert() {
		return getPreSetOffPrintDir() + File.separator + PreSet_OffPrint_Certification;
	}

	public static String getPreSetOffPrintEpubBook() {
		return getPreSetOffPrintDir() + File.separator + ReadConfig.PreSet_OffPrint_ProductId + ".epub";
	}

	public static String getPreSetOffPrintPartBook() {
		return getPreSetOffPrintDir() + File.separator + ReadConfig.PreSet_OffPrint_ProductId;
	}

	public static String getBookStoreDir() {
		return getPreSetFile().toString() + File.separator;
	}

	public static String getBookStoreBaseDir() {
		return getPreSetFile().toString() + File.separator + BookStore_Dir;
	}

	/**
	 * 词库文件路径
	 * 
	 * @return
	 */
	public static String getPreSetDictXdb() {

		return getPreSetReadDir() + File.separator + PreSet_DictXdb;
	}

	/**
	 * 词库文件路径
	 * 
	 * @return
	 */
	public static String getPreSetDictRule() {

		return getPreSetReadDir() + File.separator + PreSet_DictRule;
	}

	/**
	 * 字典文件目录
	 * 
	 * @return
	 */
	public static String getReadDictDir() {
		String preSetDir = "";
		if (checkMounted()) {
			preSetDir = getRootPath(mContext) + PRE_SET_DIR;
		} else {
			preSetDir = mContext.getFilesDir() + File.separator + PRE_SET_DIR;
		}
		File file = new File(preSetDir);
		if (!file.exists()) {
			file.mkdirs();
		}
		return preSetDir + File.separator + ReadDict;
	}

	/*
	 * public static boolean isDictExists(){ File dictDirFile = new
	 * File(getPreSetDictDir()); File[] childs = dictDirFile.listFiles();
	 * if(!dictDirFile.exists() || (childs == null || childs.length < 2)){
	 * return false; } return true; }
	 */

	public static boolean isReadExists() {
		File readDirFile = new File(getPreSetReadDir());
		File[] childs = readDirFile.listFiles();
		if (!readDirFile.exists() || (childs == null || childs.length < 3)) {
			return false;
		}
		return true;
	}

	/**
	 * 判断目录是否存在
	 * @return
     */
	public static boolean isDictXdb(){
		File readDirFile = new File(getPreSetDictXdb());
		File[] childs = readDirFile.listFiles();
		if (!readDirFile.exists() || (childs == null || childs.length < 3)) {
			return false;
		}
		return true;
	}

	/**
	 * 判断文件是否存在
	 * @return
     */
	public static boolean isDictRule(){
		File readDirFile = new File(getPreSetDictRule());
		File[] childs = readDirFile.listFiles();
		if (!readDirFile.exists() || (childs == null || childs.length < 3)) {
			return false;
		}
		return true;
	}

	public static boolean isOffPrintExists() {
		File readDirFile = new File(getPreSetOffPrintDir());
		File[] childs = readDirFile.listFiles();
		if (!readDirFile.exists() || (childs == null || childs.length < 4)) {
			return false;
		}
		return true;
	}

	public static String getReadComposingCacheDir() {
//		String dirPath = getAppRootDir()
		String dirPath = getSystemCachePath() + File.separator
				+ READCOMPOSING_CACHE;
		File dirFile = new File(dirPath);
		if (!dirFile.exists()) {
			dirFile.mkdirs();
		}
		return dirPath;
	}

	// public static String getPreSetCss(){
	// return getPreSetFile().toString() + File.separator + PreSet_Css;
	// }

	public static String getFileType(File f) {
		String type = "";
		String fName = f.getName();
		String end = fName
				.substring(fName.lastIndexOf(".") + 1, fName.length())
				.toLowerCase();
		if (end.equals("m4a") || end.equals("mp3") || end.equals("mid")
				|| end.equals("xmf") || end.equals("ogg") || end.equals("wav")) {
			type = "audio";
		} else if (end.equals("3gp") || end.equals("mp4")) {
			type = "video";
		} else if (end.equals("jpg") || end.equals("gif") || end.equals("png")
				|| end.equals("jpeg") || end.equals("bmp")) {
			type = "image";
		} else if (end.equals("txt")) {
			type = "text";
		} else if (end.equals("pdf")) {
			type = "pdf";
		} else if (end.equals("epub")) {
			type = "epub";
		} else {
			type = "*";
		}
		type += "/*";
		return type;
	}

	/**
	 * 对文件按时间倒序排序
	 */
	public static class CompratorByLastModified implements Comparator<File> {
		public int compare(File f1, File f2) {
			long diff = f1.lastModified() - f2.lastModified();
			if (diff < 0)
				return 1;
			else if (diff == 0)
				return 0;
			else
				return -1;
		}

		public boolean equals(Object obj) {
			return true;
		}
	}

	public static void saveFile(final Bitmap bm, File file) {
		OutputStream outStream = null;
		ByteArrayOutputStream baos = null;
		try {
			/*
			 * if(bm.isRecycled()){ return; }
			 */
			baos = new ByteArrayOutputStream();
			bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
			byte[] data = baos.toByteArray();

			outStream = new FileOutputStream(file);
			outStream.write(data, 0, data.length);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeStream(outStream);
			closeStream(baos);
		}
	}

	/*
	 * public static boolean moveFontFile(Context context){ boolean re = true;
	 * try { boolean space_enough = true; File ttfZipFile = new
	 * File(DangdangFileManager.getPreSetTTFZip()); if(!ttfZipFile.exists()){
	 * InputStream ttfInStream =
	 * context.getResources().openRawResource(R.raw.default_blue_font);
	 * space_enough = DangdangFileManager.writeStringToFile(ttfInStream,
	 * ttfZipFile); } //sdcard 空间不足 if(!space_enough){ return false; }
	 * 
	 * final String sourceFile = ttfZipFile.toString(); final String destDir =
	 * ttfZipFile.getParent().toString(); final String ttfpath =
	 * DangdangFileManager.getPreSetTTF(); UnZipOperator unZipTask = new
	 * UnZipOperator(sourceFile, destDir, ttfpath); unZipTask.run();
	 * 
	 * FontListHandle handle = FontListHandle.getHandle(context);
	 * handle.setDefaultFont(ProductDomain.DEFAULT_PRODUCTID);
	 * handle.setDefaultFontName
	 * (context.getString(R.string.read_fangzheng_lantinghei_font));
	 * handle.setDefaultFontPath(DangdangFileManager.getPreSetTTF());
	 * 
	 * File ttfFile = new File(ttfpath);
	 * 
	 * if(ttfFile.exists() && ttfZipFile.exists()){ ttfZipFile.delete(); } }
	 * catch (Exception e) { e.printStackTrace(); re = false; }
	 * 
	 * return re; }
	 */

	/**
	 * 移动\解压阅读相关文件 ttf\词库\hyphen
	 * 
	 * @param context
	 */
	public static boolean moveReadFile(Context context) {

		boolean move = false;
		boolean spaceEnough = true;
		File readZipFile = new File(DangdangFileManager.getPreSetReadZip());
		if (!readZipFile.exists()) {
			InputStream ttfInStream = context.getResources().openRawResource(
					R.raw.readfile);
			spaceEnough = DangdangFileManager.writeStringToFile(ttfInStream,
					readZipFile);
			if (ttfInStream != null) {
				try {
					ttfInStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		// sdcard 空间不足
		if (!spaceEnough) {
			return move;
		}

		String sourceFile = readZipFile.toString();
		String destDir = DangdangFileManager.getPreSetReadDir();
		UnZipOperator unZipTask = new UnZipOperator(sourceFile, destDir);
		unZipTask.run();

		File readDir = new File(destDir);
		if (readDir.exists()) {
			move = true;
			if (isReadExists()) {
				FirstGuideManager.getInstance(context).setFirstGuide(
						FirstGuideManager.FirstGuideTag.IS_FIRST_READFILE,
						false);
				readZipFile.delete();
			}
			// 词库路径和阅读相关文件目录
			String xdbPath = DangdangFileManager.getPreSetDictXdb();
			String rulesPath = DangdangFileManager.getPreSetDictRule();
			String readPath = DangdangFileManager.getPreSetReadDir();

			ReadConfig.getConfig().setDictPath(context, xdbPath, rulesPath);
			ReadConfig.getConfig().setPreReadPath(context, readPath);

			// 设置预置字体路径
			FontListHandle handle = FontListHandle.getHandle(context);
			if (handle.isDefaultFont(ProductDomain.DEFAULT_PRODUCTID)) {// 当用户没有选择字体时，才更改默认字体设置
				handle.setDefaultFont(ProductDomain.DEFAULT_PRODUCTID);
				handle.setDefaultFontName(FontListHandle.getHandle(mContext).getPresetDefaultFontName());
				handle.setDefaultFontPath(DangdangFileManager.getPreSetTTF());
			}
		}
		return move;
	}

	public static boolean moveXdb_rules(Context context){
		File file=new File(getPreSetReadDir());
		if (!file.exists())file.mkdirs();

		String xdbPath = DangdangFileManager.getPreSetDictXdb();
		String rulesPath = DangdangFileManager.getPreSetDictRule();

		boolean xbd = true;
		File readZipFile = new File(xdbPath);
		if (!readZipFile.exists()) {
			InputStream ttfInStream = context.getResources().openRawResource(
					R.raw.dictgbk);
			xbd = DangdangFileManager.writeStringToFile(ttfInStream,
					readZipFile);
			if (ttfInStream != null) {
				try {
					ttfInStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		// sdcard 空间不足
		if (!xbd) {
			return false;
		}
		boolean rules = true;
		File rulesFile = new File(rulesPath);
		if (!rulesFile.exists()) {
			InputStream ttfInStream = context.getResources().openRawResource(
					R.raw.rules);
			rules = DangdangFileManager.writeStringToFile(ttfInStream,
					rulesFile);
			if (ttfInStream != null) {
				try {
					ttfInStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		// sdcard 空间不足
		if (!rules) {
			return false;
		}
		ReadConfig.getConfig().setDictPath(context, xdbPath, rulesPath);
		return true;
	}

	public static boolean moveOffPrintFile(Context context) {

		boolean move = false;
		boolean spaceEnough = true;
		File readZipFile = new File(DangdangFileManager.getPreSetOffPrintZip());
		if (!readZipFile.exists()) {
			InputStream ttfInStream = context.getResources().openRawResource(
					R.raw.offprint);
			spaceEnough = DangdangFileManager.writeStringToFile(ttfInStream,
					readZipFile);
			if (ttfInStream != null) {
				try {
					ttfInStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		// sdcard 空间不足
		if (!spaceEnough) {
			return move;
		}

		String sourceFile = readZipFile.toString();
		String destDir = DangdangFileManager.getPreSetOffPrintDir();
		UnZipOperator unZipTask = new UnZipOperator(sourceFile, destDir);
		unZipTask.run();
//		ZipUtil unZipTask = new ZipUtil();
//		try {
//			unZipTask.unZip(sourceFile, destDir);
//		} catch (UnZipException e) {
//			e.printStackTrace();
//		}

		File readDir = new File(destDir);
		if (readDir.exists()) {
			move = true;
			FirstGuideManager.getInstance(context).setFirstGuide(
					FirstGuideManager.FirstGuideTag.IS_FIRST_OFFPRINT_FILE,
					false);
			readZipFile.delete();
			ReadConfig.getConfig().setOffPrintPath(context, getPreSetOffPrintDir());

		}
		return move;
	}

	public static void moveBookStore(Context context, int resId) {
		InputStream bookstoreStream = context.getResources().openRawResource(
				resId);
		File dictZipFile = new File(getPreSetBookStoreZip());

		boolean spaceEnough = DangdangFileManager.writeStringToFile(
				bookstoreStream, dictZipFile);
		if (bookstoreStream != null) {
			try {
				bookstoreStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		String sourceFile = dictZipFile.toString();
		String destDir = DangdangFileManager.getBookStoreDir();
		ZipUtil unZipTask = new ZipUtil();
		try {
			unZipTask.unZip(sourceFile, destDir);
		} catch (UnZipException e) {
			e.printStackTrace();
		}

		File destDirfile = new File(destDir);
		if (destDirfile.exists() && dictZipFile.exists()) {
			dictZipFile.delete();
		}
	}

	public static Drawable getStartPageDrawable(String path) {
		File file = new File(path);
		if (file.exists()) {
			try {
				Bitmap bt = BitmapFactory.decodeFile(path);
				if (bt.getWidth() > IMAGE_DEFAULT_WIDTH
						&& bt.getHeight() > IMAGE_DEFAULT_HEIGHT) {
					return new BitmapDrawable(bt);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			// return BitmapDrawable.createFromPath(path);
		}
		return null;
	}

	private static void closeStream(Closeable stream) {
		if (stream != null) {
			try {
				stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 对文件按照类型排序
	 */
	public static class CompratorByType implements Comparator<File> {
		public int compare(File f1, File f2) {

			if (f1.isDirectory() && f2.isFile())
				return -1;
			else if ((f1.isDirectory() && f2.isDirectory())
					|| (f1.isFile() && f2.isFile()))
				return 0;
			else
				return 1;
		}

		public boolean equals(Object obj) {
			return true;
		}
	}

	public static String initTxtCoverPath() {
		String path = DROSUtility.getCachePath();
		File file = new File(path + "txt_cover.png");
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		if (file.exists()) {
			return file.getAbsolutePath();
		}
		writeStringToFile(
				mContext.getResources().openRawResource(R.raw.txt_cover), file);
		return file.getAbsolutePath();
	}

	public static boolean nioTransferCopy(File source, File target) {
		FileChannel in = null;
		FileChannel out = null;
		FileInputStream inStream = null;
		FileOutputStream outStream = null;
		try {
			inStream = new FileInputStream(source);
			outStream = new FileOutputStream(target);
			in = inStream.getChannel();
			out = outStream.getChannel();
			in.transferTo(0, in.size(), out);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			close(inStream);
			close(in);
			close(outStream);
			close(out);
		}

		return true;
	}


	// public static String getSignInPath() {
	// String path = "";
	// if (Environment.MEDIA_MOUNTED.equals(Environment
	// .getExternalStorageState())) {
	// path = getRootPathOnSdcard() + "icon.jpg";
	// }
	//
	// return path;
	// }

	public static final String READFILE_DIR = "readfile";
	public static final String IMAGE_CACHE_DIR = "ImageCache";
	public static final String UNDEFINE_DIR = "undefine";
	public static final String PREREAD_DIR = "preread";
	public static final String BOOKSTORE_DIR = "bookstore";
	public static final String BOOK_DIR = "book";
	public static final String PART_BOOK_DIR = "PartBook";
	public static final String PRESET_FILENAME_CSS = "original_style.css";
	public static final String PRESET_FILENAME_DICT_XDB = "raw/dict-gbk.xdb";
	public static final String PRESET_FILENAME_RULES = "raw/rules.ini";
	public static final int BUFFER_SIZE = 10 * 1024; // 缓冲大小，10k

	/**
	 * 获取手机外部空间大小
	 * 
	 * @return byte size
	 */
	public static long getTotalExternalMemorySize() {
		if (checkMounted()) {
			long blockSize = 0;
			long totalBlocks = 0;
			try {
				File path = Environment.getExternalStorageDirectory();
				StatFs stat = new StatFs(path.getPath());
				blockSize = stat.getBlockSize();
				totalBlocks = stat.getBlockCount();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return totalBlocks * blockSize;
		} else {
			return -1;
		}
	}

	/**
	 * 获取存储根文件夹
	 * 
	 * @return
	 */
	public static String getAppRootDir() {
		String rootDir = File.separator + APP_DIR + File.separator;
		if (checkMounted()) {
			rootDir = Environment.getExternalStorageDirectory() + rootDir;
		} else {
			return getAppRomDir();
		}
		return rootDir;
	}

	/**
	 * 获取应用安装目录下根文件夹
	 * 
	 * @return
	 */
	public static String getAppRomDir() {
		String romDir = DDApplication.getApplication().getFilesDir()
				+ File.separator + APP_DIR + File.separator;
		return romDir;
	}

	/**
	 * 预置阅读文件路径，应用安装目录下
	 * 
	 * @return
	 */
	public static String getPresetReadfileDir() {
		String dir = getAppRomDir();
		return dir + UNDEFINE_DIR + File.separator + READFILE_DIR
				+ File.separator;
	}

	/**
	 * 获取免费字体下载路径
	 * 
	 * @return
	 */
	// public static String getDefaultFontDir() {
	// return getUserFontDir(UNDEFINE_DIR);
	// }

	/**
	 * 获取用户字体费字体下载路径
	 * 
	 * @return
	 */
	// public static String getUserFontDir(String userId) {
	// if (TextUtils.isEmpty(userId)) {
	// throw new OriginalException("the userId must not be null");
	// }
	// return getAppRootDir() + userId + File.separator + FONT_DIR +
	// File.separator;
	// }

	/**
	 * 获取图片缓存路径
	 * 
	 * @return
	 */
	public static String getImageCacheDir() {
		return getAppRootDir() + IMAGE_CACHE_DIR + File.separator;
	}

	/**
	 * 获取h5数据缓存路径
	 * 
	 * @return
	 */
	public static String getStoreH5DataCacheDir() {
		return getAppRootDir() + BOOK_STORE_CACHE + File.separator;
	}

	/**
	 * 获取书的路径
	 * 
	 * @param bookId
	 * @return
	 */
	public static String getBookDir(String bookId) {
		String path = getAppRootDir() + BOOK_DIR + File.separator + bookId;
		File dir = new File(path);
		if (!dir.exists())
			dir.mkdirs();
		if (dir.isFile()) {
			dir.delete();
			dir.mkdirs();
		}
		return path + File.separator;
	}

	/**
	 * 获原创书的路径
	 * 
	 * @param bookId
	 * @return
	 */
	public static String getPartBookDir(String bookId) {
		String path = getPreSetOffPrintDir();
		File dir = new File(path);
		if (!dir.exists())
			dir.mkdirs();
		if (dir.isFile()) {
			dir.delete();
			dir.mkdirs();
		}
		return path + File.separator;
	}

	/**
	 * 获取预置书跟文件夹路径
	 * 
	 * @return
	 */
	public static String getPresetBooksDir() {
		return getAppRootDir() + BOOK_DIR + File.separator;
	}

	/**
	 * 获取内置书城解压后的文件路径
	 * 
	 * @return
	 */
	public static String getPresetBookStoreDir() {
		String dir = "";
		if (DangdangConfig.isDevelopEnv() && checkMounted()) {
			dir = getAppRootDir();
		} else {
			dir = getAppRomDir();
		}
		return dir + UNDEFINE_DIR + File.separator + PREREAD_DIR
				+ File.separator + BOOKSTORE_DIR + File.separator;
	}

	/**
	 * 获取内置书城解压目录
	 * 
	 * @return
	 */
	public static String getPreSetBookStoreUnzipDir() {
		// TODO
		String dir = "";
		if (DangdangConfig.isDevelopEnv() && checkMounted()) {
			dir = getAppRootDir();
		} else {
			dir = getAppRomDir();
		}
		return dir + UNDEFINE_DIR + File.separator + PREREAD_DIR
				+ File.separator;
	}

	/**
	 * 拷贝并解压书城内置的html文件
	 * 
	 * @param context
	 * @param resId
	 */
	public static void copyAndUnzipPreStore(Context context, int resId) {
		// 拷贝
		InputStream bookstoreStream = context.getResources().openRawResource(
				resId);
		File zipFile = new File(getPreSetBookStoreZip());
		writeStringToFile(bookstoreStream, zipFile);
		if (bookstoreStream != null) {
			try {
				bookstoreStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// 解压
		String sourceFile = zipFile.toString();
		String destDir = getPreSetBookStoreUnzipDir();
		ZipUtil unZipTask = new ZipUtil();
		try {
			unZipTask.unZip(sourceFile, destDir);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 删除压缩文件
		File destDirfile = new File(destDir);
		if (destDirfile.exists() && zipFile.exists()) {
			zipFile.delete();
		}
	}

	public static String getReadCssPath() {
		return getPresetReadfileDir() + PRESET_FILENAME_CSS;
	}

	public static String getReadDictXDBPath() {
		return getPresetReadfileDir() + PRESET_FILENAME_DICT_XDB;
	}

	public static String getReadRulesPath() {
		return getPresetReadfileDir() + PRESET_FILENAME_RULES;
	}

	/**
	 * 获取本地启动图
	 * 
	 * @return
	 */
	public static Bitmap getBootBitmap() {
		File file = new File(getBootBitmapPath());
		if (file.exists()) {
			Bitmap bt = BitmapFactory.decodeFile(getBootBitmapPath());
			if (bt.getWidth() > IMAGE_DEFAULT_WIDTH
					&& bt.getHeight() > IMAGE_DEFAULT_HEIGHT) {
				return bt;
			}
		}
		return null;
	}

	/**
	 * 获取本地启动图路径
	 * 
	 * @return
	 */
	public static String getBootBitmapPath() {
		File dir = new File(getAppRomDir());
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return getAppRomDir() + "boot_bitmap";
	}

	/**
	 * 书城h5数据缓存
	 * 
	 * @param data
	 * @param fileName
	 */
	public static boolean writeStoreH5DataToFile(String data, String fileName) {
		// if (checkMounted()) {
		// String filepath = getStoreH5DataCacheDir();
		// File file = new File(filepath);
		// if (!file.exists()) {
		// file.mkdirs();
		// }
		// try {
		// file = new File(getStoreH5DataCacheDir() + fileName);
		// return writeDataToFile(data.getBytes(BOOK_ENCODING), file);
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		// }
		if (checkMounted()) {
			String filepath = getStoreH5DataCacheDir();
			File file = new File(filepath);
			if (!file.exists()) {
				file.mkdirs();
			}
			try {

				file = new File(getStoreH5DataCacheDir() + fileName);
				ObjectOutputStream out = new ObjectOutputStream(
						new FileOutputStream(file));
				out.writeObject(data);
				out.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public static String readStoreH5DataFromFile(String fileName) {
		String result = "";
		File file = new File(getStoreH5DataCacheDir() + fileName);
		if (file.exists() && file.length() > 0) {
			// result = getStringFromFile(file);
			// if (result == null)
			// return "";
			try {
				ObjectInputStream is = new ObjectInputStream(
						new FileInputStream(file));
				result = (String) is.readObject();
				is.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	public static String getApkDir() {
		String path = getAppRootDir() + "apk/";
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}
		return path;
	}

	public static String getFontProductDir(String indentityId, String userName) {
		try {
			String rootPath = getAppRootDir();
			File rootFile = new File(rootPath);
			if (!rootFile.exists()) {
				rootFile.mkdirs();
			}
			File bookFile = new File(rootFile.getAbsolutePath()
					+ File.separator + userName + File.separator + FONT_DIR
					+ indentityId);
			if (!bookFile.exists()) {
				bookFile.mkdirs();
			}
			return bookFile.getAbsolutePath();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取发现详情截图绝对路径
	 * 
	 * @return
	 */
	public static String getFindDetailPrintscreenPath() {
		return getImageCacheDir() + "find_share.jpg";
	}

	/**
	 * 清空指定目录
	 * 
	 * @param dir
	 * @return
	 */
	public static boolean deleteDir(File dir) {
		if (dir == null) {
			return false;
		}
		if (dir.isDirectory()) {
			String[] children = dir.list();
			if (children != null) {
				for (int i = 0; i < children.length; i++) {
					boolean success = deleteDir(new File(dir, children[i]));
					if (!success) {
						return false;
					}
				}
			}
		}
		return dir.delete();
	}

	public static void checkDirs(String pId) {
		String bookPath = getBookDir(pId);
		File file = new File(bookPath);
		if (!file.exists())
			file.mkdirs();
		else {
			if (!file.isDirectory()) {
				file.delete();
				file.mkdirs();
			}
		}
	}

	/**
	 * 写入apk
	 * 
	 * @param mContext
	 */
//	public static void writePluginApk(final Context mContext) {
//		new Thread(new Runnable() {
//
//			@Override
//			public void run() {
//				File f = DangdangFileManager.getApkFile(
//						DangdangFileManager.getAppApkDir(), PLUGINAPK_NAME);
//				if (DangdangFileManager.getFileManagerInstance()
//						.copyFilesFromAssets(mContext, PLUGINAPK_NAME, f))
//					return;
//				f = DangdangFileManager.getApkFile(
//						DangdangFileManager.getSdcardApkDir(), PLUGINAPK_NAME);
//				if (!DangdangFileManager.getFileManagerInstance()
//						.copyFilesFromAssets(mContext, PLUGINAPK_NAME, f)) {
//					// UiUtil.showToast(mContext, R.string.sdcard_space_error);
//				}
//			}
//
//		}).start();
//	}

	/**
	 * 从assets目录中复制文件内容(先放到系统目录，写失败的话放到SD卡上)
	 * 
	 * @param context
	 *            Context 使用CopyFiles类的Activity
	 * @param oldPath
	 *            String 原文件路径 如：aa
	 * @param newFile
	 *            String 复制后路径 如：xx:/bb/cc
	 */
	public boolean copyFilesFromAssets(final Context context,
			final String oldPath, final File newFile) {
		boolean isSuccess = false;

		try {
			// 如果是文件
			InputStream is = context.getAssets().open(oldPath);
			FileOutputStream fos = new FileOutputStream(newFile);
			byte[] buffer = new byte[1024];
			int byteCount = 0;
			while ((byteCount = is.read(buffer)) != -1) {// 循环从输入流读取
															// buffer字节
				fos.write(buffer, 0, byteCount);// 将读取的输入流写入到输出流
			}
			fos.flush();// 刷新缓冲区
			is.close();
			fos.close();
			isSuccess = true;
		} catch (Exception e) {
			e.printStackTrace();
			isSuccess = false;

		}

		return isSuccess;
	}

}
