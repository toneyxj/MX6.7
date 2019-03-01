package com.dangdang.reader.dread.adapter;

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
import com.dangdang.reader.dread.data.BookNote;
import com.dangdang.reader.dread.data.BookNoteDataWrapper;
import com.dangdang.reader.dread.format.BaseReadInfo;
import com.dangdang.reader.dread.format.Book;
import com.dangdang.reader.dread.format.Book.BaseNavPoint;
import com.dangdang.reader.dread.format.Chapter;
import com.dangdang.reader.dread.format.epub.IEpubBookManager;
import com.dangdang.reader.dread.jni.BaseJniWarp;
import com.dangdang.reader.utils.Utils;
import com.dangdang.zframework.view.DDImageView;
import com.dangdang.zframework.view.DDTextView;
import com.mx.mxbase.constant.APPLog;

import java.util.Date;
import java.util.List;

public class DmnNoteListAdapter extends BaseAdapter {

	private static final int ITEM_TYPE_CHAPTER_NAME = 0;
	private static final int ITEM_TYPE_NOTE = 1;

	private Context mContext;
	private LayoutInflater mFlater;
	private List<BookNoteDataWrapper> mData;
	private Book mBook;
	private String mMinuteBefore = "";
	private String mHourBefore = "";
	protected int commentColor = Color.BLACK;
	protected int contentColor = Color.BLACK;
	protected int chapterColor = Color.BLACK;

