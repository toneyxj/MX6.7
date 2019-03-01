package com.dangdang.reader.dread.core.epub;

import java.util.List;

import android.graphics.Rect;

import com.dangdang.reader.dread.config.NoteRect;
import com.dangdang.reader.dread.config.TmpRect;
import com.dangdang.reader.dread.core.base.BasePageAdapter.IDrawPageListener;
import com.dangdang.reader.dread.core.base.IEpubReaderController.IGotoPageListener;
import com.dangdang.reader.dread.core.base.IReaderController.DPageIndex;
import com.dangdang.reader.dread.data.BookNote;
import com.dangdang.reader.dread.format.BaseBookManager.DrawPageResult;
import com.dangdang.reader.dread.format.BaseBookManager.GotoPageCommand;
import com.dangdang.reader.dread.format.Chapter;
import com.dangdang.reader.dread.holder.PageBitmap;
import com.dangdang.reader.dread.jni.BaseJniWarp.ElementIndex;

public abstract class BaseControllerWrapper {

	
	public abstract PageWrap getPageWrap(DPageIndex pageIndex, Chapter currentChapter, int currentPageIndexInChapter);
	
	
	public abstract DrawPageResult drawPage(PageWrap pageWrap, int pageWidth, int pageHeight);
	
	public abstract int getChaterIndex(final Chapter chapter);
	
	public abstract int getChapterPageCount(final Chapter chapter);
	
	/**
	 * 从当前章节中获取
	 * @param anchor
	 * @return
	 */
	//public abstract int getPageIndexByAnchor(final Chapter chapter, String anchor);
	
	/**
	 *  从当前章节中获取
	 * @param chapter
	 * @param elementIndexInChapter
	 * @return
	 */
	public abstract int getPageIndexInChapter(final Chapter chapter, int elementIndexInChapter);
	
	
	public abstract Chapter getPrevOrNextChapter(DPageIndex pageIndex, Chapter currentChapter);
	
	/**
	 * 根据元素Index范围数组获取Rect[][]二维数组
	 * @param chapter
	 * @param pageIndexInChapter
	 * @param notes
	 * @return
	 */
	public abstract NoteRect[] getSelectedRects(Chapter chapter, int pageIndexInChapter, List<BookNote> notes);
	
	public abstract TmpRect[] getSearchRects(final Chapter chapter, int currentPageIndexInChapter, ElementIndex startIndex, ElementIndex endIndex);
	
	public abstract Rect[] getSelectedRectsByIndex(Chapter chapter, int pageIndexInChapter, ElementIndex startIndex, ElementIndex endIndex);
	
	public abstract Rect getPageGalleryRect(Chapter currentChapter, int currentPageIndexInChapter);
	
	
	/**
	 * 从缓存中取PageBitmap
	 * @param pageWrap
	 * @return 可能为null
	 */
	public abstract PageBitmap getCacheBitmap(PageWrap pageWrap);
	
	public abstract int checkCache(Chapter currentChapter, int currentPageIndexInChapter);
	
	public abstract int checkCurrentPageCache(Chapter currentChapter, int currentPageIndexInChapter, int currPageSeqNum);
	
	//public abstract void asynDrawPage(DrawPageAsycCommand command);
	
	public abstract void asynDrawPage(PageWrap pageWrap, int bgType, int pageWidth, int pageHeight, final IDrawPageListener l);
	
	public abstract void asynGotoPage(GotoPageCommand command, final IGotoPageListener l);
	
	
	public abstract void reset();
	
	public abstract boolean isBookComposingDone();
	
	public static class PageWrap {
		
		public Chapter chapter  = null;
		/**
		 * 章节title，每章第一节为书名
		 */
		public String headerName;
		
		public int pageIndexInChapter = -1;
		/**
		 * 页序号，(比如画廊 一页中 实际有几种不同内容) 其它可忽略此字段
		 */
		private int pageSequenceNum = 0;
		public int chapterPageCount;
		
		//public boolean isChangeChapter = false;
		
		public boolean prevChapter = false;
		public boolean nextChapter = false;
		
		/**
		 * 是否取上一章最后一页
		 */
		public boolean isLastPage = false;
		public boolean isCache = true;
		
		public Chapter getChapter() {
			return chapter;
		}

		public void setChapter(Chapter chapter) {
			this.chapter = chapter;
		}

		public String getHeaderName() {
			return headerName;
		}

		public void setHeaderName(String headerName) {
			this.headerName = headerName;
		}

		public int getPageIndexInChapter() {
			return pageIndexInChapter;
		}

		public void setPageIndexInChapter(int pageIndexInChapter) {
			this.pageIndexInChapter = pageIndexInChapter;
		}

		public int getChapterPageCount() {
			return chapterPageCount;
		}

		public void setChapterPageCount(int chapterPageCount) {
			this.chapterPageCount = chapterPageCount;
		}

		public boolean isPrevChapter() {
			return prevChapter;
		}

		public void setPrevChapter(boolean prevChapter) {
			this.prevChapter = prevChapter;
		}

		public boolean isNextChapter() {
			return nextChapter;
		}

		public void setNextChapter(boolean nextChapter) {
			this.nextChapter = nextChapter;
		}

		public boolean isLastPage() {
			return isLastPage;
		}

		public void setLastPage(boolean isLastPage) {
			this.isLastPage = isLastPage;
		}

		public boolean isCache() {
			return isCache;
		}

		public void setCache(boolean isCache) {
			this.isCache = isCache;
		}

		public int getPageSequenceNum() {
			return pageSequenceNum;
		}

		public void setPageSequenceNum(int pageSequenceNum) {
			this.pageSequenceNum = pageSequenceNum;
		}

		@Override
		public String toString() {
			return chapter + "-[" + pageIndexInChapter + "]" + isLastPage;
		}
	}

}
