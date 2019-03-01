package com.moxi.calendar.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.moxi.calendar.R;
import com.moxi.calendar.adapter.DateAdapter;
import com.moxi.calendar.utils.XJTimeUtils;
import com.mx.mxbase.base.BaseApplication;
import com.mx.mxbase.constant.APPLog;
import com.mx.mxbase.view.NoGridView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/9/6.
 */
public class SelectTimeDialog extends Dialog implements View.OnClickListener, DateAdapter.SelectIndex {
    private String time;// 上次时间
    private timeListener listener;


    @Bind(R.id.current_select_time)
    TextView current_select_time;

    @Bind(R.id.show_year_grid)
    NoGridView show_year_grid;
    @Bind(R.id.show_yue_grid)
    NoGridView show_yue_grid;

    @Bind(R.id.show_day_grid)
    NoGridView show_day_grid;
    @Bind(R.id.show_time_grid)
    NoGridView show_time_grid;
    @Bind(R.id.show_miunte_grid)
    NoGridView show_miunte_grid;

    @Bind(R.id.qiut)
    Button qiut;
    @Bind(R.id.insure)
    Button insure;

    private DateAdapter yearAdapter;
    private DateAdapter yueAdapter;

    private DateAdapter timeAdapter;
    private DateAdapter dayAdapter;
    private DateAdapter miunteAdapter;

    XJTimeUtils timeUtils = new XJTimeUtils();

    private String yearStr;
    private String monthStr;
    private String dayStr;
    private String timeStr;
    private String miunteStr;

    public SelectTimeDialog(Context context, int theme, String time,
                            timeListener listener) {
        super(context, theme);
        this.listener = listener;
        this.time = time;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.dialog_select_time);
        ButterKnife.bind(this);

        setYear();
        setMonth();
        setday();
        setMiunte();
        setTime();

        //设置当前的选择日期

        if (null==time||time.equals("")){
            yearAdapter.setOne();
            yueAdapter.setSelectedPosition(getTime(timeUtils.getMonth()));
            dayAdapter.setSelectedPosition(getTime(timeUtils.getDay()));
            timeAdapter.setSelectedPosition(getTime(timeUtils.getHour()));
            miunteAdapter.setSelectedPosition(getTime(timeUtils.getMinute()));
        }else{
            //日期格式2016-12-08 12:23;
            APPLog.e("time",time);
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            try {
                Date date = fmt.parse(time);
                Calendar calendar=Calendar.getInstance();
                calendar.setTime(date);
                int year=calendar.get(Calendar.YEAR);
                int yue=calendar.get(calendar.MONTH)+1;
                int day=calendar.get(Calendar.DAY_OF_MONTH);
                int hour=calendar.get(Calendar.HOUR_OF_DAY);
                int miunte=calendar.get(Calendar.MINUTE);

                yearAdapter.setSelectedPosition(getTime(year));
                yueAdapter.setSelectedPosition(getTime(yue));
                dayAdapter.setSelectedPosition(getTime(day));
                timeAdapter.setSelectedPosition(getTime(hour));
                miunteAdapter.setSelectedPosition(getTime(miunte));

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        qiut.setOnClickListener(this);
        insure.setOnClickListener(this);
    }
    private String getTime(int ll){
        return ll<10?("0"+ll):String.valueOf(ll);
    }

    /**
     * 设置月显示数据
     */
    private void setYear() {
        List<String> months = new ArrayList<>();
        int year = timeUtils.getYear();
        months.add(String.valueOf(year));
        months.add(String.valueOf(++year));
        if (yearAdapter == null) {
            yearAdapter = new DateAdapter(getContext(), months, this);
        } else {
            yearAdapter.notifyDataSetChanged();
        }
        show_year_grid.setAdapter(yearAdapter);
    }

    /**
     * 设置月显示数据
     */
    private void setMonth() {
        List<String> months = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            months.add(i < 10 ? ("0" + i) : String.valueOf(i));
        }
        if (yueAdapter == null) {
            yueAdapter = new DateAdapter(getContext(), months, this);
        } else {
            yueAdapter.notifyDataSetChanged();
        }
        show_yue_grid.setAdapter(yueAdapter);
    }
    List<String> days = new ArrayList<>();
    /**
     * 设置日期
     */
    private void setday() {
        int max=getDaysByYearMonth();
        days.clear();
        for (int i = 1; i <= max; i++) {
            days.add(i < 10 ? ("0" + i) : String.valueOf(i));
        }
        if (max<=30){
            for (int i = max; i < 31; i++) {
                days.add("-1");
            }
        }
        if (dayAdapter == null) {
            dayAdapter = new DateAdapter(getContext(), days, this);
        } else {
            dayAdapter.notifyDataSetChanged();
        }
        show_day_grid.setAdapter(dayAdapter);
    }

