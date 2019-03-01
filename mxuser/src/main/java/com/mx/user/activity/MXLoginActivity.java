package com.mx.user.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mx.mxbase.base.BaseActivity;
import com.mx.mxbase.constant.APPLog;
import com.mx.mxbase.constant.Constant;
import com.mx.mxbase.http.MXHttpHelper;
import com.mx.mxbase.utils.DeviceUtil;
import com.mx.mxbase.utils.GsonTools;
import com.mx.mxbase.utils.MXUamManager;
import com.mx.mxbase.utils.SharePreferceUtil;
import com.mx.mxbase.utils.StringUtils;
import com.mx.mxbase.utils.Toastor;
import com.mx.user.R;
import com.mx.user.adapter.VerificationAdapter;
import com.mx.user.adapter.VerificationContentAdapter;
import com.mx.user.application.UserApplication;
import com.mx.user.model.NewLoginUserModel;
import com.mx.user.model.UserInfoModel;
import com.mx.user.model.v3.DDLoginInfo;
import com.mx.user.model.v3.DDPicVerification;
import com.mx.user.view.BasePopupWindow;
import com.mx.user.view.XEditText;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;

/**
 * Created by Archer on 16/8/22.
 */
public class MXLoginActivity extends BaseActivity implements View.OnClickListener {
    public static boolean isBack=false;

    @Bind(R.id.xet_username)
    XEditText xEditTextUserName;
    @Bind(R.id.xet_password)
    EditText xEditTextPassWord;
    @Bind(R.id.ll_remember_password)
    LinearLayout llRememberPsw;
    @Bind(R.id.tv_login)
    TextView tvLogin;
    @Bind(R.id.tv_find_back_password)
    TextView tvFindPsw;
    @Bind(R.id.tv_register)
    TextView tvRegister;
    @Bind(R.id.tv_base_mid_title)
    TextView tvMidTitle;
    @Bind(R.id.ll_base_back)
    LinearLayout llBack;
    @Bind(R.id.tv_base_back)
    TextView tvBack;
    @Bind(R.id.img_clear_user_name)
    ImageView imgclearUsername;
    @Bind(R.id.img_remember_password)
    ImageView imgRememberPsw;
    @Bind(R.id.dada)
    CircleImageView circleImageView;
    @Bind(R.id.login_base)
    LinearLayout llLoginBase;

    private MXHttpHelper httpHelper;
    private SharePreferceUtil share;
    private String publicId = "";
    private String flagStu;
    private LocalBroadcastReceiver localReceiver;

    @Override
    protected int getMainContentViewId() {
        return R.layout.mx_activity_login;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        init();
    }

    /**
     * 初始化view
     */
    private void init() {
        isBack=getIntent().getBooleanExtra("is_back",false);
        APPLog.e("isBack",isBack);
        localReceiver = new LocalBroadcastReceiver();
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.moxi.destroy.user.ACTION");
        localBroadcastManager.registerReceiver(localReceiver, intentFilter);
        flagStu = this.getIntent().getStringExtra("flag_version_stu");
        if (flagStu == null || flagStu.equals("")) {
            flagStu = "教育版";
        }
        UserApplication.getInstance().setFlagStu(flagStu);
        share = SharePreferceUtil.getInstance(this);
        //设置监听事件
        tvFindPsw.setOnClickListener(this);
        tvLogin.setOnClickListener(this);
        tvRegister.setOnClickListener(this);
        llRememberPsw.setOnClickListener(this);
        llBack.setOnClickListener(this);
        tvMidTitle.setText("登录");
        llBack.setVisibility(View.VISIBLE);
        tvBack.setText("返回");
        httpHelper = MXHttpHelper.getInstance(this);
        imgclearUsername.setOnClickListener(this);
        imgRememberPsw.setOnClickListener(this);
        circleImageView.setOnClickListener(this);
        if (!MXUamManager.queryUser(this).equals("")) {
            startActivity(new Intent(this, MXUserCenterActivity.class));
            this.finish();
        } else {
//            MXUamManager.insertUam(this, Constant.MAIN_PACKAGE, "", "");
        }
        xEditTextUserName.setText(share.getString("save_user_name"));
        xEditTextPassWord.setText(share.getString("save_pass_word"));
        if (!share.getBoolean("is_save_password")) {
            imgRememberPsw.setImageDrawable(this.getResources().getDrawable(R.mipmap.mx_img_check_box_normal));
        } else {
            imgRememberPsw.setImageDrawable(this.getResources().getDrawable(R.mipmap.mx_img_check_box_chosed));
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
        LocalBroadcastManager.getInstance(this).unregisterReceiver(localReceiver);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_login:
                if ("".equals(xEditTextUserName.getText().toString()) || "".equals(
                        xEditTextPassWord.getText().toString())) {
                    Toastor.showToast(this, "用户名密码不能为空!");
                } else if (Constant.CODE_CLIENT.equals("b")) {//学生b端登陆
                    doFirstLogin(xEditTextUserName.getText().toString(), xEditTextPassWord.getText().toString());
                } else {
                    doLogin(xEditTextUserName.getText().toString(), xEditTextPassWord.getText().toString());
                }
                break;
            case R.id.tv_register:
                startActivity(new Intent(this, MXRegisterActivity.class));
                break;
            //记住密码
            case R.id.ll_remember_password:
                if (share.getBoolean("is_save_password")) {
                    share.setCache("is_save_password", false);
                    imgRememberPsw.setImageDrawable(this.getResources().getDrawable(R.mipmap.mx_img_check_box_normal));
                } else {
                    share.setCache("is_save_password", true);
                    imgRememberPsw.setImageDrawable(this.getResources().getDrawable(R.mipmap.mx_img_check_box_chosed));
                }
                break;
            case R.id.tv_find_back_password:
                startActivity(new Intent(this, MXRecoveredPasswordActivity.class));
                break;
            case R.id.ll_base_back:
                onBackPressed();
//                System.exit(0);
                break;
            case R.id.dada:
                MXUamManager.queryUser(this);
                break;
            case R.id.img_clear_user_name:
                xEditTextUserName.setText("");
                break;
            default:
                break;
        }
    }

