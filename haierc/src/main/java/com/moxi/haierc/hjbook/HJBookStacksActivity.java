package com.moxi.haierc.hjbook;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moxi.haierc.R;
import com.moxi.haierc.hjbook.adapter.HJBookStacksAdapter;
import com.moxi.haierc.hjbook.hjdata.HJBookData;
import com.moxi.haierc.hjbook.hjutils.ScanBookUtils;
import com.mx.mxbase.base.BaseActivity;
import com.mx.mxbase.utils.FileUtils;
import com.mx.mxbase.utils.ListUtils;
import com.mx.mxbase.utils.Toastor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

//import com.onyx.android.sdk.device.DeviceInfo;

/**
 * Created by King on 2017/8/29.
 */

public class HJBookStacksActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.grid_view_hj_book_stacks)
    GridView gridView;
    @Bind(R.id.tv_base_mid_title)
    TextView tvMidTitle;
    @Bind(R.id.tv_base_back)
    TextView tvBack;
    @Bind(R.id.ll_base_back)
    LinearLayout llBack;
    @Bind(R.id.img_book_stacks_page_pre)
    ImageView imgPre;
    @Bind(R.id.img_book_stacks_page_next)
    ImageView imgNext;
    @Bind(R.id.tv_page_stacks)
    TextView tvPageIndex;

    private String filePath;
    private boolean RELOAD = false;
    private HJBookStacksAdapter adapter;
    private List<HJBookData> listBookStacks = new ArrayList<>();
    private List<List<HJBookData>> lists = new ArrayList<>();
    private int page = 0;

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        tvBack.setText("天天阅读");
        llBack.setVisibility(View.VISIBLE);
        llBack.setOnClickListener(this);
        imgNext.setOnClickListener(this);
        imgPre.setOnClickListener(this);

        filePath = this.getIntent().getStringExtra("file_path");
        listBookStacks = ScanBookUtils.getInstance(HJBookStacksActivity.this).getBookData(0, filePath);
        tvMidTitle.setText(filePath.substring(filePath.lastIndexOf("/") + 1));
        lists = ListUtils.splitList(listBookStacks, 12);
        if (lists.size() > 0) {
            setPageValue(page);
        }
        initStatusBar(R.color.colorBlack);
    }

    private void setPageValue(final int page) {
        adapter = new HJBookStacksAdapter(this, lists.get(page), gridView);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int index = page * 12 + position;
                HJBookData hjBookData = listBookStacks.get(index);
                File file = new File(hjBookData.getFilePath());
                if (file.exists()) {
                    FileUtils.getInstance().openFile(HJBookStacksActivity.this, file);
                    ScanBookUtils.getInstance(HJBookStacksActivity.this).updateBookReadTime(hjBookData.getId());
                } else {
                    showToast("阅读文件已异常，请确认文件是否存在！！");
                }
            }
        });
        if (lists.size() > 0) {
            tvPageIndex.setText((page + 1) + "/" + lists.size());
        } else {
            tvPageIndex.setText(0 + "/" + lists.size());
        }
        if (page > 0) {
            imgPre.setImageResource(R.mipmap.img_hj_page_left_have);
            if (page < lists.size() - 1) {
                imgNext.setImageResource(R.mipmap.img_hj_page_right_have);
            } else {
                imgNext.setImageResource(R.mipmap.img_hj_page_right_no);
            }
        } else {
            imgPre.setImageResource(R.mipmap.img_hj_page_left_no);
            if (page < lists.size() - 1) {
                imgNext.setImageResource(R.mipmap.img_hj_page_right_have);
            } else {
                imgNext.setImageResource(R.mipmap.img_hj_page_right_no);
            }
        }
    }


    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
//        DeviceInfo.singleton().getDeviceController().showSystemStatusBar(this);
    }

    @Override
    public void onActivityPaused(Activity activity) {
        RELOAD = true;
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
    protected int getMainContentViewId() {
        return R.layout.activity_hj_book_stacks;
    }

    /**
     * 状态栏处理：解决全屏切换非全屏页面被压缩问题
     */
    public void initStatusBar(int barColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
            // 获取状态栏高度
            int statusBarHeight = getResources().getDimensionPixelSize(resourceId);
            View rectView = new View(this);
            // 绘制一个和状态栏一样高的矩形，并添加到视图中
            LinearLayout.LayoutParams params
                    = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, statusBarHeight);
            rectView.setLayoutParams(params);
            //设置状态栏颜色
            rectView.setBackgroundColor(getResources().getColor(barColor));
            // 添加矩形View到布局中
            ViewGroup decorView = (ViewGroup) getWindow().getDecorView();
            decorView.addView(rectView);
            ViewGroup rootView = (ViewGroup) ((ViewGroup) this.findViewById(android.R.id.content)).getChildAt(0);
            rootView.setFitsSystemWindows(true);
            rootView.setClipToPadding(true);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_base_back:
                this.finish();
                break;
            case R.id.img_book_stacks_page_pre:
                if (page > 0) {
                    page--;
                    setPageValue(page);
                } else {
                    Toastor.showToast(this, "已经是第一页了");
                }
                break;
            case R.id.img_book_stacks_page_next:
                if (page < lists.size() - 1) {
                    page++;
                    setPageValue(page);
                } else {
                    Toastor.showToast(this, "已经是最后一页了");
                }
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_PAGE_UP) {
            //上一页
            if (page > 0) {
                page--;
                setPageValue(page);
            } else {
                Toastor.showToast(this, "已经是第一页了");
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_PAGE_DOWN) {
            //下一页
            if (page < lists.size() - 1) {
                page++;
                setPageValue(page);
            } else {
                Toastor.showToast(this, "已经是最后一页了");
            }
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }
}
