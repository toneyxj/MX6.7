package com.dangdang.reader.dread.view;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;

import com.dangdang.reader.R;
import com.dangdang.reader.dread.core.epub.GlobalWindow.IReaderTextSearchOperation;
import com.dangdang.reader.dread.core.epub.ReaderAppImpl;
import com.dangdang.reader.dread.format.epub.IEpubBookManager;
import com.dangdang.reader.dread.holder.SearchDataHolder;
import com.dangdang.reader.utils.Utils;
import com.dangdang.reader.view.MyPopupWindow;

public class ReaderTextSearchWindow {

	private Context mContext;
	private PopupWindow mWindow;
	private View mParent;
	private View mContentView;

	private IReaderTextSearchOperation mOperCallback;

	public ReaderTextSearchWindow(Context context, View parent) {
		mContext = context;
		mParent = parent;

		mContentView = View.inflate(mContext, R.layout.read_textsearchwindow,
				null);
		mContentView.findViewById(R.id.reader_text_search_pre_imageview)
				.setOnClickListener(mClickListener);
		mContentView.findViewById(R.id.reader_text_search_next_imageview)
				.setOnClickListener(mClickListener);
		mContentView.findViewById(R.id.reader_text_search_imageview)
				.setOnClickListener(mClickListener);
		mContentView.findViewById(R.id.reader_text_search_close_imageview)
				.setOnClickListener(mClickListener);
		mWindow = new MyPopupWindow(mContentView, Utils.dip2px(mContext, 180),
				ViewGroup.LayoutParams.WRAP_CONTENT);

		mWindow.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				if (mOperCallback != null) {
					mOperCallback.hide(false);
				}
			}
		});
	}

	public void show() {
		mWindow.showAtLocation(mParent, Gravity.CENTER_HORIZONTAL
				| Gravity.BOTTOM, 0, Utils.dip2px(mContext, 50));
	}

	public void hide(boolean stopSearch) {
		if (mWindow != null && mWindow.isShowing()) {
			mWindow.dismiss();
			if(mOperCallback != null){
				mOperCallback.hide(stopSearch);
			}
		}
		SearchDataHolder.getHolder().resetCurrent();
	}

	protected IEpubBookManager getBookManager() {
		return (IEpubBookManager) ReaderAppImpl.getApp().getBookManager();
	}

	public boolean isShowing() {
		return mWindow != null && mWindow.isShowing();
	}

	public void setReaderTextSearchOperation(IReaderTextSearchOperation l) {
		mOperCallback = l;
	}

	final OnClickListener mClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (mOperCallback == null) {
				return;
			}
			int i = v.getId();
			if (i == R.id.reader_text_search_pre_imageview) {
				mOperCallback.gotoPageOnSearch(true);

			} else if (i == R.id.reader_text_search_next_imageview) {
				mOperCallback.gotoPageOnSearch(false);

			} else if (i == R.id.reader_text_search_imageview) {
				mOperCallback.showSearchResult();

			} else if (i == R.id.reader_text_search_close_imageview) {
				hide(true);

			}
		}
	};

}
