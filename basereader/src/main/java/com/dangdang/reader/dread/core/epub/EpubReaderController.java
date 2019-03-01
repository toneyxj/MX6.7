package com.dangdang.reader.dread.core.epub;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;

import com.dangdang.reader.R;
import com.dangdang.reader.cloud.CloudSyncConvert;
import com.dangdang.reader.cloud.MarkNoteManager;
import com.dangdang.reader.cloud.MarkNoteManager.OperateType;
import com.dangdang.reader.cloud.Status;
import com.dangdang.reader.db.service.DDStatisticsService;
import com.dangdang.reader.db.service.ShelfBookService;
import com.dangdang.reader.dread.BookNoteActivity;
import com.dangdang.reader.dread.GalleryViewActivity;
import com.dangdang.reader.dread.ReadActivity;
import com.dangdang.reader.dread.config.ReadConfig;
import com.dangdang.reader.dread.config.ReadConfig.PageTurnMode;
import com.dangdang.reader.dread.core.base.BasePageView;
import com.dangdang.reader.dread.core.base.BaseReaderApplicaion;
import com.dangdang.reader.dread.core.base.BaseReaderApplicaion.IReaderEventListener;
import com.dangdang.reader.dread.core.base.BaseReaderController;
import com.dangdang.reader.dread.core.base.BaseReaderWidget;
import com.dangdang.reader.dread.core.base.GoToParams;
import com.dangdang.reader.dread.core.base.IEpubPageView.DrawingType;
import com.dangdang.reader.dread.core.base.IReaderApplication;
import com.dangdang.reader.dread.core.base.IReaderWidget;
import com.dangdang.reader.dread.core.base.IReaderWidget.DrawPoint;
import com.dangdang.reader.dread.core.base.TextSelectionCursor;
import com.dangdang.reader.dread.core.epub.GlobalWindow.IDictOperation;
import com.dangdang.reader.dread.core.epub.GlobalWindow.IFloatingOperation;
import com.dangdang.reader.dread.core.epub.GlobalWindow.INoteWindowOperation;
import com.dangdang.reader.dread.core.epub.GlobalWindow.IReaderTextSearchOperation;
import com.dangdang.reader.dread.core.epub.GlobalWindow.IReaderTextSearchResultOperation;
import com.dangdang.reader.dread.core.epub.NoteHolder.NoteFlag;
import com.dangdang.reader.dread.core.epub.NoteHolder.NotePicRect;
import com.dangdang.reader.dread.data.BookNote;
import com.dangdang.reader.dread.data.OneSearch;
import com.dangdang.reader.dread.data.ParagraphText;
import com.dangdang.reader.dread.data.ReadInfo;
import com.dangdang.reader.dread.format.BaseBookManager.GotoPageCommand;
import com.dangdang.reader.dread.format.BaseBookManager.GotoPageResult;
import com.dangdang.reader.dread.format.BaseReadInfo;
import com.dangdang.reader.dread.format.Book;
import com.dangdang.reader.dread.format.Chapter;
import com.dangdang.reader.dread.format.IndexRange;
import com.dangdang.reader.dread.format.epub.ClickResult;
import com.dangdang.reader.dread.format.epub.ClickResult.InnerGotoClickResult;
import com.dangdang.reader.dread.format.epub.EpubBookManagerNew;
import com.dangdang.reader.dread.format.epub.IEpubBookManager;
import com.dangdang.reader.dread.format.txt.TxtChapter;
import com.dangdang.reader.dread.function.FunctionCode;
import com.dangdang.reader.dread.holder.ControllerHolder;
import com.dangdang.reader.dread.holder.MediaHolder;
import com.dangdang.reader.dread.holder.MediaHolder.MediaType;
import com.dangdang.reader.dread.holder.PromptResource;
import com.dangdang.reader.dread.holder.SearchDataHolder;
import com.dangdang.reader.dread.jni.BaseJniWarp;
import com.dangdang.reader.dread.jni.BaseJniWarp.ElementIndex;
import com.dangdang.reader.moxiUtils.NotificationWhat;
import com.dangdang.reader.personal.domain.ShelfBook;
import com.dangdang.reader.utils.NetUtils;
import com.dangdang.reader.utils.Utils;
import com.dangdang.reader.view.CommonDialog;
import com.dangdang.zframework.log.LogM;
import com.dangdang.zframework.utils.DRUiUtility;
import com.moxi.wechatshare.ShareDialog;
import com.mx.mxbase.constant.APPLog;
import com.mx.mxbase.utils.StartActivityUtils;
import com.mx.mxbase.view.AlertDialog;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author luxu
 */
public class EpubReaderController extends BaseReaderController {

    protected BaseReaderApplicaion mReaderApp;
    protected IReaderWidget mReaderWidget;
    protected IEpubBookManager mBookManager;
    protected BaseControllerWrapper mControllerWrapper;
    protected MarkNoteManager mMarkNoteManager;
    protected ControllerHolder mControllerHolder;
    protected MediaHolder mMediaHolder;

    protected GlobalWindow mGlobalWindow;

    protected int mReadWidth;
    protected int mReadHeight;
    protected float mDensity = 1f;

    protected Chapter mCurrentChapter;
    protected int mCurrentPageIndexInChapter = 1;

    protected IndexRange mCurrentPageRange;
    protected IndexRange mCurrentChapterRange;
    protected TextSelectionCursor mSelectCursor = TextSelectionCursor.None;
    protected Point mLeftPoint;
    protected Point mRightPoint;
    protected Point[] mPressPoints = new Point[2];

    protected boolean mIsMoveAfterLongPress = false;
    protected boolean mIsCanCross = true;
    protected String mSelectedText = "";
    protected ElementIndex mNoteStart = new ElementIndex();
    protected ElementIndex mNoteEnd = new ElementIndex();
    protected long lastSingleTapTime;
    protected ReadStatus mReadStatus = ReadStatus.Read;

    protected String mInnerMediaPath;

    protected static final int DOUBLE_TAP_TIMEOUT = ViewConfiguration
            .getDoubleTapTimeout();
    protected static final int MAX_NOTELEN = 1000;

    protected Context mContext;

    private int mNoteDrawLineColor = BookNote.NOTE_DRAWLINE_COLOR_RED;

    public EpubReaderController(Context context) {
        super();
        ttsHandler = new MyHandler(this);
        mDensity = DRUiUtility.getDensity();

        setReadArea();
        mContext = context.getApplicationContext();
        mControllerHolder = new ControllerHolder(mContext);
    }

    protected void setReadArea() {
        final ReadConfig readConfig = ReadConfig.getConfig();
        mReadWidth = readConfig.getReadWidth();
        mReadHeight = readConfig.getReadHeight();
    }

    @Override
    public void onSizeChange() {
        mControllerHolder.reInit(mContext);
        setReadArea();

        if (isShowingWindow()) {
            hideWindow();
        }
    }

    @Override
    public boolean canScroll(DPageIndex pageIndex) {
        final int chapterPageCount = getCurrentChapterPageCount();
        final int currentPageIndexInChapter = getCurrentPageIndexInChapter();
        final Chapter chapter = mCurrentChapter;

        LogM.i(getClass().getSimpleName(),
                "lxu canScroll 0 mCurrentHtml.path = " + chapter
                        + ", CurrentPageRange = " + getCurrentPageRange()
                        + ", CurrentPageIndexInChapter = "
                        + currentPageIndexInChapter + ", chapterPageCount = "
                        + chapterPageCount + ", pageIndex = " + pageIndex);
        if (chapter == null) {
            return false;
        }

        boolean canScroll = true;
        if (pageIndex == DPageIndex.Next) {
            /*
             * if(isLoadingPage){ return false; }
			 */
            final boolean isLastHtml = isLastChapter(chapter);
            if (isLastHtml) {
                if (isLastPageInChapter()) {
                    printLog(" last page in book ");
                    showToast(PromptResource.LastPagePrompt);
                }
                canScroll = currentPageIndexInChapter < chapterPageCount;
            }
        } else if (pageIndex == DPageIndex.Previous) {
            /*
             * if(mReComposing){ return false; }
			 */
            // TODO 如果当前是loadingPage 点击上一页 ？
            final boolean isFirstHtml = isFirstChapter(chapter);
            if (isFirstHtml) {
                if (isFirstPageInChapter()) {
                    printLog(" first page in book ");
                    showToast(PromptResource.FirstPagePrompt);
                }
                canScroll = currentPageIndexInChapter > 1;
            }
        }

        return canScroll;
    }

    public void setCanCross(boolean isCanCross) {
        mIsCanCross = isCanCross;
    }

    public int getChapterPageCount(Chapter chapter) {
        int pCount = 0;
        if (chapter != null) {
            // printLog(" current getChapterPageCount start " +
            // mCurrentChapter.hashCode());
            pCount = getCWrapper().getChapterPageCount(chapter);
            // printLog(" current getChapterPageCount end " +
            // mCurrentChapter.hashCode());
        }
        return pCount;
    }

    public int getCurrentChapterPageCount() {
        int pCount = 0;
        if (mCurrentChapter != null) {
            // printLog(" current getChapterPageCount start " +
            // mCurrentChapter.hashCode());
            pCount = getCWrapper().getChapterPageCount(mCurrentChapter);
            // printLog(" current getChapterPageCount end " +
            // mCurrentChapter.hashCode());
        }
        return pCount;
    }

    protected void showToast(int resid) {
        doFunction(FunctionCode.FCODE_SHOWTOAST, resid);
    }

    protected void showToast(String msg) {
        doFunction(FunctionCode.FCODE_SHOWTOAST, msg);
    }

    @Override
    public void onScrollingEnd(DPageIndex pageIndex) {
        ReadConfig.getConfig().setAddScroolIndex();
        printLog(" onScrollingEnd " + pageIndex);
        int currChapterPageCount = getCurrentChapterPageCount();
        final int firstPageInChapter = 1;
        int lastPageInChapter = currChapterPageCount;
        int pageIndexInChapter = getCurrentPageIndexInChapter();
        Chapter curChapter = getCurrentChapter();

        boolean isPrevChapter = false;
        boolean isNextChapter = false;
        switch (pageIndex) {
            case Current:
                break;
            case Previous:
                int prevPageIndexInChapter = pageIndexInChapter;
                isPrevChapter = (pageIndexInChapter <= 1);
                if (pageIndexInChapter > 1) {
                    prevPageIndexInChapter = isPrevChapter ? lastPageInChapter
                            : pageIndexInChapter - 1;
                } else {
                    printLog("lxu pageIndexInChapter !>1 ");
                }
                if (isPrevChapter) {
                    Chapter prevChapter = getRelativeChapter(pageIndex,
                            getCurrentChapter());
                    setCurrentChapter(prevChapter);
                    currChapterPageCount = getCurrentChapterPageCount();
                    lastPageInChapter = currChapterPageCount;
                    prevPageIndexInChapter = lastPageInChapter;
                }
                pageIndexInChapter = prevPageIndexInChapter;
                break;
            case Next:
                int nextPageIndexChapter = pageIndexInChapter;
                isNextChapter = (pageIndexInChapter >= currChapterPageCount);
                if (pageIndexInChapter < currChapterPageCount) {
                    nextPageIndexChapter = isNextChapter ? firstPageInChapter
                            : pageIndexInChapter + 1;
                } else {
                    printLog("lxu pageIndexInChapter !< currentChapterPageCount ");
                }
                if (isNextChapter) {
                    nextPageIndexChapter = firstPageInChapter;
                    Chapter nextChapter = getRelativeChapter(pageIndex,
                            getCurrentChapter());
                    setCurrentChapter(nextChapter);
                    currChapterPageCount = getCurrentChapterPageCount();// TODO ?
                }
                pageIndexInChapter = nextPageIndexChapter;
                break;
            default:
                break;
        }
        setCurrentPageIndexInChapter(pageIndexInChapter);

        Chapter currentChapter = getCurrentChapter();

        printLog("lxu <-- onScrollingEnd 0 --> pageIndex=" + pageIndex
                + ", currPi=" + pageIndexInChapter + ", currentChapterPCount="
                + currChapterPageCount + ", nextChapter = " + isNextChapter
                + ", prevChapter = " + isPrevChapter + ", getCurrentHtml() = "
                + currentChapter);

        if (currentChapter != null) {
            int tPageIndex = pageIndexInChapter;
            if (hasReadEndPage() && isLastPageInBook()) {
                tPageIndex = pageIndexInChapter - 1;
            }
            IndexRange pageRange = getPageRange(currentChapter, tPageIndex);
            if (pageRange != null) {
                setCurrentPageRange(pageRange);
                updateReadProgress(currentChapter,
                        pageRange.getStartIndexToInt());
            }
            initChapterIndexRange();
        }
        exitMediaMode(false);
        printLog("lxu <-- onScrollingEnd last ");

    }

