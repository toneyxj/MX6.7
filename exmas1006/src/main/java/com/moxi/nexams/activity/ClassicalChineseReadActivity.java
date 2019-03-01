package com.moxi.nexams.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
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
 * 文言文阅读界面展示
 * Created by Archer on 2017/1/16.
 */
public class ClassicalChineseReadActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.ll_base_back)
    LinearLayout llBack;
    @Bind(R.id.tv_base_mid_title)
    TextView tvMidTitle;
    @Bind(R.id.custom_all)
    CustomScrollView customAll;
    @Bind(R.id.custom_half)
    CustomScrollView customHalf;
    @Bind(R.id.recycler_view_test)
    RecyclerView recyclerTest;
    @Bind(R.id.img_mid)
    ImageView imgEx;
    @Bind(R.id.tv_test_main_value)
    TextView tvMainValue;
    @Bind(R.id.tv_test_main_type)
    TextView tvTestType;
    @Bind(R.id.tv_little_test_index)
    TextView tvOptionIndex;
    @Bind(R.id.paint_invalidate_rectview)
    PaintInvalidateRectView paintInvalidate;
    @Bind(R.id.ll_base_right)
    LinearLayout llRight;
    @Bind(R.id.tv_base_right)
    TextView tvRight;
    @Bind(R.id.tv_little_test_analysis)
    TextView tvAnalysis;

    private ACache aCache;
    private int page, ppsId, optionIndex;
    private String paperTitle;
    private String filePath, fileName;
    private List<PaperDetailsModel> listDetails = new ArrayList<>();
    private ClozeTestAdapter clozeTestAdapter;
    private int[] results;

    private String cacheImgPath = FileUtils.getInstance().getDataFilePath();
    private String saveSync = "savePap";
    private int paperDb, typeIndex;
    private List<PaperModelDesc> listType = new ArrayList<>();
    private boolean isHistory;

    @Override
    protected int getMainContentViewId() {
        return R.layout.mx_activity_classical_chinese_read;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        init();
    }

    /**
     * 初始化视图
     */
    private void init() {
        aCache = ACache.get(this);
        page = 0;
        paperDb = getIntent().getIntExtra("paper_index", -1);
        tvTestType.setText(getIntent().getStringExtra("test_type"));
        ppsId = getIntent().getIntExtra("pps_id", -1);
        paperTitle = getIntent().getStringExtra("paper_title");
        typeIndex = getIntent().getIntExtra("test_type_index", 0);
        isHistory = getIntent().getBooleanExtra("test_is_history", false);

        listType = (List<PaperModelDesc>) getIntent().getSerializableExtra("test_list_types");
        filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/nexams/dbdata/";

        fileName = paperDb + ".db";
        tvMidTitle.setText(paperTitle);
        imgEx.setTag(0);
        imgEx.setOnClickListener(this);
        llBack.setVisibility(View.VISIBLE);
        llBack.setOnClickListener(this);
        llRight.setOnClickListener(this);

        NewExamsSqliteUtils newdb = new NewExamsSqliteUtils(this, filePath, fileName);
        listDetails = newdb.getPaperDetailsByPpsId(ppsId);

        if (listDetails.size() > 0) {
            resolveResult(page);
        }

        customAll.setSlideListener(new CustomScrollView.SlideListener() {
            @Override
            public void moveDirection(boolean left, boolean up, boolean right, boolean down) {
                if (left) {
                    if (page > 0) {
                        page--;
                        resolveResult(page);
                    } else {
                        if (typeIndex > 0) {
                            typeIndex--;
//                            ppsId--;
                            TitleUtils.moveToActivity(ClassicalChineseReadActivity.this, isHistory,
                                    listType.get(typeIndex).getPpsMainTitle(), paperDb, listType.get(typeIndex).getPpsId(),
                                    paperTitle, typeIndex, listType);
                            ClassicalChineseReadActivity.this.finish();
                        } else {
                            Toastor.showToast(ClassicalChineseReadActivity.this, "已经是第一题了");
                        }
                    }
                } else if (right) {
                    if (page < listDetails.size() - 1) {
                        page++;
                        resolveResult(page);
                    } else {
                        if (typeIndex < listType.size() - 1) {
                            typeIndex++;
//                            ppsId++;
                            TitleUtils.moveToActivity(ClassicalChineseReadActivity.this, isHistory,
                                    listType.get(typeIndex).getPpsMainTitle(), paperDb, listType.get(typeIndex).getPpsId(),
                                    paperTitle, typeIndex, listType);
                            ClassicalChineseReadActivity.this.finish();
                        } else {
                            Toastor.showToast(ClassicalChineseReadActivity.this, "已经是最后一题了");
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
     * 解析题型数据
     *
     * @param page 当前页码
     */
    private void resolveResult(final int page) {
        if (page == listDetails.size() - 1 && typeIndex == listType.size() - 1 && !isHistory) {
            tvRight.setText("提交");
            llRight.setVisibility(View.VISIBLE);
        } else {
            llRight.setVisibility(View.GONE);
        }

        optionIndex = 0;
        PaperDetailsModel pdm = listDetails.get(page);
        //设置标题
        TitleUtils.setTestTitle(pdm.getPsjTitle(), tvMainValue, this);
        NewExamsSqliteUtils child = new NewExamsSqliteUtils(this, filePath, fileName);
        final List<PaperDetailsModel> listPaper = child.getPaperChildrenByParentId(pdm.getPsjId() + "");
        if (listPaper.size() > 0) {
            initOptionsValue(listPaper, optionIndex);
        } else {

        }

        customHalf.setSlideListener(new CustomScrollView.SlideListener() {
            @Override
            public void moveDirection(boolean left, boolean up, boolean right, boolean down) {
                if (left) {
                    saveBitMap(ppsId, page, optionIndex);
                    if (optionIndex > 0) {
                        optionIndex--;
                        initOptionsValue(listPaper, optionIndex);
                    } else {
                        Toastor.showToast(ClassicalChineseReadActivity.this, "已经是第一小题了");
                    }
                } else if (right) {
                    saveBitMap(ppsId, page, optionIndex);
                    if (optionIndex < listPaper.size() - 1) {
                        optionIndex++;
                        initOptionsValue(listPaper, optionIndex);
                    } else {
                        Toastor.showToast(ClassicalChineseReadActivity.this, "已经是最后一题了");
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
        ViewTreeObserver vto = customHalf.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                View childView = customHalf.getChildAt(0);
                customHalf.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                int childHeight = childView.getMeasuredHeight();
                int height = customHalf.getMeasuredHeight();
                height = childHeight >= height ? childHeight : height;
                paintInvalidate.getLayoutParams().height = height;
            }
        });
    }

    /**
     * 保存写的图片
     *
     * @param ppsId
     * @param page
     * @param optionIndex
     */
    private void saveBitMap(int ppsId, int page, int optionIndex) {
        String bitUrl = cacheImgPath + saveSync + "/" + ppsId + "/" + "page" + page + "/" + optionIndex + ".png";
        if (paintInvalidate.getVisibility() == View.VISIBLE) {
            Bitmap bitmap = paintInvalidate.getBitmap();
            if (bitmap != null) {
                LocationPhotoInstance.getInstance().addPhoto(bitUrl.replace(" ", ""), bitmap);
            }
        }
    }

    /**
     * @param listPaper
     * @param optionIndex
     */
    private void initOptionsValue(List<PaperDetailsModel> listPaper, final int optionIndex) {
        String tempAnalysis = "";
        if (listPaper.get(optionIndex).getPsjAnalysis().equals("")) {
            tempAnalysis = "略";
        } else {
            tempAnalysis = listPaper.get(optionIndex).getPsjAnalysis();
        }
        TitleUtils.setTestTitle("解析:" + tempAnalysis, tvAnalysis, this);
        if (isHistory) {
            tvAnalysis.setVisibility(View.VISIBLE);
        } else {
            tvAnalysis.setVisibility(View.GONE);
        }
        if (listPaper.get(optionIndex).getPsjOption().equals("")) {
            //TODO 非选择题
            TitleUtils.setTestTitle((optionIndex + 1) + "、" + listPaper.get(optionIndex).getPsjTitle(), tvOptionIndex, this);
            paintInvalidate.setVisibility(View.VISIBLE);
            recyclerTest.setVisibility(View.GONE);
            String bitUrl = cacheImgPath + saveSync + "/" + ppsId + "/" + "page" + page + "/" + optionIndex + ".png";
            LocationPhotoInstance.getInstance().loadImage(bitUrl.replace(" ", ""), new LocationPhotoInstance.LoadPhotoListener() {
                @Override
                public void onLoadSucess(Bitmap bitmap, String path) {
                    paintInvalidate.initBitmap(bitmap, path);
                }
            });
        } else {
            //TODO 选择题
            recyclerTest.setVisibility(View.VISIBLE);
            paintInvalidate.setVisibility(View.GONE);
            int[] temp = (int[]) aCache.getAsObject("ppsid_" + ppsId + "page_" + page);
            if (temp != null) {
                results = temp;
            } else {
                results = new int[listPaper.size()];
            }
            tvOptionIndex.setText((optionIndex + 1) + "/" + listPaper.size() + "一共" + listPaper.size() + "小题," + "(手指左右滑动切换小题)");
            recyclerTest.setLayoutManager(new FullyLinearLayoutManager(this));
            recyclerTest.setNestedScrollingEnabled(false);
            clozeTestAdapter = new ClozeTestAdapter(this, listPaper.get(optionIndex).getPsjTitle(), listPaper.get(optionIndex).getPsjOption(), results[optionIndex]);
            recyclerTest.setAdapter(clozeTestAdapter);
            clozeTestAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    if (isHistory) {
                        Toastor.showToast(ClassicalChineseReadActivity.this, "已经提交不能修改答案");
                    } else {
                        results[optionIndex] = position;
                        aCache.put("ppsid_" + ppsId + "page_" + page, results);
                        clozeTestAdapter.setResult(position);
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
                        TitleUtils.moveToActivity(ClassicalChineseReadActivity.this, isHistory,
                                listType.get(typeIndex).getPpsMainTitle(), paperDb, listType.get(typeIndex).getPpsId(),
                                paperTitle, typeIndex, listType);
                        ClassicalChineseReadActivity.this.finish();
                    } else {
                        Toastor.showToast(ClassicalChineseReadActivity.this, "已经是第一题了");
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
                        TitleUtils.moveToActivity(ClassicalChineseReadActivity.this, isHistory,
                                listType.get(typeIndex).getPpsMainTitle(), paperDb, listType.get(typeIndex).getPpsId(),
                                paperTitle, typeIndex, listType);
                        ClassicalChineseReadActivity.this.finish();
                    } else {
                        Toastor.showToast(ClassicalChineseReadActivity.this, "已经是最后一题了");
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
            case R.id.img_mid:
                int tag = (int) imgEx.getTag();
                if (tag == 0) {
                    customHalf.setVisibility(View.GONE);
                    imgEx.setImageResource(R.mipmap.zhankai_icon);
                    imgEx.setTag(1);
                } else {
                    customHalf.setVisibility(View.VISIBLE);
                    imgEx.setImageResource(R.mipmap.zhedie_icon);
                    imgEx.setTag(0);
                }
                break;
            case R.id.ll_base_back:
                this.finish();
                break;
            case R.id.ll_base_right:
                TitleUtils.submitPaper(this, paperDb + "", new TitleUtils.SubmitCallBack() {
                    @Override
                    public void onSuccess() {
                        ClassicalChineseReadActivity.this.finish();
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
