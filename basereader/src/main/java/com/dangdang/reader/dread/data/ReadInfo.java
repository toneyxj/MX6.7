package com.dangdang.reader.dread.data;

import android.text.TextUtils;

import com.dangdang.reader.dread.config.ReadConfig;
import com.dangdang.reader.dread.format.BaseReadInfo;
import com.dangdang.reader.dread.format.Book.BaseNavPoint;
import com.dangdang.reader.dread.format.Chapter;
import com.dangdang.reader.dread.util.DreaderConstants;
import com.dangdang.reader.personal.domain.ShelfBook;
import com.dangdang.zframework.log.LogM;
import com.mx.mxbase.constant.APPLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * @author luxu
 */
public class ReadInfo extends BaseReadInfo {

	/**
	 * 全本标识
	 */
	public final static int BSTATUS_FULL = 1;
	/**
	 * 试读标识
	 */
	public final static int BSTATUS_TRY = 0;

	
	private final static String JSONK_INDEXINHTML = "elementindex";
	private final static String JSONK_EPUBVERSION = "epubversion";
	private final static String JSONK_UNZIP_STATUSINT = "unzipstatus";
	/**
	 * 获取书签笔记列表版本时间
	 */
	private final static String JSONK_VERSION_TIME = "versiontime";
	/**
	 * 阅读进度操作时间
	 */
	private final static String JSONK_PROGRESS_OPERATETIME = "progress_operatetime";

	private String userId;

	/**
	 * true: 全本
	 * false: 试读
	 *
	 * @return
	 */
	//private boolean isBought = false;
	//private String pId = "";
	private String bookDesc = "";
	private String authorName = "";
//	private String bookName = "";
	//public String bookFile;
	//private String bookDir;
	private Chapter readChapter;
	private int chapterIndex = 0;
	private boolean hasLocalProgress = false;
	//private int pageIndex;

	/**
	 * labelIndex by chapterHtml
	 */
	//public int labelIndex;

	/**
	 * elementIndex by chapterHtml
	 */
	private int elementIndex;

	/**
	 * elelemtIndexByLabel by label( ... labelIndex )
	 */
	//public int elelemtIndexByLabel;


	private byte[] bookCertKey;

	private String epubVersion = "";
	private String epubModVersion = DreaderConstants.BOOK_MODIFY_VERSION;
	/**
	 * 内核版本
	 */
	private int kernelVersion;
	/**
	 * 影响排版结果的版本
	 */
	private int kernelComsVersion;


	/**
	 * 默认值-2，
	 * 成功1
	 * 未成功-1;
	 */
	private int unZipStatusToInt = EpubStatus.S_DEFAULT;

	/**
	 * 解压epub是否成功
	 */
	//private boolean unZipStatus = false;
	
	/**
	 * 阅读进度操作时间
	 * 单位：秒
	 */
	private long operateTime = 0;

	/**
	 * 书签、笔记版本时间
	 */
	private long versionTime = 0;

	/**
	 * 打开阅读时的进度chapterindex,不跟随当前阅读而变化
	 */
	private int prevChapterIndex;
	/**
	 * 打开阅读时的进度elementindex,不跟随当前阅读而变化
	 */
	private int prevElementIndex;

	/**
	 * 打开阅读时的操作时间,不跟随当前阅读而变化
	 */
	private long prevOperateTime;

	/**
	 * 缓存书大小
	 */
	//private long fileSize = 0;
	private byte[] bookStructDatas = null;
	private List<Chapter> chapterList;
	private List<BaseNavPoint> navPointList;
	private int mTryOrFull; //试读或者全本, 0 内置试读 ，1 试读， 2购买 全本, 3 内置全本， 4 借阅全本
	private String mBookCover;
	private String internetBookCover;
	private String mBookJson;
	private boolean mIsOthers;

	private boolean mIsLandScape;


	/**
	 * 修改原来progressInfo中值
	 * @return
	 */
	public String buildProgressInfo(boolean isPdf) {
		return buildProgressInfo("",isPdf);
	}