    protected Chapter mPrevGetRangeChapter;

    protected void initChapterIndexRange() {
        if (!isCurrentPageCanOption()) {
            return;
        }
        Chapter chapter = getCurrentChapter();
        if (!chapter.equals(mPrevGetRangeChapter)) {
            IndexRange tmpRange = getChapterRange(getCurrentChapter());
            printLog(" initChapterIndexRange " + tmpRange);
            setCurrentChapterRange(tmpRange);
        }
    }

    /**
     * 是否有阅读完成页
     *
     * @return
     */
    protected boolean hasReadEndPage() {
        return getReadInfo().isDangEpub();// getReaderApp().isEpub();
    }

    protected boolean isBought() {
        return getReadInfo().isBought();
    }

    protected void setReadEndTime() {
        String readTimeInfo = getReadInfo().getReadTimeInfo();
        ShelfBookService shelfService = ShelfBookService.getInstance(getContext());
        long endTime = System.currentTimeMillis();
        if (Utils.isStringEmpty(readTimeInfo)) {
            ShelfBook info = shelfService
                    .getShelfBookById(getReadInfo().getDefaultPid());
            if (info != null) {
                readTimeInfo = info.getTotalTime();
            }
        }
        String info = CloudSyncConvert.writeStartEndTimeToReadTimeInfo(
                readTimeInfo, 0, endTime);
        getReadInfo().setReadTimeInfo(info);
        shelfService.updateBookReadTime(getReadInfo().getDefaultPid(), info);
    }

    protected void updateReadProgress(final Chapter chapter,
                                      final int elementIndex) {
        if (chapter == null) {
            return;
        }
        ReadInfo readInfo = getReadInfo();
        readInfo.setElementIndex(elementIndex);
        if (chapter != null && !chapter.equals(readInfo.getReadChapter())) {
            int chapterIndex = getChapterIndex(chapter);
            readInfo.setChapterIndex(chapterIndex, 3);
            readInfo.setReadChapter(chapter);
            // printLog(" updateReadProgress() chapterIndex = " + chapterIndex);
        }
        if (isLastPageInBook()) {
            // mReaderApp.getReaderActivity().addFinishReadStatistics();
            Context context = mReaderApp.getContext();
            if (context instanceof ReadActivity) {
                ((ReadActivity) context).addFinishReadStatistics();// TODO
                // ?
            }
            setReadEndTime();
        }
    }

	/*
     * @Override public void doPaint(PaintContext paintContext, DPageIndex
	 * pageIndex, Point start, Point end) {
	 * 
	 * }
	 */

    @Override
    public boolean onFingerPress(int x, int y) {

        printLog(" onFingerPress " + x + ", " + y);
        // 判断点击区域是否在 左右两个光标处
        int maxDistance = (int) (25 * mDensity);
        if (isMaxFont()) {
            maxDistance *= 2;
        }
        final TextSelectionCursor cursor = findSelectionCursor(x, y,
                maxDistance);
        if (cursor != TextSelectionCursor.None) {
            moveSelectionCursorTo(cursor, x, y);
            hideWindow();
        } else {
            if (isSelectedStatus()) {
                return true;
            }

            if (isShowingWindow()) {
                /*
				 * repaintSync(true, true); hideWindow();
				 */
            } else {
                startScrolling(x, y);
            }
        }

        return true;
    }

    private boolean isMaxFont() {
        final ReadConfig readConfig = ReadConfig.getConfig();
        int lineWordNum = readConfig.getLineWordNum();
        return (lineWordNum <= readConfig.getMinLineWord());

    }

    public TextSelectionCursor findSelectionCursor(int x, int y, int maxDistance) {
        if (mLeftPoint == null || mRightPoint == null)
            return TextSelectionCursor.None;

        float fontSize = getFontSize();
        Point left = new Point(mLeftPoint);
        left.y += fontSize / 2;
        Point right = new Point(mRightPoint);
        right.x += fontSize;
        right.y += fontSize / 2;

        int leftdistance = distanceToCursor(x, y, left);
        int rightdistance = distanceToCursor(x, y, right);

        TextSelectionCursor tmpCursor = TextSelectionCursor.None;
        if (rightdistance < leftdistance) {
            tmpCursor = rightdistance <= maxDistance ? TextSelectionCursor.Right
                    : TextSelectionCursor.None;
        } else {
            tmpCursor = leftdistance <= maxDistance ? TextSelectionCursor.Left
                    : TextSelectionCursor.None;
        }
        // printLog(" findSelectionCursor = " + tmpCursor + "," + leftdistance +
        // "-" + rightdistance);
		/*
		 * if(mNoteHolder.isCrossPage()){ if(!mNoteHolder.isDrawFirst() &&
		 * tmpCursor == TextSelectionCursor.Left){ tmpCursor =
		 * TextSelectionCursor.None; } else if(!mNoteHolder.isDrawSecond() &&
		 * tmpCursor == TextSelectionCursor.Right){ tmpCursor =
		 * TextSelectionCursor.None; } }
		 */
        return tmpCursor;
    }

    protected float getFontSize() {
        return ReadConfig.getConfig().getFontSize();
    }

    protected int distanceToCursor(int x, int y, Point cursorPoint) {
        if (cursorPoint == null) {
            return Integer.MAX_VALUE;
        }

        final int dX, dY;

        final int w = TextSelectionCursor.getWidth() / 2;
        if (x < cursorPoint.x - w) {
            dX = cursorPoint.x - w - x;
        } else if (x > cursorPoint.x + w) {
            dX = x - cursorPoint.x - w;
        } else {
            dX = 0;
        }

        final int h = TextSelectionCursor.getHeight();
        if (y < cursorPoint.y) {
            dY = cursorPoint.y - y;
        } else if (y > cursorPoint.y + h) {
            dY = y - cursorPoint.y - h;
        } else {
            dY = 0;
        }

        return Math.max(dX, dY);
    }

    protected void startScrolling(int x, int y) {
        mReaderWidget.startManualScrolling(x, y, DDirection.LeftToRight);
    }

    @Override
    public boolean onFingerMove(int x, int y) {

        // printLog(" onFingerMove " + x + ", " + y);
        if (isVideoShow()) {
            resetVedioView();
        }
        /**
         * 判断是否有笔记游标 处理跨页笔记
         */
        boolean moveRet = false;
        final TextSelectionCursor cursor = mSelectCursor;
        if (cursor != TextSelectionCursor.None) {
            boolean isCross = isHorizontalCross(x, y);// 处理跨页逻辑
            if (isCross) {
                crossPageNote(x, y, DrawingType.Shadow);
            } else {
                getCHolder().resetStartCrossTime();
                moveSelectionCursorTo(cursor, x, y);//
                Point pointLeft = new Point(mLeftPoint.x, mLeftPoint.y);
                Point pointRight = new Point(mRightPoint.x, mRightPoint.y);
                int result = compareTwoPoints(pointLeft, pointRight);
                if (result > 0) {
                    pointRight.x += 2;
                    pointLeft.x -= 2;
                } else if (result < 0) {
                    pointRight.x -= 2;
                    pointLeft.x += 2;
                }
                Rect[] rects = getCurrentPageRectsByPoint(pointLeft,
                        pointRight);
                if (Utils.isArrEmpty(rects)) {
                    return false;
                }
                boolean isDrawFirst = true;
                boolean isDrawEnd = true;
                if (getCHolder().isMaxCross()) {
                    isDrawFirst = !getCHolder().isForward();
                    isDrawEnd = !isDrawFirst;
                }
                doDrawing(DrawingType.Shadow, mLeftPoint, isDrawFirst,
                        mRightPoint, isDrawEnd, new Point(x, y), rects);
            }
            moveRet = false;
        } else {
            if (isSelectedStatus()) {
                return moveRet;
            }

            if (isShowingWindow()) {
                // hideWindow();
            } else {
                scrollManuallyTo(x, y);
                moveRet = true;
            }
        }
        return moveRet;
    }

    private int compareTwoPoints(Point point1, Point point2) {
        ElementIndex[] indexes = mBookManager.getSelectedStartAndEndIndex(mCurrentChapter, mCurrentPageIndexInChapter, point1, point2);
        return indexes[0].getIndex() - indexes[1].getIndex();
//        if (point1.y > point2.y)
//            return 1;
//        if (point1.y < point2.y)
//            return -1;
//        if (point1.x > point2.x)
//            return 1;
//        if (point1.x < point2.x)
//            return -1;
//        return 0;
    }

    protected void moveSelectionCursorTo(TextSelectionCursor cursor, int x,
                                         int y) {
        mSelectCursor = cursor;
        moveCursorCoord(cursor, x, y);
    }

    protected void moveCursorCoord(TextSelectionCursor cursor, int x, int y) {
        // printLog(" moveCursorCoord cursor = " + cursor + ", x = " + x +
        // ", y = " + y);
        Point point = new Point(x, y);
        Rect[] rects = getCurrentPageRectsByPoint(point, point);
        if (rects != null) {
            y = (rects[0].top + rects[0].bottom) / 2;
        }
        if (cursor == TextSelectionCursor.Left) {
            mLeftPoint.x = x;
            mLeftPoint.y = y;
        } else {
            mRightPoint.x = x;
            mRightPoint.y = y;
        }
    }

    protected void doDrawing(DPageIndex pageIndex, DrawingType type, Point start,
                             boolean isDrawStart, Point end, boolean isDrawEnd, Point current,
                             Rect[] rects) {

        DrawPoint pStart = new DrawPoint(isDrawStart, start);
        DrawPoint pEnd = new DrawPoint(isDrawEnd, end);
        DrawPoint pCurrent = new DrawPoint(true, current);
        mReaderWidget.doDrawing(pageIndex, type, pStart, pEnd, pCurrent, rects, mNoteDrawLineColor);
    }

    protected void doDrawing(DrawingType type, Point start, boolean isDrawStart,
                             Point end, boolean isDrawEnd, Point current, Rect[] rects) {

        doDrawing(DPageIndex.Current, type, start, isDrawStart, end, isDrawEnd,
                current, rects);
    }

    protected void drawFinish(DrawingType type, Point current, boolean isPrev,
                              boolean isNext) {
        DrawPoint pCurrent = new DrawPoint(false, current);
        getReaderWidget().drawFinish(type, pCurrent, isPrev, isNext);
    }

    protected void scrollManuallyTo(int x, int y) {
        mReaderWidget.scrollManuallyTo(x, y);
    }

