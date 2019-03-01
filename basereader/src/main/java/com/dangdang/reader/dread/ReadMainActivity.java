package com.dangdang.reader.dread;

import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;

import com.dangdang.execption.FileFormatException;
import com.dangdang.reader.R;
import com.dangdang.reader.base.BaseGroupActivity;
import com.dangdang.reader.db.service.ShelfBookService;
import com.dangdang.reader.dread.config.ReadConfig;
import com.dangdang.reader.dread.core.base.BaseGlobalApplication;
import com.dangdang.reader.dread.core.base.IReaderApplication.IAbortParserListener;
import com.dangdang.reader.dread.core.epub.ReaderAppImpl;
import com.dangdang.reader.dread.data.PartReadInfo;
import com.dangdang.reader.dread.data.ReadInfo;
import com.dangdang.reader.dread.data.ReadTimes;
import com.dangdang.reader.dread.font.FontListHandle;
import com.dangdang.reader.dread.format.BaseReadInfo;
import com.dangdang.reader.dread.format.Book;
import com.dangdang.reader.dread.format.DDFile;
import com.dangdang.reader.dread.jni.BaseJniWarp;
import com.dangdang.reader.dread.jni.EpubWrap;
import com.dangdang.reader.dread.service.FontDownloadService;
import com.dangdang.reader.dread.service.ReadTimesService;
import com.dangdang.reader.dread.util.DrmWrapUtil;
import com.dangdang.reader.dread.util.IntentK;
import com.dangdang.reader.dread.util.ReadBookUtil;
import com.dangdang.reader.dread.view.ReaderScrollView;
import com.dangdang.reader.dread.view.ReaderScrollView.OnScrollStatusListener;
import com.dangdang.reader.dread.view.ReaderScrollView.ScrollEvent;
import com.dangdang.reader.personal.domain.ShelfBook;
import com.dangdang.reader.statis.DDClickHandle;
import com.dangdang.reader.utils.ConfigManager;
import com.dangdang.reader.utils.DangdangFileManager;
import com.dangdang.zframework.log.LogM;
import com.dangdang.zframework.utils.DRUiUtility;
import com.dangdang.zframework.utils.MemoryStatus;
import com.dangdang.zframework.utils.NetUtil;
import com.dangdang.zframework.utils.UiUtil;
import com.mx.mxbase.constant.APPLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReadMainActivity extends BaseGroupActivity implements
        OnScrollStatusListener {

    private final static int Index_MDN = 0;
    private final static int Index_READ = 1;
    // private final static int Index_COMMENT = 2;

    // private View mReflowBtn;
    private ReaderScrollView mContainer;
    private ScrollEvent mCurrentScroll;

    private LocalActivityManager mActManager;
    private Map<Integer, Activity> mSubActivityMap;
    private List<Module> moduleList;
    private long mStartTime = 0;// 开始阅读时间
    private long mPauseTime = 0;// 暂停阅读时间
    private BaseReadInfo mReadInfo;
    // private ShelfService mShelfService;
    private ShelfBook mShelfInfo;

    private DDFile mDDFile;
    private boolean isPdfReflow = false;
    private boolean isSwitchPdf = false;
    private String mPdfPwd;

    private DDClickHandle mClickHandle;

    protected ReadTimes readTimes;

    @Override
    public boolean isTransparentSystemBar() {
        return false;
    }

    // @SuppressLint("UseSparseArrays")
    @Override
    public void onCreateImpl(Bundle savedInstanceState) {
//        overridePendingTransition(R.anim.book_review_activity_in, R.anim.book_review_group_activity_out);
        printLog("wyz ReadMainActivity onCreate start ");
        initPubParams(getIntent());

        mainHandler = new MyHandler(this);
        mainHandler.sendEmptyMessageDelayed(MSG_ERROR_EXIT, ERROR_EXIT_TIME_THRESHOLD);

        ReadConfig config = ReadConfig.getConfig();
        config.initContext(getApplication());
        config.setIsReadOpenError(true);
        config.setIsReadNormalExit(false);
        config.setReadNotNormalExitBookId(mDefaultId);
        initFullScreenStatus(config.isFullScreen());

        setContentView(R.layout.read_main);

        mClickHandle = new DDClickHandle(getApplicationContext());
        mContainer = (ReaderScrollView) findViewById(R.id.main_workspace);
        // mReflowBtn = findViewById(R.id.pdf_reflow_btn);

        mActManager = getLocalActivityManager();
        mContainer.setOnScrollCompleteLinstenner(this);
        // mShelfService = ShelfService.getInstance(this);

        DRUiUtility ui = DRUiUtility.getUiUtilityInstance();
        ui.setContext(this);// TODO remove？
        ui.DontKeepContext(this);
        config.initValue();

        mSubActivityMap = new HashMap<Integer, Activity>();

        clearClipBoard();

        mShelfInfo = getShelfBookInfo();
        initApp(mShelfInfo);
        initModule();
        addModule();
        printLog("wyz ReadMainActivity onCreate end ");

        checkAutoDownload();
    }
    protected void initApp(ShelfBook shelfBook) {
        try {
            mDDFile = createDDFile();
            {
                mReadInfo = initReadInfo(getIntent(), shelfBook);
                ReaderAppImpl readerApp = ReaderAppImpl.getApp();
                readerApp.clear();
                readerApp.prepareInit(mReadInfo, mDDFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showToast(R.string.open_book_failed);
            finish();
        }
    }

    protected ShelfBook getShelfBookInfo() {
        APPLog.e("ReadMainActivity mdefaultId:"+(mDefaultId));
        ShelfBookService shelfService = ShelfBookService.getInstance(this);
        ShelfBook shelfBook = shelfService.getShelfBookById(mDefaultId);
        return shelfBook;
    }

    protected DDFile createDDFile() throws FileFormatException {
        return new DDFile(mBookFile);
    }

    public boolean isPdf() {
        return DDFile.isPdf(mDDFile);
    }

    public boolean isComicsNoOpen() {
        return mReadInfo.getCategory() == BaseReadInfo.CATEGORY_COMICS;
    }

    public boolean isComics() {
        EpubWrap drWrap = new EpubWrap();
        int category = drWrap.getEpubBookCategory(mDDFile.getFilePath());

        return category == DDFile.CATEGORY_COMICS;
    }

    public boolean isPartComics() {

        return mBookType == BaseJniWarp.BOOKTYPE_DD_DRM_COMICS;
    }

    public boolean isPart() {
        return DDFile.isPart(mDDFile);
    }

    public void initFullScreenStatus(boolean isFullScreen) {
        DRUiUtility.setActivityFullScreenStatus(this, isFullScreen);
    }

    private void checkAutoDownload() {
        final Context context = getApplicationContext();
        if (!NetUtil.isWifiConnected(context)) {
            return;
        }
        if (MemoryStatus.externalMemoryAvailable()) {
            if (!MemoryStatus.hasAvailable(MemoryStatus.MIN_SPACE * 2,
                    1024 * 1024 * 1)) {
                showToast(R.string.externalmemory_few);
                return;
            }
        } else {
            showToast(R.string.string_mounted_error);
        }

        startDownloadFontService();
    }

    public void startDownloadFontService() {
        if (!ReadConfig.getConfig().isFontAutoDownload())
            return;
        final Context context = getApplicationContext();
        if (!FontListHandle.getHandle(context).isFreeFontDownFinish()) {
            Intent fontService = new Intent(this, FontDownloadService.class);
            startService(fontService);
        }
    }

    public void processReflow() {

        // 显示loading页

        BaseGlobalApplication globalApp = getGlobalApp();
        if (globalApp.isCanExit()) {
            printLog(" sync exit ");
            performReflow();
            return;
        }

        globalApp.requestAbort(new IAbortParserListener() {
            @Override
            public void alreadyAbort() {
                printLog(" asyc exit ");
                mainHandler.sendEmptyMessage(MSG_SWITCH_REFLOWSTATUS);
            }
        });

    }

    protected void performReflow() {
        if (isPdfAndNotReflow()) {
            isSwitchPdf = true;
        }
        setPdfReflow(!isPdfReflow());
        replaceModule();
    }

    /**
     * 如果是内置的(包含has_key_)
     *
     * @return
     */
    private String mDefaultId;
    private String mInternetBookCover;
    private String mBookDir;
    private String mBookFile;
    private String mBookName;
    private String mAuthorName;
    private String mBookDesc;
    private byte[] mBookCertKey;
    private int mBookType = BaseJniWarp.BOOKTYPE_DD_DRM_EPUB;
    private String progress;

    private void initPubParams(Intent intent) {
        // TODO
        mDefaultId = intent.getStringExtra(IntentK.ProductId);
        mInternetBookCover = intent.getStringExtra(IntentK.Cover);
        mBookDir = intent.getStringExtra(IntentK.BookDir);
        mBookFile = intent.getStringExtra(IntentK.BookEpub); // FileUtil.getTxtPath();//
        mBookName = intent.getStringExtra(IntentK.BookName);
        progress = intent.getStringExtra(IntentK.ReadProgress);
        if (null==progress)progress="";
        mBookDesc = intent.getStringExtra(IntentK.Desc);
        mAuthorName = intent.getStringExtra(IntentK.Author);
        mBookType = intent.getIntExtra(IntentK.BookType,
                BaseJniWarp.BOOKTYPE_DD_DRM_EPUB);
        if (intent.getBooleanExtra("exist_open",false)){
            mBookCertKey = DrmWrapUtil.getPartBookCertKey(intent.getStringExtra(IntentK.BookCertKey));
        }else{
            mBookCertKey = intent.getByteArrayExtra(IntentK.BookCertKey);
        }
        readTimes = new ReadTimes();
        readTimes.setProductId(mDefaultId);
        readTimes.setStartTime(System.currentTimeMillis());
        if ((mBookName == null || mBookName.isEmpty()) && mBookType == BaseJniWarp.BOOKTYPE_DD_DRM_EPUB) {
            EpubWrap epubWrap = new EpubWrap();
            mBookName = epubWrap.getEpubBookCaption(mBookFile, BaseJniWarp.BOOKTYPE_DD_DRM_EPUB);
        }
    }

    private BaseReadInfo initReadInfo(Intent intent, ShelfBook shelfInfo) {

        Intent rmIntent = intent;// getIntent();
        String productId = mDefaultId;
        String mprogress = progress;
        String internetBookCover = mInternetBookCover;
        String bookDir = mBookDir;
        String bookFile = mBookFile; // FileUtil.getTxtPath();//
        String bookName = mBookName;
        String bookDesc = mBookDesc;
        String authorName = mAuthorName;
        int bookType = mBookType;
        byte[] bookCertKey = mBookCertKey;
        boolean isBought = rmIntent.getBooleanExtra(IntentK.IsBought, false);

        printLog("startRead productId = " + productId + ", isBought = "
                + isBought);

//		 ShelfService shelfService = ShelfService.getInstance(this); ShelfBook
//		 shelfBook = shelfService.getShelfBookByBookId(getDefaultPid());

        ReadInfo readInfo = null;
        if (isPart()) {
            readInfo = createPartReadInfo(intent, shelfInfo);
        } else {
            readInfo = new ReadInfo();
        }

        readInfo.setDefaultPid(productId);
        readInfo.setInternetBookCover(internetBookCover);
        readInfo.setBought(isBought);
        readInfo.setBookName(bookName);
        readInfo.setBookDesc(bookDesc);
        readInfo.setAuthorName(authorName);
        readInfo.setBookFile(bookFile);
        readInfo.setEBookType(bookType);
        readInfo.setBookCertKey(bookCertKey);
//        readInfo.setProgressInfo(mprogress);
        // readInfo.bookFile = readInfo.isPreSet() ? FileUtil.getTxtPath() :
        // bookFile;
        readInfo.setBookDir(bookDir);// FileUtil.getEpubDir();
        ReadConfig.getConfig().setReadProgress(this,mprogress);
        if (!readInfo.convertData(shelfInfo, isPdf())) {
            // showToast(R.string.filecontent_change);
        }
        return readInfo;
    }

    /**
     * 分章阅读的信息,初始化其独有的信息
     *
     * @param intent
     * @param shelfInfo
     * @return
     */
    protected PartReadInfo createPartReadInfo(Intent intent, ShelfBook shelfInfo) {
        PartReadInfo info = new PartReadInfo();
        if (shelfInfo != null) {
            // 书架已经有此书
            info.setIsShelf(true);
            info.setSaleId(shelfInfo.getSaleId());
//            info.setIndexOrder(shelfInfo.getServerLastIndexOrder());
            info.setIsFollow(shelfInfo.isFollow());
            info.setIsFull(shelfInfo.getBookType() == ShelfBook.BookType.BOOK_TYPE_IS_FULL_YES);
            info.setIsAutoBuy(shelfInfo.isPreload());
        } else {
            info.setIsShelf(false);
            info.setSaleId(intent.getStringExtra(IntentK.SaleId));
            info.setBookAuthor(intent.getStringExtra(IntentK.Author));
            info.setBookDesc(intent.getStringExtra(IntentK.Desc));
            info.setBookCategories(intent.getStringExtra(IntentK.Category));
            info.setIsFollow(intent.getBooleanExtra(IntentK.IsFollow, false));
            info.setIsFull(intent.getBooleanExtra(IntentK.IsFull, false));
            info.setIsSupportFull(intent.getBooleanExtra(IntentK.IsSurpportFull, false));
            info.setIsAutoBuy(intent.getBooleanExtra(IntentK.IsAutoBuy, false));
        }
        //对于原创的书，最后一章索引，单品已是最大章节，书架可能是旧的
        info.setIndexOrder(intent.getIntExtra(IntentK.IndexOrder, 0));
        info.setTargetChapterId(intent.getIntExtra(IntentK.TargetChapterId, -1));
        return info;
    }

    protected String getSysFontPath() {
        return ReadConfig.PDF_FONT_PATH;
    }

    private String getPdfResPath() {
        ConfigManager configManager = new ConfigManager(getApplicationContext());
        String path = configManager.getPdfResourceUrl();
        if (TextUtils.isEmpty(path)) {
            // sendMsg2Toast(R.string.can_not_find_pdf_resources);
        }
        return path;
    }

    private void clearClipBoard() {
        try {
            ClipboardManager clip = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            clip.setText("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Class<? extends BaseReadActivity> readClass = ReadActivity.class;

    // private SharePopupWindow mSharePopupWindow;

    private void initModule() {
        moduleList = new ArrayList<Module>();
        readClass = initReadClass();

        Module dmnModule = new Module();
        dmnModule.mIndex = Index_MDN;
        dmnModule.mClass = DirectoryMarkNoteActivity.class;

        Module readModule = new Module();
        readModule.mIndex = Index_READ;// TODO restore
        readModule.mClass = readClass;// ReadActivity.class;

		/*
		 * Module commentModule = new Module(); commentModule.mIndex =
		 * Index_COMMENT;//TODO restore commentModule.mClass =
		 * BookReadEndActivity.class;
		 */

        moduleList.add(dmnModule);
        moduleList.add(readModule);
        // moduleList.add(commentModule);
    }

    protected Class<? extends BaseReadActivity> initReadClass() {
        if (isPart())
            return PartReadActivity.class;
        return ReadActivity.class;
        // return isPdf() ? (isPdfReflow() ? PdfReflowReadActivity.class
        // : PdfReadActivity.class) : ReadActivity.class;
    }

    private void addModule() {
        for (int i = 0, len = moduleList.size(); i < len; i++) {

            Module module = moduleList.get(i);
            Intent it = module.getIntent(getApplicationContext());
            it.putExtras(getIntent());
            it.putExtra(IntentK.BookName, getBookName());
            if (Build.VERSION.SDK_INT < 11) {
                it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            }
            ScrollEvent sEvent = new ScrollEvent(module.mIndex);
            String id = getActId(sEvent.curScreen);
            View view = (mActManager.startActivity(id, it)).getDecorView();
            mContainer.addView(view);

            Activity act = mActManager.getActivity(id);
            mSubActivityMap.put(sEvent.curScreen, act);
        }
        mContainer.setToScreen(findIndex(readClass));
    }

    private void destoryModule() {
        for (int i = 0, len = moduleList.size(); i < len; i++) {
            Module module = moduleList.get(i);
            ScrollEvent sEvent = new ScrollEvent(module.mIndex);
            String id = getActId(sEvent.curScreen);
            mActManager.destroyActivity(id, true);
        }
        mSubActivityMap.clear();
        moduleList.clear();
        mContainer.removeAllViews();
        ReaderAppImpl.getApp().clear();
        mReadInfo = null;
        mShelfInfo = null;
        mDDFile = null;
        isPdfReflow = false;
        isSwitchPdf = false;
        mPdfPwd = "";

    }

    private String getActId(int id) {
        return "actid:" + id;
    }

    public void replaceModule() {

        printLog(" replaceModule start ");
        /**
         * 是否终止排版后再destroy
         */
        Activity dmn = mActManager.getActivity(getActId(Index_MDN));
        Activity ra = mActManager.getActivity(getActId(Index_READ));

        mActManager.destroyActivity(getActId(Index_MDN), true);
        mActManager.destroyActivity(getActId(Index_READ), true);

        clearModule();
        mContainer.removeAllViewsInLayout();

        initApp(getShelfBookInfo());
        initModule();
        addModule();

        printLog(" replaceModule end ");
    }

    @Override
    public void onScrollComplete(final ScrollEvent e) {
        mCurrentScroll = e;
        Activity currAct = getCurrentActivity(e);
        printLog(" onScrollComplete " + e.curScreen + " , currAct= " + currAct);
        OnScrollStatusListener l = (OnScrollStatusListener) currAct;
        l.onScrollComplete(e);

        if (currAct instanceof DirectoryMarkNoteActivity) {
            BaseReadActivity readAct = getReadActivity();
            if (readAct instanceof ReadActivity) {
                ((ReadActivity) readAct).setShowDirGuide(true);
            } else {
                printLog(" pdf ???????? ");// TODO ?
            }
        }

    }

    @Override
    public void onScrollStart(ScrollEvent e) {
        e.curScreen = e.curScreen == 0 ? 1 : 0; // temp
        Activity currAct = getCurrentActivity(e);
        printLog(" onScrollStart " + e.curScreen + " , currAct= " + currAct);
        OnScrollStatusListener l = (OnScrollStatusListener) currAct;
        l.onScrollStart(e);
    }

	/*
	 * private void notifyRestoreDir() { if(mCurrentScroll != null &&
	 * mCurrentScroll.curScreen == Index_MDN){ Intent intent = new Intent();
	 * intent.setAction(Constant.ACTION_RESTORE_DIR); sendBroadcast(intent); } }
	 */

    @Override
    public boolean isSelfProcessTouch() {
        Activity currAct = getCurrentActivity(mCurrentScroll);
        OnScrollStatusListener l = (OnScrollStatusListener) currAct;
        // printLog(" isSelfProcessTouch curScreen = " +
        // mCurrentScroll.curScreen + ", isSelfProcessTouch = " +
        // l.isSelfProcessTouch());
        APPLog.e("isSelfProcessTouch",System.currentTimeMillis());
        return l.isSelfProcessTouch();
    }

    public BaseReadActivity getReadActivity() {
        return (BaseReadActivity) mSubActivityMap.get(Index_READ);
    }

    private Activity getCurrentActivity(final ScrollEvent e) {
        Activity currAct = mSubActivityMap.get(e.curScreen);
        return currAct;
    }

    public void snapToScreen(int screen) {
        mContainer.snapToScreen(screen);
    }

    public void snapToReadScreen() {
        snapToScreen(readClass);
    }

    @SuppressWarnings("rawtypes")
    public void snapToScreen(Class clazz) {
        final int screen = findIndex(clazz);
        mContainer.snapToScreen(screen, true);
    }

    @SuppressWarnings("rawtypes")
    private int findIndex(Class clazz) {
        int index = 0;
        if (moduleList != null) {
            for (Module m : moduleList) {
                if (m.mClass == clazz) {
                    index = m.mIndex;
                    break;
                }
            }
        }
        return index;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        try {
            Activity currentAct = getCurrentActivity(mCurrentScroll);
             LogM.i(getClass().getSimpleName(), " currentAct " + currentAct);
            if (currentAct == null)
                return false;
            return currentAct.dispatchKeyEvent(event);
        } catch (Throwable e) {
            e.printStackTrace();
            return false;
        }
    }

    public void printLog(String msg) {
        LogM.i(getClass().getSimpleName(), msg);
    }

    public void printLogE(String log) {
        LogM.e(getClass().getSimpleName(), log);
    }

    protected void onResume() {
        super.onResume();
        // UmengStatistics.onResume(this);//根据友盟官方说明，这属于重复统计
        mStartTime = System.currentTimeMillis();
        // performCurrentActivityOnResume();
        // 开始阅读
    }

    protected void onDestroy() {
        printLog("wyz ReadMainActivity onDestory");
        try {
            clearModule();
            mContainer.removeAllViews();
            mContainer = null;
            mActManager.removeAllActivities();
            mActManager = null;
        } catch (Exception e) {
            LogM.e(e.toString());
        }
        super.onDestroy();
    }

    protected void clearModule() {
        moduleList.clear();
        mSubActivityMap.clear();
    }

    private void performCurrentActivityOnResume() {
        BaseReadActivity baseReadAct = (BaseReadActivity) getCurrentActivity(mCurrentScroll);
        if (baseReadAct != null) {
            baseReadAct.onResume();
        }
    }

    public void onPause() {
        super.onPause();
        // UmengStatistics.onPause(this);//根据友盟官方说明，这属于重复统计

        ShelfBookService shelfService = ShelfBookService.getInstance(this);
        ShelfBook info = shelfService.getShelfBookById(getDefaultPid());
        if (info == null)
            return;
        String readTimes = info.getTotalTime();
        mPauseTime = System.currentTimeMillis();
        long mMillsecond = mPauseTime - mStartTime;
        setReadTimes(readTimes, mMillsecond, shelfService);

        // performCurrentActivityOnPause();

    }

    private void performCurrentActivityOnPause() {
        BaseReadActivity baseReadAct = (BaseReadActivity) getCurrentActivity(mCurrentScroll);
        if (baseReadAct != null) {
            baseReadAct.onPause();
        }
    }

    private void setReadTimes(String readTimes, long mMillsecond,
                              ShelfBookService shelfService) {

        JSONObject jsonObj = null;
        long timeLong = 0;
        long startReadTime = 0;
        long endTime = 0;
        if (readTimes != null) {
            try {
                jsonObj = new JSONObject(readTimes);
                timeLong = jsonObj.optLong(ReadInfo.JSONK_READ_PAUSE_TIME, 0);
                startReadTime = jsonObj.optLong(ReadInfo.JSONK_READ_START_TIME, 0);
                endTime = jsonObj.optLong(ReadInfo.JSONK_READ_END_TIME, 0);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                timeLong = 0;
                startReadTime = 0;
                endTime = 0;
            }
        } else {
            timeLong = 0;
        }

        if (mMillsecond <= 0)
            mMillsecond = 0;

        mMillsecond = timeLong + mMillsecond;// 累计时长
        String readTime = mReadInfo.makeReadTimeInfo(startReadTime,
                mMillsecond, endTime);

//        shelfService.updateBookReadTime(getDefaultPid(), readTime);
        shelfService.updateBookReadTime(getDefaultPid(), readTime, 0);
        if (mShelfInfo != null)
            mShelfInfo.setTotalTime(readTime);
    }

    private Intent mNewIntent;


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        printLog(" onNewIntent ");
        if (mCurrentScroll == null || mCurrentScroll.curScreen != Index_READ) {
            mContainer.setToScreen(findIndex(readClass));
        }
        if (isSameBook(intent)) {
            printLog(" isSameBook=true ");
            return;
        }
        saveReadtimes();
        setIntent(intent);
        clearClipBoard();


        try {
            if (isPart()) {
                ReadBookUtil.addBook2Shelf(this, (PartReadInfo) mReadInfo);
            }
            destoryModule();
            initPubParams(intent);

            mShelfInfo = getShelfBookInfo();

            initApp(mShelfInfo);
            initModule();
            addModule();
            printLog(" onNewIntent end ");
        } catch (Exception e) {
            e.printStackTrace();
            showToast(R.string.open_book_failed);
            finish();
        }
        // readerApp.setReadInfo(mReadInfo);

//        notifyChildren(intent);
    }

	/*
	 * private boolean isFromTtsNotification(Intent intent) { return intent ==
	 * null ? false : intent.getBooleanExtra(
	 * DDTTSNotificationManager.FORM_DDTTS_NOTIFICATION, false); }
	 */

    protected boolean isSameBook(Intent intent) {
        boolean sameBook = false;
        try {
            String productId = intent.getStringExtra(IntentK.ProductId);
            boolean isBought = intent.getBooleanExtra(IntentK.IsBought, false);
            sameBook = mReadInfo != null && getDefaultPid().equals(productId)
                    && mReadInfo.isBought() == isBought;
        } catch (Exception e) {
            e.printStackTrace();
            sameBook = false;
        }
        return sameBook;
    }

    private void notifyChildren(Intent intent) {

        BaseReadActivity dmnAct = (BaseReadActivity) mSubActivityMap
                .get(Index_MDN);
        BaseReadActivity readAct = (BaseReadActivity) mSubActivityMap
                .get(Index_READ);
        // BookReadEndActivity readEndAct = (BookReadEndActivity)
        // mSubActivityMap.get(Index_COMMENT);

        dmnAct.onNewIntent(intent);
        readAct.onNewIntent(intent);
        // readEndAct.onNewIntent(intent);
    }

    private void showToast(int resid) {
        UiUtil.showToast(getApplicationContext(), resid);
    }

    public boolean isCurrentRead() {
        boolean currentRead = false;
        if (mCurrentScroll != null && mCurrentScroll.curScreen == Index_READ) {
            currentRead = true;
        }
        return currentRead;
    }

    public int getEBookType() {
        return mBookType;
    }

    public boolean isDangEpub() {
        return getEBookType() == BaseJniWarp.BOOKTYPE_DD_DRM_EPUB || getEBookType() == BaseJniWarp.BOOKTYPE_DD_DRM_HTML;
    }

    public static class Module {

        public int mIndex;
        @SuppressWarnings("rawtypes")
        public Class mClass;

        public Intent getIntent(Context context) {

            if (mClass == null) {
                throw new NullPointerException(" [ param mClass cannot null ] ");
            }
            Intent intent = new Intent();
            intent.setClass(context, mClass);

            return intent;
        }

    }

    public void shareBook() {
		/*
		 * if (mSharePopupWindow == null) { mSharePopupWindow = new
		 * SharePopupWindow(this, mContainer); }
		 * 
		 * mSharePopupWindow.setShareData(getDDShareData(),
		 * getDDStatisticsData()); mSharePopupWindow.showOrHideShareMenu();
		 */
    }

    public void exportBookNote(String strContent) {
		/*
		 * if (mSharePopupWindow == null) { mSharePopupWindow = new
		 * SharePopupWindow(this, mContainer); }
		 *
		 * mSharePopupWindow.setShareData(getDDShareData(),
		 * getDDStatisticsData()); mSharePopupWindow.showOrHideShareMenu();
		 */
    }

//    private DDShareData getDDShareData() {
//        DDShareData shareData = new DDShareData();
//
//        String desc = null;
//        String author = null;
//        try {
//            if (mReadInfo instanceof ReadInfo) {
//                ReadInfo readInfo = (ReadInfo) mReadInfo;
//                String bookCover = ImageConfig.getBookCoverBySize(readInfo.getInternetBookCover(), ImageConfig.IMAGE_SIZE_CC);
//                shareData.setPicUrl(bookCover);
//                desc = readInfo.getBookDesc();
//                author = readInfo.getAuthorName();
//            } else {
//                printLog(" not ReadInfo ");
//            }
//        } catch (Exception e) {
//        }
//
//        shareData.setDesc(desc);
//        shareData.setAuthor(author);
//        shareData
//                .setTitle(getString(R.string.share_book_title) + getBookName());
//        shareData.setBookName(getBookName());
//        shareData.setTargetUrl(DDShareData.DDREADER_BOOK_DETAIL_LINK);
//        DDShareParams params = new DDShareParams();
//        params.setSaleId(getProductId());
//        params.setMediaId(getProductId());
//        if (getReadInfo() instanceof PartReadInfo) {
//            PartReadInfo partReadInfo = (PartReadInfo) getReadInfo();
//            params.setSaleId(partReadInfo.getSaleId());
//            shareData.setMediaType(1);
//        } else
//            shareData.setMediaType(2);
//        shareData.setParams(JSON.toJSONString(params));
//        return shareData;
//    }

//    private DDStatisticsData getDDStatisticsData() {
//        DDStatisticsData statisticsData = new DDStatisticsData(
//                DDShareData.SHARE_TYPE_BOOK);
//        statisticsData.setBookName(getBookName());
//        statisticsData.setProductId(getProductId());
//
//        return statisticsData;
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
		/*
		 * if (requestCode == TencentWeiBoInfo.TENCENT_RESULT_CODE &&
		 * mSharePopupWindow != null) {
		 * mSharePopupWindow.mTencentWeiBoInfo.onActivityResult(requestCode,
		 * resultCode, data); }
		 */
    }

    protected String getProductId() {
        return mReadInfo.getProductId();
    }

    private String getBookName() {
        return mReadInfo.getBookName();
    }

    protected String getDefaultPid() {
        return mReadInfo.getDefaultPid();
    }

    protected String getBookFile() {
        return mReadInfo.getBookFile();
    }

    public boolean isPdfReflow() {
        return isPdfReflow;
    }

    public void setPdfReflow(boolean isPdfReflow) {
        this.isPdfReflow = isPdfReflow;
    }

    public boolean isPdfAndNotReflow() {
        return isPdf() && !isPdfReflow();
    }

    public boolean isSwitchPdf() {
        return isSwitchPdf;
    }

    public DDFile getDDFile() {
        return mDDFile;
    }

    public BaseGlobalApplication getGlobalApp() {
        BaseGlobalApplication app = null;
        {
            app = ReaderAppImpl.getApp();
        }
        return app;
    }

    public Book getBook() {
        return (Book) getGlobalApp().getBook();
    }

    public BaseReadInfo getReadInfo() {
        return getGlobalApp().getReadInfo();
    }

    public String getPdfPwd() {
        return mPdfPwd;
    }

    public void rememberPdfPwd(String pwd) {
        mPdfPwd = pwd;
    }

    public void setIsErrorExit(boolean isErrorExit) {
        mIsErrorExit = isErrorExit;
        ReadConfig.getConfig().setIsReadOpenError(isErrorExit);
    }


    private final static int MSG_SWITCH_REFLOWSTATUS = 1;
    private final static int MSG_ERROR_EXIT = 2;
    private final static int ERROR_EXIT_TIME_THRESHOLD = 3*1000;    // 异常退出界定阀值
    private boolean mIsErrorExit = true;                            // 是否是异常退出

    protected Handler mainHandler;

    private static class MyHandler extends Handler {
        private final WeakReference<ReadMainActivity> mFragmentView;


        MyHandler(ReadMainActivity view) {
            this.mFragmentView = new WeakReference<ReadMainActivity>(view);
        }

        @Override
        public void handleMessage(Message msg) {
            ReadMainActivity service = mFragmentView.get();
            if (service != null) {
                super.handleMessage(msg);
                try {
                    switch (msg.what) {
                        case MSG_SWITCH_REFLOWSTATUS:
                            service.performReflow();
                            break;
                        case MSG_ERROR_EXIT:
                            service.setIsErrorExit(false);
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    protected void onStart() {
        super.onStart();
        if (mClickHandle != null) {
            mClickHandle.start();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mClickHandle != null) {
            mClickHandle.stop(this, "");
        }
    }

    @Override
    protected void onDestroyImpl() {
        saveReadtimes();

        ReadConfig.getConfig().setIsReadOpenError(mIsErrorExit);
    }

    private void saveReadtimes() {
        if (readTimes == null)
            return;
        String pid = readTimes.getProductId();
        if (DangdangFileManager.isImportBook(pid))
            return;
        readTimes.setEndTime(System.currentTimeMillis());
        new ReadTimesService().addReadTimes(readTimes);
        readTimes = null;
    }
//    @Override
//    public void finish() {
//        super.finish();
//        overridePendingTransition(R.anim.book_review_group_activity_in, R.anim.book_review_activity_out);
//    }
//
//    @Override
//    protected void onUserLeaveHint() {
//        super.onUserLeaveHint();
//        APPLog.e("关闭界面");
//        this.finish();
//    }
}