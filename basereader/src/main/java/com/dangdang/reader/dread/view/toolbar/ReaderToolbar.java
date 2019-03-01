package com.dangdang.reader.dread.view.toolbar;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.dangdang.reader.R;
import com.dangdang.reader.db.service.DDStatisticsService;
import com.dangdang.reader.dread.PubReadActivity;
import com.dangdang.reader.dread.config.ReadConfig;
import com.dangdang.reader.dread.core.epub.IGlobalWindow;
import com.dangdang.reader.dread.format.Book;
import com.dangdang.reader.dread.format.Book.BaseNavPoint;
import com.dangdang.reader.dread.view.FontDownloadDialog;
import com.dangdang.reader.view.MyPopupWindow;
import com.dangdang.zframework.utils.DRUiUtility;
import com.mx.mxbase.constant.APPLog;

import java.lang.ref.WeakReference;
import java.util.List;

public class ReaderToolbar {

	private Context mContext;
	private View mRoot;
	private int mY;
	private int mBottomBarHeight;
	private int mFontHintHeight;
	private Book mBook;
	private boolean mBookLoadingFinished;

	private static final int ANIM_SHOW_BASE = 1;
	private static final int ANIM_HIDE_BASE = 2;
	private static final int ANIM_SHOW_DETAIL = 3;
	private static final int ANIM_HIDE_DETAIL = 4;
	private static final int ANIM_SHOW_BARCOMMENT = 5;
	private static final int ANIM_HIDE_BARCOMMENT = 6;
	private int mAnimType; // the next animation to play

	private TopToolbar mTopBar;
	private PopupWindow mTopBarPopup;
	private BottomToolbar mBottomBar;
	private PopupWindow mBottomBarPopup;
	//设置显示pop
	private DetailSettingToolbar mDetailBar;
	/**
	 * 详细设置
	 */
	private PopupWindow mDetailBarPopup;
	private DirPreviewToolbar mPreviewBar;
	private PopupWindow mPreviewBarPopup;
	private FontHint mFontHint;
	private PopupWindow mFontHintPopup;
	private FontDownloadDialog mFontDialog1;
//	private BarCommentDialogFragment mBarCommentDlg;
	private IGlobalWindow.IOnDisMissCallBack mOnDisMissCallBack;
	private LinearLayout mTopMoreLayout;
	private PopupWindow mTopMorePopup;
	private InputMethodManager mInputMethodManager;

	private boolean mIsShowing;
	private OnClickListener mClickListener;

	public ReaderToolbar(Context context, View root, Book book) {
		handler = new MyHandler(this);
		mContext = context;
		mRoot = root;
		mBook = book;
		mBookLoadingFinished = false;
		initView();
	}

