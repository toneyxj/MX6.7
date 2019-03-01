package com.moxi.calendar;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.moxi.calendar.adapter.ListEventAdapter;
import com.moxi.calendar.model.EventBeen;
import com.moxi.calendar.model.EventDateBeen;
import com.mx.mxbase.base.BaseActivity;
import com.mx.mxbase.constant.APPLog;
import com.mx.mxbase.view.LinerlayoutInter;
import com.mx.mxbase.view.NoListView;

import org.litepal.crud.DataSupport;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;

public class ListEventActivity extends BaseActivity implements View.OnClickListener ,LinerlayoutInter.LinerLayoutInter,OnItemClickListener{
    public static void startListEvent(Activity activity, int request,  String currentDate) {//String title,
        Intent intent = new Intent(activity, ListEventActivity.class);
        Bundle bundle = new Bundle();
//        bundle.putString("title", title);
        bundle.putString("currentDate", currentDate);
        intent.putExtras(bundle);
        activity.startActivityForResult(intent, request);

    }

//    private String title;
    private String currentDate;

    @Bind(R.id.back)
    TextView back;
    @Bind(R.id.current_date)
    TextView current_date;

    @Bind(R.id.move_layout)
    LinerlayoutInter move_layout;
    @Bind(R.id.list_view)
    NoListView list_view;
    private boolean isfirst=true;
    private List<EventDateBeen> listData=new ArrayList<>();
    private List<EventDateBeen> sonList = new ArrayList<>();
    private ListEventAdapter adapter;

    @Bind(R.id.last_page)
    ImageButton last_page;
    @Bind(R.id.show_index)
    TextView show_index;
    @Bind(R.id.next_page)
    ImageButton next_page;

    /**
     * 当前显示页数
     */
    private int CurrentIndex = 0;
    /**
     * 总页数
     */
    private int totalIndex = 1;
    /**
     * 每页个数
     */
    private int pageSize=5;
    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        Bundle bundle = null;
        if (savedInstanceState != null) {
            bundle = savedInstanceState;
        } else {
            bundle = getIntent().getExtras();
        }
//        title = bundle.getString("title");
        currentDate = bundle.getString("currentDate");
//        back.setText(title);

        back.setOnClickListener(this);
        current_date.setText(currentDate);

        move_layout.setLayoutInter(this);
        list_view.setOnItemClickListener(this);
        last_page.setOnClickListener(this);
        next_page.setOnClickListener(this);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus&&isfirst){
            isfirst=false;
            int height=move_layout.getHeight();
            pageSize=height/(ThisApplication.dip2px(100));
            getListData();
            initSonData();
        }
    }

    @Override
    public void onClick(View v) {
        if (v == back) {
            onBackPressed();
        }else if(last_page==v){
            moveLeft();
        }else if(next_page==v){
            moveRight();
        }
    }
    private void getListData(){
        listData.clear();
        long  lTime=System.currentTimeMillis();
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy年MM月dd日");
        Date dt2 = null;
        try {
            dt2 = sdf.parse(currentDate);
             lTime = dt2.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Cursor cursor = DataSupport.findBySQL("select " + MainActivity.sqlSlect + " from EventBeen where saveTime >='" + lTime + "' order by saveTime ASC");
        String lastDate=null;
        while (cursor.moveToNext()) {
            long id = cursor.getLong(0);
            String saveDate = cursor.getString(1);
            String saveTime = cursor.getString(2);
            String name = cursor.getString(3);
            String whetherNotify = cursor.getString(4);
            String setNotify = cursor.getString(5);
            String notifyTime = cursor.getString(6);
            String remark = cursor.getString(7);
            EventBeen eventBeen = new EventBeen(id, saveDate, saveTime, name, whetherNotify, setNotify, notifyTime, remark);
            if (lastDate==null||!lastDate.equals(saveDate)){
                if (lastDate==null){
                    listData.add(new EventDateBeen(eventBeen, true));
                }else {
                    if (!lastDate.split("-")[1].equals(saveDate.split("-")[1]))
                        listData.add(new EventDateBeen(eventBeen, true));
                }
            }
            listData.add(new EventDateBeen(eventBeen,false));
            lastDate=saveDate;
        }

    }
    /**
     * 分配下面数据以刷新
     */
    public void initSonData() {
        //计算页数
        totalIndex = listData.size() / pageSize;
        totalIndex += listData.size() % pageSize == 0 ? 0 : 1;

        //计算当前页数
        if (CurrentIndex > totalIndex - 1) {
            CurrentIndex = totalIndex - 1;
        }
        if (CurrentIndex<0)CurrentIndex=0;
        if (totalIndex==0)totalIndex=1;

        if (listData.size() == 0) {
            adapterItems(listData);
        } else if (totalIndex - 1 == CurrentIndex) {
            adapterItems(listData.subList(CurrentIndex * pageSize, listData.size()));
        } else {
            adapterItems(listData.subList(CurrentIndex * pageSize, (CurrentIndex + 1) * pageSize));
        }
        setShowText();
    }
    private void adapterItems(List<EventDateBeen> List) {
        sonList.clear();
        sonList.addAll(List);
        if (adapter == null) {
            adapter = new ListEventAdapter(this, sonList);
            list_view.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }

    }
    /**
     * 设置显示当前显示页数
     */
    public void setShowText() {
       String value=String.valueOf(CurrentIndex + 1) + "/" + totalIndex;
        show_index.setText(value);
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
//        outState.putString("title", title);
        outState.putString("currentDate", currentDate);
    }

    @Override
    public void onActivityRestoreInstanceState(Bundle savedInstanceState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    @Override
    protected int getMainContentViewId() {
        return R.layout.activity_list_event;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        if (requestCode == 10) {
            setResult(RESULT_OK);
            //刷新当前列表
//            EventBeen been= (EventBeen) data.getSerializableExtra("data");
//            getDate().event=
            getListData();
            initSonData();
        }
    }
//
    @Override
    public void moveLeft() {
        if (CurrentIndex > 0 && (CurrentIndex <= totalIndex - 1)) {
            CurrentIndex--;
            initSonData();
        }
    }

    @Override
    public void moveRight() {
        if (CurrentIndex >= totalIndex - 1) {
            return;
        } else {
            CurrentIndex++;
            initSonData();
        }
    }
    private int clickPosition=-1;
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        clickPosition=position;
        if (!sonList.get(position).isMonth)
        AddNewEventActivity.startActivity(ListEventActivity.this,10,"编辑事项",getDate().event,"");
    }

    private EventDateBeen getDate(){
        int datePosition=pageSize*CurrentIndex+clickPosition;
        return  listData.get(datePosition);
    }
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        APPLog.e("onKeyDown",event.getAction());
        if (keyCode == KeyEvent.KEYCODE_PAGE_UP) {
            //上一页
            moveLeft();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_PAGE_DOWN) {

            //下一页
            moveRight();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }
}
