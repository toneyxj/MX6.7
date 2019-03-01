package com.dangdang.reader.dread.fragment;

import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.dangdang.reader.R;
import com.dangdang.reader.cloud.MarkNoteManager;
import com.dangdang.reader.cloud.MarkNoteManager.OperateType;
import com.dangdang.reader.cloud.Status;
import com.dangdang.reader.dread.adapter.DmnMarkListAdapter;
import com.dangdang.reader.dread.core.base.BaseGlobalApplication;
import com.dangdang.reader.dread.core.base.GoToParams;
import com.dangdang.reader.dread.core.base.IEpubReaderController.GoToType;
import com.dangdang.reader.dread.core.epub.ReaderAppImpl;
import com.dangdang.reader.dread.data.BookMark;
import com.dangdang.reader.dread.data.BookMarkDataWrapper;
import com.dangdang.reader.dread.format.BaseReadInfo;
import com.dangdang.reader.dread.format.Book;
import com.dangdang.reader.dread.format.Book.BaseNavPoint;
import com.dangdang.reader.dread.format.Chapter;
import com.dangdang.reader.dread.function.FunctionCode;
import com.dangdang.reader.dread.jni.OutlineItem;
import com.dangdang.reader.dread.service.MarkService;
import com.dangdang.zframework.view.DDTextView;

public class DmnMarkFragment extends BaseReadFragment {

	private View mContainer;

	private View mEmptyLayout;
	private ListView mListView;
	private DmnMarkListAdapter markAdapter;
	private List<BookMarkDataWrapper> mBookMarksWrappers;
	protected OnResultListener mOnMarkResultListener;

	private boolean mIsPdf;
	private boolean mIsComics;
	private boolean mIsPartComics;

	public DmnMarkFragment() {
	}

	@Override
	public View onCreateViewImpl(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		handler = new MyHandler(this);
		mContainer = inflater.inflate(R.layout.read_dmn_mark_list, container,
				false);
		mListView = (ListView) mContainer
				.findViewById(R.id.read_dmn_mark_listview);

		mIsPdf = getBaseReadActivity().isPdf();
		mIsComics = getBaseReadActivity().isComics();
		mIsPartComics = getBaseReadActivity().isPartComics();
		{
			Book book = getBook();
			Context context = getActivity().getApplicationContext();
			markAdapter = new DmnMarkListAdapter(context, mBookMarksWrappers,
					book);
			mListView.setAdapter(markAdapter);
		}
		mListView.setOnItemClickListener(mItemClickListener);
		mListView.setOnItemLongClickListener(mItemLongClickListener);

		mEmptyLayout = mContainer.findViewById(R.id.read_dmn_empty_layout);

		loadBookMarks();

		return mContainer;
	}

	private Book getBook() {
		return getBaseReadActivity().getBook();
	}

	public BaseReadInfo getReadInfo() {
		return getBaseReadActivity().getReadInfo();
	}

	private void loadBookMarks() {
		BaseReadInfo readInfo = getReadInfo();
		if (readInfo == null) {
			return;
		}
		BaseGlobalApplication globalApp = getGlobalApp();
		{
			ReaderAppImpl appImpl = (ReaderAppImpl) globalApp;
			MarkService markService = appImpl.getServiceManager()
					.getMarkService();// TODO ?
			mBookMarksWrappers = markService.getBookMarkWrapperList(
					readInfo.getDefaultPid(), readInfo.isBoughtToInt());
		}
		handler.sendEmptyMessage(MSG_REFRESH_ADAPTER);
	}

	public void reload() {
		loadBookMarks();
	}

