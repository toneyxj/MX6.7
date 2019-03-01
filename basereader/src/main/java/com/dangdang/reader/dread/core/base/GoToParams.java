package com.dangdang.reader.dread.core.base;

import com.dangdang.reader.dread.core.base.IEpubReaderController.GoToType;
import com.dangdang.reader.dread.format.Chapter;

import java.io.Serializable;

public class GoToParams implements Serializable {
	
	private GoToType type;
	private Chapter chapter;
	private int elementIndex;
	private String anchor;
    private boolean isBuy;// 是否购买此章
    private boolean isGotoLast;// 是否调转最后一页

    public boolean isGotoLast() {
        return isGotoLast;
    }

    public void setGotoLast(boolean isGotoLast) {
        this.isGotoLast = isGotoLast;
    }

    public boolean isBuy() {
        return isBuy;
    }

    public void setBuy(boolean isBuy) {
        this.isBuy = isBuy;
    }
	public Chapter getChapter() {
		return chapter;
	}
	
	public void setChapter(Chapter chapter) {
		this.chapter = chapter;
	}
	
	public int getElementIndex() {
		return elementIndex;
	}
	
	public void setElementIndex(int elementIndex) {
		this.elementIndex = elementIndex;
	}
	
	public String getAnchor() {
		return anchor;
	}
	
	public void setAnchor(String anchor) {
		this.anchor = anchor;
	}
	
	public GoToType getType() {
		return type;
	}
	
	public void setType(GoToType type) {
		this.type = type;
	}
	
	
	public static class SearchGotoParams extends GoToParams {
		
		private int wordStartElementIndex;
		private int wordEndElementIndex;
		
		public int getWordStartElementIndex() {
			return wordStartElementIndex;
		}
		public void setWordStartElementIndex(int wordStartElementIndex) {
			this.wordStartElementIndex = wordStartElementIndex;
		}
		public int getWordEndElementIndex() {
			return wordEndElementIndex;
		}
		public void setWordEndElementIndex(int wordEndElementIndex) {
			this.wordEndElementIndex = wordEndElementIndex;
		}
	}
	
}