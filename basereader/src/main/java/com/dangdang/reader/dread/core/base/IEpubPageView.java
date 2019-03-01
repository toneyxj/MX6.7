package com.dangdang.reader.dread.core.base;

import android.graphics.Rect;

import com.dangdang.reader.dread.core.base.IReaderWidget.DrawPoint;

public interface IEpubPageView {

	/**
	 * @param start
	 * @param end
	 * @param rects
	 * @return
	 */
	public int doDrawing(DrawingType type, DrawPoint start, DrawPoint end, DrawPoint current, Rect[] rects, int drawLineColor);

	public int drawFinish(DrawingType type, DrawPoint current);
	
	/**
	 * @param isVisiby true 显示， false 隐藏
	 */
	public void operationMarkView(boolean isVisiby);
	
	
	public static enum DrawingType {
		
		/**
		 * 画线
		 */
		Line, 
		/**
		 * 笔记高亮
		 */
		Shadow, 
		/**
		 * 搜索高亮
		 */
		ShadowSearch, 
		/**
		 * TTS高亮
		 */
		ShadowTTS;
		
		public boolean isLine(){
			return this == Line;
		}
		
		public boolean isShadow(){
			return this == Shadow || this == ShadowSearch || this == ShadowTTS;
		}
		
		public boolean isTmpShadow(){
			return this == ShadowSearch || this == ShadowTTS;
		}
		
	}
	
}
