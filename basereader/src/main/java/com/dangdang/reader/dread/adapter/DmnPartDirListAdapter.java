package com.dangdang.reader.dread.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;

import com.dangdang.reader.dread.config.ReadConfig;
import com.dangdang.reader.dread.core.base.BaseReaderApplicaion;
import com.dangdang.reader.dread.core.base.IEpubReaderController;
import com.dangdang.reader.dread.core.epub.ReaderAppImpl;
import com.dangdang.reader.dread.data.PartReadInfo;
import com.dangdang.reader.dread.format.Book;
import com.dangdang.reader.dread.format.Book.BaseNavPoint;
import com.dangdang.reader.dread.format.Chapter;
import com.dangdang.reader.dread.format.epub.EpubBook.EpubNavPoint;
import com.dangdang.reader.dread.format.epub.IEpubBookManager;
import com.dangdang.reader.dread.format.part.PartBook;
import com.dangdang.reader.dread.format.part.PartChapter;
import com.dangdang.zframework.view.DDTextView;

public class DmnPartDirListAdapter extends DmnDirListAdapter {

    public DmnPartDirListAdapter(Context context) {
        super(context);
    }

    protected void hanldeViews(View itemView, DDTextView textView, DDTextView pageView, BaseNavPoint navPoint, boolean isInComposingChapter) {

        if (navPoint instanceof PartBook.PartNavPoint) {
            //章

            pageView.setText("");

            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, SUB_NAV_TEXT_SIZE); //
            textView.setPadding(paddingL, paddingTB, paddingR, paddingTB);
            pageView.setVisibility(View.VISIBLE);
        } else {
            //卷
            pageView.setVisibility(View.GONE);
            textView.setPadding(0, paddingTB, paddingR, paddingTB);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, PARENT_NAV_TEXT_SIZE); //
        }

        if (ReadConfig.getConfig().isNightMode()) {
            textView.setTextColor(Color.WHITE);
            pageView.setTextColor(Color.WHITE);
        } else {
            textView.setTextColor(FREE_COLOR);
            pageView.setTextColor(pageColor);
        }
    }

    public CheckSubNavR checkResult(int position, BaseNavPoint navPoint, boolean bookComposingDone) {

        CheckSubNavR checkResult = new CheckSubNavR();
        checkResult.pageIndex = 0;
        checkResult.isContain = false;
        checkResult.setExist(true);

        final boolean isBookComposingDone = bookComposingDone;// ReaderApplication.getApp().isBookComposingDone();
        if (isBookComposingDone) {
            try {
                final IEpubReaderController controller = getController();
                PartChapter currentHtml = (PartChapter) controller.getCurrentChapter();
                //这是章节
                if (navPoint instanceof PartBook.PartNavPoint) {
                    final PartBook.PartNavPoint partNavPoint = (PartBook.PartNavPoint) navPoint;
                    if (currentHtml != null) {
                        checkResult.isContain = currentHtml.getId() == partNavPoint.getChapterId();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return checkResult;
    }

    private IEpubReaderController getController() {
        return (IEpubReaderController) getReaderApp().getReaderController();
    }

    private int pageIndexInBook(Chapter currentChapter, int indexInHtml) {
        int indexInBook = indexInHtml;
        Book bookM = getBook();
        indexInBook = bookM.getPageIndexInBookAtBeforeHtml(currentChapter) + indexInHtml;
        return indexInBook;
    }

    private CheckSubNavR isSubNavContain(int position, EpubNavPoint navPoint, EpubNavPoint nextNav, String htmlPath, final int currentPage,
                                         boolean isBookComposingDone, Book book) {

        CheckSubNavR result = new CheckSubNavR();
        Chapter html = book.getChapterByPath(htmlPath);

        if (html != null) {
            int pageIndexInHtml = 1;
            if (!TextUtils.isEmpty(navPoint.anchor) && isBookComposingDone) {
                pageIndexInHtml = getPageByAnchor(navPoint, html);
                pageIndexInHtml = pageIndexInHtml < 0 ? 1 : pageIndexInHtml;
            }

            result.pageIndex = html.getStartIndexInBook() + pageIndexInHtml - 1;// html.startIndexInBook
            // +
            // pageIndexInHtml
            // -
            // 1;

            if (isBookComposingDone) {
                result.isContain = isContain(position, result.pageIndex, htmlPath, currentPage, nextNav, book);

				/*
                 * if(!TextUtils.isEmpty(navPoint.shortSrc) &&
				 * navPoint.shortSrc.equals(navPoint.parentNav.shortSrc)){
				 * 
				 * result.isContain = isContain(position, result.pageIndex,
				 * htmlPath, currentPage, nextNav);
				 * 
				 * } else { if(html != null){ result.pageIndex =
				 * html.startIndexInBook; } result.isContain =
				 * mBook.isHtmlContainPageIndex(html, currentPage); }
				 */
            }
        } else {
            result.setExist(false);
        }

        return result;
    }

    private String getHtmlPathByFullSrc(String fullSrc) {
        String htmlPath = fullSrc;
        int lastIndex = fullSrc.lastIndexOf("#");
        if (lastIndex != -1) {
            htmlPath = fullSrc.substring(0, lastIndex);
        }
        return htmlPath;
    }

    private boolean isContain(int position, int pageIndex, String htmlPath, int currentPage, EpubNavPoint nextNav, Book book) {

        boolean contain = false;
        /*
         * if(nextNav.isPayTip()){ return contain; }
		 */
        int navPointPageStart = pageIndex;
        int navPointPageEnd = navPointPageStart;
        try {
            if (position < getCount() - 1 && !nextNav.isPayTip()) {
                // int nextPosition = position + 1;
                // NavPoint nextNav = mData.get(nextPosition);
                String hpath = getHtmlPathByFullSrc(nextNav.fullSrc);
                hpath = (hpath.equals(htmlPath)) ? htmlPath : hpath;

                Chapter html = book.getChapterByPath(hpath);
                navPointPageEnd = html.getStartIndexInBook() + getPageByAnchor(nextNav, html) - 1;
                if (currentPage >= navPointPageStart && currentPage < navPointPageEnd) {
                    contain = true;
                }
            } else {
                EpubNavPoint currentNavPoint = (EpubNavPoint) getData().get(position);
                if (currentNavPoint.isPayTip() && nextNav.isPayTip()) {
                    return contain;
                }
                Chapter html = book.getChapterByPath(htmlPath);
                if (html != null) {
                    navPointPageEnd = html.getEndIndexInBook();
                }
                if (currentPage >= navPointPageStart && currentPage <= navPointPageEnd) {
                    contain = true;
                }
            }
        } catch (Exception e) {
            // e.printStackTrace();
        }

        return contain;
    }

    protected int getPageByAnchor(EpubNavPoint nextNav, Chapter html) {
        // pringLog(" getPageByAnchor start " + nextNav.anchor);
        int page = getBookManager().getPageIndexInHtmlByAnchor(html, nextNav.anchor);
        // pringLog(" getPageByAnchor end " + nextNav.anchor);
        return page;
    }

    private BaseReaderApplicaion getReaderApp() {
        return ReaderAppImpl.getApp();
    }

    private Book getBook() {
        return (Book) getReaderApp().getBook();
    }

    private IEpubBookManager getBookManager() {
        return (IEpubBookManager) getReaderApp().getBookManager();
    }

}