	private void initView() {
		mY = DRUiUtility.getPadScreenIsLarge() ? 0 : DRUiUtility
				.getUiUtilityInstance().getStatusHeight(mContext);
		LayoutInflater inflater = LayoutInflater.from(mContext);
		final Resources r = mContext.getResources();

		mTopBar = (TopToolbar) inflater.inflate(R.layout.reader_top_toolbar,
				null);
		mTopBarPopup = new MyPopupWindow(mTopBar, LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT);
		mTopBarPopup.setAnimationStyle(R.style.mypopwindow_anim_style);
		mTopBarPopupHeight = r
				.getDimensionPixelSize(R.dimen.reader_top_toolbar_height);
		mTopBarPopup.setHeight(mTopBarPopupHeight);

		mBottomPopView = (BottomMorePopView) inflater.inflate(
				R.layout.reader_bottom_more_pop_window, null);
		mBottomMorePopupWindow = new MyPopupWindow(mBottomPopView,
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		hideBottomMoreWindow();

		mBottomBar = (BottomToolbar) inflater.inflate(
				R.layout.reader_bottom_toolbar, null);
		mBottomBar.setToolbarListener(mToolbarListener);
		mBottomBarPopup = new MyPopupWindow(mBottomBar, LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT);
		mBottomBarPopup.setAnimationStyle(R.style.mypopwindow_anim_style);
		mBottomBarHeight = r
				.getDimensionPixelSize(R.dimen.reader_bottom_toolbar_height);
		mBottomBarPopup.setHeight(mBottomBarHeight);


		//设置主界面显示
		mDetailBar = (DetailSettingToolbar) inflater.inflate(
				R.layout.reader_detail_toolbar, null);
		mDetailBarPopup = new MyPopupWindow(mDetailBar, LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT);
		mDetailBarPopup.setAnimationStyle(R.style.mypopwindow_anim_style);
		mDetailBarPopup.setHeight(r
				.getDimensionPixelSize(R.dimen.reader_detail_toolbar_height));

		//
		mPreviewBar = (DirPreviewToolbar) inflater.inflate(
				R.layout.reader_preview_toolbar, null);
		mPreviewBarPopup = new MyPopupWindow(mPreviewBar,
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		mPreviewBarPopup.setAnimationStyle(R.style.mypopwindow_anim_style);

		mFontHint = (FontHint) inflater
				.inflate(R.layout.reader_font_hint, null);
		mFontHint.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DDStatisticsService.getDDStatisticsService(mContext).addData(
						DDStatisticsService.DOWNLOAD_FREE_FONTS,
						DDStatisticsService.OPerateTime,
						System.currentTimeMillis() + "");
				mFontHintPopup.dismiss();
				ReadConfig.getConfig().setShowFontHint(false);
				mFontDialog1 = new FontDownloadDialog(mContext);
				mFontDialog1.setOnRightClickListener(mClickListener);
				mFontDialog1.show();
			}
		});
		mFontHintPopup = new MyPopupWindow(mFontHint, LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT);
		mFontHintPopup.setAnimationStyle(R.style.mypopwindow_anim_style);
		mFontHintHeight = r.getDimensionPixelSize(R.dimen.reader_font_hint_height);
//		mFontHintPopup.setHeight(r
//				.getDimensionPixelSize(R.dimen.reader_font_hint_height));

//		mBarCommentDlg = new BarCommentDialogFragment();
//		mBarCommentDlg.setToobar(this, mContext);

		mTopMoreLayout = (LinearLayout) inflater.inflate(R.layout.reader_top_toolbar_more, null);
		mTopMorePopup = new MyPopupWindow(mTopMoreLayout, LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		mTopMorePopup.setAnimationStyle(R.style.mypopwindow_anim_style);

		mAnimType = ANIM_SHOW_BASE;
	}
	

	public boolean isReadBottomMenuShowing(){
		return mBottomBarPopup.isShowing();
	}
	
	public void switchShowing() {
		try{
			switchPopShowing();
		}catch(Throwable e){
			e.printStackTrace();
		}
	}
	
