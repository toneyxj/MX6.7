package com.dangdang.reader.dread.adapter;

import android.content.Context;
import android.graphics.Color;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dangdang.reader.R;
import com.dangdang.reader.dread.config.ReadConfig;
import com.dangdang.reader.dread.core.epub.ReaderAppImpl;
import com.dangdang.reader.dread.format.BaseReadInfo;
import com.dangdang.reader.dread.jni.BaseJniWarp;
import com.dangdang.zframework.log.LogM;

public abstract class DmnBaseAdapter extends BaseAdapter {

	private int nightColor = Color.WHITE;
	private int dayColor = Color.BLACK;

	public DmnBaseAdapter(Context context) {
		dayColor = context.getResources().getColor(
				R.color.read_text_depth_black);
	}

	public void initTextViewColor(TextView... tvs) {
		if (tvs != null && tvs.length > 0) {
			for (TextView textView : tvs) {
				if (textView == null) {
					continue;
				}
				textView.setTextColor(isNightColor() ? nightColor : dayColor);
			}
		}
	}

	protected boolean isNightColor() {
		return isSupportNightMode() && ReadConfig.getConfig().isNightMode();
	}

	protected boolean isSupportNightMode() {
		return true;
	}

	protected void printLog(String log) {
		LogM.e(getClass().getSimpleName(), log);
	}

	protected void printLogE(String log) {
		LogM.e(getClass().getSimpleName(), log);
	}

	protected String convertText(String text) {
		boolean chineseConvert = ReadConfig.getConfig().getChineseConvert();
		BaseReadInfo readInfo = ReaderAppImpl.getApp().getReadInfo();
		if (readInfo!=null){
			chineseConvert = chineseConvert&&readInfo.isSupportConvert();
		}
		return chineseConvert ? BaseJniWarp.ConvertToGBorBig5(text, 0) : text;
	}
}
