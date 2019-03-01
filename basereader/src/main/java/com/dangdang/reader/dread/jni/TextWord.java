package com.dangdang.reader.dread.jni;

import android.graphics.RectF;

public class TextWord extends RectF {
	public String w;

	public TextWord() {
		super();
		w = "";
	}

	public void Add(TextChar tc) {
		super.union(tc);
		w = w.concat(new String(new char[]{tc.c}));
	}
}
