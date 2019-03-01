package com.dangdang.reader.dread.core.epub;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.View;

import com.dangdang.reader.dread.config.ReadConfig;
import com.dangdang.reader.dread.data.OneSearch;
import com.dangdang.reader.dread.format.BaseReadInfo;
import com.dangdang.reader.dread.format.Chapter;
import com.dangdang.reader.dread.jni.BaseJniWarp.ElementIndex;
import com.dangdang.reader.dread.view.AudioWindow;
import com.dangdang.reader.dread.view.DictWindow;
import com.dangdang.reader.dread.view.FloatingWindow;
import com.dangdang.reader.dread.view.NoteWindow;
import com.dangdang.reader.dread.view.ReaderTextSearchResultWindow;
import com.dangdang.reader.dread.view.ReaderTextSearchWindow;
import com.dangdang.zframework.log.LogM;
import com.dangdang.zframework.utils.DRUiUtility;

import java.lang.ref.WeakReference;

public class GlobalWindow implements IGlobalWindow {

	/**
	 * 笔记
	 */
	public final static int NW_FLAG_NOTE = 1;
	/**
	 * 文内注
	 */
	public final static int NW_FLAG_INNERNOTE = 2;

	private final static String KEY_SHARESOURCE = "share_source";
	private final static String KEY_SHARENOTE = "share_note";

	private Context mContext;
	private View mParent;

	private FloatingWindow mFloatWindow;
	private NoteWindow mNoteWindow;
	private DictWindow mDictWindow;
	private ReaderTextSearchWindow mSearchWindow;
	private ReaderTextSearchResultWindow mSearchResultWindow;
	private AudioWindow mAudioWindow;

	private float mDensity = 1;
	private boolean mIsPdf = false;
	// private int mScreenHeight;

//	private ShareUtil mShareUtil;
	
	private IFloatingOperation mOperationCallback;

	private int mFloatWindowTop = 0;
	private int mFloatWindowBottom = 0;

	public GlobalWindow(Context context, View parent) {
		windowHandler = new MyHandler(this);
		init(context, parent);		
	}

	private void init(Context context, View parent) {
		mContext = context;
		mParent = parent;

		mDensity = DRUiUtility.getDensity();
		// mScreenHeight = DRUiUtility.getScreenHeight();

		mFloatWindow = new FloatingWindow(mContext, mParent);
		mFloatWindow.setDrawLineColor(ReadConfig.getConfig().getNoteDrawLineColor());
		mNoteWindow = new NoteWindow(mContext, mParent);
		mDictWindow = new DictWindow(mContext, mParent);
		mSearchWindow = new ReaderTextSearchWindow(mContext, mParent);
		mSearchResultWindow = new ReaderTextSearchResultWindow(mContext,
				mParent);
	}

	public void showFloatingWindow(int currentX, int currentY, int minY,
			int maxY, boolean isShowDelete, boolean showDict,boolean isShowCorrect, int drawLineColor) {
		showFloatingWindowInner(currentX, currentY, minY, maxY, isShowDelete,
				showDict,isShowCorrect, drawLineColor);
	}

	private void showFloatingWindowInner(int currentX, int currentY, int minY,
			int maxY, boolean isShowDelete, boolean showDict,boolean isShowCorrect, int drawLineColor) {
		int height = (int) (60 * mDensity);
		int top = (int) (10 * mDensity);
		int bottom = (int) (20 * mDensity);
		int hideHeight = (int) (44.5 * mDensity);

		ReadConfig readConfig = ReadConfig.getConfig();
		if (!readConfig.isFullScreen()) {
			top += DRUiUtility.getStatusHeight(mContext);
		}
		int screenHeight = readConfig.getReadHeight();
		boolean up = true;
		int y = screenHeight / 2;
		if ((minY - height - hideHeight) <= top) {
			if (maxY + height < (screenHeight - bottom)) {
				y = maxY + bottom;
			}
			up = true;
		} else {
			y = minY - height - top;
			up = false;
		}
		int x = 0;

		mFloatWindow.setDrawLineOrDelete(isShowDelete);
		mFloatWindow.setCurSelectDrawLineColorIDByColor(drawLineColor);
		mFloatWindow.show(currentX, currentY, x, y, up);

		mFloatWindowTop = y;
		mFloatWindowBottom = y + (int) (90 * mDensity);
	}

	public void hideFloatingWindow() {
		if (mFloatWindow != null) {
			mFloatWindow.hide();
		}
	}

