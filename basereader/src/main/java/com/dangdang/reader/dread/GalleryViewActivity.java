package com.dangdang.reader.dread;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dangdang.reader.R;
import com.dangdang.reader.dread.config.ReadConfig;
import com.dangdang.reader.dread.core.epub.CustomFramLayout;
import com.dangdang.reader.dread.core.epub.GalleryViewAdapter;
import com.dangdang.reader.dread.core.epub.GestrueControlGalleryView;
import com.dangdang.reader.dread.core.epub.GestrueControlGalleryView.OnGalleryPageChangeListener;
import com.dangdang.reader.dread.core.epub.GestrueControlGalleryView.OnScaleModeChangeListener;
import com.dangdang.reader.dread.core.epub.ReaderAppImpl;
import com.dangdang.reader.dread.data.GallaryData;
import com.dangdang.reader.dread.data.ReadInfo;
import com.dangdang.reader.dread.format.BaseReadInfo;
import com.dangdang.reader.utils.DangdangFileManager;
import com.dangdang.reader.utils.SystemLib;
import com.dangdang.zframework.BaseActivity;
import com.dangdang.zframework.log.LogM;
import com.dangdang.zframework.utils.DRUiUtility;
import com.dangdang.zframework.utils.UiUtil;
import com.dangdang.zframework.view.DDImageView;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;

public class GalleryViewActivity extends BaseActivity {

    public static final String KEY_GALLERY_ID = "keyGalleryId";
    public static final String KEY_GALLERY_URLS = "keyGalleryUrl";
    public static final String KEY_GALLERY_RECT = "keyGalleryRect";
    public static final String KEY_GALLERY_PAGEINDEX = "keyGalleryPageIndex";
    public static final String KEY_IMGDESC = "keyImgDesc";
    public static final String KEY_IMGBGCOLOR = "keyImgBgColor";
    public static final String KEY_LANDSCAPE = "keyLandScape";

    private final String PATH_IMAGES = android.os.Environment
            .getExternalStorageDirectory().getAbsolutePath()
            + "/Pictures/"
            + DangdangFileManager.APP_DIR;

    private GestrueControlGalleryView mGalleryView;
    private String[] galleryUrls;
    private Rect mRect;
    private int mPageIndex;
    private int mGalleryId;
    private ViewGroup mBottomLayout;
    // private SharePopupWindow mSharePopupWindow;
    private String tempShareImagePath;
    private String mProductId;
    private String mBookName;
    private String mBookDesc;
    private String mAuthor;
    private String mBookCover;
    private String[] mDesc;
    private TextView mTxtView;
    private int mImgBgColor;
    private boolean mIsLandScape;

//    private ShareUtil mShareUtil;

    @Override
    protected void onCreateImpl(Bundle savedInstanceState) {
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.read_galleryview_activity);
        receiveIntent();
        initBookInfo();
        initBottomView();
        initGallery();
        if (mIsLandScape)
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    private void initBookInfo() {
        ReadInfo readInfo = (ReadInfo) ReaderAppImpl.getApp().getReadInfo();
        mProductId = readInfo.getProductId();
        mBookName = readInfo.getBookName();
        mBookDesc = readInfo.getBookDesc();
        mBookCover = readInfo.getBookCover();
        try {
            JSONObject obj = new JSONObject(readInfo.getBookJson());
            mAuthor = obj.optString("author", "");
        } catch (Exception e) {
        }
    }