	/**
	 * 修改原来progressInfo中值
	 * @return
	 */
	public String buildProgressInfo(String value,boolean isPdf) {
		String progressInfo = getProgressInfo();
		try {
			JSONObject jObj = null;
			if(TextUtils.isEmpty(progressInfo)){
				jObj = new JSONObject();
			} else {
				jObj = new JSONObject(progressInfo);
			}
//			jObj.put(JSONK_HTMLINDEX, chapterHtml);
			jObj.put(JSONK_HTMLINDEX, chapterIndex);
			if(isPdf){
				final int pdfPageIndex = chapterIndex;
				jObj.put(JSONK_PAGEINDEX, pdfPageIndex);
				jObj.put(JSONK_PDFREFLOW_STATUS, PDF_REFLOW_YES);
			}
			jObj.put(JSONK_INDEXINHTML, elementIndex);
			jObj.put(JSONK_EPUBVERSION, epubVersion);
			jObj.put(JSONK_KERNEL_VERSION, kernelVersion);
			jObj.put(JSONK_KERNEL_COMPOSVERSION, kernelComsVersion);
			jObj.put(JSONK_UNZIP_STATUSINT, unZipStatusToInt);
			jObj.put(JSONK_PROGRESS_FLOAT, getProgressFloat());
			jObj.put(JSONK_VERSION_TIME, versionTime);
			jObj.put(JSONK_PROGRESS_OPERATETIME, operateTime);
			jObj.put("readerProgress", value);

			progressInfo = jObj.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return progressInfo;
	}


	/**
	 * @param shelfBook
	 * @return 是否是同一文件  true:是
	 */
	public boolean convertData(ShelfBook shelfBook, boolean isPdf) {
		parserProgressInfo(ReadConfig.getConfig().getReadProgress(), isPdf);
		if(shelfBook == null)
			return false;
		
		userId = shelfBook.getUserId();
		bookCertKey = shelfBook.getBookKey();

		setBookStructDatas(shelfBook.getBookStructDatas());

		mTryOrFull = shelfBook.getTryOrFull().ordinal();
		mBookCover = shelfBook.getCoverPic();
		mBookJson = shelfBook.getBookJson();
		mIsOthers = shelfBook.getIsOthers();

		return true;
	}

	public boolean isTheSameFile(final String filePath, final long cacheFileSize) {

		boolean same = false;
		try {
			final File file = new File(filePath);
			final long fileSize = file.length();
			if (fileSize == cacheFileSize) {
				same = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			same = true;
		}
		printLog(" isTheSameFile = " + same);
		return same;
	}


	public void parserProgressInfo(String progressInfo, boolean isPdf) {

		try {
			printLog(progressInfo);
			if (progressInfo == null) {
				return;
			}
			JSONObject jObj = new JSONObject(progressInfo);
			
			setProgressInfo(progressInfo);
			hasLocalProgress = true;
			
			//chapterHtml = jObj.optString(JSONK_HTMLINDEX);
			chapterIndex = jObj.optInt(JSONK_HTMLINDEX);
			APPLog.e("");
			elementIndex = jObj.optInt(JSONK_INDEXINHTML);
			epubVersion = jObj.optString(JSONK_EPUBVERSION);
			kernelVersion = jObj.optInt(JSONK_KERNEL_VERSION);
			kernelComsVersion = jObj.optInt(JSONK_KERNEL_COMPOSVERSION);
			unZipStatusToInt = jObj.optInt(JSONK_UNZIP_STATUSINT, EpubStatus.S_DEFAULT);
			versionTime = jObj.optLong(JSONK_VERSION_TIME);

			float progressFloat = (float) jObj.optDouble(JSONK_PROGRESS_FLOAT);
			long operateTime = jObj.optLong(JSONK_PROGRESS_OPERATETIME);
			setProgressFloat(progressFloat);
			setOperateTime(operateTime);
			setPrevOperateTime(operateTime);

			prevChapterIndex = chapterIndex;
			prevElementIndex = elementIndex;

//			mDayReadStartChapterIndex = jObj.optInt(JSONK_READPLAN_START_CHAPTER);
//			mDayReadStartElementIndex = jObj.optInt(JSONK_READPLAN_START_ELEMENT);
//			mDayReadCurChapterIndex = jObj.optInt(JSONK_READPLAN_READING_CHAPTER);
//			mDayReadCurElementIndex = jObj.optInt(JSONK_READPLAN_READING_ELEMENT);
//			mDayReadRate = (float)jObj.optDouble(JSONK_READPLAN_TARGET_RATE);
			

		} catch (JSONException e) {
			//e.printStackTrace();
			LogM.e(getClass().getSimpleName(), " setProgressInfo Exception ");
		}

	}

	public boolean isUnZipDefaultStatus() {
		return unZipStatusToInt == EpubStatus.S_DEFAULT;
	}

	public boolean isUnZipStatus() {
		return unZipStatusToInt == EpubStatus.S_SUCCESS;
	}

	public void setUnZipStatus(boolean unZipStatus) {
		//this.unZipStatus = unZipStatus;
		unZipStatusToInt = unZipStatus ? EpubStatus.S_SUCCESS : EpubStatus.S_FAILED;
	}

	public boolean hasCacheChapterList() {
		return chapterList != null && chapterList.size() > 0;
	}

	/*public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}*/

	public byte[] getBookStructDatas() {
		return bookStructDatas;
	}

	public void setBookStructDatas(byte[] bookStructDatas) {
		this.bookStructDatas = bookStructDatas;
	}

	public List<Chapter> getChapterList() {
		return chapterList;
	}

	public void setChapterList(List<Chapter> chapterList) {
		this.chapterList = chapterList;
	}

	public List<BaseNavPoint> getNavPointList() {
		return navPointList;
	}

	public void setNavPointList(List<BaseNavPoint> navPointList) {
		this.navPointList = navPointList;
	}

	public void resetProgress() {
		chapterIndex = 0;
		elementIndex = 0;
		epubVersion = "";
		unZipStatusToInt = EpubStatus.S_DEFAULT;
		setProgressFloat(0);
	}

	public long getOperateTime() {
		return operateTime;
	}

	public void setOperateTime(long operateTime) {
		this.operateTime = operateTime;
	}

	public long getPrevOperateTime() {
		return prevOperateTime;
	}

	public void setPrevOperateTime(long prevOperateTime) {
		this.prevOperateTime = prevOperateTime;
	}

	public long getVersionTime() {
		return versionTime;
	}

	public void setVersionTime(long versionTime) {
		this.versionTime = versionTime;
	}

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    /**
	 * 如果是内置的(包含has_key_)
	 *
	 * @return
	 */
	/*public String getDefaultPid() {
		return pId;
	}*/

	/**
	 * 如果是内置的(包含has_key_)
	 *
	 * @return
	 */
	/*public void setDefaultPid(String pid) {
		this.pId = pid;
	}*/

	/*public boolean isBought() {
		return isBought;
	}

	public void setBought(boolean isBought) {
		this.isBought = isBought;
	}*/

	/*public String getBookName() {
		return bookName;
	}

	public void setBookName(String bookName) {
		this.bookName = bookName;
	}*/
	public boolean hasLocalProgress() {
		return hasLocalProgress;
	}

	public int getChapterIndex() {
		return chapterIndex;
	}

	public void setChapterIndex(int chapterIndex,int index) {
		this.chapterIndex = chapterIndex;
	}

	public int getElementIndex() {
		return elementIndex;
	}

	public void setElementIndex(int elementIndex) {
		this.elementIndex = elementIndex;
	}

	public int getPrevChapterIndex() {
		return prevChapterIndex;
	}

	public int getPrevElementIndex() {
		return prevElementIndex;
	}

	public void setPrevChapterIndex(int chapterIndex) {
		prevChapterIndex = chapterIndex;
	}

	public void setPrevElementIndex(int elementIndex) {
		prevElementIndex = elementIndex;
	}
    public void initChapterIndexAndElementIndex(int chapterIndex,int elementIndex){
        this.chapterIndex=chapterIndex;
        this.elementIndex=elementIndex;
    }
	public byte[] getBookCertKey() {
		return bookCertKey;
	}

	public void setBookCertKey(byte[] bookCertKey) {
		this.bookCertKey = bookCertKey;
	}

	/*public String getBookDir() {
		return bookDir;
	}

	public void setBookDir(String bookDir) {
		this.bookDir = bookDir;
	}*/

	public String getEpubVersion() {
		return epubVersion;
	}

	public void setEpubVersion(String epubVersion) {
		this.epubVersion = epubVersion;
	}

	
	public String getEpubModVersion() {
		return epubModVersion;
	}

	public void setEpubModVersion(String epubModVersion) {
		this.epubModVersion = epubModVersion;
	}

	public int getKernelVersion() {
		return kernelVersion;
	}

	public void setKernelVersion(int kernelVersion) {
		this.kernelVersion = kernelVersion;
	}

	public int getKernelComsVersion() {
		return kernelComsVersion;
	}

	public void setKernelComsVersion(int kernelComsVersion) {
		this.kernelComsVersion = kernelComsVersion;
	}

	/**
	 * @return
	 */
	public int getBookType() {
		return isBoughtToInt();
	}

	public Chapter getReadChapter() {
		return readChapter;
	}

	public void setReadChapter(Chapter readChapter) {
		this.readChapter = readChapter;
	}

	public boolean isDDBook() {
		boolean ret = false;
		if (getProductId().matches("\\d*"))
			ret = true;
		return ret;
	}

	private void printLog(String msg) {
		LogM.i(getClass().getSimpleName(), "" + msg);
	}

	public static boolean isFullBook(int bookType) {
		return BSTATUS_FULL == bookType;
	}

	public static boolean isTryBook(int bookType) {
		return BSTATUS_TRY == bookType;
	}

	public static class EpubStatus {

		/**
		 * 默认
		 */
		public final static int S_DEFAULT = -2;
		/**
		 * 失败
		 */
		public final static int S_FAILED = -1;
		/**
		 * 成功
		 */
		public final static int S_SUCCESS = 1;

	}

	/**
	 * 试读或者全本, 0 内置试读 ，1 试读， 2 购买全本, 3 内置全本， 4 借阅全本
	 *
	 * @return
	 */
	public int getTryOrFull() {
		return mTryOrFull;
	}
	
	public void setTryOrFull(int tryOrFull){
		mTryOrFull = tryOrFull;
	}

	public String getTryOrFullStatisticsString() {
		String ret = "";
		if (mIsOthers)
			ret = "steal";
		else {
			/*switch (mTryOrFull) {
				case BSEBookDetailOption.TryOrFullValue.BUILT_IN_TRY:
				case BSEBookDetailOption.TryOrFullValue.COMMON_TRY:
					ret = "tryRead";
					break;
				case BSEBookDetailOption.TryOrFullValue.BORROW_FULL:
					ret = "borrow";
					break;
				default:
					break;
			}*/
		}
		return ret;
	}

	public String getBookCover() {
		return mBookCover;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getBookJson() {
		return mBookJson;
	}

    public String getBookDesc() {
        return bookDesc;
    }

    public void setBookDesc(String bookDesc) {
        this.bookDesc = bookDesc;
    }

    public boolean getIsOthers() {
		return mIsOthers;
	}

    public String getInternetBookCover() {
        return internetBookCover;
    }

    public void setInternetBookCover(String internetBookCover) {
        this.internetBookCover = internetBookCover;
    }

	public boolean isLandScape() {
		return mIsLandScape;
	}

	public void setLandScape(boolean mIsLandScape) {
		this.mIsLandScape = mIsLandScape;
	}

	@Override
	public String toString() {
		return "ReadInfo{" +
				"userId='" + userId + '\'' +
				", bookDesc='" + bookDesc + '\'' +
				", authorName='" + authorName + '\'' +
				", readChapter=" + readChapter +
				", chapterIndex=" + chapterIndex +
				", hasLocalProgress=" + hasLocalProgress +
				", elementIndex=" + elementIndex +
				", bookCertKey=" + Arrays.toString(bookCertKey) +
				", epubVersion='" + epubVersion + '\'' +
				", epubModVersion='" + epubModVersion + '\'' +
				", kernelVersion=" + kernelVersion +
				", kernelComsVersion=" + kernelComsVersion +
				", unZipStatusToInt=" + unZipStatusToInt +
				", operateTime=" + operateTime +
				", versionTime=" + versionTime +
				", prevChapterIndex=" + prevChapterIndex +
				", prevElementIndex=" + prevElementIndex +
				", prevOperateTime=" + prevOperateTime +
				", bookStructDatas=" + Arrays.toString(bookStructDatas) +
				", chapterList=" + chapterList +
				", navPointList=" + navPointList +
				", mTryOrFull=" + mTryOrFull +
				", mBookCover='" + mBookCover + '\'' +
				", internetBookCover='" + internetBookCover + '\'' +
				", mBookJson='" + mBookJson + '\'' +
				", mIsOthers=" + mIsOthers +
				", mIsLandScape=" + mIsLandScape +
				'}';
	}
}
