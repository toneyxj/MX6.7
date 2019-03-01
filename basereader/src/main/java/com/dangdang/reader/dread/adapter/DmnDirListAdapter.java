package com.dangdang.reader.dread.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dangdang.reader.R;
import com.dangdang.reader.dread.format.Book.BaseNavPoint;
import com.dangdang.zframework.log.LogM;
import com.dangdang.zframework.utils.DRUiUtility;
import com.dangdang.zframework.view.DDTextView;

import java.util.List;

public abstract class DmnDirListAdapter extends DmnBaseAdapter {

	public static final int SUB_NAV_TEXT_SIZE = 23;
	public static final int PARENT_NAV_TEXT_SIZE = 26;

	private LayoutInflater mFlater;
	private List<BaseNavPoint> mData;

	protected float mDensity = 1f;
	protected int paddingL = 20;
	protected int paddingR = paddingL * 2;
	protected int paddingTB = 10;
	protected int selectColor = Color.BLACK;
	protected int defaultColor = Color.BLACK;
	protected int pageColor = Color.BLACK;
	protected static int FREE_COLOR;
	protected static int LOCK_COLOR;

	protected Context mContext;
	protected boolean composingDone;

	public DmnDirListAdapter(Context context) {
		super(context);
		mContext = context;
		mFlater = LayoutInflater.from(context);
		mDensity = DRUiUtility.getDensity();
		paddingL = (int) (20 * mDensity);
		paddingR = (int) (5 * mDensity);
		paddingTB = (int) (10 * mDensity);
		selectColor = mContext.getResources().getColor(R.color.colorBlack);
		pageColor = mContext.getResources().getColor(R.color.color_normal);
		LOCK_COLOR = mContext.getResources().getColor(
				R.color.colorBlack);
		FREE_COLOR = mContext.getResources().getColor(
				R.color.colorBlack);
	}

	@Override
	public int getCount() {
		int count = 0;
		if (mData != null) {
			count = mData.size();
		}
		return count;
	}

	@Override
	public Object getItem(int position) {
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View cacheView = null;
		if (convertView == null) {
			cacheView = mFlater.inflate(R.layout.read_dmn_dir_item, null);
		} else {
			cacheView = convertView;
		}
		DDTextView textView = (DDTextView) cacheView
				.findViewById(R.id.read_dmn_ditem_text);
		DDTextView pageView = (DDTextView) cacheView
				.findViewById(R.id.read_dmn_ditem_page);

		BaseNavPoint navPoint = null;
		try {
			navPoint = mData.get(position);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (navPoint == null) {
			printLogE(" navPoint == null ");
			return cacheView;
		}

		// hanldeViews(textView, navPoint);

		// int pageIndex = 0;
		// boolean isSelect = false;

		CheckSubNavR checkResult = checkResult(position, navPoint,
				composingDone);
		/*
		 * LogM.i(getClass().getSimpleName(), " getView position = "+ position +
		 * ", pageIndex = " + pageIndex +", lableText = " + navPoint.lableText +
		 * ", " + navPoint.fullSrc);
		 */

		int pageIndex = checkResult.pageIndex > 0 ? checkResult.pageIndex : 0;
		pageView.setText(convertText(String.valueOf(pageIndex)));
		textView.setText(convertText(navPoint.lableText));
		cacheView.setTag(pageIndex);

		try{
			hanldeViews(cacheView, textView, pageView, navPoint,
					checkResult.isExist());
		}catch(Throwable e){
			e.printStackTrace();
		}		

		boolean isSelect = checkResult.isContain;
		if (isSelect) {
			textView.setTextColor(selectColor);
		}

		return cacheView;
	}

	/**
	 * @param itemView
	 * @param textView
	 * @param pageView
	 * @param navPoint
	 * @param isInComposingChapter
	 *            navPoint对应章节是否处于排版的章节中
	 */
	protected abstract void hanldeViews(View itemView, DDTextView textView,
			DDTextView pageView, BaseNavPoint navPoint,
			boolean isInComposingChapter);

	public abstract CheckSubNavR checkResult(int position,
			BaseNavPoint navPoint, final boolean isBookComposingDone);

	public void setComposingDone(boolean composingDone) {
		this.composingDone = composingDone;
	}

	protected boolean isComposingDone() {
		return composingDone;
	}

	public void addData(List<BaseNavPoint> navPointList) {
		this.mData = navPointList;
	}

	public List<BaseNavPoint> getData() {
		return mData;
	}

	public LayoutInflater getFlater() {
		return mFlater;
	}

	protected void pringLog(String log) {
		LogM.i(getClass().getSimpleName(), log);
	}

	public static class CheckSubNavR {

		public int pageIndex = 1;
		public boolean isContain = false;

		/**
		 * 排版的章节中是否有此NavPoint对应的章节
		 */
		private boolean isExist = true;

		public boolean isExist() {
			return isExist;
		}

		public void setExist(boolean isExist) {
			this.isExist = isExist;
		}

	}

}
