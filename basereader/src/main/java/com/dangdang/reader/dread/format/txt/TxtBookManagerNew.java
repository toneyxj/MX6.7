package com.dangdang.reader.dread.format.txt;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;

import com.dangdang.reader.dread.config.ParserStatus;
import com.dangdang.reader.dread.data.OneSearch;
import com.dangdang.reader.dread.data.ReadInfo;
import com.dangdang.reader.dread.format.BaseBookManager;
import com.dangdang.reader.dread.format.Book;
import com.dangdang.reader.dread.format.Book.BaseNavPoint;
import com.dangdang.reader.dread.format.Chapter;
import com.dangdang.reader.dread.format.IndexRange;
import com.dangdang.reader.dread.format.epub.ClickResult;
import com.dangdang.reader.dread.jni.BaseJniWarp;
import com.dangdang.reader.dread.jni.BaseJniWarp.EPageIndex;
import com.dangdang.reader.dread.jni.BaseJniWarp.EPoint;
import com.dangdang.reader.dread.jni.BaseJniWarp.ERect;
import com.dangdang.reader.dread.jni.BaseJniWarp.ElementIndex;
import com.dangdang.reader.dread.jni.ChapterListHandle;
import com.dangdang.reader.dread.jni.ChaterInfoHandler;
import com.dangdang.reader.dread.jni.DrawInteractiveBlockHandler;
import com.dangdang.reader.dread.jni.ParagraphTextHandler;
import com.dangdang.reader.dread.jni.SearchHandler;
import com.dangdang.reader.dread.jni.TxtWrap;
import com.dangdang.zframework.log.LogM;
import com.mx.mxbase.constant.APPLog;

import java.util.List;

public class TxtBookManagerNew extends BaseBookManager implements Runnable {

	private TxtBook mOneBook;
	private TxtWrap mTxtWrap;

	public TxtBookManagerNew(Context context, Book book) {
		super(context, book);

		mOneBook = (TxtBook) book;
		mTxtWrap = new TxtWrap();
		setBaseJni(mTxtWrap);
		// initComposingStyle();
	}

	@Override
	public void startRead(ReadInfo readInfo) {

		mReadInfo = readInfo;
		mBookFile = readInfo.getBookFile();

		doStart(this);
	}

	/*
	 * @Override public void reStartRead(ReadInfo readInfo) {
	 * 
	 * LogM.w(getClass().getSimpleName(), " [ reStartRead() ]");
	 * 
	 * mReadInfo = readInfo; reSet();
	 * 
	 * final boolean isFirst = preStartLoad();
	 * 
	 * doStart(new Runnable() {
	 * 
	 * @Override public void run() {
	 * startLoadChapters(mOneBook.getChapterList(), true, isFirst); } }); }
	 */

	@Override
	public void run() {
		Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
		initNative();
		startParser();
	}

	protected Book buildBookStruct(String bookFile, int bookType,
			boolean hasNotChapter) {

		printLog("  buildBookStruct  hasNotChapter = " + hasNotChapter);
		final String txtPath = bookFile;// /storage/emulated/0/ddReader/txt.txt
		mTxtWrap.openFile(txtPath);
		final ChapterListHandle chaptersHandle = new ChapterListHandle();

		// mEncodingType = mTxtWrap.getEncodingType(txtPath);
		if (hasNotChapter) {
			int status = mTxtWrap.getChapterList(txtPath, chaptersHandle);

			setComposingStatus(status);
			printLog(" buildBookStruct status = " + status);
			mOneBook.setChapterList(chaptersHandle.getChapterList());
			mOneBook.setNavPointList(chaptersHandle.getNavPointList());
		} else {
			final List<Chapter> chapterList = mReadInfo.getChapterList();
			List<BaseNavPoint> navsList = mReadInfo.getNavPointList();
			mOneBook.setChapterList(chapterList);

			if (navsList == null) {
				navsList = chaptersHandle
						.chapterListToNavPointList(chapterList);
			}
			mOneBook.setNavPointList(navsList);
		}
		mOneBook.setModVersion(chaptersHandle.getModVersion());
		mReadInfo.setEpubModVersion(mOneBook.getModVersion());

		return mOneBook;
	}

