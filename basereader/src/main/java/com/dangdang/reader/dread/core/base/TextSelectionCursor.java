
package com.dangdang.reader.dread.core.base;

import com.dangdang.zframework.utils.DRUiUtility;

public enum TextSelectionCursor {
	
	None,
	Left,
	Right;

	private static int ourHeight;
	private static int ourWidth;
	private static int ourAccent;

	private static void init() {
		if (ourHeight == 0) {
			final int dpi = DRUiUtility.getDisplayDPI();
			ourAccent = dpi / 12;
			ourWidth = dpi / 8;
			ourHeight = dpi / 6;
		}
	}

	public static int getHeight() {
		init();
		return ourHeight;
	}

	public static int getWidth() {
		init();
		return ourWidth;
	}

	public static int getAccent() {
		init();
		return ourAccent;
	}
}
