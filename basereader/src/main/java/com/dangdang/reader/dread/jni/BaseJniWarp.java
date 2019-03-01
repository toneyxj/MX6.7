package com.dangdang.reader.dread.jni;

import android.graphics.Bitmap;

public class BaseJniWarp {

	public final static int BOOKTYPE_THIRD_EPUB = 1;
	public final static int BOOKTYPE_DD_DRM_EPUB = 2;
	public final static int BOOKTYPE_DD_TXT = 3;
	public final static int BOOKTYPE_DD_PDF = 4;
	public final static int BOOKTYPE_DD_DRM_HTML = 5;
	public final static int BOOKTYPE_DD_DRM_COMICS = 6;

	protected final static int TTS_PARAGTEXT_LEN = 300;

	public static class EPageIndex {

		public int bookType;
		public String filePath;
		public int pageIndexInChapter;// TODO 从0开始的，就是集合的角标
		public int subIndexInPage; // page中的子页，例如画廊
		
		public int startByte;
		public int endByte;		

		public EPageIndex() {
			super();
			bookType = 0;
			pageIndexInChapter = 0;
			subIndexInPage = 0;
			startByte = -1;
			endByte = -1;
		}

	}
	
	public static class ERect {

		public float left;
		public float top;
		public float right;
		public float bottom;

		public ERect(float left_, float top_, float right_, float bottom_) {
			left = left_;
			top = top_;
			right = right_;
			bottom = bottom_;
		}
	}

	public static class EPoint {
		public float x;
		public float y;

		@Override
		public boolean equals(Object o) {
			if (o instanceof EPoint) {
				EPoint other = (EPoint) o;
				return other.x == x && other.y == y;
			}

			return super.equals(o);
		}

	}

	public final static native void initParseEngine(WrapClass wrap);

	/**
	 * 退出应用时
	 */
	public static native void destoryData();

	/**
	 * 重新排版时，重新打开一本书时
	 */
	public native void resetData();

	/**
	 * 退出阅读时
	 */
	public native void clearData();

	public native boolean setBkForeColor(int bkColor, int foreColor);

	/**
	 * 设置默认字体
	 * 
	 * @param fontName
	 * @param fontPath
	 * @param charset
	 * @return
	 */
	public native boolean setCurDefaultFont(String fontName, String fontPath,
			String charset);

	/**
	 * 字体添加
	 * 
	 * @param fontName
	 * @param fontPath
	 * @param charset
	 * @return
	 */
	public native boolean addBasicFont(String fontName, String fontPath,
			String charset);
    /**
     * 字体添加
     *
     * @param fontName
     * @param fontPath
     * @param charset
     * @return
     */
    public native boolean addGlobalFont(String fontName, String fontPath,
                                        String charset);
	/**
	 * 内核版本
	 * 
	 * @return
	 */
	public static int getKernelVersion() {
		return 7;// 4.8.1更改
	}

	/**
	 * 影响排版版本
	 * 
	 * @return
	 */
	public static int getCompVersion() {
		return 7;// 4.8.1更改
	}

	/**
	 * 给定数据和参数排版并渲染一页Bitmap
	 * 
	 * @param str
	 * @param width
	 *            排版宽度
	 * @param height
	 *            排版高度 如果-1 不限制高度
	 * @param padding
	 * @param lineSpacing
	 * @param paragraphSpacing
	 * @return
	 */
	public final native int drawString(String str, boolean bAsHTML,
			StringRenderHandler handler, int width, int height, ERect padding,
			float lineSpacing, float paragraphSpacing);

	/**
	 * 繁简转换
	 * 
	 * @param isConvert
	 */
	public static native void setBig5Encoding(boolean isConvert);

	/**
	 * 字符串繁简转换
	 * 
	 * @param str
	 *            转换的字符串
	 * @param type
	 *            0简体转为繁体，1繁体转简体
	 * @return
	 */
	public static native String ConvertToGBorBig5(String str, int type);

	public native int UpdateElementIndex(String strChapter, String oldModVersion, String newModVersion, int nIndex);

	public static class ElementIndex {

		protected int elementIndex;

		public ElementIndex() {

		}

		public ElementIndex(int index) {
			super();
			this.elementIndex = index;
		}

		public int getIndex() {
			return elementIndex;
		}

