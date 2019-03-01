package com.dangdang.reader.base;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;

import com.alibaba.fastjson.JSONObject;
import com.dangdang.reader.request.GetHtmlDataStringRequest;
import com.dangdang.zframework.log.LogM;
import com.dangdang.zframework.view.DDWebView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.lang.ref.WeakReference;

public abstract class BaseReaderHtmlFragment extends BaseReaderFragment {
    protected DDWebView mWebView;

    private static final String TO_H5PAGE = "toH5Page";                     // 跳转一般的h5页面
    private static final String TO_SPECIALDETAIL = "toSpecialDetail";       // H5跳转专题页
    private static final String TO_PRODUCT = "toProduct";                   // h5跳单品
    private static final String TO_CHANNELLIST = "toChannelList";           // h5跳频道列表
    private static final String TO_CHANNELDETAIL = "toChannelDetail";       // h5跳频道详情
    private static final String TO_SEARCH = "toSearch";                     // H5跳转搜索页
    private static final String TO_BARDETAIL = "toBarDetail";               // H5跳转吧
    private static final String TO_BARLIST = "toBarList";                   // H5跳转吧推荐列表
    private static final String REFRESH_FINISHED = "refreshFinished";       // 完成下拉刷新的回调接口
    private static final String REFRESH_STATE = "refreshState";             // 刷新状态
    private static final String GET_SHAREINFO = "getShareInfo";             // 获取分享的数据
    private static final String LOCALSTORAGEIMG = "localStorageImg";        // 加载本地图片缓存路径
    private static final String TO_BOOK_STORE_INDEX = "toBookstoreIndex";   // H5跳转书城首页
    private static final String TO_OPEN_LOGIN_INDEX = "toOpenLoginIndex";   // H5跳转登录页
    private static final String HIDE_SOFT_INPUT = "hideSoftInput";          // 隐藏软键盘
    private static final String TO_ARTICLE_PAGE = "toArticlePage";          // 去文章详情

    public final static String H5_PREADDRESS = "/media/h5/ddreader50/";

    protected int mGetScrollState = 0;
    protected int mNotScrollStart = 0;
    protected int mNotScrollEnd = 0;
    public static final int LEFT_CAN = 1;
    public static final int RIGHT_CAN = 2;
    public static final int LEFT_RIGHT_NO = 3;
    public static final int LEFT_RIGHT_CAN = 4;

    public static final int GETIMAGE_SUCCESS = 5;
    public static final int GETIMAGE_FAILED = 6;

    String callbackFunName;

    protected String mOrderSource;
    protected Handler mHandler;

    private static class MyHandler extends Handler {

        private final WeakReference<BaseReaderHtmlFragment> mFragmentView;

        MyHandler(BaseReaderHtmlFragment view) {
            this.mFragmentView = new WeakReference<BaseReaderHtmlFragment>(view);
        }

