package com.dangdang.reader.dread.format;

import java.io.Serializable;

public class PageInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int startIndex;
	private int endIndex;
	
	public int getStartIndex() {
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
	}
	
	public boolean hasContain(int index){
		return index >= startIndex && index <= endIndex;
	}

	@Override
	public String toString() {
		return "[" + startIndex + "-" + endIndex + "]";
	}
	
}