	protected void loadChapterList(List<Chapter> chapters, int chapterSize,
			boolean isReLoad, boolean isFirst) {
		LogM.i(getClass().getSimpleName(),
				"wyz loadChapterList==start , chapterSize = " + chapterSize);

		// loadChapterListBefore(chapterSize);

		int chapterCount = chapterSize;
		int being = 0;

		for (; being < chapterCount; being++) {
			if (isPlanAbortComposing()) {
				LogM.d(getClass().getSimpleName(),
						"lxu loadChapterList being = " + being
								+ ", reqHtmlIndex = " + mRequestChapterIndex);
				break;
			}
			if (!ParserStatus.isComposingSuccess(mStatus)) {
				break;
			}
			/*
			 * LogM.d(getClass().getSimpleName(),
			 * "lxu loadChapterList start one html = " + html +
			 * " start, [ mPlanAbortComposing = " + mPlanAbortComposing + " ]" +
			 * ", being = " + being + ", reqHtmlIndex = " + mRequestHtmlIndex +
			 * ", hasComposingWait  = " + hasComposingWait.get() +
			 * ", hasAsycLoadChapter = " + hasAsycLoadChapterData.get());
			 */
			try {
				lockMsg();
				notifyLoadData();
				processComposingWait();

				resetComposingOneDone();
				TxtChapter chapter = (TxtChapter) chapters.get(being);
				chapter.reSet();
				int pageCount = 0;

				// printLog(" getChapterPageCount being = " + being + ", start "
				// + chapter);
				pageCount = getChapterPageCount(chapter);

				chapter.setStartPageNum(1);// html.startPageNum = 1;
				chapter.setEndPageNum(pageCount);// html.endPageNum = pageCount;

				mOneBook.addPageRange(chapter.getTagPath(), chapter);

				// saveChapterCache(chapter);

				callBeingParse(chapterCount, being, chapter, false);
				setComposingOneDone();

				// printLog(" getChapterPageCount being = " + being + ", end " +
				// chapter + ", pageCount = " + pageCount);
				// LogM.e(getClass().getSimpleName(),
				// "lux2 hasAsycLoadChapterData = " +
				// hasAsycLoadChapterData.get());
			} catch (Exception e) {
				// e.printStackTrace();
			} finally {
				unLockMsg();
			}
		}

		// loadChapterListAfter(chapters, isReLoad, chapterCount);
	}

	@Override
	public synchronized int getChapterStructInner(final Chapter chapter) {

		// printLog("synchnzed native getChapterPageCountInner start chapter = "
		// + chapter);
		int pCount = getNativePageCount(chapter);
		saveChapterCache(chapter);
		// printLog("synchnzed native getChapterPageCountInner end chapter = " +
		// chapter + ", pCount = " + pCount);

		return pCount;
	}

	@Override
	protected int getNativePageCount(Chapter chapter) {
		EPageIndex pageIndex = getPageIndex(chapter, 0);
		int pCount = mTxtWrap.getPageCount(pageIndex, false);
		return pCount;
	}

	@Override
	protected ChaterInfoHandler getChapterInfo(Chapter chapter) {

		ChaterInfoHandler ciHandle = new ChaterInfoHandler();
		EPageIndex pageIndex = getPageIndex(chapter, 0);
		mTxtWrap.getChapterInfo(pageIndex, ciHandle);
		return ciHandle;
	}

	@Override
	public int getPageIndexInChapter(Chapter chapter, int elementIndex) {

		EPageIndex pageIndex = getPageIndex(chapter, 0);
		int pageByIndex = mTxtWrap.getPageByIndex(pageIndex, elementIndex);
		pageByIndex = pageByIndex + 1;
		if (pageByIndex < 1) {
			LogM.e(getClass().getSimpleName(),
					" getPageIndexInChapter pageByIndex = " + pageByIndex);
		}
		return pageByIndex < 1 ? 1 : pageByIndex;
	}

	@Override
	public int getPageIndexInHtmlByAnchor(Chapter chapter, String anchor) {
		return 0;
	}

	@Override
	public int getElementIndexByAnchor(Chapter chapter, String anchor) {
		return 0;
	}

	@Override
	public int drawPage(Chapter chapter, int pageIndexInChapter,
			int pageSeqNum, Bitmap bitmap, boolean sync) {

		EPageIndex pageIndex = getPageIndex(chapter, pageIndexInChapter);
		int status = 0;
		if (sync) {
			status = drawPageInner(chapter, pageIndexInChapter, bitmap,
					pageIndex);
		} else {
			APPLog.e("获得图片","TxtBookManagerNew1");
			status = mTxtWrap.drawPage(pageIndex, bitmap);
		}
		return status;
	}

	protected synchronized int drawPageInner(Chapter chapter,
			int pageIndexInChapter, Bitmap bitmap, EPageIndex pageIndex) {
		printLog("luxu999 drawPage start chapter = " + chapter + ", "
				+ pageIndexInChapter);
		APPLog.e("获得图片", "TxtBookManagerNew2");
		int retStatus = mTxtWrap.drawPage(pageIndex, bitmap);
		printLog("luxu999 drawPage end chapter = " + chapter + ", "
				+ pageIndexInChapter);
		return retStatus;
	}

	@Override
	public boolean isCacheChapter(Chapter chapter) {

		EPageIndex pageIndex = getPageIndex(chapter, 0);

		return mTxtWrap.isInBookCache(pageIndex);
	}

	public boolean isInPageInfoCache(Chapter chapter) {

		EPageIndex pageIndex = getPageIndex(chapter, 0);
		boolean is = mTxtWrap.isInPageInfoCache(pageIndex);
		printLog(" isInPageInfoCache chapter = " + chapter + ", is = " + is);
		return is;
	};

