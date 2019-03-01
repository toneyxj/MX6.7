package com.moxi.filemanager.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.moxi.filemanager.R;
import com.mx.mxbase.base.BaseApplication;
import com.mx.mxbase.constant.APPLog;
import com.mx.mxbase.view.LinerlayoutInter;

import java.util.List;

/**
 * Created by Administrator on 2016/8/31.
 */
public class SelectItemDialog extends Dialog implements LinerlayoutInter.LinerLayoutInter{
    private LinerlayoutInter add_moves;// dialog显示文字控件
    private TextView insure;// dialog显示文字控件
    private List<String> contents;// 提示内容
    private selectDialogListenr listenr;
    private  String mainPath;
    private   String title;
    private int currentClick=-1;

    public SelectItemDialog(Context context, int theme, List<String> contents,String mainPath, String title,
                           selectDialogListenr listenr) {
        super(context, theme);
        this.contents = contents;
        this.listenr = listenr;
        this.title=title;
        this.mainPath=mainPath;
    }

    private int indexPage = 0;
    private int totalIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.dialog_select_item);
        add_moves = (LinerlayoutInter) findViewById(R.id.add_moves);
        insure = (TextView) findViewById(R.id.insure);
        add_moves.setLayoutInter(this);

        int size = contents.size() / 5;
        size += contents.size() % 5 == 0 ? 0 : 1;
        totalIndex = size;

        reInit();

        insure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentClick==-1)return;
                listenr.selectItem(contents.get(currentClick));
                    SelectItemDialog.this.dismiss();
            }
        });
    }

    private void reInit() {
        if (totalIndex <= indexPage) {
            indexPage = totalIndex - 1;
        }
        if (indexPage == totalIndex - 1) {
            addview(contents.subList(indexPage * 5, contents.size()));
        } else {
            addview(contents.subList(indexPage * 5, (indexPage + 1) * 5));
        }
    }


    public void moveRight() {
        if (indexPage<totalIndex-1){
            indexPage++;
            reInit();
        }
    }

    public void moveLeft() {
        if (indexPage>0){
            indexPage--;
            reInit();
        }
    }

    private void addview(List<String> list) {
        add_moves.removeAllViews();
        LayoutInflater inflater = getLayoutInflater();
        int i=0;
        for (String been : list) {
            View view = inflater.inflate(R.layout.addview_select_item, null);
            TextView fodler_path = (TextView) view.findViewById(R.id.fodler_path);
            fodler_path.setText(been.replace(mainPath,""));

            int cindex=i+(indexPage * 5);

            if (cindex==currentClick){
                fodler_path.setBackgroundResource(R.drawable.di_white_bian_font);
            }

            fodler_path.setTag(i);
            fodler_path.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentClick = (int)v.getTag() + (indexPage * 5);
                    APPLog.e("当前点击"+currentClick);
                    reInit();
                }
            });

            add_moves.addView(view);
            i++;
        }

    }


    /**
     * 显示dialog
     *
     * @param context  上下文
     * @param contents 提示内容
     * @param listenr  如果点击确认返回控件
     */
    public static void getdialog(Context context, List<String> contents,String mainPath, String title,
                                 selectDialogListenr listenr) {
        SelectItemDialog dialog = new SelectItemDialog(context, R.style.dialog,
                contents,mainPath,title, listenr);
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER);
        window.getDecorView().setPadding(BaseApplication.ScreenWidth / 6, 0, BaseApplication.ScreenWidth / 6, 0);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        window.setAttributes(lp);
        dialog.show();
    }

    public interface selectDialogListenr {
        public void selectItem(String floder);
    }

}