    @Override
    public boolean onFingerRelease(int x, int y, boolean fast) {

        printLog(" onFingerRelease " + x + ", " + y + "," + fast);

        if (mSelectCursor != TextSelectionCursor.None) {
            resetSelectCursor();
            if (compareTwoPoints(mLeftPoint, mRightPoint) > 0) {
                Point tmp = mRightPoint;
                mRightPoint = mLeftPoint;
                mLeftPoint = tmp;
            }

            boolean isPrev = false;
            boolean isNext = false;
            if (getCHolder().isMaxCross()) {
                isPrev = !getCHolder().isForward();
                isNext = !isPrev;
            }
            drawFinish(DrawingType.Shadow, new Point(x, y), isPrev, isNext);

            ElementIndex[] indexes = getSelectedStartAndEndIndexes();
            ElementIndex st = indexes[0];
            ElementIndex ed = indexes[1];
            mNoteStart = ElementIndex.min(st, ed);// st > ed ? ed : st;
            mNoteEnd = ElementIndex.max(st, ed);// ed < st ? st : ed;
            mSelectedText = getSelectedText(mNoteStart, mNoteEnd);
            // printLogV(" mSelectedText = " + mSelectedText + " start = " +
            // start + ", end = " + end);
            showFloatWindow(x, y, true, true, isShowCorrectView(), -1);
        } else {
            if (isSelectedStatus()) {
                repaintSync(true, true);
            }
            resetLeftAndRightPoint();
            if (isShowingWindow()) {
                hideWindow();
            } else {
                scrollRelease(x, y, fast);
            }
        }
        getCHolder().resetStartCrossTime();
        return true;
    }

    protected ElementIndex[] getSelectedStartAndEndIndexes() {
        int pageIndexInChapter = getCurrentPageIndexInChapter();
        if (getCHolder().isMaxCross()) {
            pageIndexInChapter = getCHolder().getEndPageIndexInChapter();
            // printLog(" getSelectedStartAndEndIndexes currI: " +
            // getCurrentPageIndexInChapter() + ", cEnd: " +
            // pageIndexInChapter);
        }

        final Point[] sePoints = calcStartAndEndPoint();
        final Point start = sePoints[0];
        final Point end = sePoints[1];
        start.x += 2;
        end.x -= 2;
        ElementIndex[] indexes = getSelectedIndex(start, end,
                pageIndexInChapter);

        // ElementIndex[] pis = getCHolder().getPressEmtIndexes();
		/*
		 * printLog(" getSelectedStartAndEndIndexes start release[" + indexes[0]
		 * + "-" + indexes[1] + "], press[" + pis[0] + "-" + pis[1] + "]," +
		 * getCHolder().isMaxCross());
		 */

        if (getCHolder().isMaxCross()) {// TODO cross page release
            if (getCHolder().isForward()) {
                indexes[1] = ElementIndex.max(indexes[0], indexes[1]);// Math.max(indexes[0],
                // indexes[1]);
            } else {
                indexes[1] = ElementIndex.min(indexes[0], indexes[1]);// Math.min(indexes[0].getElementIndex(),
                // indexes[1].getElementIndex());
            }
            indexes[0] = getPressEmtIndex();
        }
		/*
		 * printLogE(" getSelectedStartAndEndIndexes end [" + indexes[0] + "-" +
		 * indexes[1] + "]");
		 */
        return indexes;
    }

    protected boolean scrollRelease(final int x, final int y, boolean fast) {
        return getReaderWidget().scrollRelease(x, y, fast);
    }

    protected void resetSelectCursor() {
        mSelectCursor = TextSelectionCursor.None;
    }

    @Override
    public void cancelOption(boolean repaint) {
        if (isShowingWindow()) {
            hideWindow();
        }
        if (repaint) {
            if (isSelectedStatus()) {
                clearSelected();
            } else {
                repaintSync(false, false);
            }
        }
    }

    @Override
    public boolean onFingerSingleTap(int x, int y, long time) {

        APPLog.e("点击图片哟！！！！进行了隐藏修改" + isShowingWindow());
//         clickZoneToTurnPage(x, y);
        if (isShowingWindow()) {
            SearchDataHolder.getHolder().resetCurrent();
            checkStopSearch();
            clearSelected();
            hideWindow();
        } else {
            handleSingleTap(x, y, time);
        }
        return true;
    }

    protected void handleSingleTap(int x, int y, long time) {
        if (!isCurrentPageCanOption()) {
            return;
        }
        try {
            APPLog.e("clickEvent","进入了这里");
            ClickResult cResult = clickEvent(x, y);

            if (cResult.isClick()) {
                long last = lastSingleTapTime;
                lastSingleTapTime = time;
                if (time - last < DOUBLE_TAP_TIMEOUT) {
                    return;
                }
                ClickResult.ImageClickResult imgClick = (ClickResult.ImageClickResult) cResult;
                openImage(imgClick.getImgPath(), imgClick.getImgDesc(),
                        imgClick.getImgRect(), imgClick.getImgBgColor());
                return;
            } else if (cResult.isInnerNote()) {
                ClickResult.InnerLabelClickResult innerClick = (ClickResult.InnerLabelClickResult) cResult;
                handleInnerNote(innerClick.getImgRect(),
                        innerClick.getLabelContent());
                return;
            } else if (cResult.isOther()) {
                InnerGotoClickResult gotoClick = (InnerGotoClickResult) cResult;
                if (gotoClick.isBookInner()) {
                    handleInnerGoto(gotoClick);
                    return;
                } else if (gotoClick.isToBrowser()) {
                    if (!TextUtils.isEmpty(gotoClick.getHref())) {
                        startBrowser(gotoClick.getHref());
                        return;
                    }
                }
            } else if (cResult.isAudio()) {
                ClickResult.AudioClickResult audioResult = (ClickResult.AudioClickResult) cResult;
                final String innerPath = audioResult.getPath();
                if (TextUtils.isEmpty(innerPath)) {
                    showToast(R.string.data_error);
                    return;
                }

                if (!TextUtils.isEmpty(mInnerMediaPath)
                        && !innerPath.equals(mInnerMediaPath)) {
                    exitMediaMode(true);
                }
                mInnerMediaPath = innerPath;
                String localPath = getLocalPath(innerPath);
                printLog(" audip innerpath = " + innerPath + "; localpath = "
                        + localPath);
                showAudio(x, y, audioResult.getImgRect(), innerPath, localPath);
                return;
            } else if (cResult.isVideo()) {
                ClickResult.AudioClickResult audioResult = (ClickResult.AudioClickResult) cResult;
                final String innerPath = audioResult.getPath();
                if (TextUtils.isEmpty(innerPath)) {
                    showToast(R.string.data_error);
                    return;
                }
                if (!TextUtils.isEmpty(mInnerMediaPath)
                        && !innerPath.equals(mInnerMediaPath)) {
                    exitMediaMode(true);
                }
                mInnerMediaPath = innerPath;
                String localPath = getLocalPath(innerPath);
                printLog(" Videoip innerpath = " + innerPath + "; localpath = "
                        + localPath);
                EpubPageView currentView = (EpubPageView) ((BaseReaderWidget) getReaderWidget())
                        .getCurrentView();
                currentView.playVideo(audioResult.getImgRect(), innerPath,
                        localPath, getReadInfo().getEBookType());
                return;
            }

		/*
		 * else if (cResult.isToBrowser()) { ToBrowserClickResult browserClick =
		 * (ToBrowserClickResult) cResult; if
		 * (!TextUtils.isEmpty(browserClick.getUrl())) {
		 * startBrowser(browserClick.getUrl()); return; } }
		 */

            if ((mReadWidth - x) < (55 * mDensity) && (y < 50 * mDensity)) {
                handleBookmark();
            } else if (!handleBookNote(x, y, cResult)) {
                if (time - lastSingleTapTime < DOUBLE_TAP_TIMEOUT) {
                    return;
                }
                if (isVideoShow()) {
                    resetVedioView();
                }
                doFunction(getClickFunction(x, y));
                exitMediaMode(false);
            }
        } catch (NoSuchMethodError e) {
            APPLog.e("NoSuchMethodError",e.getMessage());
            if (time - lastSingleTapTime < DOUBLE_TAP_TIMEOUT) {
                return;
            }
            if (isVideoShow()) {
                resetVedioView();
            }
            doFunction(getClickFunction(x, y));
            exitMediaMode(false);
        }
    }

    protected void exitMediaMode(boolean bStopAudio) {
        hideAudio();
        if (bStopAudio) {
            mInnerMediaPath = null;
            stopAudio();
        }
        if (mMediaHolder != null) {
            mMediaHolder.deleteLast();
        }
    }

    protected String getLocalPath(final String innerPath) {
        if (mMediaHolder == null) {
            mMediaHolder = new MediaHolder();
        }
        String localPath = mMediaHolder.getMediaPath(MediaType.Audio,
                innerPath, getReadInfo().getBookFile());
        return localPath;
    }

    protected void startBrowser(String url) {
        doFunction(FunctionCode.FCODE_TOBROWSER, url);
    }

    protected void handleInnerGoto(InnerGotoClickResult gotoClick) {
        Chapter chapter = null;
        if (TextUtils.isEmpty(gotoClick.getHref())) {
            chapter = getCurrentChapter();
        } else {
            chapter = getBook().getChapterByPath(gotoClick.getHref());
        }
        if (chapter != null) {
            GoToParams goParams = new GoToParams();
            goParams.setType(GoToType.Anchor);
            goParams.setChapter(chapter);
            goParams.setAnchor(gotoClick.getAnchor());

            doFunction(FunctionCode.FCODE_GOTO_PAGECHAPTER, goParams);
        } else {
            printLogE(" InnerGoto href not exist ");
        }
    }

    protected void openImage(String imgUrl, String imgDesc, Rect rect, int imgColor) {
        final Intent intent = new Intent();
        intent.setClass(getContext(), GalleryViewActivity.class);
        intent.putExtra(GalleryViewActivity.KEY_GALLERY_URLS,
                new String[]{imgUrl});
        intent.putExtra(GalleryViewActivity.KEY_GALLERY_RECT, rect);
        intent.putExtra(GalleryViewActivity.KEY_IMGDESC, new String[]{imgDesc});
        intent.putExtra(GalleryViewActivity.KEY_GALLERY_PAGEINDEX, 0);
        intent.putExtra(GalleryViewActivity.KEY_GALLERY_ID, -1);
        intent.putExtra(GalleryViewActivity.KEY_IMGBGCOLOR, imgColor);
        intent.putExtra(GalleryViewActivity.KEY_LANDSCAPE, ((EpubBookManagerNew) mBookManager).isLandScape());
        getContext().startActivity(intent);
    }

    protected void handleBookmark() {
        if (!isCurrPageSupportMark()) {
            return;
        }

        int chapterIndex = getChapterIndex(getCurrentChapter());
        IndexRange pageRange = getCurrentPageRange();
        int startIndex = pageRange.getStartIndexToInt();
        int endIndex = pageRange.getEndIndexToInt();
        boolean bookmarkExist = mMarkNoteManager.checkMarkExist(getReadInfo()
                        .getDefaultPid(), getReadInfo().getEpubModVersion(), getReadInfo().isBoughtToInt(), chapterIndex,
                startIndex, endIndex);
        DDStatisticsService mDDService = DDStatisticsService
                .getDDStatisticsService(getContext());
        if (bookmarkExist) {
            getReaderApp().doFunction(FunctionCode.FCODE_REMOVE_MARK);
            mDDService.addData(DDStatisticsService.DEL_LABLE_IN_READING,
                    DDStatisticsService.ReferType, "rightPage",
                    DDStatisticsService.OPerateTime, System.currentTimeMillis()
                            + "");
        } else {
            getReaderApp().doFunction(FunctionCode.FCODE_ADD_MARK);
            mDDService.addData(DDStatisticsService.ADD_LABLE_IN_READING,
                    DDStatisticsService.ReferType, "rightPage",
                    DDStatisticsService.OPerateTime, System.currentTimeMillis()
                            + "");
        }
    }

