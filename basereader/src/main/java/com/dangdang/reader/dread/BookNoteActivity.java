package com.dangdang.reader.dread;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;

import com.dangdang.reader.Constants;
import com.dangdang.reader.R;
import com.dangdang.reader.cloud.MarkNoteManager;
import com.dangdang.reader.cloud.MarkNoteManager.OperateType;
import com.dangdang.reader.cloud.Status;
import com.dangdang.reader.dread.core.epub.ReaderAppImpl;
import com.dangdang.reader.dread.data.BookNote;
import com.dangdang.reader.dread.data.BookNote.NoteColumn;
import com.dangdang.reader.dread.format.BaseReadInfo;
import com.dangdang.reader.dread.format.Chapter;
import com.dangdang.zframework.log.LogM;
import com.dangdang.zframework.view.DDTextView;

import java.util.Date;

public class BookNoteActivity extends ReadCommentActivity {

	public static final String BOOK_NOTE_SOURCE_TEXT = "book_note_source_text";
	public static final String BOOK_NOTE_CONTENT = "book_note_content";
	public static final String BOOK_NOTE_AUTHOR = "book_note_author";
	public static final String BOOK_NOTE_CHAPTER = "book_note_chapter";
	public static final String BOOK_NOTE_BOOKCOVER = "book_note_bookcover";
	public static final String BOOK_NOTE_BOOKAUTHOR = "book_note_bookauthor";
	public static final String BOOK_NOTE_BOOKDESC = "book_note_bookdesc";
	public static final String BOOK_NOTE_SAVE_OR_UPDATE = "book_note_save_or_update";
	public static final String BOOK_PARENT_VIEW = "book_parent_view";
	public static final String BOOK_NOTE_TIME = "book_note_time";
	public static final int REQUEST_CODE = 0;
	public static final int BOOK_NOTE_BACK = 1;
	public static final int BOOK_NOTE_SAVE = 2;
	public static final int BOOK_NOTE_DELETE = 3;

	public static final String BOOK_NOTE_BACK_FLAG = "book_note_back_flag";
	public static final String BOOK_NOTE_NEW_ID = "book_note_new_id";
	public static final String BOOK_NOTE_NEW_CONTENT = "book_note_new_content";
	public static final String STATE_CHANGED = "state_changed";
	public static final String BOOK_NOTE_SHARE_CHECK = "book_note_share_check";
	public static final String BOOK_NOTE_OBJECT = "book_note_object";

	public static final int NOTE_MAX_NUM = 1000;

	private String mSource;
	private String mPicurl;
	private View mTipLayout;
	private View mTitleLayout;
	private View mSpaceLayout;
	private DDTextView mCenterTitleView;
	private DDTextView mImageShareView;

	// private SubmitTextChangeReceiver mSubmitTextChangeReceiver;
	// private SharePopupWindow mSharePopupWindow = null;
	private String mChapterName;
	private String mNoteContent;
	private String mModVersion;
	private int mDrawLineColor;
	private int mNoteId = -1;
	private int mChapterIndex;
	private int mNoteStart;
	private int mNoteEnd;
	private int mIsBought;
	private boolean mSaveOrUpdate;
	private long mNoteTime;

	private Chapter mChapter;
	private String mBookCover;
	private String mAuthor;
	private String mBookDesc;

	// 书架搜索文本框 响应键盘输入事件
	private TextWatcher mWatcher = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void afterTextChanged(Editable s) {
			initLastNumTip(NOTE_MAX_NUM
					- mInputText.getText().toString().length());
		}
	};

	private OnClickListener mOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			int i = v.getId();
			if (i == R.id.common_menu_tv || i == R.id.common_back || i == R.id.read_comment_back) {
				try {
//					saveBtnEvent();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					destroy();
				}

			} else {
			}

		}
	};

//	private DDShareData getDDShareData() {
//		DDShareData data = new DDShareData();
//		if (TextUtils.isEmpty(mNoteContent)) {
//			data.setShareType(DDShareData.SHARE_TYPE_LINE);
//		} else {
//			data.setShareType(DDShareData.SHARE_TYPE_NOTE);
//			data.setNote(mNoteContent);
//		}
//		data.setAuthor(getIntent().getStringExtra(BOOK_NOTE_AUTHOR));
//		data.setLineationContent(mSource);
//		data.setTitle(mBookName);
//		data.setBookName(mBookName);
//		data.setPicUrl(mPicurl);
//		data.setTargetUrl(DDShareData.DDREADER_BOOK_DETAIL_LINK);
//        DDShareParams params = new DDShareParams();
//        params.setSaleId(mBookId);
//        params.setMediaId(mBookId);
//        if (getReadInfo() instanceof PartReadInfo) {
//            PartReadInfo partReadInfo = (PartReadInfo) getReadInfo();
//            params.setSaleId(partReadInfo.getSaleId());
//			data.setMediaType(1);
//		} else
//			data.setMediaType(2);
//        data.setParams(JSON.toJSONString(params));
//		return data;
//	}
    private BaseReadInfo getReadInfo(){
        return ReaderAppImpl.getApp().getReadInfo();
    }
