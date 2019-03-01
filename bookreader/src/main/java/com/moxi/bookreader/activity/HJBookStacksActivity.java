package com.moxi.bookreader.activity;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.moxi.bookreader.R;
import com.moxi.bookreader.adapter.HJBookStacksAdapter;
import com.moxi.bookreader.cache.ACache;
import com.moxi.bookreader.model.BookList;
import com.moxi.bookreader.service.DownDbService;
import com.mx.mxbase.base.BaseActivity;
import com.mx.mxbase.http.MXHttpHelper;
import com.mx.mxbase.interfaces.ClickBackListener;
import com.mx.mxbase.netstate.NetWorkUtil;
import com.mx.mxbase.utils.FileUtils;
import com.mx.mxbase.utils.GsonTools;
import com.mx.mxbase.utils.ListUtils;
import com.mx.mxbase.utils.Toastor;
import com.mx.mxbase.view.AlertDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * Created by King on 2017/8/29.
 */

public class HJBookStacksActivity extends BaseActivity implements View.OnClickListener, ServiceConnection {

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
    @Bind(R.id.rl_retry)
    RelativeLayout rlReTry;
    @Bind(R.id.tv_retry)
    TextView tvReTry;

    private HJBookStacksAdapter adapter;
    private List<BookList.BookDetail> listBookStacks = new ArrayList<>();
    private List<List<BookList.BookDetail>> lists = new ArrayList<>();
    private int page = 0;
    private Intent serverceIt;
    private DownDbService downService;
    private String url = "http://120.25.193.163:8088/moxiBook/getBooks/";
    private int bookType;
    private ACache aCache;

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        if (msg.arg1 == 1002) {
            if (msg.what == 0) {
                rlReTry.setVisibility(View.GONE);
                BookList bookList = (BookList) msg.obj;
                listBookStacks = bookList.getResult();
                lists = ListUtils.splitList(listBookStacks, 12);
                aCache.put(url + bookType, GsonTools.obj2json(bookList));
                setPageValue(page);
            } else {
                String temp = aCache.getAsString(url + bookType);
                if (temp == null) {
                    rlReTry.setVisibility(View.VISIBLE);
                    Toastor.showToast(HJBookStacksActivity.this, "网络请求失败请重试");
                } else {
                    rlReTry.setVisibility(View.GONE);
                    BookList bookList = GsonTools.getPerson(temp, BookList.class);
                    listBookStacks = bookList.getResult();
                    lists = ListUtils.splitList(listBookStacks, 12);
                    setPageValue(page);
                }
            }
        }
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        aCache = ACache.get(this);
        tvBack.setText("儿童书城");
        llBack.setVisibility(View.VISIBLE);
        llBack.setOnClickListener(this);
        imgNext.setOnClickListener(this);
        imgPre.setOnClickListener(this);
        tvReTry.setOnClickListener(this);

        bookType = this.getIntent().getIntExtra("file_type_index", -1);
        tvMidTitle.setText(this.getIntent().getStringExtra("file_type_name"));
        getBooksByType(url + bookType);
        serverceIt = new Intent();
        serverceIt.setClass(this, DownDbService.class);
        startService(serverceIt);
    }


    private void getBooksByType(String url) {
        MXHttpHelper.getInstance(this).postStringBack(1002, url, null, getHandler(), BookList.class);
    }

    private void setPageValue(final int page) {
        adapter = new HJBookStacksAdapter(this, lists.get(page), gridView);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int index = page * 12 + position;
                final BookList.BookDetail hjBookData = listBookStacks.get(index);
                File file = new File(DownDbService.getFilePathWithId(hjBookData.getId()));
                if (file.exists()) {
                    FileUtils.getInstance().openFile(HJBookStacksActivity.this, file);
                } else {
                    if (NetWorkUtil.isWifiConnected(HJBookStacksActivity.this)) {
                        new AlertDialog(HJBookStacksActivity.this).builder().setTitle("提示").setMsg("文件未下载，现在去下载?").setCancelable(false).setNegativeButton("确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (downService != null) {
                                    dialogShowOrHide(true, "正在下载，请稍后......", new ClickBackListener() {
                                        @Override
                                        public void onHitnBackground() {
                                            dialogShowOrHide(false, "正在下载，请稍后......");
                                        }
                                    });
                                    downService.setCallBack(callback);
                                    List<Integer> bookids = new ArrayList<>();
                                    bookids.add(hjBookData.getId());
                                    downService.downBookFileWithIds(bookids, hjBookData.getPath());
                                }
                            }
                        }).setPositiveButton("取消", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                            }
                        }).show();
                    } else {
                        Toastor.showToast(HJBookStacksActivity.this, "请检查网络是否可用。");
                    }
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
        getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }
        }, 500);
        bindService(serverceIt, this, Service.BIND_AUTO_CREATE);
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
        unbindService(this);
    }

    @Override
    protected int getMainContentViewId() {
        return R.layout.activity_hj_book_stacks;
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
            case R.id.tv_retry:
                int bookType = this.getIntent().getIntExtra("file_type_index", -1);
                getBooksByType(url + bookType);
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

    DownDbService.DownDbCallback callback = new DownDbService.DownDbCallback() {

        @Override
        public void onEnd(List<Integer> successIds, List<Integer> failureBookIds) {
            if (isfinish)return;
            dialogShowOrHide(false, "正在下载，请稍后......");
            if (successIds != null && successIds.size() > 0) {
                Toastor.showToast(HJBookStacksActivity.this, "下载成功，保存至" + DownDbService.localDBPath + "文件夹");
                setPageValue(page);
            }
            if (failureBookIds != null && failureBookIds.size() > 0) {
                for (int i : failureBookIds) {
                    Toastor.showToast(HJBookStacksActivity.this, findBookById(i) + "下载失败请重试！");
                }
            }
        }

        @Override
        public void onStart(String error) {
            Toastor.showToast(HJBookStacksActivity.this, error);
        }

        @Override
        public void onProgress(int i, long l, long l1, int index, int count) {
        }
    };

    @Override
    public void onServiceConnected(ComponentName name, IBinder iBinder) {
        downService = ((DownDbService.MyBinder) iBinder).getService();
        if (downService != null)
            downService.setCallBack(callback);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.e("onServiceDisconnected", name.getPackageName());
    }

    private String findBookById(int id) {
        String bookName = "";
        for (BookList.BookDetail book : listBookStacks) {
            if (book.getId() == id) {
                bookName = book.getName();
            }
        }
        return bookName;
    }
}
