package com.dangdang.reader.dread.view;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;

import com.dangdang.reader.R;
import com.dangdang.reader.base.WebBrowserActivity;
import com.dangdang.reader.db.service.DDStatisticsService;
import com.dangdang.reader.domain.YoudaoTransResult;
import com.dangdang.reader.dread.config.ReadConfig;
import com.dangdang.reader.dread.core.epub.GlobalWindow.IDictOperation;
import com.dangdang.reader.request.RequestConstants;
import com.dangdang.reader.request.RequestResult;
import com.dangdang.reader.request.YoudaoTranslateRequest;
import com.dangdang.reader.utils.NetUtils;
import com.dangdang.reader.view.MyPopupWindow;
import com.dangdang.zframework.log.LogM;
import com.dangdang.zframework.plugin.AppUtil;
import com.dangdang.zframework.utils.DRUiUtility;
import com.dangdang.zframework.utils.UiUtil;

public class DictWindow {

    //private final static DangDang_Method YoudaoTranslate = DangDang_Method.YoudaoTranslate;
    private final static String ONLINE_URL_YOUDAO = "http://m.youdao.com/dict?q=";
    private final static String ONLINE_URL_BAIDU = "http://www.baidu.com/s?wd=";

    private Context mContext;
    private View mParent;
    private View mContentView;
    private PopupWindow mWindow;

    private TextView mYaodaoView;
    private TextView mBaiduView;

    private int mHeight = 155;
    private int mDictGreen = Color.parseColor("#2390ec");
    private boolean mIsPdf = false;


    private String mWord;
    private String mExplain;
    private String mLocalExplain;

    private IDictOperation mDictOperation;

    private YoudaoTranslateRequest translateRequest;
    private Handler handler;