    protected boolean isCurrPageSupportMark() {
        return !(getReadInfo().isDangEpub() && isLastPageInBook());// getReaderApp().isEpub()
    }

    public final static int CLICK_FLAG_NOTE = 0;
    public final static int CLICK_FLAG_NOTE_BUTTON = 1;

    protected BookNote mCurrentOptionBookNote;

    protected boolean handleBookNote(int x, int y, ClickResult cResult) {

        List<BookNote> notes = getCurrentPageBookNotes();
        if (notes == null || notes.size() == 0) {
            return false;
        }
        ElementIndex clickElementIndex = getSelectedIndex(x, y);
        NotePicRect noteFlag = getNoteFlag(x, y);

        int clickFlag = -1;
        for (int i = 0, len = notes.size(); i < len; i++) {
            BookNote note = notes.get(i);

            if (noteFlag != null && isTargetNote(noteFlag.getFlag(), note)) {
                mCurrentOptionBookNote = note;
                mSelectedText = note.getSourceText();
                clickFlag = CLICK_FLAG_NOTE_BUTTON;
                break;
            } else if (clickElementIndex.getIndex() >= note.noteStart
                    && clickElementIndex.getIndex() <= note.noteEnd) {
                mCurrentOptionBookNote = note;
                mSelectedText = note.getSourceText();
                clickFlag = CLICK_FLAG_NOTE;
                break;
            }
        }

        if (clickFlag == CLICK_FLAG_NOTE) {
            //可能又是图
            if (cResult.isPicFull() || cResult.isPicSmall()) {
                clickFlag = -1;
            } else {
                showFloatWindow(x, y, false, false, isShowCorrectView(), mCurrentOptionBookNote.getDrawLineColor());
            }
        } else if (clickFlag == CLICK_FLAG_NOTE_BUTTON) {
            showNoteWindow(noteFlag.getRect(),
                    mCurrentOptionBookNote.getNoteText());
        }

        return clickFlag != -1;
    }

    protected void handleInnerNote(Rect rect, String content) {
        showInnerNoteWindow(rect, content);
    }

    protected NotePicRect getNoteFlag(int x, int y) {
        int chapterIndex = getChapterIndex(getCurrentChapter());
        int pageIndexInChapter = getCurrentPageIndexInChapter();
        NotePicRect noteFlag = NoteHolder.getHolder().isClickNoteBitmap(
                chapterIndex, pageIndexInChapter, x, y);
        return noteFlag;
    }

    protected boolean isTargetNote(NoteFlag flag, BookNote note) {

        return flag.getChapterIndex() == note.getChapterIndex()
                && flag.getStartIndex() == note.getNoteStart()
                && flag.getEndIndex() == note.getNoteEnd();
    }

    protected void clearSelected() {
        resetLeftAndRightPoint();
        resetSelectCursor();
        // resetUI();
        // repaintUI();
        repaintSync(true, true);
    }

    @Override
    public boolean onFingerLongPress(int x, int y) {

        APPLog.e("WrapClass-onFingerLongPress " + x + ", " + y);
        if (!isCurrentPageCanOption()) {
            return false;
        }

        final TextSelectionCursor cursor = mSelectCursor;
        if (cursor != TextSelectionCursor.None) {
            return false;
        }
        hideWindow();
        // 218,475
        Rect[] rects = getCurrentPageRectsByPoint(new Point(x, y), new Point(x,
                y));
        if (Utils.isArrEmpty(rects)) {
            return false;
        }

        Rect first = rects[0];
        Rect last = rects[rects.length - 1];
        mLeftPoint = new Point(first.left, (first.top + first.bottom) / 2);
        mRightPoint = new Point(last.right, (last.top + last.bottom) / 2);
        mPressPoints[0] = mLeftPoint;
        mPressPoints[1] = mRightPoint;
        doDrawing(DrawingType.Shadow, mLeftPoint, true, mRightPoint, true,
                new Point(x, y), rects);

        getCHolder().resetNoteRecord();
        ElementIndex[] pressEmtIndexes = getSelectedIndex(mLeftPoint,
                mRightPoint, getCurrentPageIndexInChapter());
        getCHolder().setPressEmtIndexes(pressEmtIndexes);
        getCHolder().setStartPageIndexInChapter(getCurrentPageIndexInChapter());

        mIsMoveAfterLongPress = false;
        callBackLongPress(x, y);

        return true;
    }

    @Override
    public boolean onFingerMoveAfterLongPress(int x, int y) {
        boolean isCross = false;
        if (mIsCanCross) {
            isCross = isHorizontalCross(x, y);// 处理跨页逻辑
        }
        printLog(" testcrosspage onFingerMoveAfterLongPress " + x + ", " + y
                + ", left = " + mLeftPoint + ", right = " + mRightPoint + ", "
                + isCross + "," + getCurrentPageIndexInChapter());
        if (isCross) {
            crossPageNote(x, y, DrawingType.Line);
        } else {
            getCHolder().resetStartCrossTime();
            TextSelectionCursor cursor = TextSelectionCursor.Right;
            if (getCHolder().isMaxCross() && !getCHolder().isForward()) {
                cursor = TextSelectionCursor.Left;
            }
            moveSelectionCursorTo(cursor, x, y);

            Point[] startEnds = calcStartAndEndPoint();
            startEnds[0].x += 1;
            startEnds[1].x -= 1;
			/*
			 * if(isMoveForward(mLeftPoint, new Point(x, y))){ startEnds[0] =
			 * mPressPoints[0]; } else { startEnds[1] = mPressPoints[1]; }
			 */
            Rect[] rects = getCurrentPageRectsByPoint(startEnds[0],
                    startEnds[1]);
            if (Utils.isArrEmpty(rects)) {
                return false;
            }
            boolean isDrawFirst = true;
            boolean isDrawEnd = true;
            doDrawing(DrawingType.Line, startEnds[0], isDrawFirst,
                    startEnds[1], isDrawEnd, new Point(x, y), rects);
        }

        mIsMoveAfterLongPress = true;
        // updateCursorPoint(rects);

        return true;
    }

    protected boolean isHorizontalCross(int x, int y) {
        return getCHolder().isHorizontalCross(x, y);
    }

    protected boolean isMoveForward(Point start, Point end) {
        boolean isMForward = true;
        if (start.y < end.y) {
            isMForward = true;
        } else if (start.y == end.y) {
            isMForward = start.x < end.x;
        } else {
            isMForward = false;
        }
        return isMForward;
    }

    protected void crossPageNote(int x, int y, DrawingType type) {
        final long nowTime = System.currentTimeMillis();
        getCHolder().initStartCrossTime(nowTime);
        if (!getCHolder().canCrossPage(nowTime)) {
            return;
        }

        final String functionCode = getClickFunction(x, y);
        final int currentPageIndexInChapter = getCurrentPageIndexInChapter();
        final boolean isForward = FunctionCode.FCODE_TURNPAGE_FORWARD
                .equals(functionCode);
        final boolean isCrossChapter = isCrossChapter(isForward);
        final boolean maxCross = getCHolder().isMaxCross();
        if (!isCrossChapter && !maxCross) {

            exitMediaMode(false);

            int tmpEnd = isForward ? currentPageIndexInChapter + 1
                    : currentPageIndexInChapter - 1;
            getCHolder().setEndPageIndexInChapter(tmpEnd);

            DPageIndex pageIndex = isForward ? DPageIndex.Next
                    : DPageIndex.Previous;

            Point start = new Point(1, 1);
            Point end = new Point(1, 1);
            boolean isDrawStart = !isForward;
            boolean isDrawEnd = isForward;
            Point current = new Point(x, y);
            int[][] seCoords = getCHolder().getStartAndEndCoords();
            if (isForward) {
                mSelectCursor = TextSelectionCursor.Right;
                start = new Point(seCoords[0][0], seCoords[0][1]);
                end = new Point(x, y);
                moveCursorCoord(TextSelectionCursor.Left, start.x, start.y);
            } else {
                mSelectCursor = TextSelectionCursor.Left;
                start = new Point(x, y);
                end = new Point(seCoords[1][0], seCoords[1][1]);
                moveCursorCoord(TextSelectionCursor.Right, end.x, end.y);
            }

            Rect[] rects = getSelectedRectsByPoint(getCurrentChapter(), tmpEnd,
                    start, end);
            doDrawing(pageIndex, type, start, isDrawStart, end, isDrawEnd,
                    current, rects);

            // printLog(" onFingerMoveAfterLongPress cross " + functionCode +
            // ", isDrawStart=" + isDrawStart + ",isDrawEnd=" + isDrawEnd);
            doFunction(functionCode, x, y, true);
        } else {
            // printLog(" onFingerMoveAfterLongPress CrossChapter=" +
            // isCrossChapter + ",maxCross=" + maxCross);
            TextSelectionCursor cursor = TextSelectionCursor.Right;
            if (getCHolder().isMaxCross() && !getCHolder().isForward()) {
                cursor = TextSelectionCursor.Left;
            }
            moveSelectionCursorTo(cursor, x, y);
        }
    }

    protected boolean isCrossChapter(final boolean isForward) {
        return isForward ? (isLastPageInChapter() || isLastPageInBook(DPageIndex.Next))
                : isFirstPageInChapter();
    }

    /**
     * 计算获取Rect[]的开始点和结束点
     *
     * @return [0]为开始点，[1]为结束点
     */
    protected Point[] calcStartAndEndPoint() {
        Point start = mLeftPoint;
        Point end = mRightPoint;
        if (mLeftPoint.y > mRightPoint.y) {
            start = mRightPoint;
            end = mLeftPoint;
        } else if (mLeftPoint.y == mRightPoint.y) {
            start = mLeftPoint.x < mRightPoint.x ? mLeftPoint : mRightPoint;
            end = mLeftPoint.x < mRightPoint.x ? mRightPoint : mLeftPoint;
        }
        return new Point[]{new Point(start.x, start.y), new Point(end.x, end.y)};
    }

    protected void updateCursorPoint(Rect[] rects) {

        Rect first = rects[0];
        Rect last = rects[rects.length - 1];
        mLeftPoint.x = first.left;
        mLeftPoint.y = first.top;

        mRightPoint.x = last.right;
        mRightPoint.y = last.top;
    }

    @Override
    public boolean onFingerReleaseAfterLongPress(int x, int y) {

        printLog(" onFingerReleaseAfterLongPress " + x + ", " + y);
        // resetSelectCursor();

		/*
		 * Point[] sePoints = calcStartAndEndPoint(); Point start = sePoints[0];
		 * Point end = sePoints[1];
		 * 
		 * int[] indexes = getSelectedIndex(start, end);
		 * if(getCHolder().isMaxCross()){//TODO cross page release indexes[0] =
		 * getPressEmtIndex(); }
		 */

        ElementIndex[] indexes = getSelectedStartAndEndIndexes();
        mNoteStart = indexes[0];
        mNoteEnd = indexes[1];
        mSelectedText = getSelectedText(indexes[0], indexes[1]);
        // printLogV(" mSelectedText = " + mSelectedText);

        if (mIsMoveAfterLongPress) {
            // printLog(" onFingerReleaseAfterLongPress[" + indexes[0] + ", " +
            // indexes[1] + "], " + mSelectedText);

            onAddNote(indexes[0], indexes[1], mSelectedText, "");

            repaintSync(true, true);
            // resetUI();//
            // repaintUI();
            resetLeftAndRightPoint();
            resetSelectCursor();
        } else {
            // show option menu
            showFloatWindow(x, y, true, true, isShowCorrectView(), -1);
        }

        boolean isPrev = false;
        boolean isNext = false;
        if (getCHolder().isMaxCross()) {
            isPrev = !getCHolder().isForward();
            isNext = !isPrev;
        }
        drawFinish(DrawingType.Line, new Point(x, y), isPrev, isNext);
        getCHolder().resetStartCrossTime();

        return true;
    }

