package com.dangdang.reader.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dangdang.reader.R;
import com.dangdang.reader.utils.Utils;
import com.dangdang.zframework.view.DDButton;

public class SyncDialog extends IDialog {

	private CYTextView mInfoView;
	private DDButton mSureView;
	private DDButton mCancleView;

	public SyncDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}

	public SyncDialog(Context context, int theme) {
		super(context, theme);
	}

	public SyncDialog(Context context) {
		super(context);
	}

	@Override
	public void onCreateD() {

		LayoutInflater layoutInflater = LayoutInflater.from(getContext());
		View contentView = layoutInflater.inflate(R.layout.sync_dialog, null);
		setContentView(contentView);
		int w = mContext.getResources().getDisplayMetrics().widthPixels;
		contentView.setMinimumWidth(w - Utils.dip2px(mContext, 40));
		mInfoView = (CYTextView) contentView.findViewById(R.id.tip);
		mSureView = (DDButton) findViewById(R.id.make_sure);
		mCancleView = (DDButton) findViewById(R.id.make_cancle);
	}

	// 左按钮事件
	public void setOnLeftClickListener(View.OnClickListener l) {
		mCancleView.setOnClickListener(l);
	}

	// 右按钮事件
	public void setOnRightClickListener(View.OnClickListener l) {
		mSureView.setOnClickListener(l);
	}

	public void setInfo(String... info) {
		if (info == null || info.length != 3)
			return;
		int w = mContext.getResources().getDisplayMetrics().widthPixels;
		ViewGroup.LayoutParams params = this.mInfoView.getLayoutParams();
		params.width = w - Utils.dip2px(mContext, 60);
		mInfoView.setLayoutParams(params);
		mInfoView.setWidth(params.width);

		String sAgeFormat = mContext.getResources().getString(
				R.string.sync_cloudprogress_tip);
		String sFinalAge = String.format(sAgeFormat, info[0], info[1], info[2]);
		// String sFinalAge =
		// "您在当前设备阅读进度是第1000页。您云端阅读进度是2014年06月09日18:14阅读到的第1061页。";
		this.mInfoView.setText(sFinalAge);
	}
}
