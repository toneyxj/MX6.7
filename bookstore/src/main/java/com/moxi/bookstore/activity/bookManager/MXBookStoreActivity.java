package com.moxi.bookstore.activity.bookManager;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.dangdang.reader.moxiUtils.BrodcastUtils;
import com.dangdang.reader.utils.ConfigManager;
import com.dangdang.reader.utils.DangdangFileManager;
import com.dangdang.reader.utils.UpdateCss;
import com.moxi.bookstore.BookstoreApplication;
import com.moxi.bookstore.R;
import com.moxi.bookstore.activity.EbookDetailActivity;
import com.moxi.bookstore.activity.OnlieBookCityActivity;
import com.moxi.bookstore.adapter.bookManager.LocalBookPageRecyclerAdapter;
import com.moxi.bookstore.adapter.bookManager.NetBookPageRecyclerAdapter;
import com.moxi.bookstore.asy.ScanReadFile;
import com.moxi.bookstore.base.BookStoreBaseActivity;
import com.moxi.bookstore.bean.Media;
import com.moxi.bookstore.bean.Message.RecommendData;
import com.moxi.bookstore.bean.Sale;
import com.moxi.bookstore.db.EbookDB;
import com.moxi.bookstore.db.SacnReadFileUtils;
import com.moxi.bookstore.db.TableConfig;
import com.moxi.bookstore.db.TableOperate;
import com.moxi.bookstore.http.HttpManager;
import com.moxi.bookstore.http.deal.RecommendEbookdeal;
import com.moxi.bookstore.http.listener.HttpOnNextListener;
import com.moxi.bookstore.http.subscribers.ProgressSubscriber;
import com.moxi.bookstore.modle.BookStoreFile;
import com.moxi.bookstore.request.JsonData;
import com.moxi.bookstore.utils.StartUtils;
import com.mx.mxbase.constant.APPLog;
import com.mx.mxbase.interfaces.OnItemClickListener;
import com.mx.mxbase.utils.DeleteDDReaderBook;
import com.mx.mxbase.utils.FileUtils;
import com.mx.mxbase.utils.SharePreferceUtil;
import com.mx.mxbase.utils.ToastUtils;
import com.mx.mxbase.view.CustomRecyclerView;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.Bind;

/**
 * Created by Archer on 16/7/27.
 */
public class MXBookStoreActivity extends BookStoreBaseActivity implements View.OnClickListener {

    private NetBookPageRecyclerAdapter adapter;
    private LocalBookPageRecyclerAdapter localAdapter;
    private List<BookStoreFile> listFiles = new ArrayList<>();

    @Bind(R.id.recycler_page_book_store)
    CustomRecyclerView recyclerView;
    @Bind(R.id.ll_book_store_found_more)
    LinearLayout llFoundMore;
    @Bind(R.id.ll_book_store_back)
    LinearLayout llBack;
    @Bind(R.id.recycler_page_local_book)
    RecyclerView recyclerLocalBook;
    @Bind(R.id.ll_book_store_total)
    LinearLayout llBookLocalAll;
    @Bind(R.id.progress_bar_hitn)
    ProgressBar progress_bar_hitn;
    @Bind(R.id.progress_net_hitn)
    ProgressBar progress_net_hitn;

    @Bind(R.id.emty_rl_net)
    RelativeLayout emty_rl_net;
    @Bind(R.id.emty_rl_location)
    RelativeLayout emty_rl_location;
    private boolean searchFirst = true;

//    private HomeKeyEventBrodcast homeKeyEventBrodcast = new HomeKeyEventBrodcast();

