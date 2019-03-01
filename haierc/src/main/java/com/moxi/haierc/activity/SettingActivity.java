package com.moxi.haierc.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.moxi.haierc.R;
import com.moxi.haierc.model.LockPassWord;
import com.moxi.haierc.model.UserInfoModel;
import com.moxi.haierc.view.PassWordEditText;
import com.moxi.haierc.view.PassWordKeyboard;
import com.mx.mxbase.constant.APPLog;
import com.mx.mxbase.constant.Constant;
import com.mx.mxbase.utils.AndroidUtil;
import com.mx.mxbase.utils.DensityUtil;
import com.mx.mxbase.utils.DeviceUtil;
import com.mx.mxbase.utils.GsonTools;
import com.mx.mxbase.utils.MXUamManager;
import com.mx.mxbase.utils.SharePreferceUtil;
import com.mx.mxbase.utils.Toastor;
import com.mx.mxbase.view.AlertDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.CharBuffer;
import java.util.Calendar;
import java.util.HashMap;

import okhttp3.Call;

/**
 * Created by zhengdelong on 2016/11/15.
 */

public class SettingActivity extends Activity implements View.OnClickListener {

    private long lastClickTime = 0;
    public static final int MIN_CLICK_DELAY_TIME = 1000;
    private static final String SETTINGS_PACKAGE_NAME = "com.android.settings";
    private static final String VCOM_INFO_FILE_PATH = "/sys/class/hwmon/hwmon0/device/vcom_value";
    private SharePreferceUtil share;

    private String[] KEY = new String[]{
            "1", "2", "3",
            "4", "5", "6",
            "7", "8", "9",
            "删除", "0", "完成"
    };

    LinearLayout ll_base_back;
    int times = 0;

    TextView tv_base_mid_title;
    TextView tv_base_back;

    //电源管理
    RelativeLayout yinsi_rel;
    //声音
    RelativeLayout shengyin_rel;
    //连续点击5次
    RelativeLayout kuozhan_rel;
    //应用管理
    RelativeLayout yingyong_gl;
    //隐私
    TextView yingsi_tv;
    //存储
    TextView cunchu;
    //应用设置
    RelativeLayout kuozhan2_rel;
    //校准
    TextView jiaozhun;
    //VCOM
    RelativeLayout about_img_rel;
    RelativeLayout rlBlueTooth;
    //关于设备
    TextView about_shebei;
    RelativeLayout about_aouther;

