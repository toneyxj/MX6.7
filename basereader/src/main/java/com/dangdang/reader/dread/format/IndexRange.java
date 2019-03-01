package com.dangdang.reader.dread.format;

import com.dangdang.reader.dread.jni.BaseJniWarp.ElementIndex;

public class IndexRange {

	private ElementIndex startIndex;
	private ElementIndex endIndex;
	
	public IndexRange() {
		super();
		startIndex = new ElementIndex();
		endIndex = new ElementIndex();
	}
	
	public IndexRange(ElementIndex startIndex, ElementIndex endIndex) {
		super();
		this.startIndex = startIndex;
		this.endIndex = endIndex;
	}

	public ElementIndex getStartIndex() {
		return startIndex;
	}
	
	public int getStartIndexToInt(){
		if(startIndex == null){
			return 0;
		}
		return startIndex.getIndex();
	}
	
	public void setStartIndex(ElementIndex startIndex) {
		this.startIndex = startIndex;
	}
	
	public ElementIndex getEndIndex() {
		return endIndex;
	}
	
	public int getEndIndexToInt(){
		if(endIndex == null){
			return 0;
		}
		return endIndex.getIndex();
	}
	
	public void setEndIndex(ElementIndex endIndex) {
		this.endIndex = endIndex;
	}
	
	public boolean hasContain(int elementIndex){
		if(hasNull(startIndex, endIndex)){
			return false;
		}
		return startIndex.getIndex() <= elementIndex && elementIndex <= endIndex.getIndex();
	}

	protected boolean hasNull(ElementIndex si, ElementIndex ei) {
		return si == null || ei == null;
	}
	
	/**
	 * 是否是无效的
	 * @return true 无效
	 */
	public boolean hasInValid(){
		if(hasNull(startIndex, endIndex)){
			return false;
		}
		return startIndex.getIndex() <= 0 && endIndex.getIndex() <= 0;
	}
	
	@Override
	public String toString() {
		return "[" + startIndex + " - " + endIndex + "]";
	}
	
}
