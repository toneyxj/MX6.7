package com.dangdang.reader.dread.jni;

import java.util.ArrayList;
import java.util.List;

import com.dangdang.reader.dread.core.epub.ReaderAppImpl;
import com.dangdang.reader.dread.data.OneSearch;
import com.dangdang.reader.dread.format.IBook;
import com.dangdang.reader.dread.format.epub.EpubChapter;
import com.dangdang.reader.dread.format.part.PartBook;
import com.dangdang.reader.dread.format.part.PartChapter;
import com.dangdang.reader.dread.format.txt.TxtChapter;
import com.dangdang.reader.dread.jni.BaseJniWarp.ElementIndex;
import com.dangdang.zframework.log.LogM;

public class SearchHandler {
	
	
	private List<OneSearch> mSearchs;
	private int mWordLen;
	
	public SearchHandler(String word){
		mSearchs = new ArrayList<OneSearch>();
		mWordLen = word.length();
	}
	

	/**
	 * epub搜索回调
	 * @param content
	 * @param contentStartIndex 搜索关键词相对于章开始位置
	 * @param contentEndIndex 搜索关键词相对于章结束位置
	 * @param keywordStartIndex 搜索关键词相对于章开始位置
	 */
	public void callBackSeachByEpub(String html, String content, 
			int contentStartIndex, int contentEndIndex, int keywordStartIndex){
		
		//printLog(" callBackSeachByEpub 1 " + html);
		//printLog(" callBackSeachByEpub 2 " + content + ", " + keywordStartIndex + "-" + keywordEndIndex + ", " + keywordIndexInContent);
		
		OneSearch oSearch = new OneSearch();
        IBook book = ReaderAppImpl.getApp().getBook();
        if (book instanceof PartBook){
            oSearch.setChapter(new PartChapter(html));
        }else{
            oSearch.setChapter(new EpubChapter(html));
        }
		oSearch.setContent(content);
		oSearch.setKeywordStartIndex(new ElementIndex(keywordStartIndex));
		oSearch.setKeywordEndIndex(new ElementIndex(keywordStartIndex + mWordLen - 1));
		oSearch.setKeywordIndexInContent(keywordStartIndex - contentStartIndex);
		
		mSearchs.add(oSearch);
	}
	
	public void callBackSeachByPdf(int pdfPageNum, String content, 
			int contentStartIndex, int contentEndIndex, int keywordStartIndex){
		
		
	}
	
	/**
	 * txt搜索回调
	 * @param path
	 * @param startByte
	 * @param endByte
	 * @param contentStartIndex
	 * @param contentEndIndex
	 * @param keywordStartIndex
	 */
	public void callBackSeachByTxt(String path, int startByte, int endByte, String content, 
			int contentStartIndex, int contentEndIndex, int keywordStartIndex){
		
		//printLog(" callBackSeachByTxt 1 " + path + ", " + startByte + "-" + endByte);
		//printLog(" callBackSeachByTxt 2 " + content + ", " + keywordStartIndex + "-" + keywordEndIndex + ", " + keywordIndexInContent);
		OneSearch oSearch = new OneSearch();
		TxtChapter chapter = new TxtChapter();
		chapter.setPath(path);
		chapter.setStartByte(startByte);
		chapter.setEndByte(endByte);
		
		oSearch.setChapter(chapter);
		oSearch.setContent(content);
		oSearch.setKeywordStartIndex(new ElementIndex(keywordStartIndex));
		oSearch.setKeywordEndIndex(new ElementIndex(keywordStartIndex + mWordLen - 1));
		oSearch.setKeywordIndexInContent(keywordStartIndex - contentStartIndex);
		
		mSearchs.add(oSearch);
	}
	
	public List<OneSearch> getSearchs() {
		return mSearchs;
	}
	
	protected void printLog(String log){
		LogM.i(getClass().getSimpleName(), log);
	}
	
}
