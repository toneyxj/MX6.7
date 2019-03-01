package com.dangdang.reader.dread.format;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.text.TextUtils;

import com.dangdang.reader.dread.config.PageType;
import com.dangdang.reader.dread.config.ReadConfig;
import com.dangdang.reader.dread.data.OneSearch;
import com.dangdang.reader.dread.data.ReadInfo;
import com.dangdang.reader.dread.format.epub.ClickResult;
import com.dangdang.reader.dread.format.epub.ClickResult.AudioClickResult;
import com.dangdang.reader.dread.format.epub.ClickResult.ClickType;
import com.dangdang.reader.dread.format.epub.ClickResult.ImageClickResult;
import com.dangdang.reader.dread.format.epub.ClickResult.InnerGotoClickResult;
import com.dangdang.reader.dread.format.epub.ClickResult.InnerGotoType;
import com.dangdang.reader.dread.format.epub.ClickResult.InnerLabelClickResult;
import com.dangdang.reader.dread.format.epub.ClickResult.ToBrowserClickResult;
import com.dangdang.reader.dread.jni.BaseJniWarp;
import com.dangdang.reader.dread.jni.BaseJniWarp.EPageIndex;
import com.dangdang.reader.dread.jni.BaseJniWarp.EPoint;
import com.dangdang.reader.dread.jni.BaseJniWarp.ERect;
import com.dangdang.reader.dread.jni.BaseJniWarp.ElementIndex;
import com.dangdang.reader.dread.jni.ChaterInfoHandler;
import com.dangdang.reader.dread.jni.DrawInteractiveBlockHandler;
import com.dangdang.reader.dread.jni.DrmWarp;
import com.dangdang.reader.dread.jni.EpubWrap;
import com.dangdang.reader.dread.jni.EpubWrap.EInnerGotoResult;
import com.dangdang.reader.dread.jni.EpubWrap.EResult;
import com.dangdang.reader.dread.jni.ImageGalleryHandler;
import com.dangdang.reader.dread.jni.InteractiveBlockHandler;
import com.dangdang.reader.dread.jni.ParagraphTextHandler;
import com.dangdang.reader.dread.jni.SearchHandler;
import com.dangdang.reader.dread.jni.VideoInfoHandler;
import com.dangdang.zframework.log.LogM;
import com.mx.mxbase.constant.APPLog;

import java.io.File;
import java.util.HashSet;
import java.util.List;

/**
 * 基类
 *
 * @author Yhyu
 */
public abstract class BaseEBookManager extends BaseBookManager {

    protected Book mOneBook;
    // protected ParserEpubN mParseEpub;

    // protected ExecutorService mExetService;

    protected EpubWrap mDrwrap;

    public BaseEBookManager(Context context, Book oneBook) {
        super(context, oneBook);
        mOneBook = oneBook;// new Book();
        // mParseEpub = new ParserEpubN();
        mDrwrap = new EpubWrap();
        setBaseJni(mDrwrap);
        // initComposingStyle();
    }

    protected void initReadInfo(ReadInfo readInfo) {
        this.mReadInfo = readInfo;
        this.mBookFile = readInfo.getBookFile();
        // this.mBookDir = bookDir;
        this.mBookDir = mBookFile.substring(0, mBookFile.lastIndexOf("."))
                + File.separator;

    }

    public void startRead(ReadInfo readInfo) {
        initReadInfo(readInfo);
        if (isInitKey(readInfo))
            initKey();

        mDrwrap.setTextColorBlack(true,34);
    }

    public abstract boolean isInitKey(ReadInfo readInfo);

