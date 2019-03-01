package com.moxi.nexams.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moxi.nexams.R;
import com.moxi.nexams.adapter.test.ClozeTestAdapter;
import com.moxi.nexams.adapter.test.TestTitleAdapter;
import com.moxi.nexams.cache.ACache;
import com.moxi.nexams.db.newdb.NewExamsSqliteUtils;
import com.moxi.nexams.model.papermodel.PaperDetailsModel;
import com.moxi.nexams.utils.FullyLinearLayoutManager;
import com.moxi.nexams.view.CustomScrollView;
import com.mx.mxbase.base.BaseActivity;
import com.mx.mxbase.interfaces.OnItemClickListener;
import com.mx.mxbase.utils.Toastor;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * 完形填空展示界面
 * Created by Archer on 2017/1/9.
 */
public class ClozeTestActivity extends BaseActivity implements View.OnClickListener {

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

    private ACache aCache;
    private int page, ppsId, optionIndex;
    private String paperTitle;
    private String filePath, fileName;
    private List<PaperDetailsModel> listDetails = new ArrayList<>();
    private int[] results;
    private ClozeTestAdapter clozeTestAdapter;

    @Override
    protected int getMainContentViewId() {
        return R.layout.mx_activity_cloze_test;
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
        final int paperDb = getIntent().getIntExtra("paper_index", -1);
        tvTestType.setText(getIntent().getStringExtra("test_type"));
        ppsId = getIntent().getIntExtra("pps_id", -1);
        paperTitle = this.getIntent().getStringExtra("paper_title");
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
                        Toastor.showToast(ClozeTestActivity.this, "已经是第一题了");
                    }
                } else if (right) {
                    if (page < listDetails.size() - 1) {
                        page++;
                        resolveResult(page);
                    } else {
                        Toastor.showToast(ClozeTestActivity.this, "已经是最后一题了");
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
    private void resolveResult(int page) {
        optionIndex = 0;
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
                        Toastor.showToast(ClozeTestActivity.this, "已经是第一小题了");
                    }
                } else if (right) {
                    if (optionIndex < listPaper.size() - 1) {
                        optionIndex++;
                        initOptionsValue(listPaper, optionIndex);
                    } else {
                        Toastor.showToast(ClozeTestActivity.this, "已经是最后一题了");
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

    /**
     * 初始化选项
     *
     * @param listPaper
     * @param optionIndex
     */
    private void initOptionsValue(List<PaperDetailsModel> listPaper, final int optionIndex) {
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
                results[optionIndex] = position;
                aCache.put("ppsid_" + ppsId + "page_" + page, results);
                clozeTestAdapter.setResult(position);
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
            default:
                break;
        }
    }
}
