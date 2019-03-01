package com.dangdang.reader.dread.adapter;

import java.util.Date;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.dangdang.reader.R;
import com.dangdang.reader.dread.config.ReadConfig;
import com.dangdang.reader.dread.core.base.BaseReaderApplicaion;
import com.dangdang.reader.dread.core.epub.ReaderAppImpl;
import com.dangdang.reader.dread.data.BookMark;
import com.dangdang.reader.dread.data.BookMarkDataWrapper;
import com.dangdang.reader.dread.format.BaseReadInfo;
import com.dangdang.reader.dread.format.Book;
import com.dangdang.reader.dread.format.Book.BaseNavPoint;
import com.dangdang.reader.dread.format.Chapter;
import com.dangdang.reader.dread.format.epub.IEpubBookManager;
import com.dangdang.reader.dread.jni.BaseJniWarp;
import com.dangdang.reader.utils.Utils;
import com.dangdang.zframework.view.DDTextView;

public class DmnMarkListAdapter extends BaseAdapter {

	private static final int ITEM_TYPE_CHAPTER_NAME = 0;
	private static final int ITEM_TYPE_NOTE = 1;

	private Context mContext;
	private LayoutInflater mFlater;
	private List<BookMarkDataWrapper> mData;
	private Book mBook;
	private String mMinuteBefore = "";
	private String mHourBefore = "";
	protected int defaultColor = Color.BLACK;
	protected int contentColor = Color.BLACK;

	public DmnMarkListAdapter(Context context, List<BookMarkDataWrapper> datas,
			Book book) {

		mData = datas;
		mBook = book;
		mContext = context;
		mFlater = LayoutInflater.from(context);

		mMinuteBefore = context.getString(R.string.minute_before);
		mHourBefore = context.getString(R.string.hour_before);
		defaultColor = mContext.getResources().getColor(
				R.color.read_text_light_black);
		contentColor = mContext.getResources().getColor(
				R.color.read_note_content_color);
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

	static class ViewHolder {
		DDTextView chapter;
		DDTextView time;
		DDTextView page;
		DDTextView content;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		int type = getItemViewType(position);
		if (convertView == null) {
			holder = new ViewHolder();
			switch (type) {
			case ITEM_TYPE_CHAPTER_NAME:
				convertView = mFlater.inflate(
						R.layout.read_dmn_note_item_chapter, null);
				holder.chapter = (DDTextView) convertView
						.findViewById(R.id.read_dmn_nitem_chapter);
				break;
			case ITEM_TYPE_NOTE:
				convertView = mFlater
						.inflate(R.layout.read_dmn_mark_item, null);
				holder.content = (DDTextView) convertView
						.findViewById(R.id.read_dmn_mitem_text);
				holder.time = (DDTextView) convertView
						.findViewById(R.id.read_dmn_mitem_addtime);
				holder.page = (DDTextView) convertView
						.findViewById(R.id.read_dmn_mitem_page);
				break;
			}
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		BookMarkDataWrapper bookMarkWrapper = mData.get(position);
		String chapterName = bookMarkWrapper.chapterName;
		BookMark bookMark = bookMarkWrapper.data;
		if (TextUtils.isEmpty(chapterName)) {
			Chapter chapter = mBook.getChapter(bookMarkWrapper.chapterIndex);
			BaseNavPoint nPoint = mBook.getNavPoint(chapter);
			if (nPoint != null) {
				chapterName = nPoint.lableText;
			}
		}
		if (type == ITEM_TYPE_NOTE) {
			holder.time
					.setText(convertText(displayMarkTime(bookMark.markTime)));
			holder.content.setText(convertText(bookMark.markText));
			if (ReadConfig.getConfig().isNightMode()) {
				holder.time.setTextColor(Color.WHITE);
				holder.content.setTextColor(Color.WHITE);
				holder.page.setTextColor(Color.WHITE);
			} else {
				holder.time.setTextColor(contentColor);
				holder.page.setTextColor(contentColor);
				holder.content.setTextColor(defaultColor);
			}
			int pageIndex = 0;
			if (isBookComposingDone()) {
				final int elementIndexByHtml = bookMark.elementIndex;
				try {
					// final Html html =
					// mBook.getHtmlByPath(bookNote.chapterPath);
					final Chapter html = mBook
							.getChapter(bookMark.chapterIndex);
					// int pageIndex = bookM.calcPageNumber(html,
					// elementIndexByHtml);
					// int pageIndex = bookM.calcPageNumber(elementIndexByHtml,
					// html);
					pageIndex = getBookManager().getPageIndexInBook(html,
							elementIndexByHtml);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			holder.page.setText(convertText(String.valueOf(pageIndex)));
		} else {
			holder.chapter.setText(convertText(chapterName));
			if (ReadConfig.getConfig().isNightMode()) {
				holder.chapter.setTextColor(Color.WHITE);
			} else {
				holder.chapter.setTextColor(defaultColor);
			}
		}
		return convertView;
	}

	private String convertText(String text) {
		boolean chineseConvert = ReadConfig.getConfig().getChineseConvert();
		BaseReadInfo readInfo = ReaderAppImpl.getApp().getReadInfo();
		if (readInfo!=null){
			chineseConvert = chineseConvert&&readInfo.isSupportConvert();
		}
		return chineseConvert ? BaseJniWarp.ConvertToGBorBig5(text, 0) : text;
	}

	/*
	 * private String displayMarkTime(long markTime){ Date date = new
	 * Date(markTime); return Utils.dataFormatString(date); }
	 */

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	private String displayMarkTime(long markTime) {

		String sMarkTime = Utils.long2DateString(markTime);
		final long newTime = new Date().getTime();
		final long distanceTime = newTime - markTime;
		long minutes = distanceTime / (1000 * 60);// TODO 分
		minutes = minutes > 0 ? minutes : 1;
		if (minutes < 60) {
			sMarkTime = String.format(mMinuteBefore, minutes);
		} else {
			final long hour = minutes / 60;
			if (hour < 24) {
				sMarkTime = String.format(mHourBefore, hour);
			}
		}
		return sMarkTime;
	}

	public void addData(List<BookMarkDataWrapper> marks) {
		this.mData = marks;
	}

	@Override
	public int getItemViewType(int position) {
		BookMarkDataWrapper data = mData.get(position);
		if (data.data == null)
			return ITEM_TYPE_CHAPTER_NAME;
		else
			return ITEM_TYPE_NOTE;
	}

	private BaseReaderApplicaion getReaderApp() {
		return ReaderAppImpl.getApp();
	}

	private IEpubBookManager getBookManager() {
		return (IEpubBookManager) getReaderApp().getBookManager();
	}

	private boolean isBookComposingDone() {// TODO 改？
		return ReaderAppImpl.getApp().isBookComposingDone();
	}

}
