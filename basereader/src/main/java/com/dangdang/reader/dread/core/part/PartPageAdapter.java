package com.dangdang.reader.dread.core.part;

import com.dangdang.reader.dread.core.epub.EpubPageAdapter;
import com.dangdang.reader.dread.format.Chapter;

import java.text.DecimalFormat;

public class PartPageAdapter extends EpubPageAdapter {


    protected String getProgress(int pageInChapter, Chapter chapter) {
        int chapterPageCount = mControllerWrapper
                .getChapterPageCount(chapter);
        if (mController.isLastChapter(chapter))
            chapterPageCount--;
        return formatProgress(pageInChapter, chapterPageCount);
    }
    /**
     * 转换章内进度
     *
     * @return
     */
    private String formatProgress(int pageNum, int pageCount) {
        printLog("pageNum=" + pageNum + ",pageCount=" + pageCount);
        float progress = pageNum * 100f / pageCount;
        DecimalFormat df = new DecimalFormat();
        df.applyPattern("#0.00");
        return df.format(progress) + "%";
    }

}
