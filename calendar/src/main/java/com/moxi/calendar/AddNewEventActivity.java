package com.moxi.calendar;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.moxi.calendar.dialog.SelectTimeDialog;
import com.moxi.calendar.model.EventBeen;
import com.moxi.calendar.utils.TimeChangeUtils;
import com.moxi.calendar.view.BaseEdite;
import com.mx.mxbase.base.BaseActivity;
import com.mx.mxbase.constant.APPLog;
import com.mx.mxbase.interfaces.InsureOrQuitListener;
import com.mx.mxbase.utils.StringUtils;

import org.litepal.crud.DataSupport;

import java.text.ParseException;

import butterknife.Bind;

public class AddNewEventActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener, OnClickListener {

    public static void startActivity(Activity activity, int request, String title, EventBeen event,String time) {
        Intent intent = new Intent(activity, AddNewEventActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("been", event);
        bundle.putString("title", title);
        bundle.putString("time", time);
        intent.putExtras(bundle);
        activity.startActivityForResult(intent, request);
    }

    @Bind(R.id.last_title)
    TextView last_title;
    @Bind(R.id.title)
    TextView titleview;
    @Bind(R.id.input_name)
    BaseEdite input_name;
    @Bind(R.id.select_time)
    TextView select_time;

    //选择提示时间
    @Bind(R.id.select_is_notify)
    RadioGroup select_is_notify;
    @Bind(R.id.select_notify_time)
    RadioGroup select_notify_time;

    @Bind(R.id.input_remark)
    BaseEdite input_remark;

    @Bind(R.id.updata_layout)
    LinearLayout updata_layout;
    @Bind(R.id.insure_update)
    Button insure_update;
    @Bind(R.id.insure_delete)
    Button insure_delete;
    @Bind(R.id.insure_add)
    Button insure_add;

    private EventBeen been;
    private String title;
    private String time;

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        Bundle bundle = null;
        if (savedInstanceState == null) {
            bundle = getIntent().getExtras();
        } else {
            bundle = savedInstanceState;
        }
        initData(bundle);

        //处理界面布局事件
        select_is_notify.setOnCheckedChangeListener(this);
        select_notify_time.setOnCheckedChangeListener(this);

        select_time.setOnClickListener(this);
        last_title.setOnClickListener(this);

        insure_update.setOnClickListener(this);
        insure_delete.setOnClickListener(this);
        insure_add.setOnClickListener(this);

    }

