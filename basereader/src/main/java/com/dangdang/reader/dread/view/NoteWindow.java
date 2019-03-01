package com.dangdang.reader.dread.view;

import android.content.Context;
import android.graphics.Point;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.dangdang.reader.R;
import com.dangdang.reader.dread.config.ReadConfig;
import com.dangdang.reader.dread.core.epub.GlobalWindow;
import com.dangdang.reader.dread.core.epub.GlobalWindow.INoteWindowOperation;
import com.dangdang.reader.view.MyPopupWindow;
import com.dangdang.zframework.utils.DRUiUtility;
import com.dangdang.zframework.view.DDImageView;
import com.dangdang.zframework.view.DDTextView;

public class NoteWindow {

	private static final float ARROW_RES_WIDTH = 39;
	private Context mContext;
	private Point mFloatingPoint;
	private PopupWindow mWindow;
	private View mParent;

	private View mContentView;
	private DDImageView mArrowView;
	private DDTextView mNoteContent;
	private ScrollView mScrollView;
	private View mBackground;
	private RelativeLayout mRootView;

	private int mWindowHeight;
	private int mScrollViewWidth = 0;
	private int mScrollViewHeight = 0;
	private int mPopupWindowHeight = 0;
	private int left = 20;
	private int noteColor;
	private int innerNoteColor;

	private float mDensity = 1f;
	private int mScreenWidth = 0;
	//private int mScreenHeight = 0;

	private int windowLeft;
	private int arrowLeft;

	private INoteWindowOperation mCallback;

	public NoteWindow(Context context, View parent) {
		mContext = context;
		mParent = parent;
		mDensity = DRUiUtility.getDensity();
		
		final ReadConfig readConfig = ReadConfig.getConfig();
		mScreenWidth = readConfig.getReadWidth();
		//mScreenHeight = readConfig.getReadHeight();
		left = (int) (20 * mDensity);
		mWindowHeight = (int) (150 * mDensity);
		mFloatingPoint = new Point(0, 0);
		noteColor = context.getResources().getColor(R.color.white);
		innerNoteColor = context.getResources().getColor(R.color.black);
	}

	public void show(int x, int y, String content, int flag) {
		setFloatingPoint(x, y);
		initViewClickEvent(flag);
		String displayName = content;
		try {
			displayName = content.replaceAll("\\\\n","\n");
		} catch (Exception e) {
		}
		mNoteContent.setText(displayName);
		if (mWindow != null && mWindow.isShowing()) {
			mWindow.dismiss();
			return;
		}
		int bottom = (int) (16 * mDensity);
		int readHeight = ReadConfig.getConfig().getReadHeight();
		if (mFloatingPoint.y + mWindowHeight > (readHeight - bottom)) { // 太靠下了
			showPopupWindowTop(flag);
		} else {
			showPopupWindowBottom(flag);
		}
		return;
	}

	public boolean isShowing() {
		if (mWindow != null) {
			return mWindow.isShowing();
		}
		return false;
	}

	public void hide() {
		if (mWindow != null) {
			mWindow.dismiss();
		}
	}

	public void setFloatingPoint(int x, int y) {
		this.mFloatingPoint.x = x;
		this.mFloatingPoint.y = y;
	}

	private void initViewClickEvent(int flag) {

		mContentView = LayoutInflater.from(mContext).inflate(R.layout.reader_note_tip, null);
		mRootView = (RelativeLayout) mContentView.findViewById(R.id.reader_note_root_view);
		mScrollView = (ScrollView) mContentView.findViewById(R.id.reader_scrollview);
		mNoteContent = (DDTextView) mContentView.findViewById(R.id.reader_note_content);
		mNoteContent.setOnClickListener(listener);
		mNoteContent.setTag(flag);
		mArrowView = (DDImageView) mContentView.findViewById(R.id.reader_note_arrow);
		mBackground = mContentView.findViewById(R.id.reader_note_bg);
	}

