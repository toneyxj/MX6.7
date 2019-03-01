package com.moxi.filemanager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mx.mxbase.base.BaseActivity;
import com.mx.mxbase.utils.GlideUtils;
import com.mx.mxbase.view.LinerlayoutInter;

import java.util.ArrayList;

import butterknife.Bind;

public class CheckImageActivity extends BaseActivity implements LinerlayoutInter.LinerLayoutInter {
    public static ArrayList<String> paths=new ArrayList<>();
    public static void startCheck(Context context, String path) {
        Intent intent = new Intent(context, CheckImageActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("path", path);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    /**
     * 上下文
     *
     * @param context 上下文
     * @param paths   图片路径集合
     */
    public static void startCheck(Context context, ArrayList<String> paths, int curentIndex) {
        Intent intent = new Intent(context, CheckImageActivity.class);
        Bundle bundle = new Bundle();
        if (paths==null)return;
        CheckImageActivity.paths.addAll(paths);
        bundle.putStringArrayList("paths", new ArrayList<String>());
        bundle.putInt("index", curentIndex);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    protected int getMainContentViewId() {
        return R.layout.activity_check_image;
    }

    @Bind(R.id.show_iamge)
    ImageView show_iamge;
    @Bind(R.id.main_layout_view)
    LinerlayoutInter main_layout_view;
    @Bind(R.id.show_image_layout)
    RelativeLayout show_image_layout;
    @Bind(R.id.show_index_txt)
    TextView show_index_txt;
    String path = "";
    //    private ArrayList<String> paths;
    private int index;

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        Bundle bundle = null;
        if (savedInstanceState != null) {
            bundle = savedInstanceState;
        } else {
            bundle = getIntent().getExtras();
        }

        main_layout_view.setLayoutInter(this);
        index = bundle.getInt("index", -1);
        if (index == -1) {
            path = bundle.getString("path");
//            LocationImageLoader.getInstance().clearCache(path);
            show_image_layout.setVisibility(View.GONE);
        } else {
//            paths = bundle.getStringArrayList("paths");
            if (paths.size()==0)finish();
//            for (String value : paths) {
//                LocationImageLoader.getInstance().clearCache(value);
//            }
        }
        lodingImage();
        show_iamge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
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
        if (index == -1) {
            outState.putString("path", path);
        } else {
//            outState.putStringArrayList("paths", new ArrayList<String>());
            outState.putInt("index", index);
        }
    }

    @Override
    public void onActivityRestoreInstanceState(Bundle savedInstanceState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    @Override
    public void moveRight() {
        if (index == -1) return;

        if (index < (paths.size() - 1)) {
            index++;
        } else if (paths.size() > 1) {
            index = 0;
        }
        lodingImage();
    }

    @Override
    public void moveLeft() {
        if (index == -1) return;

        if (index > 0) {
            index--;
        } else if (paths.size() > 1) {
            index = paths.size() - 1;
        }
        lodingImage();
    }

    private void lodingImage() {
        if (index != -1)
            path = paths.get(index);
        GlideUtils.getInstance().locatonPic(this,show_iamge,path);
        if (paths != null) {
            show_index_txt.setText(String.valueOf(index + 1) + "/" + paths.size());
        }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        paths.clear();
    }
}
