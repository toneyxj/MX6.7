package com.dangdang.reader;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.support.multidex.MultiDex;
import android.text.TextUtils;

import com.dangdang.reader.db.service.ShelfBookService;
import com.dangdang.reader.dread.jni.BaseJniWarp;
import com.dangdang.reader.dread.jni.DrmWarp;
import com.dangdang.reader.personal.domain.ShelfBook;
import com.dangdang.reader.store.StoreUtil;
import com.dangdang.reader.utils.AccountManager;
import com.dangdang.reader.utils.ConfigManager;
import com.dangdang.reader.utils.DROSUtility;
import com.dangdang.reader.utils.DangdangConfig;
import com.dangdang.reader.utils.DangdangFileManager;
import com.dangdang.reader.utils.FirstGuideManager;
import com.dangdang.reader.utils.ForegroundV2;
import com.dangdang.zframework.log.LogM;
import com.dangdang.zframework.network.image.ImageManager;
import com.dangdang.zframework.plugin.AppUtil;
import com.dangdang.zframework.utils.DangDangParams;
import com.dangdang.zframework.utils.NetUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DDApplication extends DDBaseApplication {

	public static final String EPUB_CSS = "style.css";
	public static final String EPUB_CSS_VERSION = "1.0";
	
	private static DDApplication mApp;
	private List<ShelfBook> mImportBookList;
    private boolean mIsMobileNetAllowDownload;				// 是否非WiFi情况下允许下载
	private String mEpubCSsPath = null;
	protected Typeface mTypeface;
	private Bitmap bitmap;
	private boolean mIsShowMarketComment = false;//是否显示市场评论
	private boolean mIsRefreshBar = false;//是否刷新吧
	private boolean mIsRefreshChannel = false;//是否刷新频道
	private boolean mIsRefreshMain = false;//是否刷新首页
	
	// 存放过期书籍的<bookid, String>
	private Set<String> mSet = new HashSet<String>();
	
    //private RefWatcher mRefWatcher;
	
	private boolean mIsUpdateHead = false;

	public int mBaiduPushBindRetryCount = 0;// 百度绑定失败重试次数
	public int mBaiduPushDataSendRetryCount = 0;// 发送百度绑定信息重试次数

	private ForegroundV2.Listener mForegroundListener;
	
	static {
		System.loadLibrary("ddlayoutkit");
	}
	
	public boolean isUpdateHead() {
		return mApp.mIsUpdateHead;
	}

	public void setIsUpdateHead(boolean mIsUpdateHead) {
		mApp.mIsUpdateHead = mIsUpdateHead;
	}

	@Override
	public void onCreateIpml() {
        if(mApp == null)
            mApp = this;

//        CrashHandler crashHandler = CrashHandler.getInstance();
		// 注册crashHandler
//		crashHandler.init(getApplicationContext());
		
        initParams();
		initApp();

		// leak cacary
//		LeakCanary.install(this);
		//start reminder alarm
	}
	
	private void initParams(){
		DangdangFileManager.APP_ROOT_PATH = "/data/data/" + this.getPackageName();
		DangdangFileManager.APP_START_IMG_PATH = DangdangFileManager.APP_ROOT_PATH + "/dd_startpage";
	}

	public static DDApplication getApplication(){
		return mApp;
	}
	
	public boolean isShowMarketComment() {
		return mApp.mIsShowMarketComment;
	}

	public void setShowMarketComment(boolean isRefresh) {
		mApp.mIsShowMarketComment = isRefresh;
	}
	
	public boolean isRefreshBar() {
		return mApp.mIsRefreshBar;
	}
	
	public void setRefreshBar(boolean isRefresh) {
		mApp.mIsRefreshBar = isRefresh;
	}
	
	public boolean isRefreshChannel() {
		return mApp.mIsRefreshChannel;
	}
	
	public void setRefreshChannel(boolean isRefresh) {
		mApp.mIsRefreshChannel = isRefresh;
	}
	
	public boolean isRefreshMain() {
		return mApp.mIsRefreshMain;
	}
	
	public void setRefreshMain(boolean isRefresh) {
		mApp.mIsRefreshMain = isRefresh;
	}
	
	private void initApp() {

		DangdangFileManager.getFileManagerInstance().setContext(getApplicationContext());

		final ConfigManager cm = new ConfigManager(this);
		DrmWarp drmWarp = DrmWarp.getInstance();
		drmWarp.init(cm.getPublicKeyPath(), cm.getPrivateKeyPath());

		drmWarp.setBasePackageName(cm.getPackageName(), !DangdangConfig.isOnLineOrStaggingEnv());
		/**不能去掉**/
        initImageManager();
//        ReaderExpressionUtils.init();


		/**
		 * 初始化公共参数
		 */
        DangdangConfig.ParamsType.initPackageName(getPackageName());
		DangDangParams.setPublicParams(this, getPublicParams());
		
//		ShareSDK.init(this);
        /**
         * log输出
         */
		if (DangdangConfig.mLogON) {
			//设置Log开关
			LogM.initLogLevel(true, true, true, true, true);
		} else {
			LogM.initLogLevel(false, false, false, false, false);
		}
//		UmengStatistics.openStatis=!DangdangConfig.mLogON;//debug版本不统计友盟
//		UmengStatistics.openStatis = true;

		/**
		 * 支付sdk初始化
		 */
		// 支付sdk初始化
//		String userId = "";
//		AccountManager am = new AccountManager(this);
//		if (am.isLogin()) {
//			userId = am.getUserId();
//		} else {
//			userId = "default_user";
//		}
//		Constance.isOnLine=DangdangConfig.isOnLineEnv();
//		DDPayApp.init(this,
//				DangdangConfig.isOnLineEnv(),
//				DangdangConfig.SERVER_MEDIA_API2_URL,
//				DangDangParams.getChannelId(),
//				am.getToken(),
//				userId);

        setTTF();

	}

    public void setTTF() {
        String path = DangdangFileManager.getPreSetTTF();
        if(!TextUtils.isEmpty(path) && new File(path).exists()){
            AppUtil.getInstance(this).setTypeface(path);
        }
    }

    /**
     * 初始化图片家在类，配置缓存策略及图片尺寸配置
     */
    private void initImageManager() {
        //初始化context
        ImageManager.getInstance().init(this, DangdangFileManager.getImageCacheDir());
    }
	public boolean isKeyExist(String key) {
		if(TextUtils.isEmpty(key))
			return false;
		return mSet.contains(key);
	}

	public void addValueToSet(String key) {
		mSet.add(key);
	}

	public void removeValueFromSet(String key) {
		mSet.remove(key);
	}
	
	public void clearSet() {
		mSet.clear();
	}
	
	public void updateToken(String token){
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(DangDangParams.TOKEN, token);
		DangDangParams.setPublicParams(getApplicationContext(), map);
	}

	private void printLog(String msg){
		LogM.e(getClass().getSimpleName(), msg);
	}
	
	public void exitApp() {
    	ShelfBookService.getInstance(this).updateOverDue(mSet);
    	ImageLoader.getInstance().clearMemoryCache();
		StoreUtil.getInstance().release(true);
    	FirstGuideManager.getInstance(this).release();
    	mIsShowMarketComment = false;
        BaseJniWarp.destoryData();
//		QLogService.stopService(this);
	}

	private HashMap<String, String> getPublicParams() {
		HashMap<String, String> map = new HashMap<String, String>();
		String token = new AccountManager(this).getToken();
		map.put(DangDangParams.TOKEN, token);
		map.put(com.dangdang.zframework.utils.DangdangConfig.DEVICE_TYPE, DangdangConfig.ParamsType.getDeviceType());
		map.put(com.dangdang.zframework.utils.DangdangConfig.PLATFORM_SOURCE, DangdangConfig.ParamsType.getPlatformSource());
		
		return map;
	}

	public List<ShelfBook> getmImportBookList() {
		return mImportBookList;
	}

	public void setmImportBookList(List<ShelfBook> mImportBookList) {
		this.mImportBookList = mImportBookList;
	}
	
	
	public String getEpubCss() {
		if (TextUtils.isEmpty(mEpubCSsPath)) {
			String css = DROSUtility.getEpubCssPath();
			mEpubCSsPath = css + EPUB_CSS;
		}
		return mEpubCSsPath;
	}


	public void setEpubCss(String css) {
		mEpubCSsPath = css;
	}
	
    public boolean isMobileNetAllowDownload() {
    	if(NetUtil.isWifiConnected(this))
    		return true;
        return mIsMobileNetAllowDownload;
    }

    public void setIsMobileNetAllowDownload(boolean isMobileNetAllowDownload) {
        this.mIsMobileNetAllowDownload = isMobileNetAllowDownload;
    }
    
    public Bitmap getBitmap(){
    	return bitmap;
    }
    
    public void setBitmap(Bitmap bm){
    	bitmap = bm;
    }
    
    @Override
	public void sendBroadcast(Intent intent){
		if(intent == null)
			return;
		intent.setPackage(this.getPackageName());
		super.sendBroadcast(intent);
	}
    
	public void sendBroadcast(Intent intent, boolean bo){
		if(intent == null)
			return;
		super.sendBroadcast(intent);
	}
    
    private boolean mhaveActivity;
	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		MultiDex.install(this);
	}
    public boolean getHaveActivity() {
        return mApp.mhaveActivity;
    }

    public void sethaveActivity(boolean mIsHaveActivity) {
        mApp.mhaveActivity = mIsHaveActivity;
    }
}
