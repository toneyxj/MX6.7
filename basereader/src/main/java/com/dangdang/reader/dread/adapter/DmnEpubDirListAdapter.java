package com.dangdang.reader.dread.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
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
import com.dangdang.reader.dread.format.epub.EpubBook.EpubNavPoint;
import com.dangdang.reader.dread.format.epub.IEpubBookManager;
import com.dangdang.zframework.view.DDTextView;

public class DmnEpubDirListAdapter extends DmnDirListAdapter {

	public DmnEpubDirListAdapter(Context context) {
		super(context);
	}

	protected void hanldeViews(View itemView, DDTextView textView, DDTextView pageView, BaseNavPoint navPoint, boolean isInComposingChapter) {

		final EpubNavPoint epubNavPoint = (EpubNavPoint) navPoint;
		final boolean payTip = epubNavPoint.isPayTip();
		if (epubNavPoint.isSub) {
			textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, SUB_NAV_TEXT_SIZE); //
			textView.setPadding(paddingL, paddingTB, paddingR, paddingTB);
		} else {
			textView.setPadding(0, paddingTB, paddingR, paddingTB);
			textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, PARENT_NAV_TEXT_SIZE); //
		}
		View lockView = itemView.findViewById(R.id.read_dmn_ditem_lock);
		if (ReadConfig.getConfig().isNightMode()) {
			textView.setTextColor(Color.WHITE);
			pageView.setTextColor(Color.WHITE);
			
			if (payTip || !isInComposingChapter) {
				pageView.setVisibility(View.GONE);
				lockView.setVisibility(View.VISIBLE);
			} else {
				pageView.setVisibility(View.VISIBLE);
				lockView.setVisibility(View.GONE);
			}
		} else {
			if (payTip || !isInComposingChapter) {
				textView.setTextColor(LOCK_COLOR);
				pageView.setVisibility(View.GONE);
				lockView.setVisibility(View.VISIBLE);
			} else {
				textView.setTextColor(FREE_COLOR);
				pageView.setTextColor(pageColor);
				pageView.setVisibility(View.VISIBLE);
				lockView.setVisibility(View.GONE);
			}
		}
	}

	public CheckSubNavR checkResult(int position, BaseNavPoint navPoint, boolean bookComposingDone) {

		CheckSubNavR checkResult = new CheckSubNavR();
		checkResult.pageIndex = 0;
		checkResult.isContain = false;

		final boolean isBookComposingDone = bookComposingDone;// ReaderApplication.getApp().isBookComposingDone();
		if (isBookComposingDone) {
			try {
				final EpubNavPoint epubNavPoint = (EpubNavPoint) navPoint;
				checkResult = checkIsSelect(position, epubNavPoint, true);
				// isSelect = checkResult.isContain;
				/*
				 * if(checkResult.pageIndex > 0){ pageIndex = checkResult.pageIndex;
				 * }
				 */
				if (epubNavPoint.isPayTip()) {
					checkResult.pageIndex = getReaderApp().getPageSize() + epubNavPoint.paytipIndex;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return checkResult;
	}

	protected CheckSubNavR checkIsSelect(int position, EpubNavPoint navPoint, final boolean isBookComposingDone) {

		int indexInBook = 0;
		CheckSubNavR checkResult = null;

		final IEpubReaderController controller = getController();
		final Book mBook = getBook();

		String htmlPath = getHtmlPathByFullSrc(navPoint.fullSrc);
		// if(isBookComposingDone){
		Chapter currentHtml = controller.getCurrentChapter();
		if (currentHtml != null) {
			indexInBook = pageIndexInBook(currentHtml, controller.getCurrentPageIndexInChapter());// TODO
																									// ?
		}
		if (navPoint.isSub) {
			EpubNavPoint nextNav = (EpubNavPoint) ((position < getCount() - 1) ? getData().get(position + 1) : navPoint);
			checkResult = isSubNavContain(position, navPoint, nextNav, htmlPath, indexInBook, isBookComposingDone, mBook);

			/*
			 * if(checkResult.pageIndex > 0){ pageIndex = checkResult.pageIndex;
			 * }
			 */
			// isSelect = checkResult.isContain;
		} else {

			CheckSubNavR cresult = new CheckSubNavR();
			// boolean isContain = false;
			final Chapter html = mBook.getChapterByPath(htmlPath);
			if (html != null) {
				cresult.pageIndex = html.getStartIndexInBook();// html.startIndexInBook;
			} else {
				cresult.setExist(false);
			}
			cresult.isContain = mBook.isChapterContainPageIndex(html, indexInBook);
			checkResult = cresult;
			// final boolean hasSubs = navPoint.whetherHasSubs();
			// isSelect = isContain;//(hasSubs && isContain);
			/*
			 * if(hasSubs && isContain){ NavPoint firstSub =
			 * navPoint.getSubNavPoint().get(0); NavPoint nextNav = (position <
			 * getCount() - 2) ? mData.get(position + 2) : firstSub;
			 * CheckSubNavR result = isSubNavContain(position, firstSub,
			 * nextNav, htmlPath, currentPage); final boolean hasFirstSubContain
			 * = result.isContain; if(hasFirstSubContain){ isSelect = false; } }
			 * else { isSelect = (!hasSubs && isContain); }
			 */
		}
		// }
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

	protected CheckSubNavR isSubNavContain(int position, EpubNavPoint navPoint, EpubNavPoint nextNav, String htmlPath, final int currentPage,
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

	protected String getHtmlPathByFullSrc(String fullSrc) {
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

	protected IEpubBookManager getBookManager() {
		return (IEpubBookManager) getReaderApp().getBookManager();
	}

}
