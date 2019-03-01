package com.dangdang.reader.dread.core.part;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dangdang.reader.R;
import com.dangdang.reader.db.service.BuyBookService;
import com.dangdang.reader.dread.DirectoryMarkNoteActivity;
import com.dangdang.reader.dread.ReadActivity;
import com.dangdang.reader.dread.core.base.OtherPageView;
import com.dangdang.reader.dread.core.epub.ReaderAppImpl;
import com.dangdang.reader.dread.data.PartReadInfo;
import com.dangdang.reader.dread.format.BaseReadInfo;
import com.dangdang.reader.personal.domain.ShelfBook;
import com.dangdang.reader.request.RequestConstants;
import com.dangdang.reader.utils.Constant;
import com.dangdang.zframework.log.LogM;
import com.dangdang.zframework.view.DDTextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yhyu on 2015/7/15.
 */
public class PartEndPageView extends OtherPageView {
    protected Context context;
    private int mStart = 0;
    private int mEnd = 8;
    private int mNum = 9;
    private int mTotal = 0;
    private LinearLayout mRootView;
    private LinearLayout mTopLl;
    private TextView mTopTipTv;
    private TextView mTipTv;
    private DDTextView mMulu;
    private DDTextView mDownload;


    private ShelfBook mShelfBook;//已购可赠送的图书对象

    private LinearLayout mFriendsLl;
    private TextView mFriendsTv;
    private TextView mBottomChangeTv;
    private ProgressBar mLoadingView;
    private Handler mHandler;

