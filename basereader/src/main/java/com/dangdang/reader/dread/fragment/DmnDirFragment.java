package com.dangdang.reader.dread.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.dangdang.reader.R;
import com.dangdang.reader.dread.BaseReadActivity;
import com.dangdang.reader.dread.adapter.DmnDirListAdapter;
import com.dangdang.reader.dread.adapter.DmnDirListAdapter.CheckSubNavR;
import com.dangdang.reader.dread.adapter.DmnEpubDirListAdapter;
import com.dangdang.reader.dread.adapter.DmnPartDirListAdapter;
import com.dangdang.reader.dread.adapter.DmnTxtDirListAdapter;
import com.dangdang.reader.dread.config.ReadConfig;
import com.dangdang.reader.dread.core.base.GoToParams;
import com.dangdang.reader.dread.core.base.IEpubReaderController.GoToType;
import com.dangdang.reader.dread.core.base.IFunctionManager;
import com.dangdang.reader.dread.core.epub.ReaderAppImpl;
import com.dangdang.reader.dread.format.BaseReadInfo;
import com.dangdang.reader.dread.format.Book;
import com.dangdang.reader.dread.format.Book.BaseNavPoint;
import com.dangdang.reader.dread.format.Chapter;
import com.dangdang.reader.dread.format.DDFile;
import com.dangdang.reader.dread.format.epub.EpubBook.EpubNavPoint;
import com.dangdang.reader.dread.format.epub.EpubChapter;
import com.dangdang.reader.dread.format.part.PartBook;
import com.dangdang.reader.dread.format.part.PartChapter;
import com.dangdang.reader.dread.format.txt.TxtBook.TxtNavPoint;
import com.dangdang.reader.dread.format.txt.TxtChapter;
import com.dangdang.reader.dread.function.FunctionCode;
import com.dangdang.reader.dread.jni.BaseJniWarp;
import com.dangdang.reader.dread.jni.OutlineItem;
import com.dangdang.reader.dread.util.IntentK;
import com.dangdang.zframework.log.LogM;
import com.dangdang.zframework.view.DDTextView;
import com.mx.mxbase.constant.APPLog;
import com.mx.mxbase.view.SildeFrameLayout;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class DmnDirFragment extends BaseReadFragment {

    private View mContainer;
    private SildeFrameLayout slide_layout;
    private ListView mDmnListView;
    private DmnDirListAdapter mDmnAdapter;
    private List<BaseNavPoint> mNavPointList;
    private List<BaseNavPoint> middleList = new ArrayList<>();
    private TextView mTipsView;
    private String mBookName;

    private int totalPage = 0;//总页数
    private int pageItems = 18;//单页个数
    private int currentpage = 0;//当前页数

    public DmnDirFragment() {
    }

    public void setBookName(String name) {
        mBookName = name;
    }

    @Override
    public View onCreateViewImpl(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

        handler = new MyHandler(this);
        mContainer = inflater.inflate(R.layout.read_dmn_dir_list, container,
                false);
        slide_layout = (SildeFrameLayout) mContainer
                .findViewById(R.id.slide_layout);
        mDmnListView = (ListView) mContainer
                .findViewById(R.id.read_dmn_dir_listview);
        mTipsView = (TextView) mContainer
                .findViewById(R.id.read_dmn_dir_tiptxt);
        mTipsView.setText(R.string.dir_loading);
        setTextViewColor();
        setBookName();
        checkGetDirList();
        initAdapter();

        mDmnListView.setAdapter(mDmnAdapter);
        mDmnListView.setOnItemClickListener(mItemClickListener);
        handler.sendEmptyMessage(MSG_REFRESH_ADAPTER);
        slide_layout.setListener(sildeEventListener);
        slide_layout.setnoViewPager(true);

        return mContainer;
    }

    SildeFrameLayout.SildeEventListener sildeEventListener = new SildeFrameLayout.SildeEventListener() {
        @Override
        public void onSildeEventLeft() {
            if (currentpage <= 0) return;
            currentpage--;
            initADapter();
        }

        @Override
        public void onSildeEventRight() {
            if (currentpage < totalPage - 1) {
                currentpage++;
                initADapter();
            }
        }
    };

    /**
     * 控件滑动方向
     *
     * @param direction
     * @return
     */
    public void moveDirectionDown(boolean direction) {
        if (direction) {
            sildeEventListener.onSildeEventRight();
        } else {
            sildeEventListener.onSildeEventLeft();
        }
    }

    public void setBookName() {
        if (mContainer != null
                && mContainer.findViewById(R.id.name_title) != null) {
            DDTextView titleView = (DDTextView) mContainer
                    .findViewById(R.id.name_title);
            ReadConfig config = ReadConfig.getConfig();
            boolean chineseConvert = config.getChineseConvert();
            BaseReadInfo readInfo = ReaderAppImpl.getApp().getReadInfo();
            if (readInfo != null) {
                chineseConvert = chineseConvert && readInfo.isSupportConvert();
            }

            titleView.setText(chineseConvert ? BaseJniWarp.ConvertToGBorBig5(
                    mBookName, 0) : mBookName);
        }
    }

    protected void initAdapter() {
        final Context context = getBaseReadActivity().getApplicationContext();
        final DDFile fileType = getBaseReadActivity().getDDFile();
        if (DDFile.isEpub(fileType)) {
            mDmnAdapter = new DmnEpubDirListAdapter(context);
        } else if (DDFile.isTxt(fileType)) {
            mDmnAdapter = new DmnTxtDirListAdapter(context);
        } else if (DDFile.isPart(fileType)) {
            mDmnAdapter = new DmnPartDirListAdapter(context);
        }
        mDmnListView.setAdapter(mDmnAdapter);
    }

    private void checkGetDirList() {
        if (mNavPointList == null) {
            mNavPointList = getDirectoryList();
        }
    }

    protected void checkViewVsb() {
        if (mNavPointList == null || mNavPointList.size() == 0) {
            mDmnListView.setVisibility(View.GONE);
            mTipsView.setVisibility(View.VISIBLE);
            mTipsView.setText(R.string.no_dirlist);
        } else {
            mDmnListView.setVisibility(View.VISIBLE);
            mTipsView.setVisibility(View.GONE);
        }
    }

    private List<BaseNavPoint> getDirectoryList() {

        final Book book = getBook();
        if (book == null) {// TODO ? 临时容错
            return null;
        }
        List<BaseNavPoint> mData = null;
        if (book instanceof PartBook) {
            mData = ((PartBook) book).getAllNavPointAndVolumeList();
        } else {
            mData = book.getAllNavPointList();
        }
        int index = 0;
        if (mData!=null) {
            for (BaseNavPoint point : mData) {
                APPLog.e("BaseNavPoint-index=" + index, "fullSrc=" + point.fullSrc + "  lableText=" + point.lableText + "  pageIndex=" + point.getPageIndex());
                index++;
            }
        }
        return mData;
    }

    protected Book getBook() {
        BaseReadActivity readAct = (BaseReadActivity) getActivity();
        if (readAct != null) {
            return readAct.getBook();
        }
        LogM.e(" book is null ");
        return null;
    }

    final OnItemClickListener mItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            final BaseNavPoint basePoint = middleList.get(position);
            if (basePoint instanceof PartBook.PartVolumeNavPoint) {
                return;
            }
            snapToReadScreen();
            if (basePoint instanceof EpubNavPoint) {
                handleEpubItemClick(basePoint);
            } else if (basePoint instanceof TxtNavPoint) {
                handleTxtItemClick(basePoint);
            } else if (basePoint instanceof PartBook.PartNavPoint) {
                handlePartItemClick(basePoint);
            }
        }

    };

    protected void handlePartItemClick(BaseNavPoint basePoint) {
        final IFunctionManager readerApps = getGlobalApp();
        // final String fCode = FunctionCode.FCODE_GOTO_PAGENEW;
        // final int sourceType = Constant.GOTOPAGE_DIR;
        PartBook.PartNavPoint navPoint = (PartBook.PartNavPoint) basePoint;

        final Chapter chapter = partNavPoint2Chapter(navPoint);
        GoToParams goParams = new GoToParams();
        goParams.setType(GoToType.Anchor);
        goParams.setChapter(chapter);
        readerApps.doFunction(FunctionCode.FCODE_GOTO_PAGECHAPTER,
                goParams);
    }

    private PartChapter partNavPoint2Chapter(PartBook.PartNavPoint partNavPoint) {
        PartChapter chapter = new PartChapter();
        chapter.setId(partNavPoint.getChapterId());
        chapter.setPath(partNavPoint.getFullSrc());
        chapter.setTitle(partNavPoint.getLableText());
        return chapter;
    }

    protected void handleEpubItemClick(BaseNavPoint basePoint) {
        final IFunctionManager readerApps = getGlobalApp();
        // final String fCode = FunctionCode.FCODE_GOTO_PAGENEW;
        // final int sourceType = Constant.GOTOPAGE_DIR;
        EpubNavPoint navPoint = (EpubNavPoint) basePoint;
        if (navPoint.isPayTip()) {
            readerApps.doFunction(FunctionCode.FCODE_TO_READEND);
        } else {
            String chapterPath = navPoint.fullSrc;
            int lastIndex = chapterPath.lastIndexOf("#");
            if (lastIndex != -1) {
                chapterPath = chapterPath.substring(0, lastIndex);
            }
            // ReadInfo readInfo = (ReadInfo) readerApps.getReadInfo();
            final boolean isNotExists = checkFileNotExists(chapterPath,
                    navPoint.shortSrc);
            if (isNotExists) {
                readerApps.doFunction(FunctionCode.FCODE_TO_READEND);
            } else {
                final Chapter chapter = new EpubChapter(chapterPath);
                final String anchor = navPoint.anchor;
                /*
                 * int elementIndex = 0;
				 * if(!TextUtils.isEmpty(navPoint.anchor)){ elementIndex =
				 * getBookManager().getElementIndexByAnchor(chapter, anchor); }
				 * elementIndex = elementIndex < 0 ? 0 : elementIndex;
				 */
                // final String htmlPath = chapterPath;
                // readerApps.doFunction(fCode, chapter, elementIndex, false,
                // sourceType, anchor);

                GoToParams goParams = new GoToParams();
                goParams.setType(GoToType.Anchor);
                goParams.setAnchor(anchor);
                goParams.setChapter(chapter);

                readerApps.doFunction(FunctionCode.FCODE_GOTO_PAGECHAPTER,
                        goParams);
            }
        }
    }

    protected void handleTxtItemClick(BaseNavPoint basePoint) {

        TxtNavPoint txtPoint = (TxtNavPoint) basePoint;
        // final String fCode = FunctionCode.FCODE_GOTO_PAGENEW;
        // final int sourceType = Constant.GOTOPAGE_DIR;
        final Chapter chapter = txtNavPoint2Chapter(txtPoint);
        final int elementIndex = 0;

        // getReaderApp().doFunction(fCode, chapter, elementIndex, false,
        // sourceType, "");

        GoToParams goParams = new GoToParams();
        goParams.setType(GoToType.ElementIndex);
        goParams.setChapter(chapter);
        goParams.setElementIndex(elementIndex);

        getGlobalApp()
                .doFunction(FunctionCode.FCODE_GOTO_PAGECHAPTER, goParams);

    }

    private TxtChapter txtNavPoint2Chapter(final TxtNavPoint txtPoint) {

        final TxtChapter txtChapter = new TxtChapter();
        txtChapter.setChapterName(txtPoint.getName());
        txtChapter.setPath(txtPoint.getPath());
        txtChapter.setStartByte(txtPoint.getStartByte());
        txtChapter.setEndByte(txtPoint.getEndByte());

        return txtChapter;
    }

    private boolean checkFileNotExists(final String chapterPath,
                                       final String shortPath) {

        boolean notExists = false;
        try {
            notExists = !getBook().hasExistsChapter(
                    new EpubChapter(chapterPath));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return notExists;
    }

    @Override
    public void onDestroyImpl() {

        handler.removeMessages(MSG_REFRESH_ADAPTER);
        reSetNavs();

    }

    private void reSetNavs() {
        if (mNavPointList != null) {
            mNavPointList.clear();
            mNavPointList = null;
        }
        if (middleList != null) {
            middleList.clear();
            middleList = null;
        }
    }

    private final static int MSG_REFRESH_ADAPTER = 0;

    private Handler handler;

    private void dealMsg(Message msg) {
        switch (msg.what) {
            case MSG_REFRESH_ADAPTER:
                boolean composingDone = getGlobalApp()
                        .isBookComposingDone();
                mDmnAdapter.setComposingDone(composingDone);
//            mDmnAdapter.addData(mNavPointList);
//            mDmnAdapter.notifyDataSetChanged();
                notifyAllADapter();
                checkViewVsb();

                if (composingDone || isPdfAndNotReflow()) {
                    int position = getSelectPosWrapper(mNavPointList);
                    if (position >= 0) {
                        mDmnListView.setSelection(position);
                    }
                }
                printLog(" notifyDataSetChanged finish ");
                break;
        }
    }

    private void notifyAllADapter() {
        if (mNavPointList == null) return;
        totalPage = mNavPointList.size() / pageItems;
        totalPage += mNavPointList.size() % pageItems > 0 ? 1 : 0;
        if (currentpage >= totalPage) currentpage = totalPage - 1;
        initADapter();
    }

    private void initADapter() {
        APPLog.e("currentpage", currentpage);
        if (currentpage < totalPage - 1) {
            middleList = mNavPointList.subList(currentpage * pageItems, (currentpage + 1) * pageItems);
        } else {
            middleList = mNavPointList.subList(currentpage * pageItems, mNavPointList.size());
        }

        mDmnAdapter.addData(middleList);
        mDmnAdapter.notifyDataSetChanged();
    }

    private static class MyHandler extends Handler {
        private final WeakReference<DmnDirFragment> mFragmentView;

        MyHandler(DmnDirFragment view) {
            this.mFragmentView = new WeakReference<DmnDirFragment>(view);
        }

        @Override
        public void handleMessage(Message msg) {
            DmnDirFragment service = mFragmentView.get();
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

    public void reload() {
        try {
            checkGetDirList();
            mDmnListView.setVisibility(View.GONE);
            mTipsView.setVisibility(View.VISIBLE);
            ((TextView) mContainer.findViewById(R.id.read_dmn_dir_tiptxt))
                    .setText(R.string.dir_loading);
            setTextViewColor();
            handler.sendEmptyMessage(MSG_REFRESH_ADAPTER);
        } catch (Exception e) {
            LogM.e(e.toString());
        }
    }

    private int getSelectPosWrapper(List<BaseNavPoint> navPointList) {
        int position = getSelectPos(navPointList);
        return position;
    }

    private int getSelectPos(List<BaseNavPoint> navPointList) {

        int pos = -1;
        if (navPointList != null) {

            BaseNavPoint navp = null;
            CheckSubNavR checkResult = null;
            for (int i = 0, size = navPointList.size(); i < size; i++) {
                navp = navPointList.get(i);
                checkResult = mDmnAdapter.checkResult(i, navp, true);
                if (checkResult.isContain) {
                    pos = i;
                    // break;
                }
            }

        }
        return pos;
    }

    protected OutlineItem getItem(int position) {
        return (OutlineItem) mNavPointList.get(position);
    }

    public void onNewIntent(Intent intent) {

        reSetNavs();
        mBookName = intent.getStringExtra(IntentK.BookName);
        setBookName();
        initAdapter();
    }

	/*
     * private IEpubBookManager getBookManager() { return (IEpubBookManager)
	 * getReaderApp().getBookManager(); }
	 */

    private void setTextViewColor() {
        DDTextView titleView = (DDTextView) mContainer
                .findViewById(R.id.name_title);
        if (isPdfAndNotReflow()) {
            titleView.setTextColor(getResources().getColor(
                    R.color.read_text_depth_black));
            return;
        }
        if (ReadConfig.getConfig().isNightMode()) {
            titleView.setTextColor(Color.WHITE);
        } else {
            titleView.setTextColor(getResources().getColor(
                    R.color.read_text_depth_black));
        }
    }
}
