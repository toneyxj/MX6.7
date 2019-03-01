package com.dangdang.reader.dread.view;

import com.dangdang.reader.dread.config.ReadConfig;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

public class VideoView extends RelativeLayout {

	public VideoView(Context context) {
		super(context);
	}

	public VideoView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		final ReadConfig readConfig = ReadConfig.getConfig();
		int screenWidth = readConfig.getReadWidth();
		int screenHeight = readConfig.getReadHeight();
		int width = 0, height = 0;
		int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
		switch (widthMode) {
		case View.MeasureSpec.UNSPECIFIED:
			width = screenWidth;
			break;
		default:
			width = View.MeasureSpec.getSize(widthMeasureSpec);
		}
		int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
		switch (heightMode) {
		case View.MeasureSpec.UNSPECIFIED:
			height = screenHeight;
			break;
		default:
			height = View.MeasureSpec.getSize(heightMeasureSpec);
		}
		setMeasuredDimension(width, height);
		measureChildren(View.MeasureSpec.EXACTLY | width,
				View.MeasureSpec.EXACTLY | height);
	}
}
