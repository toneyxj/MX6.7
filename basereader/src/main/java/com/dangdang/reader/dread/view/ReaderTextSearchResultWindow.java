package com.dangdang.reader.dread.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.ClipboardManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.dangdang.reader.R;
import com.dangdang.reader.base.WebBrowserActivity;
import com.dangdang.reader.db.service.DDStatisticsService;
import com.dangdang.reader.dread.adapter.ReaderTextSearchResultAdapter;
import com.dangdang.reader.dread.core.epub.GlobalWindow.IReaderTextSearchResultOperation;
import com.dangdang.reader.dread.core.epub.IGlobalWindow.IOnDisMissCallBack;
import com.dangdang.reader.dread.core.epub.ReaderAppImpl;
import com.dangdang.reader.dread.data.OneSearch;
import com.dangdang.reader.dread.format.epub.IEpubBookManager;
import com.dangdang.reader.dread.format.epub.IEpubBookManager.SearchListener;
import com.dangdang.reader.dread.holder.SearchDataHolder;
import com.dangdang.reader.view.MyPopupWindow;
import com.dangdang.zframework.log.LogM;
import com.dangdang.zframework.utils.DRUiUtility;
import com.dangdang.zframework.view.DDEditText;
import com.dangdang.zframework.view.DDImageView;
import com.dangdang.zframework.view.DDTextView;
import com.dangdang.zframework.view.pulltorefresh.PullToRefreshBase.OnRefreshListener;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ReaderTextSearchResultWindow implements OnRefreshListener {

    private Context mContext;
    private PopupWindow mWindow;
    private View mParent;
    private View mContentView;
    private FrameLayout mContainerFramLayout;
    private ListView mPullToRefreshListView;
//    private MyPullToRefreshListView mPullToRefreshListView;
    // private FooterLoadingLayout mFooterLayout;
    private ListView mListView;

    private ReaderTextSearchResultAdapter mSearchAdapter;
    private SearchDataHolder mSearchData;
    private List<OneSearch> mTempList = new ArrayList<OneSearch>();

    private IReaderTextSearchResultOperation mOperCallback;
    private String lastKeyWord;
    private InputMethodManager mInputMethodManager;
    private DDEditText mEditText;
    private int mStatusHeight;
    private boolean mSearching;
    private final static String ONLINE_URL_BAIDU = "http://www.baidu.com/s?wd=";
    private final static String ONLINE_URL_BIYING = "http://cn.bing.com/search?q=";
    private DDTextView mCountTv;
    private DDTextView mStatusTv;
    private DDImageView mSearchIv;
    private DDImageView mClearEditIv;
    private String mStringSearching;
    private String mStringSearchResultPre;
    private String mStringSearchResultPost;
    private String mStringSearchResultEnd;
    private IOnDisMissCallBack mOnDisMissCallBack;
    private Handler handler;

    public ReaderTextSearchResultWindow(Context context, View parent) {
    	handler = new MyHandler(this);
        mContext = context;
        mParent = parent;
        mStringSearching = mContext.getResources().getString(
                R.string.reader_text_searching);
        mStringSearchResultPre = mContext.getResources().getString(
                R.string.reader_text_search_result_pre);
        mStringSearchResultPost = mContext.getResources().getString(
                R.string.reader_text_search_result_post);
        mStringSearchResultEnd = mContext.getResources().getString(
                R.string.reader_text_search_result_end);
        mStatusHeight = DRUiUtility.getPadScreenIsLarge() ? 0 : DRUiUtility
                .getUiUtilityInstance().getStatusHeight(mContext);
        mContentView = View.inflate(mContext,
                R.layout.read_textsearchresultwindow, null);
        mContentView.findViewById(R.id.reader_textsearchresult_dismiss_iv)
                .setOnClickListener(mClickListener);
        mSearchIv = (DDImageView) mContentView
                .findViewById(R.id.reader_textsearchresult_search_iv);
        mSearchIv.setOnClickListener(mClickListener);
        mStatusTv = (DDTextView) mContentView
                .findViewById(R.id.reader_textsearchresult_status_tv);
        mCountTv = (DDTextView) mContentView
                .findViewById(R.id.reader_textsearchresult_count_tv);
        mContentView.findViewById(R.id.reader_text_search_result_baidu_bt)
                .setOnClickListener(mClickListener);
        mContentView.findViewById(R.id.reader_text_search_result_wiki_bt)
                .setOnClickListener(mClickListener);
        mClearEditIv = (DDImageView) mContentView
                .findViewById(R.id.reader_textsearchresult__clear_edit_iv);
        mClearEditIv.setOnClickListener(mClickListener);
        mEditText = (DDEditText) mContentView
                .findViewById(R.id.reader_textsearchresult_et);
        disableCopyWindow();

        mEditText.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (event != null
                        && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    mSearchIv.performClick();
                    return true;
                }
                return false;
            }
        });
        mEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                if (TextUtils.isEmpty(text)) {
                    mClearEditIv.setVisibility(View.GONE);
                    mEditText.performClick();
                    showInputMethodService();
                } else {
                    mClearEditIv.setVisibility(View.VISIBLE);
                }
            }
        });
        mNoDataTv = (DDTextView) mContentView
                .findViewById(R.id.reader_text_search_result_nodata_tv);
        mContainerFramLayout = (FrameLayout) mContentView
                .findViewById(R.id.reader_text_search_result_container_fl);