	public boolean isShowingFloating() {
		return mFloatWindow != null && mFloatWindow.isShowing();
	}

	public void setFloatingOperation(IFloatingOperation l) {
		mOperationCallback = l;
		mFloatWindow.setFloatingOperation(l);
	}

	public void setNoteWindowOperation(INoteWindowOperation l) {
		mNoteWindow.setNoteWindowOperation(l);
	}

	public void showNoteWindow(int x, int y, String content, int flag) {
		mNoteWindow.show(x, y, content, flag);
	}

	public void hideNoteWindow() {
		if (mNoteWindow != null) {
			mNoteWindow.hide();
		}
	}

	public boolean isShowingNote() {
		return mNoteWindow != null && mNoteWindow.isShowing();
	}

	public boolean isShowingWindow() {
		return isShowingFloating() || isShowingNote() || isShowingDict()
				|| isShowingSearchResultWindow()
				|| isShowingReaderTextSearchWindow() || isShowingAudio();
	}

	public void showDictWindow(String word, String explain, int x, int y) {
		mDictWindow.setDict(word, Html.fromHtml(explain), true);
		mDictWindow.show(x, y, mFloatWindowTop, mFloatWindowBottom);
	}

	public void hideDictWindow() {
		if (mDictWindow != null) {
			mDictWindow.hide();
		}
	}

	public boolean isShowingDict() {
		return mDictWindow != null && mDictWindow.isShowing();
	}

	public void setOnDismissCallBack(IOnDisMissCallBack onDisMissCallBack) {
		mSearchResultWindow.setOnDismissCallBack(onDisMissCallBack);
	}

	public void setDictOperation(IDictOperation dictOperation) {
		mDictWindow.setDictOperation(dictOperation);
	}

	public void showReaderTextSearchWindow() {
		mSearchWindow.show();
	}

	public void hideReaderTextSearchWindow(boolean stopSearch) {
		if (mSearchWindow != null) {
			mSearchWindow.hide(stopSearch);
		}
	}

	public boolean isShowingReaderTextSearchWindow() {
		return mSearchWindow != null && mSearchWindow.isShowing();
	}

	public void setReaderTextSearchOperation(
			IReaderTextSearchOperation readerTextSearchOperation) {
		mSearchWindow.setReaderTextSearchOperation(readerTextSearchOperation);
	}

	public void showReaderTextSearchResultWindow(String word,
			boolean resetData, boolean isFullScreen, boolean resetEditText) {
		mSearchResultWindow.show(resetData, isFullScreen, resetEditText);
		mSearchResultWindow.startSearch(word);
	}

	public void showReaderTextSearchResultWindow(String word, boolean resetData) {
		mSearchResultWindow.show(resetData);
		mSearchResultWindow.startSearch(word);
	}

	public void hideReaderTextSearchResultWindow() {
		if (mSearchResultWindow != null) {
			mSearchResultWindow.hide();
		}
	}

	public boolean isShowingSearchResultWindow() {
		return mSearchResultWindow != null && mSearchResultWindow.isShowing();
	}

	public void setReaderTextSearchResultOperation(
			IReaderTextSearchResultOperation readerTextSearchResultOperation) {
		mSearchResultWindow
				.setReaderTextSearchResultOperation(readerTextSearchResultOperation);
	}

	public OneSearch getOneSearch(boolean isPre) {
		return mSearchResultWindow.getOneSearch(isPre);
	}

	public void checkStopSearch() {
		mSearchResultWindow.checkStopSearch();
	}

	public void showAudio(int x, int y, Rect imgRect, String innerPath,
			String localPath, int bookType) {
		if (mAudioWindow == null) {
			mAudioWindow = new AudioWindow(mContext, mParent);
		}
		mAudioWindow.show(x, y, imgRect, innerPath, localPath, bookType);
	}

	public void hideAudio() {
		if (mAudioWindow != null) {
			mAudioWindow.hide();
		}
	}

	public boolean isShowingAudio() {
		return mAudioWindow != null && mAudioWindow.isShowing();
	}

	public void stopAudio() {
		printLog(" stopAudio " + mAudioWindow);
		if (mAudioWindow != null) {
			mAudioWindow.destroy();
		}
	}

	public void hideWindow(boolean bHideFloatWindow) {
		if (bHideFloatWindow)
			hideFloatingWindow();
		hideNoteWindow();
		hideDictWindow();
		hideReaderTextSearchWindow(true);
		hideReaderTextSearchResultWindow();
		hideAudio();
	}

