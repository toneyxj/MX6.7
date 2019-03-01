package com.dangdang.reader.dread.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.dangdang.reader.R;
import com.dangdang.reader.dread.core.epub.ReaderAppImpl;
import com.dangdang.reader.dread.data.OneSearch;
import com.dangdang.reader.dread.format.Book;
import com.dangdang.zframework.view.DDTextView;

public class ReaderTextSearchResultAdapter extends BaseAdapter {

	private List<OneSearch> mList;
	private Context mContext;
	private int keyWordLength;
	private static final String PRE_AND_POST_FIX = "...";

	private OnClickListener mClickListener;
	private ForegroundColorSpan mForegroundColorSpan;

	public ReaderTextSearchResultAdapter(Context context, List<OneSearch> list) {
		this.mContext = context;
		this.mList = list;
		mForegroundColorSpan = new ForegroundColorSpan(mContext.getResources()
				.getColor(R.color.blue_0074e1));
	}

	@Override
	public int getCount() {
		return mList == null ? 0 : mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void setKeyWordLength(int length) {
		this.keyWordLength = length;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		SearchResultLayoutHolder holder = null;
		if (convertView == null) {
			holder = new SearchResultLayoutHolder();
			convertView = View.inflate(mContext,
					R.layout.reader_text_search_result_item, null);
			holder.chapterTv = (DDTextView) convertView
					.findViewById(R.id.reader_text_search_result_item_chapter_tv);
			holder.contentTv = (DDTextView) convertView
					.findViewById(R.id.reader_text_search_result_item_content_tv);

			convertView.setTag(holder);

		} else {
			holder = (SearchResultLayoutHolder) convertView.getTag();
		}
		OneSearch oneSearch = mList.get(position);
		holder.chapterTv.setText(((Book) ReaderAppImpl.getApp().getBook())
				.getChapterName(oneSearch.getChapter()));
		String content = PRE_AND_POST_FIX + oneSearch.getContent()
				+ PRE_AND_POST_FIX;
		SpannableStringBuilder style = new SpannableStringBuilder(content);
		style.setSpan(
				mForegroundColorSpan,
				oneSearch.getKeywordIndexInContent()
						+ PRE_AND_POST_FIX.length(),
				oneSearch.getKeywordIndexInContent()
						+ PRE_AND_POST_FIX.length() + keyWordLength,
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		holder.contentTv.setText(style);
		holder.contentTv.setText(style);

		convertView.setOnClickListener(mClickListener);
		convertView.setTag(R.id.reader_text_search_result_item, position);

		return convertView;
	}

	public void addData(List<OneSearch> datas) {
		if (mList == null) {
			mList = new ArrayList<OneSearch>();
		}
		mList.addAll(datas);
	}

	public void reset() {
		keyWordLength = 0;
		if (mList != null) {
			mList.clear();
			notifyDataSetChanged();
		}
	}

	public void setOnClickListener(OnClickListener l) {
		mClickListener = l;
	}

	static class SearchResultLayoutHolder {
		DDTextView contentTv;
		DDTextView chapterTv;
	}
}