	private void switchPopShowing() {
		mTopBar.clearAnimation();
		mBottomBar.clearAnimation();
		mDetailBar.clearAnimation();
		mTopMoreLayout.clearAnimation();

		Log.e("mAnimType",mAnimType+"");

		switch (mAnimType) {
		case ANIM_HIDE_BASE:
//			((Activity) mContext)
//					.getParent()
//					.getWindow()
//					.clearFlags(
//							WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
			mTopMorePopup.dismiss();
			mTopBarPopup.dismiss();
			mBottomBarPopup.dismiss();
			mFontHintPopup.dismiss();
			hideTopMoreWindow();
			hideBottomMoreWindow();
			mIsShowing = false;
			mAnimType = ANIM_SHOW_BASE;
			break;
		case ANIM_SHOW_BASE:
			// mBottomBar.updateProgress(pageIndexInBook, pageSize);
//			((Activity) mContext)
//					.getParent()
//					.getWindow()
//					.addFlags(
//							WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
			mTopBarPopup.showAtLocation(mRoot, Gravity.TOP, 0, mY);
			{
				mBottomBarPopup.showAtLocation(mRoot, Gravity.BOTTOM, 0, 0);
				if (ReadConfig.getConfig().isShowFontHint())
					mFontHintPopup.showAtLocation(mRoot, Gravity.BOTTOM, 0,
							mBottomBarHeight);
				showTopMoreWindow();
				showBottomMoreWindow();
			}
			mIsShowing = true;
			mAnimType = ANIM_HIDE_BASE;
			break;
		case ANIM_SHOW_DETAIL:
//			((Activity) mContext)
//					.getParent()
//					.getWindow()
//					.clearFlags(
//							WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
			mTopMorePopup.dismiss();
			mTopBarPopup.dismiss();
			mBottomBarPopup.dismiss();
			mFontHintPopup.dismiss();
			hideTopMoreWindow();
			hideBottomMoreWindow();
			mIsShowing = true;
			mDetailBarPopup.showAtLocation(mRoot, Gravity.BOTTOM, 0, 0);
			mAnimType = ANIM_HIDE_DETAIL;
			break;
		case ANIM_HIDE_DETAIL:
//			((Activity) mContext)
//					.getParent()
//					.getWindow()
//					.clearFlags(
//							WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
			mDetailBarPopup.dismiss();
			mIsShowing = false;
			mAnimType = ANIM_SHOW_BASE;
			break;
		case ANIM_SHOW_BARCOMMENT:
//			((Activity) mContext)
//					.getParent()
//					.getWindow()
//					.clearFlags(
//							WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
			mTopMorePopup.dismiss();
			mTopBarPopup.dismiss();
			mBottomBarPopup.dismiss();
			mFontHintPopup.dismiss();
			hideTopMoreWindow();
			hideBottomMoreWindow();
			mIsShowing = true;
			showBarCommentWindow();
			mAnimType = ANIM_HIDE_BARCOMMENT;
			break;
		case ANIM_HIDE_BARCOMMENT:
//			((Activity) mContext)
//					.getParent()
//					.getWindow()
//					.clearFlags(
//							WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
//			mBarCommentPopup.dismiss();
//			mBarCommentDlg.dismiss();
			mIsShowing = false;
			mAnimType = ANIM_SHOW_BASE;
			break;
		default:
			break;
		}
	}

	public void hideTopMoreWindow() {
		handler.removeMessages(MSG_TTS_ANIM);
	}

	public void showTopMoreWindow() {
		handler.removeMessages(MSG_TTS_ANIM);
//		mTopMorePopupWindow.showAtLocation(mRoot, Gravity.TOP
//				| Gravity.RIGHT, 0, mY + mTopBarPopupHeight);
	}

	public void hideBottomMoreWindow() {
		mBottomMorePopupWindow.dismiss();
	}

	public void showBottomMoreWindow() {
		if (mCanShowBottomPopView) {
			int height = mBottomBarHeight;
			if (ReadConfig.getConfig().isShowFontHint())
				height += mFontHintHeight;
			mBottomMorePopupWindow.showAtLocation(mRoot, Gravity.BOTTOM | Gravity.LEFT
					, (int)(DRUiUtility.getDensity() * 22.5), height + (int)(DRUiUtility.getDensity() * 10));
		}
	}

//	public void hideBarCommentWindow() {
//		hideInputMethodService();
//		mBarCommentDlg.dismiss();
//	}

	public void showTopToolBarMore() {
//		mTopMorePopup.showAsDropDown(mTopBar.getViewMore(), 0, (mTopBarPopupHeight - mTopBar.getViewMore().getHeight()) / 2);
		if (mTopMorePopup.isShowing())
			mTopMorePopup.dismiss();
		else
			mTopMorePopup.showAtLocation(mRoot, Gravity.TOP
					| Gravity.RIGHT, 0, mY + mTopBarPopupHeight);
	}

	public void showBarCommentWindow() {
//		FragmentTransaction transaction = ((ReadActivity)mContext).getSupportFragmentManager().beginTransaction();
//		transaction.add(mBarCommentDlg, "BarCommentDialog");
//		transaction.commitAllowingStateLoss();
////		showBarCommentInputMethodService();
//
////		mBarCommentPopup.showAtLocation(mRoot, Gravity.BOTTOM, 0, 0);
////		showBarCommentInputMethodService();
	}


