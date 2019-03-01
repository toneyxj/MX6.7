package com.moxi.calendar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.view.PagerAdapter;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moxi.calendar.adapter.CalendarAdapter;
import com.moxi.calendar.calenderUtils.Lunar;
import com.moxi.calendar.calenderUtils.TimeUtils;
import com.moxi.calendar.model.DateInfo;
import com.moxi.calendar.model.EventBeen;
import com.moxi.calendar.utils.TimeChangeUtils;
import com.moxi.calendar.utils.TimeStringUtils;
import com.moxi.calendar.view.MyViewPager;
import com.mx.mxbase.constant.APPLog;
import com.mx.mxbase.view.LinerlayoutInter;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import butterknife.Bind;

import static com.moxi.calendar.adapter.CalendarAdapter.selectedPosition;

public class MainActivity extends BaseCalendarActivity implements MyViewPager.ViewpagerListener, View.OnClickListener, LinerlayoutInter.LinerLayoutInter {
    public static final String sqlSlect = "id,saveDate,saveTime,name,whetherNotify,setNotify,notifyTime,remark";
    public static final int counts = 1000;
    @Bind(R.id.back_button)
    TextView back_button;
    @Bind(R.id.show_current_year_month)
    TextView show_current_year_month;
    @Bind(R.id.month)
    TextView month;
    @Bind(R.id.daily)
    TextView daily;
    @Bind(R.id.select_data)
    ImageButton select_data;
    @Bind(R.id.more_motification)
    ImageButton more_motification;
    @Bind(R.id.next_year_month)
    TextView next_year_month;
    @Bind(R.id.current_date)
    TextView current_date;

    @Bind(R.id.add_new_event)
    TextView add_new_event;

    @Bind(R.id.add_events_layout)
    LinerlayoutInter add_events_layout;

    @Bind(R.id.viewpager)
    MyViewPager viewPager;

    /**
     * 装载日期数据
     */
    public MyPagerAdapter pagerAdapter = null;
    private int currPager = 500;
    /**
     * 和日历gridview相关变量
     */
    private GridView gridView = null;
    public CalendarAdapter adapter = null;
    private GridView currentView = null;
    public List<DateInfo> list = null;
    /**
     * 第一个页面的年月
     */
    private int currentYear;
    private int currentMonth;
    private List<EventBeen> listEvents = new ArrayList<>();

