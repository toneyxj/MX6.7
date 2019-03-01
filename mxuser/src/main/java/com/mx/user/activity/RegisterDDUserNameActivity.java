package com.mx.user.activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PersistableBundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mx.mxbase.base.BaseActivity;
import com.mx.mxbase.constant.Constant;
import com.mx.mxbase.utils.ActivitysManager;
import com.mx.mxbase.utils.DeviceUtil;
import com.mx.mxbase.utils.GsonTools;
import com.mx.mxbase.utils.MXUamManager;
import com.mx.mxbase.utils.Toastor;
import com.mx.mxbase.view.AlertDialog;
import com.mx.user.R;
import com.mx.user.adapter.VerificationAdapter;
import com.mx.user.adapter.VerificationContentAdapter;
import com.mx.user.model.UserInfoModel;
import com.mx.user.model.v3.DDPicVerification;
import com.mx.user.view.BasePopupWindow;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import okhttp3.Call;

/**
 * 注册当当账号
 * Created by King on 2017/12/5.
 */

public class RegisterDDUserNameActivity extends BaseActivity implements View.OnClickListener {
    @Bind(R.id.tv_base_mid_title)
    TextView tvMidTitle;
    @Bind(R.id.ll_base_back)
    LinearLayout llBack;
    @Bind(R.id.et_dd_register_phone)
    EditText etPhone;
    @Bind(R.id.et_dd_check_code)
    EditText etCode;
    @Bind(R.id.et_dd_register_password)
    EditText etPsw;
    @Bind(R.id.et_dd_register_confirm_password)
    EditText etConPsw;
    @Bind(R.id.tv_dd_register)
    TextView tvDdRegister;
    @Bind(R.id.img_dd_get_code)
    ImageView imgGetCode;
    @Bind(R.id.tv_register_code_count)
    TextView tvCount;
    @Bind(R.id.ll_dd_register_username)
    LinearLayout llRegisterBase;