	public DmnNoteListAdapter(Context context, List<BookNoteDataWrapper> datas,
			Book book) {

		mData = datas;
		mBook = book;
		mContext = context;
		mFlater = LayoutInflater.from(context);

		mMinuteBefore = context.getString(R.string.minute_before);
		mHourBefore = context.getString(R.string.hour_before);

		commentColor = mContext.getResources().getColor(
				R.color.colorBlack);
		contentColor = mContext.getResources().getColor(
				R.color.colorBlack);
		chapterColor = mContext.getResources().getColor(
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

	static class ViewHolder {
		DDTextView chapter;
		DDTextView time;
		DDTextView page;
		DDTextView content;
		DDTextView comment;
		DDImageView noteColor;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		int type = getItemViewType(position);
		APPLog.e("getView-type",type);
		if (convertView == null) {
			holder = new ViewHolder();
			APPLog.e("getView-convertView",type);
			switch (type) {
			case ITEM_TYPE_CHAPTER_NAME:
				convertView = mFlater.inflate(
						R.layout.read_dmn_note_item_chapter, null);
				holder.chapter = (DDTextView) convertView
						.findViewById(R.id.read_dmn_nitem_chapter);
				break;
			case ITEM_TYPE_NOTE:
				convertView = mFlater
						.inflate(R.layout.read_dmn_note_item, null);
				holder.comment = (DDTextView) convertView
						.findViewById(R.id.read_dmn_nitem_text);
				holder.content = (DDTextView) convertView
						.findViewById(R.id.read_dmn_nitem_note_content);
				holder.time = (DDTextView) convertView
						.findViewById(R.id.read_dmn_nitem_addtime);
				holder.page = (DDTextView) convertView
						.findViewById(R.id.read_dmn_nitem_page);
				holder.noteColor = (DDImageView) convertView
						.findViewById(R.id.read_dmn_nitem_notecolor);
				break;
			}
			convertView.setTag(holder);
		} else {
			APPLog.e("getView-holder",type);
			holder = (ViewHolder) convertView.getTag();
		}

		BookNoteDataWrapper bookNoteWrapper = mData.get(position);
		String chapterName = bookNoteWrapper.chapterName;
		BookNote bookNote = bookNoteWrapper.data;
		if (TextUtils.isEmpty(chapterName)) {
			Chapter chapter = mBook.getChapter(bookNoteWrapper.chapterIndex);
			BaseNavPoint nPoint = mBook.getNavPoint(chapter);
			if (nPoint != null) {
				chapterName = nPoint.lableText;
			}
		}
		if (type == ITEM_TYPE_NOTE) {
			if (bookNote.isChapterHead) {
			} else {
			}
			holder.time
					.setText(convertText(displayMarkTime(bookNote.noteTime)));
			holder.content.setText(convertText(bookNote.sourceText));

			switch (bookNote.drawLineColor) {
				case BookNote.NOTE_DRAWLINE_COLOR_RED:
					holder.noteColor.setImageResource(R.drawable.read_note_drawline_color_red);
					break;
				case BookNote.NOTE_DRAWLINE_COLOR_YELLOW:
					holder.noteColor.setImageResource(R.drawable.read_note_drawline_color_yellow);
					break;
				case BookNote.NOTE_DRAWLINE_COLOR_GREEN:
					holder.noteColor.setImageResource(R.drawable.read_note_drawline_color_green);
					break;
				case BookNote.NOTE_DRAWLINE_COLOR_BLUE:
					holder.noteColor.setImageResource(R.drawable.read_note_drawline_color_blue);
					break;
				case BookNote.NOTE_DRAWLINE_COLOR_PINK:
					holder.noteColor.setImageResource(R.drawable.read_note_drawline_color_pink);
					break;
				default:
					holder.noteColor.setImageResource(R.drawable.read_note_drawline_color_red);
					break;
			}

			if (bookNote.noteText != null && !"".equals(bookNote.noteText)) {
				holder.comment.setVisibility(View.VISIBLE);
				holder.comment.setText(mContext.getString(R.string.booknote)
						+ "：" + bookNote.noteText);
			} else {
				holder.comment.setVisibility(View.GONE);
			}
			if (ReadConfig.getConfig().isNightMode()) {
				holder.time.setTextColor(Color.WHITE);
				holder.comment.setTextColor(Color.WHITE);
				holder.comment.setBackgroundColor(Color.TRANSPARENT);
				holder.content.setTextColor(Color.WHITE);
				holder.page.setTextColor(Color.WHITE);
			} else {
				holder.time.setTextColor(commentColor);
				holder.comment.setTextColor(commentColor);
				holder.comment
						.setBackgroundResource(R.drawable.read_note_list_comment_bg);
				holder.content.setTextColor(contentColor);
				holder.page.setTextColor(commentColor);
			}
			int pageIndex = 0;
			if (isBookComposingDone()) {
				final int elementIndexByHtml = bookNote.noteStart;
				try {
					// final Html html =
					// mBook.getHtmlByPath(bookNote.chapterPath);
					final Chapter html = mBook
							.getChapter(bookNote.chapterIndex);
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
			if (ReadConfig.getConfig().isNightMode()) {
				holder.chapter.setTextColor(Color.WHITE);
			} else {
				holder.chapter.setTextColor(chapterColor);
			}
			holder.chapter.setText(convertText(chapterName));
		}
		return convertView;
	}

	/*
	 * private String displayMarkTime(long markTime){ Date date = new
	 * Date(markTime); return Utils.dataFormatString(date); }
	 */
	private String convertText(String text) {
		boolean chineseConvert = ReadConfig.getConfig().getChineseConvert();
		BaseReadInfo readInfo = ReaderAppImpl.getApp().getReadInfo();
		if (readInfo!=null){
			chineseConvert = chineseConvert&&readInfo.isSupportConvert();
		}
		return chineseConvert ? BaseJniWarp.ConvertToGBorBig5(text, 0) : text;
	}

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

	public void addData(List<BookNoteDataWrapper> marks) {
		this.mData = marks;
	}

	@Override
	public int getItemViewType(int position) {
		BookNoteDataWrapper data = mData.get(position);
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

	public String getBookNoteExportContent() {
		int nCharCount = 0;
		String strContent = new String();
		for (BookNoteDataWrapper bookNoteData : mData) {
			if (bookNoteData == null)
				continue;
			BookNote bookNote = bookNoteData.data;
			if (bookNote == null) {
				strContent += "\r\n";
				strContent += "\r\n";
				if (bookNoteData.chapterName != null)
					strContent += bookNoteData.chapterName;
			}
			else {
				strContent += "\r\n";
				strContent += Utils.long2DateString(bookNote.getNoteTime());
				strContent += "\r\n";
				String strNoteQuote = bookNote.getSourceText();
				if (strNoteQuote.length() > 220)
					strNoteQuote = strNoteQuote.substring(0, 220) + "...";
				strContent += strNoteQuote;
				if (bookNote.getNoteText() != null && !bookNote.getNoteText().isEmpty()) {
					strContent += "\r\n";
					strContent += "注:";
					strContent += bookNote.getNoteText();
				}
			}
		}
		strContent += "\r\n";

		return strContent;
	}
}