    protected ElementIndex getPressEmtIndex() {
        ElementIndex pressEmtIndex = new ElementIndex();
        ElementIndex[] pressEmts = getCHolder().getPressEmtIndexes();
        if (getCHolder().isForward()) {
            pressEmtIndex = pressEmts[0];
        } else {
            pressEmtIndex = pressEmts[1];
        }
        return pressEmtIndex;
    }

    /**
     * @param x
     * @param y
     * @param drawLineOrDelete true(draw line), false(delete)
     */
    protected void showFloatWindow(int x, int y, boolean drawLineOrDelete,
                                   boolean showDict, boolean isShowCorrect, int drawLineColor) {

        int minY = y;// Math.min(mLeftPoint.y, mRightPoint.y);
        if (mLeftPoint != null && mRightPoint != null) {
            minY = Math.min(mLeftPoint.y, mRightPoint.y);
        }
        int maxY = y;
        if (mLeftPoint != null && mRightPoint != null) {
            maxY = Math.max(mLeftPoint.y, mRightPoint.y);
        }
        final int fontSize = (int) getFontSize();
        minY -= fontSize;
        minY = minY < 0 ? 0 : minY;
        maxY += fontSize;
        maxY = maxY > mReadHeight ? mReadHeight : maxY;
        getGWindow().showFloatingWindow(x, y, minY, maxY, drawLineOrDelete,
                showDict, isShowCorrect, drawLineColor);
    }

    protected boolean isShowCorrectView() {
        return getReadInfo().isDangEpub();
    }

    protected void showReaderTextSearchWindow() {
        getGWindow().showReaderTextSearchWindow();
    }

    protected void showReaderTextSearchRerultWindow(String word,
                                                    boolean resetData, boolean isFullScreen, boolean resetEditText) {
        getGWindow().showReaderTextSearchResultWindow(word, resetData,
                isFullScreen, resetEditText);
    }

    protected void showReaderTextSearchRerultWindow(String word, boolean resetData) {
        getGWindow().showReaderTextSearchResultWindow(word, resetData);
    }

    protected void hideReaderTextSearchRerultWindow() {
        getGWindow().hideReaderTextSearchResultWindow();
    }

    protected void hideWindow() {
        getGWindow().hideWindow(true);
    }

    protected void hideWindowExceptFloatWindow() {
        getGWindow().hideWindow(false);
    }

    protected void showNoteWindow(Rect rect, String content) {
        int x = (rect.right - rect.left) / 3 + rect.left;
        int y = (rect.top + rect.bottom) / 2;
        getGWindow().showNoteWindow(x, y, content, GlobalWindow.NW_FLAG_NOTE);
    }

    protected void showInnerNoteWindow(Rect rect, String content) {
        int x = (rect.left + rect.right) / 2;
        getGWindow().showNoteWindow(x, rect.bottom, content,
                GlobalWindow.NW_FLAG_INNERNOTE);
    }

    protected void showAudio(int x, int y, Rect imgRect, String innerPath,
                             String localPath) {
        getGWindow().showAudio(x, y, imgRect, innerPath, localPath,
                getReadInfo().getEBookType());
    }

    protected void hideAudio() {
        getGWindow().hideAudio();
    }

    protected void stopAudio() {
        getGWindow().stopAudio();
    }

	/*
	 * protected void startAnimtedScrolling(int x, int y) {
	 * getReaderWidget().startAnimatedScrolling(x, y, 1, false); }
	 */

    protected void resetUI() {
        mReaderWidget.reset();
    }

    protected void repaintUI() {
        mReaderWidget.repaint();
    }

    protected void repaintSync(boolean prevPaint, boolean nextPaint) {
        mReaderWidget.repaintSync(prevPaint, nextPaint);
    }

    protected boolean isShowingWindow() {
        return getGWindow().isShowingWindow();// TODO
    }

    public void resetLeftAndRightPoint() {
        mLeftPoint = null;
        mRightPoint = null;
    }

    public boolean isSelectedStatus() {
        return mLeftPoint != null || mRightPoint != null;
    }

    @Override
    public ReadStatus getReadStatus() {
        return mReadStatus;
    }

	/*
	 * protected void clickZoneToTurnPage(int x, int y) { String actionId =
	 * getPageTurnForwardStr(x, y); if (actionId != null) { doFunction(actionId,
	 * x, y); //如果当前还处于选中状态，去掉 //clearCursorPoint(); }
	 * //mReaderApp.getViewWidget().getmFloatingWindow().dismissPopupWindow(); }
	 */

    /**
     * 根据x，y坐标判断，当前单击，页面往哪个方向跳转（目前在 屏幕左侧 三分之一跳转到前一页，屏幕右侧三分之一 跳转到 下一页）
     *
     * @param x
     * @param y
     * @return
     */
	/*
	 * protected boolean isClickInMenuZone(int x, int y) {
	 * 
	 * int width = mScreenWidth / 3; int height = mScreenHeight / 3; int centerX
	 * = mScreenWidth / 2; int centerY = mScreenHeight / 2;
	 * 
	 * int minX = centerX - width / 2; int maxX = centerX + width / 2; int minY
	 * = centerY - height / 2; int maxY = centerY + height / 2;
	 * 
	 * return x > minX && x < maxX && y > minY && y < maxY; }
	 */
    protected String getClickFunction(int x, int y) {
        if (PageTurnMode.isSingleHanded(ReadConfig.getConfig()
                .getPageTurnMode())) {
            return switchPageTurnModeSingleHanded(x, y);
        } else {
            return switchPageTurnModeDefault(x);
        }
    }

    protected String switchPageTurnModeSingleHanded(int x, int y) {
        if (y >= (mReadHeight * 2 / 3)) {
            return FunctionCode.FCODE_TURNPAGE_FORWARD;
        }
        return switchPageTurnModeDefault(x);
    }

    protected String switchPageTurnModeDefault(int x) {
        if (x <= mReadWidth / 3) {
            return FunctionCode.FCODE_TURNPAGE_BACK;
        } else if (x >= (mReadWidth * 2) / 3) {
            return FunctionCode.FCODE_TURNPAGE_FORWARD;
        } else {
            return FunctionCode.FCODE_OPERATIONMENU;
        }
    }

    @Override
    public boolean onFingerDoubleTap(int x, int y) {

        printLog(" onFingerDoubleTap " + x + ", " + y);

        return true;
    }

    protected BookNote onAddNote(ElementIndex startIndex, ElementIndex endIndex,
                                 String selectedText, String note) {
        int chapterIndex = getChapterIndex(getCurrentChapter());
        IReaderEventListener l = getReaderEventListener();
        if (l == null) {
            printLogE(" onAddNote l == null ");
            return null;
        }

        int tmpStart = ElementIndex.min(startIndex, endIndex).getIndex();// startIndex
        // <
        // endIndex
        // ?
        // startIndex
        // :
        // endIndex;
        int tmpEnd = ElementIndex.max(startIndex, endIndex).getIndex();// endIndex
        // >
        // startIndex
        // ?
        // endIndex
        // :
        // startIndex;
        return l.addNote(chapterIndex, tmpStart, tmpEnd, selectedText, note, mNoteDrawLineColor);
    }

    protected IReaderEventListener getReaderEventListener() {
        return getReaderApp().getReaderEventListener();
    }

    protected void callBackLongPress(int x, int y) {
        if (getReaderEventListener() != null) {
            getReaderEventListener().onLongPressEvent(x, y);
        } else {
            printLogE(" callBackLongPress l == null ");
        }
    }

    protected void onUpdateNote(BookNote bookNote) {
        long nowTime = new Date().getTime();
        bookNote.setNoteTime(nowTime);
        bookNote.setModifyTime(String.valueOf(nowTime));
        bookNote.setStatus(String.valueOf(Status.COLUMN_UPDATE));
        bookNote.setCloudStatus(String.valueOf(Status.CLOUD_NO));
        mMarkNoteManager.operationBookNote(bookNote, OperateType.UPDATE);
    }

    protected void onDeleteNote(BookNote bookNote) {
        long nowTime = new Date().getTime();
        bookNote.setNoteTime(nowTime);
        bookNote.setModifyTime(String.valueOf(nowTime));
        bookNote.setStatus(String.valueOf(Status.COLUMN_DELETE));
        bookNote.setCloudStatus(String.valueOf(Status.CLOUD_NO));
        mMarkNoteManager.operationBookNote(bookNote, OperateType.DELETE);
    }

    @Override
    public void gotoPage(final GoToParams params) {
        final Chapter chapter = params.getChapter();
        if (chapter == null) {
            LogM.e(getClass().getSimpleName(), " gotoPage chapter is null ");
            return;
        }

        GoToType gType = params.getType();
//        int pageIndexInChapter = getPageIndexInChapter(chapter, params.getElementIndex());
        if (gType == GoToType.Anchor && TextUtils.isEmpty(params.getAnchor())) {
            gType = GoToType.ElementIndex;
        }
        if (gType == GoToType.ElementIndex) {
            if (isCurrentEquals(chapter, params.getElementIndex())) {
                printLog(" gotoPage the same page [params]");
                return;
            }
        }
        if (gType == GoToType.Anchor) {
//            pageIndexInChapter = mBookManager.getPageIndexInHtmlByAnchor(chapter, params.getAnchor());
        }

        reset();
        resetUI();
        repaintUI();
        setCurrentChapter(null);

        int type = GoToType.convertInt(gType);
        GotoPageCommand gotoCommand = new GotoPageCommand();
        gotoCommand.setAnchor(params.getAnchor());
        gotoCommand.setElementIndex(params.getElementIndex());
        gotoCommand.setChapter(chapter);
        gotoCommand.setType(type);

        getCWrapper().asynGotoPage(gotoCommand, gotoPageListener);
    }

    @Override
    public void gotoPage(final Chapter chapter, final int elementIndex) {
        if (chapter == null) {
            LogM.e(getClass().getSimpleName(), " gotoPage chapter is null ");
            return;
        }

        final int pageIndexInChapter = getPageIndexInChapter(chapter, elementIndex);
        // printLog(" gotoPage " + chapter + ", " + elementIndex);

        final IndexRange pageRange = getPageRange(chapter, pageIndexInChapter);
        gotoPageFinish(chapter, pageIndexInChapter, pageRange);
        // printLog(" gotoPage end " + chapter + ", " + elementIndex +
        // ", pageIndex=" + pageIndexInChapter);

		/*
		 * GotoPageCommand gotoCommand = new GotoPageCommand();
		 * gotoCommand.setElementIndex(elementIndex);
		 * gotoCommand.setChapter(chapter);
		 * gotoCommand.setType(GoToType.convertInt(GoToType.ElementIndex));
		 * 
		 * getCWrapper().asynGotoPage(gotoCommand, gotoPageListener);
		 */

		/*
		 * ecHandler.post(new Runnable() {
		 * 
		 * @Override public void run() { reset(); resetUI(); repaintUI(); } });
		 */
    }

