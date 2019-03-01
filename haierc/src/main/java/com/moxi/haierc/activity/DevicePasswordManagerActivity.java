package com.moxi.haierc.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.moxi.haierc.R;
import com.moxi.haierc.adapter.PasswordManagerAdapter;
import com.moxi.haierc.model.LockPassWord;
import com.moxi.haierc.model.PasswordClickModel;
import com.moxi.haierc.ports.OnCheckChange;
import com.moxi.haierc.view.PassWordEditText;
import com.moxi.haierc.view.PassWordKeyboard;
import com.mx.mxbase.base.BaseActivity;
import com.mx.mxbase.utils.AndroidUtil;
import com.mx.mxbase.utils.DensityUtil;
import com.mx.mxbase.utils.DeviceUtil;
import com.mx.mxbase.utils.GsonTools;
import com.mx.mxbase.utils.Toastor;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * 设备密码管理
 * Created by King on 2017/12/20.
 */

public class DevicePasswordManagerActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.lv_device_password_setting)
    ListView listViewPswSetting;
    @Bind(R.id.tv_base_mid_title)
    TextView tvMidTitle;
    @Bind(R.id.ll_base_back)
    LinearLayout llBack;
    @Bind(R.id.ll_device_password_manager_base)
    LinearLayout llBase;

    private PopupWindow pswPop;
    private PassWordEditText payEditText;
    private TextView tvTips;
    private TextView updatePassword;
    private String tempPassword = "";

