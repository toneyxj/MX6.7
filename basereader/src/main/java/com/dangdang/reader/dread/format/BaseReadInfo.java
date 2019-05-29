package com.dangdang.reader.dread.format;

import android.content.res.Configuration;

import com.dangdang.reader.dread.config.ReadConfig;
import com.dangdang.reader.dread.data.ReadInfo;
import com.dangdang.reader.dread.jni.BaseJniWarp;
import com.dangdang.reader.utils.InbuildBooks;

import org.json.JSONException;
import org.json.JSONObject;

public class BaseReadInfo {
	
	public final static String JSONK_READ_START_TIME = "startTime";
	public final static String JSONK_READ_PAUSE_TIME = "pauseTime";
	public final static String JSONK_READ_END_TIME = "endTime";
	
	/**
	 * 本书排版时使用的内核版本
	 */
	public final static String JSONK_KERNEL_VERSION = "kernel_version";
	/**
	 * 缓存排版结果的版本号
	 */
	public final static String JSONK_KERNEL_COMPOSVERSION = "kernel_composingversion";
	
	public final static String JSONK_PROGRESS_FLOAT = "progress";
	public final static String JSONK_HTMLINDEX = "htmlindex";
	public final static String JSONK_PAGEINDEX = "pdf_pageindex";
	public final static String JSONK_EXIT_ORIENTATION = "exitOrientation";
	
	/**
	 * pdf
	 */
	public final static String JSONK_SCALE = "pdf_scale";
	public final static String JSONK_DISPLAY_MODE = "pdf_display_mode";
	public final static String JSONK_PAGE_WIDTH = "pageWidth";
	public final static String JSONK_PAGE_HIGHT = "pageHight";
	public final static String JSONK_PATCH_X = "patchX";
	public final static String JSONK_PATCH_Y = "patchY";
	public final static String JSONK_PATCH_WIDTH = "patchWidth";
	public final static String JSONK_PATCH_HIGHT = "patchHight";
	public final static String JSONK_IS_CLIP = "isClip";
	public final static String JSONK_SYMMETRY_TYPE = "symmetryType";
	public final static String JSONK_AUTOFIT_STATUS = "autofit";
	public final static String JSONK_PDFREFLOW_STATUS = "reflowstatus";
	
	public final static int AUTOFIT_YES = 1;
	public final static int AUTOFIT_NO = 0;
	
	public final static int PDF_REFLOW_YES = 1;
	public final static int PDF_REFLOW_NO = 0;
	
	public final static int CATEGORY_NORMAL = 0;
	public final static int CATEGORY_COMICS = 1;

	private int mEBookType;// 主要指epub 、drm epub
	private String bookFile;
	/**
	 * true: 全本
	 * false: 试读
	 *
	 * @return
	 */
	private boolean isBought = false;
	private String defaultPid = "";
	private String bookName = "";
	private String bookDir;

	private String mReadTimeInfo;
	private String mProgressInfo;
	
	private float progressFloat = 0f;
	
	/** PDF剪切相关 */
	private boolean isClip;
	private int symmetryType;
	private float sourceScale;
	private int exitOrientation = Configuration.ORIENTATION_PORTRAIT;
	private int autoFitStatus = AUTOFIT_NO;//自动适配状态
	private int reflowStatus = PDF_REFLOW_NO;
	
	private int category = CATEGORY_NORMAL;
	private boolean isSupportTTS=true;
	private boolean isSupportConvert=true;
	private  boolean speekStaus=false;
	private  boolean onpause=false;

	/**
	 * 语音播报状态
	 * @return
	 */
	public  synchronized boolean isSpeekStaus() {
		return speekStaus;
	}

	public synchronized void setSpeekStaus(boolean speekStaus) {
		this.speekStaus = speekStaus;
	}

	public  boolean isOnpause() {
		return onpause;
	}

	public void setOnpause(boolean onpause) {
		this.onpause = onpause;
	}

	public boolean isSupportTTS() {
		return isSupportTTS;
	}

	public void setIsSupportTTS(boolean isSupportTTS) {
		this.isSupportTTS = isSupportTTS;
	}

	public boolean isSupportConvert() {
		return isSupportConvert;
	}

	public void setIsSupportConvert(boolean isSupportConvert) {
		this.isSupportConvert = isSupportConvert;
	}

	public String getBookFile() {
		return bookFile;
	}

	public void setBookFile(String bookFile) {
		this.bookFile = bookFile;
	}

	public boolean isBought() {
		return isBought;
	}

	public void setBought(boolean isBought) {
		this.isBought = isBought;
	}
	
	/**
	 * 获取是否全本标识
	 *
	 * @return
	 */
	public int isBoughtToInt() {
		return isBought() ? ReadInfo.BSTATUS_FULL : ReadInfo.BSTATUS_TRY;
	}
	
