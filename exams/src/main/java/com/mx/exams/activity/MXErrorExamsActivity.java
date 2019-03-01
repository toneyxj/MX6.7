package com.mx.exams.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mx.exams.R;
import com.mx.exams.adapter.ResultGridAdapter;
import com.mx.exams.cache.ACache;
import com.mx.exams.db.SQLBookUtil;
import com.mx.exams.db.SQLUtil;
import com.mx.exams.db.TestSqlUtil;
import com.mx.exams.model.ChoseExamsModel;
import com.mx.exams.model.ExamsDetails;
import com.mx.exams.model.ExamsDetailsModel;
import com.mx.mxbase.base.BaseActivity;
import com.mx.mxbase.utils.GsonTools;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * 错题集界面
 * Created by Archer on 16/10/14.
 */
public class MXErrorExamsActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.ll_base_back)
    LinearLayout llBack;
    @Bind(R.id.tv_base_mid_title)
    TextView tvMidTitle;
    @Bind(R.id.recycler_reply_result)
    GridView recyclerview;
    @Bind(R.id.tv_exams_details_title)
    TextView tvExamsTitle;
    @Bind(R.id.tv_subjective_right)
    TextView tvSubRight;
    @Bind(R.id.recycler_reply_subject_result)
    GridView recyclerSubView;
    @Bind(R.id.tv_chose_title)
    TextView tvChoseTitle;
    @Bind(R.id.tv_subjective_title)
    TextView tvSubTitle;

    private ResultGridAdapter adapter, bAdapter;
    private ExamsDetailsModel edm;
    public static int page = -1;
    private int examsId;
    private String papId;

    private List<ExamsDetails> examsDetails = new ArrayList<>();
    public String examsTitle;
    private String response;

    @Override
    protected int getMainContentViewId() {
        return R.layout.mx_activity_error_exams;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        init();
    }

    /**
     *
     */
    private void init() {
        tvMidTitle.setText("错题详情");
        examsTitle = getIntent().getStringExtra("cob_exams_title");
        tvExamsTitle.setText(examsTitle);
        llBack.setVisibility(View.VISIBLE);
        llBack.setOnClickListener(this);
        papId = getIntent().getStringExtra("exams_pap_id");
        if (papId == null) {
            examsId = getIntent().getIntExtra("cob_zj_id", -1);
            final String bookId = SQLUtil.getInstance(this).getBookIdByCchId(examsId + "");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            response = SQLBookUtil.getInstance(getApplicationContext()).getExamsDetails(bookId, examsId + "");
                            parseExams(response);
                        }
                    });
                }
            }).start();
        } else {
            parePaperDetails(papId, examsTitle);
        }
    }

    /**
     * 查询试卷详情
     *
     * @param papId
     * @param examsTitle 试卷标题
     */
    private void parePaperDetails(final String papId, final String examsTitle) {
        tvMidTitle.setText(examsTitle);
        examsDetails = TestSqlUtil.getInstance(this).getExamsDetails(papId);
        final List<ChoseExamsModel> listChoseData = new ArrayList<>();
        final List<ChoseExamsModel> listData = new ArrayList<>();
        if (examsDetails.size() > 0) {
            listChoseData.clear();
            listData.clear();
            int total = 0;
            int right = 0;
            for (int i = 0; i < examsDetails.size(); i++) {
                if (examsDetails.get(i).getType() == 6 || examsDetails.get(i).getType() == 18) {
                    ChoseExamsModel cem = new ChoseExamsModel();
                    cem.setIndex(i);
                    cem.setResultKey("papId" + papId);
                    cem.setExamsDetails(examsDetails.get(i));
                    total++;
                    listChoseData.add(cem);
                    String resultKey = "papId" + papId + i;
                    String tempResult = ACache.get(this).getAsString(resultKey);
                    if (tempResult != null) {
                        try {
                            if (tempResult.equals(examsDetails.get(i).getAnswer())) {
                                right++;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    ChoseExamsModel cem = new ChoseExamsModel();
                    cem.setIndex(i);
                    cem.setExamsDetails(examsDetails.get(i));
                    cem.setResultKey("papId" + papId);
                    listData.add(cem);
                }
            }
            NumberFormat numberFormat = NumberFormat.getInstance();
            numberFormat.setMaximumFractionDigits(2);
            if (total != 0) {
                String result = numberFormat.format((float) right / (float) total * 100);
                tvSubRight.setText(result + "%");
            } else {
                tvSubRight.setText(0 + "%");
            }
            adapter = new ResultGridAdapter(this, listChoseData);
            recyclerview.setAdapter(adapter);
            recyclerview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    Intent intent = new Intent();
                    intent.setClass(MXErrorExamsActivity.this, ExamsTestActivity.class);
                    intent.putExtra("test_pap_id", papId);
                    intent.putExtra("test_pap_name", examsTitle);
                    intent.putExtra("test_pap_cprId", 0 + "");
                    page = listChoseData.get(position).getIndex();
                    startActivity(intent);
                    MXErrorExamsActivity.this.finish();
                }
            });

            bAdapter = new ResultGridAdapter(this, listData);
            recyclerSubView.setAdapter(bAdapter);
            recyclerSubView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    Intent intent = new Intent();
                    intent.setClass(MXErrorExamsActivity.this, ExamsTestActivity.class);
                    intent.putExtra("test_pap_id", papId);
                    intent.putExtra("test_pap_name", examsTitle);
                    intent.putExtra("test_pap_cprId", 0 + "");
                    page = listData.get(position).getIndex();
                    startActivity(intent);
                    MXErrorExamsActivity.this.finish();
                }
            });
        }

    }


    /**
     * 解析错题数据
     *
     * @param response exams详情
     */
    private void parseExams(String response) {
        final List<ChoseExamsModel> listChoseData = new ArrayList<>();
        final List<ChoseExamsModel> listData = new ArrayList<>();
        edm = GsonTools.getPerson(response, ExamsDetailsModel.class);
        examsDetails = edm.getResult();
        if (edm.getResult().size() > 0) {
            listChoseData.clear();
            listData.clear();
            int total = 0;
            int right = 0;
            for (int i = 0; i < examsDetails.size(); i++) {
                if (examsDetails.get(i).getType() == 6 || examsDetails.get(i).getType() == 18) {
                    ChoseExamsModel cem = new ChoseExamsModel();
                    cem.setIndex(i);
                    cem.setExamsDetails(examsDetails.get(i));
                    cem.setResultKey("cchId" + examsId);
                    total++;
                    listChoseData.add(cem);
                    String resultKey = "cchId" + examsId + i;
                    String tempResult = ACache.get(this).getAsString(resultKey);
                    if (tempResult != null) {
                        try {
                            if (tempResult.equals(examsDetails.get(i).getAnswer())) {
                                right++;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    ChoseExamsModel cem = new ChoseExamsModel();
                    cem.setIndex(i);
                    cem.setResultKey("cchId" + examsId);
                    cem.setExamsDetails(examsDetails.get(i));
                    listData.add(cem);
                }
            }
            NumberFormat numberFormat = NumberFormat.getInstance();
            numberFormat.setMaximumFractionDigits(2);
            if (total != 0) {
                String result = numberFormat.format((float) right / (float) total * 100);
                tvSubRight.setText(result + "%");
            } else {
                tvSubRight.setText(0 + "%");
            }

            if (listChoseData.size() > 0) {
                tvChoseTitle.setVisibility(View.VISIBLE);
            } else {
                tvChoseTitle.setVisibility(View.GONE);
            }
            adapter = new ResultGridAdapter(this, listChoseData);
            recyclerview.setAdapter(adapter);
            recyclerview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    Intent intent = new Intent();
                    intent.setClass(MXErrorExamsActivity.this, MXWriteHomeWorkActivity.class);
                    intent.putExtra("mid_title", examsTitle);
                    intent.putExtra("cch_id", examsId);
                    page = listChoseData.get(position).getIndex();
                    startActivity(intent);
                    MXErrorExamsActivity.this.finish();
                }
            });

            if (listData.size() > 0) {
                tvSubTitle.setVisibility(View.VISIBLE);
            } else {
                tvSubTitle.setVisibility(View.GONE);
            }

            bAdapter = new ResultGridAdapter(this, listData);
            recyclerSubView.setAdapter(bAdapter);
            recyclerSubView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    Intent intent = new Intent();
                    intent.setClass(MXErrorExamsActivity.this, MXWriteHomeWorkActivity.class);
                    intent.putExtra("mid_title", examsTitle);
                    intent.putExtra("cch_id", examsId);
                    page = listData.get(position).getIndex();
                    startActivity(intent);
                    MXErrorExamsActivity.this.finish();
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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_base_back:
                this.finish();
                break;
        }
    }
}
