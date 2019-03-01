package com.dangdang.reader.dread.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ListView;

import com.dangdang.reader.DDApplication;
import com.dangdang.reader.dread.font.FontDownLoadRequest;
import com.dangdang.reader.R;
import com.dangdang.reader.dread.FontsActivity;
import com.dangdang.reader.dread.adapter.MyFontsAdapter;
import com.dangdang.reader.dread.data.FontDomain;
import com.dangdang.reader.dread.font.DownloadDb;
import com.dangdang.reader.dread.font.DownloadDb.DType;
import com.dangdang.reader.dread.font.FontDownload;
import com.dangdang.reader.dread.font.FontListHandle;
import com.dangdang.reader.request.MultiGetMyFontListRequest;
import com.dangdang.reader.request.ResultExpCode;
import com.dangdang.reader.utils.AccountManager;
import com.dangdang.reader.utils.Constant;
import com.dangdang.reader.view.CommonDialog;
import com.dangdang.thirdpart.umeng.UmengStatistics;
import com.dangdang.zframework.network.download.DownloadConstant.Status;
import com.dangdang.zframework.network.download.DownloadManagerFactory;
import com.dangdang.zframework.network.download.IDownloadManager;
import com.dangdang.zframework.network.download.IDownloadManager.DownloadExp;
import com.dangdang.zframework.utils.MemoryStatus;
import com.dangdang.zframework.utils.NetUtil;
import com.dangdang.zframework.utils.StringUtil;
import com.dangdang.zframework.utils.UiUtil;
import com.dangdang.zframework.view.DDImageView;
import com.dangdang.zframework.view.DDTextView;
import com.mx.mxbase.constant.APPLog;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 我的字体页面, 包含免费和已购字体
 */
public class MyFontsFragment extends FontBaseFragment {
    private ListView mListView;
    private MyFontsAdapter mMyFontsAdapter;
    private List<FontDomain> mFonts = new ArrayList<FontDomain>();
    private FontDomain mChooseFont;
    private FontListHandle mFontHandle;
    private CommonDialog mNetTypeDialog;
    private DownloadDb mDownService;

    private DownloadManagerFactory.DownloadModule module = new DownloadManagerFactory.DownloadModule("font");
    private IDownloadManager mDownloadManager;
    private Class<?> modleKey = FontsActivity.class;

    private final static int MSG_WHAT_NOTIFY_ADAPTER_BUY = 0x01;
    private final static int MSG_WHAT_UPDATE_PROGRESS_BUY = 105;
    private final static int MSG_WHAT_UPDATE_STATUS_BUY = 108;

    private final static String KEY_INDENTITYID = "key_indentityId";

    private Map<String, FontDomain> indentityFontMaps = new Hashtable<String, FontDomain>();
    private boolean mPause;
    private View mLoginHintLayout;

    @Override
    public void onCreateInit(Bundle savedInstanceState) {
        mView = View.inflate(mContext, R.layout.fragment_my_fonts, null);
        mFontHandle = FontListHandle.getHandle(getActivity().getApplicationContext());
        mDownService = new DownloadDb(getActivity().getApplicationContext());
        initView();
        initDownLoad();

        getData();
        getFontsActivity().showGifLoadingByUi();
    }

    private void getData() {
        if (getActivity() == null)
            return;

        AccountManager am = new AccountManager(getActivity());
        MultiGetMyFontListRequest request = new MultiGetMyFontListRequest(mHandler, am.getToken(), FontListHandle.getHandle(mContext).getPresetDefaultFontName());
        sendRequest(request);
    }

    private void initDownLoad() {
        module.setTaskingSize(1);
        mDownloadManager = DownloadManagerFactory.getFactory().create(module);
        mDownloadManager.registerDownloadListener(modleKey, mDownloadListener);
    }

