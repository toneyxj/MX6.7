package com.dangdang.reader.dread.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.dangdang.reader.R;
import com.dangdang.reader.cloud.MarkNoteManager;
import com.dangdang.reader.cloud.MarkNoteManager.OperateType;
import com.dangdang.reader.cloud.Status;
import com.dangdang.reader.dread.BaseReadActivity;
import com.dangdang.reader.dread.DirectoryMarkNoteActivity;
import com.dangdang.reader.dread.adapter.DmnNoteListAdapter;
import com.dangdang.reader.dread.core.base.GoToParams;
import com.dangdang.reader.dread.core.base.IEpubReaderController.GoToType;
import com.dangdang.reader.dread.core.epub.ReaderAppImpl;
import com.dangdang.reader.dread.data.BookNote;
import com.dangdang.reader.dread.data.BookNoteDataWrapper;
import com.dangdang.reader.dread.format.BaseReadInfo;
import com.dangdang.reader.dread.format.Book;
import com.dangdang.reader.dread.format.Chapter;
import com.dangdang.reader.dread.format.txt.TxtChapter;
import com.dangdang.reader.dread.function.FunctionCode;
import com.dangdang.reader.dread.jni.BaseJniWarp;
import com.dangdang.reader.dread.service.NoteService;
import com.dangdang.zframework.view.DDImageView;
import com.dangdang.zframework.view.DDTextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DmnNoteFragment extends BaseReadFragment {

    private View mContainer;

    private View mNoteEmptyLayout;
    private DDImageView mNoteEmptyImg;
    private DDTextView mNoteEmptyTip2;
    private DDTextView mNoteEmptyView;
//    private SlideLinerlayout slide_layout;
    private ListView mListView;
    private DmnNoteListAdapter noteAdapter;
    private List<BookNoteDataWrapper> mBookNoteWrappers;


    // private BaseReaderApplicaion readerApps;
    // private NoteService noteService;
    // private ReadInfo readInfo;

    public DmnNoteFragment() {
        /*
		 * ReaderAppImpl readerAppImpl = ReaderAppImpl.getApp(); readerApps =
		 * readerAppImpl; noteService =
		 * readerAppImpl.getServiceManager().getNoteService(); readInfo =
		 * (ReadInfo) readerApps.getReadInfo();
		 */
    }

    @Override
    public View onCreateViewImpl(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

    	handler = new MyHandler(this);
        mContainer = inflater.inflate(R.layout.read_dmn_mark_list, container,
                false);
//        slide_layout = (SlideLinerlayout) mContainer
//                .findViewById(slide_layout);
        mListView = (ListView) mContainer
                .findViewById(R.id.read_dmn_mark_listview);
        Book book = getBook();
        Context context = getActivity().getApplicationContext();
        mBookNoteWrappers = new ArrayList<BookNoteDataWrapper>();
        noteAdapter = new DmnNoteListAdapter(context, mBookNoteWrappers, book);
        mListView.setAdapter(noteAdapter);
        mListView.setOnItemClickListener(mItemClickListener);
        mListView.setOnItemLongClickListener(mItemLongClickListener);

        mNoteEmptyLayout = mContainer.findViewById(R.id.read_dmn_empty_layout);
        mNoteEmptyImg = (DDImageView) mContainer
                .findViewById(R.id.read_dmn_empty_img);
        mNoteEmptyTip2 = (DDTextView) mContainer
                .findViewById(R.id.read_dmn_empty_tip2);
        mNoteEmptyView = (DDTextView) mContainer
                .findViewById(R.id.read_dmn_empty_tip);

        loadBookNotes();

        return mContainer;
    }
    /**
     * 控件滑动方向
     * @param direction
     * @return
     */
    public void moveDirectionDown(boolean direction){
//        if (direction){
//            slide_layout.moveDown();
//        }else {
//            slide_layout.moveUp();
//        }
    }
    private Book getBook() {
        return getBaseReadActivity().getBook();
    }

    private BaseReadInfo getReadInfo() {
        return getBaseReadActivity().getReadInfo();
    }

    private void loadBookNotes() {
        BaseReadInfo readInfo = getReadInfo();
        if (getBaseReadActivity().isPdfAndNotReflow()) {
            printLog(" ... pdf ");
        } else {
            try {
                NoteService noteService = ReaderAppImpl.getApp().getServiceManager().getNoteService();// TODO ?
                mBookNoteWrappers = noteService.getBookNoteWrapperListByBookId(
                        readInfo.getDefaultPid(), readInfo.isBoughtToInt());
            } catch (Exception e) {
            }
        }

        handler.sendEmptyMessage(MSG_REFRESH_ADAPTER);
    }

    public void reload() {
        loadBookNotes();
    }

    final OnItemClickListener mItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            turnToBook(position);
        }

    };

    private void turnToBook(int position) {

        final BookNoteDataWrapper bookNote = (BookNoteDataWrapper) noteAdapter
                .getItem(position);
        if (bookNote.data != null) {
            snapToReadScreen();
			/*
			 * final int pageIndex = (Integer) view.getTag(); final String fCode
			 * = FunctionCode.FCODE_GOTO_PAGE;
			 * ReaderApplication.getApp().doFunction(fCode, pageIndex, false);
			 */

            // final String fCode = FunctionCode.FCODE_GOTO_PAGECHAPTER;
            // final int sourceType = Constant.GOTOPAGE_NOTE;
            // final String htmlPath =
            // readerApps.getBook().getChapter(bookNote.chapterIndex).getPath();
            final Chapter chapter = getBook().getChapter(
                    bookNote.data.chapterIndex);
            final int elementIndex = bookNote.data.noteStart;

            GoToParams goParams = new GoToParams();
            goParams.setType(GoToType.ElementIndex);
            goParams.setChapter(chapter);
            goParams.setElementIndex(elementIndex);

            getGlobalApp().doFunction(FunctionCode.FCODE_GOTO_PAGECHAPTER,
                    goParams);
            printLog(" DmnNoteFragment chapter = " + chapter
                    + ", elementIndex = " + elementIndex);
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
        final BookNoteDataWrapper note = mBookNoteWrappers.get(position);
        if (note.data == null)
            return;

        final Dialog dialog = getBookOperationDialog();
        dialog.show();
        DDTextView mName = (DDTextView)
                dialog.findViewById(R.id.bookshelf_book_name);
        mName.setText(getActivity
                ().getResources().getString(R.string.booknote));

        View mDelete = dialog.findViewById(R.id.bookshelf_book_delete);


        DDTextView mTurn = (DDTextView)
                dialog.findViewById(R.id.bookshelf_book_retype);
        mTurn.setText(getActivity
                ().getResources().getString(R.string.read_turn));


        mDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                deleteBookNote(note);
                dialog.dismiss();
            }
        });
        mTurn.setOnClickListener(new View.OnClickListener() {

                                             @Override
                                             public void onClick(View v) {
                                                 turnToBook(position);
                                                 dialog.dismiss();
                                             }
                                         });


    }

    /**
     * 是否删掉记录
     *
     * @return
     */
	/*
	 * private boolean isChangeStatus(){ final boolean isEpubFull =
	 * readerApps.isEpub() && readInfo.isBought(); final boolean isPreset =
	 * readInfo.isPreSet(); return isEpubFull && !isPreset; }
	 */
    public void share(String productId, String shareTitle, String sourceText, String sourceTextAsHtml,
                      String noteText, String picurl, long noteTime) {
        // SharePopupWindow window =
        // getBaseReadActivity().getSharePopupWindow();
        // window.setShareData(getDDShareData(productId, shareTitle, sourceText,
        // noteText, picurl), getDDStatisticsData(shareTitle, sourceText,
        // noteText));
        // window.showOrHideShareMenu();

    }

