package com.dangdang.reader.dread.jni;

import com.dangdang.reader.dread.format.Book.BaseNavPoint;

public class OutlineItem extends BaseNavPoint {
	
	// level 从0开始算起
	public final int    level;
	public final String title;
	public final int    page;//pdf page
	// 上层outlineItem在列表中的下标，顶层的值=-1
	public final int    parentIdx;
	
	private String path;
	
	public OutlineItem(int _level, String _title, int _page, int _parentIdx) {
		level = _level;
		title = _title;
		page  = _page;
		parentIdx = _parentIdx;
	}

	public int getLevel() {
		return level;
	}

	public String getTitle() {
		return title;
	}

	public int getPdfPage() {
		return page;
	}

	public int getParentIdx() {
		return parentIdx;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
}