    private class MyHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            LogM.e("handleMessage = " + msg);
            super.handleMessage(msg);
            try {
                switch (msg.what) {
                    case RequestConstants.MSG_WHAT_GET_BUYALSOBUY_SUCCESS:
                        onSuccess(msg);
                        break;
                    case RequestConstants.MSG_WHAT_GET_BUYALSOBUY_FAILED:
                        onFailed(msg);
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    //    private static class MyHandler extends Handler {
//        private final WeakReference<PartEndPageView> mFragmentView;
//
//        MyHandler(PartEndPageView view) {
//            this.mFragmentView = new WeakReference<PartEndPageView>(view);
//        }
//
//        @Override
//        public void handleMessage(Message msg) {
//            PartEndPageView service = mFragmentView.get();
//            LogM.e("handleMessage = "+msg+",service="+service);
//            if (service != null) {
//                super.handleMessage(msg);
//                try {
//                    switch (msg.what) {
//                        case RequestConstants.MSG_WHAT_GET_BUYALSOBUY_SUCCESS:
//                            service.onSuccess(msg);
//                            break;
//                        case RequestConstants.MSG_WHAT_GET_BUYALSOBUY_FAILED:
//                            service.onFailed(msg);
//                            break;
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
    protected void onSuccess(Message msg) {
//        BookListHolder result = (BookListHolder) msg.obj;
//        if (result == null) {
//            return;
//        }
//        mTotal = result.getTotal();
//        List<StoreBaseBook> books = result.getMediaList();
//        if (books != null && books.size() > 0) {
//            printLog("books=" + books);
//            showDatas(result);
//        }
    }

    protected void onFailed(Message msg) {
        String errorMsg = (String) msg.obj;
        showToast(errorMsg);
    }

    public PartEndPageView(Context context) {
        super(context);
        init(context);
    }

    protected int getLayoutId() {
        return R.layout.view_part_end;
    }

    protected void init(Context context) {
        if (mHandler == null)
            mHandler = new MyHandler();
        this.context = context;
        mRootView = (LinearLayout) View.inflate(context, getLayoutId(), null);
        mTopLl = (LinearLayout) mRootView.findViewById(R.id.view_part_end_top_ll);
        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(getScreenWidth(),
                LayoutParams.WRAP_CONTENT);
        mTopLl.setLayoutParams(llp);// 适配用
        mTopTipTv = (TextView) mRootView.findViewById(R.id.view_part_end_top_tips_tv);

        mTipTv = (TextView) mRootView.findViewById(R.id.reader_end_tips_tv);

        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mRootView.setLayoutParams(lp);

        mMulu = (DDTextView) mRootView.findViewById(R.id.reader_end_mulu);
        mDownload = (DDTextView) mRootView.findViewById(R.id.reader_end_download);
        mMulu.setOnClickListener(mClickListener);
        mDownload.setOnClickListener(mClickListener);

        BaseReadInfo info = getReadInfo();
        if (!info.isBought()){
            mDownload.setVisibility(GONE);
        }

        addView(mRootView, lp);
        updatePageStyle();
        initGetDataParams();
        getDatas();
    }

    private void initGetDataParams() {
        mStart = 0;
        mEnd = isLandScape() ? 14 : 8;
        mNum = isLandScape() ? 15 : 9;
        mTotal = 0;
    }

    protected BaseReadInfo getReadInfo() {
        return ReaderAppImpl.getApp().getReadInfo();
    }

//    private EveryOneLookBookListModule everyOneLookBookListModule;

    private void getDatas() {
//
    }



    @Override
    public void updatePageStyle() {
        super.updatePageStyle();
//        final int foreColor = getForeColor();
//        if (mTopTipTv != null)
//            mTopTipTv.setTextColor(foreColor);
//        if (mTipTv != null)
//            mTipTv.setTextColor(foreColor);
//        if (mFollowTipTv != null)
//            mFollowTipTv.setTextColor(foreColor);
//        if (mFriendsTv != null)
//            mFriendsTv.setTextColor(foreColor);
//        if (everyOneLookBookListModule != null) {
//            if (isNightMode())
//                everyOneLookBookListModule.setTextColor4Reader(R.color.white, R.color.white);
//            else
//                everyOneLookBookListModule.setTextColor4Reader(R.color.black, R.color.black);
//        }
    }

    @Override
    protected int getForeColor() {
        return isNightMode() ? Color.WHITE : getColorDay();
    }

    @Override
    public void setBookType(int bookType) {
        super.setBookType(bookType);
        printLog(" setBookType " + isFullBook());
        refreshStatus();
    }

    private void refreshStatus() {
        BaseReadInfo info = ReaderAppImpl.getApp().getReadInfo();
        if (info instanceof PartReadInfo) {
            mTopTipTv.setText(R.string.try_read_end);

            PartReadInfo partReadInfo = (PartReadInfo) info;
            if (partReadInfo.isBought()) {
                mTipTv.setText(R.string.reader_trybook_lastpage_tip);

            }else {
                mTipTv.setText(R.string.reader_fullbook_ttsfinish_tip);
            }

        } else {
            updateEpubBtns();
        }
    }

    private boolean isCanPresent(String id) {
        List<String> ids = new ArrayList<String>();
        ids.add(id);
        List<ShelfBook> books = BuyBookService.getInstance(context).getBuyBookById(ids);
        if (books == null || books.size() == 0)
            return false;
        mShelfBook = books.get(0);
        //连载
        if (ShelfBook.BookType.BOOK_TYPE_IS_FULL_YES == mShelfBook.getBookType()) {
            //原创全本

            if (ShelfBook.TryOrFull.GIFT_FULL == mShelfBook.getTryOrFull()) {
                //获赠的全本
                return false;
            }
            if ("1".equals(mShelfBook.getAuthorityType())) {
                //全本购买的
                return true;
            }
            return false;
        }
        if (ShelfBook.BookType.BOOK_TYPE_NOT_NOVEL == mShelfBook.getBookType()) {
            //出版物
            if (ShelfBook.TryOrFull.GIFT_FULL == mShelfBook.getTryOrFull()) {
                return false;
            }
            return true;
        }
        return false;
    }

    private void updateEpubBtns() {
        BaseReadInfo info = getReadInfo();
        if (isFullBook() || info.isBought()) {
            mTopTipTv.setText(R.string.read_end);
            if (info.isBought()) {
                mTipTv.setText(R.string.reader_trybook_lastpage_tip);

            }else {
                mTipTv.setText(R.string.reader_fullbook_ttsfinish_tip);
            }

        } else {

            mTopTipTv.setText(R.string.read_end);
            if (info.isBought()) {
                mTipTv.setText(R.string.reader_trybook_lastpage_tip);

            }else {
                mTipTv.setText(R.string.reader_fullbook_ttsfinish_tip);
            }
        }
    }

    private OnClickListener mClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            int i = v.getId();
            if (i == R.id.view_part_end_buy_tv) {
                readEndViewBuy();

            } else if (i == R.id.reader_end_mulu) {
                if (context instanceof ReadActivity)
                    ((ReadActivity) context).showDirMarkNote(DirectoryMarkNoteActivity.DIR);

            } else if (i == R.id.reader_end_download) {
                if (context instanceof ReadActivity)
                    ((ReadActivity) context).downloadDDReader();

            } else {
            }
        }
    };