//    private DDShareData getDDShareData(String productId, String shareTitle,
//                                       String sourceText, String sourceTextAsHtml, String noteText, String picurl, long noteTime) {
//        DDShareData data = new DDShareData();
//        if (TextUtils.isEmpty(noteText)) {
////            data.setShareType(DDShareData.SHARE_TYPE_LINE);
//        } else {
////            data.setShareType(DDShareData.SHARE_TYPE_NOTE);
//            data.setNote(noteText);
//        }
//        data.setShareType(DDShareData.SHARE_TYPE_BOOKNOTE_IMAGE);
//        String author = null;
//        if (getReadInfo() instanceof ReadInfo) {
//            ReadInfo readInfo = (ReadInfo) getReadInfo();
//            author = readInfo.getAuthorName();
//        }
//       data.setAuthor(author);
//        data.setTitle(shareTitle);
//        data.setBookName(shareTitle);
//        data.setLineationContent(sourceText);
//        data.setHtmlContent(sourceTextAsHtml);
//        data.setBookCover(picurl);
//        picurl = ImageConfig.getBookCoverBySize(picurl,ImageConfig.IMAGE_SIZE_CC);
//        data.setPicUrl(picurl);
//        data.setTargetUrl(DDShareData.DDREADER_BOOK_DETAIL_LINK);
//        data.setNoteTime(noteTime);
//        DDShareParams params = new DDShareParams();
//        params.setSaleId(productId);
//        params.setMediaId(productId);
//        if (getReadInfo() instanceof PartReadInfo) {
//            PartReadInfo partReadInfo = (PartReadInfo) getReadInfo();
//            params.setSaleId(partReadInfo.getSaleId());
//            data.setMediaType(1);
//        } else
//            data.setMediaType(2);
//        data.setParams(JSON.toJSONString(params));
//        return data;
//    }