    @Override
    public void layoutAndGotoPage(final Chapter chapter, final int elementIndex) {
        if (chapter == null) {
            LogM.e(getClass().getSimpleName(), " gotoPage chapter is null ");
            return;
        }

        final int pageIndexInChapter = mBookManager.composingChapterAndGetPageIndex(chapter, elementIndex);
        // printLog(" gotoPage " + chapter + ", " + elementIndex);

        final IndexRange pageRange = getPageRange(chapter, pageIndexInChapter);
        gotoPageFinish(chapter, pageIndexInChapter, pageRange);
        // printLog(" gotoPage end " + chapter + ", " + elementIndex +
        // ", pageIndex=" + pageIndexInChapter);

		/*
		 * GotoPageCommand gotoCommand = new GotoPageCommand();
		 * gotoCommand.setElementIndex(elementIndex);
		 * gotoCommand.setChapter(chapter);
		 * gotoCommand.setType(GoToType.convertInt(GoToType.ElementIndex));
		 *
		 * getCWrapper().asynGotoPage(gotoCommand, gotoPageListener);
		 */

		/*
		 * ecHandler.post(new Runnable() {
		 *
		 * @Override public void run() { reset(); resetUI(); repaintUI(); } });
		 */
    }

    protected boolean isCurrentEquals(Chapter chapter, int elementIndex) {
        boolean contain = false;
        if (chapter.equals(getCurrentChapter())) {
            IndexRange pageRange = getCurrentPageRange();
            if (pageRange != null) {
                contain = pageRange.hasContain(elementIndex);
            }
        }
        return contain;
    }

    protected boolean isEqualsPage(Chapter chapter, int pageIndex) {
        boolean equals = false;
        if (chapter.equals(getCurrentChapter())) {
            equals = pageIndex == getCurrentPageIndexInChapter();
        }
        return equals;
    }

    protected final IGotoPageListener gotoPageListener = new IGotoPageListener() {
        @Override
        public void onGotoPage(final GotoPageCommand command,
                               final GotoPageResult result) {
            // printLog(" gotoPageListener " + result.getChapter() + "," +
            // result.getPageIndexInChapter() + ", " + result.getPageRange());
            ttsHandler.post(new Runnable() {
                @Override
                public void run() {
                    Chapter chapter = result.getChapter();
                    int pageIndexInChapter = result.getPageIndexInChapter();
                    IndexRange pageRange = result.getPageRange();
                    gotoPageFinish(chapter, pageIndexInChapter, pageRange);
                }
            });
        }
    };

    @Override
    public void gotoPage(int pageIndexInBook) {

        Chapter chapter = getBook().getChapterByPageIndex(pageIndexInBook);
        if (chapter == null) {
            LogM.e(getClass().getSimpleName(), " gotoPage chapter is null ");
            return;
        }
        int pageIndexInChapter = pageIndexInBook
                - chapter.getStartIndexInBook() + 1;
        if (isEqualsPage(chapter, pageIndexInChapter)) {
            printLog(" gotoPage the same page [isEqualsPage]");
            return;
        }

        setCurrentChapter(chapter);
        setCurrentPageIndexInChapter(pageIndexInChapter);

        int tPageIndex = pageIndexInChapter;
        if (hasReadEndPage() && isLastPageInBook()) {
            tPageIndex = pageIndexInChapter - 1;
        }
        IndexRange pageRange = getPageRange(chapter, tPageIndex);
        gotoPageFinish(chapter, pageIndexInChapter, pageRange);

    }

    protected void gotoPageFinish(Chapter chapter, int pageIndexInChapter,
                                  IndexRange pageRange) {
        setCurrentChapter(chapter);
        setCurrentPageIndexInChapter(pageIndexInChapter);
        // onScrollingEnd(DPageIndex.Current);

        if (pageRange != null) {
            setCurrentPageRange(pageRange);
            updateReadProgress(chapter, pageRange.getStartIndexToInt());
        }
        initChapterIndexRange();
        printLogE("gotoPageFinish,chapter=" + chapter + ",pageIndexInChapter=" + pageIndexInChapter);
        reset();
        resetUI();
        repaintUI();
    }

    public void reset() {
        getCWrapper().reset();
        mControllerHolder.reInit(mContext);
        NoteHolder.getHolder().clear();
        exitMediaMode(false);
    }

    public void allreSet() {
        reset();
        resetUI();
        repaintUI();
    }

    @Override
    public boolean isFirstPageInBook() {
        return isFirstChapter(getCurrentChapter()) && isFirstPageInChapter();
    }

    @Override
    public boolean isLastPageInBook() {
        return isLastChapter(getCurrentChapter()) && isLastPageInChapter();
    }

    /**
     * 包括阅读最后一页
     */
    @Override
    public boolean isLastPageInBook(DPageIndex pageIndex) {
        int currPageIndeInChapter = getCurrentPageIndexInChapter();
        if (pageIndex == DPageIndex.Previous) {
            currPageIndeInChapter -= 1;
        } else if (pageIndex == DPageIndex.Next) {
            currPageIndeInChapter += 1;
        }
        return isLastChapter(getCurrentChapter())
                && currPageIndeInChapter >= getCurrentChapterPageCount();
    }

    @Override
    public boolean isFirstPageInChapter() {
        return getCurrentPageIndexInChapter() == 1;
    }

    @Override
    public boolean isLastPageInChapter() {
        return getCurrentPageIndexInChapter() == getCurrentChapterPageCount();
    }

    @Override
    public boolean isFirstChapter(Chapter chapter) {
        if (chapter == null) {
            printLogE(" isFirstChapter chapter == null ");
            return false;// TODO ?
        }
        return getBook().isFirstChapter(chapter);
    }

    @Override
    public boolean isLastChapter(Chapter chapter) {
        if (chapter == null) {
            printLogE(" isLastChapter chapter == null ");
            return false;// TODO ?
        }
        return getBook().isLastChapter(chapter);
    }

    /**
     * 当前页是否可操作
     *
     * @return
     */
    protected boolean isCurrentPageCanOption() {
        boolean is = getCurrentChapter() != null;// &&
        // getCurrentPageIndexInChapter()
        // > 0
        if (!is) {
            printLogE(" isCurrentPageCanOption == false !!! ");
        }
        return is;
    }

    @Override
    public int getCurrentPageIndexInBook() {
        return getPageIndexInBook(getCurrentChapter(),
                getCurrentPageIndexInChapter());
    }

    @Override
    public int getPageIndexInBook(Chapter chapter, int pageIndexInChapter) {
        int pageIndexInBook = getBook().getPageIndexInBookAtBeforeHtml(chapter)
                + pageIndexInChapter;
        return pageIndexInBook;
    }

    @Override
    public int getPageSize() {
        return mReaderApp.getPageSize();
    }

    @Override
    public Chapter getCurrentChapter() {
        return mCurrentChapter;
    }

    protected void setCurrentChapter(Chapter chapter) {
        mCurrentChapter = chapter;
    }

    @Override
    public int getCurrentPageIndexInChapter() {
        return mCurrentPageIndexInChapter;
    }

    protected void setCurrentPageIndexInChapter(int pageIndexInChapter) {
        mCurrentPageIndexInChapter = pageIndexInChapter;
    }

    @Override
    public IndexRange getCurrentPageRange() {
        return mCurrentPageRange;
    }

    protected void setCurrentPageRange(IndexRange pageRange) {
        mCurrentPageRange = pageRange;
    }

    protected IndexRange getCurrentChapterRange() {
        return mCurrentChapterRange;
    }

    protected void setCurrentChapterRange(IndexRange range) {
        this.mCurrentChapterRange = range;
    }

    protected BookNote getCurrentOptionNote() {
        return mCurrentOptionBookNote;
    }

    protected int getChapterIndex(Chapter chapter) {
        return getCWrapper().getChaterIndex(chapter);
    }

    protected String getChapterName(int chapterIndex) {
        return getBook().getChapterName(chapterIndex);
    }

    protected List<BookNote> getCurrentPageBookNotes() {

        int chapterIndex = chapterIndex(getCurrentChapter());
        IndexRange indexRange = getCurrentPageRange();
        int startIndex = indexRange.getStartIndexToInt();
        int endIndex = indexRange.getEndIndexToInt();
        List<BookNote> notes = mMarkNoteManager.getBookNotes(chapterIndex,
                startIndex, endIndex);

        return notes;
    }

    @Override
    public String getPageText() {
        IndexRange indexPage = getCurrentPageRange();
        if (indexPage != null && !indexPage.hasInValid()) {
            return getSelectedText(indexPage.getStartIndex(),
                    indexPage.getEndIndex());
        }
        return "";
    }

    @Override
    public void showSearch(String word) {
        showReaderTextSearchRerultWindow(word, true, false, true);
    }

    // ------ start -------

    /**
     * 根据pageIndex 获取相对于currentChapter的某一章
     *
     * @param pageIndex
     * @return
     */
    protected Chapter getRelativeChapter(DPageIndex pageIndex,
                                         Chapter currentChapter) {
        return getCWrapper().getPrevOrNextChapter(pageIndex, currentChapter);
    }

    protected int getPageIndexInChapter(Chapter chapter, int elementIndex) {
        return getEpubBM().getPageIndexInChapter(chapter, elementIndex);
    }

	/*
	 * protected int getPageIndexInChapter(Chapter chapter, String anchor){ return
	 * getEpubBM().getPageIndexInHtmlByAnchor(chapter, anchor); }
	 */

    /**
     * 获取页的index范围(start - end)
     *
     * @param chapter
     * @param pageIndexInChapter
     * @return
     */
    protected IndexRange getPageRange(Chapter chapter, int pageIndexInChapter) {
        // printLogE(" synchronized getPageStartAndEndIndex start " +
        // pageIndexInChapter);
        IndexRange iRange = getEpubBM().getPageStartAndEndIndex(chapter,
                pageIndexInChapter);
        // printLogE(" synchronized getPageStartAndEndIndex end " +
        // pageIndexInChapter);
        return iRange;
    }

    protected IndexRange getChapterRange(Chapter chapter) {
        IndexRange cRange = getEpubBM().getChapterStartAndEndIndex(chapter);
        return cRange;
    }

    protected Rect[] getRectsByIndex(Chapter chapter, int pageIndexInChapter,
                                     ElementIndex startIndex, ElementIndex endIndex) {
        return getEpubBM().getSelectedRectsByIndex(chapter, pageIndexInChapter,
                startIndex, endIndex);
    }

    protected Rect[] getCurrentPageRectsByPoint(Point start, Point end) {
        Chapter chapter = getCurrentChapter();
        int pageIndexInChapter = getCurrentPageIndexInChapter();
        return getSelectedRectsByPoint(chapter, pageIndexInChapter, start, end);
    }

    protected Rect[] getSelectedRectsByPoint(Chapter chapter,
                                             int pageIndexInChapter, Point start, Point end) {
        return getEpubBM().getSelectedRectsByPoint(chapter, pageIndexInChapter,
                start, end);
    }

    protected ElementIndex[] getSelectedIndex(Chapter chapter,
                                              int pageIndexInChapter, Point pStart, Point pEnd) {
        return getEpubBM().getSelectedStartAndEndIndex(chapter,
                pageIndexInChapter, pStart, pEnd);
    }

    protected ElementIndex getElementIndexByPoint(Chapter chapter,
                                                  int pageIndexInChapter, Point point) {
        return getEpubBM().getElementIndexByPoint(chapter, pageIndexInChapter,
                point);
    }

    protected ElementIndex getSelectedIndex(int x, int y) {
        Chapter chapter = getCurrentChapter();
        int pageIndexInChapter = getCurrentPageIndexInChapter();
        Point point = new Point(x, y);
        return getElementIndexByPoint(chapter, pageIndexInChapter, point);
    }