//    private String[] itemStr = new String[]{"设备密码设置及更改", "版本切换锁"};
    private String[] itemStr = new String[]{"设备密码设置及更改", "版本切换锁", "手写备忘锁","锁屏密码"};
    private List<PasswordClickModel> listModel = new ArrayList<>();
    private PasswordManagerAdapter adapter;
    private String saveData = "";

    private String[] KEY = new String[]{
            "1", "2", "3",
            "4", "5", "6",
            "7", "8", "9",
            "删除", "0", "完成"
    };

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        saveData = share.getString("save_password_data");
        initView();
        initData();
    }

    private void initView() {
        llBack.setVisibility(View.VISIBLE);
        tvMidTitle.setText("设备密码管理");

        llBack.setOnClickListener(this);
    }

    private void initData() {
        if (TextUtils.isEmpty(saveData)) {
            for (String item : itemStr) {
                PasswordClickModel pcm = new PasswordClickModel();
                pcm.setItemName(item);
                pcm.setSelected(false);
                if ("设备密码设置及更改".equals(item)) {
                    pcm.setShowCheckBox(false);
                } else {
                    pcm.setShowCheckBox(true);
                }
                listModel.add(pcm);
            }
        } else {
            Gson gson = new Gson();
            listModel = gson.fromJson(saveData, new TypeToken<List<PasswordClickModel>>() {
            }.getType());
            String item=itemStr[itemStr.length-1];
            if (!listModel.get(listModel.size()-1).getItemName().equals(item)){
                PasswordClickModel pcm = new PasswordClickModel();
                pcm.setItemName(item);
                pcm.setSelected(false);
                if ("设备密码设置及更改".equals(item)) {
                    pcm.setShowCheckBox(false);
                } else {
                    pcm.setShowCheckBox(true);
                }
                listModel.add(pcm);
            }
            for (int i = 1; i < listModel.size(); i++) {
                listModel.get(i).setShowCheckBox(true);
            }
        }
        adapter = new PasswordManagerAdapter(this, listModel);
        listViewPswSetting.setAdapter(adapter);
        adapter.setOnCheckChange(new OnCheckChange() {
            @Override
            public void onChanged(int position) {
                String lockDevice = share.getString("lock_device_info");
                LockPassWord lockPassWord = GsonTools.getPerson(lockDevice, LockPassWord.class);
                if (lockPassWord != null) {
                    boolean temp = listModel.get(position).isSelected();
                    showLockDevicePop(lockPassWord, position, temp);
                } else {
                    Toastor.showToast(DevicePasswordManagerActivity.this, "请先设置设备密码！！！");
                }
            }
        });
        listViewPswSetting.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        String lockDevice = share.getString("lock_device_info");
                        LockPassWord lockPassWord = GsonTools.getPerson(lockDevice, LockPassWord.class);
                        showLockDevicePop(lockPassWord);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void showLockDevicePop(final LockPassWord lockPassWord, final int position, boolean temp) {
        View contentView = LayoutInflater.from(this).inflate(R.layout.mx_pop_set_lock_password, null);
        if (pswPop == null) {
            pswPop = new PopupWindow(contentView, DensityUtil.getScreenW(DevicePasswordManagerActivity.this) * 8 / 10,
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
        if (temp) {
            if (position == 1) {
                tvTips.setText("输入设备密码，关闭版本切换锁");
            } else if(position==2){
                tvTips.setText("输入设备密码，关闭手写备忘锁");
            }else {
                tvTips.setText("输入设备密码，关闭锁屏密码");
            }
        } else {
            if (position == 1) {
                tvTips.setText("输入设备密码，开启版本切换锁");
            } else if(position==2) {
                tvTips.setText("输入设备密码，开启手写备忘锁");
            }else {
                tvTips.setText("输入设备密码，开启锁屏密码");
            }
        }
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
                        checkPassword(lockPassWord, payEditText.getText(), position);
                        if (pswPop != null) {
                            pswPop.dismiss();
                        }
                        pswPop = null;
                    } else {
                        Toastor.showToast(DevicePasswordManagerActivity.this, "请输入完整设备密码");
                    }
                }
            }
        });

        /**
         * 当密码输入完成时的回调
         */
        payEditText.setOnInputFinishedListener(new PassWordEditText.OnInputFinishedListener() {
            @Override
            public void onInputFinished(String password) {
                checkPassword(lockPassWord, password, position);
            }
        });
        pswPop.setOutsideTouchable(true);
        pswPop.showAtLocation(llBase, Gravity.CENTER, 0, 0);
    }

    private void checkPassword(LockPassWord lockPassWord, String password, int position) {
        PasswordClickModel passwordClickModel = listModel.get(position);
        if (password.equals(lockPassWord.getPassword())) {
            listModel.get(position).setSelected(!passwordClickModel.isSelected());
            share.setCache("save_password_data", GsonTools.obj2json(listModel));
            adapter.notifyDataSetChanged();
            if (pswPop != null) {
                pswPop.dismiss();
            }
            pswPop = null;
            if (!passwordClickModel.isSelected()) {
                Toastor.showToast(this, itemStr[position] + "已关闭！");
            } else {
                Toastor.showToast(this, itemStr[position] + "已开启！");
            }
            LockPassWord newLock = new LockPassWord();
            newLock.setIsOpen(passwordClickModel.isSelected() ? 1 : 0);
            newLock.setPassword(password);
            newLock.setUpdateTime(System.currentTimeMillis());
            newLock.setMacId(AndroidUtil.getMacAddress(DevicePasswordManagerActivity.this) + DeviceUtil.getDeviceSerial());
            if (position == 1) {
                share.setCache("lock_device_of_version_change", GsonTools.obj2json(newLock));
            } else if (position==2){//手写备忘管理锁
                share.setCache("lock_device_of_write_node", GsonTools.obj2json(newLock));
            }else {
                share.setCache("lock_device_of_screen_open", GsonTools.obj2json(newLock));
            }
        } else {
            Toastor.showToast(this, "密码验证失败请重新输入");
            payEditText.removeAll();
            tempPassword = "";
        }
    }

    /**
     * 设置设备版本切换锁
     *
     * @param lockPassWord
     */
    int modifyFlag = 0;

    private void showLockDevicePop(final LockPassWord lockPassWord) {
        View contentView = LayoutInflater.from(this).inflate(R.layout.mx_pop_set_lock_password, null);
        if (pswPop == null) {
            pswPop = new PopupWindow(contentView, DensityUtil.getScreenW(DevicePasswordManagerActivity.this) * 8 / 10,
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
                }
                pswPop = null;
                newPswTimes = -1;
                modifyFlag = 0;
                newPassword = "";
            }
        });
        updatePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String temp = share.getString("lock_device_info");
                if (temp.equals("")) {
                    Toastor.showToast(DevicePasswordManagerActivity.this, "当前设备未设置密码。");
                } else {
                    modifyFlag = 1;
                    tvTips.setText("输入设备当前密码！");
                    payEditText.removeAll();
                    tempPassword = "";
                    updatePassword.setVisibility(View.INVISIBLE);
                }
            }
        });
        if (lockPassWord != null) {
            modifyFlag = 1;//显示修改密码或是关闭设备锁关键设置
            tvTips.setText("输入设备密码，修改设备密码");
        } else {
            tvTips.setText("请输入密码，设置设备锁");
        }
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
                        if (pswPop != null) {
                            pswPop.dismiss();
                        }
                        pswPop = null;
                        newPswTimes = -1;
                        modifyFlag = 0;
                        newPassword = "";
                    } else {
                        Toastor.showToast(DevicePasswordManagerActivity.this, "请输入完整设备密码");
                    }
                }
            }
        });

        /**
         * 当密码输入完成时的回调
         */
        payEditText.setOnInputFinishedListener(new PassWordEditText.OnInputFinishedListener() {
            @Override
            public void onInputFinished(String password) {
                if (modifyFlag == 0) {
                    checkPassword(lockPassWord, password);
                } else {
                    inputNewPassword(password, lockPassWord);
                }
            }
        });
        pswPop.setOutsideTouchable(true);
        pswPop.showAtLocation(llBase, Gravity.CENTER, 0, 0);
    }


    /**
     * 重新设置密码操作
     *
     * @param password
     * @param lockPassWord
     */
    private String newPassword = "";
    private int newPswTimes = -1;

    private void inputNewPassword(String password, LockPassWord lockPassWord) {
        if (newPswTimes == -1) {
            if (password.equals(lockPassWord.getPassword())) {
                payEditText.removeAll();
                tvTips.setText("请输入新密码");
                newPswTimes = 0;
            } else {
                Toastor.showToast(this, "密码输入错误请重新输入!");
                payEditText.removeAll();
            }
        } else {
            if (newPswTimes == 0) {
                newPassword = password;
                payEditText.removeAll();
                tvTips.setText("请再次输入密码");
                newPswTimes = 1;
                return;
            }
            if (newPassword.equals(password)) {
                LockPassWord newLock = new LockPassWord();
                newLock.setIsOpen(lockPassWord.getIsOpen());
                newLock.setPassword(password);
                newLock.setUpdateTime(System.currentTimeMillis());
                newLock.setMacId(AndroidUtil.getMacAddress(DevicePasswordManagerActivity.this) + DeviceUtil.getDeviceSerial());
                share.setCache("lock_device_info", GsonTools.obj2json(newLock));
                Toastor.showToast(this, "密码修改成功!");
                if (pswPop != null) {
                    pswPop.dismiss();
                }
                pswPop = null;
            } else {
                payEditText.removeAll();
                Toastor.showToast(this, "两次密码输入不一样，请重新输入!");
                tvTips.setText("请重新输入密码");
                newPswTimes = 0;
                payEditText.removeAll();
                newPassword = "";
                return;
            }
            newPswTimes = -1;
            modifyFlag = 0;
            newPassword = "";
        }
    }

    /**
     * 打开关闭设备锁操作
     *
     * @param lockPassWord
     * @param password
     */
    private void checkPassword(LockPassWord lockPassWord, String password) {
        if (lockPassWord != null) {
            if (lockPassWord.getIsOpen() == 1) {
                if (password.equals(lockPassWord.getPassword())) {
                    LockPassWord lock = new LockPassWord();
                    lock.setIsOpen(0);
                    lock.setPassword(password);
                    lock.setUpdateTime(System.currentTimeMillis());
                    lock.setMacId(AndroidUtil.getMacAddress(DevicePasswordManagerActivity.this) + DeviceUtil.getDeviceSerial());
                    share.setCache("lock_device_info", GsonTools.obj2json(lock));
                    if (pswPop != null) {
                        pswPop.dismiss();
                    }
                    pswPop = null;
                } else {
                    Toastor.showToast(DevicePasswordManagerActivity.this, "密码输入错误");
                    payEditText.removeAll();
                }
            } else {
                if (password.equals(lockPassWord.getPassword())) {
                    LockPassWord lock = new LockPassWord();
                    lock.setIsOpen(1);
                    lock.setPassword(password);
                    lock.setUpdateTime(System.currentTimeMillis());
                    lock.setMacId(AndroidUtil.getMacAddress(DevicePasswordManagerActivity.this) + DeviceUtil.getDeviceSerial());
                    share.setCache("lock_device_info", GsonTools.obj2json(lock));
                    if (pswPop != null) {
                        pswPop.dismiss();
                    }
                    pswPop = null;
                } else {
                    Toastor.showToast(DevicePasswordManagerActivity.this, "密码输入错误");
                    payEditText.removeAll();
                }
            }
        } else {
            if (tempPassword.equals("")) {
                tempPassword = payEditText.getText();
                tvTips.setText("请再次输入设备锁密码");
                payEditText.removeAll();
            } else {
                if (payEditText.getText().equals(tempPassword)) {
                    LockPassWord lock = new LockPassWord();
                    lock.setIsOpen(1);
                    lock.setPassword(tempPassword);
                    lock.setUpdateTime(System.currentTimeMillis());
                    lock.setMacId(AndroidUtil.getMacAddress(DevicePasswordManagerActivity.this) + DeviceUtil.getDeviceSerial());
                    share.setCache("lock_device_info", GsonTools.obj2json(lock));
                    if (pswPop != null) {
                        pswPop.dismiss();
                    }
                    pswPop = null;
                    Toastor.showToast(DevicePasswordManagerActivity.this, "设备密码设置成功!");
                } else {
                    tempPassword = "";
                    payEditText.removeAll();
                    tvTips.setText("请设置设备锁");
                    Toastor.showToast(DevicePasswordManagerActivity.this, "两次密码输入不一样，请重新输入!");
                }
            }
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
    protected int getMainContentViewId() {
        return R.layout.activity_device_password_manager;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_base_back:
                onBackPressed();
                break;
            default:
                break;
        }
    }

}
