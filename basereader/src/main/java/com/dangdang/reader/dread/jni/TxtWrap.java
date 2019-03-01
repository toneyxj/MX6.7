package com.dangdang.reader.dread.jni;

import com.dangdang.reader.dread.jni.BaseJniWarp.EPageIndex;

import android.graphics.Bitmap;


public class TxtWrap extends BaseJniWarp {
	
	// 判断txt编码类型
	public final native int openFile(String txtPath);
	
	// 提取txt文件的目录信息
	public final native int getChapterList(String txtPath, ChapterListHandle chaptersHandle);
	
	/**
	 * 请求停止排版
	 * @return
	 */
	public final native int cancelParse();
	
	public final int getParagraphText(EPageIndex pageIndex, int elementIndex, boolean isFirst, int maxLen, ParagraphTextHandler handler){
		final boolean forward = true;
		return getParagraphText(pageIndex, elementIndex, isFirst, forward, maxLen, handler);
	}
	
}