	private void calArrowLeft() {
		int maxWidth = mScreenWidth - 2 * left;
		int arrowWidth = (int) (mDensity * ARROW_RES_WIDTH);

		arrowLeft = left;
		if (mScrollViewWidth > maxWidth) { // full width
			windowLeft = left;
			arrowLeft = mFloatingPoint.x - left - arrowWidth / 2;
			arrowLeft = Math.min(arrowLeft, mScreenWidth - windowLeft * 2 - arrowWidth);
		} else if ((mFloatingPoint.x + mScrollViewWidth) > mScreenWidth) { // align right
			windowLeft = mScreenWidth - mScrollViewWidth;// - left  
			arrowLeft = mFloatingPoint.x - arrowWidth / 2 - windowLeft;
		} else if ((mFloatingPoint.x - left) < mScrollViewWidth / 2) { // align left
			windowLeft = left;
			arrowLeft = mFloatingPoint.x - left - arrowWidth / 2;
		} else if ((mFloatingPoint.x - left) >= mScrollViewWidth / 2) {// floating
			windowLeft = mFloatingPoint.x - mScrollViewWidth / 2;
			arrowLeft = mScrollViewWidth / 2 - arrowWidth / 2;
		}
		arrowLeft = Math.max(0, arrowLeft);
	}

	private void showPopupWindowTop(int flag) {
		RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		lp1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		lp1.bottomMargin = (int) (9 * mDensity);
		mRootView.updateViewLayout(mBackground, lp1);

		if (GlobalWindow.NW_FLAG_NOTE == flag) {
			mArrowView.setImageResource(R.drawable.reader_note_arrow_down);
			mBackground.setBackgroundResource(R.drawable.reader_note_tip);
			mNoteContent.setTextColor(noteColor);
		} else if (GlobalWindow.NW_FLAG_INNERNOTE == flag) {
			mArrowView.setImageResource(R.drawable.reader_innernote_arrow_down);
			mBackground.setBackgroundResource(R.drawable.reader_innernote_tip);
			mNoteContent.setTextColor(innerNoteColor);
		}
		mBackground.measure(lp1.width, lp1.height);
		mScrollViewWidth = mBackground.getMeasuredWidth();
		mScrollViewHeight = mBackground.getMeasuredHeight();

		calArrowLeft();
		RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		lp2.leftMargin = arrowLeft;
		lp2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		mRootView.updateViewLayout(mArrowView, lp2);
		initPopupWindow(mScrollViewWidth, mScrollViewHeight);
		mWindow.showAtLocation(mParent, Gravity.NO_GRAVITY, windowLeft, mFloatingPoint.y - mPopupWindowHeight - getFontSize());
	}

	private void showPopupWindowBottom(int flag) {
		RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		lp1.topMargin = (int) (9 * mDensity);
		lp1.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		mRootView.updateViewLayout(mBackground, lp1);

		if (GlobalWindow.NW_FLAG_NOTE == flag) {
			mArrowView.setImageResource(R.drawable.reader_note_arrow_up);
			mBackground.setBackgroundResource(R.drawable.reader_note_tip);
			mNoteContent.setTextColor(noteColor);
		} else if (GlobalWindow.NW_FLAG_INNERNOTE == flag) {
			mArrowView.setImageResource(R.drawable.reader_innernote_arrow_up);
			mBackground.setBackgroundResource(R.drawable.reader_innernote_tip);
			mNoteContent.setTextColor(innerNoteColor);
		}
		mBackground.measure(lp1.width, lp1.height);
		mScrollViewWidth = mBackground.getMeasuredWidth();
		mScrollViewHeight = mBackground.getMeasuredHeight();

		calArrowLeft();
		RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		lp2.leftMargin = arrowLeft;
		lp2.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		mRootView.updateViewLayout(mArrowView, lp2);

		initPopupWindow(mScrollViewWidth, mScrollViewHeight);
		mWindow.showAtLocation(mParent, Gravity.NO_GRAVITY, windowLeft, mFloatingPoint.y);
	}

	private int getFontSize() {
		return (int) ReadConfig.getConfig().getFontSize();
	}

	private void initPopupWindow(int width, int height) {
		if (width > (mScreenWidth - 2 * left)) {
			width = (mScreenWidth - 2 * left);
			int num = mScrollViewWidth / width + 1;
			height = mScrollViewHeight * num;
		}
		height += (int) (10 * mDensity);
		if (height > mWindowHeight) {
			height = mWindowHeight;
		}
		mPopupWindowHeight = height;
		mWindow = new MyPopupWindow(mContentView, width, height);
	}

	public void setNoteWindowOperation(INoteWindowOperation l) {
		mCallback = l;
	}

	private OnClickListener listener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			int i = v.getId();
			if (i == R.id.reader_note_content) {
				if (mCallback != null) {
					mCallback.onClick((Integer) v.getTag());
				}
				hide();

			}
		}
	};
}