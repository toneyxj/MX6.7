package com.dangdang.reader.dread.config;

import android.graphics.Rect;

public class TmpRect {

	public final static int TYPE_CURRENT = 1;
	public final static int TYPE_NOCURR = -1;
	
	private int type = TYPE_CURRENT;
	private Rect[] rects;

	public Rect[] getRects() {
		return rects;
	}

	public void setRects(Rect[] rects) {
		this.rects = rects;
	}
	
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public boolean isCurrent(){
		return type == TYPE_CURRENT;
	}
	
}