	public void startTTSAnim(){
		if(isShowing()){
			return;
		}
		handler.sendEmptyMessageDelayed(MSG_TTS_ANIM, 2000);
	}

	public void setListeners(OnClickListener clickListener,
			OnClickListener fontListener, OnClickListener bgListener,
			OnSeekBarChangeListener lightListener,
			onProgressBarChangeListener progressListener,
			PubReadActivity.OnBookMarkListener markListener,
			PubReadActivity.OnBookFollowListener followListener,
			OnSeekBarChangeListener ttsSeekBarChangeListener,
			OnClickListener onPlusLocalTtsChangeListener,
			OnClickListener onTtsButtonClickListener) {
		mTopBar.setButtonClickListener(clickListener);
		mTopBar.setMarkListener(markListener);
		mTopBar.setFollowListener(followListener);
		mBottomBar.setButtonClickListener(clickListener);
		mBottomBar.setOnProgressBarChangeListener(progressListener);
		mBottomPopView.setButtonClickListener(clickListener);
		mDetailBar.setFontClickListener(fontListener);
		mDetailBar.setLightSeekListener(lightListener);
		mDetailBar.setBgClickListener(bgListener);
		mDetailBar.setToolbarListener(mToolbarListener);
		mTopMoreLayout.findViewById(R.id.read_top_more_share).setOnClickListener(clickListener);
		mTopMoreLayout.findViewById(R.id.read_top_more_stoptraining).setOnClickListener(clickListener);
//		mBarCommentDlg.setOnClickListener(clickListener);
		mClickListener = clickListener;
	}

	public void setOnDismissCallBack(IGlobalWindow.IOnDisMissCallBack onDismissCallBack) {
		this.mOnDisMissCallBack = onDismissCallBack;
	}

	public boolean isShowing() {
		return mIsShowing;
	}

	public void setBookLoadingStart() {
		mBottomBar.setBookLoadingStart();
	}


	public void setBookLoadingFinish() {
		mBookLoadingFinished = true;
		mBottomBar.setBookLoadingFinish();
		List<BaseNavPoint> list = mBook.getAllNavPointList();
		if (list != null && list.size() > 0) {
			mPreviewBarPopup.setHeight(mContext.getResources()
					.getDimensionPixelSize(
							R.dimen.reader_preview_toolbar_height));
		} else {
			mPreviewBarPopup.setHeight(mContext.getResources()
					.getDimensionPixelSize(
							R.dimen.reader_preview_toolbar_item_height));
		}
		mPreviewBar.initData(list);
	}

	public void updateProgress(int pageIndexInBook, int pageSize) {
		mBottomBar.updateProgress(pageIndexInBook, pageSize);
		mPreviewBar.setPageSize(pageSize);
	}
	
	public void updateRecompProgress(int curr, int total){
		mBottomBar.updateRecompProgress(curr, total);
	}

	public void setSyncButtonStatus(boolean cloudSyncFinish,
			boolean cloudSyncBaseCondition) {
		// TODO Auto-generated method stub

	}

	public void destory(){
		handler.removeMessages(MSG_TTS_ANIM);
	}

	public interface ToolbarListener {
		public void showDetailSettingBar();

		public void showDirPreviewBar(int pageIndex);

		public void hideDirPreviewBar();

		public void scrollDirPreviewBar(int pageIndex);

		public void switchToolbarShowing();

	}
	public void scroolPage(int page){
		mToolbarListener.scrollDirPreviewBar(page);
	}

