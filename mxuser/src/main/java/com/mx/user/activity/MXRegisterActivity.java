package com.mx.user.activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.os.PersistableBundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
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
 * Created by Archer on 16/8/22.
 */
public class MXRegisterActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.tv_base_mid_title)
    TextView tvMidTitle;
    @Bind(R.id.ll_base_back)
    LinearLayout llBack;
    @Bind(R.id.tv_base_back)
    TextView tvBack;
    @Bind(R.id.tv_register)
    TextView tvRegister;
    @Bind(R.id.tv_register_to_login)
    TextView tvToLogin;
    @Bind(R.id.et_register_phone)
    XEditText etPhone;
    @Bind(R.id.et_check_code)
    XEditText etCheckCode;
    @Bind(R.id.et_register_password)
    EditText etPassword;
    @Bind(R.id.et_register_confirm_password)
    EditText etConfirmPassword;
    @Bind(R.id.img_get_code)
    ImageView imgGetCode;
    @Bind(R.id.img_show_code)
    ImageView imgShowCode;
    @Bind(R.id.tv_use_protocol)
    TextView tvUseProtocol;

    private MXHttpHelper httpHelper;

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        if (msg.arg1 == 1001) {
            if (msg.what == Integer.parseInt(Constant.SUCCESS)) {
                Toastor.showToast(this, "注册成功！");
            } else {
                Toastor.showToast(this, msg.obj.toString());
            }
        } else if (msg.arg1 == 1002) {
            if (msg.what == Integer.parseInt(Constant.SUCCESS)) {
                Toastor.showToast(this, "验证码发送成功！");
            } else {
                Toastor.showToast(this, msg.obj.toString());
            }
        }
    }

    @Override
    protected int getMainContentViewId() {
        return R.layout.mx_activity_register;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        init();
    }

    /**
     * 初始化视图
     */
    private void init() {
        //初始化控件
        httpHelper = MXHttpHelper.getInstance(this);
        tvMidTitle.setText("注册");
        llBack.setVisibility(View.VISIBLE);
        tvBack.setText("登录");
        //设置监听事件
        llBack.setOnClickListener(this);
        tvRegister.setOnClickListener(this);
        tvToLogin.setOnClickListener(this);
        tvUseProtocol.setOnClickListener(this);
        imgGetCode.setOnClickListener(this);
        imgShowCode.setVisibility(View.GONE);

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
            case R.id.tv_register_to_login:
                this.finish();
                break;
            case R.id.img_get_code:
                if (DeviceUtil.isMobile(etPhone.getText().toString())) {
                    HashMap<String, String> code = new HashMap<String, String>();
                    code.put("imei", DeviceUtil.getDeviceSerial());
                    code.put("mobile", etPhone.getText().toString());
                    if (Constant.CLENT_APP.equals("haier")) {
                        code.put("source", "haier");
                    }
                    httpHelper.postStringBack(1002, Constant.GET_USER_CODE, code, getHandler(), BaseModel.class);
                } else {
                    Toastor.showToast(MXRegisterActivity.this, "请输入合法手机号");
                }
                break;
            case R.id.tv_register:
                if (etPassword.getText().toString().equals("") || etCheckCode.getText().toString().equals("") ||
                        etPhone.getText().toString().equals("") || etConfirmPassword.getText().toString().equals("")) {
                    Toastor.showToast(this, "用户名密码不能为空！");
                } else if (etPassword.getText().toString().equals(etConfirmPassword.getText().toString())) {
                    HashMap<String, String> register = new HashMap<>();
                    register.put("name", "");
                    register.put("mobile", etPhone.getText().toString());
                    register.put("code", etCheckCode.getText().toString());
                    register.put("password", etPassword.getText().toString());
                    register.put("serialNo", DeviceUtil.getDeviceSerial());
                    register.put("key", getPublicKey(this));
                    httpHelper.postStringBack(1001, Constant.USER_REGISTER, register, getHandler(), BaseModel.class);
                } else {
                    Toastor.showToast(this, "两次密码输入不正确!");
                }
                break;
            //用户协议
            case R.id.tv_use_protocol:
                startActivity(new Intent(this, UserAccessProtocolActivity.class));
                break;
            default:
                break;
        }
    }

    private String getPublicKey(Context context) {
        Uri publicKey = Uri.parse("content://com.moxi.bookstore.provider.Key/publicKey");
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(publicKey, null, null, null, null);
        String key = null;
        if (cursor != null) {
            while (cursor.moveToNext()) {
                key = cursor.getString(0);
            }
        }
        return key;
    }
}