    protected ElementIndex[] getSelectedIndex(Point pStart, Point pEnd,
                                              int pageIndexInChapter) {
        Chapter chapter = getCurrentChapter();
        // int pageIndexInChapter = getCurrentPageIndexInChapter();
        Point start = pStart;
        Point end = pEnd;
        return getSelectedIndex(chapter, pageIndexInChapter, start, end);
    }

    protected String getSelectedText(ElementIndex startIndex,
                                     ElementIndex endIndex) {
        final ElementIndex st = startIndex;
        final ElementIndex ed = endIndex;
        Chapter chapter = getCurrentChapter();
        return getSelectedText(chapter, ElementIndex.min(st, ed),
                ElementIndex.max(st, ed));
    }

    protected String getSelectedText(Chapter chapter, ElementIndex startIndex,
                                     ElementIndex endIndex) {
        return getEpubBM().getText(chapter, startIndex, endIndex);
    }

    protected ClickResult clickEvent(int x, int y) {
        Chapter chapter = getCurrentChapter();
        int pageIndexInChapter = getCurrentPageIndexInChapter();
        Point point = new Point(x, y);
        return clickEvent(chapter, pageIndexInChapter, point);
    }

    protected ClickResult clickEvent(Chapter chapter, int pageIndexInChapter,
                                     Point point) {
        return getEpubBM().clickEvent(chapter, pageIndexInChapter, point);
    }

    protected ParagraphText getParagraphText(Chapter chapter, int elementIndex,
                                             boolean first, int maxLen) {
        return getEpubBM().getParagraphText(chapter, elementIndex, first,
                maxLen);
    }

    protected void preComposingChapter(Chapter chapter) {
        getEpubBM().preComposingChapter(chapter);
    }

    // ------ end -------

    protected ControllerHolder getCHolder() {
        return mControllerHolder;
    }

    protected int chapterIndex(Chapter chapter) {
        return getCWrapper().getChaterIndex(chapter);
    }

    protected Book getBook() {
        return (Book) mReaderApp.getBook();
    }

    protected ReadInfo getReadInfo() {
        return (ReadInfo) mReaderApp.getReadInfo();
    }

    public boolean doFunction(String fCode, Object... params) {

        return getReaderApp().doFunction(fCode, params);
    }

    public BaseReaderApplicaion getReaderApp() {
        return mReaderApp;
    }

    public void setReaderApp(IReaderApplication readerApp) {
        mReaderApp = (BaseReaderApplicaion) readerApp;
        mBookManager = (IEpubBookManager) mReaderApp.getBookManager();
        mMarkNoteManager = mReaderApp.getMarkNoteManager();
    }

    public IReaderWidget getReaderWidget() {
        return mReaderWidget;
    }

    public void setReaderWidget(IReaderWidget readerWidget) {
        this.mReaderWidget = readerWidget;
    }

    public void setControllerWrapper(BaseControllerWrapper controllerWrapper) {
        mControllerWrapper = controllerWrapper;
    }

    public void setGlobalWindow(GlobalWindow window) {
        mGlobalWindow = window;
        mGlobalWindow.setFloatingOperation(mFloatOperation);
        mGlobalWindow.setNoteWindowOperation(mNoteOperation);
        mGlobalWindow.setDictOperation(mDictOperation);
        mGlobalWindow.setReaderTextSearchResultOperation(mSearchResultOperaton);
        mGlobalWindow.setReaderTextSearchOperation(mSearchOperation);
    }

    protected GlobalWindow getGWindow() {
        return mGlobalWindow;
    }

    @Override
    public IGlobalWindow getWindow() {
        return getGWindow();
    }

    public IEpubBookManager getEpubBM() {
        return mBookManager;
    }

    public BaseControllerWrapper getCWrapper() {
        return mControllerWrapper;
    }

    public Activity getContext() {
        return getReaderApp().getContext();
    }

    final IFloatingOperation mFloatOperation = new IFloatingOperation() {

        @Override
        public void onCopy() {
            hideWindow();

            ClipboardManager clip = (ClipboardManager) getContext()
                    .getSystemService(Context.CLIPBOARD_SERVICE);
            String text = mSelectedText;
            boolean chineseConvert = ReadConfig.getConfig().getChineseConvert();
            BaseReadInfo readInfo = mReaderApp.getReadInfo();
            if (readInfo != null) {
                chineseConvert = chineseConvert && readInfo.isSupportConvert();
            }
            if (chineseConvert) {
                text = BaseJniWarp.ConvertToGBorBig5(mSelectedText, 0);
            }
            clip.setText(text);
            showToast(R.string.reader_copy_success);
            clearSelected();

            DDStatisticsService mDDService = DDStatisticsService
                    .getDDStatisticsService(getContext());
            mDDService.addData(DDStatisticsService.COPY_IN_READING,
                    DDStatisticsService.OPerateTime, System.currentTimeMillis()
                            + "");

        }

        @Override
        public void chackDir() {
//            ToCheckDirctoryUtils.startDict(mReaderApp.getContext(), mSelectedText);
            StartActivityUtils.StartCiBa(mReaderApp.getContext(),mSelectedText);
            hideWindow();
        }

        @Override
        public void onMarkSelected(boolean isAdd, String noteText, int drawLineColor, boolean isOnlyChangeColor) {

            hideWindowExceptFloatWindow();
            if (drawLineColor >= 0)
                mNoteDrawLineColor = drawLineColor;
            if (isAdd) {
                printLog(" onMarkSelected " + mNoteStart + ", " + mNoteEnd);
				/*
				 * Point start = mLeftPoint; Point end = mRightPoint; int[]
				 * indexes = getSelectedIndex(start, end);
				 */
                ElementIndex st = mNoteStart;// indexes[0];
                ElementIndex ed = mNoteEnd;// indexes[1];
                String selectedText = getSelectedText(st, ed);
                // printLog(" onMarkSelected [" + st + "-" + ed + "]," +
                // selectedText);

                mCurrentOptionBookNote = onAddNote(st, ed, selectedText, noteText);
            } else {
                BookNote bookNote = mCurrentOptionBookNote;
                if (bookNote != null) {
                    if (!isOnlyChangeColor)
                        bookNote.setNoteText(noteText);
                    if (drawLineColor >= 0)
                        bookNote.setDrawLineColor(drawLineColor);
                    onUpdateNote(bookNote);
                }
            }

            clearSelected();
            drawFinish(DrawingType.Line, new Point(0, 0), false, false);
            DDStatisticsService mDDService = DDStatisticsService
                    .getDDStatisticsService(getContext());
            mDDService.addData(DDStatisticsService.DO_LINE_IN_READING,
                    DDStatisticsService.OPerateTime, System.currentTimeMillis()
                            + "");
        }

        @Override
        public void onNote(boolean isAdd) {
            APPLog.e("onNote+isAdd" + isAdd);

            hideWindow();

            DDStatisticsService mDDService = DDStatisticsService
                    .getDDStatisticsService(getContext());
            mDDService.addData(DDStatisticsService.DO_NOTE_IN_READING,
                    DDStatisticsService.OPerateTime, System.currentTimeMillis()
                            + "");
            onNoteInner(isAdd);
        }

        @Override
        public void onDelete() {
            hideWindow();

            BookNote bookNote = mCurrentOptionBookNote;
            if (bookNote != null) {

                onDeleteNote(bookNote);
                // resetUI();
                // repaintUI();
                repaintSync(true, true);

                deletePicRect(bookNote);
            } else {
                printLogE(" delete booknote is null ...");
            }
        }

        protected void deletePicRect(BookNote bookNote) {
            if (!TextUtils.isEmpty(bookNote.getNoteText())) {
                int chapterIndex = bookNote.getChapterIndex();
                int pageIndexInChapter = getCurrentPageIndexInChapter();
                NoteFlag flag = new NoteFlag();
                flag.setChapterIndex(chapterIndex);
                flag.setStartIndex(bookNote.getNoteStart());
                flag.setEndIndex(bookNote.getNoteEnd());
                NoteHolder.getHolder().deleteNoteRect(chapterIndex,
                        pageIndexInChapter, flag);
            }
        }

        public void onCancelShare() {
            clearSelected();
        }

        public void onSetCurDrawLineColor(int color) {
            mNoteDrawLineColor = color;
        }

        @Override
        public void onShare() {
            ShareDialog.startShare(getContext(), "", mSelectedText, new ArrayList<String>());
//            ShareActivity.startShare(getContext(),"",mSelectedText,new ArrayList<String>());
        }

        @Override
        public String getWord() {
            return mSelectedText;
        }
    };