    private TextView tvChangeVersion;
    private TextView tvDeviceLock;
    private RelativeLayout rlChangeVersion;
    private PopupWindow pswPop;
    private RelativeLayout rlBase;
    private PassWordEditText payEditText;
    private TextView tvTips;
    private TextView updatePassword;
    private View imgLockLine, lineStore;
    private TextView tvLockScreen;
    private TextView complaint_control;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        share = SharePreferceUtil.getInstance(this);
        initView();
    }

    private void initView() {
        yingyong_gl = (RelativeLayout) findViewById(R.id.yingyong_gl);
        kuozhan_rel = (RelativeLayout) findViewById(R.id.kuozhan_rel);
        tv_base_back = (TextView) findViewById(R.id.tv_base_back);
        tv_base_back.setText("其他");
        ll_base_back = (LinearLayout) findViewById(R.id.ll_base_back);
        ll_base_back.setVisibility(View.VISIBLE);
        ll_base_back.setOnClickListener(this);
        tv_base_mid_title = (TextView) findViewById(R.id.tv_base_mid_title);
        tvDeviceLock = (TextView) findViewById(R.id.tv_state_lock_device);
        cunchu = (TextView) findViewById(R.id.cunchu);
        cunchu.setOnClickListener(this);
        tv_base_mid_title.setVisibility(View.GONE);
        shengyin_rel = (RelativeLayout) findViewById(R.id.shengyin_rel);
        yinsi_rel = (RelativeLayout) findViewById(R.id.yinsi_rel);
        kuozhan2_rel = (RelativeLayout) findViewById(R.id.kuozhan2_rel);
        about_img_rel = (RelativeLayout) findViewById(R.id.about_img_rel);
        yingsi_tv = (TextView) findViewById(R.id.yingsi_tv);
        jiaozhun = (TextView) findViewById(R.id.jiaozhun);
        about_shebei = (TextView) findViewById(R.id.about_shebei);
        about_aouther = (RelativeLayout) findViewById(R.id.about_renzheng);
        rlBlueTooth = (RelativeLayout) findViewById(R.id.bluetooth_rel);
        rlBase = (RelativeLayout) findViewById(R.id.rl_base);
        imgLockLine = findViewById(R.id.line_lock_device);
        lineStore = findViewById(R.id.line_cunchu);

        complaint_control = (TextView) findViewById(R.id.complaint_control);
        tvChangeVersion = (TextView) findViewById(R.id.tv_change_version);
        rlChangeVersion = (RelativeLayout) findViewById(R.id.rl_change_version);
        rlChangeVersion.setOnClickListener(this);
        tvLockScreen = (TextView) findViewById(R.id.tv_control);
        tvLockScreen.setOnClickListener(this);

        String versionStr = share.getString("flag_version_stu").equals("") ? "标准版" : share.getString("flag_version_stu");
        if (versionStr.contains("教育")) {
            tvChangeVersion.setText("切换版本（标准版）");
            showOrHide(false);
        } else {
            tvChangeVersion.setText("切换版本（教育版）");
            showOrHide(true);
        }

        shengyin_rel.setOnClickListener(this);
        yinsi_rel.setOnClickListener(this);
        kuozhan2_rel.setOnClickListener(this);
        about_img_rel.setOnClickListener(this);
        yingsi_tv.setOnClickListener(this);
        jiaozhun.setOnClickListener(this);
        about_shebei.setOnClickListener(this);
        kuozhan_rel.setOnClickListener(this);
        about_aouther.setOnClickListener(this);
        rlBlueTooth.setOnClickListener(this);
        tvDeviceLock.setOnClickListener(this);
        complaint_control.setOnClickListener(this);
        yingyong_gl.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String lockDevice = share.getString("lock_device_info");
        LockPassWord lockPassWord = GsonTools.getPerson(lockDevice, LockPassWord.class);
        if (lockPassWord != null) {
            tvDeviceLock.setText("设备锁管理");
        } else {
            tvDeviceLock.setText("设备锁管理（未设置）");
        }
        HashMap pass = new HashMap();
        pass.put("imei", DeviceUtil.getDeviceSerial());
        String temp = MXUamManager.querUserBId(this);
        UserInfoModel userInfoModel = GsonTools.getPerson(temp, UserInfoModel.class);
        if (userInfoModel != null) {
            pass.put("appSession", userInfoModel.getResult().getAppSession());
        }
        pass.put("macAdr", AndroidUtil.getMacAddress(SettingActivity.this));
        pass.put("data", lockDevice);
        OkHttpUtils.post().url(Constant.uploadDevicePsw).params(pass).build().connTimeOut(10000).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
            }

            @Override
            public void onResponse(String onResponse, int id) {
            }
        });
    }

    private void showOrHide(boolean show) {
        if (show) {
//            tvDeviceLock.setVisibility(View.VISIBLE);
//            imgLockLine.setVisibility(View.VISIBLE);
            lineStore.setVisibility(View.VISIBLE);
            yingsi_tv.setVisibility(View.VISIBLE);
        } else {
//            tvDeviceLock.setVisibility(View.GONE);
//            imgLockLine.setVisibility(View.GONE);
            lineStore.setVisibility(View.GONE);
            yingsi_tv.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_state_lock_device:
                //跳转到设备密码管理界面
                startActivity(new Intent(this, DevicePasswordManagerActivity.class));
                break;
            case R.id.yinsi_rel:
                //电源管理
                startActivity(new Intent(Settings.ACTION_DISPLAY_SETTINGS));
                break;
            case R.id.shengyin_rel:
                //声音
                startActivity(new Intent(Settings.ACTION_SOUND_SETTINGS));
                break;
            case R.id.yingyong_gl://应用管理
                Intent intent =  new Intent(Settings.ACTION_MANAGE_ALL_APPLICATIONS_SETTINGS);
                startActivity(intent);
                break;
            case R.id.yingsi_tv://备份
                startActivity(new Intent(Settings.ACTION_PRIVACY_SETTINGS));
                break;
            case R.id.kuozhan2_rel://开发者设置
                Intent intentApp = new Intent();
                startActivity(new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS));
                break;
            case R.id.jiaozhun://校准
                Intent tscalibration = new Intent();
                tscalibration.setClassName("com.onyx.android.tscalibration", "com.onyx.android.tscalibration.MainActivity");
                startActivity(tscalibration);
                break;
            case R.id.cunchu://存储
                startActivity(new Intent(Settings.ACTION_MEMORY_CARD_SETTINGS));
                break;
            case R.id.about_img_rel://VCOM
//                Toast.makeText(SettingActivity.this, getVcomInfo(), Toast.LENGTH_LONG).show();
                break;
            case R.id.about_shebei://设备状态
                startActivity(new Intent(Settings.ACTION_DEVICE_INFO_SETTINGS));
                break;
            case R.id.complaint_control://投诉建议
                try{
                    Intent in = new Intent();
                    in.setComponent(new ComponentName("com.onyx", "com.onyx.content.browser.activity.FeedbackActivity"));
                    startActivity(in);
                }catch (Exception e){}
                break;
            case R.id.ll_base_back://返回
                SettingActivity.this.finish();
                break;
            case R.id.kuozhan_rel://扩展
                long currentTime = Calendar.getInstance().getTimeInMillis();
                if (currentTime - lastClickTime < MIN_CLICK_DELAY_TIME || lastClickTime == 0) {
                    times++;
                    if (times == 5) {
                        kuozhan2_rel.setVisibility(View.VISIBLE);
                    }
                } else {
                    times = 0;
                }
                lastClickTime = currentTime;
                break;
            case R.id.bluetooth_rel://蓝牙
                startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
                break;
            case R.id.tv_control:
                try {
                    Intent setting = new Intent();
                    ComponentName cnSetting = new ComponentName(SETTINGS_PACKAGE_NAME, "com.android.settings.ChooseLockGeneric");
                    setting.setComponent(cnSetting);
                    startActivity(setting);
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case R.id.rl_change_version://版本切换
                String versionStr = share.getString("flag_version_stu").equals("") ? "标准版" : share.getString("flag_version_stu");
                if (versionStr.contains("标准")) {
                    new AlertDialog(this).builder().setTitle("切换版本").setMsg("确认切换为教育版?").setCancelable(false).setNegativeButton("确定", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            share.setCache("flag_version_stu", "教育版");
                            tvChangeVersion.setText("切换版本（标准版）");
                            showOrHide(false);
                        }
                    }).setPositiveButton("取消", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    }).show();
                } else {
                    String verisonChange = share.getString("lock_device_of_version_change");
                    String devicePsw = share.getString("lock_device_info");
                    LockPassWord lockPassWord = GsonTools.getPerson(verisonChange, LockPassWord.class);
                    if (lockPassWord != null && lockPassWord.getIsOpen() == 1) {
                        LockPassWord lockDevice = GsonTools.getPerson(devicePsw, LockPassWord.class);
                        lockPassWord.setPassword(lockDevice.getPassword());
                        showLockDevicePop(lockPassWord);
                    } else {
                        new AlertDialog(this).builder().setTitle("切换版本").setMsg("确认切换为标准版?").setCancelable(false).setNegativeButton("确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                share.setCache("flag_version_stu", "标准版");
                                tvChangeVersion.setText("切换版本（教育版）");
                                showOrHide(true);
                            }
                        }).setPositiveButton("取消", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                            }
                        }).show();
                    }
                }
                break;
            case R.id.about_renzheng://认证信息
                Intent intentAuthen = new Intent();
                intentAuthen.setClass(this, AuthenActivity.class);
                startActivity(intentAuthen);
                break;
            default:
                break;
        }
    }

    private void showLockDevicePop(final LockPassWord lockPassWord) {
        View contentView = LayoutInflater.from(this).inflate(R.layout.mx_pop_set_lock_password, null);
        if (pswPop == null) {
            pswPop = new PopupWindow(contentView, DensityUtil.getScreenW(SettingActivity.this) * 8 / 10,
                    WindowManager.LayoutParams.WRAP_CONTENT, true);
        }
        pswPop.setContentView(contentView);
        payEditText = (PassWordEditText) contentView.findViewById(R.id.psw_lock_device);
        tvTips = (TextView) contentView.findViewById(R.id.tv_tips);
        updatePassword = (TextView) contentView.findViewById(R.id.tv_flag_modify_psw);
        updatePassword.setVisibility(View.GONE);
        contentView.findViewById(R.id.img_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pswPop != null) {
                    pswPop.dismiss();
                    payEditText.removeAll();
                }
                pswPop = null;
            }
        });
        tvTips.setText("输入设备密码，切换到标准版!");
        PassWordKeyboard keyboard = (PassWordKeyboard) contentView.findViewById(R.id.psw_lock__keyboard);
        keyboard.setKeyboardKeys(KEY);
        keyboard.setOnClickKeyboardListener(new PassWordKeyboard.OnClickKeyboardListener() {
            @Override
            public void onKeyClick(int position, String value) {
                if (position < 11 && position != 9) {
                    payEditText.add(value);
                } else if (position == 9) {
                    payEditText.remove();
                } else if (position == 11) {
                    if (payEditText.getText().length() == 6) {
                        checkPassword(lockPassWord, payEditText.getText());
                    } else {
                        Toastor.showToast(SettingActivity.this, "请输入完整设备密码");
                    }
                }
            }
        });
        payEditText.setOnInputFinishedListener(new PassWordEditText.OnInputFinishedListener() {
            @Override
            public void onInputFinished(String password) {
                checkPassword(lockPassWord, payEditText.getText());
            }
        });
        pswPop.setOutsideTouchable(true);
        pswPop.showAtLocation(rlBase, Gravity.CENTER, 0, 0);
    }

    /**
     * 打开关闭设备锁操作
     *
     * @param lockPassWord
     * @param password
     */
    private void checkPassword(LockPassWord lockPassWord, String password) {
        if (lockPassWord != null) {
            if (password.equals(lockPassWord.getPassword())) {
                share.setCache("flag_version_stu", "标准版");
                if (pswPop != null) {
                    pswPop.dismiss();
                }
                pswPop = null;
                tvChangeVersion.setText("切换版本（教育版）");
                showOrHide(true);
                Toastor.showToast(SettingActivity.this, "版本切换成功");
            } else {
                Toastor.showToast(SettingActivity.this, "密码输入错误");
                payEditText.removeAll();
            }
        }
    }

    private String getVcomInfo() {
//        String vcom = FileUtil.readContentOfFile(new File(VCOM_INFO_FILE_PATH));
        String vcom = readFile(VCOM_INFO_FILE_PATH);
        return "VCOM:" + Double.valueOf(vcom) / 100 + " V";
    }


    private String readFile(String filepath) {
        String path = filepath;
        if (null == path) {
            return null;
        }

        String filecontent = null;
        File f = new File(path);
        if (f != null && f.exists()) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(f);
            } catch (FileNotFoundException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return null;
            }

            CharBuffer cb;
            try {
                cb = CharBuffer.allocate(fis.available());
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return null;
            }

            InputStreamReader isr;
            try {
                isr = new InputStreamReader(fis, "utf-8");
                try {
                    if (cb != null) {
                        isr.read(cb);
                    }
                    filecontent = new String(cb.array());
                    isr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        Log.d("content", "readFile filecontent = " + filecontent);
        return filecontent;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        APPLog.e("settingActivity执行了onDestroy");
    }
}
