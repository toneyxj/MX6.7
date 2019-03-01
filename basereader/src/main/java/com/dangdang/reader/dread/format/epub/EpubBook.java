package com.dangdang.reader.dread.format.epub;

import java.util.ArrayList;
import java.util.List;

import android.text.TextUtils;

import com.dangdang.reader.dread.format.Book;
import com.dangdang.reader.dread.format.Chapter;

public class EpubBook extends Book {
	
	private String mOpsPath;
	
	
	public String getOpsPath() {
		return mOpsPath;
	}

	public void setOpsPath(String opsPath) {
		this.mOpsPath = opsPath;
	}
	
	
	public EpubNavPoint getNavPoint(Chapter chapter){
		
		final List<BaseNavPoint> nPList = getNavPointList();
		if(nPList == null || chapter == null){
			return null;
		}
		EpubNavPoint nP = null;
		for(BaseNavPoint n : nPList){
			if(chapter.getPath().equals(n.fullSrc) || n.fullSrc.contains(chapter.getPath())){
				nP = (EpubNavPoint) n;
				break;
			} else {
				final List<BaseNavPoint> subs = n.subNavPs;
				if(subs != null){
					for(BaseNavPoint subN : subs){
						if(chapter.getPath().equals(subN.fullSrc) || subN.fullSrc.contains(chapter.getPath())){
							nP = (EpubNavPoint) subN;
							break;
						}
					}
				}
			}
		}
		
		return nP;
	}
	
	
	public static class EpubNavPoint extends BaseNavPoint {

		//public String lableText;
		//public String fullSrc;
		/**
		 * 不包括锚点(anchor)
		 */
		public String shortSrc;
		public String anchor;
		
		/**
		 * 目录下：子章节
		 *//*
		public List<NavPoint> subNavPs;
		
		*//**
		 * 如果是子章节，那么此为它的父章节
		 *//*
		public NavPoint parentNav;*/
		
		public boolean isSub = false;
		
		/**
		 * 试读的paytip章节顺序，从1开始
		 */
		public int paytipIndex = 1;
		
		public EpubNavPoint() {
			super();
		}

		public EpubNavPoint(String lableText, String fullSrc, String shortSrc) {
			super();
			this.lableText = lableText;
			this.fullSrc = fullSrc;
			this.shortSrc = shortSrc;
		}

		public List<BaseNavPoint> getSubNavPoint() {
			return subNavPs;
		}


		public void setSubNavPoint(List<BaseNavPoint> subNavPs) {
			this.subNavPs = subNavPs;
		}
		
		public void addSubNavPoint(BaseNavPoint navPoint){
			
			if(subNavPs == null){
				subNavPs = new ArrayList<BaseNavPoint>();
			}
			subNavPs.add(navPoint);
			
		}

		public String getAnchor() {
			return anchor;
		}

		public void setAnchor(String anchor) {
			this.anchor = anchor;
		}
		
		public String getShortSrc() {
			return shortSrc;
		}

		public void setShortSrc(String shortSrc) {
			this.shortSrc = shortSrc;
		}

		public boolean whetherHasSubs(){
			return subNavPs != null && subNavPs.size() != 0;
		}
		
		public boolean isPayTip(){
			boolean payTip = false;
			if(shortSrc != null){
				payTip = shortSrc.toLowerCase().contains(Book.PAYTIP);
			}
			return payTip;
		}
		
		public boolean hasAnchor(){
			return !TextUtils.isEmpty(getAnchor());
		}
		
	}

	@Override
	public List<BaseNavPoint> getAllNavPointList() {
		if (mNavPointList == null)
			return null;

		List<BaseNavPoint> mData = new ArrayList<BaseNavPoint>();
		int paytipIndex = 1;
		EpubNavPoint epubNp = null;
		for (BaseNavPoint np : mNavPointList) {

			epubNp = (EpubNavPoint) np;
			if (epubNp.isPayTip()) {
				epubNp.paytipIndex = paytipIndex;
				paytipIndex++;
			}
			mData.add(epubNp);
			if (np.subNavPs != null) {
				EpubNavPoint subEpubNp = null;
				for (BaseNavPoint subNp : epubNp.subNavPs) {
					subEpubNp = (EpubNavPoint) subNp;
					subEpubNp.isSub = true;
					if (subEpubNp.isPayTip()) {
						subEpubNp.paytipIndex = paytipIndex;
						paytipIndex++;
					}
					mData.add(subEpubNp);
				}
			}
		}
		return mData;
	}
}