    public void clearPrev() {
        try {
            super.clearPrev();
            // mALabelMap.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateBackground(int bgColor, int foreColor) {

        int epubBgColor = bgColor;
        int epubForeColor = foreColor;
        if (ReadConfig.getConfig().isDefaultBg(bgColor)) {
            printLog(" updateBackground  isDefaultBg = true");
            epubBgColor = -1;
            epubForeColor = -1;
        }
        printLog(" updateBackground " + epubBgColor + "," + epubForeColor);
        super.updateBackground(epubBgColor, epubForeColor);
    }

    public int getElementIndexByAnchor(final Chapter chapter,
                                       final String anchor) {

        int elementIndexByHtml = -1;
        /*
		 * final Chapter html = chapter;//mOneBook.getHtmlByPath(htmlPath);
		 * if(html != null){ if(anchor != null && anchor.trim().length() > 0){
		 * final BookReader bookReader = mBookReaderMap.get(html);
		 * doChangeBookReader(html, bookReader); final List<ALabel> aLabelList =
		 * bookReader.getALabelArray(); final List<ALabel> aLabelList =
		 * mALabelMap.get(html); if(aLabelList != null){ for(ALabel al :
		 * aLabelList){ if(anchor.equals(al.getId())){ elementIndexByHtml =
		 * al.getStartIndex(); break; } } } } }
		 */

        return elementIndexByHtml;
    }

    public int getPageIndexInHtmlByAnchor(final Chapter chapter,
                                          final String anchor) {
        // printLog(" getPageIndexInHtmlByAnchor start " + chapter + "," +
        // anchor);
        int pageIndexInHtml = 1;
        if (!TextUtils.isEmpty(anchor)) {
            if (hasCache(chapter)) {
                // printLog(" getPageIndexByAchorInner cache true " + chapter +
                // "," + anchor);
                pageIndexInHtml = getPageByAnchorFromCache(chapter, anchor);
            } else {
                // printLog(" synchnzed getPageIndexByAchorInner start " +
                // chapter + "," + anchor);
                pageIndexInHtml = getPageIndexByAchorInner(chapter, anchor);
                // printLog(" synchnzed getPageIndexByAchorInner end " + chapter
                // + "," + anchor);
            }
            pageIndexInHtml += 1;
        }
        pageIndexInHtml = pageIndexInHtml < 1 ? 1 : pageIndexInHtml;
        // printLog(" getPageIndexInHtmlByAnchor end " + chapter + "," +
        // anchor);
        return pageIndexInHtml;
    }

    protected synchronized int getPageIndexByAchorInner(final Chapter chapter,
                                                        final String anchor) {
        int pageIndexInHtml = mDrwrap
                .getPageByALabel(chapter.getPath(), anchor);
        return pageIndexInHtml;
    }

    protected int getPageByAnchorFromCache(final Chapter chapter,
                                           final String anchor) {
        return getBookCache().getPageIndexByAnchor(chapter, anchor);
    }

    protected int getPageByElementIndexFromCache(final Chapter chapter,
                                                 final int elementIndex) {
        return getBookCache().getPageIndexByElementIndex(chapter, elementIndex);
    }

	/*
	 * --------------------------------- native method start
	 * -------------------------
	 */

    protected void initKey() {
        final boolean isPre = mReadInfo.isPreSet();
        LogM.d(getClass().getSimpleName(), " [ isPre = " + isPre + " ]");
        byte[] certBytes = mReadInfo.getBookCertKey();
        if (certBytes == null) {
            certBytes = new byte[1];
            printLogE(" initKey is empty");
        }
        DrmWarp.getInstance().initBookKey(mReadInfo.getBookDir(), certBytes,
                mReadInfo.getDefaultPid(), isPre);
        // mDrwrap.initKey(mReadInfo.getBookCertKey(),
        // mReadInfo.getDefaultPid(), isPre);
    }

    @Override
    public int getChapterPageCount(Chapter chapter, boolean onlyCache) {
        int pageCount = super.getChapterPageCount(chapter, onlyCache);
        if (mReadInfo.isDangEpub() && mOneBook.isLastChapter(chapter)) {
            pageCount += 1;
            printLog(" getChapterPageCountInner lastchapter ");
        }
        return pageCount;
    }

    @Override
    public int getChapterPageCount(Chapter chapter) {
        return getChapterPageCount(chapter, false);
    }

    @Override
    protected synchronized int getChapterStructInner(final Chapter chapter) {
        int pageCount = getNativePageCount(chapter);
        if (pageCount > 0) {
            saveChapterCache(chapter);
        }
        return pageCount;
    }

    protected int getNativePageCount(Chapter chapter) {
        EPageIndex pageIndex = getEPageIndex(chapter, 0);
        int pageCount = mDrwrap.getPageCount(pageIndex, false);
        return pageCount;
    }

    @Override
    protected ChaterInfoHandler getChapterInfo(Chapter chapter) {

        ChaterInfoHandler ciHandle = new ChaterInfoHandler();
        EPageIndex pageIndex = getEPageIndex(chapter, 0);
        mDrwrap.getChapterInfo(pageIndex, ciHandle);

        return ciHandle;
    }

    public int getPageIndexInChapter(Chapter chapter, int elementIndex) {
        // printLog(" synchnzed getPageIndexInChapter start " +
        // chapter.hashCode() + "," + elementIndex + ", " + hasCache(chapter));

        int pageByIndex = 1;
        if (hasCache(chapter)) {
            pageByIndex = getPageByElementIndexFromCache(chapter, elementIndex);
        } else {
            pageByIndex = getPageIndexInChapterSyn(chapter, elementIndex);
        }

        pageByIndex = pageByIndex + 1;
        if (pageByIndex < 1) {
            LogM.e(getClass().getSimpleName(),
                    " getPageIndexInHtml pageByIndex = " + pageByIndex);
        }
        // printLog(" synchnzed getPageIndexInChapter end " + chapter.hashCode()
        // + "," + elementIndex);
        return pageByIndex < 1 ? 1 : pageByIndex;
    }

    private synchronized int getPageIndexInChapterSyn(Chapter chapter, int elementIndex) {
        // printLog(" native synchnzed getPageIndexInChapter start " +
        // elementIndex);
        EPageIndex pageIndex = getEPageIndex(chapter, 0);
        int page = mDrwrap.getPageByIndex(pageIndex, elementIndex);
        // printLog(" native synchnzed getPageIndexInChapter end " +
        // elementIndex);
        return page;
    }

    public void destroy() {
        try {
            printLog(" destroy() start ");
            super.destroy();
            // mParseEpub.destory();
            // mALabelMap.clear();
            printLog(" destroy() end ");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected Object mDrawLock = new Object();

    @Override
    public int drawPage(Chapter chapter, int pageIndexInChapter,
                        int pageSeqNum, Bitmap bitmap, boolean sync) {
        synchronized (mDrawLock) {
            printLog("native synchnzed drawPage pageIndex = "
                    + pageIndexInChapter + " start " + ", sync=" + sync);
            EPageIndex pageIndex = getEPageIndex(chapter, pageIndexInChapter);
            pageIndex.subIndexInPage = pageSeqNum;
            int retStatus = 0;
            if (sync) {
                retStatus = drawPageInner(pageIndex, bitmap);
            } else {
                retStatus = mDrwrap.drawPage(pageIndex, bitmap);
            }
            printLog("native synchnzed drawPage pageIndex = "
                    + pageIndexInChapter + " end , status = " + retStatus);
            return retStatus;
        }
    }

    protected synchronized int drawPageInner(EPageIndex pageIndex, Bitmap bitmap) {
        return mDrwrap.drawPage(pageIndex, bitmap);
    }

    @Override
    protected DrawPageResult drawPageInner(Chapter chapter,
                                           int pageIndexInChapter, int pageSeqNum, int width, int height,
                                           boolean isSync) {
        DrawPageResult result = super.drawPageInner(chapter,
                pageIndexInChapter, pageSeqNum, width, height, isSync);
        HashSet<PageType> pageType = result.getPageType();
        if (pageType == null)
            return result;
        if (pageType.contains(PageType.Gallery)) {
            ImageGalleryHandler gallaryHandle = new ImageGalleryHandler();
            getGallaryInfo(chapter, pageIndexInChapter, gallaryHandle);
            result.setGallarys(gallaryHandle.getGallerys());
        }
        if (pageType.contains(PageType.Video)) {
            VideoInfoHandler videoInfoHandler = new VideoInfoHandler();
            getVideoInfo(chapter, pageIndexInChapter, videoInfoHandler);
            result.setVideoRect(videoInfoHandler.getmVideoRect());
        }
        if (pageType.contains(PageType.CodeInteractive) ||
                pageType.contains(PageType.TableInteractive)) {
            InteractiveBlockHandler interactiveBlockInfoHandler = new InteractiveBlockHandler();
            getInteractiveBlockInfo(chapter, pageIndexInChapter, interactiveBlockInfoHandler);
            result.setListInteractiveBlocks(interactiveBlockInfoHandler.getInteractiveBlockList());
        }

        return result;
    }


    protected int getVideoInfo(Chapter chapter, int pageIndexInChapter,
                               VideoInfoHandler videoInfoHandler) {
        EPageIndex pageIndex = getEPageIndex(chapter, pageIndexInChapter);
        return mDrwrap.getVideoInfo(pageIndex, videoInfoHandler);
    }

    protected int getGallaryInfo(Chapter chapter, int pageIndexInChapter,
                                 ImageGalleryHandler handler) {
        EPageIndex pageIndex = getEPageIndex(chapter, pageIndexInChapter);
        return mDrwrap.getGalleryInfo(pageIndex, handler);
    }

    protected int getInteractiveBlockInfo(Chapter chapter, int pageIndexInChapter,
                                          InteractiveBlockHandler interactiveBlockInfoHandler) {
        EPageIndex pageIndex = getEPageIndex(chapter, pageIndexInChapter);
        return mDrwrap.getInteractiveBlocks(pageIndex, interactiveBlockInfoHandler);
    }

    public void DrawInteractiveBlock(Chapter chapter, int pageIndexInChapter, int nBlockIndex, int nWidth, int nHeight,
                                     DrawInteractiveBlockHandler handler) {
        EPageIndex pageIndex = getEPageIndex(chapter, pageIndexInChapter);
        mDrwrap.drawInteractiveBlock(pageIndex, nBlockIndex, nWidth, nHeight, handler);
    }

    @Override
    public boolean isCacheChapter(Chapter chapter) {
        if (chapter == null)
            return false;
        EPageIndex pageIndex = getEPageIndex(chapter, 0);
        boolean is = mDrwrap.isInBookCache(pageIndex);
        printLog("native isCacheChapter " + pageIndex.filePath + ", is = " + is);
        return is;
    }

    @Override
    public boolean isInPageInfoCache(Chapter chapter) {
        EPageIndex pageIndex = getEPageIndex(chapter, 0);
        boolean is = mDrwrap.isInPageInfoCache(pageIndex);
        printLog(" native isInPageInfoCache " + pageIndex.filePath + ", is = " + is);
        return is;
    }

    @Override
    public Rect[] getSelectedRectsByPoint(Chapter chapter,
                                          int pageIndexInChapter, Point start, Point end) {
        APPLog.e("WrapClass-RectsByPointstart",start.toString());
//        APPLog.e("WrapClass-RectsByPointend",end.toString());
        // printLog(" native getSelectedRectsByPoint start " +
        // pageIndexInChapter);
        EPageIndex pageIndex = getEPageIndex(chapter, pageIndexInChapter);
        EPoint pStart = convertPaint(start);
        EPoint pEnd = convertPaint(end);
        ERect[] rs = null;
        if (ReadConfig.isFirstLongClick){
            rs = mDrwrap.getWordRectsByPoint(pageIndex,
                    convertPaint(end));
        }else {
            rs = mDrwrap.getSelectedRectsByPoint(pageIndex, pStart, pEnd);
        }
        // printLog(" native getSelectedRectsByPoint end " +
        // pageIndexInChapter);
        ReadConfig.isFirstLongClick=false;
        return convertRect(rs);
    }

    @Override
    public Rect[] getSelectedRectsByIndex(Chapter chapter,
                                          int pageIndexInChapter, ElementIndex startIndex,
                                          ElementIndex endIndex) {
//        APPLog.e("getSelectedRectsByIndex-长按进入");
        // printLog(" native getSelectedRectsByIndex start " +
        // pageIndexInChapter);
        EPageIndex pageIndex = getEPageIndex(chapter, pageIndexInChapter);
        ERect[] rs = mDrwrap.getSelectedRectsByIndex(pageIndex,
                startIndex.getIndex(), endIndex.getIndex());
        // printLog(" native getSelectedRectsByIndex end " +
        // pageIndexInChapter);

        return convertRect(rs);
    }

    @Override
    public ElementIndex[] getSelectedStartAndEndIndex(Chapter chapter,
                                                      int pageIndexInChapter, Point start, Point end) {

        // printLog(" native getSelectedStartAndEndIndex start " +
        // pageIndexInChapter);
        EPageIndex pageIndex = getEPageIndex(chapter, pageIndexInChapter);
        EPoint pStart = convertPaint(start);
        EPoint pEnd = convertPaint(end);
        int[] startEnds = mDrwrap.getSelectedStartAndEndIndex(pageIndex,
                pStart, pEnd);
        // printLog(" native getSelectedStartAndEndIndex end " +
        // pageIndexInChapter);
        ElementIndex[] eis = {new ElementIndex(startEnds[0]),
                new ElementIndex(startEnds[1])};

        return eis;
    }

    public ElementIndex getElementIndexByPoint(Chapter chapter,
                                               int pageIndexInChapter, Point point) {
        EPageIndex pageIndex = getEPageIndex(chapter, pageIndexInChapter);
        EPoint pPoint = convertPaint(point);
        int elementIdex = mDrwrap.getElementIndexByPoint(pageIndex, pPoint);
        return new ElementIndex(elementIdex);
    }

    @Override
    public IndexRange getPageStartAndEndIndexInner(Chapter chapter,
                                                   int pageIndexInChapter) {
        // printLog(" synchnzed getPageStartAndEndIndexInner start " +
        // chapter.hashCode() + ",pi=" + pageIndexInChapter);
        EPageIndex pageIndex = getEPageIndex(chapter, pageIndexInChapter);
        IndexRange range = getPageStartAndEndIndexInner(pageIndex);
        // printLog(" synchnzed getPageStartAndEndIndexInner end " +
        // chapter.hashCode() + ",pi=" + pageIndexInChapter);
        return range;
    }

    protected synchronized IndexRange getPageStartAndEndIndexInner(
            EPageIndex pageIndex) {
        // printLog(" native synchnzed getPageStartAndEndIndexInner start " +
        // pageIndex.pageIndexInChapter);
        int[] startEnds = mDrwrap.getPageStartAndEndIndex(pageIndex);
        // printLog(" native synchnzed getPageStartAndEndIndexInner end " +
        // pageIndex.pageIndexInChapter);
        return new IndexRange(new ElementIndex(startEnds[0]), new ElementIndex(
                startEnds[1]));
    }

    @Override
    public String getText(Chapter chapter, ElementIndex startIndex,
                          ElementIndex endIndex) {

        // printLog(" native getText start " + startIndex);
        EPageIndex pageIndex = getEPageIndex(chapter, -1);// TODO getText不关心哪一页
        String text = mDrwrap.getText(pageIndex, startIndex.getIndex(),
                endIndex.getIndex());
        // printLog(" native getText end " + startIndex);
        return convertToSimplified(text);
    }

    @Override
    public ClickResult clickEvent(Chapter chapter, int pageIndexInChapter,
                                  Point point) {

        // printLog(" native clickEvent start " + pageIndexInChapter);
        EPageIndex pageIndex = getEPageIndex(chapter, pageIndexInChapter);
        EPoint pPoint = convertPaint(point);
        EResult result = mDrwrap.clickEvent(pageIndex, pPoint);
        // printLog(" native clickEvent end " + pageIndexInChapter);

        return convertClickResult(result);
    }

    protected ClickResult convertClickResult(EResult eResult) {

        ClickResult result = null;
        if (eResult instanceof EInnerGotoResult) {
            EInnerGotoResult eInnerResult = (EInnerGotoResult) eResult;

            InnerGotoClickResult gotoResult = new InnerGotoClickResult();
            gotoResult.setType(ClickType.Other);
            gotoResult.setGotoType(InnerGotoType.convert(eInnerResult
                    .getGotoType()));
            gotoResult.setAnchor(eInnerResult.getAnchorID());
            gotoResult.setHref(eInnerResult.getHref());
            gotoResult.setPageIndex(eInnerResult.getPageIndex());

            result = gotoResult;
            return result;
        }
        result = new ClickResult();
        ClickType clickType = ClickType.convert(eResult.getType());
        if (clickType.isPicNormal()) {
            ImageClickResult imgResult = new ImageClickResult();
            // imgResult.setImgPath(imgPath);
            imgResult.setImgPath(eResult.getStrURL());
            if (eResult.getImgRect() != null) {
                imgResult.setImgRect(convertRect(eResult.getImgRect())[0]);
            }
            imgResult.setImgBgColor(eResult.getImgBgColor());
            result = imgResult;
        } else if (clickType.isPicDesc()) {
            ImageClickResult imgResult = new ImageClickResult();
            // imgResult.setImgPath(imgPath);
            imgResult.setImgPath(eResult.getStrURL());
            imgResult.setImgDesc(eResult.getStrAlt());
            if (eResult.getImgRect() != null) {
                imgResult.setImgRect(convertRect(eResult.getImgRect())[0]);
            }
            imgResult.setImgBgColor(eResult.getImgBgColor());
            result = imgResult;
        } else if (clickType.isInnerNote()) {
            InnerLabelClickResult iLabelResult = new InnerLabelClickResult();
            iLabelResult.setLabelContent(eResult.getStrAlt());
            if (eResult.getImgRect() != null) {
                iLabelResult.setImgRect(convertRect(eResult.getImgRect())[0]);
            }
            result = iLabelResult;
        } else if (clickType.isPicFull()) {
            //全屏图

        } else if (clickType.isToBrowser()) {
            ToBrowserClickResult browserResult = new ToBrowserClickResult();
            browserResult.setUrl(eResult.getStrURL());
            result = browserResult;
        } else if (clickType.isAudio()) {
            AudioClickResult audioResult = new AudioClickResult();
            audioResult.setPath(eResult.getStrURL());
            if (eResult.getImgRect() != null) {
                audioResult.setImgRect(convertRect(eResult.getImgRect())[0]);
            }
            result = audioResult;
        } else if (clickType.isVideo()) {
            AudioClickResult audioResult = new AudioClickResult();
            audioResult.setPath(eResult.getStrURL());
            if (eResult.getImgRect() != null) {
                audioResult.setImgRect(convertRect(eResult.getImgRect())[0]);
            }
            result = audioResult;
        } else if (clickType.isPicSmall()) {
            //出血图，小图
        } else {
            clickType = ClickType.None;
        }
        result.setType(clickType);

        return result;
    }

    protected EPageIndex getEPageIndex(Chapter chapter, int pageIndexInChapter) {

        EPageIndex pageIndex = new EPageIndex();
        pageIndex.filePath = chapter.getPath();
        pageIndex.pageIndexInChapter = pageIndexInChapter - 1;// 上层逻辑是从1开始的，所以减1

        return pageIndex;
    }

    @Override
    protected void cancelParse() {
        mDrwrap.cancelParse();
    }

    @Override
    protected List<OneSearch> search(Chapter chapter, String word) {
        EPageIndex pageIndex = getEPageIndex(chapter, 0);
        SearchHandler searchCallback = new SearchHandler(word);
        mDrwrap.search(pageIndex, word, searchCallback);
        return searchCallback.getSearchs();
    }

    @Override
    protected void getParagraphTextInner(Chapter chapter, int elementIndex,
                                         boolean first, int maxLen, ParagraphTextHandler handler) {
        EPageIndex pageIndex = getEPageIndex(chapter, 0);
        mDrwrap.getParagraphText(pageIndex, elementIndex, first, maxLen, handler);
    }

    protected void afterOpenFile() {
        try {
            mReadInfo.setIsSupportConvert(mDrwrap.getEpubBookBig5EncodingSupport());
            mReadInfo.setIsSupportTTS(mDrwrap.getEpubBookTTSSupport());
            final boolean isUnSupport =!mReadInfo.isSupportConvert();
            if (isUnSupport){
                BaseJniWarp.setBig5Encoding(false);
            }else{
                BaseJniWarp.setBig5Encoding(ReadConfig.getConfig().getChineseConvert());
            }
        } catch (Exception e) {
            mReadInfo.setIsSupportConvert(false);
            mReadInfo.setIsSupportTTS(false);
            BaseJniWarp.setBig5Encoding(ReadConfig.getConfig().getChineseConvert());
        }
    }
}