    /**
     * 登录
     *
     * @param s
     * @param s1
     */
    private void doLogin(String s, String s1) {
        dialogShowOrHide(true, "登录中...");
        HashMap<String, String> login = new HashMap<>();
        login.put("imei", DeviceUtil.getDeviceSerial());//DeviceUtil.getDeviceSerial()
        login.put("mobile", s);
        login.put("password", s1);
        if (listVerification.size() == 4) {
            login.put("code", listVerification.get(0) + listVerification.get(1) + listVerification.get(2) + listVerification.get(3));
        }
        if (Constant.CODE_CLIENT.equals("c")) {
            String ddkey = getPublicKey(MXLoginActivity.this);
            if (ddkey == null || ddkey.equals("")) {
                Toastor.showToast(MXLoginActivity.this, "没有当当书城key!");
                dialogShowOrHide(false, "登录中...");
                return;
            } else {
                login.put("key", getPublicKey(MXLoginActivity.this));
            }
        }
        OkHttpUtils.post().url(Constant.USER_LOGIN_V3).params(login).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                if (MXLoginActivity.this.isfinish)return;
                dialogShowOrHide(false, "");
                Toastor.showToast(MXLoginActivity.this, "登录失败，请检查网络！");
            }

            @Override
            public void onResponse(String response, int id) {
                if (MXLoginActivity.this.isfinish)return;
                dialogShowOrHide(false, "");
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int code = jsonObject.getInt("code");
                    if (code == 0) {
                        UserInfoModel userModel = GsonTools.getPerson(response, UserInfoModel.class);
                        share.setCache("v3_user_info_json", response);
                        if (userModel != null) {
                            share.setCache("save_user_name", xEditTextUserName.getText().toString());
                            if (share.getBoolean("is_save_password")) {
                                share.setCache("save_pass_word", xEditTextPassWord.getText().toString());
                            } else {
                                share.setCache("save_pass_word", "");
                            }
                            MXUamManager.insertUam(MXLoginActivity.this, Constant.MAIN_PACKAGE, userModel.getResult().getAppSession(), userModel.getResult().getDdToken(), share.getString("v3_user_info_json"));
                            startActivity(new Intent(MXLoginActivity.this, MXUserCenterActivity.class));
                            dialogShowOrHide(false, "登录中...");
                            if (basePopupWindow != null) {
                                basePopupWindow.dismiss();
                                basePopupWindow = null;
                            }
                        }
                        listVerification.clear();
                        MXLoginActivity.this.finish();
                    } else {
                        DDLoginInfo ddLoginInfo = GsonTools.getPerson(response, DDLoginInfo.class);
                        if (ddLoginInfo.getResult() != null) {
                            showVerificationPop(ddLoginInfo.getResult().getCodes(), ddLoginInfo.getResult().getImg());
                        } else {
                            Toastor.showToast(MXLoginActivity.this, ddLoginInfo.getMsg());
                            listVerification.clear();
                            if (verificationAdapter != null) {
                                verificationAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private List<String> listVerification = new ArrayList<>();
    private VerificationAdapter verificationAdapter;
    private BasePopupWindow basePopupWindow;
    private VerificationContentAdapter verificationContentAdapter;
    private List<String> listStr;
    private ImageView imageView;

    /**
     * 设置显示验证码相关信息
     *
     * @param verification
     * @param image
     */
    private void showVerificationPop(String verification, String image) {
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
                        if (MXLoginActivity.this.isfinish)return;
                        Toastor.showToast(MXLoginActivity.this, "请求失败！");
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        if (MXLoginActivity.this.isfinish)return;
                        try {
                            DDPicVerification ddPicVerification = GsonTools.getPerson(response, DDPicVerification.class);
                            if (ddPicVerification != null) {
                                if (ddPicVerification.getCode() == 0) {
                                    setValueImgAndVerificationCode(ddPicVerification.getResult().getImg(), ddPicVerification.getResult().getCodes());
                                    verificationContentAdapter.dataChange(listStr);
                                } else {
                                    Toastor.showToast(MXLoginActivity.this, ddPicVerification.getMsg());
                                }
                            }
                        }catch ( Exception e){

                        }

                    }
                });
            }
        });

        verificationAdapter = new VerificationAdapter(this, listVerification, gridViewCode);
        gridViewCode.setAdapter(verificationAdapter);

        setValueImgAndVerificationCode(image, verification);
        verificationContentAdapter = new VerificationContentAdapter(MXLoginActivity.this, listStr);
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
                                if (basePopupWindow != null) {
                                    basePopupWindow.dismiss();
                                    basePopupWindow = null;
                                }
                                doLogin(xEditTextUserName.getText().toString(), xEditTextPassWord.getText().toString());
                            } else {
                                Toastor.showToast(MXLoginActivity.this, "请输入完整的验证码");
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
                            doLogin(xEditTextUserName.getText().toString(), xEditTextPassWord.getText().toString());
                        }
                    }
                }
            }
        });

        basePopupWindow.setContentView(contentView);
        basePopupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        basePopupWindow.setOutsideTouchable(false);
        basePopupWindow.setFocusable(true);
        basePopupWindow.showAtLocation(llLoginBase, Gravity.BOTTOM, 0, 0);

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

    private void doFirstLogin(String userName, String passWord) {
        OkHttpUtils.post().url(Constant.HTTP_FIRST + "/app/user/login?type=" + Constant.LOGIN_TYPE).addParams("mobile", userName).addParams("password", passWord).
                build().connTimeOut(10000).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                if (MXLoginActivity.this.isfinish)return;
                Toastor.showToast(MXLoginActivity.this, "登录失败");
            }

            @Override
            public void onResponse(String response, int id) {
                if (MXLoginActivity.this.isfinish)return;
                try {
                    NewLoginUserModel newLoginUserModel = GsonTools.getPerson(response, NewLoginUserModel.class);
                    if (newLoginUserModel != null) {
                        if (newLoginUserModel.getCode() == 0) {
                            share.setCache("user_info_json", response);
                            String ddkey = getPublicKey(MXLoginActivity.this);
                            dialogShowOrHide(true, "登录中...");
                            HashMap<String, String> login = new HashMap<>();
                            login.put("deviceNo", DeviceUtil.getDeviceSerial());
                            publicId = newLoginUserModel.getResult().getId() + "";
                            if (publicId.equals("")) {
                                Toastor.showToast(MXLoginActivity.this, "未获取到用户id");
                                return;
                            }
                            login.put("userId", publicId);
                            if (ddkey != null) {
                                login.put("ddKey", ddkey);
                            }
                            httpHelper.postStringBack(1001, Constant.B_LOGIN, login, getHandler(), UserInfoModel.class);
                        } else {
                            Toastor.showToast(MXLoginActivity.this, newLoginUserModel.getMsg());
                        }
                    }
                }catch (Exception e){

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
     * 点击其它地方关闭软键盘
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            // 获得当前得到焦点的View，一般情况下就是EditText（特殊情况就是轨迹求或者实体案件会移动焦点）
            View v = getCurrentFocus();
            if (StringUtils.isShouldHideInput(v, ev)) {
                StringUtils.closeIMM(this, v.getWindowToken());
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    public class LocalBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case "com.moxi.destroy.user.ACTION":
                    MXLoginActivity.this.finish();
                    break;
                default:
                    break;
            }
        }
    }
}
