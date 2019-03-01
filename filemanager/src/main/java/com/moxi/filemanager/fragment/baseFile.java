package com.moxi.filemanager.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.moxi.filemanager.view.FilePopWindow;
import com.mx.mxbase.base.baseFragment;

/**
 * Created by Administrator on 2016/9/1.
 */
public abstract class baseFile extends baseFragment implements FilePopWindow.FileOperateListener {
private FilePopWindow filePopWindow;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        filePopWindow=new FilePopWindow(getContext(),this);
        return super.onCreateView(inflater, container, savedInstanceState);
    }
    public void showManagerOneFilePopupWindow(View view, int position,int type) {
        filePopWindow.showManagerOneFilePopupWindow(view,position,type);
    }
    //    private PopupWindow onefile;
//
//    /**
//     * 单个文件操作pop
//     *
//     * @param view
//     */
//    public void showManagerOneFilePopupWindow(View view, int position,int type) {
//        // 一个自定义的布局，作为显示的内容
//        View contentView = LayoutInflater.from(getActivity()).inflate(
//                R.layout.pop_one_file_manager, null);
//        // 设置按钮的点击事件
//
//        TextView copy = (TextView) contentView.findViewById(R.id.copy);
//        TextView delete = (TextView) contentView.findViewById(R.id.delete);
//        TextView move = (TextView) contentView.findViewById(R.id.move);
//        TextView setting = (TextView) contentView.findViewById(R.id.setting);
//        TextView descibe = (TextView) contentView.findViewById(R.id.descibe);
//        TextView rename = (TextView) contentView.findViewById(R.id.rename);
//
//        onefile = new PopupWindow(contentView,
//                MyApplication.dip2px(160), LinearLayout.LayoutParams.WRAP_CONTENT, true);
//
//        onefile.setTouchInterceptor(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
//                    onefile.dismiss();
//                    onefile = null;
//                    return true;
//                }
//                return false;
//            }
//        });
//
//        if (type!=1){
//            setting.setVisibility(View.GONE);
//        }else {
//            setting.setVisibility(View.VISIBLE);
//        }
//
//        copy.setTag(position);
//        delete.setTag(position);
//        move.setTag(position);
//        setting.setTag(position);
//        descibe.setTag(position);
//        rename.setTag(position);
//
//        copy.setOnClickListener(onfile);
//        delete.setOnClickListener(onfile);
//        move.setOnClickListener(onfile);
//        setting.setOnClickListener(onfile);
//        descibe.setOnClickListener(onfile);
//        rename.setOnClickListener(onfile);
//        // 设置好参数之后再show
//        onefile.setBackgroundDrawable(new BitmapDrawable());
//
//        int[] location = new int[2];
//        view.getLocationOnScreen(location);
//        if (location[0] + view.getWidth()+100>MyApplication.ScreenWidth){
//            //左边
//            onefile.showAtLocation(view, Gravity.NO_GRAVITY, location[0] - onefile.getWidth(), location[1]);
//        }else{
//            //右边
//            onefile.showAtLocation(view, Gravity.NO_GRAVITY, location[0] + view.getWidth(), location[1]);
//        }
//
//
//    }
//
//    View.OnClickListener onfile = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            int style = 0;
//            switch (v.getId()) {
//                case R.id.copy://复制
//                    style = 0;
//                    break;
//                case R.id.delete://删除
//                    style = 1;
//                    break;
//                case R.id.move://移动
//                    style = 2;
//                    break;
//                case R.id.setting://设置
//                    style = 4;
//                    break;
//                case R.id.descibe://详情
//                    style=5;
//                    //展示文件全路径，文件名称，文件属性
//                    break;
//                case R.id.rename://重命名
//                    style = 3;
//                    break;
//                default:
//                    break;
//            }
//            onefile.dismiss();
//            onefile = null;
//            updataOnFile(style, (Integer) v.getTag());
//        }
//    };

    @Override
    public void onOperate(int style, int position) {
        updataOnFile(style,position);
    }

    /**
     * 单个文件管理
     *
     * @param style    处理方式复制，删除，移动，重命名
     * @param position 处理的问价
     */
    public abstract void updataOnFile(int style, int position);

}
