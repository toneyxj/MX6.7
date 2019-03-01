package com.dangdang.reader.dread;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.dangdang.execption.FileFormatException;
import com.dangdang.reader.Constants;
import com.dangdang.reader.DDApplication;
import com.dangdang.reader.R;
import com.dangdang.reader.cloud.CloudSyncConfig;
import com.dangdang.reader.cloud.CloudSyncConvert;
import com.dangdang.reader.cloud.MarkData;
import com.dangdang.reader.cloud.MarkNoteManager;
import com.dangdang.reader.cloud.MarkNoteManager.OperateType;
import com.dangdang.reader.cloud.NoteData;
import com.dangdang.reader.cloud.Status;
import com.dangdang.reader.db.service.DDStatisticsService;
import com.dangdang.reader.db.service.ShelfBookService;
import com.dangdang.reader.domain.CloudDataList;
import com.dangdang.reader.domain.CloudReadProgress;
import com.dangdang.reader.dread.config.ParserStatus;
import com.dangdang.reader.dread.config.ReadConfig;
import com.dangdang.reader.dread.core.base.BaseReaderApplicaion.IBookParserListener;
import com.dangdang.reader.dread.core.base.BaseReaderApplicaion.IReaderEventListener;
import com.dangdang.reader.dread.core.base.BaseReaderApplicaion.ParserProgress;
import com.dangdang.reader.dread.core.base.BaseReaderWidget;
import com.dangdang.reader.dread.core.base.GoToParams;
import com.dangdang.reader.dread.core.base.IEpubReaderController;
import com.dangdang.reader.dread.core.base.IFunctionManager;
import com.dangdang.reader.dread.core.base.IMediaInterface;
import com.dangdang.reader.dread.core.base.IReaderApplication.IAbortParserListener;
import com.dangdang.reader.dread.core.base.IVideoInterface;
import com.dangdang.reader.dread.core.epub.EpubReaderController;
import com.dangdang.reader.dread.core.epub.EpubReaderWidget;
import com.dangdang.reader.dread.core.epub.GlobalWindow;
import com.dangdang.reader.dread.core.epub.IGlobalWindow;
import com.dangdang.reader.dread.core.epub.IGlobalWindow.IOnDisMissCallBack;
import com.dangdang.reader.dread.core.epub.ReaderAppImpl;
import com.dangdang.reader.dread.data.BookMark;
import com.dangdang.reader.dread.data.BookNote;
import com.dangdang.reader.dread.data.BookNoteDataWrapper;
import com.dangdang.reader.dread.data.MarkKey;
import com.dangdang.reader.dread.data.NoteKey;
import com.dangdang.reader.dread.data.PartReadInfo;
import com.dangdang.reader.dread.data.ReadInfo;
import com.dangdang.reader.dread.format.BaseBookManager;
import com.dangdang.reader.dread.format.BaseReadInfo;
import com.dangdang.reader.dread.format.Book;
import com.dangdang.reader.dread.format.Chapter;
import com.dangdang.reader.dread.format.DDFile.FileType;
import com.dangdang.reader.dread.format.IBook;
import com.dangdang.reader.dread.format.IBookCache;
import com.dangdang.reader.dread.format.epub.EpubBook;
import com.dangdang.reader.dread.format.epub.EpubChapter;
import com.dangdang.reader.dread.format.part.PartBook;
import com.dangdang.reader.dread.function.FunctionCode;
import com.dangdang.reader.dread.holder.CommonParam;
import com.dangdang.reader.dread.holder.ServiceManager;
import com.dangdang.reader.dread.service.MarkService;
import com.dangdang.reader.dread.service.NoteService;
import com.dangdang.reader.dread.util.BookCacheHandle;
import com.dangdang.reader.dread.util.BookStructConvert;
import com.dangdang.reader.dread.util.IntentK;
import com.dangdang.reader.dread.util.ReadAction;
import com.dangdang.reader.dread.util.ReadBookUtil;
import com.dangdang.reader.dread.view.BookMarkOperateView;
import com.dangdang.reader.dread.view.FontHintDialog;
import com.dangdang.reader.dread.view.GuideWindow;
import com.dangdang.reader.dread.view.ReaderLayout;
import com.dangdang.reader.dread.view.ReaderLayout.IMoveCompleteOperateCallBack;
import com.dangdang.reader.dread.view.ReaderLayout.OnSizeChangedListener;
import com.dangdang.reader.dread.view.ReaderScrollView.ScrollEvent;
import com.dangdang.reader.dread.view.SelectionTextView.onPositionListener;
import com.dangdang.reader.dread.view.toolbar.ReaderToolbar;
import com.dangdang.reader.handle.DownloadBookHandle;
import com.dangdang.reader.moxiUtils.BrodcastData;
import com.dangdang.reader.moxiUtils.BrodcastUtils;
import com.dangdang.reader.moxiUtils.NotificationWhat;
import com.dangdang.reader.moxiUtils.SaveNoteDialog;
import com.dangdang.reader.moxiUtils.SettingInterface;
import com.dangdang.reader.moxiUtils.SettingNewDialog;
import com.dangdang.reader.moxiUtils.share.YingxiangContent;
import com.dangdang.reader.personal.DataUtil;
import com.dangdang.reader.personal.domain.ShelfBook;
import com.dangdang.reader.personal.utils.PersonalUtils;
import com.dangdang.reader.request.GetBookCloudReadInfoRequest;
import com.dangdang.reader.request.GetBookCloudReadProgressRequest;
import com.dangdang.reader.request.RequestConstants;
import com.dangdang.reader.request.RequestResult;
import com.dangdang.reader.request.UpdateBookCloudReadInfoRequest;
import com.dangdang.reader.request.UpdateBookCloudReadProgressRequest;
import com.dangdang.reader.utils.AccountManager;
import com.dangdang.reader.utils.Constant;
import com.dangdang.reader.utils.DangdangFileManager;
import com.dangdang.reader.utils.NetUtils;
import com.dangdang.reader.utils.ReferType;
import com.dangdang.reader.utils.SpecialKeyObserver;
import com.dangdang.reader.utils.SpecialKeyObserver.OnPowerKeyListener;
import com.dangdang.reader.utils.Utils;
import com.dangdang.reader.view.MobilNetDownloadPromptDialog;
import com.dangdang.reader.view.SyncDialog;
import com.dangdang.reader.view.SyncTipDialog;
import com.dangdang.zframework.log.LogM;
import com.dangdang.zframework.utils.DRUiUtility;
import com.dangdang.zframework.utils.NetUtil;
import com.dangdang.zframework.utils.UiUtil;
import com.dangdang.zframework.view.DDImageView;
import com.moxi.biji.intf.ContentBuilderInterface;
import com.mx.mxbase.constant.APPLog;
import com.mx.mxbase.dialog.HitnDialog;
import com.mx.mxbase.dialog.ListDialog;
import com.mx.mxbase.utils.AppUtil;
import com.mx.mxbase.utils.ScreenShot;
import com.mx.mxbase.utils.StartActivityUtils;
import com.mx.mxbase.utils.StringUtils;
import com.mx.mxbase.utils.ToastUtils;
import com.mx.mxbase.view.AlertDialog;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ReadActivity extends PubReadActivity implements
        ReaderToolbar.onProgressBarChangeListener, PubReadActivity.OnBookFollowListener {
    public static final int REQUEST_CODE_BUY = 101;
    public static final int TAG_TAKE_PHOTO = 102;
    public static final int TAG_PICK_PHOTO = 103;
    protected GetBookCloudReadProgressRequest mGetBookCloudReadProgressRequest = null;
    protected GetBookCloudReadInfoRequest mGetBookCloudReadInfoRequest = null;
    /**
     * 获取云端阅读进度
     *
     */
    /*
     * protected static final DangDang_Method GetBookCloudReadProgress =
	 * DangDang_Method.GetBookCloudSyncReadProgressInfo;
	 *//**
     * 获取云端书签、笔记列表
     */
    /*
     * protected static final DangDang_Method GetBookCloudReadInfo =
	 * DangDang_Method.GetBookCloudSyncReadInfo;
	 *//**
     * 提交书签、笔记列表到云端
     */
    /*
     * protected static final DangDang_Method UpdateBookCloudReadInfo =
	 * DangDang_Method.UpdateBookCloudSyncReadInfo;
	 */
    /**
     * 提交阅读进度到云端
     */
    /*
     * protected static final DangDang_Method UpdateBookCloudReadProgress =
	 * DangDang_Method.UpdateBookCloudSyncReadProgressInfo;
	 */
    protected static final List<String> blackList = new ArrayList<String>(
            Arrays.asList("LT22i", "LT S980D", "LT26ii", "LT22i", "ST27i",
                    "ST25i"));

    public static final int READER_GUIDE_NUM = 1;

    public static final int OPERATE_MARK = 0;

    protected ReaderLayout mReaderLayout;
    protected BaseReaderWidget mReaderWidget;
    protected DDImageView mBookMarkView;
    // protected DDImageView mMarkView;
    /**
     * 显示操作提示
     */
    protected GuideWindow mGuideWindow;
    protected boolean mShowDirGuide;
    protected ReaderToolbar mToolbar;

    protected BaseBookManager mBookManager;
    protected ReaderAppImpl mReaderApps;
    protected ReadInfo mReadInfo;
    protected IBookCache mBookCache;

    protected BatteryAndTimeReceiver mBatteryAndTimeReceiver;
    protected BroadcastReceiver mReadReceiver;
    //protected ShelfService mShelfService;
    protected SpecialKeyObserver mSpeKeyObserver;

    protected boolean mStructFinish = false;
    protected boolean mComposingFinish = false;
    protected boolean mFreeMem = false;
    protected boolean mDestroy = false;
    protected boolean mDelayOperation = true;

    protected long mStartReadTime;
    protected long mEndReadTime;
    protected long mPauseTime;
    protected long mStartReadTimeForTraining;

    protected CommonParam mCommonParam;

    protected CloudSyncConfig mSyncConfig;
    protected MarkNoteManager mMarkNoteManager;
    protected CloudReadProgress mCloudProgress;
    protected SyncDialog mSyncDialog;

    private boolean mbShowChooseStartDlg;

	/*
     * protected Command mGetProgressCmd = null; protected Command mGetReadInfoCmd =
	 * null;
	 */

    protected int mReComposingChapterIndex = -1;
    protected int mReComposingElementIndex = -1;
    protected ParserProgress mParserPrgs;

    /**
     *
     */
    protected int mOnceAutoSyncOnlyWifi = 0;

    protected final static int ONCE_AUTOSYNC_STATUS_YES = 1;
    protected final static int ONCE_AUTOSYNC_STATUS_NO = -1;

    protected AccountManager mAccountManager;

    private boolean mIsLandSpace = false;

    protected Handler mHandler;
    //    private ShareUtil mShareUtil;
    private int cacheFull = 0;

    private View mDownloadDDreaderLayout;

    /**
     * 我的控件
     */
    private TextView show_title;
    private TextView title_name;
    private TextView setting_style;
    private MyHandlerRefuresh handlerRefuresh = new MyHandlerRefuresh(this);
    private ScreenShot screenShot;//屏幕截图

    private static class MyHandlerRefuresh extends Handler {
        private WeakReference<ReadActivity> reference;

        public MyHandlerRefuresh(ReadActivity context) {
            reference = new WeakReference<>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            ReadActivity activity = reference.get();
            if (activity != null) {
                activity.handleMessage(msg);
            }
        }
    }


    public void handleMessage(Message msg) {
        handlerRefuresh.sendEmptyMessageDelayed(0, 2000);
        if (ReadConfig.getConfig().getScroolIndex()) {
//            EpdController.invalidate(this.getWindow().getDecorView(), EpdController.UpdateMode.GC);
        }
    }

    @Override
    public void onReadCreateImpl(Bundle savedInstanceState) {
//        DeviceInfo.currentDevice.hideSystemStatusBar(ReadActivity.this);
//        printLog("luxutagtag onReadCreateImpl() start ");
//        			((Activity) mContext)
//					.getParent()
//					.getWindow()
//					.clearFlags(
//							WindowManager.LayoutParams.FLAG_FULLSCREEN);//FLAG_FULLSCREEN,FLAG_FORCE_NOT_FULLSCREEN
        mHandler = new MyHandler(this);
        mCloudHandler = new CloudHandler(this);
        mMarkHandler = new MarkHandler(this);
        handler = new MHandler(this);
        DataUtil.getInstance(this);
        openHardwareAcceelerated();
        setContentView(R.layout.read_new);
        mRootView = (ViewGroup) findViewById(R.id.reader_layout);
        resetDestroy();
        setDelayOperation();
        mAccountManager = new AccountManager(getApplicationContext());
        try {
            mCommonParam = CommonParam.getInstance();
            mCommonParam.setmRefreshTime(true);
            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

            mReaderApps = ReaderAppImpl.getApp();
            try {
                mReadInfo = (ReadInfo) mReaderApps.getReadInfo();
                cacheFull = mReadInfo.isBoughtToInt();
                mReaderApps.init(this);
                mReaderWidget = mReaderApps.getReaderWidget();
                getGWindow().setOnDismissCallBack(this.mOnDismissCallBack);
            } catch (FileFormatException e1) {
                e1.printStackTrace();
                // showToast(R.string.open_book_failed);
                finish();
            }

            mReaderLayout = (ReaderLayout) findViewById(R.id.reader_layout_container);
            mReaderLayout.setReaderWidget((EpubReaderWidget) mReaderWidget);
            mReaderLayout.setOnSizeChangeListener(mSizeChangedListener);
            mReaderLayout
                    .setOnMoveCompleteOperateCallBack(mIMoveCompliteOperateCallBack);
            mReaderLayout.setOnBottomClickListener(mReadInfo.isDDBook());

            FrameLayout readContainer = (FrameLayout) findViewById(R.id.read_widget_container);
            readContainer.addView(mReaderWidget);

            updateScreenLight();
            initVideo();
            initUi();
            registerListener();

            initMenu();
            initFilter();
            // updateModeSetToolbarScreenLight();
            prepareRead();
            //获取原创的限免信息
            getTimeFreeInfo();

			/*mShelfService = ShelfService.getInstance(this);*/
            try {
                mStartReadTime = getCurTime();
                mStartReadTimeForTraining = getCurTime();
                addStartReaderStat();
            } catch (Exception e) {
                e.printStackTrace();
            }

            processAutoSyncGetCloudReadProgress();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sendDelayMsg();
        }
        printLog("luxutagtag onReadCreateImpl() end ");
        // displayBriefMemory();
        /**
         * 我的控件
         */
        show_title = (TextView) findViewById(R.id.show_title);
        title_name = (TextView) findViewById(R.id.title_name);
        setting_style = (TextView) findViewById(R.id.setting_style);
        handlerRefuresh.sendEmptyMessageDelayed(0, 2000);
        addReceiver();

        screenShot = new ScreenShot(this, new ScreenShot.ScreenShotListener() {
            @Override
            public void onShotSucess(final String filPath) {
                if (AppUtil.isApplicationAvilible(ReadActivity.this, "com.moxi.writeNote")) {
                    //截屏完成
                    new AlertDialog(ReadActivity.this).builder().setTitle("标注提示").setCancelable(false).setMsg("是否前往对截图进行标注").
                            setNegativeButton("前往", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    StartActivityUtils.startPicPostil(ReadActivity.this, filPath, mReadInfo.getBookName());
                                }
                            }).setPositiveButton("放弃", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    }).show();
                } else {
                    ToastUtils.getInstance().showToastShort("已保存:");
                }
                hideShow();
            }
        });

        setttingScreenOrientaton(-1, false, isfirst);
    }

    private void addReceiver() {
        IntentFilter intentFilter = new IntentFilter(
                NotificationWhat.REMARKES);
        initReceiver();
        registerReceiver(receiver, intentFilter);
    }

    private SaveNoteDialog saveNoteDialog;
    private BroadcastReceiver receiver = null;

    private void initReceiver() {
        receiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction()
                        .equals(NotificationWhat.REMARKES)) {
                    // 添加备注广播
                    BookNote bookNote = (BookNote) intent.getSerializableExtra(BookNoteActivity.BOOK_NOTE_OBJECT);
                    saveNoteDialog = SaveNoteDialog.getdialog(ReadActivity.this, bookNote, new SaveNoteDialog.SaveNoteBack() {
                        @Override
                        public void onSaveNote(Intent data) {
                            //保存后返回
                            onActivityResultNote(data);
                        }
                    });
                }
            }

        };
    }

    private int currentScreen = 0;
    private HitnDialog dialog;
    /**
     * 设置监听
     */
    private SettingInterface anInterface = new SettingInterface() {
        @Override
        public void settingFont(int font) {
            requestAbort(mAbortComposingByFont);
            //重新绘制当前界面·
            resetView();
        }

        @Override
        public void settingSize(int size) {
            setFontSize(size);
        }

        @Override
        public void settingLine(int index) {
            settingLineSpacing(index);
        }

        @Override
        public void settingPage(int index) {
            requestAbort(mAbortComposingByFont);
        }

        @Override
        public void settingScreen(int index) {
//            currentScreen=index;
            setttingScreenOrientaton(-1, true, false);
        }

        @Override
        public void settingProgress(int index) {
            requestAbort(mAbortComposingByFont);
        }

        @Override
        public void startMuen() {
            showDirMarkNote(DirectoryMarkNoteActivity.DIR);

        }

        @Override
        public void shareBiJi() {
            List<BookNoteDataWrapper> mBookNoteWrappers=null;
            try {
                BaseReadInfo readInfo = getReadInfo();
                NoteService noteService = ReaderAppImpl.getApp().getServiceManager().getNoteService();
                 mBookNoteWrappers = noteService.getBookNoteWrapperListByBookId(
                        readInfo.getDefaultPid(), readInfo.isBoughtToInt());
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (mBookNoteWrappers==null)return;
            if (mBookNoteWrappers.size()==0){
                ToastUtils.getInstance().showToastShort("暂无笔记内容");
            }
            final List<BookNoteDataWrapper> finalMBookNoteWrappers = mBookNoteWrappers;
            ListDialog.getdialog(ReadActivity.this,"请选择分享笔记平台", new ListDialog.ClickListItemListener() {
                @Override
                public void onClickItem(int position) {
                    ContentBuilderInterface content=null;
                    //笔记内容拼接
                    if (position==0){//印象笔记
                        content=new YingxiangContent();
                    }else {//有道笔记
                        ToastUtils.getInstance().showToastShort("暂未开发...");
                    }
                    if (content!=null){
                        String ct=content.getContent(finalMBookNoteWrappers,getBook());
                    }


                }
            },"印象笔记","有道云笔记");

        }

        @Override
        public void endReader() {
            onBackPressed();
        }

        @Override
        public void onScreenShort() {
            if (StringUtils.isStorageLow10M()) {
                ToastUtils.getInstance().showToastShort("内存不足，无法进行操作");
                return;
            }
            showDialog("");
            screenShot.shoot(StringUtils.getScreenShot(downloadPathSpil(mReadInfo.getBookName())), true);
        }

        @Override
        public void onScrollToPage(int page) {
            toPage(page);
        }

        @Override
        public void onChapterJump(boolean lastPage) {
            directoryChange(lastPage);
        }
    };

    private void showDialog(String hitn) {
        dialog = new HitnDialog(ReadActivity.this, com.mx.mxbase.R.style.AlertDialogStyle, hitn, 0, null);
        dialog.setCancelable(false);// 是否可以关闭dialog
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void hideShow() {
        if (dialog != null && dialog.isShowing()) dialog.dismiss();
    }

    private String downloadPathSpil(String filName) {
        String str = filName.replaceAll("[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……& amp;*（）——+|{}【】‘；：”“’。，、？|-]", "");
        return str;
    }

    /**
     * 设置屏幕显示方向
     *
     * @param index 设置屏幕方向
     * @param is    是否刷新界面
     */
    public void setttingScreenOrientaton(int index, boolean is, boolean isfirst) {
        currentScreen = index;
        final ReadConfig readConfig = ReadConfig.getConfig();
        if (index == -1) index = readConfig.getScreen();
        if (index == 1) {
            //设置屏幕是横屏
            if (isfirst) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            } else {
                is = false;
                handler.sendEmptyMessageDelayed(100, 500);
            }
        } else {
            //竖屏
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        if (is) {
            if (index != 1) {
                requestAbort(mAbortComposingByFont);
            }
//            EpubReaderController controller = (EpubReaderController) mReaderApps.getReaderController();
//            controller.allreSet();
        }
    }

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        final ReadConfig readConfig = ReadConfig.getConfig();
//        if (readConfig.getScreen() == 1) {
//            //竖屏
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        }
//        readConfig.isScreenNonChange = true;
//        readConfig.setScreen(currentScreen);
//        this.finish();
//    }

    protected void getTimeFreeInfo() {

    }


    protected void initVideo() {
        if (isInitVideo()) {
            SurfaceView surfaceView = new SurfaceView(this);
            mRootView.addView(surfaceView, new LayoutParams(0, 0));
            surfaceView.setVisibility(View.GONE);
        }
    }

    protected boolean isInitVideo() {
        return !isPdf();
    }

    protected void addStartReaderStat() {
        ReadConfig config = ReadConfig.getConfig();
        String refer = getIntent().getStringExtra(IntentK.ReferType);
        mDDService.addData(DDStatisticsService.FIRST_OPEN_READER,
                DDStatisticsService.FONT_VALUE, config.getFontSize() + "",
                DDStatisticsService.BACKGROUND, config.getReaderBgColor() + "",
                DDStatisticsService.FONT_NAME, config.getFontName(),
                DDStatisticsService.FLIP_PAGE_BY_VOICE, config.isVolKeyFlip()
                        + "", DDStatisticsService.SLAKE_SCREEN,
                config.getLightInterval() + "", DDStatisticsService.FLIP_PAGE,
                config.getAnimationTypeNew().name(),
                DDStatisticsService.NIGHT_MODEL, config.isNightMode() + "",
                DDStatisticsService.OPerateTime,
                String.valueOf(mStartReadTime), DDStatisticsService.ReferType,
                refer);
    }

    protected void sendDelayMsg() {
        mHandler.sendEmptyMessageDelayed(DELAY_OPERATION, 1000);
    }

	/*
     * protected void displayBriefMemory() {
	 * //VMRuntime.getRuntime().setMinimumHeapSize(64*2*1024*1024); final
	 * ActivityManager activityManager = (ActivityManager)
	 * getSystemService(ACTIVITY_SERVICE); printLog(" memory mcsize=" +
	 * activityManager.getMemoryClass() + "," +
	 * VMRuntime.getRuntime().getTargetHeapUtilization()); }
	 */

    protected void registerListener() {
        mBookManager = (BaseBookManager) mReaderApps.getBookManager();
        mReaderApps.registerComposingListener(mComposingListener);
        mReaderApps.setReaderEventListener(mReaderEventListener);
    }

    protected long getCurTime() {
        return System.currentTimeMillis();
    }

    protected void prepareRead() {
        startRead();
    }

    protected void startRead() {
        mReaderApps.startRead(mReadInfo);

        mSyncConfig = new CloudSyncConfig(getApplicationContext());
        // mMarkNoteManager =
        // MarkNoteManager.getInstance(readerApps.getServiceManager());
        mMarkNoteManager = mReaderApps.getMarkNoteManager();
        mMarkNoteManager.setBookSupportCloudSync(cloudSyncBaseCondition());

        cacheMarksAndNotes();

    }

    protected void cacheMarksAndNotes() {
        ServiceManager serviceManager = mReaderApps.getServiceManager();
        NoteService noteService = serviceManager.getNoteService();
        MarkService markService = serviceManager.getMarkService();

        String bookId = mReadInfo.getDefaultPid();
        int isBought = mReadInfo.isBoughtToInt();

        Map<MarkKey, BookMark> bookMarks = markService.getBookMarks(bookId,
                isBought);
        Map<NoteKey, BookNote> bookNotes = noteService.getBookNotes(bookId,
                isBought);
        mMarkNoteManager.setBookMarks(bookMarks);
        mMarkNoteManager.setBookNotes(bookNotes);
    }

    protected void initUi() {
        View toBookShelf = findViewById(R.id.read_back_tobookshelf);
        toBookShelf.setOnClickListener(mClickListener);

        BookMarkOperateView markOperateView = (BookMarkOperateView) findViewById(R.id.book_mark_operate_view);
        if (isPdf()) {
            mReaderLayout.closeOperateBookMark();
        } else {
            mReaderLayout.setOnOperateBookMarkListener(markOperateView);
        }
        final ReadConfig readConfig = ReadConfig.getConfig();
        setPrevComposingArea(readConfig.getReadWidth(),
                readConfig.getReadHeight());

        findViewById(R.id.delete_iv).setOnClickListener(mClickListener);
        findViewById(R.id.download_tv).setOnClickListener(mClickListener);
        mDownloadDDreaderLayout = findViewById(R.id.download_ddreader_layout);
        mDownloadDDreaderLayout.setOnClickListener(mClickListener);
    }

    protected void hideGuideLayout() {
        mGuideWindow.hideMenu();
    }

    public void initFilter() {
        mReadReceiver = new ReadProcessReceiver();

        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.ACTION_REMOVE_LONGCLICK);
        filter.addAction(Constant.ACTION_READER_FONT_TYPE);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        filter.addAction(Constant.ACTION_FINISH_READ);
        filter.addAction(Constant.ACTION_GET_COUPON_SUCCESS);
        filter.addAction(Constant.ACTION_READER_INIT_DICTPATH);
        filter.addAction(Constant.ACTION_READAREA_CHANGED);
        filter.addAction(Constant.ACTION_READER_RECOMPOSING);
        filter.addAction(Constant.ACTION_READER_RECOMPOSING);
        filter.addAction(ReadAction.ACTION_BOUGHT_SUCCESS);
        filter.addAction(ReadAction.ACTION_TTS_UNINSTALL);
        filter.addAction(StartActivityUtils.screenShot);

        filter.addAction(Constants.BROADCAST_BUY_DIALOG_CANCEL);
        filter.addAction(Constants.ACTION_LOGIN_CANCEL);
        filter.addAction(Constants.BROADCAST_RECHARGE_SUCCESS);
        registerReceiver(mReadReceiver, filter);

        mBatteryAndTimeReceiver = new BatteryAndTimeReceiver();
        IntentFilter mBtFilter = new IntentFilter();
        mBtFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        mBtFilter.addAction(Intent.ACTION_TIME_TICK);
        registerReceiver(mBatteryAndTimeReceiver, mBtFilter);

        mSpeKeyObserver = new SpecialKeyObserver(getApplicationContext());
        mSpeKeyObserver.setPowerKeyListener(new OnPowerKeyListener() {
            @Override
            public void onPowerKeyPressed(boolean onOrOff) {
                if (!onOrOff) {
                    cancelSelected(true);
                }
            }
        });
        mSpeKeyObserver.startPowerListener();
    }

    protected void initMenu() {
        mToolbar = new ReaderToolbar(this, mReaderWidget,
                (Book) mReaderApps.getBook());
        mToolbar.setListeners(mClickListener, mFontClickListener,
                mBgClickListener, mSeekBarChangeLight, this, mMarkListener, this,
                null, null, null);
        mToolbar.setOnDismissCallBack(mOnDismissCallBack);

        mGuideWindow = new GuideWindow(getApplicationContext(), mReaderWidget);
        mGuideWindow.setClickListener(mGuideClickListener);
    }

    final IBookParserListener mComposingListener = new IBookParserListener() {

        @Override
        public void onVersion(int kernelVersion, int kernelCompsVersion) {
            printLog(" onVersion " + kernelVersion + "," + kernelCompsVersion);
            handleKernelVersion(kernelVersion, kernelCompsVersion);
        }

        public void onStructFinish(IBook ibook) {
            Book book = getBook(ibook);
            if (mReaderApps.getFileType() == FileType.TXT
                    && ParserStatus.isSuccess(mBookManager.getStatus())) {
                // printLog(" onStructFinish cacheChapterList = " +
                // mReadInfo.hasCacheChapterList());
                if (!mReadInfo.hasCacheChapterList() && book.hasChapterList()) {
                    final Context context = getApplicationContext();
                    final byte[] bookStructs = BookStructConvert
                            .convertBookToData(book);
                    final String bookId = mReadInfo.getDefaultPid();
                    // printLog(" onStructFinish chapterJson = " +
                    // bookStructs.length);

					/*ShelfService.getInstance(context).saveBookChapterList(
                            bookId, bookStructs);*/
                }
            }

            if (!book.isTheSameFile()) {
                mReadInfo.resetProgress();
                // sendMsg2Toast(R.string.filecontent_change);
            }
            Intent intent = new Intent();
            intent.setAction(Constant.ACTION_PARSER_FINISH);
            sendBroadcast(intent);
            if (mMarkNoteManager != null && mReaderApps.getFileType() == FileType.EPUB)
                mMarkNoteManager.UpdateMarksAndBookNotesIfModVersionChange(book, mBookManager);

            if (mReadInfo.isLandScape()) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                DRUiUtility.getUiUtilityInstance().DontKeepContext(ReadActivity.this);
                mBookManager.reSet(true);
                mIsLandSpace = true;
            }

            setStructFinish();
        }

        @Override
        public void onStart(IBook book) {
            printLog("lxu  onStart() ");
            resetComposingFinish();
            resetBookCache();

            refreshCurrentProgress(book);

            handler.sendEmptyMessage(MSG_BOOK_LOADING_START);

            if (mGuideWindow.isFirstReadBook()) {
                handler.sendEmptyMessageDelayed(MSG_SHOW_READGUIDE, 1500);
            } else {
                mHandler.sendEmptyMessageDelayed(DDTTS_GUIDE_ANIM, 1500);
            }
        }

        @Override
        public void onBeingComposing(ParserProgress progress) {
            // printLog("lxu onBeingComposing: " + progress.beingchapterCount +
            // "," + progress.chapterCount);
            mParserPrgs = progress;
            if (isReadBottomMenuShowing()) {
                Message msg = handler.obtainMessage(MSG_COMPOSING_PROGRESS);
                msg.arg1 = progress.beingchapterCount;
                msg.arg2 = progress.chapterCount;

                handler.sendMessage(msg);
            }
        }

        @Override
        public void onFinish(int pageTotal, IBook book, IBookCache bookCache) {

            printLog("lxu  onFinish = " + pageTotal);
            mParserPrgs = null;
            setComposingFinish();

            if (bookCache == null || !bookCache.isAvailable()) {
                printLog("lxu  onFinish deleteBookComposingCache ");
                deleteBookComposingCache();
            } else {
                setBookCache(bookCache);
            }
            handler.removeMessages(MSG_COMPOSING_PROGRESS);
            handler.sendEmptyMessage(MSG_BOOK_LOADING_FINISHED);

            Intent intent = new Intent();
            intent.setAction(Constant.ACTION_COMPOSING_FINISH);
            sendBroadcast(intent);

            gotoCloudReadProgress();
        }

        @Override
        public void onStatus(int status, String errorMsg) {
            LogM.d("  onFail = " + status);
            if (status == ParserStatus.C_HTML_ERROR
                    || status == ParserStatus.C_DECTYPT_ERROR
                    || status == ParserStatus.C_FILENOEXIST_ERROR) {

                handler.sendEmptyMessage(MSG_SHOW_TOBACKSHELF);
                return;
            }
            if (status != ParserStatus.C_INVALID_FILE) {
                int resStr = switchErrorPrompt(status);
                Message msg = handler.obtainMessage();
                msg.what = MSG_TOAST_PROMPT;
                msg.arg1 = resStr;
                handler.sendMessage(msg);

                handler.sendEmptyMessage(MSG_FINISH_READ);
            }
        }

    };

    protected void handleKernelVersion(int kernelVersion, int kernelCompsVersion) {
        if (mReadInfo.getKernelComsVersion() != kernelCompsVersion) {
            deleteBookComposingCache();
            mReadInfo.setKernelVersion(kernelVersion);
            mReadInfo.setKernelComsVersion(kernelCompsVersion);
        }
    }

    protected void deleteBookComposingCache() {
        BookCacheHandle.deleteBookCache(mReadInfo.getDefaultPid(),
                mReadInfo.isBoughtToInt());
    }

    protected int switchErrorPrompt(int code) {
        int resStr = R.string.open_book_failed;
        switch (code) {
            case ParserStatus.FILE_ERROR:
                resStr = R.string.parser_file_failed;
                break;
            case ParserStatus.UNZIP_ERROR:
                resStr = R.string.unzip_book_error;
                break;
            case ParserStatus.EPUB_ERROR:
                resStr = R.string.file_not_exist;
                break;
            case ParserStatus.C_INVALID_FILE:
                resStr = R.string.fileexception_noread;
                break;
            case ParserStatus.TRAINING_OVER:
                resStr = R.string.training_over;
                break;
        }
        return resStr;
    }

    final IReaderEventListener mReaderEventListener = new IReaderEventListener() {

        @Override
        public void onMenuEvent() {
            // printLog(" onMenuEvent ");
            showOrHideMenu();
        }

        @Override
        public BookNote addNote(int chapterIndex, int startIndex, int endIndex,
                                String selectedText, String noteText, int drawLineColor) {

            printLog(" addNote " + chapterIndex + ",[" + startIndex + "-"
                    + endIndex + "]");
            BookNote note = getBookNote(chapterIndex, startIndex, endIndex,
                    selectedText, noteText, drawLineColor);
            mMarkNoteManager.operationBookNote(note, OperateType.NEW);
            return note;
        }

        @Override
        public void onLongPressEvent(int x, int y) {
            mGuideWindow.showLineGuide(y);
        }

    };

    protected BookNote getBookNote(int chapterIndex, int startIndex,
                                   int endIndex, String selectedText, String noteText, int drawLineColor) {
        ReadInfo rInfo = mReadInfo;
        BookNote note = new BookNote();
        note.setBookId(rInfo.getDefaultPid());
        note.setBookPath(rInfo.getBookDir());
        note.setChapterName(getChapterName(chapterIndex));
        note.setChapterIndex(chapterIndex);
        note.setSourceText(selectedText);
        note.setNoteStart(startIndex);
        note.setNoteEnd(endIndex);
        note.setNoteText(noteText);
        note.setNoteTime(new Date().getTime());
        note.setIsBought(rInfo.isBoughtToInt());
        note.setStatus(String.valueOf(Status.COLUMN_NEW));
        note.setCloudStatus(String.valueOf(Status.CLOUD_NO));
        note.setBookModVersion(rInfo.getEpubModVersion());
        note.setDrawLineColor(drawLineColor);

        return note;
    }

    protected String getChapterName(int chapterIndex) {
        Book book = (Book) mReaderApps.getBook();
        return book.getChapterName(chapterIndex);
    }

    /**
     * 上一次排版范围
     */
    protected int mPrevCpsWidth;
    protected int mPrevCpsHeight;

    protected void setPrevComposingArea(int width, int height) {
        mPrevCpsWidth = width;
        mPrevCpsHeight = height;
    }

    final OnSizeChangedListener mSizeChangedListener = new OnSizeChangedListener() {
        @Override
        public void onSizeChanged(int w, int h, int oldw, int oldh) {
            processReadAreaChanged();
        }
    };

    protected void processReadAreaChanged() {
        DRUiUtility.getUiUtilityInstance().DontKeepContext(this);
        final ReadConfig readConfig = ReadConfig.getConfig();
        int tmpCpsWidth = readConfig.getReadWidth();
        int tmpCpsHeight = readConfig.getReadHeight();
        if (tmpCpsWidth != mPrevCpsWidth || tmpCpsHeight != mPrevCpsHeight) {

            setPrevComposingArea(tmpCpsWidth, tmpCpsHeight);
            mReaderWidget.onSizeChange();
            setReComposingIndex(mReadInfo.getChapterIndex(),
                    mReadInfo.getElementIndex());
            requestAbort(mAbortComposingByFont);
        }
    }

    final OnBookMarkListener mMarkListener = new OnBookMarkListener() {

        @Override
        public void addMark() {
            printLog(" addMark ");
            startMarkAnim(false);
        }

        @Override
        public void removeMark() {
            printLog(" removeMark ");
            startMarkAnim(true);
        }
    };

    @Override
    public void follow() {

    }

    @Override
    public void unFlollow() {

    }

    /**
     * @param addOrDel true: del; false: add;
     */
    protected void startMarkAnim(boolean addOrDel) {
        if (mBookMarkView != null) {
            mBookMarkView.setVisibility(View.VISIBLE);

            printLog(" startMarkAnim addOrDel = " + addOrDel);

            Animation animation = null;
            if (addOrDel) {
                animation = AnimationUtils.loadAnimation(this,
                        R.anim.push_up_out);
            } else {
                animation = AnimationUtils.loadAnimation(this,
                        R.anim.push_down_in);
            }
            mBookMarkView.startAnimation(animation);
        }
        Message msg = mMarkHandler.obtainMessage();
        msg.obj = addOrDel;
        msg.what = OPERATE_MARK;
        mMarkHandler.sendMessage(msg);
    }

    protected Handler mMarkHandler;

    private void dealMarkMsg(Message msg) {
        boolean mAddOrDel = (Boolean) msg.obj;
        printLog(" mMarkHandler handleMessage mAddOrDel = " + mAddOrDel);
        if (mAddOrDel) {
            mReaderApps.doFunction(FunctionCode.FCODE_REMOVE_MARK);
        } else {
            mReaderApps.doFunction(FunctionCode.FCODE_ADD_MARK);
        }
        // bookMarkView.destroyDrawingCache();
        if (mBookMarkView != null)
            mBookMarkView.setVisibility(View.GONE);
    }

    private static class MarkHandler extends Handler {
        private final WeakReference<ReadActivity> mFragmentView;

        MarkHandler(ReadActivity view) {
            this.mFragmentView = new WeakReference<ReadActivity>(view);
        }

        @Override
        public void handleMessage(Message msg) {
            ReadActivity service = mFragmentView.get();
            if (service != null) {
                super.handleMessage(msg);
                try {
                    service.dealMarkMsg(msg);
                } catch (Exception e) {
                    LogM.e(service.TAG, e.toString());
                }
            }
        }
    }

    final OnClickListener mBgClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int index = 0;
            String ddstr = DDStatisticsService.READ_BG_TYPE[0];
            int i = v.getId();
            if (i == R.id.toolbar_font_color_1) {
                index = 0;
                ddstr = DDStatisticsService.READ_BG_TYPE[0];

            } else if (i == R.id.toolbar_font_color_2) {
                index = 1;
                ddstr = DDStatisticsService.READ_BG_TYPE[1];

            } else if (i == R.id.toolbar_font_color_3) {
                index = 2;
                ddstr = DDStatisticsService.READ_BG_TYPE[2];

            } else if (i == R.id.toolbar_font_color_4) {
                index = 3;
                ddstr = DDStatisticsService.READ_BG_TYPE[3];

            } else if (i == R.id.toolbar_font_color_5) {
                index = 4;
                ddstr = DDStatisticsService.READ_BG_TYPE[3];

            } else if (i == R.id.toolbar_font_color_6) {
                index = 5;
                ddstr = DDStatisticsService.READ_BG_TYPE[3];

            } else if (i == R.id.toolbar_font_color_7) {
                index = 6;
                ddstr = DDStatisticsService.READ_BG_TYPE[3];

            } else if (i == R.id.toolbar_font_color_8) {
                index = 7;
                ddstr = DDStatisticsService.READ_BG_TYPE[3];

            } else if (i == R.id.read_detail_light_sys) {
                updateScreenLight();
                mDDService.addData(DDStatisticsService.BRIGHT_FOLLOW_SYSTEM,
                        DDStatisticsService.OPerateTime,
                        System.currentTimeMillis() + "");
                return;
            } else {
            }
            mDDService.addData(DDStatisticsService.BACKGROUNDMODEL,
                    DDStatisticsService.OPerateTime, System.currentTimeMillis()
                            + "", DDStatisticsService.BACKGROUND, ddstr);

            ReadConfig.getConfig().setNightMode(false);
            updateScreenLight();
            performChangeBackground(index);
        }
    };

    protected void performChangeBackground(int index) {

        ReadConfig config = ReadConfig.getConfig();
        if (index != -1) {
            config.setReaderBgColorDay(ReadConfig.READER_BG_COLOR_DAY[index]);
        }
        int color = config.getReaderBgColor();
        mBookManager.updateBackground(color, config.getReaderForeColor());
        mReaderApps.getReaderController().reset();
        mReaderWidget.setBackgroundColor(color);
        mReaderWidget.repaintSync(true, true);

        // resetView();

        Intent intent = new Intent();
        intent.setAction(Constant.ACTION_CHANGE_BACKGROUND);
        sendBroadcast(intent);
    }

    final OnClickListener mFontClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.read_font_zoom_out_layout || id == R.id.read_font_zoom_in_layout) {
                onFontSizeChange(id);

            } else if (id == R.id.read_line_spacing_s || id == R.id.read_line_spacing_m || id == R.id.read_line_spacing_l || id == R.id.read_line_spacing_x) {
                onLineSpacingChange(id);

            }
        }
    };

    private void onLineSpacingChange(int id) {
        final ReadConfig readConfig = ReadConfig.getConfig();
        float spacing = readConfig.getLineSpacing();
        if (id == R.id.read_line_spacing_s) {
            if (spacing == ReadConfig.LINESPACING_DEFAULT_S) {
                showToast("当前为紧凑样式");
                return;
            }
            readConfig.saveLineSpacing(ReadConfig.LINESPACING_DEFAULT_S);

        } else if (id == R.id.read_line_spacing_m) {
            if (spacing == ReadConfig.LINESPACING_DEFAULT_M) {
                showToast("当前为适中样式");
                return;
            }
            readConfig.saveLineSpacing(ReadConfig.LINESPACING_DEFAULT_M);

        } else if (id == R.id.read_line_spacing_l) {
            if (spacing == ReadConfig.LINESPACING_DEFAULT_L) {
                showToast("当前为松散样式");
                return;
            }
            readConfig.saveLineSpacing(ReadConfig.LINESPACING_DEFAULT_L);

        } else if (id == R.id.read_line_spacing_x) {
            if (spacing == ReadConfig.LINESPACING_DEFAULT_X) {
                showToast("当前为松散样式");
                return;
            }
            readConfig.saveLineSpacing(ReadConfig.LINESPACING_DEFAULT_X);

        }
//        resetView();
        requestAbort(mAbortComposingByFont);

    }

    public void settingLineSpacing(int index) {
        final ReadConfig readConfig = ReadConfig.getConfig();
        switch (index) {
            case 0:
                readConfig.saveLineSpacing(ReadConfig.LINESPACING_DEFAULT_S);
                break;
            case 1:
                readConfig.saveLineSpacing(ReadConfig.LINESPACING_DEFAULT_M);
                break;
            case 2:
                readConfig.saveLineSpacing(ReadConfig.LINESPACING_DEFAULT_X);
                break;
            default:
                readConfig.saveLineSpacing(ReadConfig.LINESPACING_DEFAULT_L);
                break;
        }
        requestAbort(mAbortComposingByFont);
    }

    protected void onFontSizeChange(int id) {
        final ReadConfig readConfig = ReadConfig.getConfig();
        int lineWordNum = readConfig.getLineWordNum();
        if (id == R.id.read_font_zoom_out_layout) {
            if (lineWordNum >= readConfig.getMaxLineWord()) {
                showToast(R.string.min_fontsize_tip);
                return;
            }
        } else if (id == R.id.read_font_zoom_in_layout) {
            if (lineWordNum <= readConfig.getMinLineWord()) {
                showToast(R.string.max_fontsize_tip);
                return;
            }

        }
        saveLineWord(lineWordNum, id);
        requestAbort(mAbortComposingByFont);
    }

    /**
     * 设置字体大小
     *
     * @param index
     */
    private void setFontSize(int index) {
        saveLineWord(index, 0);
        requestAbort(mAbortComposingByFont);
    }

    /**
     * 设置字体大小
     *
     * @param lineWordNum
     * @param viewId
     */
    protected void saveLineWord(int lineWordNum, int viewId) {

        ReadConfig config = ReadConfig.getConfig();
        if (viewId == R.id.read_font_zoom_out_layout) {
            lineWordNum += ReadConfig.LineWordOneStepNum;

        } else if (viewId == R.id.read_font_zoom_in_layout) {
            lineWordNum -= ReadConfig.LineWordOneStepNum;

        }
        mDDService.addData(DDStatisticsService.FONTCHANGE,
                DDStatisticsService.OPerateTime, System.currentTimeMillis()
                        + "", DDStatisticsService.FONT_VALUE, lineWordNum + "");
        printLog(" saveLineWordNum = " + lineWordNum);
        config.saveLineWordNum(lineWordNum);
    }

    protected void reComposing() {
        handler.sendEmptyMessage(MSG_RECOMPOSING);
    }

    final IAbortParserListener mAbortComposingByFont = new IAbortParserListener() {
        @Override
        public void alreadyAbort() {
            printLog(" mAbortComposingByFont alreadyAbort() ");
            reComposing();
        }
    };

    final onPositionListener mAnimPosListener = new onPositionListener() {
        @Override
        public void onPosition(int position) {

            final ReadConfig config = ReadConfig.getConfig();
            // int location = getLocationByAnimType(config.getAnimationType());
            config.setAnimationType(ReadConfig.ANIMATION_TYPE[position]);
        }
    };

    /**
     * 去书吧
     *
     * @param toJoin 是否发贴
     */
    public void startCommentActivity(boolean toJoin) {
        //1.原创  2出版物  3 纸书
        int type = 2;
        if (mReadInfo instanceof PartReadInfo) {
            type = 1;
        }
        if (toJoin && !isLogin()) {
            gotoLogin();
            return;
        }
        ReadBookUtil.startBar(this, mReadInfo.getDefaultPid(), toJoin, mReadInfo.getBookName(), type);

    }

    public void onOneKeyBuyClickEvent() {
//        String userId = mAccountManager.getUserId();
//        if (!mAccountManager.checkTokenValid()) {
//            gotoLogin();
//        } else if (userId == null) {
//            handleTokenBad();
//        } else {
//            oneKeyBuy(mReadInfo.getProductId());
//        }
    }

    protected void handleTokenBad() {

        handler.post(new Runnable() {
            @Override
            public void run() {
                processTokenBad();
            }
        });
    }

	/*
     * public void onBackToShelfEvent() { //if(bookManager.isCanExit()){
	 * finishActivity(); //} }
	 */

    protected void oneKeyBuy(String bookId) {

//        BuyBookHandle buyBookHandle = new BuyBookHandle(this, bookId, bookId, REQUEST_CODE_BUY, StoreEBookBuyHandle.FROM_EBOOK_DETAIL, mRootView);
//        buyBookHandle.buy();
    }

    final OnClickListener mClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {

            int i = v.getId();
            if (i == R.id.read_top_back || i == R.id.read_back_tobookshelf) {// exitRead();
                needHideMenu();
                if (canExit())
                    attemptExit(true);

            } else if (i == R.id.read_top_mark_setting) {
                needHideMenu();

            } else if (i == R.id.read_bottom_dmn) {
                printLog(" R.id.read_bottom_dmn ");
                showDirMarkNote(DirectoryMarkNoteActivity.MARK);

            } else if (i == R.id.read_guide_light_layout) {
                hideGuideLayout();

            } else if (i == R.id.read_bottom_night || i == R.id.read_bottom_day) {
                mDDService.addData(DDStatisticsService.NIGHT_MODEL,
                        System.currentTimeMillis() + "");
                processDayAndNightSwitch();

            } else if (i == R.id.read_bottom_settings) {
                setReComposingIndex(mReadInfo.getChapterIndex(),
                        mReadInfo.getElementIndex());
                mGuideWindow.showLightGuide();

            } else if (i == R.id.font_auto_download) {
                ReadConfig.getConfig().setFontAutoDownload(true);
                getReadMain().startDownloadFontService();
                FontHintDialog dialog = new FontHintDialog(mContext);
                dialog.setInfo(R.string.toolbar_font_hint_dialog2);
                dialog.show();

            } else if (i == R.id.read_last_pos) {
                mDDService.addData(DDStatisticsService.UNDOSLIPING,
                        DDStatisticsService.OPerateTime,
                        System.currentTimeMillis() + "");

            } else if (i == R.id.read_top_search) {
                mDDService.addData(DDStatisticsService.SEARCH_IN_READER,
                        DDStatisticsService.OPerateTime,
                        System.currentTimeMillis() + "");
                toolBarSwitchShowing(2);
                getController().showSearch("");

            } else if (i == R.id.read_top_download || i == R.id.download_tv || i == R.id.download_ddreader_layout) {
                mRootView.removeView(mDownloadDDreaderLayout);
                downloadDDReader();

            } else if (i == R.id.delete_iv) {
                mRootView.removeView(mDownloadDDreaderLayout);

            } else {
            }
        }
    };

    final OnClickListener mGuideClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int i = v.getId();
            if (i == R.id.guide_close_button) {
            }
        }
    };

    protected void stopMedia() {
        getMediaInterface().onMediaStop();
    }

    protected boolean isVideoShow() {
        return getVideoInterface().isVideoShow();
    }

    protected boolean isVideoLandscape() {
        return getVideoInterface().isVideoLandscape();
    }

    protected boolean changeVideoOrientation() {
        return getVideoInterface().changeVideoOrientation();
    }

    protected void resetVedioView() {
        getVideoInterface().resetVedioView();
    }

    protected void resetVedioViewWithOutOrientation() {
        getVideoInterface().resetVedioViewWithOutOrientation();
    }

    public static boolean isServiceRunning(Context mContext, String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager
                .getRunningServices(100);
        if (!(serviceList.size() > 0)) {
            return false;
        }
        for (int i = 0; i < serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().equals(className) == true) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }

    protected void installApk(File file) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file),
                "application/vnd.android.package-archive");
        startActivity(intent);
    }


    final IMoveCompleteOperateCallBack mIMoveCompliteOperateCallBack = new IMoveCompleteOperateCallBack() {

        @Override
        public void toExitRead() {
            if (canExit())
                attemptExit(true);
        }

        @Override
        public void toBookReview() {
            startCommentActivity(false);
        }

        @Override
        public void toShare() {
            shareBook();
        }

        @Override
        public void toBookDetail() {
            startBookDetailActivity();
        }

        @Override
        public void toAddBookMark(boolean isAdd) {
            if (isAdd) {
                mMarkListener.addMark();
            } else {
                mMarkListener.removeMark();
            }
        }

        @Override
        public boolean isSelected() {
            return isSelectedStatus();
        }

    };

    protected void startBookDetailActivity() {
//        BuyBookStatisticsUtil.getInstance().setShowType(BuyBookStatisticsUtil.ShowType.SHOW_TYPE_READ);
//        BuyBookStatisticsUtil.getInstance().setShowTypeId("");
//
//        StoreEBookDetailActivity.launch(this, mReadInfo.getProductId(), mReadInfo.getProductId(), MediaOrderSource.DEFAULT);
    }

    protected void processDayAndNightSwitch() {
        updateScreenLight();
        performChangeBackground(-1);
        // mToolbar.updateNightMode();
    }

	/*
     * protected void settingMarkView(View view) { boolean bo =
	 * isMarkExistInCurrentPage(); if (bo) { view.setSelected(false); } else {
	 * view.setSelected(true); }
	 * readerWidget.getController().addOrDelMarkEvent(DRUiUtility
	 * .getScreenWith() - 10, 10); }
	 */

    public void showDirMarkNote(int flag) {

        int delayMillis = 0;
        needHideMenu();
        Message msg = handler.obtainMessage();
        msg.what = MSG_TO_DIRMARKNOTE;
        msg.arg1 = flag;
        handler.sendMessageDelayed(msg, delayMillis);
        mDDService.addData(DDStatisticsService.HITCIRCULARBOOKMARK,
                DDStatisticsService.OPerateTime, System.currentTimeMillis()
                        + "");
    }

    protected boolean mKeyDown = false;

    private long downTime = 0;

    //
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        APPLog.e(" event-onKeyDown " + keyCode + "," + isDelayOperation());
//        if (keyCode== KeyEvent.KEYCODE_BACK){
//            if (event.getRepeatCount()==0) {
//                downTime = System.currentTimeMillis();
////                handler.sendEmptyMessageDelayed(101, 2000);
//            }
//        }
//        return super.onKeyDown(keyCode, event);
//    }
    private long clickupOrDown=0;
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        APPLog.e(" event-onKeyUp " + keyCode + "," + isDelayOperation());
        long curTime=System.currentTimeMillis();
        if (Math.abs(curTime-clickupOrDown)<100)return true;
        clickupOrDown=curTime;
        switch (keyCode) {
            case KeyEvent.KEYCODE_MENU:
                if (mGuideWindow != null && mGuideWindow.isShow()) {
                    return true;
                }
                int delayMillis = 0;
            /*
             * if(readerLayout.isShow()){ readerLayout.hideMenu(); delayMillis =
			 * readerLayout.getDuration(); }
			 */
                if (!processSelectedOption()) {
                    setLightsOutMode(true);
                    break;
                } else if (getGWindow().isShowingWindow()) {
                    getGWindow().hideWindow(true);
                    setLightsOutMode(true);
                    break;
                }

                if (isMenuShow()) {
                    boolean hasExit = !needHideMenu() && mKeyDown;
                    attemptExit(hasExit);
                } else {
                    handler.sendEmptyMessageDelayed(MSG_SHOW_MENU_PRE, delayMillis);
                }
                break;
            case KeyEvent.KEYCODE_BACK:
                if (System.currentTimeMillis() - downTime >= 2000) break;
                handler.removeMessages(101);
                mKeyDown = true;
                if (isDelayOperation()) {
                    break;
                }
                if (isShowLoading) {
                    hideGifLoadingByUi();
                    break;
                }
                if (isSelectedStatus()) {
                    cancelSelected(true);
                    setLightsOutMode(true);
                    break;
                } else if (getGWindow().isShowingWindow()) {
                    getGWindow().hideWindow(true);
                    setLightsOutMode(true);
                    break;
                } else if (mReaderLayout.isShow()) {
                    mReaderLayout.hideMenu();
                    break;
                } else if (isVideoShow()) {
                    if (changeVideoOrientation()) {
                        mKeyDown = false;
                        return true;
                    } else {
                        resetVedioView();
                        mKeyDown = false;
                        return true;
                    }
                }
                if (needHideMenu()) {
                    mKeyDown = false;
                    return true;
                }
                if (!canExit()) {
                    mKeyDown = false;
                    return true;
                }
                boolean hasExit = mKeyDown;
//				boolean hasExit = !needHideMenu() && mKeyDown;
                attemptExit(hasExit);
                break;
            case KeyEvent.KEYCODE_PAGE_UP:
            case KeyEvent.KEYCODE_VOLUME_UP:
//                mReaderApps.pageTurning(true);
                turnPageByVolumeKey(true);
                return true;
            case KeyEvent.KEYCODE_PAGE_DOWN:
            case KeyEvent.KEYCODE_VOLUME_DOWN:
//                mReaderApps.pageTurning(false);
                turnPageByVolumeKey(false);
                return true;
        }
        mKeyDown = false;
        return super.onKeyUp(keyCode, event);
    }


    protected boolean canExit() {
        return true;
    }

    protected boolean processSelectedOption() {
        if (isSelectedStatus() || !isAnimFinish()) {
            return false;
        } else {
            cancelSelected(true);
            return true;
        }
    }

    protected boolean isAnimFinish() {
        return mReaderWidget != null && mReaderWidget.isAnimFinish();
    }

    protected void cancelSelected(boolean repaint) {
        getController().cancelOption(repaint);
    }

    protected boolean isSelectedStatus() {
        return getController().isSelectedStatus();
    }

    protected IGlobalWindow getGWindow() {
        return getController().getWindow();
    }

    protected boolean turnPageByVolumeKey(boolean up) {
//        if (!ReadConfig.getConfig().isVolKeyFlip()) {
//            return false;
//        }
//        if (isVideoLandscape())
//            return false;
//        if (isVideoShow())
//            return false;
//        if (!processSelectedOption()) {
//            return true;
//        }
//        if (isMenuShow()) {
//            needHideMenu();
//            return true;
//        }
//        if (settingNewDialog != null && settingNewDialog.isShowing()) {
//            settingNewDialog.dismiss();
//        }
//        if (saveNoteDialog != null && saveNoteDialog.isShowing()) {
//            saveNoteDialog.dismiss();
//        }
        mReaderApps.doFunction(up ? FunctionCode.FCODE_TURNPAGE_BACK
                : FunctionCode.FCODE_TURNPAGE_FORWARD);
        return true;
    }

    protected void attemptExit(boolean hasExit) {
        if (hasExit) {
            PersonalUtils.refreshUserInfo(mContext);
            if (mBookManager.isCanExit()) {
                finishActivity();
            } else {
                requestAbort(mAbortListener);
            }

            exitNormal();
        }
    }

    protected void refreshCurrentProgress(IBook book) {
        Chapter chapter = getBook(book).getChapter(mReadInfo.getChapterIndex());
        if (!mBookManager.isCacheChapter(chapter)) {
            mBookManager.getChapterPageCount(chapter);
        }
        getController().gotoPage(chapter, mReadInfo.getElementIndex());
    }

    protected void requestAbort(IAbortParserListener l) {
        mReaderApps.requestAbort(l);
        initSettingDialog();
    }

    protected void finishActivity() {
        processCache();
        //onReadDestroyImpl中已经有了，处理在这里调用同步笔记和书签空指针
        destoryData();
        cancelSelected(false);
        finish();
        printLog(" finishActivity end ");
    }

    protected void processCache() {
        if (!mReaderApps.isEpub() && !mReaderApps.isPdf()) {
            printLog(" process BookCache not epub or not pdf ");
            return;
        }
        printLog(" process BookCache start ");
        IBookCache bookCache = getBookCache();
        if (bookCache != null && bookCache.isAvailable()) {
            String bookId = mReadInfo.getDefaultPid();
            int isFull = cacheFull;
            BookCacheHandle.seriBookCache(this, bookId, isFull, bookCache);
        }
        printLog(" process BookCache end ");
    }

    protected boolean needHideMenu() {
        boolean hide = false;
        try {
            if (isMenuShow()) {
                toolBarSwitchShowing(3);
                hide = true;
                setLightsOutMode(true);
            }
        } catch (Exception e) {
            LogM.e(" needHideMenu " + e.toString());
        }
        return hide;
    }

    private SettingNewDialog settingNewDialog;

    /**
     * 点评屏幕呼唤起设置
     */
    protected void toolBarSwitchShowing(int i) {
//        mToolbar.switchShowing();
        int currentPage = mReaderApps.getCurrentPageIndex();
        int totalpage = mReaderApps.getPageSize();
        String mulu = getIntent().getStringExtra(IntentK.BookName);
        settingNewDialog = SettingNewDialog.getdialog(ReadActivity.this, mulu, totalpage, currentPage, anInterface);
    }

    private void toPage(int pageIndex) {
//        Book.BaseNavPoint point = ( (Book) mReaderApps.getBook()).getNavPoint(pageIndex);
//        mPreviewBar.setCurrentPosition(point);
//        APPLog.e("toPage", pageIndex);
//        mToolbar.scroolPage(pageIndex);
        onProgressBarChangeEnd(pageIndex);

    }

    private void initSettingDialog() {
        handlerRefuresh.postDelayed(new Runnable() {
            @Override
            public void run() {
                int totalpage = mReaderApps.getPageSize();
                if (null != settingNewDialog && settingNewDialog.isShowing()) {
                    int currentPage = mReaderApps.getCurrentPageIndex();
                    settingNewDialog.initSetProgress(totalpage, currentPage);
                }
                if (totalpage <= 0) {
                    listPoints.clear();
                }
                initDirectoryList();
            }

        }, 200);
    }

    /**
     * 目录跳转
     *
     * @param isLast
     */
    private void directoryChange(boolean isLast) {
        try {
            int totalpage = mReaderApps.getPageSize();
            int currentPage = mReaderApps.getCurrentPageIndex();
            if (totalpage <= 0) {
                listPoints.clear();
                return;
            }
            if (listPoints.size() <= 0) return;
            int size = listPoints.size();
            Book.BaseNavPoint basePoint=mToolbar.getNavPoint(currentPage);
            int index = 0;
            if (basePoint!=null) {
                String curFulls = basePoint.getFullSrc();
                int pageIndex = basePoint.getPageIndex();
                String lableText = basePoint.lableText;
                for (int i = 0; i < size; i++) {
                    if (listPoints.get(i).getPageIndex() == pageIndex &&
                            listPoints.get(i).getLableText().equals(lableText) &&
                            listPoints.get(i).getFullSrc().equals(curFulls)) {
                        index = i;
                        break;
                    }
                }
            }else {
                //base无法获取到页数，采用计算方式获取
                int total=1;
                    for (int i = 0; i <size; i++) {
                        int pageindex= listPoints.get(i).getPageIndex();
                        if (pageindex==0) {
                            index=i-1;
                            break;
                        }
                        int curt=total+pageindex;
                        if (currentPage>=total&&currentPage<curt){
                            index=i;
                            break;
                        }
                        total=curt;
                    }

            }
            if (isLast) {
                    if (index<=0) {
                        return;
                    }
                index--;
                handleEpubItemClick(listPoints.get(index));
                initSettingDialog();
            } else {
                index++;
                index=getIndex(index,size);
                if (index<(size-1)&&listPoints.get(index).getPageIndex()>0){
                    handleEpubItemClick(listPoints.get(index));
                    initSettingDialog();
                }else {
                    ToastUtils.getInstance().showToastShort("无更多章节！！");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    private int getIndex(int index,int size){
        for (int i = index; i < size; i++) {
            index=i;
            if (listPoints.get(i).getPageIndex()>0)break;
        }
        return index;
    }

    protected boolean isMenuShow() {
        return mToolbar.isShowing();
    }

    protected boolean isReadBottomMenuShowing() {
        return mToolbar != null && mToolbar.isReadBottomMenuShowing();
    }

    protected void removeLongClick() {
        mReaderWidget.removeLongClick();
    }

    @Override
    protected boolean isOperateMenuShow() {
        return getMediaInterface().isShowMedia();// getController().getWindow().isShowingWindow();
    }

    @Override
    protected void hideOperateMenu() {
        getController().getWindow().hideWindow(true);
    }

    @Override
    public void onReadPauseImpl() {
        hideShow();
        try {
//            UmengStatistics.onPageEnd(getClass().getSimpleName());
//            printLog(" onPause() ");
            switchWakeLock(false);
            updateProgress(true, 2);
        } catch (Exception e) {
            LogM.e(" onReadPauseImpl " + e.toString());
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        long time = getCurTime();
        if (mPauseTime != 0
                && (time - mPauseTime) >= DDStatisticsService.Interval) {
            mDDService.addData(DDStatisticsService.ReadingTime,
                    DDStatisticsService.ProductId, mReadInfo.getDefaultPid(),
                    DDStatisticsService.StartTime,
                    String.valueOf(mStartReadTime),
                    DDStatisticsService.EndTime, String.valueOf(mPauseTime),
                    DDStatisticsService.Length,
                    String.valueOf(mPauseTime - mStartReadTime));
            mStartReadTime = time;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        mPauseTime = getCurTime();
        try {
            changeVideoOrientation();
            stopMedia();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (isVideoShow()) {
                resetVedioViewWithOutOrientation();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        printLog(" onStop() ");
    }

    private boolean isfirst = true;

    @Override
    public void onReadResumeImpl() {
//        if (isfirst)
//        setttingScreenOrientaton(-1, false, isfirst);

        isfirst = false;

        printLog(" onResume() ");
//        if (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT != getCurrentOrientation() && !mIsLandSpace) {
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        }
        switchWakeLock(true);
        createWakeLock();
        initFirstInTime();
        UiUtil.hideInput(getReadMain());
//		InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//		mInputMethodManager.hideSoftInputFromWindow(getWindow().getDecorView()
//				.getApplicationWindowToken(), 0);
        setLightsOutMode(true);

        if (mToolbar == null) {
            return;
        }

        mStartReadTimeForTraining = getCurTime();
    }

    protected int getCurrentOrientation() {
        return this.getResources().getConfiguration().orientation;
    }

    @Override
    public void onReadDestroyImpl() {
        handlerRefuresh.removeCallbacksAndMessages(null);
        printLog("luxutagtag onDestroyImpl() " + this);
        processCloudSyncLogic();
        setDestroy();
        destoryData();
//        if (mShareUtil != null)
//            mShareUtil.clear();
//        mShareUtil = null;

        try {
            mMarkHandler.removeMessages(OPERATE_MARK);

            handler.removeMessages(MSG_SHOW_MENU);
            handler.removeMessages(MSG_SHOW_MENU_PRE);
            handler.removeMessages(MSG_TO_DIRMARKNOTE);
            handler.removeMessages(MSG_FINISH_READ);
            handler.removeMessages(MSG_TOAST_PROMPT);
            handler.removeMessages(MSG_RECOMPOSING);
            handler.removeMessages(MSG_COMPOSING_PROGRESS);
            handler.removeMessages(MSG_BOOK_LOADING_FINISHED);
            handler.removeMessages(MSG_BOOK_LOADING_START);
            handler.removeMessages(MSG_SHOW_READGUIDE);

            mHandler.removeMessages(DDTTS_GUIDE_ANIM);
            mHandler.removeMessages(RequestConstants.MSG_WHAT_REQUEST_DATA_SUCCESS);
            mHandler.removeMessages(RequestConstants.MSG_WHAT_REQUEST_DATA_FAIL);

            mCloudHandler.removeMessages(MSG_CLOUD_GOTO_READPROGRESS);
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        try {
            mEndReadTime = getCurTime();
            mDDService.addData(DDStatisticsService.ReadingTime,
                    DDStatisticsService.ProductId, mReadInfo.getDefaultPid(),
                    DDStatisticsService.StartTime,
                    String.valueOf(mStartReadTime),
                    DDStatisticsService.EndTime, String.valueOf(mEndReadTime),
                    DDStatisticsService.Length,
                    String.valueOf(mEndReadTime - mStartReadTime));

        } catch (Exception e1) {
            e1.printStackTrace();
        }

        try {
            // mReaderApps.setReaderEventListener(null);
            mReaderWidget.clear();
            // mReaderApps.clear();
            // mReaderApps = null;
            // mReaderWidget = null;
            if (mToolbar != null) {
                mToolbar.destory();
                if (mToolbar.isShowing()) {
                    toolBarSwitchShowing(4);
                }
                // mToolbar = null;
            }
            mRootView.removeAllViews();
        } catch (Exception e) {
            LogM.e("onReadDestroyImpl " + e.toString());
        }
    }

    protected void resetDestroy() {
        mDestroy = false;
    }

    protected void setDestroy() {
        mDestroy = true;
    }

    protected boolean isDestroy() {
        return mDestroy;
    }

    protected void processCloudSyncLogic() {

        try {
            /*cancelCommand(mGetProgressCmd);
            cancelCommand(mGetReadInfoCmd);*/
            if (mGetBookCloudReadInfoRequest != null) {
                cancelRequest(mGetBookCloudReadInfoRequest);
                mGetBookCloudReadInfoRequest = null;
            }
            if (mGetBookCloudReadProgressRequest != null) {
                cancelRequest(mGetBookCloudReadProgressRequest);
                mGetBookCloudReadProgressRequest = null;
            }


            mMarkNoteManager.mergeChangeMarkAndNote();
            mMarkNoteManager.clear();
            processAutoSubmitReadProgressAndReadInfoToCloud();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void destoryData() {
        APPLog.e("mFreeMem=" + mFreeMem);
        if (mFreeMem) {
            return;
        }
        mFreeMem = true;
        try {
            super.destoryData();
            mReaderApps.destroy();
            if (mReadReceiver != null) {
                unregisterReceiver(mReadReceiver);
                mReadReceiver = null;
            }
            if (mBatteryAndTimeReceiver != null) {
                unregisterReceiver(mBatteryAndTimeReceiver);
                mBatteryAndTimeReceiver = null;
            }
            mSpeKeyObserver.stopPowerListener();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void updateProgress(boolean notifyToShelf, int index) {
        final long lastTime = new Date().getTime();
        mReadInfo.setProgressFloat(getProgressFloat());
        mReadInfo.setOperateTime(lastTime);

        final String bid = mReadInfo.getDefaultPid();
        final String progressInfo = mReadInfo.buildProgressInfo(isPdf());
        sendBrodcastMain();

        if (notifyToShelf) {
            sendBroadCastToShelf(bid, progressInfo, lastTime);
        }
    }

    /**
     * 退出阅读发送广播
     */
    private void sendBrodcastMain() {
        //发送广播通知首页更新
        BrodcastData data = new BrodcastData();
        data.id = mReadInfo.getProductId();
        int currentPage = mReaderApps.getCurrentPageIndex();
        int totalpage = mReaderApps.getPageSize();
//        APPLog.e("currentPage="+currentPage);
//        APPLog.e("totalpage="+totalpage);
        if (totalpage > 0) {
            totalpage = totalpage - 1;
        }
        float progrssF = currentPage * 100f / totalpage;
        DecimalFormat format = new DecimalFormat("0.00");
        String value = format.format(progrssF) + "%";
        Intent refuresh = new Intent("RefureshBookInodrmationBrodcastrecever");
        refuresh.putExtra(BrodcastUtils.PROGRESS, mReadInfo.buildProgressInfo(value, false));
        refuresh.putExtra(BrodcastUtils.ID, data.id);
        sendBroadcast(refuresh);

        Intent intent = new Intent(BrodcastUtils.readBrodcast);
        intent.putExtra(BrodcastUtils.PROGRESS, mReadInfo.buildProgressInfo(value, false));
        intent.putExtra(BrodcastUtils.ID, data.id);
        sendBroadcast(intent);
    }

    protected float getProgressFloat() {

        float progress = mReadInfo.getProgressFloat();
        try {
            if (mReaderApps.isBookComposingDone()) {
                int pageIndexInBook = mReaderApps.getCurrentPageIndex();
                int pageSize = mReaderApps.getPageSize();
                if (pageSize > 0) {
                    if (pageIndexInBook > pageSize)
                        pageIndexInBook = pageSize;
                    progress = pageIndexInBook * 100f / pageSize;
                } else
                    LogM.e(TAG, "divide 0 exception, pageSize is " + pageSize);
            }
            progress = Utils.retainDecimal(progress, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return progress;
    }

    protected void sendBroadCastToShelf(String bid, String progressInfo,
                                        long lasttime) {

        DataUtil.getInstance(this).reorderBook(bid, progressInfo, null, false, false);

//		Intent intent = new Intent(Constants.BROADCAST_FINISH_READ);
//        intent.putExtra("bookId", bid);
//        intent.putExtra("readprogress", progressInfo);

//        if (mReadBook.getBookType() == BaseReadBook.BOOKTYPE_DD_DRM_EPUB) {
//            OriginalBook book = (OriginalBook) mReadBook;
//            intent.putExtra("isfollow", book.isFollow());
//            intent.putExtra("bookKey", book.getBookKey());
//            intent.putExtra("localLastIndexOrder",book.getChapters().size()-1);
//        }

//		sendBroadcast(intent);
    }

    final IAbortParserListener mAbortListener = new IAbortParserListener() {
        @Override
        public void alreadyAbort() {
            printLog(" mAbortListener alreadyAbort ");
            handler.sendEmptyMessage(MSG_FINISH_READ);
        }
    };

    protected void openHardwareAcceelerated() {
        // ---开启硬件加速，和 翻页速度 以及 放大镜显示有关 ，需要配合 manifest中的
        // <uses-sdk
        // android:minSdkVersion="8"/>
        // 使用，在manifest中，不能有 android:targetSdkVersion 这句话 start
        String model = Build.MODEL;
        String manufacturer = Build.MANUFACTURER;
        boolean madeInSony = manufacturer != null
                && manufacturer.toLowerCase().contains("sony");
        boolean contains = blackList.contains(model);
        if (!(madeInSony && contains)) {
            Window window = getWinD();
            final int FLAG_HARDWARE = 0x01000000;
            int flagmask = FLAG_HARDWARE;
            window.setFlags(flagmask, flagmask);
        }
        // ---开启硬件加速 end
    }

    protected final static int MSG_SHOW_MENU = 1;
    protected final static int MSG_SHOW_MENU_PRE = 2;
    protected final static int MSG_TO_DIRMARKNOTE = 3;
    protected final static int MSG_FINISH_READ = 4;
    protected final static int MSG_TOAST_PROMPT = 6;
    protected final static int MSG_RECOMPOSING = 7;
    protected final static int MSG_BOOK_LOADING_FINISHED = 8;
    protected final static int MSG_BOOK_LOADING_START = 9;
    protected final static int MSG_SHOW_TOBACKSHELF = 10;
    protected final static int MSG_HIDE_TOBACKSHELF = 11;
    protected final static int MSG_SHOW_READGUIDE = 12;
    protected final static int MSG_COMPOSING_PROGRESS = 13;
    protected final static int MSG_SHOW_TRAINING_OUTRANGE_DLG = 14;
    protected final static int MSG_SHOW_TRAINING_LEFTPAGES_DLG = 15;

    protected Handler handler;

    private void deal_Msg(Message msg) {
        switch (msg.what) {
            case MSG_TO_DIRMARKNOTE:
                mDDService.addData(DDStatisticsService.MEMUGETDIRECTORY,
                        DDStatisticsService.OPerateTime,
                        System.currentTimeMillis() + "");
                snapToScreen(DirectoryMarkNoteActivity.class);
                break;
            case MSG_SHOW_MENU_PRE:
                // int status = readerAppsNew.getParserStatus();
                if (isStructFinish()) {
                    showOrHideMenu();
                }
                break;
            case MSG_SHOW_MENU:
                break;
            case MSG_FINISH_READ:
                finishActivity();
                break;
            case MSG_TOAST_PROMPT:
                int resStr = msg.arg1;
                showToast(resStr);
                break;
            case MSG_RECOMPOSING:
                mReaderApps.doFunction(FunctionCode.FCODE_RECOMPOSING,
                        getRecomposingChapterIndex(),
                        getRecomposingElementIndex());
                mToolbar.setBookLoadingStart();
                break;
            case MSG_BOOK_LOADING_FINISHED:
                bookLoadingFinished();
                break;
            case MSG_BOOK_LOADING_START:
                mToolbar.setBookLoadingStart();
                break;
            case MSG_SHOW_TOBACKSHELF:
                findViewById(R.id.read_back_tobookshelf_layout).setVisibility(
                        View.VISIBLE);
                break;
            case MSG_HIDE_TOBACKSHELF:
                hideBackToShelfLayout();
                break;
            case MSG_SHOW_READGUIDE:
                try {
                    mGuideWindow.showReadGuide();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case MSG_COMPOSING_PROGRESS:
                int currPrgs = msg.arg1;
                int total = msg.arg2;
                mToolbar.updateRecompProgress(currPrgs, total);
                initSettingDialog();
                break;
            case 100:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                requestAbort(mAbortComposingByFont);
                break;
            case 101:
                anInterface.onScreenShort();
                break;
            default:
                handleOtherWhat(msg);
                break;
        }
    }

    private static class MHandler extends Handler {
        private final WeakReference<ReadActivity> mFragmentView;

        MHandler(ReadActivity view) {
            this.mFragmentView = new WeakReference<ReadActivity>(view);
        }

        @Override
        public void handleMessage(Message msg) {
            ReadActivity service = mFragmentView.get();
            if (service != null) {
                super.handleMessage(msg);
                try {
                    service.deal_Msg(msg);
                } catch (Exception e) {
                    LogM.e(service.TAG, e.toString());
                }
            }
        }
    }

    protected void handleOtherWhat(Message msg) {

    }

    protected void bookLoadingFinished() {
        final int pageIndexInBook = mReaderApps.getCurrentPageIndex();
        final int pageSize = mReaderApps.getPageSize();
        mToolbar.updateProgress(pageIndexInBook, pageSize);
        mToolbar.setBookLoadingFinish();

        //页面加载完毕-传输给setting
//
        initSettingDialog();

        // readerApps.getViewWidget().reSet();不能reSet
    }

    protected void showOrHideMenu() {

        mReaderWidget.repaint();
        mToolbar.setSyncButtonStatus(isCloudSyncFinish(),
                cloudSyncBaseCondition());
        // boolean isMarkExist = false;// isMarkExistInCurrentPage();
        /*
         * if (isLastPageInTryReadBook() && !topMenu.isShow()) {
		 * topMenu.showMenu(readerWidget, isMarkExist);
		 * topMenu.showOrHideLayout(View.GONE); return; }
		 */
        if (!mToolbar.isShowing()) {
            // topMenu.showMenu(readerWidget, isMarkExist);
            // topMenu.setPopupWindowBgColor(ReadConfig.BG_THEME[3].equals(ReadConfig.getConfig().getReaderBgStr()));

            toolBarSwitchShowing(5);
            setLightsOutMode(false);
            if (isBookComposingFinish()) {
                onComposingFinishUpdateProgress();
            } else if (mParserPrgs != null) {
                mToolbar.updateRecompProgress(mParserPrgs.beingchapterCount,
                        mParserPrgs.chapterCount);
            }
            mGuideWindow.showSettingGuide();
            setReComposingIndex(mReadInfo.getChapterIndex(),
                    mReadInfo.getElementIndex());
        }
        // showProgressMenu();
    }

    /**
     * 进度条处理
     */
    protected void onComposingFinishUpdateProgress() {
        final int pageIndexInBook = mReaderApps.getCurrentPageIndex();
        final int pageSize = mReaderApps.getPageSize();
        mToolbar.updateProgress(pageIndexInBook, pageSize);
    }

    protected void clearFloatLayer() {
        /*
         * readerWidget.getmFloatingWindow().dismissPopupWindow();
		 * readerWidget.getmNotePopupWindow().dismissPopupWindow();
		 * readerApps.getController().clearCursorPoint();
		 */
    }

    protected void hideBackToShelfLayout() {
        findViewById(R.id.read_back_tobookshelf_layout)
                .setVisibility(View.GONE);
    }

    @Override
    public void onScrollStart(ScrollEvent e) {

    }

    @Override
    public void onScrollComplete(ScrollEvent e) {
        if (mShowDirGuide) {
            mGuideWindow.showDirGuide();
        }
        printLog(" onScrollComplete " + e.curScreen);
        if (isBookComposingFinish()) {
            gotoCloudReadProgress();
        }
        if (mReaderWidget != null) {
            mReaderWidget.resetMorePointer();
        }
    }

    public void readEndViewBuy() {
        String type = mReadInfo.getTryOrFullStatisticsString();
        mDDService.addData(DDStatisticsService.APPEND_CART,
                DDStatisticsService.EBookType, type,
                DDStatisticsService.ReferType, "tryFinish",
                DDStatisticsService.OPerateTime, System.currentTimeMillis()
                        + "");
        onOneKeyBuyClickEvent();
    }

    protected class BatteryAndTimeReceiver extends BroadcastReceiver {

        protected void batteryEvent(Intent intent) {
            int current = intent.getExtras().getInt("level"); // 获得当前电量
            int total = intent.getExtras().getInt("scale"); // 获得总电量
            mCommonParam.setmBatteryValue(current * 1.0f / total); // 计算百分比
        }

        protected void timeEvent() {
            if (mCommonParam.ismRefreshTime()) {
                mCommonParam.setmCurTime(Utils.getCurrentTime());
                /*
                 * readerWidget.reSet(); readerWidget.invalidate();
				 */
                mReaderWidget.repaintFooter();
            }
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {
                batteryEvent(intent);
            } else if (Intent.ACTION_TIME_TICK.equals(action)) {
                timeEvent();
            }
        }
    }

    protected class ReadProcessReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            final String action = intent.getAction();
            APPLog.e(" onReceive action=" + action);
            if (Constant.ACTION_REMOVE_LONGCLICK.equals(action)) {
                // readerWidget.removeLongClick();
            } else if (Constant.ACTION_READER_FONT_TYPE.equals(action)) {
                resetView();
                reComposing();
            } else if (Intent.ACTION_USER_PRESENT.equals(action)) {
                /*
				 * ReadConfig config = ReadConfig.getConfig(); if
				 * (config.getAnimationType() == AnimType.Shift) { }
				 */
            } else if (Constant.ACTION_FINISH_READ.equals(action)
                    || Constant.ACTION_GET_COUPON_SUCCESS.equals(action)) {
                attemptExit(true);
            } else if (Constant.ACTION_READER_INIT_DICTPATH.equals(action)) {

            } else if (Constant.ACTION_READAREA_CHANGED.equals(action)) {
                initFullScreenStatus(ReadConfig.getConfig().isFullScreen());
                processReadAreaChanged();
            } else if (Constant.ACTION_READER_RECOMPOSING.equals(action)) {
                resetView();
                requestAbort(mAbortComposingByFont);
            } else if (ReadAction.ACTION_BOUGHT_SUCCESS.equals(action)) {
                try {
                    handleBoughtSucc(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (Constants.BROADCAST_BUY_DIALOG_CANCEL.equals(action) || Constants.ACTION_LOGIN_CANCEL.equals(action)) {
                // 取消购买.当前章如果为null。则关闭阅读，避免loading
                IEpubReaderController controller = (IEpubReaderController) mReaderApps.getReaderController();
                Chapter chapter = controller.getCurrentChapter();
                if (chapter == null) {
                    attemptExit(true);
                }
            } else if (Constants.BROADCAST_RECHARGE_SUCCESS.equals(action)) {
                onRechargeSucess(intent);
            } else if (StartActivityUtils.screenShot.equals(action)) {
                Intent intent1 = new Intent(StartActivityUtils.screenShot_BookSend);
                intent1.putExtra("is_system_broadcast", true);
                intent1.putExtra("backImgPath", intent.getStringExtra("backImgPath"));
                intent1.putExtra("title", intent.getStringExtra("title"));
                sendBroadcast(intent1);
            }
        }
    }

    protected void onRechargeSucess(Intent intent) {

    }

    public static final int DELAY_OPERATION = 0x04;
    public static final int DDTTS_NOTIFICATION_UPDATE = 0;
    public static final int DDTTS_START = 1;
    public static final int DDTTS_STOP = 2;
    public static final int DDTTS_GUIDE_ANIM = 5;

    private static class MyHandler extends Handler {
        private final WeakReference<ReadActivity> mFragmentView;

        MyHandler(ReadActivity view) {
            this.mFragmentView = new WeakReference<ReadActivity>(view);
        }

        @Override
        public void handleMessage(Message msg) {
            ReadActivity service = mFragmentView.get();
            if (service != null) {
                super.handleMessage(msg);
                try {
                    service.dealMsg(msg);
                } catch (Exception e) {
                    LogM.e(service.TAG, e.toString());
                }
            }
        }
    }

    private void dealMsg(Message msg) {
        switch (msg.what) {
            case DELAY_OPERATION:
                resetDelayOperation();
                break;
            case DDTTS_NOTIFICATION_UPDATE:
                break;
            case RequestConstants.MSG_WHAT_REQUEST_DATA_SUCCESS: {
                RequestResult result = (RequestResult) msg.obj;
                if ("updateBookCloudSyncReadProgressInfo".equals(result.getAction())) {
                    mUpdateCloudReadProgresssFinish = true;
                } else if ("getBookCloudSyncReadProgressInfo".equals(result.getAction())) {
                    mGetBookCloudReadProgressRequest = null;
                    processGetCloudReadProgressResult(result.getResult(), true);
                    mGetCloudReadProgressFinish = true;
                } else if ("getBookCloudSyncReadInfo".equals(result.getAction())) {
                    mGetBookCloudReadInfoRequest = null;
                    processsGetCloudReadInfoResult(result.getResult(), true);
                    mGetCloudReadInfoFinish = true;
                } else if ("updateBookCloudSyncReadInfo".equals(result.getAction())) {
                    Bundle bundle = msg.getData();
                    String markInfo = bundle.getString("markInfo");
                    String noteInfo = bundle.getString("noteInfo");
                    processUpdateCloudReadInfoResult(markInfo, noteInfo, result.getResult(), true);
                    mUpdateCloudReadInfoFinish = true;
                }
            }
            break;
            case RequestConstants.MSG_WHAT_REQUEST_DATA_FAIL: {
                RequestResult result = (RequestResult) msg.obj;
                if ("updateBookCloudSyncReadProgressInfo".equals(result.getAction())) {
                    mUpdateCloudReadProgresssFinish = true;
                } else if ("getBookCloudSyncReadProgressInfo".equals(result.getAction())) {
                    mGetBookCloudReadProgressRequest = null;
                    processGetCloudReadProgressResult(result.getResult(), false);
                    mGetCloudReadProgressFinish = true;
                } else if ("getBookCloudSyncReadInfo".equals(result.getAction())) {
                    mGetBookCloudReadInfoRequest = null;
                    processsGetCloudReadInfoResult(result.getResult(), false);
                    mGetCloudReadInfoFinish = true;
                } else if ("updateBookCloudSyncReadInfo".equals(result.getAction())) {
                    Bundle bundle = msg.getData();
                    String markInfo = bundle.getString("markInfo");
                    String noteInfo = bundle.getString("noteInfo");
                    processUpdateCloudReadInfoResult(markInfo, noteInfo, result.getResult(), false);
                    mUpdateCloudReadInfoFinish = true;
                }
            }
            break;
            default:
                break;
        }
    }

    protected void setDelayOperation() {
        mDelayOperation = true;
    }

    protected void resetDelayOperation() {
        mDelayOperation = false;
    }

    protected boolean isDelayOperation() {
        return mDelayOperation;
    }

    @Override
    public void onProgressBarChangeEnd(int progress) {
        // final int pageSize = bookManager.getPageSize();
        int page = progress;// * pageSize / 100;
        page = page > 0 ? page : 1;
        gotoReadProgress(page);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BookNoteActivity.REQUEST_CODE
                && resultCode == RESULT_OK) {
            onActivityResultNote(data);
        } else if (requestCode == MoreReadSettingsActivity.REQ_UPDATE_SETTING) {
            mReaderWidget.animChangeAfter();
            if (data != null) {
                int updateBg = data.getIntExtra(
                        MoreReadSettingsActivity.STRING_RESULT_UPDATE_BG, 0);
                int convert = data.getIntExtra(
                        MoreReadSettingsActivity.STRING_RESULT_CHINESE_CONVERT,
                        0);
                if (updateBg == MoreReadSettingsActivity.RESULT_UPDATE_BG) {
                    // updateModeSetToolbarScreenLight();
                    updateScreenLight();
                    performChangeBackground(-1);
                }
                if (convert == MoreReadSettingsActivity.RESULT_CHINESE_CONVERT) {
                    getController().reset();
                    resetView();
                }
            }
        } else if (requestCode == InteractiveBlockViewActivity.REQUEST_CODE) {
            if (mReaderWidget != null) {
                ((EpubReaderWidget) mReaderWidget).showInteractiveBlockIconView();
            }
        } else if (requestCode == REQUEST_CODE_BUY && resultCode == RESULT_OK) {
            dealBuySuccess();
        }
    }

    protected void dealBuySuccess() {
        if (mToolbar.isShowing())
            toolBarSwitchShowing(6);
        mReadInfo.setTryOrFull(ShelfBook.TryOrFull.FULL.ordinal());
        mReadInfo.setBought(true);
        Intent intent = new Intent();
        intent.setAction(Constant.ACTION_BOUGHT_EPUB_BOOK);
        sendBroadcast(intent);
        ShelfBook sb = DataUtil.getInstance(mContext).getShelfBookFromList(mReadInfo.getProductId());
        if (sb != null && sb.getBookFinish() == 1 && (sb.getTryOrFull() == ShelfBook.TryOrFull.BORROW_FULL
                || sb.getTryOrFull() == ShelfBook.TryOrFull.MONTH_FULL
                || sb.getTryOrFull() == ShelfBook.TryOrFull.GIFT_FULL
                || sb.getTryOrFull() == ShelfBook.TryOrFull.FULL)) {
            sb.setTryOrFull(ShelfBook.TryOrFull.FULL);
            sb = ShelfBookService.getInstance(mContext).saveOneBook(sb);
            if (sb.isUpdate()) {
                DataUtil.getInstance(mContext).updateBookInList(sb);
            }
            showToast(R.string.buy_success);
        } else {
            if (NetUtil.isMobileConnected(mContext) && !DDApplication.getApplication().isMobileNetAllowDownload()) {
                showMobilNetDownloadPromptDialog();
            } else {
                downloadBook();
            }
        }

    }

    private void downloadBook() {
        ShelfBook sb = DataUtil.getInstance(mContext).getShelfBookFromList(mReadInfo.getProductId());
        if (sb == null)
            return;

        sb.setTryOrFull(ShelfBook.TryOrFull.FULL);
        sb.setLastTime(System.currentTimeMillis());
        sb.setBookKey(null);
        File f = DownloadBookHandle.getHandle(mContext).getBookDest(true, sb.getMediaId(), sb.getBookType());
        sb.setBookDir(f.getParent());
        sb.setBookFinish(0);
        DataUtil.getInstance(this).downloadBook(sb, TAG);
        showToast(R.string.tips_buy_success);
    }

    private void showMobilNetDownloadPromptDialog() {
        final MobilNetDownloadPromptDialog dialog = new MobilNetDownloadPromptDialog(mContext);
        dialog.setOnLeftClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                showToast(R.string.tips_buy_success);
                dialog.dismiss();
            }
        });
        dialog.setOnRightClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                DDApplication.getApplication().setIsMobileNetAllowDownload(true);
                downloadBook();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    protected void onActivityResultNote(Intent data) {
        resetView();
        boolean bShare = data.getBooleanExtra(BookNoteActivity.BOOK_NOTE_SHARE_CHECK, false);
        if (bShare) {
            IGlobalWindow globalWindow = getGWindow();
            if (globalWindow instanceof GlobalWindow) {
                ReadInfo readInfo = (ReadInfo) getReadInfo();
                BookNote bookNote = (BookNote) data.getSerializableExtra(BookNoteActivity.BOOK_NOTE_OBJECT);
                EpubReaderController controller = (EpubReaderController) mReaderApps.getReaderController();
                if (controller != null) {
                    ((GlobalWindow) globalWindow).showShare(bookNote.getBookId(), bookNote.getBookPath(),
                            readInfo.getBookName(), bookNote.getSourceText(), controller.getSelectedTextWithPara(bookNote.getNoteStart(), bookNote.getNoteEnd()),
                            bookNote.getNoteText(), bookNote.getNoteTime(), readInfo.getInternetBookCover(), readInfo.getAuthorName(),
                            true, readInfo.getEBookType());
                }
            }
        }
    }

    protected void finishPrevRead() {
        updateProgress(true, 3);
        mBookManager.clearPrev();
        getController().reset();
        // resetView();
    }

    protected void resetViewAndData() {
		/*
		 * readerApps.getController().resetCurrentData();
		 * readerWidget.resetOffsetY();
		 */
        resetView();
    }

    protected void resetView() {
        mReaderWidget.reset();
        mReaderWidget.repaint();
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        hideBackToShelfLayout();
        resetViewAndData();
        requestFinishPrevAndStartNext();
        registerListener();

        changeBuyStatus();
    }

    protected void changeBuyStatus() {
        Intent changeIntent = new Intent(ReadAction.ACTION_CHANGEBOOK);
        changeIntent.putExtra(IntentK.ProductId, mReadInfo.getProductId());
        changeIntent.putExtra(IntentK.BookType, mReadInfo.getBookType());
        sendBroadcast(changeIntent);
    }

    protected void requestFinishPrevAndStartNext() {
        mReadInfo = (ReadInfo) mReaderApps.getReadInfo();
        requestAbort(new IAbortParserListener() {
            @Override
            public void alreadyAbort() {
                finishPrevRead();
                prepareRead();
            }
        });
    }

    public void addFinishReadStatistics() {
        String pid = mReadInfo.getDefaultPid();

        boolean bo = DangdangFileManager.isImportBook(pid);
        boolean ret = !bo && mReadInfo.isBought();

        if (bo || ret) {
            mDDService.addData(DDStatisticsService.FINISH_READ,
                    DDStatisticsService.ProductId, pid,
                    DDStatisticsService.OPerateTime,
                    String.valueOf(System.currentTimeMillis()));
        }

		/*
		 * if (!DangdangFileManager.isImportBook(pid) && readInfo.isBought) {
		 * mDDService.addData(DDStatisticsService.FINISH_READ,
		 * DDStatisticsService.ProductId, readInfo.pId,
		 * DDStatisticsService.OPerateTime
		 * ,String.valueOf(System.currentTimeMillis())); }
		 */
    }

    /**
     * 是否可执行自动云同步
     *
     * @return
     */
    protected boolean isPerformAutoCloucSyncWrap() {

        return isPerformAutoCloucSync() == SYNC_NETWORK_OK;
    }

    protected boolean isPerformAutoCloucSyncWrap2() {

        boolean autoCloud = false;
        if (autoCloudSyncBaseCondition()) {
            boolean isAutoSync = mSyncConfig.getAutoSyncSwitch();
            if (isAutoSync) {
                final Context context = getApplicationContext();
                if (NetUtils.isWifiConnected(context)) {
                    autoCloud = true;
                } else if (NetUtils.isMobileConnected(context)) {
                    autoCloud = !mSyncConfig.getAutoSyncOnlyWifi();
                    if (!autoCloud && mOnceAutoSyncOnlyWifi != 0) {
                        autoCloud = !isOnceAutoSyncOnlyWifi();
                    }
                }
            }
        }
        return autoCloud;
    }

    /**
     * 打开阅读这次 是否仅仅wifi自动同步
     *
     * @return
     */
    protected boolean isOnceAutoSyncOnlyWifi() {
        return mOnceAutoSyncOnlyWifi == ONCE_AUTOSYNC_STATUS_YES;
    }

    /**
     * 是否可执行自动云同步 0：不可以同步 1：可以同步 2：只有2G网，可以提示打开移动网同步 3：只有2G网，不再提示，不可以同步
     *
     * @return
     */
    protected int isPerformAutoCloucSync() {
        int realAutoSync = SYNC_NETWORK_NO;
        if (autoCloudSyncBaseCondition()) {
            boolean isAutoSync = mSyncConfig.getAutoSyncSwitch();
            if (isAutoSync) {
                boolean isOnlyWifiSync = mSyncConfig.getAutoSyncOnlyWifi()
                        || isOnceAutoSyncOnlyWifi();
                final Context context = getApplicationContext();
                if (NetUtils.isWifiConnected(context)) {
                    realAutoSync = SYNC_NETWORK_OK;
                } else if (NetUtils.isMobileConnected(context)) {
                    if (!isOnlyWifiSync) {
                        realAutoSync = mSyncConfig.isNoMobileDialogTip() ? SYNC_NETWORK_OK
                                : SYNC_NETWORK_NOTWIFI;
                    } else {
                        realAutoSync = mSyncConfig.isNoMobileDialogTip() ? SYNC_NETWORK_NOTWIFI_NOSYNC
                                : SYNC_NETWORK_NOTWIFI;
                    }
                }
            }
        }
        return realAutoSync;
    }

    /**
     * 没有网络
     */
    protected final static int SYNC_NETWORK_NO = 0;
    /**
     * 可以同步
     */
    protected final static int SYNC_NETWORK_OK = 1;
    /**
     * 只有2G网（目前实为非wifi网络），提示打开移动网同步
     */
    protected final static int SYNC_NETWORK_NOTWIFI = 2;

    /**
     * 只有2G网 （目前实为非wifi网络），不再提示，不可以同步
     */
    protected final static int SYNC_NETWORK_NOTWIFI_NOSYNC = 3;

    /**
     * 是否可执行云同步 0：没有网络 1：可以同步 2：只有2G网，提示打开移动网同步
     *
     * @return
     */
    protected int isPerformClickCloudSync() {
        int realAutoSync = SYNC_NETWORK_NO;
        if (cloudSyncBaseCondition()) {
            final Context context = getApplicationContext();
            if (!NetUtils.isNetworkConnected(context))
                return realAutoSync;
            boolean isOnlyWifiSync = mSyncConfig.getAutoSyncOnlyWifi()
                    || isOnceAutoSyncOnlyWifi();
            if (NetUtils.isWifiConnected(context)) {
                realAutoSync = SYNC_NETWORK_OK;
            } else if (NetUtils.isMobileConnected(context)) {
                if (!isOnlyWifiSync)
                    realAutoSync = SYNC_NETWORK_OK;
                else
                    realAutoSync = SYNC_NETWORK_NOTWIFI;
            }
        }
        return realAutoSync;
    }

    /**
     * 手动云同步条件
     *
     * @return
     */
    protected boolean isPerformClickCloudSyncWrap() {
        boolean clickCloudSync = false;
        if (mAccountManager.checkTokenValid()) {
            if (isPerformClickCloudSync() == SYNC_NETWORK_OK) {
                clickCloudSync = true;
            }
        }
        return clickCloudSync;
    }

    /**
     * 是否满足自动云同步基本条件
     *
     * @return
     */
    protected boolean autoCloudSyncBaseCondition() {
        final boolean isLogin = mAccountManager.checkTokenValid();
        return isLogin && cloudSyncBaseCondition();
    }

    /**
     * 是否满足云同步基本条件
     *
     * @return
     */
    protected boolean cloudSyncBaseCondition() {
        final boolean isEpubFull = mReadInfo.isDangEpub()
                && mReadInfo.isBought();// mReaderApps.isEpub()
        final boolean isPreset = mReadInfo.isPreSet();
        return isEpubFull && !isPreset;
    }

    /**
     * 手动获取云端阅读进度
     */
	/*
	 * protected void processClickSyncCloudReadProgress(){
	 * if(mAccountManager.checkTokenValid()){ final int status =
	 * isPerformClickCloudSync(); if(status == SYNC_NETWORK_OK){
	 * setClickCloudSync(); getCloudReadProgress(); } else if(status ==
	 * SYNC_NETWORK_NOTWIFI){ final SyncTipDialog openDialog = new
	 * SyncTipDialog(getReadMain(), R.style.dialog_commonbg);
	 * openDialog.setOnLeftClickListener(new OnClickListener() {
	 * 
	 * @Override public void onClick(View v) {
	 * mSyncConfig.saveNoMobileDialogTip(openDialog.isSelect());
	 * mSyncConfig.saveAutoSyncOnlyWifi(true);//TODO ? openDialog.dismiss(); }
	 * }); openDialog.setOnRightClickListener(new OnClickListener() {
	 * 
	 * @Override public void onClick(View v) {
	 * mSyncConfig.saveNoMobileDialogTip(openDialog.isSelect());
	 * mSyncConfig.saveAutoSyncOnlyWifi(false);//TODO ? openDialog.dismiss();
	 * setClickCloudSync(); getCloudReadProgress(); } }); openDialog.show(); }
	 * else if(status == SYNC_NETWORK_NO){ showToast(R.string.network_exp); } }
	 * else { Intent intent = new Intent(getReadMain(),
	 * RegisterAndLoginActivity.class); startActivity(intent); } }
	 */

    /**
     * 此书所属账号与当前登录账号是否同一个
     *
     * @return
     */
    protected boolean isSameUser() {
        final String userId = mReadInfo.getUserId();
        final String currentId = mAccountManager.getUserId();
        return !TextUtils.isEmpty(userId)
                && userId.equals(currentId);
    }

    /**
     * 自动获取云端阅读进度
     */
    protected void processAutoSyncGetCloudReadProgress() {
		/*
		 * if(!isPerformAutoCloucSyncWrap()){ return; }
		 */

        int status = isPerformAutoCloucSync();
        if (status == SYNC_NETWORK_NO || status == SYNC_NETWORK_NOTWIFI_NOSYNC) {
            return;
        }
        if (!isSameUser()) {
            printLog(" isSameUser=false ");
            return;
        }

        if (status == SYNC_NETWORK_NOTWIFI) {
            final SyncTipDialog openDialog = new SyncTipDialog(getReadMain(),
                    R.style.dialog_commonbg);
            openDialog.setOnLeftClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSyncConfig.saveNoMobileDialogTip(openDialog.isSelect());
                    if (openDialog.isSelect()) {
                        mSyncConfig.saveAutoSyncOnlyWifi(true);
                    }
                    mOnceAutoSyncOnlyWifi = ONCE_AUTOSYNC_STATUS_YES;
                    openDialog.dismiss();
                }
            });
            openDialog.setOnRightClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSyncConfig.saveNoMobileDialogTip(openDialog.isSelect());
                    if (openDialog.isSelect()) {
                        mSyncConfig.saveAutoSyncOnlyWifi(false);
                    }
                    mOnceAutoSyncOnlyWifi = ONCE_AUTOSYNC_STATUS_NO;
                    openDialog.dismiss();
                    getCloudReadProgress();

                    mDDService.addData(DDStatisticsService.MOBILEFLOWSYNC,
                            DDStatisticsService.OPerateTime,
                            String.valueOf(getCurTime()));
                    mDDService.addData(DDStatisticsService.SYNC,
                            DDStatisticsService.UserId,
                            mAccountManager.getUserId(),
                            DDStatisticsService.OPerateTime,
                            String.valueOf(getCurTime()));
                }
            });
            openDialog.show();
        } else {
            mDDService.addData(DDStatisticsService.SYNC,
                    DDStatisticsService.UserId, mAccountManager.getUserId(),
                    DDStatisticsService.OPerateTime,
                    String.valueOf(getCurTime()));

            getCloudReadProgress();
        }

    }

    /**
     * 自动提交阅读进度到云端
     */
	/*
	 * protected void processAutoSubmitReadProgressToCloud(){
	 * if(!isPerformAutoCloucSyncWrap()){ return; }
	 * 
	 * submitReadProgressToCloud(); }
	 */

    /**
     * 获取云端书签、笔记列表
     */
    protected void processGetBookCloudReadInfo() {
        // if(!isPerformAutoCloucSyncWrap()){
        if (!isPerformAutoCloucSyncWrap2()) {
            return;
        }

        getCloudReadInfo();
    }

    /**
     * 自动提交书签、笔记列表到云端
     */
	/*
	 * protected void processAutoSubmitBookCloudReadInfo(){
	 * if(!isPerformAutoCloucSyncWrap()){ return; }
	 * processSubmitBookCloudReadInfo(); }
	 */

    /**
     * 提交书签、笔记列表到云端
     */
    protected void processSubmitBookCloudReadInfo() {

        ServiceManager serviceManager = mReaderApps.getServiceManager();
        MarkService markService = serviceManager.getMarkService();
        NoteService noteService = serviceManager.getNoteService();

        String productId = mReadInfo.getProductId();
        int isBought = mReadInfo.isBoughtToInt();
        String userId = mAccountManager.getUserId();

        MarkData markData = markService.getNotSyncBookMarks(productId,
                isBought, userId);
        NoteData noteData = noteService.getNotSyncBookNotes(productId,
                isBought, userId);
        String marksJson = markData.getMarksString();
        String notesJson = noteData.getNotesString();

        if (TextUtils.isEmpty(marksJson) && TextUtils.isEmpty(notesJson)) {
            return;
        }
        submitReadInfoToCloud(marksJson, notesJson);
    }

    /**
     * 自动提交阅读进度和书签、笔记等到云端
     */
    protected void processAutoSubmitReadProgressAndReadInfoToCloud() {
        // if(!isPerformAutoCloucSyncWrap()){
        if (!isPerformAutoCloucSyncWrap2()) {
            return;
        }
        if (!isSameUser()) {
            printLog(" isSameUser=false ");
            return;
        }

        submitReadProgressToCloud();
        processSubmitBookCloudReadInfo();
    }

    /**
     * 处理提交阅读进度和书签、笔记等到云端
     */
    protected void processSubmitReadProgressAndReadInfoToCloud() {
        // if(!isPerformAutoCloucSyncWrap()){
        if (!isPerformAutoCloucSyncWrap2()) {
            return;
        }

        submitReadProgressToCloud();
        processSubmitBookCloudReadInfo();
    }

    protected void getCloudReadProgress() {
        mGetCloudReadProgressFinish = false;
        final String productId = mReadInfo.getProductId();
        mGetBookCloudReadProgressRequest = new GetBookCloudReadProgressRequest(productId, mHandler);
        sendRequest(mGetBookCloudReadProgressRequest);
        //mGetProgressCmd = sendCommand(GetBookCloudReadProgress, productId);
    }

    protected void getCloudReadInfo() {
        mGetCloudReadInfoFinish = false;
        // String token =
        // mAccountManager.getToken();//"1BB45136183241088BED2C527303F70CANcOGS";//
        String productId = mReadInfo.getProductId();// "1900000924";//
        long versionTime = mReadInfo.getVersionTime();// "1900000924";//
		/*mGetReadInfoCmd = sendCommand(GetBookCloudReadInfo, productId,
				versionTime);*/
        mGetBookCloudReadInfoRequest = new GetBookCloudReadInfoRequest(productId, versionTime, mHandler);
        sendRequest(mGetBookCloudReadInfoRequest);
    }

    protected void submitReadProgressToCloud() {

        mUpdateCloudReadProgresssFinish = false;
        String productId = mReadInfo.getProductId();
        int chapterIndex = mReadInfo.getChapterIndex();// readInfo.getPrevChapterIndex();//
        int elementIndex = mReadInfo.getElementIndex();// readInfo.getPrevElementIndex();//
        long operateTime = mReadInfo.getOperateTime() / 1000;// readInfo.getPrevOperateTime()
        // / 1000;//
        long[] time = CloudSyncConvert
                .parseStartEndTimeFromReadTimeInfo(mReadInfo.getReadTimeInfo());
        String progressInfo = CloudSyncConvert.convertProgress(productId,
                chapterIndex, elementIndex, operateTime, time[0], time[1]);

        //sendCommand(UpdateBookCloudReadProgress, progressInfo);
        UpdateBookCloudReadProgressRequest request = new UpdateBookCloudReadProgressRequest(progressInfo, mHandler);
        sendRequest(request);
    }

    protected void submitReadInfoToCloud(String marksJson, String notesJson) {

        mUpdateCloudReadInfoFinish = false;
        String tMarkInfo = marksJson;
        String tNoteInfo = notesJson;
        long versionTime = mReadInfo.getVersionTime();
        //sendCommand(UpdateBookCloudReadInfo, tMarkInfo, tNoteInfo, versionTime);
        UpdateBookCloudReadInfoRequest request = new UpdateBookCloudReadInfoRequest(mHandler, tMarkInfo, tNoteInfo, versionTime, ReferType.READER);
        sendRequest(request);
    }

    protected boolean isCloudSyncFinish() {

        return mGetCloudReadProgressFinish && mGetCloudReadInfoFinish
                && mUpdateCloudReadInfoFinish
                && mUpdateCloudReadProgresssFinish;
    }

    protected boolean mGetCloudReadProgressFinish = true;
    protected boolean mGetCloudReadInfoFinish = true;
    protected boolean mUpdateCloudReadInfoFinish = true;
    protected boolean mUpdateCloudReadProgresssFinish = true;

	/*@Override
	public void onCommandResult(CommandResult result) {
		try {
			DangDang_Method action = result.getCommand().getAction();
			// printLog("  onCommandResult " + action.getMethod() + " = " +
			// result.getResultType().name());
			if (action == GetBookCloudReadProgress) {
				mGetProgressCmd = null;
				processGetCloudReadProgressResult(result);
				mGetCloudReadProgressFinish = true;
			} else if (action == GetBookCloudReadInfo) {
				mGetReadInfoCmd = null;
				processsGetCloudReadInfoResult(result);
				mGetCloudReadInfoFinish = true;
			} else if (action == UpdateBookCloudReadInfo) {
				processUpdateCloudReadInfoResult(result);
				mUpdateCloudReadInfoFinish = true;
			} else if (action == UpdateBookCloudReadProgress) {
				processUpdateCloudReadProgressResult(result);
				mUpdateCloudReadProgresssFinish = true;
			}

			mCloudHandler.sendEmptyMessage(MSG_CLOUD_CHECK_SYNCFINISH);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/

    /*protected void processGetCloudReadProgressResult(CommandResult result) {
        if (isDestroy()) {
            printLog(" GetCloudReadProgressResult destroy = true ");
            return;
        }
        Object dataResult = result.getResult();
        if (result.getResultType() == ResultType.Success) {
            processGetCloudReadProgressResultSuccess(dataResult);
        } else {

        }
        processGetBookCloudReadInfo();
    }*/
    protected void processGetCloudReadProgressResult(Object dataResult, boolean success) {
        if (isDestroy()) {
            printLog(" GetCloudReadProgressResult destroy = true ");
            return;
        }
        if (success) {
            processGetCloudReadProgressResultSuccess(dataResult);
        } else {

        }
        processGetBookCloudReadInfo();
    }

    /*protected void processsGetCloudReadInfoResult(CommandResult result) {
		if (isDestroy()) {
			printLog(" GetCloudReadInfoResult destroy = true ");
			return;
		}
		Object dataResult = result.getResult();
		if (result.getResultType() == ResultType.Success) {
			processGetCloudReadInfoResultSuccess(dataResult);
		} else {

		}
		processSubmitReadProgressAndReadInfoToCloud();
	}*/
    protected void processsGetCloudReadInfoResult(Object dataResult, boolean success) {
        if (isDestroy()) {
            printLog(" GetCloudReadInfoResult destroy = true ");
            return;
        }
        if (success) {
            processGetCloudReadInfoResultSuccess(dataResult);
        } else {

        }
        processSubmitReadProgressAndReadInfoToCloud();
    }

    /*protected void processUpdateCloudReadInfoResult(CommandResult result) {
        Object dataResult = result.getResult();
        if (result.getResultType() == ResultType.Success) {
            processUpdateCloudReadInfoResultSuccess(result, dataResult);
        } else {

        }
    }

    protected void processUpdateCloudReadProgressResult(CommandResult result) {

    }*/
    protected void processUpdateCloudReadInfoResult(String marksJson, String notesJson, Object dataResult, boolean success) {
        if (success) {
            processUpdateCloudReadInfoResultSuccess(marksJson, notesJson, dataResult);
        } else {
        }
    }

    protected void processGetCloudReadProgressResultSuccess(Object dataResult) {
        if (dataResult != null) {
            mCloudProgress = (CloudReadProgress) dataResult;
            printLog(" GetCloudReadProgressSuccess " + isBookComposingFinish());
            if (isBookComposingFinish()) {
                gotoCloudReadProgress();
            }
            String readTimeInfo = getReadTimeInfo();
            readTimeInfo = CloudSyncConvert.writeStartEndTimeToReadTimeInfo(
                    readTimeInfo, mCloudProgress.getStartTime(),
                    mCloudProgress.getEndTime());
            mReadInfo.setReadTimeInfo(readTimeInfo);
			/*mShelfService.updateBookReadTime(mReadInfo.getDefaultPid(),
					readTimeInfo);*/
        }
    }

    protected String getReadTimeInfo() {
        String readTimeInfo = mReadInfo.getReadTimeInfo();
        if (Utils.isStringEmpty(readTimeInfo)) {
            ShelfBookService shelfService = ShelfBookService.getInstance(this);
            ShelfBook info = shelfService.getShelfBookById(mReadInfo
                    .getDefaultPid());
            if (info != null) {
                readTimeInfo = info.getTotalTime();
                mReadInfo.setReadTimeInfo(readTimeInfo);
            }
        }
        return readTimeInfo;
    }

    protected void gotoCloudReadProgress() {
        if (mCloudProgress == null) {
            return;
        }
        printLog(" CurrentRead = " + isCurrentRead());
        if (isCurrentRead()) {
            Message msg = mCloudHandler.obtainMessage();
            msg.what = MSG_CLOUD_GOTO_READPROGRESS;
            msg.obj = mCloudProgress;
            mCloudHandler.sendMessage(msg);
        }
    }

    protected void processGetCloudReadInfoResultSuccess(Object dataResult) {
        if (dataResult != null) {
            CloudDataList dataList = (CloudDataList) dataResult;
            if (!dataList.isEmpty()) {
                if (!dataList.isMarkEmpty()) {
                    ReadConfig.getConfig().setFirstMarkFlag(false);
                }
                updateVersionTime(dataList.getVersionTime());
                mMarkNoteManager.mergeMarkAndNote(dataList);
                mMarkNoteManager.mergeChangeMarkAndNote();
                dataList.clearList();
                if (isStructFinish())
                    mMarkNoteManager.UpdateMarksAndBookNotesIfModVersionChange((Book) mReaderApps.getBook(), mBookManager);

                mCloudHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        resetView();
                    }
                });
            }
        }

    }

    /*protected void processUpdateCloudReadInfoResultSuccess(CommandResult result,
            Object dataResult) {
        if (dataResult != null) {
            long versionTime = (Long) dataResult;
            updateVersionTime(versionTime);

            String marksJson = (String) result.getCommand().getParameters()[0];
            String notesJson = (String) result.getCommand().getParameters()[1];
            ServiceManager serverManager = mReaderApps.getServiceManager();// TODO
                                                                            // ?
            if (!TextUtils.isEmpty(marksJson)) {
                try {
                    JSONArray marks = new JSONArray(marksJson);
                    long modifyTime = versionTime;//
                    serverManager.getMarkService().updateMarksCloudStatus(
                            marks, Status.CLOUD_YES, modifyTime);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (!TextUtils.isEmpty(notesJson)) {
                try {
                    JSONArray notes = new JSONArray(notesJson);
                    long modifyTime = versionTime;
                    serverManager.getNoteService().updateNotesCloudStatus(
                            notes, Status.CLOUD_YES, modifyTime);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }*/
    protected void processUpdateCloudReadInfoResultSuccess(String marksJson, String notesJson,
                                                           Object dataResult) {
        if (dataResult != null) {
            if (mReaderApps == null || mReaderApps.getServiceManager() == null)
                return;
            JSONObject jsonObject = (JSONObject) dataResult;
//            long versionTime = (Long) dataResult;
            long versionTime = jsonObject.getLong("versionTime");
            updateVersionTime(versionTime);

//            String marksJson = (String) result.getCommand().getParameters()[0];
//            String notesJson = (String) result.getCommand().getParameters()[1];
            ServiceManager serverManager = mReaderApps.getServiceManager();// TODO
            // ?
            if (!TextUtils.isEmpty(marksJson)) {
                try {
                    JSONArray marks = new JSONArray(marksJson);
                    long modifyTime = versionTime;//
                    serverManager.getMarkService().updateMarksCloudStatus(
                            marks, Status.CLOUD_YES, modifyTime);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (!TextUtils.isEmpty(notesJson)) {
                try {
                    JSONArray notes = new JSONArray(notesJson);
                    long modifyTime = versionTime;
                    serverManager.getNoteService().updateNotesCloudStatus(
                            notes, Status.CLOUD_YES, modifyTime);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    protected void updateVersionTime(long versionTime) {
        if (versionTime != 0 && mReadInfo.getVersionTime() != versionTime) {// TODO
            // 还是<
            mReadInfo.setVersionTime(versionTime);
            updateProgress(false, 4);
        }
    }

    protected final static int MSG_CLOUD_GOTO_READPROGRESS = 1;
    protected final static int MSG_CLOUD_CHECK_SYNCFINISH = 2;

    protected Handler mCloudHandler;

    private static class CloudHandler extends Handler {
        private final WeakReference<ReadActivity> mFragmentView;

        CloudHandler(ReadActivity view) {
            this.mFragmentView = new WeakReference<ReadActivity>(view);
        }

        @Override
        public void handleMessage(Message msg) {
            ReadActivity service = mFragmentView.get();
            if (service != null) {
                super.handleMessage(msg);
                try {
                    switch (msg.what) {
                        case MSG_CLOUD_GOTO_READPROGRESS:
                            service.handleGotoReadProgress((CloudReadProgress) msg.obj);
                            break;
                        case MSG_CLOUD_CHECK_SYNCFINISH:
					/*
					 * if(topMenu.isShow()){
					 * topMenu.setSyncFinishStatus(isCloudSyncFinish()); }
					 */
                            break;
                    }
                } catch (Exception e) {
                    LogM.e(service.TAG, e.toString());
                }
            }
        }
    }

    protected void handleGotoReadProgress(CloudReadProgress cloudProgress) {
        printLog(" goto CloudReadProgress " + cloudProgress + ", curAct = "
                + isCurrentRead());
        if (cloudProgress == null) {
            return;
        }

        final long localClientOperateTime = mReadInfo.getPrevOperateTime();
        final long cloudClientOperateTime = cloudProgress
                .getClientOperateTime() * 1000;// 云端时间单位为秒，需要转成毫秒 1402307303121
        // 1402307463000
        if (mReadInfo.getProductId().equals(cloudProgress.getProductId())
                && localClientOperateTime < cloudClientOperateTime) {

            final int localChapterIndex = mReadInfo.getPrevChapterIndex();
            final int localElementIndex = mReadInfo.getPrevElementIndex();

            final int cloudChapterIndex = cloudProgress.getChapterIndex();
            final int cloudElementIndex = cloudProgress.getElementIndex();

            int localPage = mBookManager.getPageIndexInBook(localChapterIndex,
                    localElementIndex);
            final int cloudPage = mBookManager.getPageIndexInBook(
                    cloudChapterIndex, cloudElementIndex);
            if (localPage != cloudPage && !mbShowChooseStartDlg) {
                final int showLocalPage = mBookManager.getPageIndexInBook(
                        mReadInfo.getChapterIndex(),
                        mReadInfo.getElementIndex());
                showSynDialog(cloudChapterIndex, cloudElementIndex, showLocalPage, cloudPage, cloudClientOperateTime);
            }
        }
        resetCloudProgress();
    }

    protected boolean changeLocalReadProgress(int chapterIndex, int elementIndex,
                                              int cloudPageIndex) {
        mReadInfo.setChapterIndex(chapterIndex, 1);
        mReadInfo.setElementIndex(elementIndex);

        cloudPageIndex = cloudPageIndex > 0 ? cloudPageIndex : 1;
        gotoReadProgress(cloudPageIndex);
        clearFloatLayer();

        updateProgress(false, 5);

        return true;
    }

    protected void processRecomposingIndex(int cloudChapterIndex,
                                           int cloudElementIndex) {
        if (mToolbar != null && mToolbar.isShowing()) {
            setReComposingIndex(cloudChapterIndex, cloudElementIndex);
        }
    }

    protected void setReComposingIndex(int cloudChapterIndex,
                                       int cloudElementIndex) {
        mReComposingChapterIndex = cloudChapterIndex;
        mReComposingElementIndex = cloudElementIndex;
    }

    protected int getRecomposingChapterIndex() {
        return mReComposingChapterIndex;
    }

    protected int getRecomposingElementIndex() {
        return mReComposingElementIndex;
    }

    protected void gotoReadProgress(int pageIndexInBook) {
        mReaderApps.doFunction(FunctionCode.FCODE_GOTO_PAGEINDEX,
                pageIndexInBook, false);
    }

    protected void resetCloudProgress() {
        mCloudProgress = null;
    }

    protected Book getBook(IBook book) {
        return (Book) book;
    }

    protected IEpubReaderController getController() {
        return (IEpubReaderController) mReaderApps.getReaderController();
    }

    protected IMediaInterface getMediaInterface() {
        return (IMediaInterface) mReaderApps.getReaderController();
    }

    protected IVideoInterface getVideoInterface() {
        return (IVideoInterface) mReaderApps.getReaderController();
    }

    protected BaseBookManager getBookManager() {
        return mBookManager;
    }

    protected void resetComposingFinish() {
        mComposingFinish = false;
    }

    protected void setComposingFinish() {
        mComposingFinish = true;
    }

    protected boolean isBookComposingFinish() {
        return mComposingFinish;
    }

    protected void setStructFinish() {
        mStructFinish = true;
    }

    protected boolean isStructFinish() {
        return mStructFinish;
    }

    protected void resetBookCache() {
        mBookCache = null;
    }

    protected void setBookCache(IBookCache bookCache) {
        mBookCache = bookCache;
    }

    protected IBookCache getBookCache() {
        return mBookCache;
    }

    public void setShowDirGuide(boolean show) {
        mShowDirGuide = show;
    }

    @Override
    protected boolean supportDoubleAdjustLight() {
        return true;
    }

    @Override
    protected boolean isOperateBookMark() {
        return mReaderLayout != null && mReaderLayout.isOperate();
    }

    public void printLog(String log) {
//        LogM.i(getClass().getSimpleName(), log);
    }

    protected IOnDisMissCallBack mOnDismissCallBack = new IOnDisMissCallBack() {

        @Override
        public void onDismissCallBack() {
            setLightsOutMode(true);
        }
    };

    protected void handleBoughtSucc(Intent intent) {
		/*List<String> boughtSucc = intent
				.getStringArrayListExtra(BookStoreActivity.KEY_ADD_DOWNLOAD_PID);
		if (boughtSucc == null || boughtSucc.isEmpty()) {
			return;
		}
		ReadInfo readInfo = mReadInfo;
		if (readInfo.isDangEpub()) {
			if (boughtSucc.contains(readInfo.getProductId())) {
				readInfo.setBought(true);
				readInfo.setTryOrFull(TryOrFullValue.COMMON_FULL);
				if (mToolbar.isShowing()) {
					mToolbar.refreshBuyViewStatus(readInfo);
				}

				final boolean isLastPage = getController().isLastPageInBook();
				if (isLastPage) {
					changeBuyStatus();
				}
			}
		}*/
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        processConfigurationChanged();
    }

    protected void processConfigurationChanged() {
        DRUiUtility.getUiUtilityInstance().DontKeepContext(this);
        final ReadConfig readConfig = ReadConfig.getConfig();
        int tmpCpsWidth = readConfig.getReadWidth();
        int tmpCpsHeight = readConfig.getReadHeight();
        if (tmpCpsWidth != mPrevCpsWidth || tmpCpsHeight != mPrevCpsHeight) {
            printLog(" onSizeChanged request recomposing prev:" + mPrevCpsWidth
                    + "-" + mPrevCpsHeight + ", curr:" + tmpCpsWidth + "-"
                    + tmpCpsHeight);

            setPrevComposingArea(tmpCpsWidth, tmpCpsHeight);
            mReaderWidget.onSizeChange();
            setReComposingIndex(mReadInfo.getChapterIndex(),
                    mReadInfo.getElementIndex());
        }
    }

    private boolean isShowLoading = false;

    @Override
    public void showGifLoadingByUi() {
        super.showGifLoadingByUi();
        isShowLoading = true;
    }

    @Override
    public void hideGifLoadingByUi() {
        super.hideGifLoadingByUi();
        isShowLoading = false;
    }

    public boolean isLandScape() {
        return mIsLandSpace;
    }

    public void showSynDialog(final int cloudChapterIndex, final int cloudElementIndex, final int localPage,
                              final int cloudPage, final long cloudClientOperateTime) {
        final int showLocalPage = localPage;
        final int showCloudPage = cloudPage;
        String date = Utils.dateFormat(cloudClientOperateTime);
        if (mSyncDialog == null) {
            mSyncDialog = new SyncDialog(getReadMain(),
                    R.style.dialog_commonbg);
        }
        mSyncDialog.setOnRightClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mSyncDialog.dismiss();
                if (changeLocalReadProgress(cloudChapterIndex,
                        cloudElementIndex, cloudPage)) {
                    processRecomposingIndex(cloudChapterIndex,
                            cloudElementIndex);
                    mDDService.addData(DDStatisticsService.ISSYNCPROGRESS,
                            DDStatisticsService.OPerateTime,
                            String.valueOf(getCurTime()));
                    if (!isCurrentRead()) {
                        snapToScreen(getClass());
                    }
                }
            }

        });
        mSyncDialog.setCanceledOnTouchOutside(false);
        mSyncDialog.setOnLeftClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mSyncDialog.dismiss();
            }
        });
        mSyncDialog.setInfo(String.valueOf(showLocalPage), date,
                String.valueOf(showCloudPage));
        mSyncDialog.show();
    }

    public void downloadDDReader() {
//        try {
//            Uri uri = Uri.parse("market://details?id=com.dangdang.reader");
//            Intent marketIntent = new Intent(Intent.ACTION_VIEW, uri);
//            marketIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(marketIntent);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        String id = mReadInfo.getProductId();
        Intent intent = new Intent("activity.EbookDetailActivity");
        intent.putExtra("saleId", Long.parseLong(id));
        startActivity(intent);
        //退出
        onBackPressed();
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        sendBrodcastMain();
        if (settingNewDialog != null && settingNewDialog.isShowing()) {
            settingNewDialog.dismiss();
        }
        if (saveNoteDialog != null && saveNoteDialog.isShowing()) {
            saveNoteDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        if (receiver != null)
            unregisterReceiver(receiver);

        if (screenShot != null) screenShot.destoryView();
        super.onDestroy();
    }

    /**
     * 章节跳转
     *
     * @param basePoint
     */
    private void handleEpubItemClick(Book.BaseNavPoint basePoint) {
        if (basePoint instanceof EpubBook.EpubNavPoint) {
            final IFunctionManager readerApps = getGlobalApp();
            // final String fCode = FunctionCode.FCODE_GOTO_PAGENEW;
            // final int sourceType = Constant.GOTOPAGE_DIR;
            EpubBook.EpubNavPoint navPoint = (EpubBook.EpubNavPoint) basePoint;
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
                    goParams.setType(IEpubReaderController.GoToType.Anchor);
                    goParams.setAnchor(anchor);
                    goParams.setChapter(chapter);

                    readerApps.doFunction(FunctionCode.FCODE_GOTO_PAGECHAPTER,
                            goParams);
                }
            }
        }
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

    private List<Book.BaseNavPoint> listPoints = new ArrayList<>();

    private void initDirectoryList() {

        if (listPoints == null) {
            listPoints = new ArrayList<>();
        }
        if (listPoints.size() == 0) {
            final Book book = getBook();
            if (book == null) {// TODO ? 临时容错
                return;
            }
            if (book instanceof PartBook) {
                listPoints = ((PartBook) book).getAllNavPointAndVolumeList();
            } else {
                listPoints = book.getAllNavPointList();
            }
        }
//        if (listPoints.size() == 0) return;
//        int currentPage = mReaderApps.getCurrentPageIndex();
//        if (currentPage <= 0) return;
//
//        int lastTotal = 1;
//        for (int i = 1; i < listPoints.size(); i++) {
//            int curIndex = listPoints.get(i-1).getPageIndex();
//            if (listPoints.get(i).getPageIndex() == 0) {
//                break;
//            }
//            lastTotal += curIndex;
//            listPoints.get(i).setPageIndex(lastTotal);
//        }
//        int index = 0;
//        for (Book.BaseNavPoint point : listPoints) {
//            APPLog.e("BaseNavPoint-index=" + index, "fullSrc=" + point.fullSrc + "  lableText=" + point.lableText + "  pageIndex=" + point.getPageIndex());
//            index++;
//        }

    }
}