        @Override
        public void handleMessage(Message msg) {
            BaseReaderHtmlFragment service = mFragmentView.get();
            if (service == null) {
                return;
            }
            try {
                super.handleMessage(msg);
                service.dealMsg(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected void dealMsg(Message msg) {
        switch (msg.what) {
            case GETIMAGE_SUCCESS:
                String remoteUrl = String.valueOf(msg.obj);
                //String localUrl = service.getLocalImgUrl(remoteUrl);
                String localUrl = getLocalImageUrl(remoteUrl);
                if (TextUtils.isEmpty(localUrl)) {
                    handleJavaScriptMethod("", remoteUrl);
                } else {
                    handleJavaScriptMethod(localUrl, remoteUrl);
                }
                break;
            case GETIMAGE_FAILED:
                remoteUrl = String.valueOf(msg.obj);
                handleJavaScriptMethod("", remoteUrl);
                break;
        }
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        mHandler = new MyHandler(this);
    }

    protected void initWebView() {
        mWebView.setHorizontalScrollBarEnabled(false);
        mWebView.setHorizontalScrollbarOverlay(false);
        mWebView.setVerticalScrollBarEnabled(false);
        mWebView.setVerticalScrollbarOverlay(false);
        mWebView.setScrollbarFadingEnabled(false);
        try {
            mWebView.getSettings().setJavaScriptEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mWebView.getSettings().setDefaultTextEncodingName("gbk");
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setAppCacheMaxSize(1024 * 1024 * 8);
        String appCachePath = getActivity().getApplicationContext()
                .getCacheDir().getAbsolutePath();
        // mWebView.getSettings().setBlockNetworkImage(false);
        mWebView.getSettings().setAppCachePath(appCachePath);
        mWebView.getSettings().setAllowFileAccess(true);
        mWebView.getSettings().setAppCacheEnabled(true);
//        mWebView.addJavascriptInterface(mJsHandle, "JSHandle");
        mWebView.setWebChromeClient(mChromeClient);
        mWebView.setOnLongClickListener(new DDWebView.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
    }

    protected final WebChromeClient mChromeClient = new WebChromeClient() {
        @Override
        public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
            String message = consoleMessage.message();
            LogM.d(TAG, "message:" + message);
            if ("Uncaught ReferenceError: refresh is not defined".equals(message)) {
                getHtmlData();
            }
            return true;
        }
    };

    protected void getHtmlData() {
        showGifLoadingByUi(mRootView, -1);
        GetHtmlDataStringRequest request = new GetHtmlDataStringRequest(
                getHtmlUrl(), mHandler);
        sendRequest(request);
    }

    protected abstract String getHtmlUrl();

//    @Override
//    public void callHandler(final String methodName, final String methodParam) {
//        LogM.d(TAG, "methodParam:" + methodParam);
//
//        if (TextUtils.isEmpty(methodName)) {
//            return;
//        }
//        getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    JSONObject json = JSONObject.parseObject(methodParam);
//                    handleH5Method(methodName, json);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//    }

    private static long lastClickTime;

    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        if (time - lastClickTime < 1000) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    private void handleH5Method(String methodName, JSONObject json) {
        LogM.d(TAG, "handleH5Method methodName:" + methodName + "," + json);
        try {
//            if (GET_SHAREINFO.equals(methodName)) {
//                getShareInfo(json);
//            } else if (TO_H5PAGE.equals(methodName)) {
//                toH5Page(json);
//            } else if (TO_PRODUCT.equals(methodName)) {
//                toProduct(json);
//            } else if (TO_CHANNELLIST.equals(methodName)) {
//                toChannelList(json);
//            } else if (TO_CHANNELDETAIL.equals(methodName)) {
//                toChannelDetail(json);
//            } else if (TO_SEARCH.equals(methodName)) {
//                toSearch(json);
//            } else if (TO_SPECIALDETAIL.equals(methodName)) {
//                toDissertaion(json);
//            } else if (REFRESH_FINISHED.equals(methodName)) {
//                boolean isFinished = json.getBoolean("tf");
//                refreshFinished(isFinished);
//            } else if (REFRESH_STATE.equals(methodName)) {
//                boolean state = json.getBoolean("tf");
//                refreshState(state);
//            } else if (LOCALSTORAGEIMG.equals(methodName)) {
//                handleLocalImage(json);
//            } else if (TO_BOOK_STORE_INDEX.equals(methodName)) {
//                toStore();
//            } else if (TO_OPEN_LOGIN_INDEX.equals(methodName)) {
//                toLogin();
//            }  else if (TO_ARTICLE_PAGE.equals(methodName)) {
//                toArticlePage(json);
//            } else if (HIDE_SOFT_INPUT.equals(methodName)) {
//                UiUtil.hideInput(getActivity());
//            }
        } catch (Exception e) {
            e.printStackTrace();
            showToast("数据异常");
        }
    }


    private void handleJavaScriptMethod(String fileUrl, String srcImgUrl) {
        String url;
        if (TextUtils.isEmpty(fileUrl)) {
            url = "javascript:" + callbackFunName + "(" + "'" + fileUrl + "'"
                    + "," + "'" + srcImgUrl + "'" + ")";
        } else {
            url = "javascript:" + callbackFunName + "(" + "'" + "file://"
                    + fileUrl + "'" + "," + "'" + srcImgUrl + "'" + ")";
        }
        mWebView.loadUrl(url);
    }

//    private void loadImage(final String srcImgUrl) {
//        DisplayImageOptions mImageOptions = new DisplayImageOptions.Builder().cacheInMemory(false)
//                .cacheOnDisk(true).considerExifParams(true)
//                .bitmapConfig(Bitmap.Config.RGB_565).build();
//        ImageLoader.getInstance().loadImage(srcImgUrl, mImageOptions,
//                new ImageLoadingListener() {
//
//                    @Override
//                    public void onLoadingStarted(String arg0, View arg1) {
//                    }
//
//                    @Override
//                    public void onLoadingFailed(String url, View arg1,
//                                                FailReason arg2) {
//                        Message m = mHandler.obtainMessage(GETIMAGE_FAILED, url);
//                        mHandler.sendMessage(m);
//                    }
//
//                    @Override
//                    public void onLoadingComplete(String url, View arg1,
//                                                  Bitmap arg2) {
//                        Message m = mHandler.obtainMessage(GETIMAGE_SUCCESS, url);
//                        mHandler.sendMessage(m);
//                    }
//
//                    @Override
//                    public void onLoadingCancelled(String url, View arg1) {
//                        Message m = mHandler.obtainMessage(GETIMAGE_FAILED, url);
//                        mHandler.sendMessage(m);
//                    }
//                });
//    }
//
    private String getLocalImageUrl(String srcImgUrl) {
        File cacheDir = ImageLoader.getInstance().getDiskCache().getDirectory();
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
        String fileUrl = "";
        File f = ImageLoader.getInstance().getDiskCache().get(srcImgUrl);
        if (f.exists()) {
            fileUrl = f.getAbsolutePath();
        }
        return fileUrl;
    }


//    @Override
//    public void onShowToast(String msg) {
//        UiUtil.showToast(getActivity(), msg);
//    }
//
//    @Override
//    public String getServerFont() {
//        return DangdangFileManager.getPreSetTTF();
//    }
//
//    @Override
//    public String localStorageImg(String srcImgUrl) {
//        return DangdangFileManager.getImageCacheDir();
//    }
//
//    @Override
//    public String getParam() {
//        return DangDangParams.getPublicParams();
//    }
//
//    @Override
//    public void getNativeScrollState(int num) {
//        mGetScrollState = num;
//    }
//
//    @Override
//    public void setNotScrollHeight(int start, int end) {
//        mNotScrollStart = UiUtil.dip2px(mContext, start);
//        mNotScrollEnd = UiUtil.dip2px(mContext, end);
//    }

    public abstract void refreshState(boolean state);

    /**
     * 处理webview的back事件
     */
    public void onBack() {
        // TODO 处理goBack事件的问题，如空白页
        try {
            getActivity().finish();
//            if(mWebView != null && mWebView.canGoBack()){
//                mWebView.goBack();
//            } else{
//                getActivity().finish();
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
