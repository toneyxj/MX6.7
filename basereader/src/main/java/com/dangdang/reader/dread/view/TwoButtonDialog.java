package com.dangdang.reader.dread.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.dangdang.reader.R;
import com.dangdang.reader.db.service.DDStatisticsService;
import com.dangdang.reader.utils.Utils;
import com.dangdang.reader.view.IDialog;
import com.dangdang.zframework.view.DDButton;

public class TwoButtonDialog extends IDialog {

	private TextView mInfoView;
	private DDButton mSureView;
	private DDButton mCancelView;

	private View.OnClickListener mLeftListener;
	private View.OnClickListener mRightListener;

	public TwoButtonDialog(Context context) {
		super(context, R.style.Dialog_NoBackground);
	}

	@Override
	public void onCreateD() {
		LayoutInflater layoutInflater = LayoutInflater.from(getContext());
		View contentView = layoutInflater.inflate(R.layout.font_dialog1, null);
		setContentView(contentView);
		int w = mContext.getResources().getDisplayMetrics().widthPixels;
		contentView.setMinimumWidth(w - Utils.dip2px(mContext, 40));
		mInfoView = (TextView) contentView.findViewById(R.id.tip);
		mInfoView.setText(R.string.toolbar_font_hint_dialog1);
		mSureView = (DDButton) findViewById(R.id.font_auto_download);
		mCancelView = (DDButton) findViewById(R.id.font_no_download);
		mCancelView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DDStatisticsService.getDDStatisticsService(mContext).addData(
						DDStatisticsService.IS_HIT_DICTIONARY,
						DDStatisticsService.OPerateTime,
						System.currentTimeMillis() + "",
						DDStatisticsService.IS_DOWNLOAD, "0");
				dismiss();
				if (mLeftListener != null) {
					mLeftListener.onClick(v);
				}
			}
		});
		mSureView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DDStatisticsService.getDDStatisticsService(mContext).addData(
						DDStatisticsService.IS_HIT_DICTIONARY,
						DDStatisticsService.OPerateTime,
						System.currentTimeMillis() + "",
						DDStatisticsService.IS_DOWNLOAD, "1");
				dismiss();
				if (mRightListener != null) {
					mRightListener.onClick(v);
				}
			}
		});
	}

	public void setText(String text) {
		mInfoView.setText(text);
	}

	public void setSureViewText(String text){
		mSureView.setText(text);
	}
	
	public void setCancelViewText(String text){
		mCancelView.setText(text);
	}
	
	// 右按钮事件
	public void setOnLeftClickListener(View.OnClickListener l) {
		mLeftListener = l;
	}

	// 右按钮事件
	public void setOnRightClickListener(View.OnClickListener l) {
		mRightListener = l;
	}
}
