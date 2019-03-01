package com.dangdang.reader.dread.config;

import android.graphics.Rect;

import com.dangdang.reader.dread.core.epub.NoteHolder.NoteFlag;

public class NoteRect {

	private Rect[] rects;
	/**
	 * 是否有笔记
	 */
	private boolean hasNote = false;
	
	private int chapterIndex;
	private int pageIndexInChapter;
	private NoteFlag flag;
	private int drawLineColor;
	
	public NoteRect(){
		
	}
	
	public Rect[] getRects() {
		return rects;
	}
	
	public void setRects(Rect[] rects) {
		this.rects = rects;
	}
	
	public boolean isHasNote() {
		return hasNote;
	}
	
	public void setHasNote(boolean hasNote) {
		this.hasNote = hasNote;
	}
	
	public int getChapterIndex() {
		return chapterIndex;
	}

	public void setChapterIndex(int chapterIndex) {
		this.chapterIndex = chapterIndex;
	}

	public int getPageIndexInChapter() {
		return pageIndexInChapter;
	}
	
	public void setPageIndexInChapter(int pageIndexInChapter) {
		this.pageIndexInChapter = pageIndexInChapter;
	}
	
	public NoteFlag getFlag() {
		return flag;
	}
	
	public void setFlag(NoteFlag flag) {
		this.flag = flag;
	}

	public int getDrawLineColor() {
		return drawLineColor;
	}

	public void setDrawLineColor(int drawLineColor) {
		this.drawLineColor = drawLineColor;
	}
}
