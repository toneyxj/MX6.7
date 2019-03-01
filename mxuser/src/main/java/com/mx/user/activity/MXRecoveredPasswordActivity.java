package com.mx.user.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.PersistableBundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mx.mxbase.base.BaseActivity;
import com.mx.mxbase.constant.Constant;
import com.mx.mxbase.http.MXHttpHelper;
import com.mx.mxbase.model.BaseModel;
import com.mx.mxbase.utils.DeviceUtil;
import com.mx.mxbase.utils.Toastor;
import com.mx.user.R;
import com.mx.user.view.XEditText;

import java.util.HashMap;

import butterknife.Bind;

/**
 * Created by Archer on 16/10/19.
 */
public class MXRecoveredPasswordActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.ll_base_back)
    LinearLayout llBack;
    @Bind(R.id.tv_base_back)
    TextView tvBack;
    @Bind(R.id.tv_base_mid_title)
    TextView tvMidTitle;
    @Bind(R.id.img_get_code)
    ImageView imgGetCode;
    @Bind(R.id.et_register_phone)
    XEditText etPhone;
    @Bind(R.id.img_show_code)
    ImageView imgShowCode;
    @Bind(R.id.et_check_code)
    XEditText etCheckCode;
    @Bind(R.id.tv_time_count)
    TextView tvTimeCount;
    @Bind(R.id.tv_next_step)
    TextView tvNextStep;
    @Bind(R.id.ll_input_password)
    LinearLayout llInputPsw;
    @Bind(R.id.ll_get_code)
    LinearLayout llGetCode;
    @Bind(R.id.et_pass_word)
    XEditText etPassWord;
    @Bind(R.id.et_again_pass_word)
    XEditText etAgainPsw;
    @Bind(R.id.img_show_pass_word)
    ImageView imgShowPass;

    private int time = 60;
    private MXHttpHelper httpHelper;
    private RetrieveModel rm;

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        if (msg.arg1 == 1002) {
            if (msg.what == Integer.parseInt(Constant.SUCCESS)) {
                time = 60;
                getHandler().postDelayed(runnable, 1000);
                imgGetCode.setVisibility(View.GONE);
                tvTimeCount.setVisibility(View.VISIBLE);
            } else {
            }
        } else if (msg.arg1 == 1003) {
            if (msg.what == Integer.parseInt(Constant.SUCCESS)) {
                rm = (RetrieveModel) msg.obj;
                if (rm != null) {
                    llGetCode.setVisibility(View.GONE);
                    llInputPsw.setVisibility(View.VISIBLE);
                    tvNextStep.setText("确认修改");
                    tvBack.setText("找回密码");
                    tvMidTitle.setText("修改密码");
                }
                Toastor.showToast(this, "校验成功");
            } else {
                Toastor.showToast(this, msg.obj.toString());
            }
        } else if (msg.arg1 == 1004) {
            if (msg.what == Integer.parseInt(Constant.SUCCESS)) {
                startActivity(new Intent(this, MXLoginActivity.class));
                this.finish();
                Toastor.showToast(this, "修改成功,请重新登录");
            } else {
                Toastor.showToast(this, msg.obj.toString());
            }
        }
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (time > 0) {
                time--;
                tvTimeCount.setText("" + time + "s");
                getHandler().postDelayed(this, 1000);
            } else {
                imgGetCode.setVisibility(View.VISIBLE);
                tvTimeCount.setVisibility(View.GONE);
            }
        }
    };

    @Override
    protected int getMainContentViewId() {
        return R.layout.mx_activity_recovered_password;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        init();
    }

    /**
     * 初始化view
     */
    private void init() {

        httpHelper = MXHttpHelper.getInstance(this);

        llBack.setVisibility(View.VISIBLE);
        tvBack.setText("登录");
        tvMidTitle.setText("找回密码");
        imgShowCode.setVisibility(View.GONE);
        imgShowPass.setVisibility(View.GONE);
        imgGetCode.setVisibility(View.VISIBLE);
        tvTimeCount.setVisibility(View.GONE);
        llGetCode.setVisibility(View.VISIBLE);
        llInputPsw.setVisibility(View.GONE);

        llBack.setOnClickListener(this);
        imgGetCode.setOnClickListener(this);
        tvNextStep.setOnClickListener(this);

        etCheckCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().equals("")) {
                    if (charSequence.toString().length() == 6) {
                        imgShowCode.setVisibility(View.VISIBLE);
                    } else {
                        imgShowCode.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        etAgainPsw.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().equals("")) {
                    if (etAgainPsw.getText().toString().equals(etPassWord.getText().toString())) {
                        imgShowPass.setVisibility(View.VISIBLE);
                    } else {
                        imgShowPass.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

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
    public void onBackPressed() {
        if (tvNextStep.getText().toString().equals("下一步")) {
            this.finish();
        } else {
            tvBack.setText("登录");
            tvMidTitle.setText("找回密码");
            llGetCode.setVisibility(View.VISIBLE);
            llInputPsw.setVisibility(View.GONE);
            tvNextStep.setText("下一步");
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_base_back:
                if (tvNextStep.getText().toString().equals("下一步")) {
                    this.finish();
                } else {
                    tvBack.setText("登录");
                    tvMidTitle.setText("找回密码");
                    llGetCode.setVisibility(View.VISIBLE);
                    llInputPsw.setVisibility(View.GONE);
                    tvNextStep.setText("下一步");
                }
                break;
            case R.id.img_get_code:
                if (DeviceUtil.isMobile(etPhone.getText().toString())) {
                    HashMap<String, String> code = new HashMap<String, String>();
                    code.put("imei", DeviceUtil.getDeviceSerial());
                    code.put("mobile", etPhone.getText().toString());
//                    if (AppUtil.getPackageInfo(this).applicationInfo.packageName.equals("com.moxi.user")) {
                    code.put("source", Constant.CLENT_APP);
//                    }
                    code.put("type", "1");
                    httpHelper.postStringBack(1002, Constant.GET_USER_CODE, code, getHandler(), BaseModel.class);
                } else {
                    Toastor.showToast(this, "请输入合法手机号");
                }
                break;
            case R.id.tv_next_step:
                if (tvNextStep.getText().toString().equals("下一步")) {
                    if (etCheckCode.getText().length() == 6) {
                        HashMap<String, String> code = new HashMap<String, String>();
                        code.put("code", etCheckCode.getText().toString());
                        code.put("mobile", etPhone.getText().toString());
                        httpHelper.postStringBack(1003, Constant.RETRIEVE_CODE, code, getHandler(), RetrieveModel.class);
                    } else {
                        Toastor.showToast(this, "请输入合法的验证码再试!");
                    }
                } else {
                    if (etAgainPsw.getText().toString().equals(etPassWord.getText().toString())) {
                        HashMap<String, String> code = new HashMap<String, String>();
                        code.put("token", rm.getResult().getToken());
                        code.put("mobile", rm.getResult().getMobile());
                        code.put("newPassword", etAgainPsw.getText().toString());
                        httpHelper.postStringBack(1004, Constant.MODIFAY_PASSWORD, code, getHandler(), BaseModel.class);
                    } else {
                        Toastor.showToast(this, "两次输入密码不一致，请重新输入后再试");
                    }
                }
                break;
            default:
                break;
        }
    }

    public class RetrieveModel extends BaseModel {
        private Retrieve result;

        public Retrieve getResult() {
            return result;
        }

        public void setResult(Retrieve result) {
            this.result = result;
        }

        public class Retrieve {
            private String token;
            private String mobile;

            public String getToken() {
                return token;
            }

            public void setToken(String token) {
                this.token = token;
            }

            public String getMobile() {
                return mobile;
            }

            public void setMobile(String mobile) {
                this.mobile = mobile;
            }
        }
    }
}
