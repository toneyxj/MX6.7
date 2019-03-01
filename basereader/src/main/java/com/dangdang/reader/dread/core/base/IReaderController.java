package com.dangdang.reader.dread.core.base;

import com.dangdang.reader.dread.core.epub.IGlobalWindow;


/**
 * @author luxu
 */
public interface IReaderController {

    /**
     * 是否能滚动
     *
     * @param pageIndex
     * @return
     */
    boolean canScroll(DPageIndex pageIndex);

    void onScrollingEnd(DPageIndex pageIndex);

    //public void doPaint(PaintContext paintContext, DPageIndex pageIndex, Point start, Point end);

    boolean onFingerPress(int x, int y);

    boolean onFingerMove(int x, int y);

    boolean onFingerRelease(int x, int y, boolean fast);

    boolean onFingerLongPress(int x, int y);

    boolean onFingerReleaseAfterLongPress(int x, int y);

    boolean onFingerMoveAfterLongPress(int x, int y);

    boolean onFingerSingleTap(int x, int y, long time);

    boolean onFingerDoubleTap(int x, int y);

    boolean isFirstPageInBook();

    boolean isLastPageInBook();

    int getCurrentPageIndexInBook();

    int getPageSize();

    void gotoPage(int pageIndexInBook);

    void reset();

    boolean isSelectedStatus();

    ReadStatus getReadStatus();

    void cancelOption(boolean repaint);

    IGlobalWindow getWindow();

    void onSizeChange();

    enum DPageIndex {

        Previous, Current, Next;

        public DPageIndex getNext() {
            switch (this) {
                case Previous:
                    return Current;
                case Current:
                    return Next;
                default:
                    return null;
            }
        }

        public DPageIndex getPrevious() {
            switch (this) {
                case Current:
                    return Previous;
                case Next:
                    return Current;
                default:
                    return null;
            }
        }
    }

    /**
     * 章索引，标识当前排版的章节索引
     *
     * @author Yhyu
     * @date 2015-1-23 下午5:06:04
     */
    enum DChapterIndex {

        Previous, Current, Next;

        public DChapterIndex getNext() {
            switch (this) {
                case Previous:
                    return Current;
                case Current:
                    return Next;
                default:
                    return null;
            }
        }

        public DChapterIndex getPrevious() {
            switch (this) {
                case Current:
                    return Previous;
                case Next:
                    return Current;
                default:
                    return null;
            }
        }
    }

    enum DDirection {

        LeftToRight(true), RightToLeft(true);

        public final boolean isHorizontal;

        DDirection(boolean isHorizontal) {
            this.isHorizontal = isHorizontal;
        }

    }

    enum DAnimType {

        None, Slide, Shift, Vertical, Shape

    }

    enum ReadStatus {

        Read, VoiceRead, TTS

    }

}
