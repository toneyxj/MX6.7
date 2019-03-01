package com.moxi.filemanager;

import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.mx.mxbase.base.BaseActivity;

/**
 * Created by Administrator on 2016/8/17.
 */
public abstract class BaseFileManagerActivity extends BaseActivity {
    public boolean isSelectMore;
    PopupWindow manager;

    public void showManagerPopupWindow(View view) {
        // 一个自定义的布局，作为显示的内容
        View contentView = LayoutInflater.from(this).inflate(
                R.layout.pop_manager_layout, null);
        // 设置按钮的点击事件

        TextView new_floder = (TextView) contentView.findViewById(R.id.new_floder);
        TextView double_select = (TextView) contentView.findViewById(R.id.double_select);

        manager = new PopupWindow(contentView,
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);

        manager.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    manager.dismiss();
                    manager=null;
                    return true;
                }
                return false;
            }
        });

        new_floder.setOnClickListener(clickListener);
        double_select.setOnClickListener(clickListener);
        // 设置好参数之后再show
        manager.setBackgroundDrawable(new BitmapDrawable());
        manager.showAsDropDown(view,-50,5);

    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.new_floder://新建文件
                    newFloder();
                    break;
                case R.id.double_select://多选模式
                    isSelectMore=true;
                    selectMore();
                    break;
                default:
                    break;
            }
            updataManader();
        }
    };
    /**
     * 点击管理更新
     */
    public void updataManader() {
        manager.dismiss();
        manager=null;
    }

    public abstract void newFloder();
    public abstract void selectMore();

    /**
     * 排序方式 0：按名称，1：按大小，2：按创建时间,3按类型排序
     */
    public int sortStyle=0;
    public PopupWindow popSort;
    private View view;

    public void showSortPopupWindow(View view) {
        this.view=view;
            View contentView = LayoutInflater.from(this).inflate(
                    R.layout.pop_file_manager_sort_layout, null);
            // 设置按钮的点击事件
            LinearLayout sort_name = (LinearLayout) contentView.findViewById(R.id.sort_name);
            LinearLayout sort_big = (LinearLayout) contentView.findViewById(R.id.sort_big);
            LinearLayout sort_create_time = (LinearLayout) contentView.findViewById(R.id.sort_create_time);
            LinearLayout sort_type = (LinearLayout) contentView.findViewById(R.id.sort_type);

            ImageView show_index_one = (ImageView) contentView.findViewById(R.id.show_index_one);
            ImageView show_index_two = (ImageView) contentView.findViewById(R.id.show_index_two);
             ImageView show_index_three = (ImageView) contentView.findViewById(R.id.show_index_three);
             ImageView show_index_four = (ImageView) contentView.findViewById(R.id.show_index_four);
            if (sortStyle == 3) {
                show_index_three.setVisibility(View.VISIBLE);
            } else if (sortStyle == 1) {
                show_index_two.setVisibility(View.VISIBLE);
            } else if (sortStyle==2){
                show_index_four.setVisibility(View.VISIBLE);
            }else{
                show_index_one.setVisibility(View.VISIBLE);
            }

        popSort = new PopupWindow(contentView,
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);

        popSort.setTouchInterceptor(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                        popSort.dismiss();
                        popSort=null;
                        return true;
                    }
                    return false;
                }
            });

        sort_name.setOnClickListener(sortListener);
        sort_big.setOnClickListener(sortListener);
        sort_type.setOnClickListener(sortListener);
        sort_create_time.setOnClickListener(sortListener);
            // 设置好参数之后再show
        popSort.setBackgroundDrawable(new BitmapDrawable());
        popSort.showAsDropDown(view,180,5);
    }
    View.OnClickListener sortListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.sort_name://按名称排序
                    sortStyle=0;
                    break;
                case R.id.sort_big://按大小排序
                    sortStyle=1;
                    break;
                case R.id.sort_create_time://按创建时间排序
                    sortStyle=3;
                    break;
                case R.id.sort_type://按类型排序
                    sortStyle=2;
                    break;
                default:
                    break;
            }
            updataSort();
        }
    };

    /**
     * 点击管理更新
     */
    public void updataSort() {
        dismissSort();
    }

    @Override
    protected void onStop() {
        super.onStop();
        dismissSort();
    }

    public void dismissSort(){
        if (popSort!=null) {
            if (!popSort.isShowing()){
                if (view!=null){
                    popSort.showAsDropDown(view);
                }
                popSort.dismiss();
            }else {
                popSort.dismiss();
            }
            popSort = null;
        }
    }

}