//    private DDStatisticsData getDDStatisticsData(String bookName,
//                                                 String sourceContent, String noteText) {
//        DDStatisticsData data = null;
//        if (TextUtils.isEmpty(noteText)) {
//            data = new DDStatisticsData(DDShareData.SHARE_TYPE_LINE);
//        } else {
//            data = new DDStatisticsData(DDShareData.SHARE_TYPE_NOTE);
//            data.setNote(noteText);
//        }
//        data.setBookName(bookName);
//        data.setLineationContent(sourceContent);
//        data.setProductId(getReadInfo().getProductId());
//
//        return data;
//    }

    private void deleteBookNote(BookNoteDataWrapper data) {
        // ServiceManager serviceManager = readerApps.getServiceManager();
        // NoteService noteService =
        // readerApps.getServiceManager().getNoteService();
        long nowTime = new Date().getTime();
        if (data == null)
            return;
        BookNote note = data.data;
        if (note == null)
            return;
        note.setNoteTime(nowTime);
        note.setModifyTime(String.valueOf(nowTime));
        note.setStatus(String.valueOf(Status.COLUMN_DELETE));
        note.setCloudStatus(String.valueOf(Status.CLOUD_NO));
		/*
		 * if(isChangeStatus()) noteService.updateNoteStatus(note.id,
		 * Status.COLUMN_DELETE, nowTime); else
		 * noteService.deleteBookNoteById(note.id);
		 */
        // noteService.deleteBookNote(note.bookId, note.chapterIndex,
        // note.noteStart, note.noteEnd);

        // MarkNoteManager.getInstance(serviceManager).operationBookNote(note,
        // OperateType.DELETE);
        ReaderAppImpl readerApps = ReaderAppImpl.getApp();// TODO ？
        MarkNoteManager markNoteManager = readerApps.getMarkNoteManager();
        markNoteManager.operationBookNote(note, OperateType.DELETE);
        int index = mBookNoteWrappers.indexOf(data);
        mBookNoteWrappers.remove(data);
        if (index >= mBookNoteWrappers.size()) {
            if (mBookNoteWrappers.get(index - 1).data == null)
                mBookNoteWrappers.remove(index - 1);
        } else {
            if (mBookNoteWrappers.get(index).data == null
                    && mBookNoteWrappers.get(index - 1).data == null)
                mBookNoteWrappers.remove(index - 1);
        }
        noteAdapter.notifyDataSetChanged();
        // readerApps.getController().deleteBookNoteFromList(note);

        readerApps.getReaderWidget().reset();
        readerApps.getReaderWidget().repaint();
        if (mBookNoteWrappers.size() <= 0)
            showOrHideNoteEmptyView();
    }

    @Override
    public void onDestroyImpl() {
        handler.removeMessages(MSG_REFRESH_ADAPTER);

    }

    private void showOrHideNoteEmptyView() {
        BaseReadActivity baseReadActivity = getBaseReadActivity();
        if (noteAdapter.getCount() == 0) {
            mListView.setVisibility(View.GONE);
            mNoteEmptyLayout.setVisibility(View.VISIBLE);
            mNoteEmptyImg.setImageResource(R.drawable.read_dmn_note_empty_img);
            mNoteEmptyView.setText(getActivity().getResources().getString(
                    R.string.reader_note_empty_tip));
            mNoteEmptyTip2.setText(getActivity().getResources().getString(
                    R.string.reader_note_empty_tip2));
            if (baseReadActivity instanceof DirectoryMarkNoteActivity) {
                ((DirectoryMarkNoteActivity)baseReadActivity).hideBookNoteExportBtn();
            }
        } else {
            mNoteEmptyLayout.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
            if (baseReadActivity instanceof DirectoryMarkNoteActivity) {
                ((DirectoryMarkNoteActivity)baseReadActivity).showBookNoteExportBtn();
            }
        }

    }

    public String getBookNoteExportContent() {
        if (noteAdapter != null)
            return  noteAdapter.getBookNoteExportContent();
        return null;
    }

    public int getBookNoteCount() {
        if (noteAdapter != null)
            return  noteAdapter.getCount();
        return 0;
    }

    public String getSelectedTextWithPara(BookNote bookNote) {
        final Chapter chapter = getBook().getChapter(
                bookNote.chapterIndex);

        BaseJniWarp baseJniWarp = new BaseJniWarp();
        BaseJniWarp.EPageIndex pageIndex = new BaseJniWarp.EPageIndex();
        pageIndex.filePath = chapter.getPath();
        pageIndex.pageIndexInChapter = -1;
        pageIndex.bookType = BaseJniWarp.BOOKTYPE_THIRD_EPUB;
        if (getReadInfo().getEBookType() == BaseJniWarp.BOOKTYPE_DD_TXT) {
            pageIndex.bookType = BaseJniWarp.BOOKTYPE_DD_TXT;
            TxtChapter txtChapter = (TxtChapter)chapter;
            pageIndex.startByte = txtChapter.getStartByte();
            pageIndex.endByte = txtChapter.getEndByte();
        }
        String[] textsBookNoteInBook = baseJniWarp.getTextWithPara(pageIndex, bookNote.getNoteStart(), bookNote.getNoteEnd());
        String textJoin = new String();
        if (textsBookNoteInBook.length > 0) {
            textJoin += textsBookNoteInBook[0];
            for (int i = 1; i < textsBookNoteInBook.length; i++) {
                textJoin += "<p style=\"text-indent:2em;\">" + textsBookNoteInBook[i] + "</p>";
            }
        }

        return textJoin;
    }


    private final static int MSG_REFRESH_ADAPTER = 0;
    private Handler handler;

    private void dealMsg(Message msg){
    	switch (msg.what) {
        case MSG_REFRESH_ADAPTER:
            if (noteAdapter == null)
                return;
            noteAdapter.addData(mBookNoteWrappers);
            noteAdapter.notifyDataSetChanged();
            showOrHideNoteEmptyView();
            break;
    	}
    }
    
    private static class MyHandler extends Handler {
		private final WeakReference<DmnNoteFragment> mFragmentView;

		MyHandler(DmnNoteFragment view) {
			this.mFragmentView = new WeakReference<DmnNoteFragment>(view);
		}

		@Override
		public void handleMessage(Message msg) {
			DmnNoteFragment service = mFragmentView.get();
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
}
