package com.moxi.haierc.hjbook;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moxi.haierc.R;
import com.moxi.haierc.hjbook.adapter.HJBookTypeAdapter;
import com.moxi.haierc.hjbook.hjdata.HJPrePathDir;
import com.moxi.haierc.hjbook.hjutils.ScanBookUtils;
import com.moxi.haierc.util.Utils;
import com.moxi.updateapp.UpdateUtil;
import com.mx.mxbase.base.BaseActivity;
import com.mx.mxbase.utils.ListUtils;
import com.mx.mxbase.utils.Toastor;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * Created by King on 2017/8/28.
 */

public class HJBookIndexActivity extends BaseActivity implements View.OnClickListener {

    private List<HJPrePathDir> listBookType = new ArrayList<>();
    @Bind(R.id.grid_view_hj_book_type)
    GridView gridView;
    @Bind(R.id.tv_base_mid_title)
    TextView tvMidTitle;
    @Bind(R.id.tv_base_back)
    TextView tvBack;
    @Bind(R.id.ll_base_back)
    LinearLayout llBack;
    @Bind(R.id.img_book_index_page_pre)
    ImageView imgPagePre;
    @Bind(R.id.img_book_index_page_next)
    ImageView imgPageNext;
    @Bind(R.id.tv_page_index)
    TextView tvPageIndex;

    private List<List<HJPrePathDir>> lists = new ArrayList<>();
    private int page = 0;
    private HJBookTypeAdapter adapter;

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        init();
    }

    private void init() {
        tvBack.setText("首页");
        llBack.setVisibility(View.VISIBLE);
        llBack.setOnClickListener(this);
        tvMidTitle.setText("天天阅读");
        imgPageNext.setOnClickListener(this);
        imgPagePre.setOnClickListener(this);

        listBookType = ScanBookUtils.getInstance(HJBookIndexActivity.this).getPreDir(0);
        lists = ListUtils.splitList(listBookType, 9);
        setPageValue(page);
    }

    private void setPageValue(final int page) {
        adapter = new HJBookTypeAdapter(this, lists.get(page), page, gridView);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int index = page * 9 + position;
                    if (index >= listBookType.size()) {
                        return;
                    }
                    HJPrePathDir filePath = listBookType.get(index);
                    if (filePath.number > 0) {
                        Intent intent = new Intent();
                        intent.putExtra("file_path", filePath.getPrePath());
                        intent.setClass(HJBookIndexActivity.this, HJBookStacksActivity.class);
                        startActivity(intent);
                    } else {
                        Toastor.showToast(HJBookIndexActivity.this, "此目录下面没有书籍可读");
                    }
//                }
            }
        });
        if (lists.size() > 0) {
            tvPageIndex.setText((page + 1) + "/" + lists.size());
        } else {
            tvPageIndex.setText(0 + "/" + lists.size());
        }
        if (page > 0) {
            imgPagePre.setImageResource(R.mipmap.img_hj_page_left_have);
            if (page < lists.size() - 1) {
                imgPageNext.setImageResource(R.mipmap.img_hj_page_right_have);
            } else {
                imgPageNext.setImageResource(R.mipmap.img_hj_page_right_no);
            }
        } else {
            imgPagePre.setImageResource(R.mipmap.img_hj_page_left_no);
            if (page < lists.size() - 1) {
                imgPageNext.setImageResource(R.mipmap.img_hj_page_right_have);
            } else {
                imgPageNext.setImageResource(R.mipmap.img_hj_page_right_no);
            }
        }
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
    protected int getMainContentViewId() {
        return R.layout.activity_hj_book_index;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_base_back:
                this.finish();
                break;
            case R.id.img_book_index_page_pre:
                if (page > 0) {
                    page--;
                    setPageValue(page);
                } else {
                    Toastor.showToast(this, "已经是第一页了");
                }
                break;
            case R.id.img_book_index_page_next:
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
