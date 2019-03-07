package com.mx.exams.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mx.exams.R;
import com.mx.exams.adapter.NoAdapter;
import com.mx.exams.model.ExamsDetails;
import com.mx.exams.model.ExamsDetailsModel;
import com.mx.exams.view.NoListView;
import com.mx.exams.view.SlideLinerlayout;
import com.mx.mxbase.base.BaseActivity;
import com.mx.mxbase.constant.Constant;
import com.mx.mxbase.http.MXHttpHelper;
import com.mx.mxbase.model.BaseModel;
import com.mx.mxbase.utils.GsonTools;
import com.mx.mxbase.utils.MXUamManager;
import com.mx.mxbase.utils.Toastor;
import com.mx.mxbase.view.AlertDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;

/**
 * Created by Archer on 16/9/29.
 */
public class MXWriteExamsActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.no_recycler_view)
    NoListView recyclerView;
    @Bind(R.id.slide_liner_layout)
    SlideLinerlayout slideLinerlayout;
    @Bind(R.id.tv_base_mid_title)
    TextView tvMidTitle;
    @Bind(R.id.ll_base_back)
    LinearLayout llBack;
    @Bind(R.id.ll_base_right)
    LinearLayout llRight;
    @Bind(R.id.tv_base_right)
    TextView tvRight;

    private String examsDetails = "";
    private String cob_tx_id, cob_dic_id;
    private String cob_zj_id, cos_sem_id;
    private List<ExamsDetails> listExams = new ArrayList<>();
    private ExamsDetailsModel edm;

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        if (msg.arg1 == 102) {
            if (msg.what == Integer.parseInt(Constant.SUCCESS)) {
                Toastor.showToast(this, "提交成功");
            } else {
                Toastor.showToast(this, "提交失败，请重试！");
            }
        }
    }

    @Override
    protected int getMainContentViewId() {
        return R.layout.mx_activity_write_exams;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        init();
    }

    /**
     * 初始化视图
     */
    private void init() {
        examsDetails = this.getIntent().getStringExtra("exams_details");
        cob_dic_id = this.getIntent().getStringExtra("cob_dic_id");
        cob_tx_id = this.getIntent().getStringExtra("cob_tx_id");

        cob_zj_id = this.getIntent().getStringExtra("cos_zj_id");
        cos_sem_id = this.getIntent().getStringExtra("cos_sem_id");

        tvMidTitle.setText(this.getIntent().getStringExtra("exams_details_title"));

        parseExamsDetails(examsDetails);
        llRight.setVisibility(View.VISIBLE);
        llRight.setOnClickListener(this);
        tvRight.setText("提交");
        llBack.setVisibility(View.VISIBLE);
        llBack.setOnClickListener(this);
        slideLinerlayout.setSlideListener(new SlideLinerlayout.SlideListener() {
            @Override
            public void moveDirection(boolean left, boolean up, boolean right, boolean down) {
                if (left) {
                }
                if (up) {

                }
                if (right) {

                }
                if (down) {

                }
            }

            @Override
            public void toBootom() {
                Toastor.showToast(MXWriteExamsActivity.this, "已到最后一页了！");
            }

            @Override
            public void toTop() {

            }
        });
    }

    /**
     * 解析试卷详情
     *
     * @param examsDetails
     */
    private void parseExamsDetails(String examsDetails) {
        edm = GsonTools.getPerson(examsDetails, ExamsDetailsModel.class);
        if (!cob_tx_id.equals("")) {
            if (!cob_dic_id.equals("")) {
                for (ExamsDetails ed : edm.getResult()) {
                    if (ed.getType() == 6 && ed.getDifficulty() == Integer.parseInt(cob_dic_id)) {
                        listExams.add(ed);
                    }
                }
            } else {
                for (ExamsDetails ed : edm.getResult()) {
                    if (ed.getType() == Integer.parseInt(cob_tx_id)) {
                        listExams.add(ed);
                    }
                }
            }
        } else {
            if (!cob_dic_id.equals("")) {
                for (ExamsDetails ed : edm.getResult()) {
                    if (ed.getType() == 6 && ed.getDifficulty() == Integer.parseInt(cob_dic_id)) {
                        listExams.add(ed);
                    }
                }
            } else {
                for (ExamsDetails ed : edm.getResult()) {
                    if (ed.getType() == 6) {
                        listExams.add(ed);
                    }
                }
            }
        }
        if (listExams.size() > 0) {
            recyclerView.setAdapter(new NoAdapter(this, listExams));
        } else {
            Toastor.showToast(this, "没有找到数据，请更改查询条件，或更换章节");
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
            case R.id.ll_base_right:
                new AlertDialog(this).builder().setTitle("提示").setMsg("确认提交?").setCancelable(false).setNegativeButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        submitTongbu();
                    }
                }).setPositiveButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                }).show();
                break;
            default:
                break;
        }
    }

    /**
     * 提交同步练习
     */
    private void submitTongbu() {
        HashMap<String, String> sub = new HashMap<>();
        sub.put("semId", cos_sem_id);
        sub.put("cchId", cob_zj_id);
        sub.put("appSession", MXUamManager.queryUser(this));
        MXHttpHelper.getInstance(this).postStringBack(102, Constant.SUBMIT_TB_RESULT, sub, getHandler(), BaseModel.class);
    }
}
