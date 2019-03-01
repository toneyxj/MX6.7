package com.moxi.bookstore.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.TextView;

import com.moxi.bookstore.R;
import com.moxi.bookstore.base.BookStoreBaseActivity;
import com.moxi.bookstore.view.HSlidableListView;

import butterknife.Bind;

public class OrderActivity extends BookStoreBaseActivity {
    @Bind(R.id.title)
    TextView orderNum;
    @Bind(R.id.orderlist_lv)
    HSlidableListView orderlv;

    @Override
    protected int getMainContentViewId() {
        return R.layout.activity_order;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

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

    public void goBack(View v){finish();}

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}
