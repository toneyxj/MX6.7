package com.dangdang.reader.dread.jni;

import java.util.ArrayList;
import java.util.List;

import com.dangdang.reader.dread.format.Book.BaseNavPoint;
import com.dangdang.reader.dread.format.Chapter;
import com.dangdang.reader.dread.format.epub.EpubBook.EpubNavPoint;
import com.dangdang.reader.dread.format.epub.EpubChapter;
import com.dangdang.zframework.log.LogM;
import com.dangdang.reader.dread.util.DreaderConstants;

public class BookStructHandler {

	private String mVersion;
	private String mModVersion;
	private List<Chapter> mChapterList;
	private List<BaseNavPoint> mNavPointList;
	private boolean mIsLandScape;
	
	public BookStructHandler() {
		super();
		mChapterList = new ArrayList<Chapter>();
		mNavPointList = new ArrayList<BaseNavPoint>();
		mModVersion = DreaderConstants.BOOK_MODIFY_VERSION;
		mIsLandScape = false;
	}

	public void setEpubVersion(String version){
		//printLog(" BookStruct setEpubVersion " + version);
		mVersion = version;
	}
	
	public void setEpubModVersion(String version){
		//printLog(" BookStruct setEpubModVersion " + version);
		if (!version.isEmpty())
			mModVersion = version;
	}

	public void setEpubChapter(String html){
		//printLog(" BookStruct setEpubChapter " + html);
		EpubChapter chapter = new EpubChapter(html);
		if(!mChapterList.contains(chapter)){
			chapter.setIndexInBook(mChapterList.size());
			mChapterList.add(chapter);
		}
	}
	
	private EpubNavPoint oneLevelNPoint;
	public void setEpubNavPoint(String fullSrc, String anchor, String lableText, int level){
		//printLog(" BookStruct setEpubNavPoint " + fullSrc + "," + anchor + "," + lableText + "," + level);
		
		EpubNavPoint eNPoint = new EpubNavPoint();
		eNPoint.setFullSrc(fullSrc);
		eNPoint.setLableText(lableText);
		eNPoint.setAnchor(anchor);
		try {
			eNPoint.setShortSrc(fullSrc.substring(fullSrc.lastIndexOf("/") + 1));
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(level == 0){
			mNavPointList.add(eNPoint);
			oneLevelNPoint = eNPoint;
		} else {
			oneLevelNPoint.addSubNavPoint(eNPoint);
		}
	}
	
	public String getVersion() {
		return mVersion;
	}

	public String getModVersion() {
		return mModVersion;
	}

	public List<Chapter> getChapterList() {
		return mChapterList;
	}
	
	public List<BaseNavPoint> getNavPointList(){
		return mNavPointList;
	}

	protected void printLog(String log){
		LogM.i(getClass().getSimpleName(), log);
	}

	public boolean isLandScape() {
		return mIsLandScape;
	}

	public void setLandScape(boolean isLandScape) {
		this.mIsLandScape = isLandScape;
	}
	
}