    /**
     * 设置小时
     */
    private void setTime() {
        List<String> months = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            months.add(i < 10 ? ("0" + i) : String.valueOf(i));
        }
        if (timeAdapter == null) {
            timeAdapter = new DateAdapter(getContext(), months, this);
        } else {
            timeAdapter.notifyDataSetChanged();
        }
        show_time_grid.setAdapter(timeAdapter);
    }

    /**
     * 设置分
     */
    private void setMiunte() {
        List<String> months = new ArrayList<>();
        for (int i = 0; i < 60; i++) {
            months.add(i < 10 ? ("0" + i) : String.valueOf(i));
        }
        if (miunteAdapter == null) {
            miunteAdapter = new DateAdapter(getContext(), months, this);
        } else {
            miunteAdapter.notifyDataSetChanged();
        }
        show_miunte_grid.setAdapter(miunteAdapter);
    }
    /**
     * 根据年 月 获取对应的月份 天数
     * */
    private   int getDaysByYearMonth() {
        if (null==yearStr||null==monthStr)return 30;
        int year=Integer.parseInt(yearStr);
        int month=Integer.parseInt(monthStr);
        Calendar a = Calendar.getInstance();
        a.set(Calendar.YEAR, year);
        a.set(Calendar.MONTH, month - 1);
        a.set(Calendar.DATE, 1);
        a.roll(Calendar.DATE, -1);
        int maxDate = a.get(Calendar.DATE);
        return maxDate;
    }

    /**
     * 显示输入提示框
     *
     * @param context  上下文
     * @param time     上次选择时间
     * @param listener 监听
     */
    public static void getdialog(Context context, String time,
                                 timeListener listener) {
        SelectTimeDialog dialog = new SelectTimeDialog(context, com.mx.mxbase.R.style.dialog, time
                , listener);
        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER);

        window.getDecorView().setPadding(BaseApplication.ScreenWidth / 10, 0, BaseApplication.ScreenWidth / 10, 0);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        dialog.show();
    }

    @Override
    public void onClick(View v) {

        if (v==insure){
            if (listener!=null)listener.backTime(SpiltTime());
        }
            this.dismiss();
    }

    @Override
    protected void onStop() {
        super.onStop();
        ButterKnife.unbind(this);
    }

    @Override
    public void select(DateAdapter adapter, String value) {

        if (adapter == yearAdapter) {
            yearStr=value;
            setday();
        } else if (adapter == yueAdapter) {
            monthStr=value;
            setday();
        } else if (adapter == dayAdapter) {
            dayStr=value;
        } else if (adapter == timeAdapter) {
            timeStr=value;
        } else if (adapter == miunteAdapter) {
            miunteStr=value;
        }
        SpiltTime();
    }
    public String SpiltTime(){
        String value=yearStr+"-"+monthStr+"-"+dayStr+" "+timeStr+":"+miunteStr;
        current_select_time.setText("选择时间："+value);
        return value;
    }

    public interface timeListener {
        public void backTime(String time);
    }
}