	@Override
	public Rect[] getSelectedRectsByPoint(Chapter chapter,
			int pageIndexInChapter, Point start, Point end) {

		EPageIndex pageIndex = getPageIndex(chapter, pageIndexInChapter);

		EPoint pStart = convertPaint(start);
		EPoint pEnd = convertPaint(end);
		ERect[] rs = null;
		rs = mTxtWrap.getSelectedRectsByPoint(pageIndex, pStart, pEnd);
		return convertRect(rs);
	}

	private EPageIndex getPageIndex(Chapter chapter, int pageIndexInChapter) {

		TxtChapter txtChapter = (TxtChapter) chapter;
		EPageIndex pageIndex = new EPageIndex();
		pageIndex.bookType = BaseJniWarp.BOOKTYPE_DD_TXT;
		pageIndex.filePath = mBookFile;
		pageIndex.startByte = txtChapter.getStartByte();
		pageIndex.endByte = txtChapter.getEndByte();
		pageIndex.pageIndexInChapter = pageIndexInChapter - 1; // 上层逻辑是从1开始的，所以减1

		return pageIndex;
	}

	@Override
	public Rect[] getSelectedRectsByIndex(Chapter chapter,
			int pageIndexInChapter, ElementIndex startIndex,
			ElementIndex endIndex) {

		EPageIndex pageIndex = getPageIndex(chapter, pageIndexInChapter);
		ERect[] rs = mTxtWrap.getSelectedRectsByIndex(pageIndex,
				startIndex.getIndex(), endIndex.getIndex());

		return convertRect(rs);
	}

	@Override
	public ElementIndex[] getSelectedStartAndEndIndex(Chapter chapter,
			int pageIndexInChapter, Point start, Point end) {

		EPageIndex pageIndex = getPageIndex(chapter, pageIndexInChapter);
		EPoint pStart = convertPaint(start);
		EPoint pEnd = convertPaint(end);
		int[] startEnds = mTxtWrap.getSelectedStartAndEndIndex(pageIndex,
				pStart, pEnd);
		ElementIndex[] eis = { new ElementIndex(startEnds[0]),
				new ElementIndex(startEnds[1]) };

		return eis;
	}

	@Override
	public ElementIndex getElementIndexByPoint(Chapter chapter,
			int pageIndexInChapter, Point point) {
		EPageIndex pageIndex = getPageIndex(chapter, pageIndexInChapter);
		EPoint pPoint = convertPaint(point);
		int elementIdex = mTxtWrap.getElementIndexByPoint(pageIndex, pPoint);
		return new ElementIndex(elementIdex);
	}

	@Override
	public synchronized IndexRange getPageStartAndEndIndexInner(
			Chapter chapter, int pageIndexInChapter) {

		EPageIndex pageIndex = getPageIndex(chapter, pageIndexInChapter);
		// printLog("luxu999 getPageStartAndEndIndex start " +
		// pageIndexInChapter);
		int[] startEnds = mTxtWrap.getPageStartAndEndIndex(pageIndex);
		// printLog("luxu999 getPageStartAndEndIndex end " +
		// pageIndexInChapter);
		return new IndexRange(new ElementIndex(startEnds[0]), new ElementIndex(
				startEnds[1]));
	}

	@Override
	public String getText(Chapter chapter, ElementIndex startIndex,
			ElementIndex endIndex) {

		EPageIndex pageIndex = getPageIndex(chapter, -1);// TODO
															// getText不关心哪一页
		String text = mTxtWrap.getText(pageIndex, startIndex.getIndex(),
				endIndex.getIndex());

		return convertToSimplified(text);
	}

	@Override
	public ClickResult clickEvent(Chapter chapter, int pageIndexInChapter,
			Point point) {

		return new ClickResult();
	}

	@Override
	protected void cancelParse() {
		mTxtWrap.cancelParse();
	}

	@Override
	protected List<OneSearch> search(Chapter chapter, String word) {
		EPageIndex pageIndex = getPageIndex(chapter, -1);// TODO
		SearchHandler searchCallback = new SearchHandler(word);
		mTxtWrap.search(pageIndex, word, searchCallback);
		return searchCallback.getSearchs();
	}

	protected void getParagraphTextInner(Chapter chapter, int elementIndex,
			boolean first, int maxLen, ParagraphTextHandler handler) {

		EPageIndex pageIndex = getPageIndex(chapter, -1);// TODO
		mTxtWrap.getParagraphText(pageIndex, elementIndex,
				first, maxLen, handler);

	}

	@Override
	public void DrawInteractiveBlock(Chapter chapter, int pageIndexInChapter,
			int nBlockIndex, int nWidth, int nHeight,
			DrawInteractiveBlockHandler handler) {
		// TODO Auto-generated method stub
		
	}

}
