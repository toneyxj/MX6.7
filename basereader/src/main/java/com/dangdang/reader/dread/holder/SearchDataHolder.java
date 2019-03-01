package com.dangdang.reader.dread.holder;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.dangdang.reader.dread.data.OneSearch;
import com.dangdang.reader.dread.format.Chapter;

public class SearchDataHolder {

	private static SearchDataHolder mInstance = null;
	
	private OneSearch mCurrent;
	private Map<Chapter, ChapterMapping> mChapterMaps = new Hashtable<Chapter, ChapterMapping>();
	private List<OneSearch> mSearchs = new Vector<OneSearch>();
	
	private SearchDataHolder(){
	}
	
	public static synchronized SearchDataHolder getHolder(){
		if(mInstance == null){
			mInstance = new SearchDataHolder();
		}
		return mInstance;
	}
	
	public void addSearchs(List<OneSearch> searchData) {
		try {
			addMapping(searchData);
			this.mSearchs.addAll(searchData);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void addMapping(List<OneSearch> searchData) {
		int currSize = searchData.size();
		int preTotalSize = mSearchs.size();
		Chapter chapter = searchData.get(0).getChapter();
		ChapterMapping value = new ChapterMapping(preTotalSize, preTotalSize + currSize);
		mChapterMaps.put(chapter, value);
		printLog(" addMapping " + value);
	}

	public List<OneSearch> getSearchs() {
		return mSearchs;
	}
	
	public void reset(){
		mSearchs.clear();
		mChapterMaps.clear();
	}
	
	public void clear(){
		reset();
	}
	
	public void setCurrent(OneSearch search){
		mCurrent = search;
	}
	
	public OneSearch getCurrent(){
		return mCurrent;
	}
	
	public void resetCurrent(){
		mCurrent = null;
	}
	
	public List<OneSearch> getSearchesByRange(Chapter chapter, int startIndex, int endIndex){
		
		ChapterMapping mapping = mChapterMaps.get(chapter);
		printLog(" getSearchesByRange " + mapping);
		if(mapping == null){
			return null;
		}
		List<OneSearch> tmpSearches = new ArrayList<OneSearch>();
		int startPos = mapping.getStartPos();
		int endPos = mapping.getEndPos();
		
		for(int i = startPos; i < endPos; i++){
			try {
				OneSearch oneSearch = mSearchs.get(i);
				if(oneSearch.isInClude(startIndex, endIndex)){
					tmpSearches.add(oneSearch);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return tmpSearches;
	}
	
	protected void printLog(String log){
		
	}
	
	public static class ChapterMapping {
		
		private int startPos;
		private int endPos;
		
		public ChapterMapping(int startPos, int endPos) {
			super();
			this.startPos = startPos;
			this.endPos = endPos;
		}

		public int getStartPos() {
			return startPos;
		}

		public int getEndPos() {
			return endPos;
		}
		
		@Override
		public String toString() {
			return "[" + startPos + "-" + endPos + "]";
		}
		
	}
	
	
}