	public boolean isPreSet() {
		boolean isPre = false;
		String defaultPid = getDefaultPid();
		if (defaultPid != null && defaultPid.startsWith(InbuildBooks.PUBLIC_KEY_PREFIX)) {
			isPre = true;
		}
		return isPre;
	}
	
	/**
	 * 真实书Id, 如果是内置的(除去前缀has_key_的)
	 *
	 * @return
	 */
	public String getProductId() {
		String productId = getDefaultPid();
		if (isPreSet()) {//
			productId = productId.replaceFirst(InbuildBooks.PUBLIC_KEY_PREFIX + "_", "");
		}
		return productId;
	}

	/**
	 * 如果是内置的(包含has_key_)
	 *
	 * @return
	 */
	public String getDefaultPid() {
		return defaultPid;
	}

	public void setDefaultPid(String defaultPid) {
		this.defaultPid = defaultPid;
	}

	public String getBookName() {
		if(bookName == null){
			return "";
		}
		return bookName;
	}

	public void setBookName(String bookName) {
		this.bookName = bookName;
	}

	public String getBookDir() {
		return bookDir;
	}

	public void setBookDir(String bookDir) {
		this.bookDir = bookDir;
	}
	
	public String makeReadTimeInfo(long readStartTime, long readTotalTime, long readEndTime) {
		mReadTimeInfo = "";
		try {
			JSONObject jObj = new JSONObject();
			// jObj.put(JSONK_HTMLINDEX, chapterHtml);
			jObj.put(JSONK_READ_START_TIME, readStartTime);
			jObj.put(JSONK_READ_PAUSE_TIME, readTotalTime);
			jObj.put(JSONK_READ_END_TIME, readEndTime);
			mReadTimeInfo = jObj.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return mReadTimeInfo;
	}

	public String getReadTimeInfo() {
		return mReadTimeInfo;
	}

	public void setReadTimeInfo(String info) {
		mReadTimeInfo = info;
	}

	public void setEBookType(int bookType) {
		this.mEBookType = bookType;
	}
	
	public int getEBookType() {
		return mEBookType;
	}
	
	public boolean isDangEpub(){
		return getEBookType() == BaseJniWarp.BOOKTYPE_DD_DRM_EPUB||getEBookType()==BaseJniWarp.BOOKTYPE_DD_DRM_HTML||getEBookType()==BaseJniWarp.BOOKTYPE_DD_DRM_COMICS;
	}

	public String getProgressInfo() {
		return mProgressInfo;
	}

	public void setProgressInfo(String progressInfo) {
		this.mProgressInfo = progressInfo;
	}
	
	public float getProgressFloat() {
		return progressFloat;
	}

	public void setProgressFloat(float progressFloat) {
		this.progressFloat = progressFloat;
	}
	
	public int getExitOrientation() {
		return exitOrientation;
	}

	public void setExitOrientation(int exitOrientation) {
		this.exitOrientation = exitOrientation;
	}

	public float getSourceScale() {
		return sourceScale;
	}

	public void setSourceScale(float sourceScale) {
		this.sourceScale = sourceScale;
	}

	public boolean isClip() {
		return isClip;
	}

	public void setClip(boolean isClip) {
		this.isClip = isClip;
	}


	public int getSymmetryType() {
		return symmetryType;
	}

	public void setSymmetryType(int symmetryType) {
		this.symmetryType = symmetryType;
	}
	


	public int getAutoFitStatus() {
		return autoFitStatus;
	}

	public void setAutoFitStatus(int autoFitStatus) {
		this.autoFitStatus = autoFitStatus;
	}
	
	public void setAutoFitStatus(boolean autoFit) {
		this.autoFitStatus = autoFit ? AUTOFIT_YES : AUTOFIT_NO;
	}

	/**
	 * pdf奇偶对称（当前为奇数）
	 * @return
	 */
	public boolean isOddSymmetry(){
		return ReadConfig.isOddSymmetry(getSymmetryType());
	}
	
	/**
	 * pdf奇对称（当前为偶数）
	 * @return
	 */
	public boolean isEvenSymmetry(){
		return ReadConfig.isEvenSymmetry(getSymmetryType());
	}
	
	public boolean isSymmetry(){
		return isOddSymmetry() || isEvenSymmetry();
	}
	
	public boolean isAutoFit(){
		return getAutoFitStatus() == AUTOFIT_YES;
	}

	public int getReflowStatus() {
		return reflowStatus;
	}

	public void setReflowStatus(int reflowStatus) {
		this.reflowStatus = reflowStatus;
	}

	public int getCategory() {
		return category;
	}

	public void setCategory(int category) {
		this.category = category;
	}
	
}