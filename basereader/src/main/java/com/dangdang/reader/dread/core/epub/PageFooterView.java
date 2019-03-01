package com.dangdang.reader.dread.core.epub;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.dangdang.reader.R;
import com.dangdang.reader.dread.view.BatteryView;
import com.dangdang.zframework.log.LogM;

public class PageFooterView extends FrameLayout {

	private View mFooterView;
	private BatteryView mBatteryView;
	private TextView mTimeView;
	private TextView mPageView;
	
	public PageFooterView(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {
		mFooterView = View.inflate(getContext(), R.layout.read_footer, null);
		addView(mFooterView);
		
		mBatteryView = (BatteryView) mFooterView.findViewById(R.id.read_footer_battery);
		mTimeView = (TextView)mFooterView.findViewById(R.id.read_footer_time);
		mPageView = (TextView)mFooterView.findViewById(R.id.read_footer_page);
		
		//setTime(Utils.getCurrentTime());
		//setPage(10, 99);
	}
	
	public boolean isShow(){
		return getVisibility() == View.VISIBLE;
	}
	
	public void setColor(int color){
		mTimeView.setTextColor(color);
		mPageView.setTextColor(color);
		mBatteryView.setColor(color);
	}
	
	public void setTime(String shortTime){
		mTimeView.setText(shortTime);
	}
	
	/**
	 * @param progress 出版物为页进度
	 */
	public void setPageProgress(String progress){
		
		mPageView.setText(progress);
	}
	
	public void setPage(String pageInfo){
		mPageView.setText(pageInfo);
	}
	
	public void setBatteryValue(float battery){
		mBatteryView.setBatteryValue(battery);
	}
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		//super.onLayout(changed, left, top, right, bottom);
		
		mFooterView.layout(0, 0, right + left, bottom - top);
		int pLeft = (right - left - mPageView.getMeasuredWidth());
		int pTop = mFooterView.getPaddingTop();
		mPageView.layout(pLeft, pTop, pLeft + mPageView.getMeasuredWidth(), pTop + mPageView.getMeasuredHeight());
		
	}
	
	public void clear(){
		mBatteryView.clear();
	}
	
	public void printLog(String log){
		LogM.i(getClass().getSimpleName(), log);
	}
}
