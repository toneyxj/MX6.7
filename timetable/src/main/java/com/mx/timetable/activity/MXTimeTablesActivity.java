package com.mx.timetable.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.mx.mxbase.base.BaseActivity;
import com.mx.mxbase.constant.APPLog;
import com.mx.mxbase.interfaces.OnItemClickListener;
import com.mx.mxbase.utils.DividerItemDecoration;
import com.mx.mxbase.utils.GsonTools;
import com.mx.mxbase.utils.SharePreferceUtil;
import com.mx.mxbase.utils.Toastor;
import com.mx.mxbase.view.AlertDialog;
import com.mx.timetable.R;
import com.mx.timetable.adapter.NewAddCourseAdapter;
import com.mx.timetable.adapter.TimeTableCourseAdapter;
import com.mx.timetable.model.CourseModel;
import com.mx.timetable.model.CurrentCourse;
import com.mx.timetable.view.CustomPeriods;
import com.mx.timetable.view.TableTitleView;
import com.mx.timetable.view.Utils;
import com.mx.timetable.view.WeekTitleView;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;

/**
 * Created by Archer on 16/8/8.
 */
public class MXTimeTablesActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.table_title_time_tables)
    TableTitleView tableTitleView;
    @Bind(R.id.recycler_time_table_week)
    RecyclerView recyclerWeek;
    @Bind(R.id.wtv_time_tables_morning)
    WeekTitleView wtvMorning;
    @Bind(R.id.recycler_time_table_morning)
    RecyclerView recyclerMorning;
    @Bind(R.id.wtv_time_tables_afternoon)
    WeekTitleView wtvAfternoon;
    @Bind(R.id.recycler_time_table_after)
    RecyclerView recyclerAfter;
    @Bind(R.id.custom_periods_time_tables_morning)
    CustomPeriods customPeriods;
    @Bind(R.id.custom_periods_time_tables_afternoon)
    CustomPeriods customPeriodsAfter;
    @Bind(R.id.tv_time_table_edit)
    TextView tvEditTimeTable;
    @Bind(R.id.recycler_time_table_course)
    RecyclerView recyclerCourse;
    @Bind(R.id.ll_time_table_back)
    LinearLayout llBack;
    @Bind(R.id.ll_parent_layout)
    LinearLayout llParent;

    private TimeTableCourseAdapter courseAdapter;
    private List<String> morningCourse = new ArrayList<>();//String{"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""};
    private List<String> afternoonCourse = new ArrayList<>();//String[]{"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""};
    private String[] courses = new String[]{"语文", "数学", "英语", "物理", "化学", "生物", "历史", "地理", "政治"};
    private int[] courseRes = new int[]{R.mipmap.mx_img_course_yw, R.mipmap.mx_img_course_sx, R.mipmap.mx_img_course_yy,
            R.mipmap.mx_img_course_wl, R.mipmap.mx_img_course_hx, R.mipmap.mx_img_course_sw, R.mipmap.mx_img_course_ls
            , R.mipmap.mx_img_course_dl, R.mipmap.mx_img_course_zz};
    private int[] coursePressRes = new int[]{R.mipmap.mx_img_course_yw_press, R.mipmap.mx_img_course_sx_press, R.mipmap.mx_img_course_yy_press,
            R.mipmap.mx_img_course_wl_press, R.mipmap.mx_img_course_hx_press, R.mipmap.mx_img_course_sw_press, R.mipmap.mx_img_course_ls_press
            , R.mipmap.mx_img_course_dl_press, R.mipmap.mx_img_course_zz_press};

    private int[] addCourseRes = new int[]{R.mipmap.new_add_1, R.mipmap.new_add_2, R.mipmap.new_add_3, R.mipmap.new_add_4, R.mipmap.new_add_5, R.mipmap.new_add_6, R.mipmap.new_add_7, R.mipmap.new_add_8};
    private int[] addCourseResSelected = new int[]{R.mipmap.new_add_1_selected, R.mipmap.new_add_2_selected, R.mipmap.new_add_3_selected, R.mipmap.new_add_4_selected, R.mipmap.new_add_5_selected, R.mipmap.new_add_6_selected, R.mipmap.new_add_7_selected, R.mipmap.new_add_8_selected};

    private List<CourseModel> listCourse = new ArrayList<>();
    private SharePreferceUtil sharePreferceUtil;
    private MorningAdapter morningAdapter, afternoonAdapter;
    private String tempCourse = "";
    private boolean saveKeCheng = false;
    private Display display;
    private PopupWindow mPopupWindow;

    @Override
    protected int getMainContentViewId() {
        return R.layout.mx_activity_time_tables;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        init();
    }

    /**
     * 初始化view
     */
    private void init() {
        WindowManager windowManager = (WindowManager) this
                .getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
        //设置values
        tableTitleView.setLeftTitle("科目");
        tableTitleView.setRightTitle("星期");
        tableTitleView.setTitleSize(22);
        wtvMorning.setTitleSize(22);
        wtvMorning.setTitle("上午");
        wtvAfternoon.setTitle("下午");
        wtvAfternoon.setTitleSize(22);
        tvEditTimeTable.setOnClickListener(this);
        llBack.setOnClickListener(this);
        sharePreferceUtil = SharePreferceUtil.getInstance(this);
        customPeriods.setPeriods(new String[]{"第一节", "第二节", "第三节", "第四节"});
        customPeriodsAfter.setPeriods(new String[]{"第一节", "第二节", "第三节", "第四节"});
        //设置recycler
        recyclerWeek.setLayoutManager(new GridLayoutManager(this, 7));
        recyclerWeek.setAdapter(new WeekAdapter(this));

        //获取课表数据
        getSchedleDate();

        //设置课表内容点击时间
        setCourseClick();

        //设置底部课程
        setCourseData(courses);
    }

    /**
     * 显示新增课程
     */
    private void showPopNewAddCourse() {
        LayoutInflater mLayoutInflater = (LayoutInflater) this
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        View newadd = mLayoutInflater.inflate(
                R.layout.mx_add_cursor_pop_layout, null);
        LinearLayout llparent = (LinearLayout) newadd.findViewById(R.id.ll_parent_new_add_course);
        llparent.setLayoutParams(new FrameLayout.LayoutParams((int) (display
                .getWidth() * 0.85), LinearLayout.LayoutParams.WRAP_CONTENT));
        final EditText etCourseName = (EditText) newadd.findViewById(R.id.et_course_name);

        TextView tvSure = (TextView) newadd.findViewById(R.id.tv_course_sure);
        RecyclerView recyclerview = (RecyclerView) newadd.findViewById(R.id.recycler_new_add_course);
        DividerItemDecoration dividerVERTICAL = new DividerItemDecoration(DividerItemDecoration.VERTICAL);
        dividerVERTICAL.setSize(1);
        dividerVERTICAL.setColor(0xFF000000);
        DividerItemDecoration dividerHORIZONTAL = new DividerItemDecoration(DividerItemDecoration.HORIZONTAL);
        dividerHORIZONTAL.setSize(1);
        dividerHORIZONTAL.setColor(0xFF000000);
        recyclerview.addItemDecoration(dividerVERTICAL);//添加竖线
        recyclerview.addItemDecoration(dividerHORIZONTAL);//添加横线
        recyclerview.setLayoutManager(new GridLayoutManager(this, 4));
        final NewAddCourseAdapter adapter = new NewAddCourseAdapter(this);
        recyclerview.setAdapter(adapter);

        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                adapter.setCurrentChose(position);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });

        tvSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String courseName = etCourseName.getText().toString();
                if (courseName.equals("")) {
                    Toastor.showToast(MXTimeTablesActivity.this, "新增课程名称不能为空");
                    return;
                }
                if (adapter.getCurrentChose() == -99) {
                    Toastor.showToast(MXTimeTablesActivity.this, "请选择新增课程图标");
                    return;
                }
                if (mPopupWindow.isShowing() && mPopupWindow != null) {
                    boolean exist = DataSupport.isExist(CourseModel.class, "courseName=" + "'" + courseName + "'");
                    List<String> list = Arrays.asList(courses);
                    if (exist || list.contains(courseName)) {
                        Toastor.showToast(MXTimeTablesActivity.this, "新增课程名已存在请重新输入");
                    } else {
                        List<CourseModel> listtemp = DataSupport.findAll(CourseModel.class);
                        if (listtemp != null && listtemp.size() < 10) {
                            CourseModel courseModel = new CourseModel();
                            courseModel.setCourseId(adapter.getCurrentChose());
                            courseModel.setChosen(false);
                            courseModel.setCourseRes(addCourseRes[adapter.getCurrentChose()]);
                            courseModel.setCoursePressRes(addCourseResSelected[adapter.getCurrentChose()]);
                            courseModel.setCourseName(courseName);
                            if (courseModel.save()) {
                                mPopupWindow.dismiss();
                                mPopupWindow = null;
                                setCourseData(courses);
                            }
                        } else {
                            if (mPopupWindow != null && mPopupWindow.isShowing()) {
                                mPopupWindow.dismiss();
                                mPopupWindow = null;
                            }
                            Toastor.showToast(MXTimeTablesActivity.this, "新增课程已达上限");
                        }
                    }
                }
            }
        });

        mPopupWindow = new PopupWindow(newadd, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(0));

        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                mPopupWindow.dismiss();
            }
        });

        mPopupWindow.setTouchInterceptor(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    mPopupWindow.dismiss();
                    return true;
                }
                return false;
            }
        });
        mPopupWindow.showAtLocation(llParent, Gravity.CENTER, 0, 0);
    }

    private void setCourseClick() {
        morningAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                if (tvEditTimeTable.getText().toString().equals("完成")) {
                    if (morningCourse.get(position).equals("")) {
                        morningCourse.set(position, tempCourse);//[position] = tempCourse;
                        morningAdapter.notifyDataSetChanged();
                        saveKeCheng = true;
                    } else {
                        new AlertDialog(MXTimeTablesActivity.this).builder().setTitle("提示").setCancelable(false).setMsg("" +
                                "是否确定更改课程").setNegativeButton("确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                morningCourse.set(position, tempCourse);
                                morningAdapter.notifyDataSetChanged();
                                saveKeCheng = true;
                            }
                        }).setPositiveButton("取消", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                            }
                        }).show();
                    }
                }
            }

            @Override
            public void onItemLongClick(View view, final int position) {
                if (!morningCourse.get(position).equals("")) {
                    new AlertDialog(MXTimeTablesActivity.this).builder().setTitle("提示").setMsg("确认清空课程?").setCancelable(false).setNegativeButton("确定", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            morningCourse.set(position, "");
                            morningAdapter.notifyDataSetChanged();
                            saveKeCheng = true;
                            saveUpdate();
                        }
                    }).setPositiveButton("取消", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    }).show();
                }
            }
        });

        afternoonAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                if (tvEditTimeTable.getText().toString().equals("完成")) {
                    if (afternoonCourse.get(position).equals("")) {
                        afternoonCourse.set(position, tempCourse);//[position] = tempCourse;
                        afternoonAdapter.notifyDataSetChanged();
                        saveKeCheng = true;
                    } else {
                        new AlertDialog(MXTimeTablesActivity.this).builder().setTitle("提示").setMsg("确认更改课程?").setCancelable(false).setNegativeButton("确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                afternoonCourse.set(position, tempCourse);
                                afternoonAdapter.notifyDataSetChanged();
                                saveKeCheng = true;
                            }
                        }).setPositiveButton("取消", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                            }
                        }).show();
                    }
                }
            }

            @Override
            public void onItemLongClick(View view, final int position) {
                if (!afternoonCourse.get(position).equals("")) {
                    new AlertDialog(MXTimeTablesActivity.this).builder().setTitle("提示").setMsg("确认清空课程?").setCancelable(false).setNegativeButton("确定", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            afternoonCourse.set(position, "");
                            afternoonAdapter.notifyDataSetChanged();
                            saveKeCheng = true;
                            saveUpdate();
                        }
                    }).setPositiveButton("取消", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    }).show();
                }
            }
        });
    }

    /**
     * 获取课程表数据
     */
    private void getSchedleDate() {
        CurrentCourse ccMorning = GsonTools.getPerson(sharePreferceUtil.getString("mx_ke_biao_morning"), CurrentCourse.class);
        if (ccMorning != null && ccMorning.getListCours() != null) {
            morningCourse = ccMorning.getListCours();
        } else {
            for (int i = 0; i < 28; i++) {
                morningCourse.add("");
            }
        }
        morningAdapter = new MorningAdapter(this, morningCourse);
        recyclerMorning.setLayoutManager(new GridLayoutManager(this, 7));
        recyclerMorning.setAdapter(morningAdapter);

        CurrentCourse ccAfternoon = GsonTools.getPerson(sharePreferceUtil.getString("mx_ke_biao_afternoon"), CurrentCourse.class);
        if (ccAfternoon != null && ccAfternoon.getListCours() != null) {
            afternoonCourse = ccAfternoon.getListCours();
        } else {
            for (int i = 0; i < 28; i++) {
                afternoonCourse.add("");
            }
        }
        afternoonAdapter = new MorningAdapter(this, afternoonCourse);
        recyclerAfter.setLayoutManager(new GridLayoutManager(this, 7));
        recyclerAfter.setAdapter(afternoonAdapter);
    }

    /**
     * 设置课程数据
     *
     * @param courses
     */
    private void setCourseData(final String[] courses) {
        listCourse.clear();
        for (int i = 0; i < courses.length; i++) {
            CourseModel cm = new CourseModel();
            cm.setChosen(false);
            cm.setCourseRes(courseRes[i]);
            cm.setCoursePressRes(coursePressRes[i]);
            cm.setCourseName(courses[i]);
            listCourse.add(cm);
        }
        List<CourseModel> listNew = DataSupport.findAll(CourseModel.class);
        listCourse.addAll(listNew);

        CourseModel cm1 = new CourseModel();
        cm1.setChosen(false);
        cm1.setCourseRes(R.mipmap.zhankai_icon);
        cm1.setCoursePressRes(R.mipmap.zhankai_icon);
        cm1.setCourseName("新增");
        listCourse.add(cm1);

        recyclerCourse.setLayoutManager(new GridLayoutManager(this, 5));
        courseAdapter = new TimeTableCourseAdapter(this, listCourse);
        recyclerCourse.setAdapter(courseAdapter);
        courseAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (position == listCourse.size() - 1) {
                    if (mPopupWindow != null && !mPopupWindow.isShowing()) {
                        mPopupWindow.showAtLocation(llParent, Gravity.CENTER, 0, 0);
                    } else {
                        showPopNewAddCourse();
                    }
                } else {
                    for (int i = 0; i < listCourse.size(); i++) {
                        if (i == position) {
                            listCourse.get(i).setChosen(true);
                            tempCourse = listCourse.get(i).getCourseName();
                        } else {
                            listCourse.get(i).setChosen(false);
                        }
                        courseAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onItemLongClick(View view, final int position) {
                new AlertDialog(MXTimeTablesActivity.this).builder().setTitle("提示").setMsg("是否删除此课程?").setCancelable(false).setNegativeButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            int result = DataSupport.deleteAll(CourseModel.class, "courseName = " + "'" + listCourse.get(position).getCourseName() + "'");
                            if (result > 0) {
                                Toastor.showToast(MXTimeTablesActivity.this, "删除成功");
                                setCourseData(courses);
                            } else {
                                Toastor.showToast(MXTimeTablesActivity.this, "默认课程无法删除");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).setPositiveButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                }).show();
            }
        });
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
    public void onBackPressed() {
        APPLog.e("onBackPressed");
        if (saveKeCheng) {
            new AlertDialog(MXTimeTablesActivity.this).builder().setTitle("提示").setMsg("是否放弃本次修改?").setCancelable(false).setNegativeButton("确定", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MXTimeTablesActivity.this.finish();
                }
            }).setPositiveButton("取消", new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            }).show();
        } else {
            MXTimeTablesActivity.this.finish();
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_time_table_edit:
                if (tvEditTimeTable.getText().toString().equals("编辑课程表")) {
                    tvEditTimeTable.setText("完成");
                    saveKeCheng = true;
                } else {
                    tvEditTimeTable.setText("编辑课程表");
                    saveUpdate();
                    tempCourse = "";
                    new AlertDialog(this).builder().setMsg("课表修改成功！").setPositiveButton("确定", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    }).show();
                    for (int i = 0; i < listCourse.size(); i++) {
                        listCourse.get(i).setChosen(false);
                        courseAdapter.notifyDataSetChanged();
                    }
                }
                break;
            case R.id.ll_time_table_back:
                if (saveKeCheng) {
                    new AlertDialog(MXTimeTablesActivity.this).builder().setTitle("提示").setMsg("是否放弃本次修改?").setCancelable(false).setNegativeButton("确定", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            MXTimeTablesActivity.this.finish();
                        }
                    }).setPositiveButton("取消", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    }).show();
                } else {
                    this.finish();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 更新保存课表
     */
    private void saveUpdate() {
        CurrentCourse cMorning = new CurrentCourse();
        CurrentCourse cAfternoon = new CurrentCourse();
        List<String> listMorning = new ArrayList<>();
        List<String> listAfternoon = new ArrayList<>();
        for (int i = 0; i < 28; i++) {
            listMorning.add(morningCourse.get(i));
            listAfternoon.add(afternoonCourse.get(i));
        }
        saveKeCheng = false;
        cMorning.setListCours(listMorning);
        cAfternoon.setListCours(listAfternoon);
        sharePreferceUtil.setCache("mx_ke_biao_morning", GsonTools.obj2json(cMorning));
        sharePreferceUtil.setCache("mx_ke_biao_afternoon", GsonTools.obj2json(cAfternoon));
    }

    /**
     * 星期适配器
     */
    public class WeekAdapter extends RecyclerView.Adapter {

        private String[] weeks = new String[]{"星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日"};
        private Context context;

        public WeekAdapter(Context context) {
            this.context = context;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.mx_recycler_week_name_item, parent, false);
            GridLayoutManager.LayoutParams params = (GridLayoutManager.LayoutParams) view.getLayoutParams();
            params.height = (int) Utils.dp2px(context.getResources(), 470 / customPeriods.getPeriods().length);
            view.setLayoutParams(params);
            return new WeekViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (position == 0) {
                ((WeekViewHolder) holder).imgWeekFirst.setVisibility(View.GONE);
            } else {
                ((WeekViewHolder) holder).imgWeekFirst.setVisibility(View.GONE);
            }
            ((WeekViewHolder) holder).tvWeekName.setText(weeks[position]);
            ((WeekViewHolder) holder).weekTitleView.setTitleSize(22);
            ((WeekViewHolder) holder).weekTitleView.setTitle(weeks[position]);
        }

        @Override
        public int getItemCount() {
            return weeks.length;
        }

        class WeekViewHolder extends RecyclerView.ViewHolder {

            TextView tvWeekName;
            ImageView imgWeekFirst;
            WeekTitleView weekTitleView;

            public WeekViewHolder(View itemView) {
                super(itemView);
                tvWeekName = (TextView) itemView.findViewById(R.id.tv_recycler_item_week);
                imgWeekFirst = (ImageView) itemView.findViewById(R.id.img_recycler_item_first);
                weekTitleView = (WeekTitleView) itemView.findViewById(R.id.wtv_recycler_item_week);
            }
        }
    }


    /**
     * 课表内容适配器
     */
    public class MorningAdapter extends RecyclerView.Adapter {

        private List<String> course;
        private Context context;
        private OnItemClickListener onItemClickListener;

        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            this.onItemClickListener = onItemClickListener;
        }

        public MorningAdapter(Context context, List<String> course) {
            this.context = context;
            this.course = course;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.mx_recycler_time_table_item, parent, false);
            GridLayoutManager.LayoutParams params = (GridLayoutManager.LayoutParams) view.getLayoutParams();
            params.height = (int) Utils.dp2px(context.getResources(), 240 / customPeriods.getPeriods().length);
            view.setLayoutParams(params);
            return new MorningViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
            if (position == 0) {
                ((MorningViewHolder) holder).imgWeekFirst.setVisibility(View.GONE);
            } else {
                ((MorningViewHolder) holder).imgWeekFirst.setVisibility(View.GONE);
            }
            if (position > 20) {
                ((MorningViewHolder) holder).imgTimeBottom.setVisibility(View.GONE);
            } else {
                ((MorningViewHolder) holder).imgTimeBottom.setVisibility(View.VISIBLE);
            }
            ((MorningViewHolder) holder).tvWeekName.setText(course.get(position));
            ((MorningViewHolder) holder).weekTitleView.setTitleSize(22);
            ((MorningViewHolder) holder).weekTitleView.setTitle(course.get(position));
            if (onItemClickListener != null) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onItemClickListener.onItemClick(holder.itemView, position);
                    }
                });
                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        onItemClickListener.onItemLongClick(holder.itemView, position);
                        if (saveKeCheng) {
                            return false;
                        } else {
                            return true;
                        }
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return course != null ? course.size() : 0;
        }

        class MorningViewHolder extends RecyclerView.ViewHolder {

            TextView tvWeekName;
            ImageView imgWeekFirst;
            WeekTitleView weekTitleView;
            ImageView imgTimeBottom;

            public MorningViewHolder(View itemView) {
                super(itemView);
                tvWeekName = (TextView) itemView.findViewById(R.id.tv_recycler_item_week);
                imgWeekFirst = (ImageView) itemView.findViewById(R.id.img_recycler_item_first);
                weekTitleView = (WeekTitleView) itemView.findViewById(R.id.wtv_recycler_item_time_table);
                imgTimeBottom = (ImageView) itemView.findViewById(R.id.img_recycler_item_time_table);
            }
        }
    }
}
