package com.dangdang.reader.dread.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.dangdang.reader.R;
import com.dangdang.reader.utils.Utils;
import com.dangdang.reader.view.IDialog;
import com.dangdang.zframework.view.DDButton;

public class FontHintDialog extends IDialog {

	private TextView mInfoView;
	private DDButton mSureView;

	public FontHintDialog(Context context) {
		super(context, R.style.Dialog_NoBackground);
	}

	@Override
	public void onCreateD() {
		LayoutInflater layoutInflater = LayoutInflater.from(getContext());
		View contentView = layoutInflater.inflate(R.layout.font_dialog2, null);
		setContentView(contentView);
		int w = mContext.getResources().getDisplayMetrics().widthPixels;
		contentView.setMinimumWidth(w - Utils.dip2px(mContext, 40));
		mInfoView = (TextView) contentView.findViewById(R.id.tip);
		mSureView = (DDButton) findViewById(R.id.font_download_ok);
		mSureView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
	}

	public void setInfo(int infoRes) {
		mInfoView.setText(infoRes);
	}
}
