package com.dangdang.reader.dread.format.txt;

import android.text.TextUtils;

import com.dangdang.reader.dread.format.Chapter;

public class TxtChapter extends Chapter {

	private static final long serialVersionUID = 1L;
	//protected String path
	protected String chapterName;
	protected int startByte;
	protected int endByte;
	
	public TxtChapter(){
		
	}
	
	public int getStartByte() {
		return startByte;
	}
	public void setStartByte(int startByte) {
		this.startByte = startByte;
	}
	public int getEndByte() {
		return endByte;
	}
	public void setEndByte(int endByte) {
		this.endByte = endByte;
	}
	
	public String getChapterName() {
		return chapterName;
	}
	public void setChapterName(String chapterName) {
		this.chapterName = chapterName;
	}
	
	public String getTagPath() {
		final StringBuffer pathSb = new StringBuffer(path);
		pathSb.append(":");
		pathSb.append(startByte);
		pathSb.append("-");
		pathSb.append(endByte);
		
		return pathSb.toString();
	}
	
	@Override
	public boolean equals(Object o) {
		if(o == null || !(o instanceof TxtChapter)){
			return false;
		}
		if(this.path == null || this.path.trim().length() == 0){
			return false;
		}
		TxtChapter other = (TxtChapter) o;
		return this.startByte == other.startByte && this.endByte == other.endByte;
	}
	
	@Override
	public int hashCode() {
		if(TextUtils.isEmpty(path)){
			return super.hashCode();
		}
		StringBuffer sb = new StringBuffer();
		sb.append(path);
		sb.append("[");
		sb.append(startByte);
		sb.append("-");
		sb.append(endByte);
		sb.append("]");
		
		return sb.toString().hashCode();
	}
	
	@Override
	public String toString() {
		
		StringBuffer sb = new StringBuffer("TxtChapter[ path = ");
		sb.append(path);
		sb.append(", ( ");
		sb.append(startByte);
		sb.append(" - ");
		sb.append(endByte);
		sb.append(" ) ]");
		
		return sb.toString();
	}
}
