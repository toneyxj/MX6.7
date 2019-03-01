package com.dangdang.reader.dread.jni;

import java.io.Serializable;

public class ALabelInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/*private int startIndex;
	private int endIndex;*/
	private int pageIndex;
	private String anchor;
	
	/*public int getStartIndex() {
		return startIndex;
	}
	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}
	public int getEndIndex() {
		return endIndex;
	}
	public void setEndIndex(int endIndex) {
		this.endIndex = endIndex;
	}*/
	
	public int getPageIndex() {
		return pageIndex;
	}
	
	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}
	
	public String getAnchor() {
		return anchor;
	}
	
	public void setAnchor(String anchor) {
		this.anchor = anchor;
	}
	
	public boolean isSame(String tAnchor){
		return anchor != null && anchor.equals(tAnchor);
	}

}