//        mPullToRefreshListView = new MyPullToRefreshListView(mContext,
//                PullToRefreshBase.mod);
//        mPullToRefreshListView.init(this);
        mPullToRefreshListView = new ListView(mContext);
        mPullToRefreshListView.setCacheColorHint(Color.TRANSPARENT);
        mPullToRefreshListView.setDivider(null);
        mPullToRefreshListView.setDividerHeight(0);
        mContainerFramLayout.addView(mPullToRefreshListView, 0);


        mWindow = new MyPopupWindow(mContentView,
                ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.FILL_PARENT);
        mWindow.setFocusable(true);
        mWindow.setBackgroundDrawable(new BitmapDrawable());
        mWindow.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss() {
                if (!mItemClick) {
                    if (isSearching()) {
                        resetSearching();
                        stopSearch();
                    }
                }
                if (mOnDisMissCallBack != null) {
                    mOnDisMissCallBack.onDismissCallBack();
                }
            }
        });

        mSearchAdapter = new ReaderTextSearchResultAdapter(context, null);
//        mListView = mPullToRefreshListView.getRefreshableView();
        mListView = mPullToRefreshListView;
        mListView.setAdapter(mSearchAdapter);
        /*
		 * mFooterLayout = new FooterLoadingLayout(context); mFooterLayout
		 * .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		 * mFooterLayout.setLoadingTvColor(Color.WHITE);
		 */

        mSearchAdapter.setOnClickListener(mItemClickListener);

        mSearchData = SearchDataHolder.getHolder();
    }

    @SuppressLint("NewApi")
    protected void disableCopyWindow() {
        if (Build.VERSION.SDK_INT > 11) {
            mEditText.setLongClickable(false);
            mEditText.setTextIsSelectable(false);
            mEditText
                    .setCustomSelectionActionModeCallback(new android.view.ActionMode.Callback() {
                        @Override
                        public boolean onPrepareActionMode(
                                android.view.ActionMode mode, Menu menu) {
                            return false;
                        }

                        @Override
                        public void onDestroyActionMode(
                                android.view.ActionMode mode) {

                        }

                        @Override
                        public boolean onCreateActionMode(
                                android.view.ActionMode mode, Menu menu) {
                            return false;
                        }

                        @Override
                        public boolean onActionItemClicked(
                                android.view.ActionMode mode, MenuItem item) {
                            return false;
                        }
                    });
        }
    }

    public void show(boolean resetData, boolean isFullScreen,
                     boolean resetEditText) {
        mItemClick = false;
        if (resetData) {
            resetData();
        }
        if (resetEditText) {
            mEditText.setText("");
            lastKeyWord = null;
        }
        if (!mWindow.isShowing()) {
            if (!isFullScreen) {
                setClipboardText();
            }
            ((Activity) mContext).getWindow().clearFlags(
                    WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            mWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            mWindow.showAtLocation(mParent, Gravity.LEFT | Gravity.TOP, 0,
                    mStatusHeight);
            mListView.setSelectionFromTop(mCurrent, mTop);
        }
    }

    public void setOnDismissCallBack(IOnDisMissCallBack onDismissCallBack) {
        this.mOnDisMissCallBack = onDismissCallBack;
    }

    private void setClipboardText() {
        try {
            if (null == mClipboard) {
                mClipboard = (ClipboardManager) mContext
                        .getSystemService(Context.CLIPBOARD_SERVICE);
            }
            CharSequence clipCharSequence = mClipboard.getText();
            String text = clipCharSequence == null ? null : clipCharSequence
                    .toString().trim();
            if (text != null) {
                text = (String) (text.length() > 10 ? text.subSequence(0, 9)
                        : text);
                mEditText.setText(text);
                mEditText.setSelection(text.length());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void show(boolean resetData) {
        mItemClick = false;
        if (resetData) {
            resetData();
        }
        if (!mWindow.isShowing()) {
            mWindow.showAtLocation(mParent, Gravity.TOP, 0, mStatusHeight);
        }
    }

    public void resetData() {
        checkStopSearch();
        mTempList.clear();
        mSearchData.reset();
        mSearchAdapter.reset();
        // mFooterLayout.setFooterGone();

        mStatusTv.setVisibility(View.INVISIBLE);
        mCountTv.setVisibility(View.INVISIBLE);
        mNoDataTv.setVisibility(View.INVISIBLE);

    }

    public void hide() {
        if (mWindow != null) {
            mWindow.dismiss();
        }
    }

    public boolean isShowing() {
        return mWindow != null && mWindow.isShowing();
    }

    public void setReaderTextSearchResultOperation(
            IReaderTextSearchResultOperation l) {
        mOperCallback = l;
    }

    private void setFooterView(boolean isListNull, boolean hasNext) {
		/*
		 * if (mFooterLayout != null) { mFooterLayout.setNoPadding(); int
		 * footerViewsCount = mListView.getFooterViewsCount(); if (isListNull) {
		 * if (footerViewsCount > 0) { mFooterLayout.setFooterGone(); } return;
		 * } if (hasNext) { if (footerViewsCount <= 0) {
		 * mListView.addFooterView(mFooterLayout); } mFooterLayout.setLoading();
		 * } else { if (footerViewsCount <= 0) {
		 * mListView.addFooterView(mFooterLayout); }
		 * mFooterLayout.setLoadingComplete(); } }
		 */
    }

    private boolean isSearching() {
        return mSearching;
    }

    private void setSearching() {
        mSearching = true;
    }

    private void resetSearching() {
        mSearching = false;
    }

    public void checkStopSearch() {
        if (isSearching()) {
            resetSearching();
            stopSearch();
        }
    }

    public void startSearch(String word) {
        if (TextUtils.isEmpty(word)) {
            printLog(" word is empty ");
            return;
        }
        getEpubM().search(word, new SearchListener() {

            @Override
            public void onStart() {
                handler.sendEmptyMessage(MSG_SEARCH_START);
                printLog(" onStart ");
                // setSearching();
            }

            @Override
            public void onSearch(List<OneSearch> searchs) {
                // printLog(" onSearch " + searchs.size());
                if (searchs != null && searchs.size() > 0) {
                    Message message = handler.obtainMessage(MSG_SEARCHING);
                    message.obj = searchs;
                    handler.sendMessage(message);
                }
            }

            @Override
            public void onEnd() {
                printLog(" onEnd ");
                handler.sendEmptyMessage(MSG_SEARCH_END);
                // resetSearching();
            }
        });
    }

    public void stopSearch() {
        getEpubM().abortSearch();
    }

    public IEpubBookManager getEpubM() {
        return (IEpubBookManager) ReaderAppImpl.getApp().getBookManager();
    }

    final OnClickListener mClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (mOperCallback == null) {
                return;
            }
            int i = v.getId();
            if (i == R.id.reader_textsearchresult_dismiss_iv) {
                if (mSearching) {
                    stopSearch();
                }
                hideInputMethodService();
                mOperCallback.dismissSearchResultWindow();

            } else if (i == R.id.reader_textsearchresult_search_iv) {
                hideInputMethodService();
                startSearch();

            } else if (i == R.id.reader_text_search_result_baidu_bt) {
                DDStatisticsService.getDDStatisticsService(mContext).addData(
                        DDStatisticsService.SEARCH_BY_BAIDU,
                        DDStatisticsService.OPerateTime,
                        System.currentTimeMillis() + "");
                String baiDuKeyWord = mEditText.getText().toString().trim();
                if (!TextUtils.isEmpty(baiDuKeyWord)) {
                    hide();
                    startBrowser(getBaiduSearchUrl(baiDuKeyWord));
                }

            } else if (i == R.id.reader_text_search_result_wiki_bt) {
                DDStatisticsService.getDDStatisticsService(mContext).addData(
                        DDStatisticsService.SEARCH_BY_BING,
                        DDStatisticsService.OPerateTime,
                        System.currentTimeMillis() + "");
                String biYingKeyWord = mEditText.getText().toString().trim();
                if (!TextUtils.isEmpty(biYingKeyWord)) {
                    hide();
                    startBrowser(getBiYingSearchUrl(biYingKeyWord));
                }

            } else if (i == R.id.reader_textsearchresult__clear_edit_iv) {
                mEditText.setText("");
                lastKeyWord = null;
                resetData();
                forceShowSoftInput();

            }
        }

    };

    private void startSearch() {
        String keyWord = mEditText.getText().toString().trim();
        if (!TextUtils.isEmpty(keyWord)) {
            if (!Pattern.compile("\\p{Punct}{1,}+").matcher(keyWord).matches()) {
                if (!keyWord.equals(lastKeyWord)) {
                    checkStopSearch();
                    setFooterView(true, true);
                    mOperCallback.doSearch(keyWord);
                    lastKeyWord = keyWord;
                    mSearchAdapter.setKeyWordLength(keyWord.length());
                    mStatusTv.setVisibility(View.VISIBLE);
                    mCountTv.setVisibility(View.VISIBLE);
                    mStatusTv.setText(mStringSearching);
                    mCountTv.setText(mStringSearchResultPre + 0
                            + mStringSearchResultPost);
                }
            }
        }
    }

    private String getBaiduSearchUrl(String word) {
        return ONLINE_URL_BAIDU + word;
    }

    private String getBiYingSearchUrl(String word) {
        return ONLINE_URL_BIYING + word;
    }

    private void startBrowser(String url) {
        Intent intent = new Intent(mContext, WebBrowserActivity.class);
        intent.putExtra(WebBrowserActivity.KEY_URL, url);
        intent.putExtra(WebBrowserActivity.KEY_FULLSCREEN, true);
        mContext.startActivity(intent);
    }

    public OneSearch getOneSearch(boolean isPre) {
        OneSearch oneSearch = null;
        if (isPre) {
            if (mCurrent > 0) {
                oneSearch = mSearchData.getSearchs().get(mCurrent - 1);
                mCurrent -= 1;
            }
        } else {
            if (mCurrent < mSearchAdapter.getCount() - 1) {
                oneSearch = mSearchData.getSearchs().get(mCurrent + 1);
                mCurrent += 1;
            }
        }
        return oneSearch;
    }

    private int mCurrent;
    private boolean mItemClick;
    private int mTop;
    final OnClickListener mItemClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            hideInputMethodService();
            mItemClick = true;
            hide();
            try {
                mCurrent = (Integer) v
                        .getTag(R.id.reader_text_search_result_item);
                mTop = v.getTop();
                OneSearch oneSearch = (OneSearch) mSearchAdapter
                        .getItem(mCurrent);
                if (mOperCallback != null) {
                    SearchDataHolder.getHolder().setCurrent(oneSearch);
                    mOperCallback.gotoPageOnSearch(oneSearch.getChapter(),
                            oneSearch.getKeywordStartIndex(),
                            oneSearch.getKeywordEndIndex());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    };

    private void hideInputMethodService() {
        mInputMethodManager = (InputMethodManager) mContext
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        mInputMethodManager.hideSoftInputFromWindow(mEditText.getWindowToken(),
                0);
    }

    private void showInputMethodService() {
        mInputMethodManager = (InputMethodManager) mContext
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        mInputMethodManager.showSoftInputFromInputMethod(
                mEditText.getWindowToken(), 0);
    }

    private void forceShowSoftInput() {
        mEditText.setFocusable(true);
        mEditText.setFocusableInTouchMode(true);
        mEditText.requestFocus();
        InputMethodManager imm = (InputMethodManager) mContext
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mEditText, InputMethodManager.RESULT_SHOWN);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
                InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

	/*
	 * @Override public void onRefresh(int mode) { if (mSearching) {
	 * refreshListView(); } }
	 */

    protected void printLog(String log) {
        LogM.i(getClass().getSimpleName(), log);
    }

    private final static int MSG_SEARCHING = 1;
    private final static int MSG_SEARCH_START = 2;
    private final static int MSG_SEARCH_END = 3;
    private static final int PAGE_SIZE = 10;
    
    private void dealMsg(Message msg){
    	switch (msg.what) {
        case MSG_SEARCH_START:
            setSearching();
            break;
        case MSG_SEARCHING:
            if (isSearching()) {
                @SuppressWarnings("unchecked")
                List<OneSearch> list = (List<OneSearch>) msg.obj;
                mSearchData.addSearchs(list);
                mTempList.addAll(list);
                if (mSearchAdapter.getCount() < PAGE_SIZE) {
                    refreshListView();
                }
            }
            break;
        case MSG_SEARCH_END:
            if (isSearching()) {
                resetSearching();
                mStatusTv.setText(mStringSearchResultEnd);
                refreshListView();
                if (mSearchAdapter.getCount() == 0) {
                    setFooterView(true, false);
                    mNoDataTv.setVisibility(View.VISIBLE);
                } else {
                    setFooterView(false, false);
                    mNoDataTv.setVisibility(View.INVISIBLE);
                }
            }
            break;
    	}
    }
    
    private static class MyHandler extends Handler {
		private final WeakReference<ReaderTextSearchResultWindow> mFragmentView;

		MyHandler(ReaderTextSearchResultWindow view) {
			this.mFragmentView = new WeakReference<ReaderTextSearchResultWindow>(view);
		}

		@Override
		public void handleMessage(Message msg) {
			ReaderTextSearchResultWindow service = mFragmentView.get();
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
    
    private DDTextView mNoDataTv;
    private ClipboardManager mClipboard;

    private void refreshListView() {
        mSearchAdapter.addData(mTempList);
        mCountTv.setText(mStringSearchResultPre + mSearchAdapter.getCount()
                + mStringSearchResultPost);
        mSearchAdapter.notifyDataSetChanged();
        mTempList.clear();
        if (mSearchAdapter.getCount() > 0) {
            setFooterView(false, mSearching);
        }
    }

    @Override
    public void onPullDownRefresh() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPullUpRefresh() {
        // TODO Auto-generated method stub

    }

}