    public boolean isCanRefuresh = true;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction()
                    .equals(BrodcastUtils.readBrodcast)) {
                if (isfinish) return;
                reloadLocationBook();
                isCanRefuresh = true;
                new DeleteDDReaderBook(MXBookStoreActivity.this, true, null).execute();
            }
        }
    };

    View window;


    @Override
    protected int getMainContentViewId() {
        window = View.inflate(this, R.layout.mx_activity_book_store, null);
        return R.layout.mx_activity_book_store;
    }

    private void initData() {
//        registerReceiver(homeKeyEventBrodcast, new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
        checkCss();
        llFoundMore.setOnClickListener(this);
        emty_rl_net.setOnClickListener(this);
        llBack.setOnClickListener(this);
        llBookLocalAll.setOnClickListener(this);
        recyclerLocalBook.setLayoutManager(new GridLayoutManager(this, 4));
        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));

        getRecommData();
    }

    protected void checkCss() {
        ConfigManager configManager = new ConfigManager(getApplication());
        UpdateCss updateCss = new UpdateCss(configManager);
        updateCss.execute(getApplication());
    }

    private void resetLocalAdapter() {
        progress_bar_hitn.setVisibility(View.GONE);
        emty_rl_location.setVisibility((listFiles.size() == 0) ? View.VISIBLE : View.GONE);
        if (listFiles.size() < 8 && searchFirst) {
            if (listFiles.size() == 0)
                progress_bar_hitn.setVisibility(View.VISIBLE);
            SacnReadFileUtils.getInstance(this).SearchBooks(this, scanReadListner);
            searchFirst = false;
        }

        localAdapter = new LocalBookPageRecyclerAdapter(listFiles, this);
        recyclerLocalBook.setAdapter(localAdapter);
        localAdapter.setOnItemClickLIstener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (position >= listFiles.size()) return;
                BookStoreFile getFile = listFiles.get(position);
                if (!(new File(getFile.filePath)).exists()) {
                    ToastUtils.getInstance().showToastShort("文件已经删除");
                    SacnReadFileUtils.getInstance(MXBookStoreActivity.this).deleteFile(getFile.filePath);
                    reloadLocationBook();
                    return;
                }
                EbookDB eBook = TableOperate.getInstance().queryByPath(TableConfig.TABLE_NAME, getFile.filePath);

                if (eBook != null) {
                    //开始跳转当当阅读器
                    StartUtils.OpenDDRead(MXBookStoreActivity.this, eBook);
                } else {
//                    new PrepareCMS(MXBookStoreActivity.this).insertCMS(getFile.filePath);
                    FileUtils.getInstance().openFile(MXBookStoreActivity.this, new File(getFile.filePath));
                    SharePreferceUtil.getInstance(MXBookStoreActivity.this).setCache("Recently", getFile.filePath);
                    SacnReadFileUtils.getInstance(MXBookStoreActivity.this).updateIndex(getFile);
                }
//                reloadLocationBook();
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
    }

    private ScanReadFile.ScanReadListner scanReadListner = new ScanReadFile.ScanReadListner() {
        @Override
        public void onScanReadEnd() {
            if (isfinish) return;
            if (listFiles.size() < 8)
                resetLocalAdapter();
        }

        @Override
        public void onScanReadFile(BookStoreFile file) {
            if (isfinish) return;
            if (listFiles.size() > 8) return;
            listFiles.add(file);
            if (listFiles.size() == 8) {
                resetLocalAdapter();
            }
        }
    };

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        initData();
        IntentFilter intentFilter = new IntentFilter(
                BrodcastUtils.readBrodcast);
        registerReceiver(receiver, intentFilter);
        String mstuFlag = getIntent().getStringExtra("flag_version_stu");
        ((BookstoreApplication) getApplication()).setStuFlag(mstuFlag);
