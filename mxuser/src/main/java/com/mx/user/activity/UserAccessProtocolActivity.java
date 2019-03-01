package com.mx.user.activity;

import android.app.Activity;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mx.mxbase.base.BaseActivity;
import com.mx.mxbase.utils.Toastor;
import com.mx.user.R;
import com.mx.user.util.TextController;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import butterknife.Bind;

/**
 * Created by Archer on 2016/11/22.
 */
public class UserAccessProtocolActivity extends BaseActivity implements View.OnClickListener {
    @Bind(R.id.ll_page_left)
    LinearLayout llPageLeft;
    @Bind(R.id.ll_page_right)
    LinearLayout llPageRight;
    @Bind(R.id.tv_page_count)
    TextView tvCount;
    @Bind(R.id.ll_base_back)
    LinearLayout llBack;
    @Bind(R.id.tv_base_mid_title)
    TextView tvMidTitle;
    @Bind(R.id.tv_user_access_protocol)
    TextView tvAccessProtocol;

    private int page = 0;
    private TextController textController;

    @Override
    protected int getMainContentViewId() {
        return R.layout.mx_activity_user_access_protocol;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        llPageRight.setOnClickListener(this);
        llPageLeft.setOnClickListener(this);
        llBack.setVisibility(View.VISIBLE);
        llBack.setOnClickListener(this);
        tvMidTitle.setText("Topsir电纸书用户协议");
        textController = new TextController(tvAccessProtocol);
        init(page);
    }

    private void init(int page) {
        try {
            String temp = readTextFromSDcard();
            textController.onTextLoaded(temp, new TextController.OnInitializedListener() {
                @Override
                public void onInitialized() {
                    // stop displaying loading here
                    // enable buttons
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        tvCount.setText((page + 1) + "/" + 9);
    }

    /**
     * 按行读取txt
     *
     * @throws Exception
     */
    private String readTextFromSDcard() throws Exception {
        AssetManager assets = getAssets();
        InputStream in = assets.open("test.txt");
        InputStreamReader reader = new InputStreamReader(in);
        BufferedReader bufferedReader = new BufferedReader(reader);
        StringBuffer buffer = new StringBuffer("");
        String str;
        while ((str = bufferedReader.readLine()) != null) {
            buffer.append(str);
            buffer.append("\n");
        }
        return buffer.toString();
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
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_PAGE_UP:
                prePage();
                return true;
            case KeyEvent.KEYCODE_PAGE_DOWN:
                nextPage();
                return true;
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_page_left:
                prePage();
                break;
            case R.id.ll_page_right:
                nextPage();
                break;
            case R.id.ll_base_back:
                this.finish();
                break;
            default:
                break;
        }
    }

    private void nextPage() {
        if (textController.isNextEnabled()) {
            textController.next();
            page++;
            tvCount.setText((page + 1) + "/" + 9);
        } else {
            Toastor.showToast(this, "已经是最后一页了");
        }
    }

    private void prePage() {
        if (textController.isPreviousEnabled()) {
            textController.previous();
            page--;
            tvCount.setText((page + 1) + "/" + 9);
        } else {
            Toastor.showToast(this, "已经是第一页了");
        }
    }
}
