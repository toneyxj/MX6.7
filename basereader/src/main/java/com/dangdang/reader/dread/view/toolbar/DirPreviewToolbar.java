package com.dangdang.reader.dread.view.toolbar;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dangdang.reader.R;
import com.dangdang.reader.dread.format.Book.BaseNavPoint;
import com.dangdang.zframework.view.DDTextView;

public class DirPreviewToolbar extends RelativeLayout {

	private List<BaseNavPoint> mData;
	private int mTotalPage;
	private ListView mListView;
	private TextView mTextView;
	private DirPreviewAdapter mAdapter;
	private LayoutInflater mInflater;
	private int mTop;
	private Context mContext;

	public DirPreviewToolbar(Context context) {
		super(context);
		mContext = context;
		mInflater = LayoutInflater.from(context);
	}

	public DirPreviewToolbar(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		mInflater = LayoutInflater.from(context);
	}

	public void initData(List<BaseNavPoint> navs) {
		if (navs == null || navs.size() == 0) {
			mListView.setVisibility(GONE);
		} else {
			mListView.setVisibility(VISIBLE);
			mData = navs;
			mAdapter = new DirPreviewAdapter();
			mListView.setAdapter(mAdapter);
		}
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		mListView = (ListView) findViewById(R.id.preview_chapter_list);
		mTextView = (TextView) findViewById(R.id.preview_page);
		mTop = getResources().getDimensionPixelSize(R.dimen.reader_preview_toolbar_top);
	}

	private class DirPreviewAdapter extends BaseAdapter {

		private static final int ITEM_TYPE_BLANK = 0;
		private static final int ITEM_TYPE_CHAPTER = 1;
		private BaseNavPoint mBlank = new BaseNavPoint();

		@Override
		public int getCount() {
			return mData.size() + 2;
		}

		@Override
		public BaseNavPoint getItem(int position) {
			if (position == 0 || position == mData.size() + 1)
				return mBlank;
			else
				return mData.get(position - 1);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public int getViewTypeCount() {
			return 2;
		}

		@Override
		public int getItemViewType(int position) {
			if(position >= getCount()){
				this.notifyDataSetChanged();
				return ITEM_TYPE_BLANK;
			}
			BaseNavPoint data = getItem(position);
			if (data.lableText == null)
				return ITEM_TYPE_BLANK;
			else
				return ITEM_TYPE_CHAPTER;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(position >= getCount()){
				this.notifyDataSetChanged();
				return new View(mContext);
			}
			
			View cacheView = null;
			int type = getItemViewType(position);
			if (convertView == null) {
				switch (type) {
				case ITEM_TYPE_CHAPTER:
					cacheView = mInflater.inflate(R.layout.reader_preview_toolbar_item, null);
					break;
				case ITEM_TYPE_BLANK:
					cacheView = mInflater.inflate(R.layout.reader_preview_toolbar_blank, null);
					break;
				}
			} else {
				cacheView = convertView;
			}
			DDTextView textView = (DDTextView) cacheView.findViewById(R.id.reader_preview_item_text);
			BaseNavPoint navPoint = getItem(position);
			textView.setText(navPoint.lableText);
			return cacheView;
		}
	}

	public void scrollToIndex(BaseNavPoint point, int pageIndex) {
		mTextView.setText(pageIndex + "/" + mTotalPage);
		if (mData == null || mData.size() == 0){
			return;
		}
		if(point == null){
			return;
		}
		int index = mData.indexOf(point) + 1;
		if (android.os.Build.VERSION.SDK_INT >= 11) {
			try {
				Method method = AbsListView.class.getDeclaredMethod("smoothScrollToPositionFromTop", int.class, int.class);
				method.setAccessible(true);
				method.invoke(mListView, index, mTop);
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		} else {
			mListView.setSelectionFromTop(index, mTop);
		}
	}

	public void setPageSize(int pageSize) {
		mTotalPage = pageSize;
	}

	public void setCurrentPosition(BaseNavPoint point) {
		if (mData == null || mData.size() == 0){
			return;
		}
		if(point == null){
			return;
		}
		int index = mData.indexOf(point) + 1;
		mListView.setSelectionFromTop(index, mTop);
	}
}
