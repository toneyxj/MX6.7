package com.moxi.bookstore;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.TextView;

import com.dangdang.reader.GuideActivity;
import com.moxi.bookstore.adapter.BookRackAdapter;
import com.moxi.bookstore.base.BookStoreBaseActivity;
import com.moxi.bookstore.interfacess.ClickPosition;
import com.moxi.bookstore.interfacess.MoveListener;
import com.moxi.bookstore.modle.BookRack;
import com.moxi.bookstore.utils.PictureUtils;
import com.moxi.bookstore.view.MyrecycleView;
import com.mx.mxbase.constant.APPLog;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

public class MainActivity extends BookStoreBaseActivity implements ClickPosition,MoveListener{
    @Override
    protected int getMainContentViewId() {
        return R.layout.activity_main;
    }

    @Bind(R.id.recyclerview_layout)
    MyrecycleView recyclerview_layout;
    private BookRackAdapter mAdapter;

    private boolean isFirst=true;
    /**
     * 展示列表控件高度
     */
    private int show_layout_height=0;
    /**
     * 展示数据集合
     */
    private List<BookRack> listData=new ArrayList<>();
    @Bind(R.id.show_title)
    TextView show_title;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (isFirst&&hasFocus){
            show_layout_height=recyclerview_layout.getHeight();
            initrecycleView();
        }
    }

    /**
     * 初始化recycleview
     */
private void  initrecycleView(){
    if (show_layout_height==0)return;
    int width=BookstoreApplication.ScreenWidth/3;
    int height=show_layout_height/2;
    mAdapter = new BookRackAdapter(this, listData,width,height,this);

    recyclerview_layout.setLayoutManager(new StaggeredGridLayoutManager(3,
            StaggeredGridLayoutManager.VERTICAL));
    recyclerview_layout.setAdapter(mAdapter);

//    recyclerview_layout.addItemDecoration(new DividerGridItemDecoration(this));
    // 设置item动画
    recyclerview_layout.setItemAnimator(new DefaultItemAnimator());
    addmonishuju();
}
    private void addmonishuju(){
        for (int i = 0; i < 6; i++) {
            listData.add(new BookRack("1122","1236", PictureUtils.imagePath,"阅读数据的奥秘呀"+i,2+i+"%",12));
        }
        mAdapter.notifyDataSetChanged();
    }
    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        recyclerview_layout.setLayoutInter(this);
    initrecycleView();


        show_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, GuideActivity.class));
//                StartRead startRead = new StartRead();
//                startRead.startOffPrintRead(MainActivity.this);
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
    public void click(int position) {
//        APPLog.e(position);
//        String path="/mnt/sdcard/Books/2016082521433470704.epub";
//        ExternalFile externalFile=new ExternalFile();
//        externalFile.filePath=path;
//        externalFile.bookName="测试书本";
//        StartRead read=new StartRead();
//        read.startExteralFile(this,externalFile);
//        read.startRead(this,new StartRead.ReadParams());

    }

    @Override
    public void moveRight() {
    APPLog.e("moveRight");
    }

    @Override
    public void moveLeft() {
        APPLog.e("moveLeft");
    }
}
