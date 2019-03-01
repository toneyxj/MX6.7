package com.dangdang.reader.dread.core.epub;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import com.dangdang.reader.cloud.MarkNoteManager;
import com.dangdang.reader.dread.cache.PageBitmapCache;
import com.dangdang.reader.dread.config.NoteRect;
import com.dangdang.reader.dread.config.PageType;
import com.dangdang.reader.dread.config.ReadConfig;
import com.dangdang.reader.dread.config.TmpRect;
import com.dangdang.reader.dread.core.base.BasePageAdapter;
import com.dangdang.reader.dread.core.base.BasePageView;
import com.dangdang.reader.dread.core.base.BaseReaderApplicaion;
import com.dangdang.reader.dread.core.base.BaseReaderController;
import com.dangdang.reader.dread.core.base.BaseReaderWidget;
import com.dangdang.reader.dread.core.base.IEpubPageView.DrawingType;
import com.dangdang.reader.dread.core.base.IReaderController;
import com.dangdang.reader.dread.core.base.IReaderController.DPageIndex;
import com.dangdang.reader.dread.core.epub.BaseControllerWrapper.PageWrap;
import com.dangdang.reader.dread.core.part.PartEndPageView;
import com.dangdang.reader.dread.data.BookNote;
import com.dangdang.reader.dread.data.ReadInfo;
import com.dangdang.reader.dread.format.BaseBookManager.DrawPageAsycCommand;
import com.dangdang.reader.dread.format.BaseBookManager.DrawPageResult;
import com.dangdang.reader.dread.format.Chapter;
import com.dangdang.reader.dread.format.IndexRange;
import com.dangdang.reader.dread.holder.GalleryIndex;
import com.dangdang.reader.dread.holder.PageBitmap;
import com.dangdang.reader.dread.holder.PageState;
import com.dangdang.reader.dread.jni.BaseJniWarp.ElementIndex;
import com.dangdang.reader.utils.Utils;
import com.dangdang.zframework.log.LogM;
import com.mx.mxbase.constant.APPLog;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.List;

public class EpubPageAdapter extends BasePageAdapter {

    protected BaseReaderController mController;
    protected BaseControllerWrapper mControllerWrapper;
    protected BaseReaderApplicaion mReaderApp;
    protected DrawWrapper mDrawWrapper;

    protected MarkNoteManager mMarkNoteManager;

    protected ViewGroup mContainer;
    protected BasePageView mReadEndPageView;
    protected Handler mHandler;

    public EpubPageAdapter() {
        mHandler = new MyHandler(this);
        mDrawWrapper = new DrawWrapper();
        mDrawWrapper.init();
    }

    @Override
    public View getView(final DPageIndex pageIndex, View convertView,
                        BaseReaderWidget parent) {

        // printLogE(" getView start " + pageIndex);
        mContainer = parent;
        View pageView = adapterView(pageIndex, convertView, parent);

        // printLogE(" getView end " + pageIndex);
        return pageView;
    }

    protected View adapterView(final DPageIndex pageIndex, View convertView,
                               BaseReaderWidget parent) {
        View pageView = null;
        final Chapter currentChapter = mController.getCurrentChapter();
        if (currentChapter != null && (isReadEndPage(pageIndex)/* || isOutPlanRange(pageIndex, false)*/)) {
            printLog(" adapterView isReadEndPage=true ");
            pageView = adapterReadEndView(parent);
        } else {
            pageView = adapterPageView(pageIndex, convertView, parent);
        }
        return pageView;
    }

    protected boolean isReadEndPage(final DPageIndex pageIndex) {
        return getReadInfo().isDangEpub()
                && mController.isLastPageInBook(pageIndex);// mReaderApp.isEpub()
    }

    protected View adapterReadEndView(ViewGroup parent) {
        if (mReadEndPageView == null) {
            initReadEndPage(parent.getContext());
        }
        mReadEndPageView.updatePageStyle();
        if (mReadEndPageView.getVisibility() != View.VISIBLE) {
            mReadEndPageView.setVisibility(View.VISIBLE);
        }
        return mReadEndPageView;
    }

    protected void initReadEndPage(Context context) {
//		ReadEndPageView readEndView = new ReadEndPageView(context);
//		readEndView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
//				LayoutParams.MATCH_PARENT));
//		readEndView.setBookType(getReadInfo().getBookType());
//		readEndView.initList(getReadInfo().getProductId());
//		mReadEndPageView = readEndView;
        PartEndPageView readEndView = new PartEndPageView(context);
        readEndView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        readEndView.setBookType(getReadInfo().getBookType());
        mReadEndPageView = readEndView;
    }

