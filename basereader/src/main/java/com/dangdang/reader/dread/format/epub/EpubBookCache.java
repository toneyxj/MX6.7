package com.dangdang.reader.dread.format.epub;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import com.dangdang.reader.dread.format.Chapter;
import com.dangdang.reader.dread.format.IBookCache;
import com.dangdang.reader.dread.format.IndexRange;
import com.dangdang.reader.dread.format.PageInfo;
import com.dangdang.reader.dread.jni.BaseJniWarp.ElementIndex;
import com.dangdang.reader.dread.jni.ChaterInfoHandler;
import com.dangdang.zframework.log.LogM;

public class EpubBookCache implements IBookCache ,Serializable{
	
	
	//private Map<Chapter, Integer> mPageCountMap = null;
	private int mPageCount;
	private Map<Chapter, ChaterInfoHandler> mPageInfoCache = null;
	private boolean mAvailable = true;
	
	public EpubBookCache(){
		//mPageCountMap = new Hashtable<Chapter, Integer>();
		mPageInfoCache = new Hashtable<Chapter, ChaterInfoHandler>();
	}
	
	public int getPageCount(Chapter chapter){
		return getChapterInfo(chapter).getPageInfos().size();
	}

	public int getChapterWeighedTextCount(Chapter chapter){
		return getChapterInfo(chapter).getWeightedTextCount();
	}

	public boolean hasPageCount(Chapter chapter){
		return hasCache(chapter);
	}
	
	/*public void setPageCount(Chapter chapter, int pageCount){
		mPageCountMap.put(chapter, pageCount);
	}*/
	
	public void setPageInfo(Chapter chapter, ChaterInfoHandler ch){
		mPageInfoCache.put(chapter, ch);
	}
	
	public PageInfo getPageInfo(Chapter chapter, int pageIndexInChapter){
		if(chapter == null){
			return null;
		}
		PageInfo pageInfo = null;
		try {
			ChaterInfoHandler ciHandle = getChapterInfo(chapter);
			if(ciHandle != null){
				pageInfo = ciHandle.getPageInfo(pageIndexInChapter);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pageInfo;
	}

	public ChaterInfoHandler getChapterInfo(Chapter chapter) {
		ChaterInfoHandler ciHandle = mPageInfoCache.get(chapter);
		return ciHandle;
	}
	
	public IndexRange getChapterStartAndEndIndex(Chapter chapter) {
		ChaterInfoHandler ciHandle = getChapterInfo(chapter);
		if(ciHandle == null){
			return null;
		}
		IndexRange indexRange = new IndexRange();
		indexRange.setStartIndex(new ElementIndex(ciHandle.getFirstElementIndex()));
		indexRange.setEndIndex(new ElementIndex(ciHandle.getLastElementIndex()));
		return indexRange;
	}
	
	public boolean hasAnchor(Chapter chapter){
		return hasCache(chapter);
	}

	public boolean hasCache(Chapter chapter) {
		return mPageInfoCache.containsKey(chapter);
	}
	
	public int getPageIndexByAnchor(Chapter chapter, String anchor){
		if(chapter == null){
			return 0;
		}
		int pageIndex = 0;
		try {
			ChaterInfoHandler ciHandle = getChapterInfo(chapter);
			if(ciHandle != null){
				pageIndex = ciHandle.getPageIndexByAnchor(anchor);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return pageIndex;
	}
	
	public int getPageIndexByElementIndex(Chapter chapter, int elementIndex){
		if(chapter == null){
			return 0;
		}
		int pageIndex = 0;
		try {
			ChaterInfoHandler ciHandle = getChapterInfo(chapter);
			if(ciHandle != null){
				pageIndex = ciHandle.getPageIndexByElementIndex(elementIndex);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pageIndex;
	}
	
	
	public Map<Chapter, ChaterInfoHandler> getPageInfoCache(){
		return mPageInfoCache;
	}
	
	public void setPageInfoCache(Map<Chapter, ChaterInfoHandler> pageInfoCache){
		mPageInfoCache = pageInfoCache;
	}
	
	public int getPageCount() {
		return mPageCount;
	}

	public void setPageCount(int pageCount) {
		this.mPageCount = pageCount;
	}

	public void reset(){
		//mPageCountMap.clear();
		try {
			Iterator<Chapter> iters = mPageInfoCache.keySet().iterator();
			while(iters.hasNext()){
				mPageInfoCache.get(iters.next()).reset();
			}
			mPageInfoCache.clear();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean isAvailable() {
		return mAvailable;
	}

	public void setAvailable(boolean available) {
		this.mAvailable = available;
	}
	
	protected void printLog(String log){
		LogM.i(getClass().getSimpleName(), log);
	}
	
	protected void printLogE(String log){
		LogM.e(getClass().getSimpleName(), log);
	}
	
}
