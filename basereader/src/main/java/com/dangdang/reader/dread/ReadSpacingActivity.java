package com.dangdang.reader.dread;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.dangdang.reader.base.BaseStatisActivity;
import com.dangdang.reader.db.service.DDStatisticsService;
import com.dangdang.reader.dread.config.PagePadding;
import com.dangdang.reader.dread.config.ReadConfig;
import com.dangdang.reader.dread.jni.BaseJniWarp;
import com.dangdang.reader.dread.jni.BaseJniWarp.ERect;
import com.dangdang.reader.dread.jni.StringRenderHandler;
import com.dangdang.reader.utils.Constant;
import com.dangdang.reader.R;
import com.dangdang.zframework.utils.BitmapUtil;
import com.dangdang.zframework.utils.DRUiUtility;
import com.dangdang.zframework.view.DDImageView;
import com.dangdang.zframework.view.DDTextView;

public class ReadSpacingActivity extends BaseStatisActivity implements View.OnTouchListener{

	private DDImageView mSpacingSample;
	private View mSpacingUp;
	private View mSpacingDown;
	private View mSpacingLeft;
	private View mSpacingRight;
	private View mSpacingOk;
	private View mSpacingReset;

	private TextView mPageTip;

	private PagePadding mPrePagePadding;
	private float mPreLineSpacing;
	private float mPreParagraphSpacing;

	private int mReadWidth;
	private int mReadHeight;
	private PagePadding mPagePadding;
	private float mLineSpacing;
	private float mParagraphSpacing;
	private float mFontSize;

	private float mLineSpacingStep;
	private float mPaddingLeftOrRightStep;

	private int mTouchSlop = 10;
	private Handler mHandler;
	
	@Override
	protected void onCreateImpl(Bundle savedInstanceState) {
		setContentView(R.layout.read_spacing_set);

		mHandler = new MyHandler(this);
		initFullScreenStatus();

		mSpacingSample = (DDImageView) findViewById(R.id.read_spacing_sample);
		mSpacingUp = findViewById(R.id.read_spacing_up);
		mSpacingDown = findViewById(R.id.read_spacing_down);
		mSpacingLeft = findViewById(R.id.read_spacing_left);
		mSpacingRight = findViewById(R.id.read_spacing_right);
		mSpacingOk = findViewById(R.id.read_spacing_ok);
		mSpacingReset = findViewById(R.id.read_spacing_reset);

		findViewById(R.id.common_back).setOnClickListener(
				mClickListener);
        ((TextView)findViewById(R.id.common_title)).setText("自定义版式");
		mSpacingUp.setOnClickListener(mClickListener);
		mSpacingDown.setOnClickListener(mClickListener);
		mSpacingLeft.setOnClickListener(mClickListener);
		mSpacingRight.setOnClickListener(mClickListener);
		mSpacingOk.setOnClickListener(mClickListener);
		mSpacingReset.setOnClickListener(mClickListener);
		mSpacingSample.setOnTouchListener(this);
		mPageTip = (TextView) findViewById(R.id.read_spacing_pagetip);

		initSpacing();
		initPreSpacing();

		reComposingSample();

		mTouchSlop = ViewConfiguration.get(getBaseContext())
				.getScaledTouchSlop();
		mHandler.sendEmptyMessageDelayed(MSG_HIDE_GUSTURETIP, 3500);
	}

	@Override
	public boolean isSwipeBack() {
		return true;
	}

	private void initFullScreenStatus() {
		DRUiUtility.setActivityFullScreenStatus(this, ReadConfig.getConfig()
				.isFullScreen());
	}
    @Override
    public boolean isTransparentSystemBar() {
        return false;
    }

	private void initSpacing() {
		final ReadConfig readConfig = ReadConfig.getConfig();
		mReadWidth = readConfig.getReadWidth();
		mReadHeight = readConfig.getReadHeight()
				- (int) (50 * DRUiUtility.getDensity());
		mPagePadding = readConfig.getPaddingRect(this);
		mLineSpacing = readConfig.getLineSpacing();
		mParagraphSpacing = readConfig.getParagraphSpacing();

		mLineSpacingStep = readConfig.getLineSpacingStep();
		mPaddingLeftOrRightStep = readConfig.getPaddingLeftOrRightStep();

		mFontSize = ReadConfig.getConfig().getFontSize();
	}