    protected void readEndViewBuy() {
        if (getContext() instanceof ReadActivity)
            getReadActivity().readEndViewBuy();
    }

    private ReadActivity getReadActivity() {
        return (ReadActivity) context;
    }

    @Override
    public int getScreenHeight() {
        if (isLandScape()) {
            return mScreenWidth;
        }

        return super.getScreenHeight();
    }

    protected int getColumnNum() {
        return 3;
    }

    protected boolean isLandScape() {
        return getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    @Override
    public int getScreenWidth() {

        if (isLandScape()) {
            return mScreenHeight;
        }

        return super.getScreenWidth();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int screenWidth = getScreenWidth();
        int screenHeight = getScreenHeight();

        int width = 0, height = 0;
        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        switch (widthMode) {
            case View.MeasureSpec.UNSPECIFIED:
                width = screenWidth + mShapeWidth;
                break;
            default:
                width = screenWidth + mShapeWidth;// View.MeasureSpec.getSize(widthMeasureSpec);
        }
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        switch (heightMode) {
            case View.MeasureSpec.UNSPECIFIED:
                height = screenHeight;
                break;
            default:
                height = screenHeight;// View.MeasureSpec.getSize(heightMeasureSpec);
        }
        printLog(" onMeasure width = " + width + ", height = " + height);
        setMeasuredDimension(width, height);
        int c = getChildCount();
        printLog("getChildCount = " + c);

        for (int i = 0; i < c; i++) {
            measureView(getChildAt(i));
        }
    }

    private void measureView(View v) {
        v.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        v.measure(View.MeasureSpec.EXACTLY | (int) (v.getMeasuredWidth()), View.MeasureSpec.EXACTLY | (int) (v.getMeasuredHeight()));

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int width = getScreenWidth();// r - l;
        int height = b - t;
        mRootView.layout(0, 0, width, height);
    }

    private void showToast(String msg) {
    }

    @Override
    protected void attachViewToParent(View child, int index, LayoutParams params) {
        super.attachViewToParent(child, index, params);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        try {
            getContext().unregisterReceiver(mFollowReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        try {
            IntentFilter filter = new IntentFilter();
            filter.addAction(Constant.ACTION_PART_READ_FOLLOW);
            filter.addAction(Constant.ACTION_BOUGHT_EPUB_BOOK);
            getContext().registerReceiver(mFollowReceiver, filter);
            refreshStatus();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private BroadcastReceiver mFollowReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Constant.ACTION_PART_READ_FOLLOW.equals(action)) {
                BaseReadInfo info = getReadInfo();
                PartReadInfo partReadInfo = (PartReadInfo) info;
            }
            if (Constant.ACTION_BOUGHT_EPUB_BOOK.equals(action)) {
                updateEpubBtns();
            }
        }
    };
}
