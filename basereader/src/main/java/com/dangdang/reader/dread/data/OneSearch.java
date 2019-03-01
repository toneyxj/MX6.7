package com.dangdang.reader.dread.data;

import com.dangdang.reader.dread.format.Chapter;
import com.dangdang.reader.dread.jni.BaseJniWarp.ElementIndex;

public class OneSearch {

	private Chapter chapter;
	private String content;
	private ElementIndex keywordStartIndex;//相对于章
	private ElementIndex keywordEndIndex;//相对于章
	private int keywordIndexInContent;
	
	public Chapter getChapter() {
		return chapter;
	}
	
	public void setChapter(Chapter chapter) {
		this.chapter = chapter;
	}
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public ElementIndex getKeywordStartIndex() {
		return keywordStartIndex;
	}
	
	public void setKeywordStartIndex(ElementIndex keywordStartIndex) {
		this.keywordStartIndex = keywordStartIndex;
	}
	
	public ElementIndex getKeywordEndIndex() {
		return keywordEndIndex;
	}
	
	public void setKeywordEndIndex(ElementIndex keywordEndIndex) {
		this.keywordEndIndex = keywordEndIndex;
	}
	
	public int getKeywordIndexInContent() {
		return keywordIndexInContent;
	}
	
	public void setKeywordIndexInContent(int keywordIndexInContent) {
		this.keywordIndexInContent = keywordIndexInContent;
	}
	
	public boolean isInClude(int startIndex, int endIndex) {
		return keywordStartIndex.getIndex() >= startIndex && keywordStartIndex.getIndex() <= endIndex;
	}
	
	@Override
	public boolean equals(Object o) {
		if(o == null){
			return false;
		}
		if(!(o instanceof OneSearch)){
			return false;
		}
		OneSearch other = (OneSearch)o;
		return other.getChapter().equals(getChapter()) 
				&& other.getKeywordStartIndex().equals(getKeywordStartIndex()) 
				&& other.getKeywordEndIndex().equals(getKeywordEndIndex());
	}
	
}