    private void initData(Bundle bundle) {
        been = (EventBeen) bundle.getSerializable("been");
        title = bundle.getString("title");
        time = bundle.getString("time");

        last_title.setText(title);
        ((RadioButton) findViewById(R.id.select1)).setChecked(true);
        ((RadioButton) findViewById(R.id.check1)).setChecked(true);

        if (been == null) {
            titleview.setText("新事件");
            been = new EventBeen();
            updata_layout.setVisibility(View.GONE);
        } else {
            insure_add.setVisibility(View.GONE);
            titleview.setText(been.name);
            input_name.setText(been.name);
            select_time.setText(TimeChangeUtils.getTime(been.saveTime));
            input_remark.setText(been.remark);
            if (been.whetherNotify.equals("1")||been.whetherNotify.equals("2")) {
                ((RadioButton) findViewById(R.id.select2)).setChecked(true);
                int index = Integer.parseInt(been.setNotify);
                ((RadioButton) select_notify_time.getChildAt(index)).setChecked(true);
                select_notify_time.setVisibility(View.VISIBLE);
            }
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.last_title:
                onBackPressed();
                break;
            case R.id.select_time://选择提醒时间
                SelectTimeDialog.getdialog(this, time, new SelectTimeDialog.timeListener() {
                    @Override
                    public void backTime(String time) {
                        //获得时间字符串开始赋值
                        try {
                            select_time.setText(time);
                            String lon = TimeChangeUtils.getLongTime(time);
                            been.saveDate = time.split(" ")[0];//设置提示日期
                            been.saveTime = lon;//设置具体的保存时间
                        } catch (ParseException e) {
                            showToast("时间转换失败");
                            select_time.setText("");
                        }

                    }
                });
                break;
            case R.id.insure_update://修改
                been.name=input_name.getText().toString().trim();
                been.remark=input_remark.getText().toString().trim();
                if (been.name.equals("")) {
                    showToast("事件名称不能为空");
                    return;
                }
                if (been.saveTime.equals("")) {
                    showToast("请选择提示时间");
                    return;
                }

                dialogShowOrHide(true,"保存中...");

                if (been.whetherNotify.equals("0")) {
                    been.notifyTime = TimeChangeUtils.getTime(been.saveTime);
                } else {
                    long time = Long.parseLong(been.saveTime);
                    if (been.setNotify.equals("0")) {
                        time -= 300l*1000l;
                    } else if (been.setNotify.equals("1")) {
                        time -= 900l*1000l;
                    } else if (been.setNotify.equals("2")) {
                        time -= 1800l*1000l;
                    } else if (been.setNotify.equals("3")) {
                        time -= 3600l*1000l;
                    } else {
                        time -= 86400l*1000l;
                    }
                    been.notifyTime=TimeChangeUtils.getTime(time);
                }

                ContentValues cv = new ContentValues();
                cv.put("name", been.name);
                cv.put("saveDate", been.saveDate);
                cv.put("saveTime", been.saveTime);
                cv.put("whetherNotify", been.whetherNotify);
                cv.put("setNotify", been.setNotify);
                cv.put("notifyTime", been.notifyTime);
                cv.put("remark", been.remark);
                cv.put("isDiabolo", false);

                int i1 = DataSupport.update(been.getClass(), cv, been.id);
                if (i1 > 0) {
                    showToast("修改成功");
                    setResult();
                    AddNewEventActivity.this.finish();
                } else {
                    showToast("修改失败");
                }
                dialogShowOrHide(false,"");
                break;
            case R.id.insure_delete://删除
                insureDialog("请确认删除事件：" + been.name, "删除事件", new InsureOrQuitListener() {
                    @Override
                    public void isInsure(Object code, boolean is) {
                        if (is){
                            int i = DataSupport.delete(been.getClass(), been.id);
                            if (i > 0) {
                                showToast("删除成功");
                                setResult();
                                AddNewEventActivity.this.finish();
                            } else {
                                showToast("删除失败");
                            }
                        }
                    }
                });
                break;
            case R.id.insure_add://添加
                been.name=input_name.getText().toString().trim();
                been.remark=input_remark.getText().toString().trim();

                if (been.name.equals("")) {
                    showToast("事件名称不能为空");
                    return;
                }
                if (been.saveTime.equals("")) {
                    showToast("请选择提示时间");
                    return;
                }
                dialogShowOrHide(true, "保存中...");

                if (been.whetherNotify.equals("0")) {
                    been.notifyTime = TimeChangeUtils.getTime(been.saveTime);
                } else {
                    long time = Long.parseLong(been.saveTime);
                    if (been.setNotify.equals("1")) {
                        time -= 300l*1000l;
                    } else if (been.setNotify.equals("2")) {
                        time -= 900l*1000l;
                    } else if (been.setNotify.equals("3")) {
                        time -= 1800l*1000l;
                    } else if (been.setNotify.equals("4")) {
                        time -= 86400l*1000l;
                    } else {
                        //当前时间
                    }
                    been.notifyTime=TimeChangeUtils.getTime(time);
                }
                APPLog.e("最后保存数据"+been.toString());
                if (been.save()) {
                    showToast("保存成功");
                    setResult();
                    AddNewEventActivity.this.finish();
                }
                dialogShowOrHide(false,"");
                break;
            default:
                break;
        }
    }
    private void setResult(){
        Intent intent=getIntent();
        intent.putExtra("data",been);
        setResult(RESULT_OK,intent);
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
        outState.putSerializable("been", been);
        outState.putString("title", title);
        outState.putString("time", time);
    }

    @Override
    public void onActivityRestoreInstanceState(Bundle savedInstanceState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

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

    @Override
    protected int getMainContentViewId() {
        return R.layout.activity_add_new_event;
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (group == select_is_notify) {
            if (checkedId == R.id.select1) {
                been.whetherNotify = "0";
                select_notify_time.setVisibility(View.GONE);
            } else if (checkedId == R.id.select2) {
                been.whetherNotify = "1";
                select_notify_time.setVisibility(View.VISIBLE);
            }
        } else if (group == select_notify_time) {
            switch (checkedId) {
                case R.id.check1:
                    been.setNotify = "0";
                    break;
                case R.id.check2:
                    been.setNotify = "1";
                    break;
                case R.id.check3:
                    been.setNotify = "2";
                    break;
                case R.id.check4:
                    been.setNotify = "3";
                    break;
                case R.id.check5:
                    been.setNotify = "4";
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
