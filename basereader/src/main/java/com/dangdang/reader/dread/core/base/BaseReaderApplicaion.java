package com.dangdang.reader.dread.core.base;

import android.app.Activity;

import com.dangdang.reader.cloud.MarkNoteManager;
import com.dangdang.reader.dread.data.BookNote;
import com.dangdang.reader.dread.format.BaseReadInfo;
import com.dangdang.reader.dread.format.Chapter;
import com.dangdang.reader.dread.format.DDFile.FileType;
import com.dangdang.reader.dread.format.IBook;
import com.dangdang.reader.dread.format.IBookCache;
import com.dangdang.reader.dread.format.IBookManager;
import com.dangdang.reader.dread.holder.ServiceManager;

public abstract class BaseReaderApplicaion extends BaseGlobalApplication {

	
	private IReaderEventListener mReaderEventListener;
	
	public abstract void startRead(BaseReadInfo readInfo);
	
	public abstract void reStartRead(BaseReadInfo readInfo);

	public abstract IReaderWidget getReaderWidget(); 
	
	public abstract IReaderController getReaderController();
	
	public abstract IBookManager getBookManager();
	
	public abstract FileType getFileType();
	
	/**
	 * 相对这本书的
	 * @return
	 */
	public abstract int getCurrentPageIndex();
	
	public abstract int getPageSize();

	public abstract Activity getContext();
	
	public abstract ServiceManager getServiceManager();

	public abstract MarkNoteManager getMarkNoteManager();
	
	
	public IReaderEventListener getReaderEventListener() {
		return mReaderEventListener;
	}

	public void setReaderEventListener(IReaderEventListener l) {
		this.mReaderEventListener = l;
	}
	
	public void registerComposingListener(IBookParserListener l) {
		getBookManager().registerComposingListener(l);
	}
	
	public void requestAbort(IAbortParserListener l){
		getBookManager().requestAbortComposing(l);
	}
	
	public boolean isEpub(){
		return getFileType() == FileType.EPUB;
	}
	
	public boolean isTxt(){
		return getFileType() == FileType.TXT;
	}
	
	public boolean isPdf(){
		return getFileType() == FileType.PDF;
	}
	public boolean isPart(){
		return getFileType() == FileType.PART;
	}


	public static interface IReadProgress {
		
	}
	
	public static class EpubProgress implements IReadProgress {
		
		private Chapter chapter;
		private int chapterIndex;
		private int elementIndex;
		
		public EpubProgress(){
		}
		
		public EpubProgress(Chapter chapter, int chapterIndex, int elementIndex) {
			super();
			this.chapter = chapter;
			this.chapterIndex = chapterIndex;
			this.elementIndex = elementIndex;
		}

		public Chapter getChapter() {
			return chapter;
		}

		public void setChapter(Chapter chapter) {
			this.chapter = chapter;
		}

		public int getChapterIndex() {
			return chapterIndex;
		}

		public void setChapterIndex(int chapterIndex) {
			this.chapterIndex = chapterIndex;
		}

		public int getElementIndex() {
			return elementIndex;
		}

		public void setElementIndex(int elementIndex) {
			this.elementIndex = elementIndex;
		}
		
	}
	
	public static class PdfProgress implements IReadProgress {
		
		private int pageIndex;

		public PdfProgress(){
		}
		
		public PdfProgress(int pageIndex) {
			super();
			this.pageIndex = pageIndex;
		}

		public int getPageIndex() {
			return pageIndex;
		}

		public void setPageIndex(int pageIndex) {
			this.pageIndex = pageIndex;
		}
		
	}
	
	/**
	 * @author luxu
	 *
	 */
	public static interface IReaderEventListener {
		
		public void onMenuEvent();
		
		public void onLongPressEvent(int x, int y);
		
		public BookNote addNote(int chapterIndex, int startIndex, int endIndex, String selectedText, String note, int drawLineColor);
		
	}
	
	public static interface IBookParserListener {
		
		/**
		 * @param kernelVersion 内核版本
		 * @param kernelCompsVersion 影响排版结果的版本
		 */ 
		public void onVersion(int kernelVersion, int kernelCompsVersion);
		
		public void onStructFinish(IBook book);
		
		public void onStart(IBook book);

		public void onBeingComposing(ParserProgress progress);

		public void onFinish(int pageTotal, IBook book, IBookCache bookCache);
		
		public void onStatus(int status, String errorMsg);
		

	}
	
	public static class ParserProgress {

		//public String beingParseHtml;
		public int chapterIndex;
		public int beingchapterCount;
		public int chapterCount;
		
	}
	
}