//	private DDStatisticsData getDDStatisticsData() {
//		DDStatisticsData data = null;
//		if (TextUtils.isEmpty(mNoteContent)) {
//			data = new DDStatisticsData(DDShareData.SHARE_TYPE_LINE);
//		} else {
//			data = new DDStatisticsData(DDShareData.SHARE_TYPE_NOTE);
//			data.setNote(mNoteContent);
//		}
//		data.setLineationContent(mSource);
//		data.setBookName(mBookName);
//		data.setProductId(mBookId);
//
//		return data;
//	}

	private void initSubmitText() {
		mSubmitView.setText(R.string.shelf_share);
		mSubmitView.setOnClickListener(mOnClickListener);
	}

	private void initValue(Intent tent) {

		mNoteId = tent.getIntExtra(NoteColumn.Id, -1);

		mSource = tent.getStringExtra(BOOK_NOTE_SOURCE_TEXT);
		mBookId = tent.getStringExtra(Constants.BOOK_ID);
		mSaveOrUpdate = tent.getBooleanExtra(BOOK_NOTE_SAVE_OR_UPDATE, true);
		mNoteContent = tent.getStringExtra(BOOK_NOTE_CONTENT);
		mNoteTime = tent.getLongExtra(BOOK_NOTE_TIME, 0);

		mChapterName = tent.getStringExtra(NoteColumn.ChapterName);
		mChapterIndex = tent.getIntExtra(NoteColumn.ChapterIndex, 0);
		mNoteStart = tent.getIntExtra(NoteColumn.NoteStart, 0);
		mNoteEnd = tent.getIntExtra(NoteColumn.NoteEnd, 0);
		mIsBought = tent.getIntExtra(NoteColumn.IsBought, 0);
		mModVersion = tent.getStringExtra(NoteColumn.ModVersion);
		mDrawLineColor = tent.getIntExtra(NoteColumn.ExpColumn4, BookNote.NOTE_DRAWLINE_COLOR_RED);

		mChapter = (Chapter)tent.getSerializableExtra(BOOK_NOTE_CHAPTER);
		mBookCover = tent.getStringExtra(BOOK_NOTE_BOOKCOVER);
		mAuthor = tent.getStringExtra(BOOK_NOTE_BOOKAUTHOR);
		if (mAuthor == null)
			mAuthor = new String("");
		mBookDesc = tent.getStringExtra(BOOK_NOTE_BOOKDESC);
		if (mBookDesc == null)
			mBookDesc = new String("");

		if (TextUtils.isEmpty(mNoteContent)) {
			try {
				MarkNoteManager markNoteManager = getMarkNoteManager();
				BookNote cacheNote = markNoteManager.checkNoteExist(mBookId, mModVersion,
						mIsBought, mChapterIndex, mNoteStart, mNoteEnd);
				if (cacheNote != null) {
					mSaveOrUpdate = false;
					mNoteContent = cacheNote.getNoteText();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void onCreateImpl(Bundle savedInstanceState) {
		super.onCreateImpl(savedInstanceState);

		mChildActivity = true;
		initValue(getIntent());

		mTipLayout = findViewById(R.id.read_comment_tip_layout);
		mTipLayout.setVisibility(View.GONE);
		mTitleLayout = findViewById(R.id.read_comment_title_layout);
		mTitleLayout.setVisibility(View.GONE);

		mSpaceLayout = findViewById(R.id.read_comment_space);
		mSpaceLayout.setVisibility(View.VISIBLE);

		mReadNoteContent.setVisibility(View.VISIBLE);
		String str = getString(R.string.quote_content) + mSource;
		SpannableStringBuilder style = new SpannableStringBuilder(str);
		style.setSpan(new ForegroundColorSpan(0xff01a08b), 0, 4,
				Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
		mReadNoteContent.setText(style);
		mStarRate.setVisibility(View.GONE);
		mInputText.setHint(R.string.input_note_content);
		mInputText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(
				NOTE_MAX_NUM)});
		mInputText.addTextChangedListener(mWatcher);

		initEditTextView(mNoteContent);

		mCenterTitleView = (DDTextView) findViewById(R.id.common_title);
		mCenterTitleView.setText(R.string.booknote_edit);

		initSubmitText();
		initLastNumTipNum();
		mBackView.setOnClickListener(mOnClickListener);

		mSubmitView.setText(R.string.save);

//		mImageShareView = (DDTextView) findViewById(R.id.common_menu_image_share);
//		mImageShareView.setText(R.string.share_image);
//		mImageShareView.setOnClickListener(mOnClickListener);

	}

	private void initLastNumTipNum() {
		String str = mInputText.getText().toString().trim();
		if ("".equals(str)) {
			initLastNumTip(NOTE_MAX_NUM);
		} else {
			initLastNumTip(NOTE_MAX_NUM - str.length());
		}
	}

	private void initEditTextView(String content) {
		// mEditText = (DDEditText) findViewById(R.id.read_note_conent_edit);
		mInputText.setText(content);
		if (!TextUtils.isEmpty(content)) {
			int len = content.length() > NOTE_MAX_NUM ? NOTE_MAX_NUM : content
					.length();
			mInputText.setSelection(len);
		}
	}

	/*
	 * private void backBtnEvent() { Intent intent=new Intent();
	 * intent.putExtra(BOOK_NOTE_BACK_FLAG, BOOK_NOTE_BACK);
	 * setResult(RESULT_OK, intent); }
	 */

	private void saveBtnEvent() {
		BookNote bookNote = saveOrUpdateNote();
		Intent intent = new Intent();
		intent.putExtra(BOOK_NOTE_BACK_FLAG, BOOK_NOTE_SAVE);
		intent.putExtra(BOOK_NOTE_NEW_ID, mNoteId);
		intent.putExtra(BOOK_NOTE_NEW_CONTENT, mInputText.getText().toString()
				.trim());
		intent.putExtra(BOOK_NOTE_OBJECT, bookNote);
		setResult(RESULT_OK, intent);
	}

    @Override
    public void finish() {
        try {
            saveBtnEvent();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            hideSoftKeyBoard();
        }
        super.finish();
    }
    /*
	 * private void saveNote(NoteService noteService) {
	 * 
	 * BookNote note = createBookNote(Status.COLUMN_NEW);
	 * 
	 * mNoteId = (int) noteService.saveNote(note); }
	 */

	/**
	 * Status.COLUMN_NEW...
	 * 
	 * @param status
	 * @return
	 */
	private BookNote createBookNote(int status) {
		BookNote note = new BookNote();
		long operateTime = new Date().getTime();
		note.bookId = mBookId;
		note.bookPath = mBookDir;
		note.chapterName = mChapterName;
		note.chapterIndex = mChapterIndex;
		note.sourceText = mSource;
		note.noteStart = mNoteStart;
		note.noteEnd = mNoteEnd;
		note.noteText = mInputText.getText().toString().trim();
		note.noteTime = new Date().getTime();
		note.isBought = mIsBought;
		note.status = String.valueOf(status);
		note.cloudStatus = String.valueOf(Status.CLOUD_NO);
		note.modifyTime = String.valueOf(operateTime);
		note.bookModVersion = mModVersion;
		note.drawLineColor = mDrawLineColor;
		return note;
	}

	/*
	 * private void updateNote(NoteService noteService) { note.noteText =
	 * content; noteService.updateNoteByBookId(mBookId, mIsBought,
	 * mChapterIndex, mNoteStart, mNoteEnd,
	 * mInputText.getText().toString().trim()); }
	 */

	private BookNote saveOrUpdateNote() {

		/*
		 * ServiceManager mServiceManager = new ServiceManager(this);
		 * NoteService noteService = mServiceManager.getNoteService();
		 */

		/*
		 * ServiceManager serviceManager =
		 * ReaderApplication.getApp().getServiceManager(); MarkNoteManager
		 * markNoteManager = MarkNoteManager.getInstance(serviceManager);
		 */
		MarkNoteManager markNoteManager = getMarkNoteManager();
		BookNote note = null;
		if (mSaveOrUpdate) { // 保存新的笔记
//			 saveNote(noteService);
			note = createBookNote(Status.COLUMN_NEW);
			mNoteId = (int) markNoteManager.operationBookNote(note,
					OperateType.NEW);
		} else { // 更新笔记
			// updateNote(noteService);
			note = createBookNote(Status.COLUMN_UPDATE);
			note.id = mNoteId;
			markNoteManager.operationBookNote(note, OperateType.UPDATE);
		}
		return note;
	}

	private MarkNoteManager getMarkNoteManager() {
		return ReaderAppImpl.getApp().getMarkNoteManager();
	}

	// 隐藏输入法
	public void hideSoftKeyBoard() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		boolean isopen = imm.isActive();
		if (isopen) {
			imm.hideSoftInputFromWindow(mInputText.getWindowToken(), 0);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroyImpl() {
		super.onDestroyImpl();
		try{
			mInputText.removeTextChangedListener(mWatcher);
		}catch(Exception e){
			e.printStackTrace();
		}
		/*
		 * try { unregisterReceiver(mSubmitTextChangeReceiver); } catch
		 * (Exception e) { e.printStackTrace(); }
		 */
	}

	class SubmitTextChangeReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			initSubmitText();
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		try {
			if (keyCode == KeyEvent.KEYCODE_BACK) {

//					saveBtnEvent();
					destroy();

				return true;
			} else if (keyCode == KeyEvent.KEYCODE_HOME) {
				saveBtnEvent();
			}
			return super.onKeyDown(keyCode, event);
		} catch (Exception e) {
			LogM.e(this.getClass().getName(), e.toString());
			return true;
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		/*
		 * if (requestCode == TencentWeiBoInfo.TENCENT_RESULT_CODE) {
		 * mSharePopupWindow.mTencentWeiBoInfo.onActivityResult(requestCode,
		 * resultCode, data); }
		 */
	}

}
