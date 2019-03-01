package com.mx.user.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mx.mxbase.base.BaseActivity;
import com.mx.mxbase.constant.Constant;
import com.mx.mxbase.http.MXHttpHelper;
import com.mx.mxbase.model.BaseModel;
import com.mx.mxbase.utils.GsonTools;
import com.mx.mxbase.utils.MXUamManager;
import com.mx.mxbase.utils.Toastor;
import com.mx.user.R;
import com.mx.user.application.UserApplication;
import com.mx.user.model.UserInfoModel;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;

/**
 * Created by Archer on 16/8/24.
 */
public class MXEditInfomationActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.tv_base_mid_title)
    TextView tvMidTitle;
    @Bind(R.id.tv_base_back)
    TextView tvBack;
    @Bind(R.id.ll_base_back)
    LinearLayout llBack;
    @Bind(R.id.et_edit_name)
    EditText etName;
    @Bind(R.id.et_edit_sex)
    EditText etSex;
    @Bind(R.id.et_edit_email)
    EditText etEmail;
    @Bind(R.id.tv_edit_save)
    TextView tvSave;
    @Bind(R.id.tv_user_mobile)
    EditText etUserPhone;
    @Bind(R.id.et_edit_school)
    EditText etUserSchool;
    @Bind(R.id.et_edit_grade)
    EditText etUserGrade;
    @Bind(R.id.ll_edit_stu_info)
    LinearLayout llStuInfo;
    private UserInfoModel uim;

    private MXHttpHelper httpHelper;

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        if (msg.arg1 == 1001) {
            if (msg.what == Integer.parseInt(Constant.SUCCESS)) {
                UserInfoModel.UserInfo.MemberBean memberBean = uim.getResult().getMember();
                Toastor.showToast(this, "操作成功");
                if (etSex.getText().toString().equals("男")) {
                    memberBean.setSex(0);
                } else if (etSex.getText().toString().equals("女")) {
                    memberBean.setSex(1);
                } else {
                    memberBean.setSex(2);
                }
                memberBean.setName(etName.getText().toString());
                memberBean.setEmail(etEmail.getText().toString());
                memberBean.setSchool(etUserSchool.getText().toString());
                memberBean.setGrade(etUserGrade.getText().toString());
                uim.getResult().setMember(memberBean);
                share.setCache("v3_user_info_json", GsonTools.obj2json(uim));
                MXUamManager.insertUam(MXEditInfomationActivity.this, Constant.MAIN_PACKAGE, uim.getResult().getAppSession(), uim.getResult().getDdToken(), share.getString("v3_user_info_json"));
                this.finish();
            } else {
                Toastor.showToast(this, "操作失败");
            }
        }
    }

    @Override
    protected int getMainContentViewId() {
        return R.layout.mx_activity_edit_infomation;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        init();
    }

    /**
     * 初始化视图
     */
    private void init() {

        httpHelper = MXHttpHelper.getInstance(this);

        llBack.setVisibility(View.VISIBLE);
        llBack.setOnClickListener(this);
        tvBack.setText("个人中心");
        tvMidTitle.setText("编辑个人信息");
        tvSave.setOnClickListener(this);
        String temp = share.getString("v3_user_info_json");
        uim = GsonTools.getPerson(temp, UserInfoModel.class);
        etName.setText(uim.getResult().getMember().getName());
        if (uim.getResult().getMember().getSex() == 0) {
            etSex.setText("男");
        } else if (uim.getResult().getMember().getSex() == 1) {
            etSex.setText("女");
        } else {
            etSex.setText("未知");
        }
        if (UserApplication.getInstance().getFlagStu().equals("教育版")) {
            llStuInfo.setVisibility(View.VISIBLE);
        } else {
            llStuInfo.setVisibility(View.INVISIBLE);
        }
        etEmail.setText(uim.getResult().getMember().getEmail());
        etUserPhone.setText(uim.getResult().getMember().getMobile());
        etUserGrade.setText(uim.getResult().getMember().getGrade());
        etUserSchool.setText(uim.getResult().getMember().getSchool());
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
            case R.id.tv_edit_save:
                Pattern p = Pattern.compile("^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(\\.([a-zA-Z0-9_-])+)+$");
                if (!etEmail.getText().toString().equals("")) {
                    Matcher m = p.matcher(etEmail.getText().toString());
                    if (!m.matches()) {
                        Toastor.showToast(this, "邮箱不合法");
                        return;
                    }
                }
                HashMap<String, String> update = new HashMap<>();
                update.put("appSession", MXUamManager.queryUser(this));
                if (etSex.getText().toString().equals("男")) {
                    update.put("sex", "0");
                } else if (etSex.getText().toString().equals("女")) {
                    update.put("sex", "1");
                } else {
                    update.put("sex", "2");
                }
                update.put("name", etName.getText().toString());
                update.put("email", etEmail.getText().toString());
                update.put("school", etUserSchool.getText().toString());
                update.put("grade", etUserGrade.getText().toString());
                httpHelper.postStringBack(1001, Constant.UPDATE_USER_INFO, update, getHandler(), BaseModel.class);
                break;
            default:
                break;
        }
    }
}
