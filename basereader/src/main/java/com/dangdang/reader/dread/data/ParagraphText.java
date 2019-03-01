package com.dangdang.reader.dread.data;

import android.text.TextUtils;

import com.dangdang.reader.dread.jni.BaseJniWarp.ElementIndex;

public class ParagraphText {

	private int code;

	private String text = "";
	private ElementIndex startEmtIndex;
	private ElementIndex endEmtIndex;
	private boolean isTip = false;

	public String getText() {
		if(text == null){
			text = "";
		}
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public ElementIndex getStartEmtIndex() {
		return startEmtIndex;
	}

	public void setStartEmtIndex(ElementIndex startEmtIndex) {
		this.startEmtIndex = startEmtIndex;
	}

	public ElementIndex getEndEmtIndex() {
		return endEmtIndex;
	}

	public void setEndEmtIndex(ElementIndex endEmtIndex) {
		this.endEmtIndex = endEmtIndex;
	}
	
	public int getTextLen(){
		return getEndIndexToInt() - getStartIndexToInt();
	}

	public int getStartIndexToInt() {
		if(startEmtIndex == null){
			return 0;
		}
		return startEmtIndex.getIndex();
	}

	public int getEndIndexToInt() {
		if(endEmtIndex == null){
			return 0;
		}
		return endEmtIndex.getIndex();
	}
	
	public boolean isIllegality(){
		return TextUtils.isEmpty(text) || (getStartIndexToInt() == 0 && getEndIndexToInt() == 0);
	}
	
	public boolean isTip() {
		return isTip;
	}

	public void setTip(boolean isTip) {
		this.isTip = isTip;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer("[");
		sb.append(text);
		sb.append("][");
		sb.append(getStartIndexToInt() + "-" + getEndIndexToInt());
		sb.append("]");
		return sb.toString();
	}

}
