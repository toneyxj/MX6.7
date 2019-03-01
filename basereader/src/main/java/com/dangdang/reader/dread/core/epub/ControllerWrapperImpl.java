package com.dangdang.reader.dread.core.epub;

import android.graphics.Rect;
import android.text.TextUtils;

import com.dangdang.reader.dread.cache.PageBitmapCache;
import com.dangdang.reader.dread.config.NoteRect;
import com.dangdang.reader.dread.config.TmpRect;
import com.dangdang.reader.dread.core.base.BasePageAdapter.IDrawPageListener;
import com.dangdang.reader.dread.core.base.BaseReaderApplicaion;
import com.dangdang.reader.dread.core.base.IEpubReaderController.IGotoPageListener;
import com.dangdang.reader.dread.core.base.IReaderController.DPageIndex;
import com.dangdang.reader.dread.core.epub.NoteHolder.NoteFlag;
import com.dangdang.reader.dread.data.BookNote;
import com.dangdang.reader.dread.data.OneSearch;
import com.dangdang.reader.dread.data.ReadInfo;
import com.dangdang.reader.dread.format.BaseBookManager;
import com.dangdang.reader.dread.format.BaseBookManager.DrawPageAsycCommand;
import com.dangdang.reader.dread.format.BaseBookManager.DrawPageResult;
import com.dangdang.reader.dread.format.BaseBookManager.DrawPageSyncCommand;
import com.dangdang.reader.dread.format.BaseBookManager.GotoPageCommand;
import com.dangdang.reader.dread.format.BaseBookManager.GotoPageResult;
import com.dangdang.reader.dread.format.BaseBookManager.IAsynListener;
import com.dangdang.reader.dread.format.BaseBookManager.PageSize;
import com.dangdang.reader.dread.format.Book;
import com.dangdang.reader.dread.format.Book.BaseNavPoint;
import com.dangdang.reader.dread.format.Chapter;
import com.dangdang.reader.dread.format.IBookManager.DrawPageStyle;
import com.dangdang.reader.dread.format.txt.TxtChapter;
import com.dangdang.reader.dread.holder.PageBitmap;
import com.dangdang.reader.dread.holder.SearchDataHolder;
import com.dangdang.reader.dread.jni.BaseJniWarp.ElementIndex;
import com.dangdang.reader.dread.task.ITaskCallback.BaseTaskKey;
import com.dangdang.reader.dread.task.ITaskCallback.BaseTaskResult;
import com.dangdang.zframework.log.LogM;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ControllerWrapperImpl extends BaseControllerWrapper {


    protected BaseReaderApplicaion mReadApp;
    protected BaseBookManager mBookManager;

    //	protected Map<PageIndexKey, PageBitmap> mCaches = new Hashtable<PageIndexKey, PageBitmap>();
    protected Map<PageIndexKey, IDrawPageListener> mPageListener = new Hashtable<PageIndexKey, IDrawPageListener>();
    protected Map<BaseTaskKey, IGotoPageListener> mGotoPageListener = new Hashtable<BaseTaskKey, IGotoPageListener>();

    public ControllerWrapperImpl(BaseReaderApplicaion readApp) {
        super();
        mReadApp = readApp;
        mBookManager = (BaseBookManager) readApp.getBookManager();
    }

    @Override
    public PageWrap getPageWrap(DPageIndex pageIndex, Chapter currentChapter, int mCurrentPageIndexInChapter) {

        printLog(" getPageWrap start " + pageIndex + "," + mCurrentPageIndexInChapter);
        PageWrap param = new PageWrap();

        boolean prevChapter = false;
        boolean nextChapter = false;
        boolean isLastPage = false;
        boolean isCache = true;
        int currentPageIndexInChapter = mCurrentPageIndexInChapter;
        int chapterPageCount = getChapterPageCount(currentChapter);
        Chapter chapter = currentChapter;
        if (pageIndex == DPageIndex.Previous) {
            if (currentPageIndexInChapter <= 1) {
                chapter = getPrevOrNextChapter(pageIndex, currentChapter);
                if (isInPageInfoCache(chapter)) {
                    chapterPageCount = getChapterPageCount(chapter);
                } else {
                    chapterPageCount = 1;//TODO 这里不对，需要改？
                    isLastPage = true;
                    isCache = false;
                }
                prevChapter = true;
            }
        } else if (pageIndex == DPageIndex.Next) {
            if (currentPageIndexInChapter >= chapterPageCount) {// == or >= ?
                chapter = getPrevOrNextChapter(pageIndex, currentChapter);
                if (isInPageInfoCache(chapter)) {
                    chapterPageCount = getChapterPageCount(chapter);
                } else {
                    chapterPageCount = 1;
                    isCache = false;
                }
                nextChapter = true;
            }
        }
        final int tmpCurrPageIndexInChapter = getTmpPageIndexInChapter(pageIndex, chapterPageCount,
                currentPageIndexInChapter, prevChapter, nextChapter);

        param.chapter = chapter;

        param.chapterPageCount = chapterPageCount;
        param.headerName = getHeaderName(chapter, tmpCurrPageIndexInChapter, isLastPage);
        param.pageIndexInChapter = tmpCurrPageIndexInChapter;
        param.prevChapter = prevChapter;
        param.nextChapter = nextChapter;
        param.isCache = isCache;
        param.isLastPage = isLastPage;

        printLog(" getPageWrap end " + pageIndex + ",count=" + chapterPageCount + "," + mCurrentPageIndexInChapter);

        return param;
    }

    public int getChapterPageCount(final Chapter chapter) {
        //printLog(" synchronized getChapterPageCount start " + chapter.hashCode() + ", " + chapter);
        int pageCount = mBookManager.getChapterPageCount(chapter, true);
        //printLog(" synchronized getChapterPageCount end " + chapter.hashCode() + ", " + pageCount);
        return pageCount;
    }


    @Override
    public int getChaterIndex(Chapter chapter) {
        return getBook().chapterIndexInBook(chapter);
    }

    protected int getTmpPageIndexInChapter(DPageIndex pageIndex, int chapterPageCount, int currentPageIndexInChapter,
                                           boolean prevChapter, boolean nextChapter) {
        printLog("lux2  getTmpCurrentPageIndexInChapter PageIndex = " + pageIndex
                + ", mNextChapter = " + nextChapter + ", mPrevChapter = " + prevChapter);
        //final int chapterPageCount = getCurrentChapterPageCount();
        final int firstPageInChapter = 1;
        final int lastPageInChapter = chapterPageCount;
        int tmpIndex = currentPageIndexInChapter;//mCurrentPageIndexInChapter;
        if (pageIndex == DPageIndex.Next) {
            tmpIndex = nextChapter ? firstPageInChapter : tmpIndex + 1;
        } else if (pageIndex == DPageIndex.Previous) {
            tmpIndex = prevChapter ? lastPageInChapter : tmpIndex - 1;
            tmpIndex = tmpIndex < 1 ? 1 : tmpIndex;
        }
        return tmpIndex;
    }

    /**
     * 同步操作，内核已经排版好直接渲染
     *
     * @param pageWrap
     * @param pageWidth
     * @param pageHeight
     * @return
     */
    public DrawPageResult drawPage(PageWrap pageWrap, int pageWidth, int pageHeight) {

        Chapter chapter = pageWrap.getChapter();
        int pageIndexInChapter = pageWrap.getPageIndexInChapter();
        int sequence = pageWrap.getPageSequenceNum();
        int bgType = 0;

        DrawPageStyle drawStyle = new DrawPageStyle();
        drawStyle.setBgType(bgType);

        DrawPageSyncCommand params = new DrawPageSyncCommand();
        params.setChapter(chapter);
        params.setPageIndexInChapter(pageIndexInChapter);
        params.setPageSequenceNum(sequence);
        params.setDrawStyle(drawStyle);
        params.setPageSize(new PageSize(pageWidth, pageHeight));
        params.setSync(true);

        DrawPageResult dpResult = (DrawPageResult) mBookManager.drawPageSync(params);

        PageIndexKey key = PageIndexKey.getKey(chapter, pageIndexInChapter, sequence);
        //TODO 缓存修改
        PageBitmap pageBitmap = PageBitmapCache.getInstance().getPageBitmap(key);
        if (pageBitmap == null)
            pageBitmap = new PageBitmap();
        pageBitmap.setBitmap(dpResult.getBitmap());
        pageBitmap.setPageRange(dpResult.getPageRange());
        pageBitmap.setPageType(dpResult.getPageType());
        pageBitmap.setGallarys(dpResult.getGallarys());
        pageBitmap.setVideoRect(dpResult.getVideoRect());
		pageBitmap.setListInteractiveBlocks(dpResult.getListInteractiveBlocks());

        setPageBitmapCache(key, pageBitmap);
        return dpResult;
    }

    public PageBitmap getCacheBitmap(PageWrap pageWrap) {
        synchronized (PageBitmapCache.getInstance()) {
            Chapter chapter = pageWrap.getChapter();
            int pageIndexInChapter = pageWrap.getPageIndexInChapter();
            int seqNum = pageWrap.getPageSequenceNum();
            PageIndexKey key = PageIndexKey.getKey(chapter, pageIndexInChapter, seqNum);
            PageBitmap pageBitmap = PageBitmapCache.getInstance().getPageBitmap(key);
            if (pageBitmap != null && !pageBitmap.isUseable()) {
                LogM.d(getClass().getSimpleName(), " pageBitmap not useable " + key);
                pageBitmap = null;
                PageBitmapCache.getInstance().remove(key);
            }
            return pageBitmap;
        }
    }

    @Override
    public Rect getPageGalleryRect(Chapter currentChapter, int currentPageIndexInChapter) {
        if (currentChapter == null || currentPageIndexInChapter < 0) {
            printLogE(" getPageGalleryRect illegality paramater... ");
            return null;
        }

        Rect rect = null;
        Map<PageIndexKey, PageBitmap> mCaches = PageBitmapCache.getInstance().snapshot();
        Iterator<PageIndexKey> iters = mCaches.keySet().iterator();
        while (iters.hasNext()) {
            PageIndexKey key = iters.next();
            if (currentChapter.equals(key.getChapter())
                    && currentPageIndexInChapter == key.getPageIndexInChapter()) {
                PageBitmap pageBitmap = mCaches.get(key);
                if (pageBitmap != null && pageBitmap.hasGallary()) {
                    rect = pageBitmap.getGallarys()[0].getGalleryRect();
                }
                break;
            }
        }
        return rect;
    }

    @Override
    public int checkCache(Chapter currentChapter, int currentPageIndexInChapter) {

        int freeCount = 0;
        synchronized (PageBitmapCache.getInstance()) {
//		synchronized (mCaches) {
            freeCount = checkCacheInner(currentChapter, currentPageIndexInChapter);
        }
        return freeCount;
    }

    public int checkCurrentPageCache(Chapter currentChapter, int currentPageIndexInChapter, int currPageSeqNum) {
        printLog(" checkCurrentPageCache start currentChapter = " + currentChapter + ", currIndex = " + currentPageIndexInChapter + ", seqNum=" + currPageSeqNum);
        int freeCount = 0;
        Map<ControllerWrapperImpl.PageIndexKey, PageBitmap> mMap = PageBitmapCache.getInstance().snapshot();
        synchronized (PageBitmapCache.getInstance()) {
            Iterator<PageIndexKey> iters = mMap.keySet().iterator();
            while (iters.hasNext()) {
                PageIndexKey freeKey = null;
                PageIndexKey iKey = iters.next();
                if (iKey.getChapter().equals(currentChapter)
                        && iKey.getPageIndexInChapter() == currentPageIndexInChapter
                        && iKey.getSequence() != currPageSeqNum) {
                    freeKey = iKey;
                }

                if (freeKey != null) {
                    PageBitmap pageBitmap = mMap.get(freeKey);
                    if (pageBitmap != null) {
                        pageBitmap.free();
                    }
                    iters.remove();
                    freeCount++;
                    printLog(" checkCurrentPageCache freeKey = " + freeKey);
                } else {
                    //printLog(" checkCache iKey = " + iKey);
                }
            }
        }
        printLog(" checkCurrentPageCache end currentChapter = " + currentChapter + ", currIndex = " + currentPageIndexInChapter + ", freeCount=" + freeCount);
        return freeCount;
    }

    private int checkCacheInner(Chapter currentChapter, int currentPageIndexInChapter) {
        int freeCount = 0;
        Chapter prevChapter = getPrevOrNextChapter(DPageIndex.Previous, currentChapter);
        Chapter nextChapter = getPrevOrNextChapter(DPageIndex.Next, currentChapter);
        Iterator<PageIndexKey> iters = PageBitmapCache.getInstance().snapshot().keySet().iterator();
        while (iters.hasNext()) {
            PageIndexKey freeKey = null;
            PageIndexKey iKey = iters.next();
            if (iKey.getChapter().equals(currentChapter)) {
                int diff = currentPageIndexInChapter - iKey.getPageIndexInChapter();
                if (Math.abs(diff) >= 2) {
                    freeKey = iKey;
                }
            } else if (iKey.getChapter().equals(prevChapter)) {
                if (currentPageIndexInChapter <= 1) {//当前是否章节第一页
                    int prevPageCount = getChapterPageCount(prevChapter);
                    int diff = prevPageCount - iKey.getPageIndexInChapter();
                    if (Math.abs(diff) >= 1) {
                        freeKey = iKey;
                    }
                } else {
                    freeKey = iKey;
                }
            } else if (iKey.getChapter().equals(nextChapter)) {
                int currentPageCount = getChapterPageCount(currentChapter);
                if (currentPageIndexInChapter == currentPageCount) {//当前是否章节最后一页
                    int diff = 1 - iKey.getPageIndexInChapter();
                    if (Math.abs(diff) >= 1) {
                        freeKey = iKey;
                    }
                } else {
                    freeKey = iKey;
                }
            } else {
                freeKey = iKey;
            }

            if (freeKey != null) {
                PageBitmap pageBitmap = PageBitmapCache.getInstance().snapshot().get(freeKey);
                if (pageBitmap != null) {
                    pageBitmap.free();
                }
                iters.remove();
                freeCount++;
                //printLog(" checkCache freeKey = " + freeKey);
            } else {
                //printLog(" checkCache iKey = " + iKey);
            }
        }
        printLog(" checkCache end cacheSize = " + PageBitmapCache.getInstance().snapshot().size() + ", freeCount = " + freeCount);
        return freeCount;
    }

    private void setPageBitmapCache(PageIndexKey key, PageBitmap pageBitmap) {
        PageBitmapCache.getInstance().putPageBitmap(key, pageBitmap);
    }

    public boolean isCacheChapter(Chapter chapter) {
        return mBookManager.isCacheChapter(chapter);
    }

    public boolean isInPageInfoCache(Chapter chapter) {
        return mBookManager.isInPageInfoCache(chapter);
    }

	/*@Override
    public int getPageIndexByAnchor(final Chapter chapter, String anchor) {
		return 1;//mBookManager.getPageIndexInHtmlByAnchor(chapter, anchor);
	}*/

    @Override
    public int getPageIndexInChapter(final Chapter chapter, int elementIndexInChapter) {
        return mBookManager.getPageIndexInChapter(chapter, elementIndexInChapter);
    }

    @Override
    public Rect[] getSelectedRectsByIndex(Chapter chapter, int pageIndexInChapter, ElementIndex startIndex, ElementIndex endIndex) {
        return mBookManager.getSelectedRectsByIndex(chapter, pageIndexInChapter, startIndex, endIndex);
    }

    public NoteRect[] getSelectedRects(Chapter chapter, int pageIndexInChapter, List<BookNote> notes) {

        final int size = notes.size();
        NoteRect[] nRects = new NoteRect[size];
        for (int i = 0; i < size; i++) {
            BookNote note = notes.get(i);

            int startIndex = note.getNoteStart();
            int endIndex = note.getNoteEnd();
            Rect[] rects = mBookManager.getSelectedRectsByIndex(chapter, pageIndexInChapter, new ElementIndex(startIndex), new ElementIndex(endIndex));

            NoteRect nRect = new NoteRect();
            nRect.setRects(rects);
            if (rects != null && rects.length > 0) {
                nRect.setHasNote(!TextUtils.isEmpty(note.getNoteText()));
            }
            nRect.setChapterIndex(getChaterIndex(chapter));
            nRect.setPageIndexInChapter(pageIndexInChapter);
            nRect.setFlag(getNoteFlag(note));
            nRect.setDrawLineColor(note.getDrawLineColor());

            nRects[i] = nRect;
        }
		
		/*IndexRange[] ranges = convertRanges(notes);
		
		//Rect[][] rss = new Rect[ranges.length][];
		for(int i = 0, len = ranges.length; i < len; i++){
			IndexRange range = ranges[i];
			int startIndex = range.getStartIndex();
			int endIndex = range.getEndIndex();
			Rect[] rects = mBookManager.getSelectedRectsByIndex(chapter, pageIndexInChapter, startIndex, endIndex);
			
			NoteRect nRect = new NoteRect();
			nRect.setRects(rects);
			rss[i] = nRect;
		}*/
        return nRects;
    }

    private NoteFlag getNoteFlag(BookNote note) {

        NoteFlag flag = new NoteFlag();
        flag.setChapterIndex(note.getChapterIndex());
        flag.setStartIndex(note.getNoteStart());
        flag.setEndIndex(note.getNoteEnd());

        return flag;
    }

    public TmpRect[] getSearchRects(final Chapter chapter, int currentPageIndexInChapter, ElementIndex startIndex, ElementIndex endIndex) {
        List<TmpRect> rectList = new ArrayList<TmpRect>();
        OneSearch currSearch = SearchDataHolder.getHolder().getCurrent();
        if (currSearch != null) {
            final Chapter searchChapter = currSearch.getChapter();
            if (chapter.equals(searchChapter)) {
                ElementIndex searchStartEmtIndex = currSearch.getKeywordStartIndex();
                ElementIndex searchEndEmtIndex = currSearch.getKeywordEndIndex();
                int searchPageIndex = getPageIndexInChapter(searchChapter, searchStartEmtIndex.getIndex());
                if (searchPageIndex == currentPageIndexInChapter) {

                    List<OneSearch> pageSearchs = SearchDataHolder.getHolder().getSearchesByRange(searchChapter,
                            startIndex.getIndex(), endIndex.getIndex());
                    if (pageSearchs != null) {
                        for (int i = 0, len = pageSearchs.size(); i < len; i++) {
                            OneSearch tmpSearch = pageSearchs.get(i);
                            TmpRect searchRect = getTmpRect(searchChapter, tmpSearch.getKeywordStartIndex(),
                                    tmpSearch.getKeywordEndIndex(), searchPageIndex);
                            searchRect.setType(currSearch.equals(tmpSearch) ? TmpRect.TYPE_CURRENT : TmpRect.TYPE_NOCURR);
                            rectList.add(searchRect);
                        }
                    } else {
                        TmpRect searchRect = getTmpRect(searchChapter, searchStartEmtIndex,
                                searchEndEmtIndex, searchPageIndex);
                        searchRect.setType(TmpRect.TYPE_CURRENT);
                        rectList.add(searchRect);
                    }
                }
            }
        }
        final int size = rectList.size();
        TmpRect[] rectss = null;
        if (size > 0) {
            rectss = new TmpRect[size];
            rectList.toArray(rectss);
        }
        return rectss;
    }

    protected TmpRect getTmpRect(final Chapter searchChapter, ElementIndex searchStartEmtIndex, ElementIndex searchEndEmtIndex, int searchPageIndex) {
        Rect[] rects = getSelectedRectsByIndex(searchChapter, searchPageIndex, searchStartEmtIndex, searchEndEmtIndex);
        TmpRect searchRect = new TmpRect();
        searchRect.setRects(rects);
        return searchRect;
    }
	
	/*private IndexRange[] convertRanges(List<BookNote> notes){
		
		final int size = notes.size();
		IndexRange[] ranges = new IndexRange[size];
		for(int i = 0; i < size; i++){
			BookNote note = notes.get(i);
			ranges[i] = new IndexRange(note.getNoteStart(), note.getNoteEnd());
		}
		return ranges;
	}*/

    protected void asynDrawPage(DrawPageAsycCommand command) {
        mBookManager.drawPage(command);
    }

    @Override
    public void asynDrawPage(PageWrap pageWrap, int bgType, int pageWidth, int pageHeight, IDrawPageListener l) {

        DrawPageAsycCommand command = new DrawPageAsycCommand();
        command.setChapter(pageWrap.getChapter());
        command.setPageIndexInChapter(pageWrap.getPageIndexInChapter());
        command.setCache(pageWrap.isCache());
        command.setLast(pageWrap.isLastPage());
        command.setBgType(bgType);
        command.setPageSize(new PageSize(pageWidth, pageHeight));
        command.setAsynListener(new IAsynListener() {
            @Override
            public void onAsyn(BaseTaskKey pcommand, BaseTaskResult presult) {

                DrawPageAsycCommand command = (DrawPageAsycCommand) pcommand;
                DrawPageResult result = (DrawPageResult) presult;
                PageIndexKey key = PageIndexKey.getKey(command.getChapter(), command.getPageIndexInChapter(), command.getPageSequenceNum());
                IDrawPageListener drawListener = mPageListener.remove(key);
                if (drawListener != null) {
                    drawListener.onDrawPage(command, result);
                }

                //考虑 result.getStatus()
                //TODO 缓存修改
                PageBitmap pageBitmap = PageBitmapCache.getInstance().getPageBitmap(key);
                if (pageBitmap == null)
                    pageBitmap = new PageBitmap();
                pageBitmap.setBitmap(result.getBitmap());
                pageBitmap.setPageRange(result.getPageRange());
                pageBitmap.setPageType(result.getPageType());
                pageBitmap.setGallarys(result.getGallarys());
                pageBitmap.setVideoRect(result.getVideoRect());
				pageBitmap.setListInteractiveBlocks(result.getListInteractiveBlocks());
                //修正index
                if (command.isLast()) {
                    int pageIndexInChapter = getChapterPageCount(command.getChapter());
                    key = PageIndexKey.getKey(command.getChapter(), pageIndexInChapter, command.getPageSequenceNum());
                }
                setPageBitmapCache(key, pageBitmap);
            }

        });
        command.setSync(false);

        PageIndexKey key = PageIndexKey.getKey(pageWrap.getChapter(), pageWrap.getPageIndexInChapter(), pageWrap.getPageSequenceNum());
        mPageListener.put(key, l);

        asynDrawPage(command);
    }


    @Override
    public void asynGotoPage(GotoPageCommand command, IGotoPageListener l) {

        command.setAsynListener(new IAsynListener() {
            @Override
            public void onAsyn(BaseTaskKey tcommand, BaseTaskResult tresult) {
                IGotoPageListener gotoListener = mGotoPageListener.remove(tcommand);

                if (gotoListener != null) {
                    GotoPageCommand command = (GotoPageCommand) tcommand;
                    GotoPageResult result = (GotoPageResult) tresult;
                    printLog(" asynGotoPage gotoListener " + gotoListener + ","
                            + result.getChapter() + ", " + result.getPageIndexInChapter());
                    gotoListener.onGotoPage(command, result);
                } else {
                    printLog(" asynGotoPage gotoListener == null");
                }
            }
        });
        command.setCacheChapter(mBookManager.hasCache(command.getChapter()));
        mGotoPageListener.put(command, l);

        asynGotoPage(command);
		/*printLog(" asynGotoPage put " + command.getChapter() + "," + command.getElementIndex() 
				+ ", " + command.getAnchor());*/
    }

    protected void asynGotoPage(final BaseBookManager.GotoPageCommand command) {
        mBookManager.asynGoto(command);
    }

    public Chapter getPrevOrNextChapter(DPageIndex pageIndex, Chapter currentChapter) {

        Chapter tmpCurrChapter = currentChapter;
        Chapter chapter = currentChapter;
        List<Chapter> chapters = getBook().getChapterList();
        if (chapters == null) {
            return tmpCurrChapter;
        }
        int htmlInChapters = chapters.indexOf(chapter);
        int tmpHtmlIndex = -1;
        if (pageIndex == DPageIndex.Previous) {
            tmpHtmlIndex = htmlInChapters - 1;
        } else if (pageIndex == DPageIndex.Next) {
            tmpHtmlIndex = htmlInChapters + 1;
        } else if (pageIndex == DPageIndex.Current) {
            tmpHtmlIndex = htmlInChapters;
        }
        if (tmpHtmlIndex < 0 || tmpHtmlIndex >= chapters.size()) {
            tmpCurrChapter = currentChapter;
        } else {
            tmpCurrChapter = chapters.get(tmpHtmlIndex);
        }
        if (tmpCurrChapter == null) {
            printLog(" getPrevOrNextHtml tmpHtmlIndex = " + tmpHtmlIndex + ", chaptersize = " + chapters.size());
        }
		/*printLog("lxu getPrevOrNextHtml " + tmpCurrChapter + ", tmpHtmlIndex = " + tmpHtmlIndex + ", currentHtml = " + currentChapter
				 + ", pageIndex = " + pageIndex);*/
        return tmpCurrChapter;
    }

    /**
     * @param chapter
     * @param pageIndexInChapter
     * @param isLast             表示是否取章节最后一页
     * @return
     */
    public String getHeaderName(Chapter chapter, int pageIndexInChapter, boolean isLast) {
        String name = "";
        if (pageIndexInChapter == 1 && !isLast) {
            name = getReadInfo().getBookName();
        } else {
            BaseNavPoint nPoint = getBook().getNavPoint(chapter, pageIndexInChapter);
            name = nPoint != null ? nPoint.getLableText() : "";
        }
        //int chaperIndex = getChaterIndex(chapter);//TODO 要改
        return name;
    }

    @Override
    public void reset() {
//		Iterator<PageIndexKey> iters = mCaches.keySet().iterator();
//		while(iters.hasNext()){
//			try {
//				PageBitmap mValue = mCaches.get(iters.next());
//				if(mValue != null){
//					mValue.free();
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
        try {
            PageBitmapCache.getInstance().clear();
//			mCaches.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        printLog(" reset() ");
        try {
            mPageListener.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            mGotoPageListener.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected ReadInfo getReadInfo() {
        return (ReadInfo) mReadApp.getReadInfo();
    }

    protected Book getBook() {
        return (Book) mReadApp.getBook();
    }

    @Override
    public boolean isBookComposingDone() {
        return mBookManager.isBookComposingDone();
    }

    public void printLog(String msg) {
        LogM.i(getClass().getSimpleName(), msg);
    }

    public void printLogE(String msg) {
        LogM.e(getClass().getSimpleName(), msg);
    }

    public static class PageIndexKey {

        private Chapter chapter;
        private int pageIndexInChapter;
        /**
         * 序号
         * 每一页有几张不同的图
         * 比如：画廊
         */
        private int sequence;

        public PageIndexKey() {

        }

        public Chapter getChapter() {
            return chapter;
        }

        public void setChapter(Chapter chapter) {
            this.chapter = chapter;
        }

        public int getPageIndexInChapter() {
            return pageIndexInChapter;
        }

        public void setPageIndexInChapter(int pageIndexInChapter) {
            this.pageIndexInChapter = pageIndexInChapter;
        }

        public int getSequence() {
            return sequence;
        }

        public void setSequence(int sequence) {
            this.sequence = sequence;
        }

        public static PageIndexKey getKey(Chapter chapter, int pageIndexInChapter, int sequenceNum) {

            PageIndexKey key = new PageIndexKey();
            key.setChapter(chapter);
            key.setPageIndexInChapter(pageIndexInChapter);
            key.setSequence(sequenceNum);

            return key;
        }

        @Override
        public boolean equals(Object o) {

            if (o == null || !(o instanceof PageIndexKey)) {
                return false;
            }
            PageIndexKey other = (PageIndexKey) o;
            return getChapter().equals(other.getChapter())
                    && getPageIndexInChapter() == other.getPageIndexInChapter()
                    && getSequence() == other.getSequence();
        }

        @Override
        public int hashCode() {
            if (getChapter() == null) {
                return super.hashCode();
            }

            String toS = toString();
            int hashCode = toS.hashCode();
            //LogM.e("PageIndexKey", toS + ", hashCode = " + hashCode);

            return hashCode;
        }

        @Override
        public String toString() {

            String path = getChapter().getPath();
            if (getChapter() instanceof TxtChapter) {
                TxtChapter tc = (TxtChapter) getChapter();
                path = tc.getTagPath();
            }
            StringBuilder sbd = new StringBuilder(path);
            sbd.append("-");
            sbd.append(getPageIndexInChapter());
            sbd.append("-");
            sbd.append(getSequence());

            return sbd.toString();
        }

    }


}
