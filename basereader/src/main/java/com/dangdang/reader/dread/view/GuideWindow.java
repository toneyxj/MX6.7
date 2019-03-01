package com.dangdang.reader.dread.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.dangdang.reader.R;
import com.dangdang.reader.dread.core.epub.ReaderAppImpl;
import com.dangdang.reader.dread.data.ReadInfo;
import com.dangdang.reader.utils.FirstGuideManager;
import com.dangdang.reader.view.MyPopupWindow;
import com.dangdang.zframework.log.LogM;
import com.dangdang.zframework.utils.DRUiUtility;

import java.lang.ref.WeakReference;

public class GuideWindow {

	private Context mContext;
	private LayoutInflater mInflater;
	private View mParentView;

	private PopupWindow mPopupWindow;

	private OnClickListener mClickListener;
	private OnClickListener mClickCallback;
	private FirstGuideManager mGuideManager;
	private Handler mHandler;

	public GuideWindow(Context context, View parent) {
		super();
		mContext = context;
		mParentView = parent;
		mInflater = LayoutInflater.from(mContext);

		mHandler = new MyHandler(this);
		mGuideManager = FirstGuideManager.getInstance(mContext);
		mClickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				hideMenu();
				if(mClickCallback != null){
					mClickCallback.onClick(v);
				}
			}
		};
	}

	public boolean isShow() {
//		return mPopupWindow != null && mPopupWindow.isShowing();
		return true;
	}

	public void showReadGuide() {
		if (isShow())
			return;
		if (isFirstReadBook()) {
			mGuideManager.setFirstGuide(FirstGuideManager.FirstGuideTag.IS_FIRST_READ_BOOK, false);
			View view = mInflater.inflate(R.layout.reader_guide_layout_read, null);
			View button = view.findViewById(R.id.guide_close_button);
			button.setOnClickListener(mClickListener);
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) button.getLayoutParams();
			params.topMargin = (int) (DRUiUtility.getScreenHeight() * 0.75f);
			View text = view.findViewById(R.id.reader_guide_text);
			((RelativeLayout.LayoutParams) text.getLayoutParams()).topMargin = (int) (DRUiUtility.getScreenHeight() * 0.4f);
			showMenu(view);
		}
	}

	public boolean isFirstReadBook() {
//		return mGuideManager.isFirstGuide(FirstGuideManager.FirstGuideTag.IS_FIRST_READ_BOOK);
		return false;
	}

	public void showDirGuide() {
		if (isShow())
			return;
		if (mGuideManager.isFirstGuide(FirstGuideManager.FirstGuideTag.IS_FIRST_TOUCH_CONTENT)) {
			mGuideManager.setFirstGuide(FirstGuideManager.FirstGuideTag.IS_FIRST_TOUCH_CONTENT, false);
			View view = mInflater.inflate(R.layout.reader_guide_layout_dir, null);
			view.setOnClickListener(mClickListener);
			showMenu(view);
		}
	}

	public void showSettingGuide() {
		if (isShow())
			return;
		if (mGuideManager.isFirstGuide(FirstGuideManager.FirstGuideTag.IS_FIRST_READ_SETTING)) {
			mGuideManager.setFirstGuide(FirstGuideManager.FirstGuideTag.IS_FIRST_READ_SETTING, false);
			View view = mInflater.inflate(R.layout.reader_guide_layout_setting, null);
			view.setOnClickListener(mClickListener);
			View circle = view.findViewById(R.id.read_guide_setting_circle);
			((RelativeLayout.LayoutParams) circle.getLayoutParams()).leftMargin = calcSettingGuidePosition();
			showMenu(view);
		}
	}

	private int calcSettingGuidePosition() {
		ReaderAppImpl readerApps = ReaderAppImpl.getApp();
		ReadInfo readInfo = (ReadInfo) readerApps.getReadInfo();
		return (int) (DRUiUtility.getScreenWith() * 250f / 720);

	}

	public void showLightGuide() {
		if (isShow())
			return;
		if (mGuideManager.isFirstGuide(FirstGuideManager.FirstGuideTag.IS_FIRST_TOUCH_LIGHT)) {
			mGuideManager.setFirstGuide(FirstGuideManager.FirstGuideTag.IS_FIRST_TOUCH_LIGHT, false);
			View view = mInflater.inflate(R.layout.reader_guide_layout_light, null);
			view.setOnClickListener(mClickListener);
			showMenu(view);
		}
	}

	public void showLineGuide(int y) {
		if (isShow())
			return;
		if (mGuideManager.isFirstGuide(FirstGuideManager.FirstGuideTag.IS_FIRST_READ_LINE)) {
			mGuideManager.setFirstGuide(FirstGuideManager.FirstGuideTag.IS_FIRST_READ_LINE, false);
			View view = mInflater.inflate(R.layout.reader_guide_layout_line, null);
			View imageView = view.findViewById(R.id.reader_guide_line_image);
			int screenHeight = DRUiUtility.getScreenHeight();
			int topMargin = (int) ((screenHeight / 2 - 108 * DRUiUtility.getDensity()) / 2);
			if (y > screenHeight / 2) {
				((RelativeLayout.LayoutParams) imageView.getLayoutParams()).topMargin = topMargin;
			} else {
				((RelativeLayout.LayoutParams) imageView.getLayoutParams()).topMargin = screenHeight / 2 + topMargin;
			}
			mHandler.sendEmptyMessageDelayed(0, 4000);
			view.setOnClickListener(mClickListener);
			showMenu(view);
		}
	}

	private void showMenu(View view) {
		if (mPopupWindow != null && mPopupWindow.isShowing())
			mPopupWindow.dismiss();
		mPopupWindow = new MyPopupWindow(view, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		try {
			mPopupWindow.showAtLocation(mParentView, Gravity.NO_GRAVITY, 0, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void hideMenu() {
		mHandler.removeMessages(0);
		try {
			if (mPopupWindow != null && mPopupWindow.isShowing()) {
				mPopupWindow.dismiss();
			}
		} catch (Exception e) {
			pringLogE(" guidewindow hideMenu error, " + e);
		}
	}
	
	protected void pringLog(String log){
		LogM.i(getClass().getSimpleName(), log);
	}
	
	protected void pringLogE(String log){
		LogM.e(getClass().getSimpleName(), log);
	}
	
	public void setClickListener(View.OnClickListener l){
		mClickCallback = l;
	}
	
	private static class MyHandler extends Handler {
		private final WeakReference<GuideWindow> mFragmentView;

		MyHandler(GuideWindow view) {
			this.mFragmentView = new WeakReference<GuideWindow>(view);
		}

		@Override
		public void handleMessage(Message msg) {
			GuideWindow service = mFragmentView.get();
			if (service != null) {
				super.handleMessage(msg);
				try {
					service.hideMenu();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}