    protected ReadInfo getReadInfo() {
        return (ReadInfo) mReaderApp.getReadInfo();
    }

    protected View adapterPageView(final DPageIndex pageIndex, View convertView,
                                   BaseReaderWidget parent) {

        // printLog(" getView adapterPageView start " + pageIndex);
        EpubPageView ePageView = null;
        if (convertView == null || !(convertView instanceof EpubPageView)) {
            ePageView = new EpubPageView(parent.getContext());
            ePageView.setOnGalleryPageChangeListener(parent
                    .getGalleryPageListener());
            ePageView.setLayoutParams(new LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        } else {
            ePageView = (EpubPageView) convertView;
        }
        int width = parent.getWidth();
        int height = parent.getHeight();
        boolean isLoading = true;
        final Chapter currentChapter = mController.getCurrentChapter();
        if (currentChapter != null) {
            final int currentPageIndexInChapter = mController
                    .getCurrentPageIndexInChapter();
            PageWrap pageWrap = mControllerWrapper.getPageWrap(pageIndex,
                    currentChapter, currentPageIndexInChapter);

            final Chapter tmpChapter = pageWrap.getChapter();
            final int tmpPageIndexInChapter = pageWrap.getPageIndexInChapter();
            final String headName = pageWrap.getHeaderName();

            PageBitmap pageBitmap = mControllerWrapper.getCacheBitmap(pageWrap);
            // printLog("lxu getView adapterPageView pageIndex = " + pageIndex +
            // ", " + pageWrap.isCache + ", " + pageWrap + ",  pageBitmap = " +
            // pageBitmap);

            if (pageBitmap != null) {
                isLoading = false;
                handlePageDataToUI(ePageView, pageBitmap, tmpChapter,
                        tmpPageIndexInChapter, false);
            } else {
                int bgType = 0;
                asynDrawPage(pageWrap, bgType, width, height);
            }
            ePageView.setHead(headName);
            setPageViewTag(ePageView, tmpChapter, tmpPageIndexInChapter);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mControllerWrapper.checkCache(currentChapter,
                            currentPageIndexInChapter);
                }
            });
        }
        // printLog(" getView adapterPageView 8 " + pageIndex + ", chapter=" +
        // (currentChapter != null));
        if (isLoading) {
            ePageView.reset();
            ePageView.hideHeaderAndFooter();
            ePageView.showLoading();
        }
        printLog(" getView adapterPageView end " + pageIndex + ", isLoading = "
                + isLoading + ", " + ePageView);
        return ePageView;
    }

    @Override
    public void refreshView(DPageIndex pageIndex, BasePageView pageView,
                            BaseReaderWidget parent) {
        final GalleryIndex galleryIndex = getGIndex(pageView);
        // printLog(" refreshView " + pageIndex + "," + galleryIndex);
        refreshView(pageIndex, galleryIndex, pageView, parent);
    }

    protected GalleryIndex getGIndex(BasePageView pageView) {
        PageState state = pageView.getPageState();
        GalleryIndex galleryIndex = state == null ? null : state
                .getGalleryIndex();
        return galleryIndex;
    }

    @Override
    public void refreshView(DPageIndex pageIndex, GalleryIndex galleryIndex,
                            BasePageView pageView, BaseReaderWidget parent) {

        if (pageView instanceof EpubPageView) {
            int frameIndex = galleryIndex != null ? galleryIndex
                    .getFrameIndex() : 0;
            refreshViewInner(pageIndex, frameIndex, pageView, parent);
            if (galleryIndex != null && pageIndex == DPageIndex.Current) {
                final Chapter currentChapter = mController.getCurrentChapter();
                if (currentChapter != null) {
                    final int currentPageIndexInChapter = mController
                            .getCurrentPageIndexInChapter();
                    mControllerWrapper.checkCurrentPageCache(currentChapter,
                            currentPageIndexInChapter, frameIndex);
                }
                PageState state = new PageState();
                state.setGalleryIndex(galleryIndex);
                pageView.setPageState(state);
            }
        } else if (pageView instanceof PartEndPageView) {

        } else {
            printLog("lxu refreshView pageIndex = " + pageIndex + ",otherV="
                    + pageView);
        }
        pageView.updatePageStyle();
    }

    protected void refreshViewInner(DPageIndex pageIndex, int pageSequenceNum,
                                    BasePageView pageView, ViewGroup parent) {
        EpubPageView epubPageView = (EpubPageView) pageView;
        int pageWidth = parent.getWidth();
        int pageHeight = parent.getHeight();
        final Chapter currentChapter = mController.getCurrentChapter();
        if (currentChapter != null) {
            final int currentPageIndexInChapter = mController
                    .getCurrentPageIndexInChapter();
            PageWrap pageWrap = mControllerWrapper.getPageWrap(pageIndex,
                    currentChapter, currentPageIndexInChapter);
            pageWrap.setPageSequenceNum(pageSequenceNum);

            final Chapter tmpChapter = pageWrap.getChapter();
            final int tmpPageIndexInChapter = pageWrap.getPageIndexInChapter();
            // final String headName = pageWrap.headerName;

            PageBitmap pageBitmap = mControllerWrapper.getCacheBitmap(pageWrap);
            printLog("lxu refreshView pageIndex = " + pageIndex + ", "
                    + pageWrap.isCache + ", " + pageWrap + ",  pageBitmap = "
                    + pageBitmap);

            if (pageBitmap == null) {
                DrawPageResult dpResult = mControllerWrapper.drawPage(pageWrap,
                        pageWidth, pageHeight);
                pageBitmap = new PageBitmap();
                pageBitmap.setBitmap(dpResult.getBitmap());
                pageBitmap.setPageRange(dpResult.getPageRange());
                pageBitmap.setPageType(dpResult.getPageType());
                // pageBitmap.setGallarys(dpResult.getGallarys());
            }
            if (pageBitmap != null) {
                handlePageDataToUI(epubPageView, pageBitmap, tmpChapter,
                        tmpPageIndexInChapter, false);
            }
            setPageViewTag(epubPageView, tmpChapter, tmpPageIndexInChapter);
        }
    }

    protected EpubPageView findPageViewByTag(final Chapter chapter,
                                             final int pageIndexInChapter) {
        PageKey tag = getPageKey(chapter, pageIndexInChapter);
        EpubPageView pageView = (EpubPageView) mContainer.findViewWithTag(tag);
        // printLog(" viewtag find = " + tag + "," + (pageView == null));
        return pageView;
    }

    protected void setPageViewTag(EpubPageView epubPageView,
                                  final Chapter tmpChapter, final int tmpPageIndexInChapter) {
        PageKey tag = getPageKey(tmpChapter, tmpPageIndexInChapter);
        epubPageView.setTag(tag);
        // printLog(" viewtag set = " + tag);
    }

    protected PageKey getPageKey(Chapter chapter, int pageIndexInChapter) {
        PageKey key = new PageKey();
        key.setChapter(chapter);
        key.setPageIndexInChapter(pageIndexInChapter);
        return key;
    }

    protected void asynDrawPage(final PageWrap pageWrap, final int bgType,
                                final int pageWidth, final int pageHeight) {

		/*
         * DrawPageAsycCommand command = new DrawPageAsycCommand();
		 * command.setChapter(pageWrap.chapter);
		 * command.setPageIndexInChapter(pageWrap.pageIndexInChapter);
		 * command.setLast(pageWrap.isLastPage); command.setBgType(bgType);
		 * command.setPageSize(new PageSize(pageWidth, pageHeight));
		 * command.setListener(drawPageListener); command.setSync(false);
		 * 
		 * mDataBridge.asynDrawPage(command);
		 */
        new Thread() {
            @Override
            public void run() {
                try {
                    mControllerWrapper.asynDrawPage(pageWrap, bgType,
                            pageWidth, pageHeight, drawPageListener);
                } catch (Exception e) {
                    LogM.e(" controllerWrapper.asynDrawPage " + e.toString());
                }
            }
        }.start();

    }

    final IDrawPageListener drawPageListener = new IDrawPageListener() {
        @Override
        public void onDrawPage(final DrawPageAsycCommand command,
                               final DrawPageResult result) {

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    handleDrawPageResult(command, result);
                }
            });
        }
    };

    protected void handleDrawPageResult(final DrawPageAsycCommand command,
                                        final DrawPageResult result) {
        final Chapter chapter = command.getChapter();
        final int pageIndexInChapter = command.getPageIndexInChapter();
        // final int status = result.getStatus();
        final Bitmap bitmap = result.getBitmap();
        final HashSet<PageType> pageType = result.getPageType();
        final IndexRange pageRange = result.getPageRange();
        final int sequence = command.getPageSequenceNum();

        EpubPageView pageView = findPageViewByTag(chapter, pageIndexInChapter);

        printLog(" drawpagexyz onTask " + chapter + ", " + pageIndexInChapter
                + ", " + pageType + ", pageView=" + pageView);

        if (pageView != null) {
            ControllerWrapperImpl.PageIndexKey key = ControllerWrapperImpl.PageIndexKey.getKey(chapter, pageIndexInChapter, sequence);
            //TODO 缓存修改
            PageBitmap pageBitmap = PageBitmapCache.getInstance().getPageBitmap(key);
            if (pageBitmap == null)
                pageBitmap = new PageBitmap(pageType, pageRange, bitmap);
            pageBitmap.setGallarys(result.getGallarys());
            pageBitmap.setVideoRect(result.getVideoRect());
			pageBitmap.setListInteractiveBlocks(result.getListInteractiveBlocks());
            handlePageDataToUI(pageView, pageBitmap, chapter,
                    pageIndexInChapter, command.isLast());
            // 修正PageView的tag
            if (command.isLast()) {
                int pageIndexInChapterT = mControllerWrapper
                        .getChapterPageCount(chapter);
                setPageViewTag(pageView, chapter, pageIndexInChapterT);
            }
        } else {
            printLog(" viewtag onTask pageView == null ");
        }
    }

    protected void handlePageDataToUI(final EpubPageView pageView,
                                      PageBitmap pageBitmap, final Chapter chapter,
                                      final int pageIndexInChapter, final boolean isLast) {

        pageView.resetView();
        handleHeaderAndFooter(pageView, pageBitmap);

        int pageIndexTmp = pageIndexInChapter;
        if (isLast) {
            int chapterPageCount = mControllerWrapper
                    .getChapterPageCount(chapter);
            pageIndexTmp = chapterPageCount;
        }
        if (isBookComposingDone()) {
//            String progress=getProgress(pageIndexTmp, chapter);
//            //当前页数
//            int pageIndexInBook = getPageIndexInBook(chapter, pageIndexTmp);
            pageView.setPage(getCurrentProgress(chapter, pageIndexTmp));
        }else{
            //else是我自己添加的
            pageView.setPage(getCurrentProgress(chapter, pageIndexTmp));
        }
        // pageView.setPage(pageIndexTmp,
        // mControllerWrapper.getChapterPageCount(chapter));
        // pageView.setPage("[" + getChapterIndex(chapter) + "]" + pageIndexTmp
        // + " / " + mControllerWrapper.getChapterPageCount(chapter));

        NoteRect[] nRects = null;
        boolean bookmarkExist = false;
        if (!isPdf()) {
            IndexRange pageRange = pageBitmap.getPageRange();
            int chapterIndex = getChapterIndex(chapter);
            ElementIndex startIndex = pageRange.getStartIndex();
            ElementIndex endIndex = pageRange.getEndIndex();
            List<BookNote> notes = mMarkNoteManager.getBookNotes(chapterIndex,
                    startIndex.getIndex(), endIndex.getIndex());
            if (!Utils.isCollEmpty(notes)) {
                nRects = mControllerWrapper.getSelectedRects(chapter,
                        pageIndexTmp, notes);
                // pageView.addNote(rss);
            } else {
                printLogV(" notes is empty ");
            }
            TmpRect[] searchRect = mControllerWrapper.getSearchRects(chapter,
                    pageIndexTmp, startIndex, endIndex);
            pageView.setTmpRects(DrawingType.ShadowSearch, searchRect);
            bookmarkExist = mMarkNoteManager.checkMarkExist(getReadInfo()
                            .getDefaultPid(), getReadInfo().getEpubModVersion(), getReadInfo().isBoughtToInt(),
                    chapterIndex, startIndex.getIndex(), endIndex.getIndex());
            if (pageBitmap.hasGallary()) {
                pageView.setGallarys(pageBitmap.getGallarys());
            }
            if (pageBitmap.hasVideo()) {
                pageView.setVideoRect(pageBitmap.getVideoRect());
            }
			if (pageBitmap.hasInteractiveBlock()) {
				pageView.setInteractiveBlocks(pageBitmap.getListInteractiveBlocks(), chapter, pageIndexInChapter);
			}
				
        }
        pageView.updatePageInner(bookmarkExist, pageBitmap.getBitmap(),
                DrawingType.Line, nRects);
        // handleMarkAndNote(pageView, chapter, pageIndexTmp,
        // pageBitmap.getPageRange());
    }
    private String getCurrentProgress( Chapter chapter,int currentPage){
         ReadConfig readConfig = ReadConfig.getConfig();
        String value="";
//        double proprotion=0;
//        try {
//             proprotion=Double.parseDouble(progress.replace("%",""))/100;
//        }catch (Exception e){
//            return progress;
//        }
        String progress=getProgress(currentPage,chapter);

        int totalsize;
        try {
            totalsize=pageCount(chapter);
        }catch (Exception e){
            return progress;
        }

        APPLog.e("totalsize="+totalsize);
        switch (readConfig.getProgress()){
            case 0:
                float progrssF=currentPage*100f/totalsize;
                DecimalFormat format=new DecimalFormat("0.00");

                value= format.format(progrssF)+"%";
                break;
            case 1:
                value=currentPage+"页\t全章"+totalsize+"页";
                break;
            case 2:
                value=currentPage+"页\t剩余"+String.valueOf(totalsize-currentPage)+"页";
                break;
            case 3:
                break;
            default:
                value=progress;
                break;
        }
        return value;
    }
    private int pageCount(Chapter chapter){
        int chapterPageCount = mControllerWrapper
                .getChapterPageCount(chapter);
        if (mController.isLastChapter(chapter))
            chapterPageCount--;
        return chapterPageCount;
    }
    /**
     * 计算进度条进度值
     * @param pageInChapter
     * @param chapter
     * @return
     */
    protected String getProgress(int pageInChapter, Chapter chapter) {

        int pageIndexInBook = getPageIndexInBook(chapter, pageInChapter);
        int pageSize = getPageSize();
        return pageIndexInBook + "/" + pageSize;
    }

    protected void handleHeaderAndFooter(final EpubPageView pageView,
                                         PageBitmap pageBitmap) {
        if (pageBitmap.isShowHeader()) {
            pageView.showHeader();
        } else {
            pageView.hideHeader();
        }
        if (pageBitmap.isShowFooter()) {
            pageView.showFooter();
        } else {
            pageView.hideFooter();
        }
    }

    protected boolean isBookComposingDone() {
        return mControllerWrapper.isBookComposingDone();
    }

    protected int getPageIndexInBook(Chapter chapter, int pageIndexInChapter) {
        return mController.getPageIndexInBook(chapter, pageIndexInChapter);
    }

    protected int getPageSize() {
        return mController.getPageSize();
    }

    protected boolean isPdf() {
        return mReaderApp.isPdf();
    }

	/*
	 * protected void handleMarkAndNote(EpubPageView pageView, final Chapter
	 * chapter, final int pageIndexInChapter, final IndexRange pageRange) {
	 * 
	 * int chapterIndex = getChapterIndex(chapter); int startIndex =
	 * pageRange.getStartIndex(); int endIndex = pageRange.getEndIndex();
	 * List<BookNote> notes = mMarkNoteManager.getBookNotes(chapterIndex,
	 * startIndex, endIndex); if(!Utils.isCollEmpty(notes)){ IndexRange[] ranges
	 * = convertRanges(notes); Rect[][] rss =
	 * mControllerWrapper.getSelectedRects(chapter, pageIndexInChapter, ranges);
	 * pageView.addNote(rss); } else { printLogV(" notes is empty "); } }
	 */

    protected int getChapterIndex(final Chapter chapter) {
        return mControllerWrapper.getChaterIndex(chapter);
    }

    public void setController(IReaderController controller) {
        mController = (BaseReaderController) controller;
    }

    public void setDateBridge(BaseControllerWrapper dataBridge) {
        mControllerWrapper = dataBridge;
    }

    public void setReaderApp(BaseReaderApplicaion readerApp) {
        mReaderApp = readerApp;
        mMarkNoteManager = readerApp.getMarkNoteManager();
        // initReadEndPage(readerApp.getContext());
    }

    public static class PageKey {

        protected Chapter chapter;
        protected int pageIndexInChapter;

        public PageKey() {
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

        @Override
        public boolean equals(Object o) {

            if (o == null || !(o instanceof PageKey)) {
                return false;
            }

            PageKey other = (PageKey) o;
            return getChapter().equals(other.getChapter())
                    && getPageIndexInChapter() == other.getPageIndexInChapter();
        }

        @Override
        public int hashCode() {
            String sole = chapter.getPath() + "-" + getPageIndexInChapter();
            return sole.hashCode();
        }

        @Override
        public String toString() {
            return chapter.getPath() + "-" + getPageIndexInChapter();
        }
    }

    private static class MyHandler extends Handler {
        private final WeakReference<EpubPageAdapter> mFragmentView;

        MyHandler(EpubPageAdapter view) {
            this.mFragmentView = new WeakReference<EpubPageAdapter>(view);
        }

        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {
            EpubPageAdapter service = mFragmentView.get();
            if (service != null) {
                super.handleMessage(msg);
                try {
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
