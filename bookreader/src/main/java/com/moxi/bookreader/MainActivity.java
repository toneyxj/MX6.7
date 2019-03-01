package com.moxi.bookreader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.PersistableBundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.moxi.bookreader.activity.HJBookStacksActivity;
import com.moxi.bookreader.adapter.HJBookTypeAdapter;
import com.moxi.bookreader.cache.ACache;
import com.moxi.bookreader.model.BookModel;
import com.mx.mxbase.base.BaseActivity;
import com.mx.mxbase.http.MXHttpHelper;
import com.mx.mxbase.utils.GsonTools;
import com.mx.mxbase.utils.ListUtils;
import com.mx.mxbase.utils.Toastor;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private List<BookModel.BookBean> listBookType = new ArrayList<>();
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
    @Bind(R.id.rl_retry)
    RelativeLayout rlReTry;
    @Bind(R.id.tv_retry)
    TextView tvRetry;

    private List<List<BookModel.BookBean>> lists = new ArrayList<>();
    private int page = 0;
    private HJBookTypeAdapter adapter;
    private String url = "http://120.25.193.163:8088/moxiBook/getCategory";
    private ACache aCache;

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        if (msg.arg1 == 1001) {
            if (msg.what == 0) {
                rlReTry.setVisibility(View.GONE);
                BookModel bookmodel = (BookModel) msg.obj;
                listBookType = bookmodel.getResult();
                aCache.put(url, GsonTools.obj2json(bookmodel));
                lists = ListUtils.splitList(listBookType, 9);
                setPageValue(page);
            } else {
                String temp = aCache.getAsString(url);
                if (temp == null) {
                    rlReTry.setVisibility(View.VISIBLE);
                    Toastor.showToast(MainActivity.this, "网络请求失败请重试");
                } else {
                    rlReTry.setVisibility(View.GONE);
                    BookModel bookmodel = GsonTools.getPerson(temp, BookModel.class);
                    listBookType = bookmodel.getResult();
                    lists = ListUtils.splitList(listBookType, 9);
                    setPageValue(page);
                }
            }
        }
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
                BookModel.BookBean filePath = listBookType.get(index);
                if (filePath.getCount() > 0) {
                    Intent intent = new Intent();
                    intent.putExtra("file_type_index", filePath.getC_id());
                    intent.putExtra("file_type_name", filePath.getC_name());
                    intent.setClass(MainActivity.this, HJBookStacksActivity.class);
                    startActivity(intent);
                } else {
                    Toastor.showToast(MainActivity.this, "此目录下面没有书籍可读");
                }
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
    protected int getMainContentViewId() {
        return R.layout.activity_main;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        aCache = ACache.get(this);
        init();
    }

    private void init() {
        tvBack.setText("     ");
        llBack.setVisibility(View.VISIBLE);
        llBack.setOnClickListener(this);
        tvMidTitle.setText("儿童书城");
        imgPageNext.setOnClickListener(this);
        imgPagePre.setOnClickListener(this);
        tvRetry.setOnClickListener(this);

        getBookTypeList(url);
    }

    private void getBookTypeList(String url) {
        MXHttpHelper.getInstance(this).postStringBack(1001, url, null, getHandler(), BookModel.class);
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
            case R.id.tv_retry:
                getBookTypeList(url);
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
