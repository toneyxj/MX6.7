package com.dangdang.reader.dread.format.epub;

import java.util.List;

import android.graphics.Point;
import android.graphics.Rect;

import com.dangdang.reader.dread.data.OneSearch;
import com.dangdang.reader.dread.data.ParagraphText;
import com.dangdang.reader.dread.format.Chapter;
import com.dangdang.reader.dread.format.IBookManager;
import com.dangdang.reader.dread.format.IndexRange;
import com.dangdang.reader.dread.jni.BaseJniWarp.ElementIndex;

public interface IEpubBookManager extends IBookManager {

	public int getPageIndexInChapter(final Chapter chapter, final int elementIndex);
	
	public int getElementIndexByAnchor(final Chapter chapter, final String anchor);//TODO ? 
	
	public int getPageIndexInBook(final Chapter chapter, final int elementIndex); //TODO ? 
	
	public int getPageIndexInHtmlByAnchor(final Chapter chapter, final String anchor);//TODO ? 
	
	public int getChapterPageCount(final Chapter chapter, boolean onlyCache);
	
	public void preComposingChapter(final Chapter chapter);

	public int composingChapterAndGetPageIndex(final Chapter chapter, final int elementIndex);


	/**
	 * 是否排版完成
	 * @param chapter
	 * @return
	 */
	public boolean isCacheChapter(final Chapter chapter);
	
	/**
	 * 是否有页数据结构(页数和index对应范围)
	 * @param chapter
	 * @return
	 */
	public boolean isInPageInfoCache(final Chapter chapter);
	
	
	/**
	 * 根据开始点和结束点获取Rect[]
	 * 标注和笔记时：阴影和线
	 * 如果pStart == pEnd，那么返回这个在当前页一句话的ERect[]，参考 原CharacterSet.getParagraph(...)实现; 
	 * @param pageIndexInChapter
	 * @param pStart
	 * @param pEnd
	 * @return
	 */
	public Rect[] getSelectedRectsByPoint(Chapter chapter, int pageIndexInChapter, Point pStart, Point pEnd);
	
	
	/**
	 * 根据开始索引和结束索引获取Rect[]
	 * @param pageIndexInChapter
	 * @param startIndex
	 * @param endIndex
	 * @return
	 */
	public Rect[] getSelectedRectsByIndex(Chapter chapter, int pageIndexInChapter, ElementIndex startIndex, ElementIndex endIndex);

	/**
	 * 两点之间对应的开始Element索引位置和结束Element索引位置
	 * @param pageIndexInChapter
	 * @param pStart
	 * @param pEnd
	 * @return
	 */
	public ElementIndex[] getSelectedStartAndEndIndex(Chapter chapter, int pageIndexInChapter, Point pStart, Point pEnd);
	
	/**
	 * 通过一个点获取对应elementIndex
	 * @param chapter
	 * @param pageIndexInChapter
	 * @param point
	 * @return
	 */
	public ElementIndex getElementIndexByPoint(Chapter chapter, int pageIndexInChapter, Point point);

	/**
	 * 获取页开始索引位置和结束索引位置
	 */
	public IndexRange getPageStartAndEndIndex(Chapter chapter, int pageIndexInChapter);
	
	/**
	 * 只从缓存取
	 * @param chapter
	 * @return 可能为null
	 */
	public IndexRange getChapterStartAndEndIndex(Chapter chapter);

	/**
	 * 获取对应位置的字符串
	 */
	public String getText(Chapter chapter, ElementIndex startIndex, ElementIndex endIndex);

	/**
	 * 如：点击图片
	 * return:  点击类型和图片路径等(锚点、注释相关数据)
	 */
	public ClickResult clickEvent(Chapter chapter, int pageIndexInChapter, Point point);
	
	public void updateBackground(int bgColor, int foreColor);
	
	public void search(String word, SearchListener l);
	
	//public void requestAbortComposing(AbortComposingListener l);
	
	public ParagraphText getParagraphText(Chapter chapter, int elementIndex, boolean first, int maxLen);
	
	public void abortSearch();
	
	public interface SearchListener {
		
		public void onStart();
		
		public void onSearch(List<OneSearch> searchs);
		
		public void onEnd();
		
	}

	public boolean isAlreadyDestroy();
}
