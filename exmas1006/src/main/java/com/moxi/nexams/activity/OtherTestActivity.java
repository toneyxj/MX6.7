package com.moxi.nexams.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moxi.nexams.R;
import com.moxi.nexams.db.newdb.NewExamsSqliteUtils;
import com.moxi.nexams.model.papermodel.PaperDetailsModel;
import com.moxi.nexams.model.papermodel.PaperModelDesc;
import com.moxi.nexams.utils.LocationPhotoInstance;
import com.moxi.nexams.utils.TitleUtils;
import com.moxi.nexams.view.CustomScrollView;
import com.moxi.nexams.view.PaintInvalidateRectView;
import com.mx.mxbase.base.BaseActivity;
import com.mx.mxbase.utils.FileUtils;
import com.mx.mxbase.utils.Toastor;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * 其他题型显示界面
 * Created by Archer on 2017/1/13.
 */
public class OtherTestActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.ll_base_back)
    LinearLayout llBack;
    @Bind(R.id.tv_base_mid_title)
    TextView tvMidTitle;
    @Bind(R.id.tv_little_test_main)
    TextView tvTestType;
    @Bind(R.id.tv_little_test_main_value)
    TextView tvTestTitle;
    @Bind(R.id.custom_all)
    CustomScrollView customAll;
    @Bind(R.id.paint_invalidate_rectview)
    PaintInvalidateRectView paintInvalidateRectView;
    @Bind(R.id.ll_base_right)
    LinearLayout llRight;
    @Bind(R.id.tv_base_right)
    TextView tvRight;
    @Bind(R.id.tv_little_test_main_analysis)
    TextView tvAnalysis;

    private int page, ppsId;
    private String paperTitle;
    private String filePath, fileName;
    private List<PaperDetailsModel> listDetails = new ArrayList<>();
    private int paperDb, typeIndex;
    private List<PaperModelDesc> listType = new ArrayList<>();
    private String cacheImgPath = FileUtils.getInstance().getDataFilePath();
    private String saveSync = "savePap";
    private boolean isHistory;

    private ViewTreeObserver vto;

    @Override
    protected int getMainContentViewId() {
        return R.layout.mx_activity_other_test;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        init();
    }

    /**
     * 初始化view
     */
    private void init() {
        llBack.setVisibility(View.VISIBLE);
        llBack.setOnClickListener(this);
        llRight.setOnClickListener(this);

        page = 0;
        paperDb = getIntent().getIntExtra("paper_index", -1);
        tvTestType.setText(getIntent().getStringExtra("test_type"));
        ppsId = getIntent().getIntExtra("pps_id", -1);
        paperTitle = this.getIntent().getStringExtra("paper_title");
        filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/nexams/dbdata/";
        typeIndex = getIntent().getIntExtra("test_type_index", 0);
        listType = (List<PaperModelDesc>) getIntent().getSerializableExtra("test_list_types");
        isHistory = getIntent().getBooleanExtra("test_is_history", false);

        fileName = paperDb + ".db";
        tvMidTitle.setText(paperTitle);

        NewExamsSqliteUtils newdb = new NewExamsSqliteUtils(this, filePath, fileName);
        listDetails = newdb.getPaperDetailsByPpsId(ppsId);

        if (listDetails.size() > 0) {
            resolveResult(page);
        }

        /**
         * 设置scrollview手势监听
         */
        customAll.setSlideListener(new CustomScrollView.SlideListener() {
            @Override
            public void moveDirection(boolean left, boolean up, boolean right, boolean down) {
                if (left) {
                    saveBitMap(ppsId, page);
                    if (page > 0) {
                        page--;
                        resolveResult(page);
                    } else {
                        if (typeIndex > 0) {
                            typeIndex--;
//                            ppsId--;
                            TitleUtils.moveToActivity(OtherTestActivity.this, isHistory,
                                    listType.get(typeIndex).getPpsMainTitle(), paperDb, listType.get(typeIndex).getPpsId(),
                                    paperTitle, typeIndex, listType);
                            OtherTestActivity.this.finish();
                        } else {
                            Toastor.showToast(OtherTestActivity.this, "已经是第一题了");
                        }
                    }
                } else if (right) {
                    saveBitMap(ppsId, page);
                    if (page < listDetails.size() - 1) {
                        page++;
                        resolveResult(page);
                    } else {
                        if (typeIndex < listType.size() - 1) {
                            typeIndex++;
//                            ppsId++;
                            TitleUtils.moveToActivity(OtherTestActivity.this, isHistory,
                                    listType.get(typeIndex).getPpsMainTitle(), paperDb, listType.get(typeIndex).getPpsId(),
                                    paperTitle, typeIndex, listType);
                            OtherTestActivity.this.finish();
                        } else {
                            Toastor.showToast(OtherTestActivity.this, "已经是最后一题了");
                        }
                    }
                }
            }

            @Override
            public void toBootom() {

            }

            @Override
            public void toTop() {

            }
        });

        /**
         * 计算scrollview高度重新赋值给paintInvalidateRectView
         */
        ViewTreeObserver vto = customAll.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                View childView = customAll.getChildAt(0);

                customAll.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                int height = childView.getMeasuredHeight();
                paintInvalidateRectView.getLayoutParams().height = height;
            }
        });
    }

    /**
     * @param page 页码
     */
    private void resolveResult(int page) {
        String bitUrl = cacheImgPath + saveSync + "/" + ppsId + "/" + "page" + page + "/" + page + ".png";
        LocationPhotoInstance.getInstance().loadImage(bitUrl.replace(" ", ""), new LocationPhotoInstance.LoadPhotoListener() {
            @Override
            public void onLoadSucess(Bitmap bitmap, String path) {
                paintInvalidateRectView.initBitmap(bitmap, path);
            }
        });
        if (page == listDetails.size() - 1 && typeIndex == listType.size() - 1 && !isHistory) {
            tvRight.setText("提交");
            llRight.setVisibility(View.VISIBLE);
        } else {
            llRight.setVisibility(View.GONE);
        }
        PaperDetailsModel pdm = listDetails.get(page);
        String tempAnalysis = "";
        if (pdm.getPsjAnalysis().equals("")) {
            tempAnalysis = "略";
        } else {
            tempAnalysis = pdm.getPsjAnalysis();
        }
        TitleUtils.setTestTitle("解析:" + tempAnalysis, tvAnalysis, this);
        if (isHistory) {
            tvAnalysis.setVisibility(View.VISIBLE);
        } else {
            tvAnalysis.setVisibility(View.GONE);
        }
        TitleUtils.setTestTitle((page + 1) + "、" + pdm.getPsjTitle(), tvTestTitle, this);
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        saveBitMap(ppsId, page);
    }

    @Override
    public void onActivitySaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {

    }

    @Override
    public void onActivityRestoreInstanceState(Bundle savedInstanceState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    /**
     * 保存写的图片
     *
     * @param ppsId
     * @param page
     */
    private void saveBitMap(int ppsId, int page) {
        String bitUrl = cacheImgPath + saveSync + "/" + ppsId + "/" + "page" + page + "/" + page + ".png";
        if (paintInvalidateRectView.getVisibility() == View.VISIBLE) {
            Bitmap bitmap = paintInvalidateRectView.getBitmap();
            if (bitmap != null) {
                LocationPhotoInstance.getInstance().addPhoto(bitUrl.replace(" ", ""), bitmap);
            }
        }
    }


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_PAGE_UP:
                if (page > 0) {
                    page--;
                    resolveResult(page);
                } else {
                    if (typeIndex > 0) {
                        typeIndex--;
                        TitleUtils.moveToActivity(OtherTestActivity.this, isHistory,
                                listType.get(typeIndex).getPpsMainTitle(), paperDb, listType.get(typeIndex).getPpsId(),
                                paperTitle, typeIndex, listType);
                        OtherTestActivity.this.finish();
                    } else {
                        Toastor.showToast(OtherTestActivity.this, "已经是第一题了");
                    }
                }
                return true;
            case KeyEvent.KEYCODE_PAGE_DOWN:
                if (page < listDetails.size() - 1) {
                    page++;
                    resolveResult(page);
                } else {
                    if (typeIndex < listType.size() - 1) {
                        typeIndex++;
                        TitleUtils.moveToActivity(OtherTestActivity.this, isHistory,
                                listType.get(typeIndex).getPpsMainTitle(), paperDb, listType.get(typeIndex).getPpsId(),
                                paperTitle, typeIndex, listType);
                        OtherTestActivity.this.finish();
                    } else {
                        Toastor.showToast(OtherTestActivity.this, "已经是最后一题了");
                    }
                }
                return true;
            case KeyEvent.KEYCODE_BACK:
                this.finish();
                return true;
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_base_back:
                this.finish();
                break;
            case R.id.ll_base_right:
                TitleUtils.submitPaper(this, paperDb + "", new TitleUtils.SubmitCallBack() {
                    @Override
                    public void onSuccess() {
                        OtherTestActivity.this.finish();
                    }

                    @Override
                    public void onFail() {

                    }
                });
                break;
            default:
                break;
        }
    }
}
