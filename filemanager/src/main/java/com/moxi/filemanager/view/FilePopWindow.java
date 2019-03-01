package com.moxi.filemanager.view;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.moxi.filemanager.R;
import com.mx.mxbase.base.MyApplication;

/**
 * Created by xj on 2017/11/28.
 */

public class FilePopWindow {
    private Context context;
    private PopupWindow onefile;
    private FileOperateListener listener;

    public FilePopWindow(Context context, FileOperateListener listener) {
        this.context = context;
        this.listener = listener;
    }

    /**
     * 单个文件操作pop
     *
     * @param view
     */
    public void showManagerOneFilePopupWindow(View view, int position, int type) {
        // 一个自定义的布局，作为显示的内容
        View contentView = LayoutInflater.from(context).inflate(
                R.layout.pop_one_file_manager, null);
        // 设置按钮的点击事件

        TextView bluetooth_share = (TextView) contentView.findViewById(R.id.bluetooth_share);
        TextView copy = (TextView) contentView.findViewById(R.id.copy);
        TextView delete = (TextView) contentView.findViewById(R.id.delete);
        TextView move = (TextView) contentView.findViewById(R.id.move);
        TextView setting = (TextView) contentView.findViewById(R.id.setting);
        TextView descibe = (TextView) contentView.findViewById(R.id.descibe);
        TextView rename = (TextView) contentView.findViewById(R.id.rename);
        TextView export_pdf = (TextView) contentView.findViewById(R.id.export_pdf);

        onefile = new PopupWindow(contentView,
                MyApplication.dip2px(200), LinearLayout.LayoutParams.WRAP_CONTENT, true);

        onefile.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    onefile.dismiss();
                    onefile = null;
                    return true;
                }
                return false;
            }
        });

        if (type != 1) {
            setting.setVisibility(View.GONE);
        } else {
            setting.setVisibility(View.VISIBLE);
        }
        if (type==0){
            bluetooth_share.setVisibility(View.GONE);
            export_pdf.setVisibility(View.VISIBLE);
        }else {
            bluetooth_share.setVisibility(View.VISIBLE);
            export_pdf.setVisibility(View.GONE);
        }

        copy.setTag(position);
        delete.setTag(position);
        move.setTag(position);
        setting.setTag(position);
        descibe.setTag(position);
        rename.setTag(position);
        export_pdf.setTag(position);
        bluetooth_share.setTag(position);

        copy.setOnClickListener(onfile);
        delete.setOnClickListener(onfile);
        move.setOnClickListener(onfile);
        setting.setOnClickListener(onfile);
        descibe.setOnClickListener(onfile);
        rename.setOnClickListener(onfile);
        export_pdf.setOnClickListener(onfile);
        bluetooth_share.setOnClickListener(onfile);
        // 设置好参数之后再show
        onefile.setBackgroundDrawable(new BitmapDrawable());

        int[] location = new int[2];
        view.getLocationOnScreen(location);
        if (location[0] + view.getWidth() + 100 > MyApplication.ScreenWidth) {
            //左边
            onefile.showAtLocation(view, Gravity.NO_GRAVITY, location[0] - onefile.getWidth(), location[1]);
        } else {
            //右边
            onefile.showAtLocation(view, Gravity.NO_GRAVITY, location[0] + view.getWidth(), location[1]);
        }
    }

    View.OnClickListener onfile = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int style = 0;
            switch (v.getId()) {
                case R.id.copy://复制
                    style = 0;
                    break;
                case R.id.delete://删除
                    style = 1;
                    break;
                case R.id.move://移动
                    style = 2;
                    break;
                case R.id.setting://设置
                    style = 4;
                    break;
                case R.id.descibe://详情
                    style = 5;
                    //展示文件全路径，文件名称，文件属性
                    break;
                case R.id.rename://重命名
                    style = 3;
                    break;
                case R.id.bluetooth_share://蓝牙分享
                    style = 6;
                    break;
                case R.id.export_pdf://导出PDF
                    style = 7;
                    break;
                default:
                    break;
            }
            onefile.dismiss();
            onefile = null;
            if (listener!=null){
                listener.onOperate(style, (Integer) v.getTag());
            }
        }
    };
    public interface FileOperateListener{
        void onOperate(int style,int position);
    }
}
