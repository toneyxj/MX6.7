package com.dangdang.reader.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.dangdang.reader.R;
import com.dangdang.zframework.view.DDButton;
import com.dangdang.zframework.view.DDImageView;

public class SyncTipDialog extends IDialog implements
		android.view.View.OnClickListener {

	private DDImageView mCheck;
	private DDButton mSureView;
	private DDButton mCancleView;
	private boolean mIsSelected = false;

	public SyncTipDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}

	public SyncTipDialog(Context context, int theme) {
		super(context, theme);
	}

	public SyncTipDialog(Context context) {
		super(context);
	}

	@Override
	public void onCreateD() {

		LayoutInflater layoutInflater = LayoutInflater.from(getContext());
		View contentView = layoutInflater.inflate(R.layout.sync_tip_dialog,
				null);
		setContentView(contentView);

		contentView.findViewById(R.id.check).setOnClickListener(this);
		mCheck = (DDImageView) contentView.findViewById(R.id.check_img);
		mSureView = (DDButton) findViewById(R.id.make_sure);
		mCancleView = (DDButton) findViewById(R.id.make_cancle);

		mSureView.setText(R.string.allow);
		mCancleView.setText(R.string.no_allow);
	}

	// 左按钮事件
	public void setOnLeftClickListener(View.OnClickListener l) {
		mCancleView.setOnClickListener(l);
	}

	// 右按钮事件
	public void setOnRightClickListener(View.OnClickListener l) {
		mSureView.setOnClickListener(l);
	}

	public boolean isSelect() {
		return mIsSelected;
	}

	public void setSelect(boolean isSelect) {
		this.mIsSelected = isSelect;
		setStatus();
	}

	private void setStatus() {
		if (this.mIsSelected)
			mCheck.setImageResource(R.drawable.txt_delete_select);
		else
			mCheck.setImageResource(R.drawable.txt_delete_default);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		int i = arg0.getId();
		if (i == R.id.check) {
			mIsSelected = !mIsSelected;
			setStatus();

		}
	}
}
