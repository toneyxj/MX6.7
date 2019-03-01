package com.dangdang.reader.dread.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.View;

import com.dangdang.reader.R;
import com.dangdang.reader.dread.config.ReadConfig;
import com.dangdang.reader.dread.core.base.BaseReaderApplicaion;
import com.dangdang.reader.dread.core.base.IEpubReaderController;
import com.dangdang.reader.dread.core.epub.ReaderAppImpl;
import com.dangdang.reader.dread.format.Book;
import com.dangdang.reader.dread.format.Book.BaseNavPoint;
import com.dangdang.reader.dread.format.Chapter;
import com.dangdang.reader.dread.format.txt.TxtBook.TxtNavPoint;
import com.dangdang.zframework.view.DDTextView;

public class DmnTxtDirListAdapter extends DmnDirListAdapter {

	public DmnTxtDirListAdapter(Context context) {
		super(context);
	}

	@Override
	protected void hanldeViews(View itemView, DDTextView textView, DDTextView pageView, BaseNavPoint navPoint, boolean isInComposingChapter) {

		textView.setPadding(paddingTB, paddingTB, paddingR, paddingTB);
		textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, PARENT_NAV_TEXT_SIZE); //
		textView.setTextColor(mContext.getResources().getColor(R.color.read_text_depth_black));

		final TxtNavPoint txtNavPoint = (TxtNavPoint) navPoint;
		if (txtNavPoint.getSplitChapterNum() > 0) {
			textView.setText(txtNavPoint.lableText + " (" + txtNavPoint.getSplitChapterNum() + ")");
		}
		if (ReadConfig.getConfig().isNightMode()) {
			textView.setTextColor(Color.WHITE);
			pageView.setTextColor(Color.WHITE);
		} else {
			textView.setTextColor(FREE_COLOR);
			pageView.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public CheckSubNavR checkResult(int position, BaseNavPoint navPoint, boolean isBookComposingDone) {

		CheckSubNavR checkResult = new CheckSubNavR();
		checkResult.pageIndex = 0;
		checkResult.isContain = false;
		if (isBookComposingDone) {
			try {
				final Book book = getBook();

				final TxtNavPoint txtNavPoint = (TxtNavPoint) navPoint;
				final Chapter chapter = book.getChapterByPath(txtNavPoint.getTagPath());
				if (chapter != null) {
					checkResult.pageIndex = chapter.getStartIndexInBook();
				}
				final Chapter currentChapter = getController().getCurrentChapter();
				final int indexInBook = book.getPageIndexInBookAtBeforeHtml(currentChapter) + getController().getCurrentPageIndexInChapter();
				checkResult.isContain = book.isChapterContainPageIndex(chapter, indexInBook);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return checkResult;
	}

	private Book getBook() {
		return (Book) getReaderApp().getBook();
	}

	private BaseReaderApplicaion getReaderApp() {
		return ReaderAppImpl.getApp();
	}

	private IEpubReaderController getController() {
		return (IEpubReaderController) getReaderApp().getReaderController();
	}

}
