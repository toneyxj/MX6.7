package com.moxi.nexams.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moxi.nexams.R;
import com.moxi.nexams.adapter.test.SevenGetFiveAdapter;
import com.moxi.nexams.adapter.test.TestTitleAdapter;
import com.moxi.nexams.cache.ACache;
import com.moxi.nexams.db.newdb.NewExamsSqliteUtils;
import com.moxi.nexams.model.papermodel.PaperDetailsModel;
import com.moxi.nexams.model.papermodel.PaperModelDesc;
import com.moxi.nexams.utils.FullyLinearLayoutManager;
import com.moxi.nexams.utils.TitleUtils;
import com.moxi.nexams.view.CustomScrollView;
import com.mx.mxbase.base.BaseActivity;
import com.mx.mxbase.interfaces.OnItemClickListener;
import com.mx.mxbase.utils.Toastor;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * 七选五界面展示
 * Created by Archer on 2017/1/13.
 */
public class SevenChoiceFiveActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.ll_base_back)
    LinearLayout llBack;
    @Bind(R.id.tv_base_mid_title)
    TextView tvMidTitle;
    @Bind(R.id.recycler_view_test)
    RecyclerView recyclerTest;
    @Bind(R.id.recycler_test_title)
    RecyclerView recyclerTitle;
    @Bind(R.id.custom_all)
    CustomScrollView customAll;
    @Bind(R.id.custom_half)
    CustomScrollView customHalf;
    @Bind(R.id.img_mid)
    ImageView imgEx;
    @Bind(R.id.tv_little_test_index)
    TextView tvOptionIndex;
    @Bind(R.id.tv_little_test_main)
    TextView tvTestType;
    @Bind(R.id.ll_base_right)
    LinearLayout llRight;
    @Bind(R.id.tv_base_right)
    TextView tvRight;
    @Bind(R.id.tv_little_test_analysis)
    TextView tvAnalysis;

    private int page, ppsId, optionIndex;//当前页码也就是第几道题 下面还有小题
    private String paperTitle;
    private String filePath, fileName;
    private List<PaperDetailsModel> listDetails = new ArrayList<>();
    private ACache aCache;
    private int[] results;
    private SevenGetFiveAdapter sevenGetFiveAdapter;
    private int paperDb, typeIndex;
    private List<PaperModelDesc> listType = new ArrayList<>();
    private boolean isHistory;

    @Override
    protected int getMainContentViewId() {
        return R.layout.mx_activity_seven_choose_five;
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
        imgEx.setTag(0);
        imgEx.setOnClickListener(this);
        llBack.setVisibility(View.VISIBLE);
        llBack.setOnClickListener(this);

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
                    if (page > 0) {
                        page--;
                        resolveResult(page);
                    } else {
                        if (typeIndex > 0) {
                            typeIndex--;
//                            ppsId--;
                            TitleUtils.moveToActivity(SevenChoiceFiveActivity.this, isHistory,
                                    listType.get(typeIndex).getPpsMainTitle(), paperDb, listType.get(typeIndex).getPpsId(),
                                    paperTitle, typeIndex, listType);
                            SevenChoiceFiveActivity.this.finish();
                        } else {
                            Toastor.showToast(SevenChoiceFiveActivity.this, "已经是第一题了");
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
                            TitleUtils.moveToActivity(SevenChoiceFiveActivity.this, isHistory,
                                    listType.get(typeIndex).getPpsMainTitle(), paperDb, listType.get(typeIndex).getPpsId(),
                                    paperTitle, typeIndex, listType);
                            SevenChoiceFiveActivity.this.finish();
                        } else {
                            Toastor.showToast(SevenChoiceFiveActivity.this, "已经是最后一题了");
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
     * 显示每一页
     *
     * @param page
     */
    private void resolveResult(final int page) {
        optionIndex = 0;
        if (page == listDetails.size() - 1 && typeIndex == listType.size() - 1 && !isHistory) {
            tvRight.setText("提交");
            llRight.setVisibility(View.VISIBLE);
        } else {
            llRight.setVisibility(View.GONE);
        }
        //当前题目详情
        PaperDetailsModel pdm = listDetails.get(page);
        //设置标题
        TestTitleAdapter tAdapter = new TestTitleAdapter(this, (page + 1) + "、" + pdm.getPsjTitle());
        recyclerTitle.setAdapter(tAdapter);
        //查询大题下面的小题
        NewExamsSqliteUtils child = new NewExamsSqliteUtils(this, filePath, fileName);
        final List<PaperDetailsModel> listPaper = child.getPaperChildrenByParentId(pdm.getPsjId() + "");
        if (listPaper.size() > 0) {
            initOptionsValue(listPaper, optionIndex);
        } else {
            tvOptionIndex.setText("暂无小题");
        }
        customHalf.setSlideListener(new CustomScrollView.SlideListener() {
            @Override
            public void moveDirection(boolean left, boolean up, boolean right, boolean down) {
                if (left) {
                    if (optionIndex > 0) {
                        optionIndex--;
                        initOptionsValue(listPaper, optionIndex);
                    } else {
                        Toastor.showToast(SevenChoiceFiveActivity.this, "已经是第一小题了");
                    }
                } else if (right) {
                    if (optionIndex < listPaper.size() - 1) {
                        optionIndex++;
                        initOptionsValue(listPaper, optionIndex);
                    } else {
                        Toastor.showToast(SevenChoiceFiveActivity.this, "已经是最后一题了");
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
        customHalf.setScrollHigh(0);
    }

    private void initOptionsValue(List<PaperDetailsModel> listPaper, final int optionIndex) {
        int[] temp = (int[]) aCache.getAsObject("ppsid_" + ppsId + "page_" + page);
        if (temp != null) {
            results = temp;
        } else {
            results = new int[listPaper.size()];
            for (int i = 0; i < results.length; i++) {
                results[i] = -1;
            }
        }

        tvOptionIndex.setText((optionIndex + 1) + "/" + listPaper.size() + "一共" + listPaper.size() + "小题," + "(手指左右滑动切换小题)");
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
        recyclerTest.setLayoutManager(new FullyLinearLayoutManager(this));
        recyclerTest.setNestedScrollingEnabled(false);
        sevenGetFiveAdapter = new SevenGetFiveAdapter(this, listPaper.get(optionIndex).getPsjOption(), results[optionIndex]);
        recyclerTest.setAdapter(sevenGetFiveAdapter);
        sevenGetFiveAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (isHistory) {
                    Toastor.showToast(SevenChoiceFiveActivity.this, "已经提交不能修改答案");
                } else {
                    results[optionIndex] = position;
                    aCache.put("ppsid_" + ppsId + "page_" + page, results);
                    sevenGetFiveAdapter.setResult(position);
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
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
                        TitleUtils.moveToActivity(SevenChoiceFiveActivity.this, isHistory,
                                listType.get(typeIndex).getPpsMainTitle(), paperDb, listType.get(typeIndex).getPpsId(),
                                paperTitle, typeIndex, listType);
                        SevenChoiceFiveActivity.this.finish();
                    } else {
                        Toastor.showToast(SevenChoiceFiveActivity.this, "已经是第一题了");
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
                        TitleUtils.moveToActivity(SevenChoiceFiveActivity.this, isHistory,
                                listType.get(typeIndex).getPpsMainTitle(), paperDb, listType.get(typeIndex).getPpsId(),
                                paperTitle, typeIndex, listType);
                        SevenChoiceFiveActivity.this.finish();
                    } else {
                        Toastor.showToast(SevenChoiceFiveActivity.this, "已经是最后一题了");
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
                        SevenChoiceFiveActivity.this.finish();
                    }

                    @Override
                    public void onFail() {

                    }
                });
                break;
        }
    }
}