	public void showShare(String productId, String bookDir, String bookName,
			String sourceText, String sourceTextAsHtml, String noteText, long noteTime, String bookCover,
			String author, final boolean isAdd, final int bookType) {
//		int type = 0;
//		switch(bookType){
//		case BaseJniWarp.BOOKTYPE_DD_PDF:
//		case BaseJniWarp.BOOKTYPE_DD_TXT:
//		case BaseJniWarp.BOOKTYPE_THIRD_EPUB:
//			type = 0;
//			break;
//		case BaseJniWarp.BOOKTYPE_DD_DRM_HTML:
//			type = 1;
//			break;
//		case BaseJniWarp.BOOKTYPE_DD_DRM_EPUB:
//			type = 2;
//			break;
//		}
//		final DDShareData shareData = getDDShareData(productId, bookName,
//				bookDir, sourceText, sourceTextAsHtml, noteText, noteTime, bookCover, author, type);
//		if(mShareUtil == null)
//			mShareUtil = new ShareUtil((Activity) mContext);
//		mShareUtil.share(shareData,
//                getDDStatisticsData(productId, bookName, sourceText, noteText),
//                new ShareListener() {
//                    @Override
//                    public void onShareStart() {
//
//                    }
//
//                    @Override
//                    public void onShareComplete(Object result, ShareData data) {
//                        if (mIsPdf) {
//                            printLog(" share callback pdf=true ");
//                            return;
//                        }
//                        callbackToNote(isAdd, shareData.getNote());
//                    }
//
//                    @Override
//                    public void onShareCancel() {
//                        cancelShare();
//                    }
//
//                    @Override
//                    public void onShareError(Exception e) {
//
//                    }
//                });
//		/*
//		 * if (mShareWindow == null) { mShareWindow = new
//		 * SharePopupWindow((Activity) mContext, mParent); } DDShareData
//		 * shareData = getDDShareData(productId, bookName, bookDir, sourceText,
//		 * noteText, bookCover, author); mShareWindow.setShareData(shareData,
//		 * getDDStatisticsData(productId, bookName, sourceText, noteText));
//		 * ShareCallbackUtil.getInstance().setDataAndCallbak(shareData, new
//		 * ShareCallback() {
//		 *
//		 * @Override public void onCallBack(boolean isShareSuccess, DDShareData
//		 * shareData) { // printLog(" shareCallback succ=" + isShareSuccess + //
//		 * "," + shareData.getNote() + ", isAdd=" + isAdd); if(mIsPdf){
//		 * printLog(" share callback pdf=true "); return; } if (isShareSuccess)
//		 * { callbackToNote(isAdd, shareData.getNote()); } }
//		 *
//		 * @Override public void onCancel() { cancelShare(); } });
//		 *
//		 * mShareWindow.showOrHideShareMenu(); if
//		 * (mShareWindow.isShowShareMenu()) {
//		 *
//		 * }
//		 */
	}

	private void callbackToNote(boolean isAdd, String noteText) {
		if (mOperationCallback != null) {

			Message msg = windowHandler.obtainMessage();
			msg.what = MSG_SHARE_CALLBACK;
			Bundle data = new Bundle();
			data.putBoolean(KEY_SHARESOURCE, isAdd);
			data.putString(KEY_SHARENOTE, noteText);
			msg.setData(data);

			windowHandler.sendMessage(msg);
		}
	}

