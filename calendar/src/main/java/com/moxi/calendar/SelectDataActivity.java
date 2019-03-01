package com.moxi.calendar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.moxi.calendar.adapter.SelectDataAdapter;
import com.moxi.calendar.model.SelectDataBeen;
import com.mx.mxbase.view.NoGridView;

import java.util.ArrayList;

import butterknife.Bind;

public class SelectDataActivity extends BaseCalendarActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    public static void StartSelectDataActivity(Activity activity, int year, int month) {
        Intent intent = new Intent(activity, SelectDataActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("year", year);
        bundle.putInt("month", month);
        intent.putExtras(bundle);
        activity.startActivityForResult(intent,11);
    }

    private int year;
    private int month;

    @Bind(R.id.back_button)
    TextView back_button;
    @Bind(R.id.next_year_month)
    TextView next_year_month;
    @Bind(R.id.more_motification)
    ImageButton more_motification;
    @Bind(R.id.grid_select_Time)
    NoGridView grid_select_Time;
    @Bind(R.id.insure)
    Button insure;

    private ArrayList<SelectDataBeen> selectDataBeens = new ArrayList<>();
    private SelectDataAdapter selectDataAdapter;

    @Override
    protected int getMainContentViewId() {
        return R.layout.activity_select_data;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        super.onActivityCreated(activity, savedInstanceState);
        Bundle bundle = null;
        if (savedInstanceState != null) {
            bundle = savedInstanceState;
        } else {
            bundle = getIntent().getExtras();
        }
        year = bundle.getInt("year");
        month = bundle.getInt("month");


        back_button.setOnClickListener(this);
        more_motification.setOnClickListener(this);
        insure.setOnClickListener(this);
        grid_select_Time.setOnItemClickListener(this);

        initData();
        next_year_month.setText(getYearMonthStr());
    }

    /**
     * 初始化数据
     */
    private void initData() {
        //年
        selectDataBeens.add(new SelectDataBeen(0, 0, false));
        selectDataBeens.add(new SelectDataBeen(year, 2, true));
        selectDataBeens.add(new SelectDataBeen(0, 1, false));
        //月
        selectDataBeens.add(new SelectDataBeen(1, 3, month == 1));
        selectDataBeens.add(new SelectDataBeen(2, 3, month == 2));
        selectDataBeens.add(new SelectDataBeen(3, 3, month == 3));
        selectDataBeens.add(new SelectDataBeen(4, 3, month == 4));
        selectDataBeens.add(new SelectDataBeen(5, 3, month == 5));
        selectDataBeens.add(new SelectDataBeen(6, 3, month == 6));
        selectDataBeens.add(new SelectDataBeen(7, 3, month == 7));
        selectDataBeens.add(new SelectDataBeen(8, 3, month == 8));
        selectDataBeens.add(new SelectDataBeen(9, 3, month == 9));
        selectDataBeens.add(new SelectDataBeen(10, 3, month == 10));
        selectDataBeens.add(new SelectDataBeen(11, 3, month == 11));
        selectDataBeens.add(new SelectDataBeen(12, 3, month == 12));

        selectDataAdapter = new SelectDataAdapter(this, selectDataBeens);
        grid_select_Time.setAdapter(selectDataAdapter);
    }

    public String getYearMonthStr() {
        return year + "年" + month + "月";
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_button:
                onBackPressed();
                break;
            case R.id.insure:
                Intent intent=new Intent();
                Bundle bundle = new Bundle();
                bundle.putInt("year", year);
                bundle.putInt("month", month);
                intent.putExtras(bundle);
                setResult(RESULT_OK,intent);

                onBackPressed();
                break;
            case R.id.more_motification:
                StringBuilder builder = new StringBuilder();
                builder.append(getYearMonthStr());
                builder.append("1日");
                ListEventActivity.startListEvent(this, 10, builder.toString());
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        SelectDataBeen been = selectDataBeens.get(position);
        switch (been.type) {
            case 0://上一年
                selectDataBeens.get(1).dataIndex--;
                break;
            case 1://下一年
                selectDataBeens.get(1).dataIndex++;
                break;
            case 3://月
                for (int i = 3; i < selectDataBeens.size(); i++) {
                    selectDataBeens.get(i).Select = false;
                }
                month = been.dataIndex;
                been.Select = true;
                break;

        }
        if (been.type != 2) {
            selectDataAdapter.notifyDataSetChanged();
            year = selectDataBeens.get(1).dataIndex;
            next_year_month.setText(getYearMonthStr());
        }

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    @Override
    public void timeChange() {

    }

    @Override
    public void dataChange() {

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
        outState.putInt("year", year);
        outState.putInt("month", month);
    }

    @Override
    public void onActivityRestoreInstanceState(Bundle savedInstanceState) {

    }

}
