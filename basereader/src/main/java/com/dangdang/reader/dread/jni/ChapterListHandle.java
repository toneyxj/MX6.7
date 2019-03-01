package com.dangdang.reader.dread.jni;

import java.util.ArrayList;
import java.util.List;

import android.text.TextUtils;

import com.dangdang.reader.dread.format.Book.BaseNavPoint;
import com.dangdang.reader.dread.format.Chapter;
import com.dangdang.reader.dread.format.txt.TxtBook.TxtNavPoint;
import com.dangdang.reader.dread.format.txt.TxtChapter;
import com.dangdang.reader.dread.util.DreaderConstants;

public class ChapterListHandle {

	private List<Chapter> chapterList = new ArrayList<Chapter>(1);
	private List<BaseNavPoint> navPointList = new ArrayList<BaseNavPoint>(1);
	private TxtNavPoint prevNavPoint;
	private int prevNavposition = 0;
	private String 	mModVersion = DreaderConstants.BOOK_MODIFY_VERSION;

	public void setTxtChapter(String path, String chapterName, int startByte, int endByte) {

		TxtChapter chapter = new TxtChapter();
		chapter.setPath(path);
		chapter.setChapterName(chapterName);
		chapter.setStartByte(startByte);
		chapter.setEndByte(endByte);
		chapterList.add(chapter);
		
		if(!TextUtils.isEmpty(chapterName)){
			chapterToNavPoint(chapter);
		}
	}

	protected void chapterToNavPoint(final TxtChapter chapter) {
		
		TxtNavPoint navPoint = new TxtNavPoint();
		navPoint.setPath(chapter.getPath());
		navPoint.setName(chapter.getChapterName());
		navPoint.setStartByte(chapter.getStartByte());
		navPoint.setEndByte(chapter.getEndByte());
		
		final int navPointSize = navPointList.size();
		if(prevNavPoint != null && navPoint.getName().equals(prevNavPoint.getName())){
			
			final int navPointPosition = navPointSize;
			navPoint.setSplitChapterNum(navPointPosition - prevNavposition);
			
		} else {
			prevNavPoint = navPoint;
			prevNavposition = navPointSize;
		}
		navPointList.add(navPoint);
	}
	
	public List<BaseNavPoint> chapterListToNavPointList(List<Chapter> chapterList){
		
		if(chapterList != null){
			//TxtNavPoint navPoint = null;
			TxtChapter txtChapter = null;
			for(Chapter c : chapterList){
				
				txtChapter = (TxtChapter) c;
				if(!TextUtils.isEmpty(txtChapter.getChapterName())){
					/*navPoint = new TxtNavPoint();
					navPoint.setPath(txtChapter.getPath());
					navPoint.setName(txtChapter.getChapterName());
					navPoint.setStartByte(txtChapter.getStartByte());
					navPoint.setEndByte(txtChapter.getEndByte());
					
					navPointList.add(navPoint);*/
					chapterToNavPoint(txtChapter);
				}
			}
		}
		return navPointList;
	}
	

	public List<Chapter> getChapterList() {
		return chapterList;
	}

	public List<BaseNavPoint> getNavPointList() {
		return navPointList;
	}

	public String getModVersion() {
		return mModVersion;
	}

	public void setModVersion(String mModVersion) {
		this.mModVersion = mModVersion;
	}
	
}