	private void initPreSpacing() {
		mPrePagePadding = ReadConfig.getConfig().getPaddingRect(this);
		mPreLineSpacing = getLineSpacing();
		mPreParagraphSpacing = getParagraphSpacing();
	}

	private boolean isSame() {
		final ReadConfig readConfig = ReadConfig.getConfig();
		return mPrePagePadding.equals(readConfig.getPaddingRect(this))
				&& mPreLineSpacing == readConfig.getLineSpacing()
				&& mPreParagraphSpacing == readConfig.getParagraphSpacing();
	}

	final OnClickListener mClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// final ReadConfig readConfig = ReadConfig.getConfig();
			int i = v.getId();
			if (i == R.id.read_spacing_up) {
				deLineSpacing();

			} else if (i == R.id.read_spacing_down) {
				addLineSpacing();

			} else if (i == R.id.read_spacing_left) {
				addPaddingLeft();

			} else if (i == R.id.read_spacing_right) {
				dePaddingLeft();

			} else if (i == R.id.read_spacing_ok) {
				DDStatisticsService.getDDStatisticsService(
						ReadSpacingActivity.this).addData(
						DDStatisticsService.IS_INDIVIDUAL_DICTIONARY,
						DDStatisticsService.OPerateTime,
						System.currentTimeMillis() + "");
				saveSpacing();
				showToast(R.string.set_success);
				onBackPressed();

			} else if (i == R.id.read_spacing_reset) {// initSpacing();
				restoreDefault();
				reComposingSample();
				showToast(R.string.restore_defaultvalue);

			} else if (i == R.id.common_back) {
				onBackPressed();

			}
		}
	};

	private void restoreDefault() {
		final ReadConfig config = ReadConfig.getConfig();
		mPagePadding = config.getDefaultPaddingRect(this);
		mLineSpacing = ReadConfig.LINESPACING_DEFAULT_M;
		mParagraphSpacing = ReadConfig.PARAGRAPHSPACING_DEFAULT;
	}

	private void saveSpacing() {
		final ReadConfig config = ReadConfig.getConfig();
		final int diffWordNum = config.getLineWordNum()
				- config.getMinLineWord();

		calcFontSizeAndPaddingLeft(getPagePadding().getPaddingLeft());

		config.saveLineSpacing(getLineSpacing());
		config.saveParagraphSpacing(getParagraphSpacing());
		config.savePaddingLeftOrRight(getPagePadding().getPaddingLeft());

		if (!mPrePagePadding.equals(getPagePadding())) {
			int lineWordNum = calcLineWordNum();
			int minLineWordNum = lineWordNum - diffWordNum;
			minLineWordNum = minLineWordNum < 2 ? 2 : minLineWordNum;
			int maxLineWordNum = minLineWordNum
					+ config.getDefaultMaxLineWord()
					- config.getDefaultMinLineWord();
			if (getPagePadding().equals(config.getDefaultPaddingRect(this))) {
				lineWordNum = config.getDefaultLineWord();
				minLineWordNum = config.getDefaultMinLineWord();
				maxLineWordNum = config.getDefaultMaxLineWord();

				printLog(" saveSpacing default=true ");
			}
			printLog(" saveSpacing " + lineWordNum + "," + minLineWordNum + ","
					+ maxLineWordNum);
			config.saveLineWordNum(lineWordNum);
			config.saveMinLineWord(minLineWordNum);
			config.saveMaxLineWord(maxLineWordNum);
		}
		// initPreSpacing();
	}

	private int calcLineWordNum() {
		return (int) ((getReadWidth() - 2 * getPagePadding().getPaddingLeft()) / mFontSize);
	}

	public float calcFontSizeAndPaddingLeft(float paddingLeft) {
		final int mScreenWidth = getReadWidth();
		// TODO
		float fsize = 16;
		float mLeft = paddingLeft;
		float pageWidth = mScreenWidth - 2 * mLeft;
		int lineWordNum = (int) (pageWidth / mFontSize);

		int m = (int) (pageWidth % lineWordNum);
		if (m == 0) {
			fsize = pageWidth / lineWordNum;
		} else {
			if (m <= (mLeft / 2)) {
				fsize = (pageWidth - m) / lineWordNum;
				mLeft = m / 2f + mLeft;
			} else {
				int needValue = lineWordNum - m;

				fsize = (pageWidth + needValue) / lineWordNum;
				mLeft = mLeft - needValue / 2f;
			}
			// setMarginLeft(mLeft);
			updatePadding(mLeft);
		}
		return fsize;
	}

	private void updatePadding(float paddingLeftAndRight) {
		mPagePadding.setPaddingLeft(paddingLeftAndRight);
		mPagePadding.setPaddingRight(paddingLeftAndRight);
	}

	private void updateLineSpacing(float lineSpacing) {
		mLineSpacing = lineSpacing;
		mParagraphSpacing = lineSpacing;
	}

	private Bitmap mSampleBmp = null;

	private void resetSampleBmp() {
		BitmapUtil.recycle(mSampleBmp);
		mSampleBmp = null;
	}

	private void setSampleBmp(Bitmap bitmap) {
		mSampleBmp = bitmap;
	}

	private void reComposingSample() {
		BaseJniWarp jniWrap = new BaseJniWarp();

		final String str = getString(R.string.read_spacing_sample);
		final int width = getReadWidth();
		final int height = getReadHeight();
		final PagePadding pagePadding = getPagePadding();
		final ERect padding = new ERect(pagePadding.getPaddingLeft(),
				pagePadding.getPaddingTop(), pagePadding.getPaddingRight(),
				pagePadding.getPaddingBottom());
		final float lineSpacing = getLineSpacing();
		final float paragraphSpacing = getParagraphSpacing();
		final StringRenderHandler callBack = new StringRenderHandler();

		jniWrap.drawString(str, true, callBack, width, height, padding,
				lineSpacing, paragraphSpacing);

		Bitmap bitmap = callBack.getBitmap();
		if (bitmap != null) {
			mSpacingSample.setImageBitmap(bitmap);
		}
		printLog(" bitmap = " + bitmap);
		resetSampleBmp();
		setSampleBmp(bitmap);

		int lineSpacingToPx = ReadConfig.getConfig().getLineSpacingToPx(
				lineSpacing);
		String spacingNumTip = getString(R.string.read_spacing_num_tip,
				(int) pagePadding.getPaddingLeft(), lineSpacingToPx);
		mPageTip.setText(spacingNumTip);
	}

	private int mStartX = 0;
	private int mStartY = 0;

	@Override
	public boolean onTouch(View v, MotionEvent event) {
//		return false;
//	}
//
//	public boolean onTouchEvent(android.view.MotionEvent event) {

		final int ex = (int) event.getX();
		final int ey = (int) event.getY();
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mStartX = ex;
			mStartY = ey;
			break;
		case MotionEvent.ACTION_MOVE:
			int diffX = ex - mStartX;
			int diffY = ey - mStartY;
			if (Math.abs(diffX) <= mTouchSlop && Math.abs(diffY) < mTouchSlop) {
				break;
			}

			if (Math.abs(diffX) > Math.abs(3 * diffY)
					&& Math.abs(diffX) > mTouchSlop) {
				// printLog(" update padding ");
				if (diffX > 0) {
					dePaddingLeft();
				} else {
					addPaddingLeft();
				}
				mStartX = ex;
				mStartY = ey;
			} else if (Math.abs(diffY) > Math.abs(3 * diffX)
					&& Math.abs(diffY) > mTouchSlop) {
				// printLog(" update spacing ");
				if (diffY > 0) {
					addLineSpacing();
				} else {
					deLineSpacing();
				}
				mStartX = ex;
				mStartY = ey;
			}
			break;
		case MotionEvent.ACTION_UP:
			reset();
			break;
		}

		return true;
	}

	private void reset() {
		mStartX = 0;
		mStartY = 0;
	}

	@Override
	protected void onDestroyImpl() {
		boolean isSame = isSame();
		// printLog(" onDestroyImpl " + isSame + "," +
		// getPagePadding().getPaddingLeft());
		if (!isSame) {
			Intent intent = new Intent();
			intent.setAction(Constant.ACTION_READER_RECOMPOSING);
			sendBroadcast(intent);
		}
		try {
			resetSampleBmp();
		} catch (Exception e) {
			e.printStackTrace();
		}
		mHandler.removeMessages(MSG_HIDE_GUSTURETIP);
	}

	@Override
	public void showToast(int resid) {
//		super.showToast(resid);
		if(mToast == null||mTextView==null){
			mToast =  new Toast(mContext);
			mTextView = new DDTextView(mContext);
			mTextView.setGravity(17);
			mTextView.setTextColor(mContext.getResources().getColor(R.color.white));
			try {
				mTextView.setBackgroundResource(R.drawable.toast_frame);
			} catch (Throwable var6) {
				var6.printStackTrace();
			}

			mToast.setView(mTextView);
			mTextView.setText(resid);
		} else {
			mToast.setDuration(Toast.LENGTH_SHORT);
			mTextView.setText(resid);
		}
		mToast.show();
	}
	private Toast mToast;
	private DDTextView mTextView;

	private final static int MSG_HIDE_GUSTURETIP = 1;
	
	private static class MyHandler extends Handler {
		private final WeakReference<ReadSpacingActivity> mFragmentView;

		MyHandler(ReadSpacingActivity view) {
			this.mFragmentView = new WeakReference<ReadSpacingActivity>(view);
		}

		@Override
		public void handleMessage(Message msg) {
			ReadSpacingActivity service = mFragmentView.get();
			if (service != null) {
				super.handleMessage(msg);
				try {
					switch (msg.what) {
					case MSG_HIDE_GUSTURETIP:
						View view = service.findViewById(R.id.read_spacing_gesturetip);
						view.startAnimation(AnimationUtils.loadAnimation(
								service.getApplicationContext(),
								R.anim.popwindow_fade_animation_end));
						view.setVisibility(View.GONE);
						break;
					default:
						break;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public PagePadding getPagePadding() {
		return mPagePadding;
	}

	public void setPagePadding(PagePadding pagePadding) {
		this.mPagePadding = pagePadding;
	}

	public float getLineSpacing() {
		return mLineSpacing;
	}

	public float getParagraphSpacing() {
		return mParagraphSpacing;
	}

	public int getReadWidth() {
		return mReadWidth;
	}

	public int getReadHeight() {
		return mReadHeight;
	}

	private float getLineSpacingStep() {
		return mLineSpacingStep;
	}

	private float getPaddingLeftOrRightStep() {
		return mPaddingLeftOrRightStep;
	}

	private void addLineSpacing() {
		float spacing = getLineSpacing();
		if (spacing >= ReadConfig.getConfig().getMaxLineSpacing()) {
			showToast(R.string.read_spacing_maxvalue_tip);
		} else {
			spacing += getLineSpacingStep();
			updateLineSpacing(spacing);
			reComposingSample();
		}
	}

	private void deLineSpacing() {
		float spacing = getLineSpacing();
		if (ReadConfig.getConfig().getMinLineSpacing() >= spacing) {
			showToast(R.string.read_spacing_minvalue_tip);
		} else {
			spacing -= getLineSpacingStep();
			updateLineSpacing(spacing);
			reComposingSample();
		}
	}

	private void dePaddingLeft() {
		float paddingLeft = getPagePadding().getPaddingLeft();
		if (paddingLeft <= ReadConfig.getConfig().getMinPaddingLeftOrRight()) {
			showToast(R.string.read_spacing_minvalue_tip);
		} else {
			paddingLeft -= getPaddingLeftOrRightStep();
			updatePadding(paddingLeft);
			reComposingSample();
		}
	}

	private void addPaddingLeft() {
		float paddingLeft = getPagePadding().getPaddingLeft();
		if (paddingLeft >= ReadConfig.getConfig().getMaxPaddingLeftOrRight()) {
			showToast(R.string.read_spacing_maxvalue_tip);
		} else {
			paddingLeft += getPaddingLeftOrRightStep();
			updatePadding(paddingLeft);
			reComposingSample();
		}
	}

	@Override
	public void onBackPressed() {
		finish();
		setResult(2);
	}

}
