package com.dangdang.reader.dread.format.epub;

import android.text.TextUtils;

import com.dangdang.reader.dread.format.Chapter;

/**
 * one EpubChapter == one Html
 * @author luxu
 *
 */
public class EpubChapter extends Chapter {

	
	/*public int startPageNum;//in html
	public int endPageNum;//in html 
	
	public int startIndexInBook;
	public int endIndexInBook;
	
	public String path;*/
	
	private static final long serialVersionUID = 1L;

	public EpubChapter(){
		
	}
	
	public EpubChapter(String path){
		this.path = path;
	}
	
	/*public boolean isContainPageIndex(int pageIndexInHtml){
		
		if(startPageNum == 0 || endPageNum == 0){
			return false;
		}
		final int pageTotal = getPageTotal();
		return pageIndexInHtml <= pageTotal;
		
	}*/
	
	@Override
	public String toString() {
		
		return "Html[" + path + "-(" + startPageNum + "-" + endPageNum +")(" + startIndexInBook + "-" + endIndexInBook +")]";
	}
	
	@Override
	public boolean equals(Object o) {
		if(o == null || !(o instanceof EpubChapter)){
			return false;
		}
		if(this.path == null || this.path.trim().length() == 0){
			return false;
		}
		EpubChapter other = (EpubChapter) o;
		return this.path.equals(other.path);
	}
	
	@Override
	public int hashCode() {
		if(TextUtils.isEmpty(path)){
			return super.hashCode();
		}
		return path.hashCode();
	}
	
}