    private void initBottomView() {
        mTxtView = (TextView) findViewById(R.id.reader_image_desc);
        mTxtView.setText(mDesc[0]);
        mTxtView.setMovementMethod(new ScrollingMovementMethod());
        mBottomLayout = (ViewGroup) findViewById(R.id.reader_image_bottom_layout);
        DDImageView download = (DDImageView) findViewById(R.id.reader_image_download);
        download.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String path = PATH_IMAGES + File.separator
                        + System.currentTimeMillis() + ".png";
                saveBitmap(path, mGalleryView.getCurrentPageIndex());
                String toast = getString(R.string.file_saved) + path;
                SystemLib.updateSystemGallery(GalleryViewActivity.this, new File(path));
                UiUtil.showToast(GalleryViewActivity.this, toast);
            }
        });
    }

    private void initGallery() {
        GallaryData gallaryData = new GallaryData();
        gallaryData.setImageRect(mRect);
        gallaryData.setFiles(galleryUrls);
        gallaryData.setImgTexts(mDesc);
        CustomFramLayout frameLayout = (CustomFramLayout) findViewById(R.id.gallery_fl);
        int argbColor = ReadConfig.getConfig().getReaderBgColor();
        if (mImgBgColor != -1)
            argbColor = mImgBgColor;
        int red = Color.red(argbColor);
        int green = Color.green(argbColor);
        int blue = Color.blue(argbColor);
        int rectColor = Color.rgb(red, green, blue);
        Paint rectPaint = new Paint();
        rectPaint.setColor(rectColor);
        frameLayout.init(gallaryData.getImageRect(), rectPaint);
        mGalleryView = (GestrueControlGalleryView) findViewById(R.id.gallery);
        mGalleryView.setGalleryData(gallaryData);
        mGalleryView.setGallerySize(DRUiUtility.getScreenWith(),
                DRUiUtility.getScreenHeight());
        GalleryViewAdapter adapter = new GalleryViewAdapter(this,
                Arrays.asList(galleryUrls), true);
        mGalleryView.setAdapter(adapter);
        mGalleryView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mGalleryView.startExit();
            }
        });
        mGalleryView.setGalleryId(mGalleryId);
        mGalleryView.setOnScaleModeChangeListener(mOnScaleModeChangeListener);
        mGalleryView.setGalleryPageChangeListener(mOnGalleryPageChangeListener);
        mGalleryView.scrollToPage(mPageIndex);
    }

    private void receiveIntent() {
        Intent intent = getIntent();
        galleryUrls = intent.getStringArrayExtra(KEY_GALLERY_URLS);
        mRect = intent.getParcelableExtra(KEY_GALLERY_RECT);
        mPageIndex = intent.getIntExtra(KEY_GALLERY_PAGEINDEX, 0);
        mGalleryId = intent.getIntExtra(KEY_GALLERY_ID, 0);
        mDesc = intent.getStringArrayExtra(KEY_IMGDESC);
        mImgBgColor = intent.getIntExtra(KEY_IMGBGCOLOR, -1);
        mIsLandScape = intent.getBooleanExtra(KEY_LANDSCAPE, false);
    }

    public void setBottomBarVisibility(boolean visible) {
        if (visible) {
            mBottomLayout.setVisibility(ViewGroup.VISIBLE);
        } else {
            mBottomLayout.setVisibility(ViewGroup.INVISIBLE);
        }
    }

    @Override
    public void finish() {
        super.finish();
//        overridePendingTransition(0, 0);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            if (mShareUtil != null && mShareUtil.isShow()) {
//                return true;
//            }
            mGalleryView.startExit();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroyImpl() {
        mGalleryView.reset();
//        if (mShareUtil != null)
//            mShareUtil.clear();
//        mShareUtil = null;
    }

    protected void onResume() {
        super.onResume();
//        UmengStatistics.onPageStart(getClass().getSimpleName());
        if (mGalleryView.isFirstResume()) {
            mGalleryView.startFirstResume();
        }
    }

	/*
	 * private ShareCallbackUtil.ShareCallback mShareCallback = new
	 * ShareCallbackUtil.ShareCallback() {
	 * 
	 * @Override public void onCallBack(boolean isShareSuccess,
	 * SharePopupWindow.DDShareData shareData) { if (tempShareImagePath != null)
	 * { File file = new File(tempShareImagePath); if (file.exists()) {
	 * file.delete(); } } }
	 * 
	 * @Override public void onCancel() { } };
	 */

//    private DDShareData getDDShareData() {
//        DDShareData shareData = new DDShareData();
//        shareData.setAuthor(mAuthor);
//        shareData.setTitle(mBookName);
//        shareData.setBookName(mBookName);
//        shareData.setShareType(DDShareData.CONTENT_TYPE_READIMAGE);
//        shareData.setTargetUrl(DDShareData.DDREADER_BOOK_DETAIL_LINK);
//        shareData.setContent(mBookDesc);
//        shareData.setDesc(mBookDesc);
////        shareData.setContent(mDesc[mPageIndex]);
////        shareData.setDesc(mDesc[mPageIndex]);
//        shareData.setPicUrl(mBookCover);
//        tempShareImagePath = DangdangFileManager.getImageCacheDir()
//                + "gallerySharetemp.png";
//        saveBitmap(tempShareImagePath, mGalleryView.getCurrentPageIndex());
//        shareData.setPicUrl(tempShareImagePath);
//        DDShareParams params = new DDShareParams();
//        params.setSaleId(getReadInfo().getProductId());
//        params.setMediaId(getReadInfo().getProductId());
//        if (getReadInfo() instanceof PartReadInfo) {
//            PartReadInfo partReadInfo = (PartReadInfo) getReadInfo();
//            params.setSaleId(partReadInfo.getSaleId());
//            shareData.setMediaType(1);
//        } else
//        	shareData.setMediaType(2);
//        shareData.setParams(JSON.toJSONString(params));
//        return shareData;
//    }

    private BaseReadInfo getReadInfo() {
        return ReaderAppImpl.getApp().getReadInfo();
    }

//    private DDShareData.DDStatisticsData getDDStatisticsData() {
//        DDShareData.DDStatisticsData statisticsData = new DDShareData.DDStatisticsData(
//                DDShareData.CONTENT_TYPE_READIMAGE);
//        statisticsData.setBookName(mBookName);
//        statisticsData.setProductId(mProductId);
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

    private void saveBitmap(String filePath, int index) {
        GalleryViewAdapter adapter = mGalleryView.getAdapter();
        byte[] bitmapData = adapter.getBitmapData(index);
        if (bitmapData == null || bitmapData.length <= 0) {
            LogM.e(getClass().getSimpleName(), " save bmp failed ");
            return;
        }
        File f = new File(filePath);
        FileOutputStream fOut = null;
        try {
            if (f.exists()) {
                f.delete();
            }
            File dir = f.getParentFile();
            if (!dir.exists())
                dir.mkdirs();
            f.createNewFile();
            fOut = new FileOutputStream(f);
            fOut.write(bitmapData, 0, bitmapData.length);
            fOut.flush();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fOut != null) {
                    fOut.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private OnScaleModeChangeListener mOnScaleModeChangeListener = new OnScaleModeChangeListener() {

        @Override
        public void onScaleModeChange(int mode) {
            if (mode == GestrueControlGalleryView.MODE_FIT_WIDTH) {
                setBottomBarVisibility(true);
            } else {
                setBottomBarVisibility(false);
            }
        }
    };

    private OnGalleryPageChangeListener mOnGalleryPageChangeListener = new OnGalleryPageChangeListener() {
        public void onPageChange(GallaryData gallaryData, int galleryId,
                                 int pageIndex) {
            if (mDesc.length > pageIndex && pageIndex >= 0)
                mTxtView = (TextView) findViewById(R.id.reader_image_desc);
            mTxtView.setText(mDesc[pageIndex]);
            mPageIndex = pageIndex;
        }
    };
}