	private void cancelShare() {
		if (mOperationCallback != null) {
			mOperationCallback.onCancelShare();
		}
	}

//	private DDShareData getDDShareData(String productId, String bookName,
//			String bookDir, String sourceText, String sourceTextAsHtml, String noteText, long noteTime,
//			String bookCover, String author, int bookType) {
//		DDShareData data = new DDShareData();
//		if (Utils.isStringEmpty(noteText)) {
//			data.setShareType(DDShareData.SHARE_TYPE_LINE);
//		} else {
//			data.setShareType(DDShareData.SHARE_TYPE_NOTE);
//			data.setNote(noteText);
//		}
//		if (getReadInfo().getEBookType() != BaseJniWarp.BOOKTYPE_DD_PDF)
//			data.setShareType(DDShareData.SHARE_TYPE_BOOKNOTE_IMAGE);
//		data.setAuthor(author);
//		data.setTitle(bookName);
//		data.setBookName(bookName);
//		data.setLineationContent(sourceText);
//		data.setHtmlContent(sourceTextAsHtml);
//		data.setNoteTime(noteTime);
//		data.setBookCover(bookCover);
//		data.setBookDir(bookDir);
//		data.setBookId(productId);
//		bookCover = ImageConfig.getBookCoverBySize(bookCover,ImageConfig.IMAGE_SIZE_CC);
//		data.setPicUrl(bookCover);
//		data.setTargetUrl(DDShareData.DDREADER_BOOK_DETAIL_LINK);
//        DDShareParams params = new DDShareParams();
//        params.setSaleId(productId);
//        params.setMediaId(productId);
//        if (getReadInfo() instanceof PartReadInfo) {
//            PartReadInfo partReadInfo = (PartReadInfo) getReadInfo();
//            params.setSaleId(partReadInfo.getSaleId());
//			data.setMediaType(1);
//		} else
//			data.setMediaType(2);
//        data.setParams(JSON.toJSONString(params));
//		return data;
//	}
    private BaseReadInfo getReadInfo(){
        return ReaderAppImpl.getApp().getReadInfo();
    }
//	private DDStatisticsData getDDStatisticsData(String productId,
//			String bookName, String sourceText, String noteText) {
//		DDStatisticsData data = null;
//		if (Utils.isStringEmpty(noteText)) {
//			data = new DDStatisticsData(DDShareData.SHARE_TYPE_LINE);
//		} else {
//			data = new DDStatisticsData(DDShareData.SHARE_TYPE_NOTE);
//			data.setNote(noteText);
//		}
//
//		data.setLineationContent(sourceText);
//		data.setProductId(productId);
//		data.setBookName(bookName);
//
//		return data;
//	}

//	private String getPicUrl(String pid, String bookDir) {
//		return ShareUtil.getPicUrl(pid, bookDir);
//	}

	public void toShareActivityResult(int requestCode, int resultCode,
			Intent data) {
		/*
		 * if (mShareWindow != null) {
		 * mShareWindow.mTencentWeiBoInfo.onActivityResult(requestCode,
		 * resultCode, data); } else {
		 * printLogE(" getOnActivityResult shareWindow == null"); }
		 */
	}

	public void initIsPdf(boolean isPdf) {
		mIsPdf = isPdf;
		mFloatWindow.initIsPdf(isPdf);
		mDictWindow.initIsPdf(isPdf);
	}

	protected void printLog(String log) {
		LogM.i(getClass().getSimpleName(), log);
	}

	protected void printLogE(String log) {
		LogM.e(getClass().getSimpleName(), log);
	}

	private final static int MSG_SHARE_CALLBACK = 0x1;

	private Handler windowHandler;

	private void dealMsg(Message msg){
		switch (msg.what) {
		case MSG_SHARE_CALLBACK:
			Bundle data = msg.getData();
			boolean isAdd = data.getBoolean(KEY_SHARESOURCE);
			String note = data.getString(KEY_SHARENOTE);
			mOperationCallback.onMarkSelected(isAdd, note, -1, false);
			break;
		default:
			break;
		}
	}
	
	private static class MyHandler extends Handler {
		private final WeakReference<GlobalWindow> mFragmentView;

		MyHandler(GlobalWindow view) {
			this.mFragmentView = new WeakReference<GlobalWindow>(view);
		}

		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			GlobalWindow service = mFragmentView.get();
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
	
	public static interface IFloatingOperation {

		public void onCopy();
		public void chackDir();

		/**
		 * 即划线
		 * 
		 * @param isAdd
		 *            true 增加 , false 修改
		 * @param note
		 */
		public void onMarkSelected(boolean isAdd, String note, int drawLineColor, boolean isOnlyChangeColor);

		public void onNote(boolean isAdd);

		public void onDelete();

		public void onCancelShare();

		public void onSetCurDrawLineColor(int color);
		public void onShare();
		public String getWord();
	}

	public static interface INoteWindowOperation {

		public void onClick(int flag);

	}

	public static interface IDictOperation {

		public void onDictNote(String word, String explain);

		public void onYoudao(String word);

		public void onBaidu(String word);

	}

	public static interface IReaderTextSearchOperation {

		public void gotoPageOnSearch(boolean isPre);

		public void showSearchResult();

		public void hide(boolean abortSearch);

	}

	public static interface IReaderTextSearchResultOperation {

		public void gotoPageOnSearch(Chapter chapter,
				ElementIndex wordStartIndex, ElementIndex wordEndIndex);

		public void dismissSearchResultWindow();

		public void doSearch(String searchText);

	}

}