    @Override
    protected int getMainContentViewId() {
        return R.layout.activity_main;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        super.onActivityCreated(activity, savedInstanceState);
        viewPager.setLayoutInter(this);
        back_button.setOnClickListener(this);
        add_new_event.setOnClickListener(this);
        month.setOnClickListener(this);
        daily.setOnClickListener(this);
        more_motification.setOnClickListener(this);
        select_data.setOnClickListener(this);

        add_events_layout.setLayoutInter(this);
        initLayout();
        //注册后台下载服务
        Intent intent = new Intent(MainActivity.this, LocationService.class);
        startService(intent);

        back_button.setText((new Lunar(System.currentTimeMillis()).getCyclicalDateString()));

//        getHandler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
////                wakeUpAndUnlock();
//                StartActivityUtils.sendSimulatorClick(MainActivity.this,3);
//            }
//        },10000);
    }
    private void initLayout() {
        pagerAdapter = null;
        //设置获得当前时间字符串
        TimeChangeUtils.currentTimeStr = TimeChangeUtils.getTime(System.currentTimeMillis());

        setDayData();
        initData();

        pagerAdapter = new MyPagerAdapter();
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(MainActivity.counts);
        viewPager.setPageMargin(0);

        setStyle(0);
        getCurrentEvents();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_button://月
                this.finish();
                break;
            case R.id.month://月
                setStyle(0);
                break;
            case R.id.daily://日
                setStyle(1);
                break;
            case R.id.select_data://选择跳转日期
                //日期
//                int data=CalendarAdapter.selectedPosition;
                SelectDataActivity.StartSelectDataActivity(this, currentThisYear, CurrentThisMonth);
                break;
            case R.id.more_motification://更多
//                DataSupport.findBySQL()
                //show_current_year_month.getText().toString()
                ListEventActivity.startListEvent(MainActivity.this, 10, currentThisYear + "年" + CurrentThisMonth + "月" + CalendarAdapter.selectedPosition + "日");
                break;
            case R.id.add_new_event://添加新事件"yyyy-MM-dd HH:mm"

                String data=String.valueOf(currentThisYear) +"-"+ String.valueOf(CurrentThisMonth) +"-"+ String.valueOf(CalendarAdapter.selectedPosition) + " "+xjTimeUtils.getHour()+":00";
                AddNewEventActivity.startActivity(MainActivity.this, 10, show_current_year_month.getText().toString(),null, data);
                break;
            default:
                break;
        }
    }

    private void getCurrentEvents() {
        listEvents.addAll(DataSupport.findAll(EventBeen.class));
        APPLog.e(listEvents.toString());
    }

    /**
     * 当前选择显示模式,0选择月，其他选择日
     */
    private int currentStyle = -1;

    private void setStyle(int style) {
        if (currentStyle == style) return;
        currentStyle = style;

        if (currentStyle == 0) {
            daily.setBackgroundResource(R.drawable.arc_right_di_white);
            daily.setTextColor(Color.BLACK);

            month.setBackgroundColor(Color.TRANSPARENT);
            month.setTextColor(Color.WHITE);
        } else {
            month.setBackgroundResource(R.drawable.arc_left_di_white);
            month.setTextColor(Color.BLACK);

            daily.setBackgroundColor(Color.TRANSPARENT);
            daily.setTextColor(Color.WHITE);
        }
    }

    @Override
    public void moveNext() {
        int position = viewPager.getCurrentItem();
        if (position >= pagerAdapter.getCount() - 1) {
            return;
        }
        ++position;
//        int year = TimeUtils.getTimeByPosition(position, currentYear, currentMonth, "year");
//        int month = TimeUtils.getTimeByPosition(position, currentYear, currentMonth, "month");
//        next_year_month.setText(year + " 年 " + month + " 月");

        viewPager.setCurrentItem(position, false);
    }

    @Override
    public void moveLast() {
        int position = viewPager.getCurrentItem();
        if (position <= 0) {
            return;
        }
        --position;
        viewPager.setCurrentItem(position, false);
    }

    @Override
    public void timeChange() {
    }

    @Override
    public void dataChange() {
        initLayout();
    }

    /**
     * 设置日期数据
     */
    private void setDayData() {
        show_current_year_month.setText(xjTimeUtils.getYear() + "年\t\t" + xjLunar.getMonth(xjTimeUtils.getMonth() - 1));
        current_date.setText(xjTimeUtils.currentTime(2));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        if (requestCode == 10) {
            //处理事件
            getclickEvents();
            adapter.setEventDateBeens(getEventTimes());
            adapter.notifyDataSetChanged();
        } else if (requestCode == 11) {
            //时间跳转
            Bundle bundle = data.getExtras();
            final int year = bundle.getInt("year");
            final int month = bundle.getInt("month");

            if (year != currentThisYear || month != CurrentThisMonth) {
                int position = (year - currentThisYear) * 12 + (month - CurrentThisMonth);
                int currentItem = viewPager.getCurrentItem();
                currentItem+=position;
                if (currentItem<50||currentItem>MainActivity.counts*2-50) {
                    currentYear = year;
                    currentMonth = month;

                    currentThisYear = currentYear;
                    CurrentThisMonth = currentMonth;
                    pagerAdapter = new MyPagerAdapter();
                    viewPager.setAdapter(pagerAdapter);
                    viewPager.setCurrentItem(MainActivity.counts);
                    viewPager.setPageMargin(0);
                    getCurrentEvents();
                    setDataShow();
                }else {
                    viewPager.setCurrentItem(currentItem);
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

    private int currentThisYear;
    private int CurrentThisMonth;

    /**
     * 初始化日历的gridview
     */
    private GridView initCalendarView(int position) {
        currentThisYear = TimeUtils.getTimeByPosition(position, currentYear, currentMonth, "year");
        CurrentThisMonth = TimeUtils.getTimeByPosition(position, currentYear, currentMonth, "month");
        String formatDate = TimeUtils.getFormatDate(currentThisYear, CurrentThisMonth);
        try {
            list = TimeUtils.initCalendar(formatDate, CurrentThisMonth);
        } catch (Exception e) {
            finish();
        }
        //获得一个月的前后时间
        gridView = new GridView(this);
        adapter = new CalendarAdapter(this, list,getEventTimes());
        gridView.setAdapter(adapter);
        gridView.setNumColumns(7);
        gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        gridView.setGravity(Gravity.CENTER);
        gridView.setOnItemClickListener(new OnItemClickListenerImpl(adapter, this));
        return gridView;
    }

    private HashSet<Integer> getEventTimes(){
        HashSet<Integer> eventTimes=new HashSet<>();
        long[] timelongs=TimeStringUtils.getTimeLongs(currentThisYear,CurrentThisMonth);
        Cursor cursor = DataSupport.findBySQL("select " + MainActivity.sqlSlect + " from EventBeen where saveTime >='" + timelongs[0] + "' and saveTime <'"+timelongs[1]+"' order by saveTime ASC");
        while (cursor.moveToNext()) {
            String notifyTime = cursor.getString(6);
            try{
               String yearMD= notifyTime.split(" ")[0];
                String day=(yearMD.split("-")[2]);
                eventTimes.add(Integer.parseInt(day));
            }catch (Exception e){}
        }
        return eventTimes;
    }

    /**
     * 单页事件展示个数
     */
    private int pageEventsSize = 3;
    private int indexPage = 0;
    private int totalIndex = 0;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            int height = add_events_layout.getMeasuredHeight();//
            int itemheight = ThisApplication.dip2px(50);
            pageEventsSize = height / itemheight;
            pageEventsSize=6;
        }
    }

    /**
     * 滑动事件列表
     */
    @Override
    public void moveRight() {
        if (indexPage < totalIndex - 1) {
            indexPage++;
            reInit();
        }
    }

    /**
     * 滑动事件列表
     */
    @Override
    public void moveLeft() {
        if (indexPage > 0) {
            indexPage--;
            reInit();
        }
    }

    private void reInit() {
        totalIndex = listEvents.size() / pageEventsSize;
        totalIndex += (listEvents.size() % pageEventsSize == 0) ? 0 : 1;
        if (totalIndex == 0) totalIndex = 1;

        if (totalIndex <= indexPage) {
            indexPage = totalIndex - 1;
        }
        if (indexPage == totalIndex - 1) {
            addview(listEvents.subList(indexPage * pageEventsSize, listEvents.size()));
        } else {
            addview(listEvents.subList(indexPage * pageEventsSize, (indexPage + 1) * pageEventsSize));
        }
    }

    private void addview(List<EventBeen> list) {
        add_events_layout.removeAllViews();
        LayoutInflater inflater = getLayoutInflater();
        int i = 0;
        for (EventBeen been : list) {
            View view = inflater.inflate(R.layout.event_item, null);
            LinearLayout click_layout = (LinearLayout) view.findViewById(R.id.click_layout);
            TextView show_time = (TextView) view.findViewById(R.id.show_time);
            TextView events_text = (TextView) view.findViewById(R.id.events_text);
            String time = TimeChangeUtils.getTime(been.saveTime);
            time = time.split(" ")[1];

            if (TimeChangeUtils.judgeFormerly(been.saveTime)) {
                show_time.setTextColor(getResources().getColor(R.color.colorGray));
                events_text.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                show_time.setTextColor(getResources().getColor(R.color.colorBlack));
            }

            String title = been.name;
            show_time.setText(time);
            String value=title;
            value += "\t\t\t" + ((been.remark.equals("")) ? "" : "备注:" + been.remark);
            events_text.setText(getStyle(this,value,title,R.style.size22));

            click_layout.setTag(i);
            click_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int currentClick = (int) v.getTag() + (indexPage * pageEventsSize);
                    AddNewEventActivity.startActivity(MainActivity.this, 10, "编辑事件", listEvents.get(currentClick),"");
                }
            });

            add_events_layout.addView(view);
            i++;
        }

    }

    /**
     * 拼接字符串颜色
     *
     * @param context        当前上下文
     * @param value          拼接原值
     * @param span           需要拼接的数据集
     * @param changeResource 颜色原值
     * @return 返回拼接后的字符串stple
     */
    private   SpannableStringBuilder getStyle(Context context, String value, String span, int changeResource) {
        SpannableStringBuilder style = new SpannableStringBuilder(value);
        int start = value.indexOf(span);
        int end = start + span.length();
        style.setSpan(new TextAppearanceSpan(context, changeResource), start, end,
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return style;
    }
    /**
     * viewpager的适配器，从第500页开始，最多支持0-1000页
     */
    private class MyPagerAdapter extends PagerAdapter {

        @Override
        public void setPrimaryItem(ViewGroup container, int position,
                                   Object object) {
            currentView = (GridView) object;
            adapter = (CalendarAdapter) currentView.getAdapter();
            adapter.changeSelect();
            changePosition(position);
        }

        @Override
        public int getCount() {
            return MainActivity.counts * 2;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            GridView gv = initCalendarView(position);
            gv.setId(position);
            container.addView(gv);
            return gv;
        }
    }

    private void changePosition(int position) {
        int year = TimeUtils.getTimeByPosition(position, currentYear, currentMonth, "year");
        int month = TimeUtils.getTimeByPosition(position, currentYear, currentMonth, "month");
        next_year_month.setText(year + " 年 " + month + " 月");

        currentThisYear = year;
        CurrentThisMonth = month;
        //日期
        getclickEvents();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        currentYear = TimeUtils.getCurrentYear();
        currentMonth = TimeUtils.getCurrentMonth();

        currentThisYear = currentYear;
        CurrentThisMonth = currentMonth;

        selectedPosition = TimeUtils.getCurrentDay();
        setDataShow();
    }

    private void setDataShow() {
        String formatDate = TimeUtils.getFormatDate(currentYear, currentMonth);
        try {
            list = TimeUtils.initCalendar(formatDate, currentMonth);
        } catch (Exception e) {
            finish();
        }
        next_year_month.setText(currentYear + " 年 " + currentMonth + " 月");
    }

    /**
     * 获得当日事件
     */
    public void getclickEvents() {
        listEvents.clear();
        String value = currentThisYear + "-" + getAddZero(CurrentThisMonth) + "-" + getAddZero(selectedPosition);
        Cursor cursor = DataSupport.findBySQL("select " + sqlSlect + " from EventBeen where saveDate='" + value + "' order by saveTime ASC");
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
            listEvents.add(eventBeen);
        }
//        APPLog.e("当日数据集合=" + listEvents.toString());
        reInit();
    }

    private String getAddZero(int value) {
        return value < 10 ? ("0" + value) : String.valueOf(value);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
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