//        List<BookStoreFile> files=new ArrayList<>();
//        files=DataSupport.findAll(BookStoreFile.class);
//        for (BookStoreFile bookStoreFile:files)
//        {
//            APPLog.e(bookStoreFile.toString());
//        }
        String md5 = TableOperate.getInstance().getHaveMD5();
        if (md5 == null || md5.equals("")) {
            //更新当当书城数据库
            dialogShowOrHide(true, "更新网络数据库");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    DataSupport.deleteAll(BookStoreFile.class);
                    final List<EbookDB> ebookDBs = TableOperate.getInstance().queryAll(TableConfig.TABLE_NAME);
                    for (EbookDB e : ebookDBs) {
                        APPLog.e(e.toString());
                        TableOperate.getInstance().AddMd5(TableConfig.TABLE_NAME, e);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            reloadLocationBook();
                            dialogShowOrHide(false, "更新网络数据库");
                        }
                    });
                }
            }).start();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                DangdangFileManager.moveXdb_rules(getApplicationContext());
            }
        }).start();

    }

    @Override
    public void onActivityStarted(Activity activity) {
        //getRecommData();
    }

    private boolean isFirst = true;

    @Override
    public void onActivityResumed(Activity activity) {
//        DeviceInfo.currentDevice.showSystemStatusBar(MXBookStoreActivity.this);
        if (isFirst) {
            new DeleteDDReaderBook(MXBookStoreActivity.this, false, new DeleteDDReaderBook.operationSucess() {
                @Override
                public void onSucess() {
                    reloadLocationBook();
                }
            }).execute();
            isFirst = false;
        } else {
            reloadLocationBook();
        }

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Bundle outState, PersistableBundle
            outPersistentState) {

    }

    @Override
    public void onActivityRestoreInstanceState(Bundle savedInstanceState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    @Override
    protected void onDestroy() {
        SacnReadFileUtils.getInstance(this).removeListener(scanReadListner);
        SacnReadFileUtils.getInstance(this).ondestory();
        unregisterReceiver(receiver);
        super.onDestroy();

//        unregisterReceiver(homeKeyEventBrodcast);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_book_store_found_more:
                if (progress_bar_hitn.getVisibility() == View.GONE)
                    startActivity(new Intent(MXBookStoreActivity.this, OnlieBookCityActivity.class));
                break;
            case R.id.ll_book_store_back:
                this.finish();
//                new ScanReadFile().execute();
                break;
            case R.id.emty_rl_net:
                getRecommData();
                break;
            case R.id.ll_book_store_total:
                Intent localBook = new Intent();
                localBook.setClass(this, MXStacksActivity.class);
                startActivityForResult(localBook, 10);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        if (requestCode == 10) {
            reloadLocationBook();
        }
    }

    private void reloadLocationBook() {
        progress_bar_hitn.setVisibility(View.VISIBLE);
        List<BookStoreFile> lists = SacnReadFileUtils.getInstance(this).getBookStoreSize(8);
        listFiles.clear();
        listFiles.addAll(lists);
        resetLocalAdapter();
    }

    List<Media> recomlist;

    private void getRecommData() {
        progress_net_hitn.setVisibility(View.VISIBLE);
        emty_rl_net.setVisibility(View.GONE);
        setListNetAdapter(false);
        if (JsonData.getInstance().isCanrequest(this) || recomlist == null || recomlist.size() == 0) {
            RecommendEbookdeal deal = new RecommendEbookdeal(new ProgressSubscriber(recomListener, this),getTokenValue());
            HttpManager.getInstance().doHttpDeal(deal);
        }
    }

    HttpOnNextListener recomListener = new HttpOnNextListener<RecommendData>() {
        @Override
        public void onNext(RecommendData obj) {
            if (isfinish) return;
            if (recomlist == null) recomlist = new ArrayList<>();
            recomlist.clear();
            List<Sale> sales = obj.getSaleList();
            sales = switchData(sales);
            for (int i = 0; i < sales.size(); i++) {
                recomlist.add(sales.get(i).getMediaList().get(0));
            }
            JsonData.getInstance().setMediaStr(MXBookStoreActivity.this, recomlist);
            setNetAdapter();
        }

        @Override
        public void onError() {
            if (isfinish) return;
            setListNetAdapter(true);
        }
    };

    private boolean setListNetAdapter(boolean is) {
        if (recomlist == null) recomlist = new ArrayList<>();
        recomlist.clear();
        recomlist.addAll(JsonData.getInstance().getMedias(MXBookStoreActivity.this));
        if (recomlist.size() > 0 || is) {
            setNetAdapter();
            return true;
        } else {
            return false;
        }
    }

    private List<Sale> switchData(List<Sale> sales) {
        Calendar mcalender = Calendar.getInstance();
        mcalender.setTimeInMillis(System.currentTimeMillis());
        int weekDay = mcalender.get(Calendar.DAY_OF_WEEK);
        int start = weekDay - 1;
        int cha=(start + 8)-sales.size();
        if (cha>0){
            start -=cha;
        }
        return sales.subList(start, start + 8);
    }

    private void setNetAdapter() {
        progress_net_hitn.setVisibility(View.GONE);
        if (recomlist == null) recomlist = new ArrayList<>();
        emty_rl_net.setVisibility((recomlist.size() == 0) ? View.VISIBLE : View.GONE);
        if (recomlist.size()>4){
            recomlist=recomlist.subList(0,4);
        }
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        } else {
            adapter = new NetBookPageRecyclerAdapter(recomlist, this, 1);
            recyclerView.setAdapter(adapter);
            adapter.setOnItemClickLIstener(new OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Media item = recomlist.get(position);
                    Intent intent = new Intent(MXBookStoreActivity.this, EbookDetailActivity.class);
                    intent.putExtra("saleId", item.getSaleId());
                    intent.putExtra("booktitle", item.getTitle());
                    intent.putExtra("lowestprice", item.getLowestPrice());
                    intent.putExtra("saleprice", item.getSalePrice());
                    startActivity(intent);
                }

                @Override
                public void onItemLongClick(View view, int position) {

                }
            });
        }
    }
}