    public DictWindow(Context context, View parent) {
        super();
        this.mContext = context;
        this.mParent = parent;
        handler = new MyHandler(this);
        mContentView = View.inflate(mContext, R.layout.read_dictwindow, null);
        mWindow = new MyPopupWindow(mContentView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        View onlineDict = mContentView.findViewById(R.id.read_dict_online);
        mYaodaoView = (TextView) mContentView.findViewById(R.id.read_dict_youdao);
//        mYaodaoView.setVisibility(View.GONE);
        mBaiduView = (TextView) mContentView.findViewById(R.id.read_dict_baidu);
        View enoteView = mContentView.findViewById(R.id.read_dict_e);
        View youdaoAllView = mContentView.findViewById(R.id.read_dict_allresult);
        View youdaoTipView = mContentView.findViewById(R.id.read_dict_youdaotip);//
        View backView = mContentView.findViewById(R.id.read_dict_back);//

        //mYaodaoView.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);

        onlineDict.setOnClickListener(mClickListener);
        mYaodaoView.setOnClickListener(mClickListener);
        mBaiduView.setOnClickListener(mClickListener);
        enoteView.setOnClickListener(mClickListener);
        youdaoAllView.setOnClickListener(mClickListener);
        youdaoTipView.setOnClickListener(mClickListener);
        backView.setOnClickListener(mClickListener);

        mDictGreen = mContext.getResources().getColor(R.color.blue_2390ec);
        mHeight = (int) (DRUiUtility.getDensity() * 150);

        //showYoudaoTip();
    }

    final OnClickListener mClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int i = v.getId();
            if (i == R.id.read_dict_online) {
                if (!NetUtils.isNetworkConnected(mContext)) {
                    printLog(" no net ");
                    showToast(mContext.getString(R.string.network_exp));
                    return;
                }
                showOrHideYoudaoBaidu(true);

                View outView = mContentView.findViewById(R.id.read_dict_online);
                View inView = mContentView.findViewById(R.id.read_dict_top_olayout);
                outView.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.push_left_out_fast));
                inView.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.push_right_in_fast));

            } else if (i == R.id.read_dict_back) {
                DDStatisticsService.getDDStatisticsService(mContext).addData(
                        DDStatisticsService.RETURN_FROM_DICTIONARY,
                        DDStatisticsService.OPerateTime,
                        System.currentTimeMillis() + "");
                /*outView = mContentView.findViewById(R.id.read_dict_top_olayout);
                inView = mContentView.findViewById(R.id.read_dict_online);
				outView.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.push_left_out_fast));
				inView.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.push_right_in_fast));
				showOrHideYoudaoBaidu(false);*/
                setDict(mWord, mLocalExplain, true);

            } else if (i == R.id.read_dict_youdao) {
                DDStatisticsService.getDDStatisticsService(mContext).addData(
                        DDStatisticsService.HIT_YOUDAO_DICTIONARY,
                        DDStatisticsService.OPerateTime,
                        System.currentTimeMillis() + "");
                getYoudaoExplain(mWord);
                setDictTip(R.string.list_loading_tip);

            } else if (i == R.id.read_dict_baidu) {
                DDStatisticsService.getDDStatisticsService(mContext).addData(
                        DDStatisticsService.HIT_BAIDU_DICTIONARY,
                        DDStatisticsService.OPerateTime,
                        System.currentTimeMillis() + "");
                startBrowser(getBaiduSearchUrl(mWord));
                if (mDictOperation != null) {
                    mDictOperation.onBaidu(mWord);
                }

            } else if (i == R.id.read_dict_allresult || i == R.id.read_dict_youdaotip) {
                startBrowser(getYoudaoSearchUrl(mWord));
                if (mDictOperation != null) {
                    mDictOperation.onYoudao(mWord);
                }

            } else if (i == R.id.read_dict_e) {
                DDStatisticsService.getDDStatisticsService(mContext).addData(
                        DDStatisticsService.ADD_NOTE_IN_DICTIONARY,
                        DDStatisticsService.OPerateTime,
                        System.currentTimeMillis() + "");
                if (mDictOperation != null) {
                    mDictOperation.onDictNote(mWord, mExplain);
                }

            }
        }

    };

    private void getYoudaoExplain(String word) {
        if (TextUtils.isEmpty(word))
            return;
        if (translateRequest == null) {
            translateRequest = new YoudaoTranslateRequest(handler);
        }
        translateRequest.setParamater(word);
        AppUtil.getInstance(mContext).getRequestQueueManager().sendRequest(translateRequest, getClass().getSimpleName());
    }

    private void onRequestFinish(RequestResult result) {
        showYoudaoTip();
        if (result != null) {
            final YoudaoTransResult youdaoExplain = (YoudaoTransResult) result.getResult();
            boolean hasSucc = false;
            if (youdaoExplain != null) {
                final String viewStr = youdaoExplain.getViewString();
                printLog(" onCommandResult viewStr = " + viewStr);
                if (!TextUtils.isEmpty(viewStr)) {
                    hasSucc = true;
                    setDict(mWord, viewStr, false);
                }
            }
            if (!hasSucc) {
                setDictTip(R.string.online_dict_noresult);
            }
        } else {
            setDictTip(R.string.online_dict_failed);
        }
    }

    private String getBaiduSearchUrl(String word) {
        return ONLINE_URL_BAIDU + word;
    }

    private String getYoudaoSearchUrl(String word) {
        return ONLINE_URL_YOUDAO + word;
    }

    private void startBrowser(String url) {
        Intent intent = new Intent(mContext, WebBrowserActivity.class);
        intent.putExtra(WebBrowserActivity.KEY_URL, url);
        intent.putExtra(WebBrowserActivity.KEY_FULLSCREEN, true);
        mContext.startActivity(intent);
    }

    /**
     * @param showOrHide true: show
     */
    private void showOrHideYoudaoBaidu(boolean showOrHide) {
        View onLineView = mContentView.findViewById(R.id.read_dict_online);
        View dictRightLayout = mContentView.findViewById(R.id.read_dict_top_olayout);
        onLineView.clearAnimation();
        dictRightLayout.clearAnimation();
        onLineView.setVisibility(showOrHide ? View.GONE : View.VISIBLE);
        dictRightLayout.setVisibility(showOrHide ? View.VISIBLE : View.GONE);
    }

    public void setDict(String word, CharSequence explain, boolean isLocal) {
        mWord = word;
        mExplain = explain.toString();
        if (isLocal) {
            mLocalExplain = mExplain;
        }

        TextView wordView = (TextView) mContentView.findViewById(R.id.read_dict_word);
        TextView explainView = (TextView) mContentView.findViewById(R.id.read_dict_text);
        wordView.setText(word);

        View dictE = mContentView.findViewById(R.id.read_dict_e);
        if (TextUtils.isEmpty(explain)) {//list_loading_tip
            explain = mContext.getString(R.string.dict_noresult_tip);
            dictE.setVisibility(View.GONE);
            explainView.setGravity(Gravity.CENTER);
        } else {
            dictE.setVisibility(mIsPdf ? View.GONE : View.VISIBLE);
            explainView.setGravity(Gravity.CENTER_VERTICAL);
        }
        explainView.setText(explain);
    }

    private void setDictTip(int resid) {
        TextView explainView = (TextView) mContentView.findViewById(R.id.read_dict_text);
        explainView.setText(resid);
        explainView.setGravity(Gravity.CENTER);
    }

    public void show(int x, int y, int floatWindowTop, int floatWindowBottom) {
        mWindow.showAtLocation(mParent, getGravity(y, floatWindowTop, floatWindowBottom), 0, 0);
        hideYoudaoTip();
        showOrHideYoudaoBaidu(false);
        ScrollView scrollView = (ScrollView) mContentView.findViewById(R.id.read_dict_text_sv);
        scrollView.scrollTo(0, 0);
    }

    private int getGravity(int y, int floatWindowTop, int floatWindowBottom) {
        int gravity = Gravity.BOTTOM;
        if (ReadConfig.getConfig().getReadHeight() - y <= mHeight
                && (floatWindowBottom > floatWindowTop && mHeight < floatWindowTop)) {
            gravity = Gravity.TOP;
        }

        if (floatWindowBottom > floatWindowTop && ReadConfig.getConfig().getReadHeight() - mHeight < floatWindowBottom) {
            gravity = Gravity.TOP;
        }
        return gravity;
    }

    public boolean isShowing() {
        return mWindow != null && mWindow.isShowing();
    }

    public void hide() {
        if (mWindow != null) {
            mWindow.dismiss();
        }
    }

    public void showYoudaoTip() {
        View allResultView = mContentView.findViewById(R.id.read_dict_allresult);
        TextView youdaoTip = (TextView) mContentView.findViewById(R.id.read_dict_youdaotip);

        SpannableStringBuilder builder = new SpannableStringBuilder(youdaoTip.getText().toString());
        ForegroundColorSpan redSpan = new ForegroundColorSpan(mDictGreen);
        builder.setSpan(redSpan, 1, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        youdaoTip.setText(builder);

        allResultView.setVisibility(View.VISIBLE);
        youdaoTip.setVisibility(View.VISIBLE);
    }

    public void hideYoudaoTip() {
        mContentView.findViewById(R.id.read_dict_allresult).setVisibility(View.GONE);
        mContentView.findViewById(R.id.read_dict_youdaotip).setVisibility(View.GONE);
    }

    public void setDictOperation(IDictOperation dictOperation) {
        this.mDictOperation = dictOperation;
    }


    private void showToast(String msg) {
        UiUtil.showToast(mContext, msg);

    }

    public void initIsPdf(boolean isPdf) {
        this.mIsPdf = isPdf;
		/*if(isPdf){
			mContentView.findViewById(R.id.read_dict_e).setVisibility(View.GONE);
		}*/
    }

    protected void printLog(String word) {
        LogM.i(getClass().getSimpleName(), word);
    }

    private static class MyHandler extends Handler {
        private final WeakReference<DictWindow> mFragmentView;

        MyHandler(DictWindow view) {
            this.mFragmentView = new WeakReference<DictWindow>(view);
        }

        @Override
        public void handleMessage(Message msg) {
            DictWindow service = mFragmentView.get();
            if (service != null) {
                super.handleMessage(msg);
                try {
                    switch (msg.what) {
                        case RequestConstants.MSG_WHAT_REQUEST_DATA_SUCCESS:
                        case RequestConstants.MSG_WHAT_REQUEST_DATA_FAIL:
                            service.onRequestFinish((RequestResult) msg.obj);
                            break;
                    }
                } catch (Exception e) {
                    LogM.e(DictWindow.class.getSimpleName(), e.toString());
                }
            }
        }

    }
}