		public void setIndex(int elementIndex) {
			this.elementIndex = elementIndex;
		}

		public static ElementIndex min(ElementIndex e1, ElementIndex e2) {
			return e1.getIndex() > e2.getIndex() ? e2 : e1;
		}

		public static ElementIndex max(ElementIndex e1, ElementIndex e2) {
			return e1.getIndex() > e2.getIndex() ? e1 : e2;
		}

		@Override
		public boolean equals(Object o) {
			if (o == null) {
				return false;
			}
			if (!(o instanceof ElementIndex)) {
				return false;
			}
			ElementIndex other = (ElementIndex) o;
			return other.getIndex() == getIndex();
		}

		@Override
		public String toString() {
			return "[" + elementIndex + "]";
		}

	}
	
	// epub与txt通用接口
	public final native int getPageCount(EPageIndex pageIndex, boolean quickScan);
	
	public final native int getPageByIndex(EPageIndex pageIndex, int index);// 根据字符索引获取字符所在的页号

	/**
	 * 是否有缓存
	 * @return
	 */
	public final native boolean isInBookCache(EPageIndex pageIndex);

	public final native boolean isInPageInfoCache(EPageIndex pageIndex);

	/**
	 * /** 获取每一页对应Bitmap，如果没有缓存，会重新排版
	 * 
	 * @param pageIndex
	 * @param bitmap
	 *            背景类型
	 * @return 页类型
	 */
	public final native int drawPage(EPageIndex pageIndex, Bitmap bitmap);

	/**
	 * 会导致排版 根据开始点和结束点获取Rect[] 标注和笔记时：阴影和线 如果pStart ==
	 * pEnd，那么返回在当前页一句话的ERect[]，参考 原CharacterSet.getParagraph(...)实现;
	 * 
	 * @param pageIndex
	 * @param pStart
	 * @param pEnd
	 * @return
	 */
	public final native ERect[] getSelectedRectsByPoint(EPageIndex pageIndex,
			EPoint pStart, EPoint pEnd);

	/**
	 * 会导致排版 根据开始索引和结束索引获取Rect[]
	 * 
	 * @param pageIndex
	 * @param startIndex
	 * @param endIndex
	 * @return
	 */
	public final native ERect[] getSelectedRectsByIndex(EPageIndex pageIndex,
			int startIndex, int endIndex);

	/**
	 * 会导致排版 两点之间对应的开始Element索引位置和结束Element索引位置
	 * 
	 * @param pageIndex
	 * @param pStart
	 * @param pEnd
	 * @return
	 */
	public final native int[] getSelectedStartAndEndIndex(EPageIndex pageIndex,
			EPoint pStart, EPoint pEnd);

	/**
	 * 根据一个点获取元素Index
	 * 
	 * @param pageIndex
	 * @param point
	 * @return 正常 > 0
	 */
	public final native int getElementIndexByPoint(EPageIndex pageIndex,
			EPoint point);

	/**
	 * 会导致排版 获取页开始索引位置和结束索引位置
	 */
	public final native int[] getPageStartAndEndIndex(EPageIndex pageIndex);

	/**
	 * 获取对应位置的字符串
	 */
	public final native String getText(EPageIndex pageIndex, int startIndex,
			int endIndex);

	/**
	 * 获取对应位置的字符串,字符串按照段落返回数组
	 */
	public final native String[] getTextWithPara(EPageIndex pageIndex, int startIndex,
									   int endIndex);

	/**
	 * 返回这一章的页数及每一页的startIndex和endIndex
	 */
	public final native int getChapterInfo(EPageIndex pageIndex, ChaterInfoHandler handler);

	/**
	 * 搜索
	 * @param keyword
	 * @param callback
	 * @return
	 */
	public final native int search(EPageIndex pageIndex, String keyword,  SearchHandler callback);

	/**
	 * 支持tts接口
	 * 
	 * @param elementIndex
	 * @param isFirst
	 * @return
	 */
	public final native int getParagraphText(EPageIndex pageIndex, int elementIndex,
			boolean isFirst, boolean forward, int maxLen,
			ParagraphTextHandler handler);

	public final native int layoutAndGetPageByIndex(EPageIndex pageIndex, int index);

	public final native ERect[] getWordRectsByPoint(EPageIndex pageIndex,EPoint point);
}