    protected void onNoteInner(boolean isAdd) {
        if (mSelectedText==null)return;
        String content = "";
        String source = "";
        String chaptername = "";
        String version = "";
        int chapterIndex = 0;
        int noteStart = -1;
        int noteEnd = -1;
        int isBought = 0;

        ReadInfo readInfo = getReadInfo();
        String bookid = readInfo.getDefaultPid();
        String bookdir = readInfo.getBookDir();
        String bookname = readInfo.getBookName();
        int noteid = -1;
        int drawLineColor = mNoteDrawLineColor;
        long noteTime = 0;

        BookNote note = getCurrentOptionNote();
        if ( null==note ||! mSelectedText.equals(note.getSourceText())) {
//            source = note.getSourceText();
//            chaptername = note.getChapterName();
//            chapterIndex = note.getChapterIndex();
//            noteStart = note.getNoteStart();
//            noteEnd = note.getNoteEnd();
//            isBought = note.getIsBought();
//            noteid = note.getId();
//            version = note.getBookModVersion();
//            drawLineColor = note.getDrawLineColor();
//            noteTime = note.getNoteTime();
//            noteTime = new Date().getTime();
//        } else {
            source = mSelectedText;
            chapterIndex = getChapterIndex(getCurrentChapter());
            chaptername = getChapterName(chapterIndex);
            noteStart = mNoteStart.getIndex();
            noteEnd = mNoteEnd.getIndex();
            isBought = readInfo.isBoughtToInt();
            version = readInfo.getEpubModVersion();
            noteTime = new Date().getTime();

            note = new BookNote();
            note.bookId = bookid;
            note.bookPath = bookname;
            note.chapterName = chaptername;
            note.chapterIndex = chapterIndex;
            note.sourceText = source;
            note.noteStart = noteStart;
            note.noteEnd = noteEnd;
            note.isBought = isBought;
            note.status = String.valueOf(Status.COLUMN_NEW);
            note.cloudStatus = String.valueOf(Status.CLOUD_NO);
            note.noteTime=noteTime;
            note.bookModVersion = version;
            note.drawLineColor = drawLineColor;
        }

//        String author = null;
//        try {
//            JSONObject obj = new JSONObject(readInfo.getBookJson());
//            author = obj.optString("author", "");
//        } catch (Exception e) {
//        }
//        Intent tent = new Intent(mReaderApp.getContext(),
//                BookNoteActivity.class);
//        tent.putExtra(Constants.BOOK_ID, bookid);
//        tent.putExtra(Constants.BOOK_NAME, bookname);
//        tent.putExtra(BookNoteActivity.BOOK_NOTE_SOURCE_TEXT, source);
//        tent.putExtra(BookNoteActivity.BOOK_NOTE_CONTENT, content);
//        tent.putExtra(Constants.BOOK_DIR, bookdir);
//        tent.putExtra(BookNoteActivity.BOOK_NOTE_SAVE_OR_UPDATE, isAdd);
//        tent.putExtra(BookNoteActivity.BOOK_NOTE_AUTHOR, author);
//        tent.putExtra(BookNoteActivity.BOOK_NOTE_TIME, noteTime);
//
//        tent.putExtra(NoteColumn.ChapterName, chaptername);
//        tent.putExtra(NoteColumn.ChapterIndex, chapterIndex);
//        tent.putExtra(NoteColumn.NoteStart, noteStart);
//        tent.putExtra(NoteColumn.NoteEnd, noteEnd);
//        tent.putExtra(NoteColumn.Id, noteid);
//        tent.putExtra(NoteColumn.IsBought, isBought);
//        tent.putExtra(NoteColumn.ModVersion, version);
//        tent.putExtra(NoteColumn.ExpColumn4, drawLineColor);
//        Bundle bundle = new Bundle();
//        bundle.putSerializable(BookNoteActivity.BOOK_NOTE_CHAPTER, getCurrentChapter());
//        tent.putExtras(bundle);
//        tent.putExtra(BookNoteActivity.BOOK_NOTE_BOOKCOVER, readInfo.getBookCover());
//        tent.putExtra(BookNoteActivity.BOOK_NOTE_BOOKAUTHOR, readInfo.getAuthorName());
//        tent.putExtra(BookNoteActivity.BOOK_NOTE_BOOKDESC, readInfo.getBookDesc());
//
        APPLog.e("隐藏原本笔记保存跳转");
//        (mReaderApp.getContext()).startActivityForResult(tent,
//                BookNoteActivity.REQUEST_CODE);

//        BookNote note = new BookNote();
////        long operateTime = new Date().getTime();
//        note.bookId = bookid;
//        note.bookPath = bookname;
//        note.chapterName = chaptername;
//        note.chapterIndex = chapterIndex;
//        note.sourceText = source;
//        note.noteStart = noteStart;
//        note.noteEnd = noteEnd;
////        note.noteText = mInputText.getText().toString().trim();
////        note.noteTime = new Date().getTime();
//        note.isBought = isBought;
//        note.status = String.valueOf(Status.COLUMN_NEW);
//        note.cloudStatus = String.valueOf(Status.CLOUD_NO);
////        note.modifyTime = String.valueOf(operateTime);
//        note.bookModVersion = version;
//        note.drawLineColor = drawLineColor;

//        SaveNoteDialog.getdialog(mReaderApp.getContext(),note);
        if (isAdd) {
            Intent intent = new Intent(NotificationWhat.REMARKES);
            intent.putExtra(BookNoteActivity.BOOK_NOTE_OBJECT, note);
            mReaderApp.getContext().sendBroadcast(intent);
        } else {
            new AlertDialog(mReaderApp.getContext()).builder().setTitle("删除笔记").setCancelable(false).setMsg("您确定要删除笔记，删除后不可恢复").
                    setNegativeButton("取消", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            hideWindow();
                        }
                    }).setPositiveButton("确定", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mFloatOperation.onDelete();
                }
            }).show();
        }

    }

    protected Activity getTopActivity() {
        Activity topAct = getContext();
        if (getContext().getParent() != null) {
            topAct = getContext().getParent();
        }
        return topAct;
    }

    protected void showNetTypeDialog() {
        final Activity context = getContext();
        final CommonDialog netTipDialog = new CommonDialog(getTopActivity(),
                R.style.dialog_commonbg);
        netTipDialog.setInfo(context
                .getString(R.string.before_download_info_tip));
        netTipDialog.setRightButtonText(context
                .getString(R.string.before_download_continue));
        netTipDialog.setOnRightClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                netTipDialog.dismiss();
            }
        });
        netTipDialog.setLeftButtonText(context
                .getString(R.string.cancel_download));
        netTipDialog.setOnLeftClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                netTipDialog.dismiss();
            }
        });
        netTipDialog.show();
    }

    protected void checkWifiAndDownloadDict(final Activity context) {
        if (NetUtils.isMobileConnected(getContext())) {
            showNetTypeDialog();
        } else {
        }
    }

    protected void checkStopSearch() {
        getGWindow().checkStopSearch();
    }

    final INoteWindowOperation mNoteOperation = new INoteWindowOperation() {
        @Override
        public void onClick(int flag) {
            printLog(" NoteOperation onClick " + flag);
            if (flag == GlobalWindow.NW_FLAG_NOTE) {
                onNoteInner(false);
            }
        }
    };

    final IDictOperation mDictOperation = new IDictOperation() {

        @Override
        public void onYoudao(String word) {
            printLog(" onYoudao " + word);
            clearSelected();
            hideWindow();
        }

        @Override
        public void onBaidu(String word) {
            printLog(" onBaidu " + word);

            clearSelected();
            hideWindow();
        }

        @Override
        public void onDictNote(String word, String explain) {
            printLog(" onDictNote " + word);

            ElementIndex st = mNoteStart;// indexes[0];
            ElementIndex ed = mNoteEnd;// indexes[1];
            String selectedText = getSelectedText(st, ed);
            // printLog(" onMarkSelected [" + st + "-" + ed + "]," +
            // selectedText);
            if (!TextUtils.isEmpty(explain) && explain.length() > MAX_NOTELEN) {
                explain = explain.substring(0, MAX_NOTELEN);
            }
            onAddNote(st, ed, selectedText, explain);
            clearSelected();
            hideWindow();
        }

    };

    final IReaderTextSearchResultOperation mSearchResultOperaton = new IReaderTextSearchResultOperation() {

        public void gotoPageOnSearch(Chapter chapter,
                                     ElementIndex wordStartIndex, ElementIndex wordEndIndex) {
            printLog(" gotoPageOnSearch " + chapter + ", " + wordStartIndex
                    + "-" + wordEndIndex);

            final int pageIndexInChapter = getPageIndexInChapter(chapter,
                    wordStartIndex.getIndex());

            final IndexRange pageRange = getPageRange(chapter,
                    pageIndexInChapter);
            setCurrentChapter(chapter);

            if (pageIndexInChapter != getCurrentPageIndexInChapter()) {
                setCurrentPageIndexInChapter(pageIndexInChapter);
                if (pageRange != null) {
                    setCurrentPageRange(pageRange);
                    updateReadProgress(chapter, pageRange.getStartIndexToInt());
                }
                initChapterIndexRange();

                reset();
                resetUI();
                repaintUI();
            } else {
                repaintSync(false, false);
            }
            showReaderTextSearchWindow();
        }

        @Override
        public void dismissSearchResultWindow() {
            hideReaderTextSearchRerultWindow();
        }

        @Override
        public void doSearch(String searchText) {
            // TODO Auto-generated method stub
            showReaderTextSearchRerultWindow(searchText, true);
        }

    };

    final IReaderTextSearchOperation mSearchOperation = new IReaderTextSearchOperation() {

        @Override
        public void gotoPageOnSearch(boolean isPre) {
            OneSearch oneSearch = null;
            if (isPre) {
                oneSearch = getGWindow().getOneSearch(true);
            } else {
                oneSearch = getGWindow().getOneSearch(false);
            }
            if (oneSearch != null) {
                SearchDataHolder.getHolder().setCurrent(oneSearch);
                mSearchResultOperaton.gotoPageOnSearch(oneSearch.getChapter(),
                        oneSearch.getKeywordStartIndex(),
                        oneSearch.getKeywordEndIndex());
            } else {
                if (isPre) {
                    showToast(R.string.reader_text_search_file_start);
                } else {
                    showToast(R.string.reader_text_search_file_end);
                }
            }
        }

        @Override
        public void showSearchResult() {
            getGWindow().hideReaderTextSearchWindow(false);
            getGWindow().showReaderTextSearchResultWindow(null, false, true,
                    false);
        }

        @Override
        public void hide(boolean abortSearch) {
            SearchDataHolder.getHolder().resetCurrent();
            if (abortSearch) {
                checkStopSearch();
            }
            clearSelected();
        }
    };

    protected final static int MSG_PLAY_TTS = 1;
    protected final static int MSG_START_TTS = 2;
    protected final static int MSG_HIGHLIGHT_TTS = 3;
    protected Handler ttsHandler;

    private void dealMsg(Message msg) {
    }

    private static class MyHandler extends Handler {
        private final WeakReference<EpubReaderController> mFragmentView;

        MyHandler(EpubReaderController view) {
            this.mFragmentView = new WeakReference<EpubReaderController>(view);
        }

        @Override
        public void handleMessage(Message msg) {
            EpubReaderController service = mFragmentView.get();
            if (service != null) {
                super.handleMessage(msg);
                try {
                    service.dealMsg(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onMediaStop() {
        hideAudio();
        exitMediaMode(true);
    }

    @Override
    public void hideMedia() {
        hideAudio();

    }

    @Override
    public boolean isShowMedia() {
        return getGWindow().isShowingAudio();
    }

    @Override
    public boolean changeVideoOrientation() {
        boolean isChangeVideoOrientation = false;
        EpubPageView epubPageView = getCurrentEpubPageView();
        if (epubPageView != null) {
            isChangeVideoOrientation = epubPageView.changeVideoOrientation();
        }
        return isChangeVideoOrientation;
    }

    protected EpubPageView getCurrentEpubPageView() {
        EpubPageView epubPageView = null;
        if (mReaderWidget instanceof EpubReaderWidget) {
            EpubReaderWidget epubReaderWidget = (EpubReaderWidget) mReaderWidget;
            BasePageView currentView = epubReaderWidget.getCurrentView();
            if (currentView instanceof EpubPageView) {
                epubPageView = (EpubPageView) currentView;
            }
        }
        return epubPageView;
    }

    @Override
    public void resetVedioView() {
        EpubPageView epubPageView = getCurrentEpubPageView();
        if (epubPageView != null) {
            epubPageView.resetVedioView();
            epubPageView.showPlayIcon();
        }
    }

    @Override
    public boolean isVideoShow() {
        boolean isVideoShow = false;
        EpubPageView epubPageView = getCurrentEpubPageView();
        if (epubPageView != null) {
            isVideoShow = epubPageView.isVideoShow();
        }
        return isVideoShow;
    }

    @Override
    public boolean isVideoLandscape() {
        boolean isVideoShow = false;
        EpubPageView epubPageView = getCurrentEpubPageView();
        if (epubPageView != null) {
            isVideoShow = epubPageView.isVideoLandscape();
        }
        return isVideoShow;
    }

    public void resetVedioViewWithOutOrientation() {
        EpubPageView epubPageView = getCurrentEpubPageView();
        if (epubPageView != null) {
            epubPageView.resetVedioViewWithOutOrientation();
            epubPageView.showPlayIcon();
        }
    }

    public String getSelectedTextWithPara(int startIndex, int endIndex) {
        if (getReadInfo().getEBookType() == BaseJniWarp.BOOKTYPE_DD_PDF) {
            return mSelectedText;
        }
        BaseJniWarp baseJniWarp = new BaseJniWarp();
        BaseJniWarp.EPageIndex pageIndex = new BaseJniWarp.EPageIndex();
        pageIndex.filePath = mCurrentChapter.getPath();
        pageIndex.pageIndexInChapter = -1;
        pageIndex.bookType = BaseJniWarp.BOOKTYPE_THIRD_EPUB;
        if (getReadInfo().getEBookType() == BaseJniWarp.BOOKTYPE_DD_TXT) {
            pageIndex.bookType = BaseJniWarp.BOOKTYPE_DD_TXT;
            TxtChapter txtChapter = (TxtChapter) mCurrentChapter;
            pageIndex.startByte = txtChapter.getStartByte();
            pageIndex.endByte = txtChapter.getEndByte();
        }
        String[] textsBookNoteInBook = baseJniWarp.getTextWithPara(pageIndex, startIndex, endIndex);
        String textJoin = new String();
        if (textsBookNoteInBook.length > 0) {
            textJoin += textsBookNoteInBook[0];
            for (int i = 1; i < textsBookNoteInBook.length; i++) {
                textJoin += "<p style=\"text-indent:2em;\">" + textsBookNoteInBook[i] + "</p>";
            }
        }

        return textJoin;
    }
}