	private ToolbarListener mToolbarListener = new ToolbarListener() {

		@Override
		public void showDirPreviewBar(int pageIndex) {
			if (mBookLoadingFinished) {
				BaseNavPoint point = mBook.getNavPoint(pageIndex);
				mPreviewBar.setCurrentPosition(point);
				mPreviewBarPopup.showAtLocation(mRoot, Gravity.BOTTOM, 0,
						mBottomBarHeight);
				mTopMorePopup.dismiss();
				mTopBarPopup.dismiss();
				hideTopMoreWindow();
				hideBottomMoreWindow();
//				((Activity) mContext)
//						.getParent()
//						.getWindow()
//						.clearFlags(
//								WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
			}
		}

		@Override
		public void showDetailSettingBar() {
			mAnimType = ANIM_SHOW_DETAIL;
			switchToolbarShowing();
		}

		@Override
		public void hideDirPreviewBar() {
			mPreviewBarPopup.dismiss();
		}

		@Override
		public void scrollDirPreviewBar(int pageIndex) {
			BaseNavPoint point = mBook.getNavPoint(pageIndex);
			mPreviewBar.scrollToIndex(point, pageIndex);
		}

		@Override
		public void switchToolbarShowing() {
			APPLog.e("点击这里");
			switchShowing();
		}
	};

	/**
	 * 获得当前章节
	 * @param pageIndex
	 * @return
     */
	public BaseNavPoint getNavPoint(int pageIndex){
		BaseNavPoint point = mBook.getNavPoint(pageIndex);
		return point;
	}
	
	private PopupWindow mBottomMorePopupWindow;
	private BottomMorePopView mBottomPopView;
	private boolean mCanShowBottomPopView = false;

	private int mTopBarPopupHeight;

	public interface onProgressBarChangeListener {
		void onProgressBarChangeEnd(int progress);
	}

	public void updateCurReadProgressRate(float finishRate) {
		if (mBottomPopView != null)
			mBottomPopView.updateCurReadProgressRate(finishRate);
	}

	public boolean isCanShowBottomPopView() {
		return mCanShowBottomPopView;
	}

	public void setCanShowBottomPopView(boolean mCanShowBottomPopView) {
		this.mCanShowBottomPopView = mCanShowBottomPopView;
	}

//	public void hideInputMethodService() {
//		mInputMethodManager = (InputMethodManager) mContext
//				.getSystemService(Context.INPUT_METHOD_SERVICE);
//		mInputMethodManager.hideSoftInputFromWindow(mBarCommentDlg.getEditView().getWindowToken(),
//				0);
//	}

	private void showBarCommentInputMethodService() {
		mInputMethodManager = (InputMethodManager) mContext
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		mInputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
	}

//	public void processPickPhote(List<String> pathList, boolean bOrig) {
//		mBarCommentDlg.processPickPhote(pathList, bOrig);
//	}
//
//	public void processTakePhote(String imgPath) {
//		mBarCommentDlg.processTakePhote(imgPath);
//	}

//	public String getCommentText() {
//		return mBarCommentDlg.getText();
//	}
//
//	public int getCommentRating() {
//		return mBarCommentDlg.getRating();
//	}

	public void resetShowState() {
		mIsShowing = false;
		mAnimType = ANIM_SHOW_BASE;

	}

//	public ArrayList<String> getCommentImgList() {
//		return mBarCommentDlg.getImgList();
//	}
//
//	public boolean isCommentImgOrig() {
//		return mBarCommentDlg.isOrig();
//	}
//
//	public void clearBarComment() { mBarCommentDlg.clear(); }
//	public void clearBarCommentText() { mBarCommentDlg.clearText(); }

	public final static int MSG_TTS_ANIM = 1;
	private Handler handler;
	
	private static class MyHandler extends Handler {
		private final WeakReference<ReaderToolbar> mFragmentView;

		MyHandler(ReaderToolbar view) {
			this.mFragmentView = new WeakReference<ReaderToolbar>(view);
		}

		@Override
		public void handleMessage(Message msg) {
			ReaderToolbar service = mFragmentView.get();
			if (service != null) {
				super.handleMessage(msg);
				try {
					switch (msg.what) {
					case MSG_TTS_ANIM:
						break;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
}