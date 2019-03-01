package com.dangdang.reader.dread.core.base;

import com.dangdang.reader.dread.format.BaseBookManager.GotoPageCommand;
import com.dangdang.reader.dread.format.BaseBookManager.GotoPageResult;
import com.dangdang.reader.dread.format.Chapter;
import com.dangdang.reader.dread.format.IndexRange;

/**
 * @author luxu
 */
public interface IEpubReaderController extends IReaderController {


    boolean isFirstPageInChapter();

    boolean isLastPageInChapter();

    boolean isFirstChapter(Chapter chapter);

    boolean isLastChapter(Chapter chapter);

    Chapter getCurrentChapter();

    int getCurrentPageIndexInChapter();

    IndexRange getCurrentPageRange();

    String getPageText();

    void gotoPage(Chapter chapter, int elementIndex);

    void gotoPage(GoToParams params);

    int getPageIndexInBook(Chapter chapter, int pageIndexInChapter);

    boolean isLastPageInBook(DPageIndex pageIndex);

    void showSearch(String word);

    enum GoToType {

        ElementIndex, Anchor, LastPage;

        public static int convertInt(GoToType type) {

            int it = GotoPageCommand.TYPE_ELEMENTI;
            if (type == Anchor) {
                it = GotoPageCommand.TYPE_ANCHOR;
            } else if (type == LastPage) {
                it = GotoPageCommand.TYPE_LASTPAGE;
            } else if (type == ElementIndex) {
                it = GotoPageCommand.TYPE_ELEMENTI;
            }
            return it;
        }

    }

    interface IGotoPageListener {

        void onGotoPage(GotoPageCommand command, GotoPageResult result);

    }

    void layoutAndGotoPage(final Chapter chapter, final int elementIndex);
}
