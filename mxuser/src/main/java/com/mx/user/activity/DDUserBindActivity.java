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
import android.os.PersistableBundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mx.mxbase.base.BaseActivity;
import com.mx.mxbase.constant.Constant;
import com.mx.mxbase.utils.DeviceUtil;
import com.mx.mxbase.utils.GsonTools;
import com.mx.mxbase.utils.MXUamManager;
import com.mx.mxbase.utils.Toastor;
import com.mx.user.R;
import com.mx.user.adapter.VerificationAdapter;
import com.mx.user.adapter.VerificationContentAdapter;
import com.mx.user.model.UserInfoModel;
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
import okhttp3.Call;

/**
 * Created by King on 2017/12/5.
 */

public class DDUserBindActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.rl_dd_bind)
    RelativeLayout rlDdBind;
    @Bind(R.id.tv_base_mid_title)
    TextView tvMidTitle;
    @Bind(R.id.ll_base_back)
    LinearLayout llBack;
    @Bind(R.id.xet_dd_username)
    XEditText xEditTextUser;
    @Bind(R.id.xet_dd_password)
    XEditText xEditTextPsw;
    @Bind(R.id.ll_bind_base)
    LinearLayout llBindBase;
    @Bind(R.id.img_clear_dd_user_name)
    ImageView imgClearUserName;
    @Bind(R.id.tv_dd_register)
    TextView tvRegister;

    private List<String> listStr;
    private ImageView imageView;
    private List<String> listVerification = new ArrayList<>();
    private VerificationAdapter verificationAdapter;
    private BasePopupWindow basePopupWindow;
    private VerificationContentAdapter verificationContentAdapter;

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        rlDdBind.setOnClickListener(this);
        tvMidTitle.setText("绑定账号");
        llBack.setVisibility(View.VISIBLE);
        llBack.setOnClickListener(this);
        imgClearUserName.setOnClickListener(this);
        tvRegister.setOnClickListener(this);
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
    protected int getMainContentViewId() {
        return R.layout.mx_activity_bind_dd_user;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_base_back:
                this.finish();
                break;
            case R.id.rl_dd_bind:
                if (xEditTextPsw.getText().toString().equals("") || xEditTextUser.getText().toString().equals("")) {
                    Toastor.showToast(this, "账号密码不能为空！");
                } else {
                    OkHttpUtils.post().url(Constant.GET_DD_PIC_VERIFICATION).addParams("imei", DeviceUtil.getDeviceSerial().equals("unknown") ? "JS004J00T004BH1E0057" : DeviceUtil.getDeviceSerial()).build().execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            Toastor.showToast(DDUserBindActivity.this, "请求失败！");
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            if (isfinish)return;
                            try {
                                DDPicVerification ddPicVerification = GsonTools.getPerson(response, DDPicVerification.class);
                                if (ddPicVerification != null) {
                                    if (ddPicVerification.getCode() == 0) {
                                        showVerificationPop(ddPicVerification.getResult().getCodes(), ddPicVerification.getResult().getImg());
                                        setValueImgAndVerificationCode(ddPicVerification.getResult().getImg(), ddPicVerification.getResult().getCodes());
                                        verificationContentAdapter.dataChange(listStr);
                                    } else {
                                        Toastor.showToast(DDUserBindActivity.this, ddPicVerification.getMsg());
                                    }
                                }
                            }catch (Exception e){

                            }

                        }
                    });
                }
                break;
            case R.id.tv_dd_register:
                startActivity(new Intent(DDUserBindActivity.this, RegisterDDUserNameActivity.class));
                this.finish();
                break;
            case R.id.img_clear_dd_user_name:
                xEditTextUser.setText("");
                xEditTextPsw.setText("");
                break;
            default:
                break;
        }
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
                        if (DDUserBindActivity.this.isfinish)return;
                        Toastor.showToast(DDUserBindActivity.this, "请求失败！");
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        if (DDUserBindActivity.this.isfinish)return;
                        try {
                            DDPicVerification ddPicVerification = GsonTools.getPerson(response, DDPicVerification.class);
                            if (ddPicVerification != null) {
                                if (ddPicVerification.getCode() == 0) {
                                    setValueImgAndVerificationCode(ddPicVerification.getResult().getImg(), ddPicVerification.getResult().getCodes());
                                    verificationContentAdapter.dataChange(listStr);
                                } else {
                                    Toastor.showToast(DDUserBindActivity.this, ddPicVerification.getMsg());
                                }
                            }
                        }catch (Exception e){

                        }

                    }
                });
            }
        });

        verificationAdapter = new VerificationAdapter(this, listVerification, gridViewCode);
        gridViewCode.setAdapter(verificationAdapter);

        setValueImgAndVerificationCode(img, codes);
        verificationContentAdapter = new VerificationContentAdapter(DDUserBindActivity.this, listStr);
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
                                Toastor.showToast(DDUserBindActivity.this, "请输入完整的验证码");
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
        basePopupWindow.showAtLocation(llBindBase, Gravity.BOTTOM, 0, 0);

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
        bindUser.put("dUserName", xEditTextUser.getText().toString());
        bindUser.put("dPassword", xEditTextPsw.getText().toString());
        bindUser.put("dCode", code.get(0) + code.get(1) + code.get(2) + code.get(3));
        bindUser.put("pdfKey", getPublicKey(this));
        bindUser.put("imei", DeviceUtil.getDeviceSerial().equals("unknown") ? "JS004J00T004BH1E0057" : DeviceUtil.getDeviceSerial());
        OkHttpUtils.post().url(Constant.bindDDUser).params(bindUser).build().connTimeOut(10000).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                if (isfinish)return;
                dialogShowOrHide(false, "登录中...");
            }

            @Override
            public void onResponse(String response, int id) {
                if (isfinish)return;
                dialogShowOrHide(false, "登录中...");
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int code = jsonObject.getInt("code");
                    String msg = jsonObject.getString("msg");
                    if (code == 0) {
                        UserInfoModel userModel = GsonTools.getPerson(response, UserInfoModel.class);
                        share.setCache("v3_user_info_json", response);
                        if (userModel != null) {
                            Intent bind = new Intent("com.moxi.bind.dd.user.ACTION");
                            bind.putExtra("action_type", "bind");
                            LocalBroadcastManager.getInstance(DDUserBindActivity.this).sendBroadcast(bind);
                            MXUamManager.insertUam(DDUserBindActivity.this, Constant.MAIN_PACKAGE, userModel.getResult().getAppSession(), userModel.getResult().getDdToken(), share.getString("v3_user_info_json"));
                            DDUserBindActivity.this.finish();
                        }
                    } else {
                        Toastor.showToast(DDUserBindActivity.this, msg);
                        if (basePopupWindow != null) {
                            basePopupWindow.dismiss();
                            basePopupWindow = null;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                listVerification.clear();
                if (verificationContentAdapter != null) {
                    verificationContentAdapter.notifyDataSetChanged();
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
}
