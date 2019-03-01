package com.dangdang.reader.dread.format.epub;

import java.io.FileNotFoundException;
import java.util.List;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;

import com.dangdang.reader.R;
import com.dangdang.reader.dread.config.ReadConfig;
import com.dangdang.reader.dread.core.epub.ReaderAppImpl;
import com.dangdang.reader.dread.data.ReadInfo;
import com.dangdang.reader.dread.format.BaseEBookManager;
import com.dangdang.reader.dread.format.Book;
import com.dangdang.reader.dread.format.Book.BaseNavPoint;
import com.dangdang.reader.dread.format.Chapter;
import com.dangdang.reader.dread.format.epub.EpubBook.EpubNavPoint;
import com.dangdang.reader.dread.function.FunctionCode;
import com.dangdang.reader.dread.jni.BaseJniWarp;
import com.dangdang.reader.dread.jni.BookStructHandler;
import com.dangdang.reader.dread.jni.ChaterInfoHandler;
import com.dangdang.zframework.log.LogM;

/**
 * @author luxu
 */
public class EpubBookManagerNew extends BaseEBookManager implements Runnable {

	protected boolean mIsLandScape;

    public EpubBookManagerNew(Context context, Book oneBook) {
        super(context, oneBook);
    }

    public void startRead(ReadInfo readInfo) {
        super.startRead(readInfo);
        doStart(this);
    }

    @Override
    public boolean isInitKey(ReadInfo readInfo) {
        return readInfo.getEBookType() == BaseJniWarp.BOOKTYPE_DD_DRM_EPUB;
    }

    @Override
    public void run() {
        Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
        initNative();
        startParser();
    }

    protected Book buildBookStruct(final String bookFile, int bookType,
                                   final boolean hasNotChapter) throws FileNotFoundException {

        final String epubPath = bookFile;
		/*
		 * EpubBook textBook = new EpubBook(); mParseEpub.buildBook(textBook,
		 * epubPath, mBookDir);
		 */
        BookStructHandler handler = new BookStructHandler();
        // mDrwrap.buildBookStruct(epubPath, handler);

        mDrwrap.openFile(epubPath, bookType, handler);
        //
        afterOpenFile();
        mOneBook.setChapterList(handler.getChapterList());
        mOneBook.setNavPointList(handler.getNavPointList());
        mOneBook.setVersion(handler.getVersion());
		mOneBook.setModVersion(handler.getModVersion());

        mReadInfo.setEpubVersion(mOneBook.getVersion());
		mReadInfo.setEpubModVersion(mOneBook.getModVersion());
		mReadInfo.setLandScape(handler.isLandScape());
		mIsLandScape = handler.isLandScape();
       // List<Chapter> chapterList = mOneBook.getChapterList();b

        return mOneBook;
    }

    protected void loadChapterList(List<Chapter> chapters, int chapterSize,
                                   boolean isReLoad, boolean isFirst) {
        LogM.d(getClass().getSimpleName(), "wyz loadChapterList==start , "
                + Thread.currentThread().getName());

        // loadChapterListBefore(chapterSize);

        final int chapterCount = chapterSize;
        int being = 0;
        // Html prev = null;

        for (; being < chapterCount; being++) {
            if (isPlanAbortComposing()) {
                LogM.d(getClass().getSimpleName(),
                        "lxu loadChapterList being = " + being
                                + ", reqHtmlIndex = " + mRequestChapterIndex);
                break;
            }
            if (isComposingError(mStatus)) {
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
                Chapter chapter = chapters.get(being);
                chapter.reSet();
                int pageCount = 0;

                printLog(" getChapterPageCount start " + chapter);
                // mComposingOneDone = false;
                pageCount = getChapterPageCount(chapter);
                // getALableByHtml(html);
                // mComposingOneDone = true;

                chapter.setStartPageNum(1);// html.startPageNum = 1;
                chapter.setEndPageNum(pageCount);// html.endPageNum = pageCount;

                mappingToBook(chapter);
                // saveChapterCache(html);

                callBeingParse(chapterCount, being, chapter, false);
                setComposingOneDone();

                if (pageCount <= 0) {
                    setBookCacheNotAvailable();
                    printLog(" getChapterPageCount end " + chapter
                            + ", pageCount = " + pageCount + ",being=" + being);
                }
                printLog(" getChapterPageCount end " + chapter
                        + ", pageCount = " + pageCount + ",being=" + being);
                // LogM.e(getClass().getSimpleName(),
                // "lux2 hasAsycLoadChapterData = " +
                // hasAsycLoadChapterData.get());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                unLockMsg();
            }
        }

        // loadChapterListAfter(chapters, isReLoad, chapterCount);
    }

    protected void mappingToBook(Chapter chapter) {
        mOneBook.addPageRange(chapter.getPath(), chapter);
        try {
            ChaterInfoHandler chapterInfo = getBookCache().getChapterInfo(
                    chapter);
            EpubNavPoint navPoint = (EpubNavPoint) mOneBook.getNavPoint(chapter);
            if (navPoint != null) {
                navPoint.setPageIndex(chapter.getEndPageNum());
                List<BaseNavPoint> subNavs = navPoint.getSubNavPs();
                if (subNavs != null && subNavs.size() > 0) {
                    for (int i = 0, len = subNavs.size(); i < len; i++) {
                        EpubNavPoint subPoint = (EpubNavPoint) subNavs.get(i);
                        if (subPoint.hasAnchor()) {
                            int pageIndex = chapterInfo
                                    .getPageIndexByAnchor(subPoint.getAnchor());
                            subPoint.setPageIndex(pageIndex);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	public boolean isLandScape() {
		return mIsLandScape;
	}

}
