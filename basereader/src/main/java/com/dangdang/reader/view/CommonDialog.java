package com.dangdang.reader.view;

import android.content.Context;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dangdang.reader.R;
import com.dangdang.zframework.view.DDButton;
import com.dangdang.zframework.view.DDEditText;
import com.dangdang.zframework.view.DDImageView;
import com.dangdang.zframework.view.DDTextView;

public class CommonDialog extends IDialog {

	private DDTextView mTitleView;
	private DDTextView mInfoView;
	private DDTextView mMutiInfoView;
	private DDTextView mCheckInfoView;
	private DDButton mSureView;
	private DDButton mCancleView;

	private View mButtonLayout;

	private DDEditText mEditText;
	private TextWatcher mContentWatcher;

	private View mCheckLayout;
	private DDImageView mImageView;
	private boolean isSelect;

	private boolean isFirstShow = true; // 用在 书架 借阅书籍过期时，如果是第一次弹出对话框，点击
										// 立即还书，还要在弹出一次（此处为修改显示提示语）

	public CommonDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}

	public CommonDialog(Context context, int theme) {
		super(context, theme);
	}

	public CommonDialog(Context context) {
		super(context);
	}

	@Override
	public void onCreateD() {

		LayoutInflater layoutInflater = LayoutInflater.from(getContext());
		View contentView = layoutInflater.inflate(R.layout.common_dialog, null);
		setContentView(contentView);
		ViewGroup.LayoutParams params = contentView.getLayoutParams();
		if (params != null) {
			params.width = (int) (mContext.getResources().getDisplayMetrics().widthPixels * 0.8);
			contentView.setLayoutParams(params);
		}
		mCheckLayout = findViewById(R.id.checkbox_layout);
		mImageView = (DDImageView) findViewById(R.id.checkbox_img);
		mImageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isSelect) {
					mImageView.setImageResource(R.drawable.txt_delete_default);
				} else {
					mImageView.setImageResource(R.drawable.txt_delete_select);
				}
				isSelect = !isSelect;
			}
		});
		mCheckInfoView = (DDTextView) findViewById(R.id.checkbox_tv);
		mTitleView = (DDTextView) findViewById(R.id.dialog_title);
		mInfoView = (DDTextView) findViewById(R.id.dialog_content_tip);
		mMutiInfoView = (DDTextView) findViewById(R.id.muti_dialog_content_tip);
		mEditText = (DDEditText) findViewById(R.id.dialog_content_edit);
		mButtonLayout = findViewById(R.id.upgrade_bottom_layout);

		mSureView = (DDButton) findViewById(R.id.make_sure);
		mCancleView = (DDButton) findViewById(R.id.make_cancle);

		// getWindow().setBackgroundDrawable(new ColorDrawable(0));
		setCanceledOnTouchOutside(true);
	}

	public void setCheckInfo(String info) {
		mCheckInfoView.setText(info);
	}

	// 左按钮事件
	public void setOnLeftClickListener(View.OnClickListener l) {
		View cancleView = findViewById(R.id.make_cancle);
		cancleView.setOnClickListener(l);
	}

	// 右按钮事件
	public void setOnRightClickListener(View.OnClickListener l) {
		View sureView = findViewById(R.id.make_sure);
		sureView.setOnClickListener(l);
	}

	// title 提示
	public void setTitleInfo(String info) {
		mTitleView.setText(info);
	}

	// 正文 提示居中
	public void setInfo(String info) {
		mInfoView.setText(info);
	}

	public void setInfoLineNum(int num) {
		mInfoView.setLines(num);
	}

	public void setMultiInfoLineNum(int num) {
		mMutiInfoView.setLines(num);
	}

	// 正文 提示居左
	public void setMutiInfo(String info) {
		mMutiInfoView.setText(info);
		mInfoView.setVisibility(View.GONE);
		mMutiInfoView.setVisibility(View.VISIBLE);
	}

	// 左按钮文字
	public void setLeftButtonText(String info) {
		mCancleView.setText(info);
	}

	// 右按钮文字
	public void setRightButtonText(String info) {
		mSureView.setText(info);
	}

	public void hideRightAndLeftButton() {
		mButtonLayout.setVisibility(View.GONE);
		// mSureView.setVisibility(View.INVISIBLE);
		// mCancleView.setVisibility(View.INVISIBLE);
	}

	public void hideRightButton() {
		mSureView.setVisibility(View.GONE);
	}

	public void hideLeftButton() {
		mCancleView.setVisibility(View.GONE);
	}

	public void showEditText() {
		if (mContentWatcher != null) {
			mEditText.addTextChangedListener(mContentWatcher);
		}
		mInfoView.setVisibility(View.GONE);
		mEditText.setVisibility(View.VISIBLE);
	}

	public void setPasswordEdit() {
		mEditText.setInputType(InputType.TYPE_CLASS_TEXT
				| InputType.TYPE_TEXT_VARIATION_PASSWORD);
		mEditText.setHint(R.string.please_inputpassword);
		mEditText.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
				256) });
	}

	public void showCheckBoxLayout() {
		mCheckLayout.setVisibility(View.VISIBLE);
	}

	public void hideEditText() {
		mInfoView.setVisibility(View.VISIBLE);
		mEditText.setVisibility(View.GONE);
	}

	public String getEditTextInfo() {
		return mEditText.getText().toString().trim();
	}

	public void setEditTextInfo(String info) {
		mEditText.setText(info);
	}

	public DDEditText getEditText() {
		return mEditText;
	}

	// 书签
	public void setEditTextLines(int count) {
		mEditText.setSingleLine(false);
		mEditText.setLines(count);
		mEditText.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
				1000) });
		mEditText.setBackgroundResource(R.drawable.mark_sub);
	}

	public void addTextChangedListener(TextWatcher mTextWatcher) {
		mContentWatcher = mTextWatcher;
	}

	public boolean isSelect() {
		return isSelect;
	}

	public void setSelect(boolean isSelect) {
		this.isSelect = isSelect;
	}

	public boolean isFirstShow() {
		return isFirstShow;
	}

	public void setFirstShow(boolean isFirstShow) {
		this.isFirstShow = isFirstShow;
	}

}
