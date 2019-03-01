package com.moxi.nexams.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moxi.nexams.R;
import com.moxi.nexams.adapter.test.ClozeTestAdapter;
import com.moxi.nexams.cache.ACache;
import com.moxi.nexams.db.newdb.NewExamsSqliteUtils;
import com.moxi.nexams.model.papermodel.PaperDetailsModel;
import com.moxi.nexams.model.papermodel.PaperModelDesc;
import com.moxi.nexams.utils.FullyLinearLayoutManager;
import com.moxi.nexams.utils.LocationPhotoInstance;
import com.moxi.nexams.utils.TitleUtils;
import com.moxi.nexams.view.CustomScrollView;
import com.moxi.nexams.view.PaintInvalidateRectView;
import com.mx.mxbase.base.BaseActivity;
import com.mx.mxbase.interfaces.OnItemClickListener;
import com.mx.mxbase.utils.FileUtils;
import com.mx.mxbase.utils.Toastor;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * 单项选择界面展示
 * Created by Archer on 2017/1/13.
 */
public class ChoiceTestActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.ll_base_back)
    LinearLayout llBack;
    @Bind(R.id.tv_base_mid_title)
    TextView tvMidTitle;
    @Bind(R.id.recycler_test_title)
    RecyclerView recyclerTitle;
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
    @Bind(R.id.tv_little_test_analysis)
    TextView tvAnaylsis;

    private int page, ppsId;
    private String paperTitle;
    private String filePath, fileName;
    private List<PaperDetailsModel> listDetails = new ArrayList<>();
    private int[] results;
    private ACache aCache;
    private ClozeTestAdapter cAdapter;
    private int paperDb, typeIndex;
    private List<PaperModelDesc> listType = new ArrayList<>();
    private String cacheImgPath = FileUtils.getInstance().getDataFilePath();
    private String saveSync = "savePap";
    private boolean isHistory;

    @Override
    protected int getMainContentViewId() {
        return R.layout.mx_activity_choice_test;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        init();
    }

    /**
     * 初始化view
     */
    private void init() {
        aCache = ACache.get(this);
        page = 0;
        paperDb = getIntent().getIntExtra("paper_index", -1);
        tvTestType.setText(getIntent().getStringExtra("test_type"));
        ppsId = getIntent().getIntExtra("pps_id", -1);
        paperTitle = this.getIntent().getStringExtra("paper_title");
        typeIndex = getIntent().getIntExtra("test_type_index", 0);
        listType = (List<PaperModelDesc>) getIntent().getSerializableExtra("test_list_types");
        isHistory = getIntent().getBooleanExtra("test_is_history", false);

        filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/nexams/dbdata/";
        fileName = paperDb + ".db";

        tvMidTitle.setText(paperTitle);
        llBack.setVisibility(View.VISIBLE);
        llBack.setOnClickListener(this);
        llRight.setOnClickListener(this);

        NewExamsSqliteUtils newdb = new NewExamsSqliteUtils(this, filePath, fileName);
        listDetails = newdb.getPaperDetailsByPpsId(ppsId);

        recyclerTitle.setLayoutManager(new FullyLinearLayoutManager(this));
        recyclerTitle.setNestedScrollingEnabled(false);

        if (listDetails.size() > 0) {
            resolveResult(page);
        }

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
                            TitleUtils.moveToActivity(ChoiceTestActivity.this, isHistory,
                                    listType.get(typeIndex).getPpsMainTitle(), paperDb, listType.get(typeIndex).getPpsId(),
                                    paperTitle, typeIndex, listType);
                            ChoiceTestActivity.this.finish();
                        } else {
                            Toastor.showToast(ChoiceTestActivity.this, "已经是第一题了");
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
                            TitleUtils.moveToActivity(ChoiceTestActivity.this, isHistory,
                                    listType.get(typeIndex).getPpsMainTitle(), paperDb, listType.get(typeIndex).getPpsId(),
                                    paperTitle, typeIndex, listType);
                            ChoiceTestActivity.this.finish();
                        } else {
                            Toastor.showToast(ChoiceTestActivity.this, "已经是最后一题了");
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
    }

    /**
     * 保存写的图片
     *
     * @param ppsId
     * @param page
     */
    private void saveBitMap(int ppsId, int page) {
        String bitUrl = cacheImgPath + saveSync + "/" + ppsId + "/" + "page" + page + ".png";
        if (paintInvalidateRectView.getVisibility() == View.VISIBLE) {
            Bitmap bitmap = paintInvalidateRectView.getBitmap();
            if (bitmap != null) {
                LocationPhotoInstance.getInstance().addPhoto(bitUrl.replace(" ", ""), bitmap);
            }
        }
    }

    /**
     * 显示每一页
     *
     * @param page 题号
     */
    private void resolveResult(final int page) {
        //当前题目详情
        if (page == listDetails.size() - 1 && typeIndex == listType.size() - 1 && !isHistory) {
            tvRight.setText("提交");
            llRight.setVisibility(View.VISIBLE);
        } else {
            llRight.setVisibility(View.GONE);
        }
        int[] temp = (int[]) aCache.getAsObject("ppsid_" + ppsId + "page_" + page);
        if (temp != null) {
            results = temp;
        } else {
            results = new int[1];
        }
        PaperDetailsModel pdm = listDetails.get(page);
        TitleUtils.setTestTitle((page + 1) + "、" + pdm.getPsjTitle(), tvTestTitle, this);
        String tempAnalysis = "";
        if (pdm.getPsjAnalysis().equals("")) {
            tempAnalysis = "略";
        } else {
            tempAnalysis = pdm.getPsjAnalysis();
        }
        TitleUtils.setTestTitle("解析:" + tempAnalysis, tvAnaylsis, this);
        if (isHistory) {
            tvAnaylsis.setVisibility(View.VISIBLE);
        } else {
            tvAnaylsis.setVisibility(View.GONE);
        }
        if (pdm.getPsjOption().equals("")) {
            paintInvalidateRectView.setVisibility(View.VISIBLE);
            recyclerTitle.setVisibility(View.GONE);
            String bitUrl = cacheImgPath + saveSync + "/" + ppsId + "/" + "page" + page + ".png";
            LocationPhotoInstance.getInstance().loadImage(bitUrl.replace(" ", ""), new LocationPhotoInstance.LoadPhotoListener() {
                @Override
                public void onLoadSucess(Bitmap bitmap, String path) {
                    paintInvalidateRectView.initBitmap(bitmap, path);
                }
            });
        } else {
            paintInvalidateRectView.setVisibility(View.GONE);
            recyclerTitle.setVisibility(View.VISIBLE);
            //设置标题
            cAdapter = new ClozeTestAdapter(this, "", pdm.getPsjOption(), results[0]);
            recyclerTitle.setAdapter(cAdapter);
            cAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    if (isHistory) {
                        Toastor.showToast(ChoiceTestActivity.this, "已经提交不能修改答案");
                    } else {
                        results[0] = position;
                        aCache.put("ppsid_" + ppsId + "page_" + page, results);
                        cAdapter.setResult(position);
                    }
                }

                @Override
                public void onItemLongClick(View view, int position) {

                }
            });
        }
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
                        TitleUtils.moveToActivity(ChoiceTestActivity.this, isHistory,
                                listType.get(typeIndex).getPpsMainTitle(), paperDb, listType.get(typeIndex).getPpsId(),
                                paperTitle, typeIndex, listType);
                        ChoiceTestActivity.this.finish();
                    } else {
                        Toastor.showToast(ChoiceTestActivity.this, "已经是第一题了");
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
                        TitleUtils.moveToActivity(ChoiceTestActivity.this, isHistory,
                                listType.get(typeIndex).getPpsMainTitle(), paperDb, listType.get(typeIndex).getPpsId(),
                                paperTitle, typeIndex, listType);
                        ChoiceTestActivity.this.finish();
                    } else {
                        Toastor.showToast(ChoiceTestActivity.this, "已经是最后一题了");
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
                        ChoiceTestActivity.this.finish();
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
