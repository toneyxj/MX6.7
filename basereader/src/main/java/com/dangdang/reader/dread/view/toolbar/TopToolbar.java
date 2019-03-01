package com.dangdang.reader.dread.view.toolbar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dangdang.reader.R;
import com.dangdang.reader.cloud.MarkNoteManager;
import com.dangdang.reader.db.service.DDStatisticsService;
import com.dangdang.reader.dread.PubReadActivity;
import com.dangdang.reader.dread.core.base.IEpubReaderController;
import com.dangdang.reader.dread.core.epub.ReaderAppImpl;
import com.dangdang.reader.dread.data.PartReadInfo;
import com.dangdang.reader.dread.data.ReadInfo;
import com.dangdang.reader.dread.format.IndexRange;
import com.dangdang.zframework.log.LogM;
import com.dangdang.zframework.view.DDImageView;
import com.dangdang.zframework.view.DDTextView;

public class TopToolbar extends RelativeLayout {

    private OnClickListener mReaderListener;
    private PubReadActivity.OnBookMarkListener mBookmarkListener;
    private PubReadActivity.OnBookFollowListener mBookFollowListener;
    private DDImageView mBack;
    private DDImageView mBookmark;
    private View mSearch;
    private TextView mBuy;

    private boolean isTTSStop = true;

    public TopToolbar(Context context) {
        super(context);
    }

    public TopToolbar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setButtonClickListener(OnClickListener clickListener) {
        mReaderListener = clickListener;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mBack = (DDImageView) findViewById(R.id.read_top_back);
        mBookmark = (DDImageView) findViewById(R.id.read_top_mark_setting);
        mSearch = findViewById(R.id.read_top_search);
        mBuy = (DDTextView)findViewById(R.id.read_top_download);

        mBack.setOnClickListener(mListener);
        mBookmark.setOnClickListener(mListener);
        mSearch.setOnClickListener(mListener);
        mBuy.setOnClickListener(mListener);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        updateToolbarStatus();
    }

    private OnClickListener mListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            int i = v.getId();
            if (i == R.id.read_top_mark_setting) {
                boolean s = v.isSelected();
                if (s) {
                    DDStatisticsService.getDDStatisticsService(getContext())
                            .addData(DDStatisticsService.DEL_LABLE_IN_READING,
                                    DDStatisticsService.ReferType, "menu",
                                    DDStatisticsService.OPerateTime,
                                    System.currentTimeMillis() + "");
                    mBookmarkListener.removeMark();
                } else {
                    DDStatisticsService.getDDStatisticsService(getContext())
                            .addData(DDStatisticsService.ADD_LABLE_IN_READING,
                                    DDStatisticsService.ReferType, "menu",
                                    DDStatisticsService.OPerateTime,
                                    System.currentTimeMillis() + "");
                    mBookmarkListener.addMark();
                }
                v.setSelected(!s);

            } else {
            }
            mReaderListener.onClick(v);
        }
    };

    public void setMarkListener(PubReadActivity.OnBookMarkListener markListener) {
        mBookmarkListener = markListener;
    }

    public void setFollowListener(PubReadActivity.OnBookFollowListener markListener) {
        mBookFollowListener = markListener;
    }

    private void updateToolbarStatus() {
        if (!isTTSStop) {
            return;
        }
        ReaderAppImpl readerApps = ReaderAppImpl.getApp();
        ReadInfo readInfo = (ReadInfo) readerApps.getReadInfo();
        IEpubReaderController controller = (IEpubReaderController) readerApps
                .getReaderController();
        MarkNoteManager markNoteManager = readerApps.getMarkNoteManager();
        if (controller == null)
            return;
        IndexRange range = controller.getCurrentPageRange();
        boolean exist = false;
        if (range != null) {
			exist = markNoteManager.checkMarkExist(readInfo.getDefaultPid(), readInfo.getEpubModVersion(),
                    readInfo.isBoughtToInt(), readInfo.getChapterIndex(),
                    range.getStartIndexToInt(), range.getEndIndexToInt());
        }
        mBookmark.setSelected(exist);

    }

    protected void printLog(String log) {
        LogM.i(getClass().getSimpleName(), log);
    }

    protected void printLogE(String log) {
        LogM.e(getClass().getSimpleName(), log);
    }
}