	final OnItemClickListener mItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			{
				turnToBook(position);
			}
		}

	};


	private void turnToBook(int position) {
		final BookMarkDataWrapper bookMark = (BookMarkDataWrapper) markAdapter
				.getItem(position);
		if (bookMark.data != null) {
			snapToReadScreen();
			final Chapter chapter = getBook().getChapter(
					bookMark.data.chapterIndex);
			final int elementIndex = bookMark.data.elementIndex;
			GoToParams goParams = new GoToParams();
			goParams.setType(GoToType.ElementIndex);
			goParams.setChapter(chapter);
			goParams.setElementIndex(elementIndex);

			getGlobalApp().doFunction(FunctionCode.FCODE_GOTO_PAGECHAPTER,
					goParams);
			printLog(" MarkFragment chapter = " + chapter + ", elementIndex = "
					+ elementIndex);
		}
	}

	final OnItemLongClickListener mItemLongClickListener = new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {
			showOperationDialog(position);
			return true;
		}

	};

	private void showOperationDialog(final int position) {
		{
			mClickBookMarkDataWrapper = mBookMarksWrappers.get(position);
			if (mClickBookMarkDataWrapper.data == null)
				return;
		}
		final Dialog dialog = getBookOperationDialog();
		dialog.show();
		DDTextView mName = (DDTextView) dialog
				.findViewById(R.id.bookshelf_book_name);
		mName.setText(getActivity().getResources().getString(
				R.string.menu_bookmark));
		View mDelete = dialog.findViewById(R.id.bookshelf_book_delete);
		DDTextView mTurn = (DDTextView) dialog
				.findViewById(R.id.bookshelf_book_retype);
		mTurn.setText(getActivity().getResources()
				.getString(R.string.read_turn));
		mDelete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
				{
                    deleteBookMark(mClickBookMarkDataWrapper);
                }
                dialog.dismiss();
            }
		});
		mTurn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				{
					turnToBook(position);
				}
				dialog.dismiss();
			}
		});
	}

	private void deleteBookMark(BookMarkDataWrapper data) {
		long operateTime = new Date().getTime();
		/*
		 * MarkService markService =
		 * readerApps.getServiceManager().getMarkService();
		 * //markService.deleteBookMark(mark.pId, mark.elementIndex,
		 * mark.chapterIndex, mark.isBought); if(isChangeStatus())
		 * markService.updateMarkStatus(mark.pId, mark.isBought,
		 * mark.chapterIndex, mark.elementIndex, Status.COLUMN_DELETE, nowTime);
		 * else markService.deleteBookMark(mark.pId, mark.elementIndex,
		 * mark.chapterIndex, mark.isBought);
		 */
		if (data == null)
			return;
		BookMark mark = data.data;
		if (mark == null)
			return;
		mark.setMarkTime(operateTime);
		mark.setModifyTime(String.valueOf(operateTime));
		mark.setStatus(String.valueOf(Status.COLUMN_DELETE));
		mark.setCloudStatus(String.valueOf(Status.CLOUD_NO));

		// MarkNoteManager.getInstance(readerApps.getServiceManager());
		ReaderAppImpl readerApps = ReaderAppImpl.getApp();// TODO ？耦合
		MarkNoteManager markNoteManager = readerApps.getMarkNoteManager();
		markNoteManager.operationBookMark(mark, OperateType.DELETE);

		int index = mBookMarksWrappers.indexOf(data);
		mBookMarksWrappers.remove(data);
		if (index >= mBookMarksWrappers.size()) {
			if (mBookMarksWrappers.get(index - 1).data == null)
				mBookMarksWrappers.remove(index - 1);
		} else {
			if (mBookMarksWrappers.get(index).data == null
					&& mBookMarksWrappers.get(index - 1).data == null)
				mBookMarksWrappers.remove(index - 1);
		}
		markAdapter.notifyDataSetChanged();
		readerApps.getReaderWidget().reset();
		readerApps.getReaderWidget().repaint();
		if (mBookMarksWrappers.size() <= 0)
			showBookNotesIsEmpty();
	}

	/**
	 * 是否删掉记录
	 * 
	 * @return
	 */
	/*
	 * private boolean isChangeStatus() { final boolean isEpubFull =
	 * readerApps.isEpub() && readInfo.isBought(); final boolean isPreset =
	 * readInfo.isPreSet(); return isEpubFull && !isPreset; }
	 */

	@Override
	public void onDestroyImpl() {
		handler.removeMessages(MSG_REFRESH_ADAPTER);
	}

	private void showOrHideMarkEmptyView() {
		if ((markAdapter == null || markAdapter.getCount() == 0)) {
			showBookNotesIsEmpty();
		} else {
			mEmptyLayout.setVisibility(View.GONE);
			mListView.setVisibility(View.VISIBLE);
		}

	}

	/*
	 * private void checkBookNotesIsEmpty() {
	 * 
	 * showBookNotesIsEmpty(); ReadInfo readInfo = (ReadInfo)
	 * readerApps.getReadInfo(); NoteService noteService =
	 * readerApps.getServiceManager().getNoteService(); //List<BookNote>
	 * bookNotes = noteService.getBookNoteListByBookId(readInfo.pId); int
	 * bookNoteCount = noteService.getBookNoteCount(readInfo.pId,
	 * readInfo.isBoughtToInt()); if(bookNoteCount > 0){
	 * mOnMarkResultListener.OnBackResult(true); }else{
	 * mOnMarkResultListener.OnBackResult(false); } }
	 */

	private void showBookNotesIsEmpty() {
		mListView.setVisibility(View.GONE);
		if(isPdf()){
			mEmptyLayout.findViewById(R.id.read_dmn_empty_img).setVisibility(View.GONE);
			mEmptyLayout.findViewById(R.id.read_dmn_empty_tip2).setVisibility(View.GONE);
		} 
		mEmptyLayout.setVisibility(View.VISIBLE);
	}

	public interface OnResultListener {
		public void OnBackResult(boolean isSucess);
	}

	public void SetOnResultListener(OnResultListener l) {
		mOnMarkResultListener = l;
	}

	private final static int MSG_REFRESH_ADAPTER = 0;
	private Handler handler;

	private void dealMsg(Message msg){
		switch (msg.what) {
		case MSG_REFRESH_ADAPTER:
			{
				markAdapter.addData(mBookMarksWrappers);
				markAdapter.notifyDataSetChanged();
			}
			showOrHideMarkEmptyView();

			break;
		}
	}
	
	private static class MyHandler extends Handler {
		private final WeakReference<DmnMarkFragment> mFragmentView;

		MyHandler(DmnMarkFragment view) {
			this.mFragmentView = new WeakReference<DmnMarkFragment>(view);
		}

		@Override
		public void handleMessage(Message msg) {
			DmnMarkFragment service = mFragmentView.get();
			if (service != null) {
				super.handleMessage(msg);
				try {
					service.dealMsg(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private BookMarkDataWrapper mClickBookMarkDataWrapper;

	private boolean mIsPdfReflow;

}
