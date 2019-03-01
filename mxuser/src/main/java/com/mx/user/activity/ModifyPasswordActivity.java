package com.mx.user.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mx.mxbase.base.BaseActivity;
import com.mx.mxbase.constant.Constant;
import com.mx.mxbase.utils.MXUamManager;
import com.mx.mxbase.utils.Toastor;
import com.mx.user.R;
import com.mx.user.view.XEditText;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.Bind;
import okhttp3.Call;

/**
 * Created by Archer on 2016/11/30.
 */
public class ModifyPasswordActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.tv_base_back)
    TextView tvBack;
    @Bind(R.id.ll_base_back)
    LinearLayout llBack;
    @Bind(R.id.xet_old_password)
    XEditText xetOld;
    @Bind(R.id.xet_new_password)
    XEditText xetNew;
    @Bind(R.id.xet_again_new_password)
    XEditText xetAgain;
    @Bind(R.id.tv_modify_password)
    TextView tvModify;
    @Bind(R.id.img_right_pass_word)
    ImageView imgRightPsw;
    @Bind(R.id.tv_base_mid_title)
    TextView tvMidTitle;

    @Override
    protected int getMainContentViewId() {
        return R.layout.mx_activity_modify_pass_word;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        init();
    }

    /**
     * 初始化视图
     */
    private void init() {
        llBack = (LinearLayout) findViewById(R.id.ll_base_back);
        llBack.setVisibility(View.VISIBLE);
        llBack.setOnClickListener(this);
        tvBack.setText("个人中心");

        tvModify.setOnClickListener(this);
        tvMidTitle.setText("密码设置");

        xetNew.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length() < 6) {
                    imgRightPsw.setVisibility(View.GONE);
                } else {
                    imgRightPsw.setVisibility(View.VISIBLE);
                }
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
            case R.id.tv_modify_password:
                if (checkInput()) {
                    modifyPassword();
                }
                break;
        }
    }

    /**
     * 检测输入
     *
     * @return
     */
    private boolean checkInput() {
        boolean right = false;
        if (xetOld.getText().toString().equals("") || xetNew.getText().toString().equals("") || xetAgain.getText().toString().equals("")) {
            Toastor.showToast(this, "密码信息输入不完整");
            right = false;
            return right;
        } else {
            if (xetNew.getText().toString().length() >= 6) {
                if (xetNew.getText().toString().equals(xetAgain.getText().toString())) {
                    right = true;
                    return right;
                } else {
                    Toastor.showToast(this, "两次输入密码不统一");
                    right = false;
                    return right;
                }
            } else {
                Toastor.showToast(this, "密码长度应为6-12位");
                right = false;
                return right;
            }
        }
    }

    /**
     * 修改密码
     */
    private void modifyPassword() {
        HashMap<String, String> update = new HashMap<>();
        update.put("appSession", MXUamManager.queryUser(this));
        update.put("oldPwd", xetOld.getText().toString());
//        update.put("newPwd'", xetNew.getText().toString());
        OkHttpUtils.post().url(Constant.UPDATE_PASSWORD + "?newPwd=" + xetNew.getText().toString()).params(update).build().connTimeOut(1000).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                if (ModifyPasswordActivity.this.isfinish)return;
                Toastor.showToast(ModifyPasswordActivity.this, "操作失败");
            }

            @Override
            public void onResponse(String response, int id) {
                if (ModifyPasswordActivity.this.isfinish)return;
                JSONObject res = null;
                try {
                    res = new JSONObject(response);
                    int code = res.getInt("code");
                    if (code == 0) {
                        Toastor.showToast(ModifyPasswordActivity.this, "操作成功，请重新登陆");
                        share.setCache(Constant.USER_INFO, "");
                        MXUamManager.insertUam(ModifyPasswordActivity.this, Constant.MAIN_PACKAGE, "", "", "");
                        startActivity(new Intent(ModifyPasswordActivity.this, MXLoginActivity.class));
                        ModifyPasswordActivity.this.finish();
                    } else {
                        Toastor.showToast(ModifyPasswordActivity.this, "操作失败");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toastor.showToast(ModifyPasswordActivity.this, "操作失败");
                }
            }
        });
    }
}