    private List<String> listStr;
    private ImageView imageView;
    private List<String> listVerification = new ArrayList<>();
    private VerificationAdapter verificationAdapter;
    private BasePopupWindow basePopupWindow;
    private VerificationContentAdapter verificationContentAdapter;

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        tvMidTitle.setText("当当注册");
        llBack.setVisibility(View.VISIBLE);
        llBack.setOnClickListener(this);
        tvDdRegister.setOnClickListener(this);
        imgGetCode.setOnClickListener(this);
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
        if (timer != null) {
            timer.cancel();
        }
    }

    @Override
    protected int getMainContentViewId() {
        return R.layout.mx_activity_register_dd_username;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_base_back:
                this.finish();
                break;
            case R.id.img_dd_get_code:
                if (DeviceUtil.isMobile(etPhone.getText().toString())) {
                    HashMap<String, String> getCode = new HashMap<>();
                    getCode.put("phoneNum", etPhone.getText().toString());
                    getCode.put("imei", DeviceUtil.getDeviceSerial().equals("unknown") ? "JS004J00T004BH1E0057" : DeviceUtil.getDeviceSerial());
                    OkHttpUtils.post().url(Constant.registerCodeUrl).params(getCode).build().connTimeOut(10000).execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            if (RegisterDDUserNameActivity.this.isfinish)return;
                            Toastor.showToast(RegisterDDUserNameActivity.this, "请检查网络连接！");
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            if (RegisterDDUserNameActivity.this.isfinish)return;
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                int code = jsonObject.getInt("code");
                                String msg = jsonObject.getString("msg");
                                if (code == 0) {
                                    startCountDownTime(1000);
                                    tvCount.setVisibility(View.VISIBLE);
                                    imgGetCode.setVisibility(View.GONE);
                                } else {
                                    Toastor.showToast(RegisterDDUserNameActivity.this, msg);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } else {
                    Toastor.showToast(RegisterDDUserNameActivity.this, "请输入合法手机号");
                }
                break;
            case R.id.tv_dd_register:
                if (etPhone.getText().toString().equals("") || etPsw.getText().toString().equals("") ||
                        etPhone.getText().toString().equals("") || etConPsw.getText().toString().equals("")
                        || etCode.getText().toString().equals("")) {
                    Toastor.showToast(this, "用户名密码不能为空！");
                } else if (etPsw.getText().toString().equals(etConPsw.getText().toString())) {
                    HashMap<String, String> register = new HashMap<>();
                    register.put("dUserName", etPhone.getText().toString());
                    register.put("dPassword", etPsw.getText().toString());
                    register.put("imei", DeviceUtil.getDeviceSerial().equals("unknown") ? "JS004J00T004BH1E0057" : DeviceUtil.getDeviceSerial());
                    register.put("dCode", etCode.getText().toString());
                    OkHttpUtils.post().url(Constant.registerDDUser).params(register).build().connTimeOut(10000).execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {

                        }

                        @Override
                        public void onResponse(String response, int id) {
                            if (RegisterDDUserNameActivity.this.isfinish)return;
                            JSONObject jsonObject = null;
                            try {
                                jsonObject = new JSONObject(response);
                                int code = jsonObject.getInt("code");
                                String msg = jsonObject.getString("msg");
                                if (code == 0) {
                                    new AlertDialog(RegisterDDUserNameActivity.this).builder().setTitle("提示").setCancelable(false).setMsg("" +
                                            "注册成功！是否现在绑定当当账号？").setNegativeButton("确定", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            OkHttpUtils.post().url(Constant.GET_DD_PIC_VERIFICATION).addParams("imei", DeviceUtil.getDeviceSerial().equals("unknown") ? "JS004J00T004BH1E0057" : DeviceUtil.getDeviceSerial()).build().execute(new StringCallback() {
                                                @Override
                                                public void onError(Call call, Exception e, int id) {
                                                    if (RegisterDDUserNameActivity.this.isfinish)return;
                                                    Toastor.showToast(RegisterDDUserNameActivity.this, "请求失败！");
                                                }

                                                @Override
                                                public void onResponse(String response, int id) {
                                                    if (RegisterDDUserNameActivity.this.isfinish)return;
                                                    try {
                                                        DDPicVerification ddPicVerification = GsonTools.getPerson(response, DDPicVerification.class);
                                                        if (ddPicVerification != null) {
                                                            if (ddPicVerification.getCode() == 0) {
                                                                showVerificationPop(ddPicVerification.getResult().getCodes(), ddPicVerification.getResult().getImg());
                                                                setValueImgAndVerificationCode(ddPicVerification.getResult().getImg(), ddPicVerification.getResult().getCodes());
                                                                verificationContentAdapter.dataChange(listStr);
                                                            } else {
                                                                Toastor.showToast(RegisterDDUserNameActivity.this, ddPicVerification.getMsg());
                                                            }
                                                        }
                                                    }catch (Exception e){}

                                                }
                                            });
                                        }
                                    }).setPositiveButton("取消", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                        }
                                    }).show();
                                } else {
                                    Toastor.showToast(RegisterDDUserNameActivity.this, msg);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } else {
                    Toastor.showToast(this, "两次密码输入不正确!");
                }
                break;
            default:
                break;
        }
    }

    private void showVerificationPop(String codes, String img) {
        GridView gridViewContent, gridViewCode;
        FrameLayout frameLayout;
        if (basePopupWindow == null) {
            basePopupWindow = new BasePopupWindow(this);
        }

        View contentView = LayoutInflater.from(this).inflate(R.layout.mx_popwindow_veritry_code, null);
        imageView = (ImageView) contentView.findViewById(R.id.img_verification_code);
        gridViewContent = (GridView) contentView.findViewById(R.id.gv_verification_content);
        gridViewCode = (GridView) contentView.findViewById(R.id.mx_verification_code);
        frameLayout = (FrameLayout) contentView.findViewById(R.id.fl_retry_verification);

        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listVerification.clear();
                OkHttpUtils.post().url(Constant.GET_DD_PIC_VERIFICATION).addParams("imei", DeviceUtil.getDeviceSerial()).build().execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        if (RegisterDDUserNameActivity.this.isfinish)return;
                        Toastor.showToast(RegisterDDUserNameActivity.this, "请求失败！");
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        if (RegisterDDUserNameActivity.this.isfinish)return;
                        try {
                            DDPicVerification ddPicVerification = GsonTools.getPerson(response, DDPicVerification.class);
                            if (ddPicVerification != null) {
                                if (ddPicVerification.getCode() == 0) {
                                    setValueImgAndVerificationCode(ddPicVerification.getResult().getImg(), ddPicVerification.getResult().getCodes());
                                    verificationContentAdapter.dataChange(listStr);
                                } else {
                                    Toastor.showToast(RegisterDDUserNameActivity.this, ddPicVerification.getMsg());
                                }
                            }
                        }catch ( Exception e){}

                    }
                });
            }
        });

        verificationAdapter = new VerificationAdapter(this, listVerification, gridViewCode);
        gridViewCode.setAdapter(verificationAdapter);

        setValueImgAndVerificationCode(img, codes);
        verificationContentAdapter = new VerificationContentAdapter(RegisterDDUserNameActivity.this, listStr);
        gridViewContent.setAdapter(verificationContentAdapter);
        gridViewContent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position % 5 == 4) {
                    switch (position) {
                        case 4:
                            if (listVerification.size() > 0) {
                                listVerification.remove(listVerification.size() - 1);
                                verificationAdapter.notifyDataSetChanged();
                            }
                            break;
                        case 9:
                            listVerification.clear();
                            verificationAdapter.notifyDataSetChanged();
                            break;
                        case 14:
                            if (listVerification.size() == 4) {
                                dialogShowOrHide(true, "请稍后");
                                doBindDDUser(listVerification);
                            } else {
                                Toastor.showToast(RegisterDDUserNameActivity.this, "请输入完整的验证码");
                            }
                            break;
                        default:
                            break;
                    }
                } else {
                    if (listVerification.size() < 4) {
                        listVerification.add(listStr.get(position));
                        verificationAdapter.notifyDataSetChanged();
                        if (listVerification.size() == 4) {
                            dialogShowOrHide(true, "请稍后");
                            doBindDDUser(listVerification);
                        }
                    }
                }
            }
        });

        basePopupWindow.setContentView(contentView);
        basePopupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        basePopupWindow.setOutsideTouchable(false);
        basePopupWindow.setFocusable(true);
        basePopupWindow.showAtLocation(llRegisterBase, Gravity.BOTTOM, 0, 0);

        contentView.findViewById(R.id.img_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (basePopupWindow != null) {
                    basePopupWindow.dismiss();
                    listVerification.clear();
                    basePopupWindow = null;
                }
            }
        });
    }

    /**
     * 绑定当当账号
     *
     * @param code
     */
    private void doBindDDUser(List<String> code) {
        String temp = share.getString("v3_user_info_json");
        UserInfoModel userInfoModel = GsonTools.getPerson(temp, UserInfoModel.class);
        if (userInfoModel == null) {
            Toastor.showToast(this, "请先在登录界面登录之后再绑定当当账号！");
            return;
        }
        HashMap<String, String> bindUser = new HashMap<>();
        bindUser.put("appSession", userInfoModel.getResult().getAppSession());
        bindUser.put("dUserName", etPhone.getText().toString());
        bindUser.put("dPassword", etPsw.getText().toString());
        bindUser.put("dCode", code.get(0) + code.get(1) + code.get(2) + code.get(3));
        bindUser.put("pdfKey", getPublicKey(this));
        bindUser.put("imei", DeviceUtil.getDeviceSerial().equals("unknown") ? "JS004J00T004BH1E0057" : DeviceUtil.getDeviceSerial());
        OkHttpUtils.post().url(Constant.bindDDUser).params(bindUser).build().connTimeOut(10000).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                if (RegisterDDUserNameActivity.this.isfinish)return;
                dialogShowOrHide(false, "登录中...");
            }

            @Override
            public void onResponse(String response, int id) {
                if (RegisterDDUserNameActivity.this.isfinish)return;
                dialogShowOrHide(false, "登录中...");
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int code = jsonObject.getInt("code");
                    if (code == 0) {
                        UserInfoModel userModel = GsonTools.getPerson(response, UserInfoModel.class);
                        share.setCache("v3_user_info_json", response);
                        if (userModel != null) {
                            MXUamManager.insertUam(RegisterDDUserNameActivity.this, Constant.MAIN_PACKAGE, userModel.getResult().getAppSession(), userModel.getResult().getDdToken(), share.getString("v3_user_info_json"));
                           if (!MXLoginActivity.isBack) {
                               Intent bind = new Intent("com.moxi.bind.dd.user.ACTION");
                               bind.putExtra("action_type", "bind");
                               LocalBroadcastManager.getInstance(RegisterDDUserNameActivity.this).sendBroadcast(bind);
                               startActivity(new Intent(RegisterDDUserNameActivity.this, MXUserCenterActivity.class));
                           }else {
                               MXLoginActivity.isBack=false;
                               ActivitysManager.getAppManager().finishAllActivity();
                           }
                            RegisterDDUserNameActivity.this.finish();
                        }
                        listVerification.clear();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 获取当当token
     *
     * @param context
     * @return
     */
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

    /**
     * 设置验证码相关信息
     *
     * @param img
     * @param codes
     */
    private void setValueImgAndVerificationCode(String img, String codes) {
        byte[] bytes = Base64.decode(img, Base64.DEFAULT);
        imageView.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
        String[] tempStr = codes.split(",");
        listStr = new ArrayList<>(Arrays.asList(tempStr));
        listStr.add(4, "删除");
        listStr.add(9, "清除");
        listStr.add(14, "确定");
    }

    CountDownTimer timer;
    int startTime;

    private void startCountDownTime(long time) {
        startTime = 60;
        timer = new CountDownTimer(time * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (startTime > 0) {
                    startTime--;
                    tvCount.setText(startTime + "s");
                } else {
                    timer.onFinish();
                }
            }

            @Override
            public void onFinish() {
                timer.cancel();
                tvCount.setVisibility(View.GONE);
                imgGetCode.setVisibility(View.VISIBLE);
            }
        };
        timer.start();
    }
}