    private void initView() {
        mListView = (ListView) findViewById(R.id.fragment_my_fonts_content_lv);
        mMyFontsAdapter = new MyFontsAdapter(mContext, mFonts, mListView);
        mMyFontsAdapter.setDefaultFontIdentity(mFontHandle.getDefaultFontFlag());
        mListView.setAdapter(mMyFontsAdapter);
        FontItemClickListener mItemClickListener = new FontItemClickListener();
        mMyFontsAdapter.setListener(mCheckedChangeListener, mItemClickListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        mPause = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPause = false;
    }

    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            getData();
        }
    }

    @Override
    public void onDestroyImpl() {
        if (mChooseFont != null) {
            notifyFontChange(mChooseFont);
        }
        mRadioList.clear();
        indentityFontMaps.clear();
        if(mDownloadManager != null && modleKey != null)
        	mDownloadManager.unRegisterDownloadListener(modleKey);
    }

    public void handleFail(ResultExpCode expCode) {
        if (mFonts == null)
            mFonts = new ArrayList<FontDomain>();
        mFonts.clear();
        List<FontDomain> fontLists = getDownloadFinishFonts(null);
        if (fontLists != null) {
            mFonts.addAll(fontLists);
        }
        if (mFonts != null && mFonts.size() > 0) {
            mMyFontsAdapter.setDatas(mFonts);
            mDownloadHandler.sendEmptyMessage(MSG_WHAT_NOTIFY_ADAPTER_BUY);
        } else {
            String text = getPromptText(expCode);
            UiUtil.showToast(getActivity(), text);
        }
    }

    private String getPromptText(ResultExpCode expCode) {
        String text = getString(R.string.request_get_data_error);
        if (expCode != null) {
            if (!TextUtils.isEmpty(expCode.errorMessage)) {
                text = expCode.errorMessage;
            } else if (ResultExpCode.ERRORCODE_NONET.equals(expCode.errorCode)) {
                text = getString(R.string.time_out_tip);
            }
        }
        return text;
    }

    protected List<FontDomain> getDownloadFinishFonts(DType type) {
        List<FontDomain> fontLists = null;
        if (type == null) {
            fontLists = mFontHandle.getDownloadFontList();
        } else {
            fontLists = mFontHandle.getDownloadFontList(type);
        }
        return fontLists;
    }

    public void handleSuccess(List<FontDomain> fontList) {
        APPLog.e(fontList.toString());
        for (FontDomain fontDomain : fontList) {
            FontDownload sDownload = mDownService.getDownload(fontDomain.productId);
            if (sDownload != null) {
                // sDownload.progress;
                fontDomain.progress = mFontHandle.getFontDownloadSize(fontDomain.productId);
                fontDomain.totalSize = sDownload.totalSize;
                if (!StringUtil.isEmpty(sDownload.status)) {
                    fontDomain.status = Status.convert(sDownload.status);
                }
                fontDomain.fontZipPath = sDownload.saveDir;
                if (fontDomain.status == Status.FINISH) {
                    String sourceFile = fontDomain.fontZipPath;
                    String ttfPath = mFontHandle.getTTfDestFile(sourceFile, fontDomain.productId);
                    if (mFontHandle.ttfFileExists(ttfPath)) {
                        fontDomain.fontFtfPath = ttfPath;
                    } else {
                        mDownService.updateStatusById(sDownload.indentityId, Status.UNSTART.getStatus());
                        fontDomain.status = Status.UNSTART;
                    }
                }
            }
            indentityFontMaps.put(fontDomain.productId, fontDomain);
        }
        mMyFontsAdapter.setDatas(fontList);
        mMyFontsAdapter.notifyDataSetChanged();
    }

    private class FontItemClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            printLog(" [mDownloadOclOfMyFont]" + v.getTag());

            if (isNet()) {
                if (!hasAvailable()) {
                    UiUtil.showToast(getActivity(), R.string.externalmemory_few);
                }
                int netType = NetUtil.getConnectedType(getActivity());
                boolean mobileNetType = DDApplication.getApplication().isMobileNetAllowDownload();
                if (netType == ConnectivityManager.TYPE_MOBILE && !mobileNetType) {
                    showNetTypeDialog(v);
                    return;
                }
                int selectPos = (Integer) v.getTag();
                FontDomain fontDomain = mFonts.get(selectPos);
                handleClickListenerOfFont(fontDomain);
            } else {
                UiUtil.showToast(getActivity(), R.string.time_out_tip);
            }
        }
    }

    public boolean isNet() {
        return NetUtil.isNetworkConnected(getActivity());
    }

    public boolean hasAvailable() {
        return MemoryStatus.hasAvailable(MemoryStatus.MIN_SPACE, 1024 * 1024 * 1);
    }

    private void showNetTypeDialog(View parentView) {
        final View clickView = parentView;
        if (mNetTypeDialog == null) {
            mNetTypeDialog = new CommonDialog(getActivity(), R.style.dialog_commonbg);
        }
        mNetTypeDialog.setInfo(getString(R.string.before_download_info_tip));
        mNetTypeDialog.setRightButtonText(getString(R.string.before_download_continue));
        mNetTypeDialog.setOnRightClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DDApplication.getApplication().setIsMobileNetAllowDownload(true);
                int selectPos = (Integer) clickView.getTag();
                FontDomain fontDomain = mFonts.get(selectPos);
                handleClickListenerOfFont(fontDomain);
                mNetTypeDialog.dismiss();
            }
        });
        mNetTypeDialog.setLeftButtonText(getString(R.string.before_download_pause));
        mNetTypeDialog.setOnLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNetTypeDialog.dismiss();
            }
        });
        mNetTypeDialog.show();
    }

    protected void handleClickListenerOfFont(FontDomain fontDomain) {
        final String indentityId = fontDomain.productId;
        String url = fontDomain.getDownloadURL();
        Status status = Status.UNSTART;
        long start = mFontHandle.getFontDownloadSize(indentityId);
        File dest = mFontHandle.getFontSaveFile(indentityId);
        long totalSize = 0;
        FontDownload sDownload = mDownService.getDownload(indentityId);
        if (sDownload != null) {
            totalSize = sDownload.totalSize;
            status = Status.convert(sDownload.status);
        }
        if (fontDomain.getProductname().equals("汉仪丫丫体")) {
            UmengStatistics.onEvent(getActivity(), "hanyiyaya_freefont");
        } else if (fontDomain.getProductname().equals("方正兰亭黑")) {
            UmengStatistics.onEvent(getActivity(), "fangzhenglanting_freefont");
        } else if (fontDomain.getProductname().equals("方正宋体")) {
            UmengStatistics.onEvent(getActivity(), "fangzhengsong_freefont");
        } else if (fontDomain.getProductname().equals("汉仪细等线")) {
            UmengStatistics.onEvent(getActivity(), "hanyixideng_freefont");
        }
        // download
        fontDownload(indentityId, status, url, dest, start, totalSize);
        fontDomain.status = Status.PENDING;
        if (sDownload == null) {
            fontDomain.fontZipPath = dest.getAbsolutePath();
            if (fontDomain.freeBook)
                saveDownloadRecord(fontDomain, DownloadDb.DType.FONT_FREE);
            else
                saveDownloadRecord(fontDomain, DType.FONT_CHARGE);
        }
    }

    private void fontDownload(String indentityId, Status status, String url, File dest, long start, long totalSize) {
        switch (status) {
            case UNSTART:
            case FAILED:
            case PAUSE:
                startDownload(indentityId, url, start, totalSize, dest);
                updateDownloadStatus(indentityId, Status.PENDING);
                break;
            case DOWNLOADING:
            case RESUME:
            case PENDING:
                pauseDownload(indentityId, url, start, totalSize, dest);
                break;
            default:
                break;
        }
        return;
    }

    private void pauseDownload(String indentityId, String url, long start, long totalSize, File dest) {
        FontDownLoadRequest request = new FontDownLoadRequest(module);
        request.setParams(indentityId, start, totalSize, url, dest);
        mDownloadManager.pauseDownload(request);
        return;
    }

    private void startDownload(String indentityId, String url, long start, long totalSize, File dest) {
        FontDownLoadRequest request = new FontDownLoadRequest(module);
        request.setParams(indentityId, start, totalSize, url, dest);
        mDownloadManager.startDownload(request);
        return;
    }

    private void updateDownloadStatus(String indentityId, Status status) {
        if (!isAdded()) {
            return;
        }
        FontDomain fontDomain = indentityFontMaps.get(indentityId);
        if (fontDomain != null) {
            fontDomain.status = status;
        }
        // status

        View v = mListView.findViewWithTag(indentityId);
        if (v != null) {
            DDImageView progressView = (DDImageView) v.findViewById(R.id.fragment_font_item_download_view);
            if (progressView != null) {
                // progressView.setDownloadStatus(status);
                setDownloadStatus(progressView, status);
            }

            DDTextView progressText = (DDTextView) v.findViewById(R.id.fragment_font_item_download_progress);
            if (progressText != null) {
                if (status == Status.FAILED) {
                    progressText.setText(getString(R.string.try_again));
                } else if (status == Status.PENDING) {
                    progressText.setText(getString(R.string.downloadstatus_waito));
                } else if (status == Status.PAUSE) {
                    progressText.setText(getString(R.string.downloadstatus_pauseo));
                }
            }
        }
    }

    private void saveDownloadRecord(FontDomain freeFont, DownloadDb.DType type) {
        String indentity = freeFont.productId;
        String url = freeFont.getDownloadURL();
        String fontZipPath = freeFont.fontZipPath;
        long progress = freeFont.progress;
        long totalSize = freeFont.totalSize;
        String status = freeFont.status.getStatus();
        String data = freeFont.jsonStr;
        String user = mFontHandle.getUserName();// AccountManager.getUsername();
        DownloadDb.DType tp = type;
        mDownService.saveDownload(indentity, url, fontZipPath, progress, totalSize, status, data, user, tp);
    }

    final IDownloadManager.IDownloadListener mDownloadListener = new IDownloadManager.IDownloadListener() {

        @Override
        public void onDownloading(IDownloadManager.DownloadInfo info) {
            String indentityId = (String) info.download.getTag();
            long progress = info.progress.progress;
            long total = info.progress.total;

            if (!mPause) {
                sendMsgUpdateProgress(indentityId, progress, total);
            }

            Status status = Status.DOWNLOADING;
            if (status != Status.convert(mDownService.getStatusByIndentityId(indentityId))) {
                sendMsgUpdateDownloadStatus(indentityId, status);
                mDownService.updateStatusById(indentityId, status.getStatus());
            }
        }

        @Override
        public void onPauseDownload(IDownloadManager.DownloadInfo info) {
            printLog("onPauseDownload[" + info.download.getTag() + "]{progress=" + info.progress.progress + ",total=" + info.progress.total
                    + ", info=" + info + "}");

            String indentityId = (String) info.download.getTag();
            Status status = Status.PAUSE;

            sendMsgUpdateDownloadStatus(indentityId, status);
            mDownService.updateStatusById(indentityId, status.getStatus());
        }

        @Override
        public void onDownloadFinish(IDownloadManager.DownloadInfo info) {
            printLog("onDownloadFinish[" + info.download.getTag() + "]{progress=" + info.progress.progress + ",total="
                    + info.progress.total + ", info=" + info + "}");
            String sourceFile = info.file.getAbsolutePath();
            String indentityId = (String) info.download.getTag();

            Status status;
            File fontFile = new File(sourceFile);
            if (fontFile.exists() && fontFile.length() > 1024) {
                mFontHandle.addUnZip(sourceFile, indentityId);
                status = Status.FINISH;
            } else {
                status = Status.FAILED;
            }
            sendMsgUpdateDownloadStatus(indentityId, status);
            mDownService.updateStatusById(indentityId, status.getStatus());
        }

        @Override
        public void onFileTotalSize(IDownloadManager.DownloadInfo info) {
            printLog("onFileTotalSize[" + info.download.getTag() + "]{progress=" + info.progress.progress + ",Total=" + info.progress.total
                    + "}");

            String url = info.url;
            long totalSize = info.progress.total;
            mDownService.updateTotalSize(url, totalSize);
        }

        @Override
        public void onDownloadFailed(IDownloadManager.DownloadInfo info, IDownloadManager.DownloadExp exp) {
            printLog("onDownloadFailed[" + info.download.getTag() + "]{" + info + "}");
            /*
             * if (exp != null && exp.headers != null) { UpgradeHeader
			 * upgradeHeader = exp.headers.upgradeHeader; ErrorHeader
			 * errorHeader = exp.headers.errorHeader; if (upgradeHeader != null
			 * && upgradeHeader.isCompatible()) {
			 * processCompelUpgrade(upgradeHeader); } else if (errorHeader !=
			 * null) { if (errorHeader.errorCode == MErrorCode.E_Token_Bad ||
			 * errorHeader.errorCode == MErrorCode.E_Token_Bad2) {
			 * processTokenBad(); } } }
			 */
            String indentityId = (String) info.download.getTag();
            Status status = Status.FAILED;

            sendMsgUpdateDownloadStatus(indentityId, status);
            mDownService.updateStatusById(indentityId, status.getStatus());
            expPrompt(exp);
        }

        private void expPrompt(IDownloadManager.DownloadExp exp) {
            final int statusCode = exp.statusCode;
            int res = -1;
            switch (statusCode) {
                case DownloadExp.CODE_NET:
                    res = R.string.time_out_tip;
                    break;
                case DownloadExp.CODE_WRITEFILE:
                    res = R.string.writefile_error;
                    break;
            }
        }
    };

    private void sendMsgUpdateDownloadStatus(String indentityId, Status status) {
        Message msg = mDownloadHandler.obtainMessage(MSG_WHAT_UPDATE_STATUS_BUY);
        msg.obj = status;
        msg.getData().putString(KEY_INDENTITYID, indentityId);
        mDownloadHandler.sendMessage(msg);
        if (status == Status.FINISH) {
            mDownloadHandler.sendEmptyMessage(MSG_WHAT_NOTIFY_ADAPTER_BUY);
        }
    }

    private void sendMsgUpdateProgress(String indentityId, long progress, long total) {
        Message msg = mDownloadHandler.obtainMessage(MSG_WHAT_UPDATE_PROGRESS_BUY);
        msg.obj = indentityId;
        msg.arg1 = (int) progress;
        msg.arg2 = (int) total;
        mDownloadHandler.sendMessage(msg);
    }

    final Handler mDownloadHandler = new DownloadHandler(this);

    static class DownloadHandler extends Handler {
        private final WeakReference<MyFontsFragment> mFragment;

        public DownloadHandler(MyFontsFragment mFragment) {
            this.mFragment = new WeakReference<MyFontsFragment>(mFragment);
        }

        public void handleMessage(Message msg) {
            if (mFragment.get() == null)
                return;
            String indentityId = null;
            Status status = null;
            int progress;
            int total;
            switch (msg.what) {
                case MSG_WHAT_UPDATE_PROGRESS_BUY:
                    indentityId = (String) msg.obj;
                    progress = msg.arg1;
                    total = msg.arg2;
                    mFragment.get().updateDownloadProgress(indentityId, progress, total);
                    break;
                case MSG_WHAT_UPDATE_STATUS_BUY:
                    indentityId = msg.getData().getString(KEY_INDENTITYID);
                    status = (Status) msg.obj;
                    mFragment.get().updateDownloadStatus(indentityId, status);
                    break;
                case MSG_WHAT_NOTIFY_ADAPTER_BUY:
                    mFragment.get().notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }
    }

    public void notifyDataSetChanged() {
        mMyFontsAdapter.notifyDataSetChanged();
    }

    private void updateDownloadProgress(String indentityId, int progress, int total) {
        FontDomain fontDomain = indentityFontMaps.get(indentityId);
        if (fontDomain != null) {
            fontDomain.progress = progress;
            fontDomain.totalSize = total;
        }
        // download progress
        View v = mListView.findViewWithTag(indentityId);
        if (v != null) {
            float mPrgs = (progress * 100f / total);
            DDTextView progressText = (DDTextView) v.findViewById(R.id.fragment_font_item_download_progress);
            if (progressText != null) {
                int mPrgsI = (int) Math.rint(mPrgs);
                progressText.setText(mPrgsI + "%");
            }
        }
    }

    public void setDownloadStatus(DDImageView downloadView, Status status) {
        switch (status) {
            case DOWNLOADING:
            case RESUME:
                downloadView.setImageResource(R.drawable.font_pause);
                break;
            case UNSTART:
            case FAILED:
                downloadView.setImageResource(R.drawable.font_download);
                break;
            case PAUSE:
            case PENDING:
            case FINISH:
                downloadView.setImageResource(R.drawable.font_download);
                break;
        }
    }

    final Handler mHandler = new MyHandler(this);

    final CompoundButton.OnCheckedChangeListener mCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (mFonts.size() < 1)
                return;
            int selectPos = (Integer) buttonView.getTag();
            FontDomain fontDomain = mFonts.get(selectPos);
            handleOnCheckedChanged(fontDomain, buttonView, isChecked);
        }
    };

    private void handleOnCheckedChanged(FontDomain fontDomain, CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            String fIndentityId = mFontHandle.getDefaultFontFlag();
            if (!fIndentityId.equals(fontDomain.productId)) {
                mChooseFont = fontDomain;
            }

            setTTfPath(fontDomain);
            saveChooseFont(fontDomain);
            reSetOtherRadio(buttonView);
        } else {
            mRadioList.remove(buttonView);
        }
    }

    private void setTTfPath(FontDomain fontDomain) {
        String ttfpath = fontDomain.fontFtfPath;
        if (StringUtil.isEmpty(ttfpath)) {
            String sourceFile = fontDomain.fontZipPath;
            if (!StringUtil.isEmpty(sourceFile)) {
                String productId = fontDomain.productId;
                ttfpath = mFontHandle.getTTfDestFile(sourceFile, productId);
                fontDomain.fontFtfPath = ttfpath;
            }
        }
    }

    private void saveChooseFont(FontDomain fontDomain) {
        String indentityId = fontDomain.productId;
        mFontHandle.setDefaultFont(indentityId);
        mFontHandle.setDefaultFontPath(fontDomain.fontFtfPath);
        mFontHandle.setDefaultFontName(fontDomain.getProductname());

        setDefaultFontIdentity();// TODO repet set default font
    }

    private void setDefaultFontIdentity() {
        String defaultIndentity = mFontHandle.getDefaultFontFlag();
        mMyFontsAdapter.setDefaultFontIdentity(defaultIndentity);
    }

    private List<CompoundButton> mRadioList = new CopyOnWriteArrayList<CompoundButton>();

    private void reSetOtherRadio(CompoundButton currentRadio) {
        String defaultFont = mFontHandle.getDefaultFontFlag();
        for (CompoundButton radio : mRadioList) {
            String productId = (String) radio.getTag(R.id.fragment_font_item_radiobtn);
            if (!radio.equals(currentRadio) && !productId.equals(defaultFont)) {
                radio.setChecked(false);
            }
        }
        if (!mRadioList.contains(currentRadio)) {
            mRadioList.add(currentRadio);
        }
    }

    private void notifyFontChange(FontDomain fontDomain) {
        final String action = Constant.ACTION_READER_FONT_TYPE;
        Intent intent = new Intent();
        intent.setAction(action);
        getActivity().sendBroadcast(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_LOGIN:
                if (resultCode == Activity.RESULT_OK) {
                    getData();
                }
                break;
            default:
                break;
        }
    }
}
