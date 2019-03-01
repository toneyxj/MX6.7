package com.dangdang.reader.dread.core.epub;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;

import com.dangdang.reader.R;
import com.dangdang.reader.dread.config.ReadConfig;
import com.dangdang.reader.dread.format.BaseReadInfo;
import com.dangdang.reader.dread.jni.BaseJniWarp;
import com.dangdang.zframework.log.LogM;
import com.dangdang.zframework.view.DDTextView;

public class PageHeaderView extends FrameLayout {

	private DDTextView mNameView;

	public PageHeaderView(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {
		mNameView = (DDTextView) View.inflate(getContext(),
				R.layout.read_header, null);
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		addView(mNameView, lp);
	}

	public void setName(String name) {
		try {
			LogM.e(this.hashCode()+" setName " +name);
			ReadConfig config = ReadConfig.getConfig();
			boolean chineseConvert = config.getChineseConvert();
			BaseReadInfo readInfo = ReaderAppImpl.getApp().getReadInfo();
			if (readInfo!=null){
				chineseConvert = (chineseConvert&&readInfo.isSupportConvert());
			}
			name = chineseConvert ? BaseJniWarp.ConvertToGBorBig5(name, 0)
					: name;
			LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT);
			mNameView.setLayoutParams(lp);
			mNameView.setText(name);
		} catch (Exception e) {
			e.printStackTrace();
			LogM.e(e.toString());
		}
	}

	public void setColor(int color) {
		try {
			mNameView.setTextColor(color);
		} catch (Exception e) {
			LogM.e(e.toString());
		}
	}

	public boolean isShow() {
		return getVisibility() == View.VISIBLE;
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		mNameView.layout(0, 0, right, bottom - top);
	}

	public void setMaxWidth(int width) {
		mNameView.setMaxWidth(width);
	}
}
