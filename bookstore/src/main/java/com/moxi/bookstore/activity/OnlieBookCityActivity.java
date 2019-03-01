package com.moxi.bookstore.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dangdang.reader.moxiUtils.BrodcastUtils;
import com.dangdang.reader.utils.ConfigManager;
import com.dangdang.reader.utils.UpdateCss;
import com.moxi.bookstore.BookstoreApplication;
import com.moxi.bookstore.R;
import com.moxi.bookstore.adapter.bookManager.NetBookPageRecyclerAdapter;
import com.moxi.bookstore.base.BookStoreBaseActivity;
import com.moxi.bookstore.bean.Media;
import com.moxi.bookstore.bean.Message.RecommendData;
import com.moxi.bookstore.bean.Sale;
import com.moxi.bookstore.http.HttpManager;
import com.moxi.bookstore.http.deal.RecommendEbookdeal;
import com.moxi.bookstore.http.listener.HttpOnNextListener;
import com.moxi.bookstore.http.subscribers.ProgressSubscriber;
import com.moxi.bookstore.request.JsonData;
import com.moxi.bookstore.request.json.Connector;
import com.moxi.bookstore.utils.ToolUtils;
import com.moxi.bookstore.view.recyle.OnlineBookDecoration;
import com.mx.mxbase.constant.APPLog;
import com.mx.mxbase.interfaces.OnItemClickListener;
import com.mx.mxbase.portal.CheckWifiLoginTask;
import com.mx.mxbase.utils.ActivityUtils;
import com.mx.mxbase.utils.DeleteDDReaderBook;
import com.mx.mxbase.utils.FileUtils;
import com.mx.mxbase.view.CustomRecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.Bind;

/**
 * 新添加的网上书城页面
 */
public class OnlieBookCityActivity extends BookStoreBaseActivity implements View.OnClickListener {
    @Override
    protected int getMainContentViewId() {
        return R.layout.activity_onlie_book_city;
    }

    @Bind(R.id.back_rl)
    RelativeLayout back_rl;
    @Bind(R.id.my_acont_tv)
    TextView my_acont_tv;
    @Bind(R.id.search_rl)
    LinearLayout search_rl;

    @Bind(R.id.online_fl)
    TextView online_fl;
    @Bind(R.id.online_zy)
    TextView online_zy;
    @Bind(R.id.online_cxs)
    TextView online_cxs;
    @Bind(R.id.online_xsb)
    TextView online_xsb;
    @Bind(R.id.online_rsb)
    TextView online_rsb;

    @Bind(R.id.recycler_page_book_store)
    CustomRecyclerView recyclerView;

    @Bind(R.id.emty_rl_net)
    RelativeLayout emty_rl_net;
    @Bind(R.id.progress_net_hitn)
    ProgressBar progress_net_hitn;

    private NetBookPageRecyclerAdapter adapter;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction()
                    .equals(BrodcastUtils.readBrodcast)) {
                if (isfinish) return;
                new DeleteDDReaderBook(OnlieBookCityActivity.this, true, null).execute();
            }
        }
    };

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        ToolUtils.getIntence().clearLoginUserData();
        String mstuFlag = getIntent().getStringExtra("flag_version_stu");
        ((BookstoreApplication) getApplication()).setStuFlag(mstuFlag);

        IntentFilter intentFilter = new IntentFilter(
                BrodcastUtils.readBrodcast);
        registerReceiver(receiver, intentFilter);

        //设置监听
        back_rl.setOnClickListener(this);
        my_acont_tv.setOnClickListener(this);
        search_rl.setOnClickListener(this);

        online_fl.setOnClickListener(this);
        online_zy.setOnClickListener(this);
        online_cxs.setOnClickListener(this);
        online_xsb.setOnClickListener(this);
        online_rsb.setOnClickListener(this);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        recyclerView.addItemDecoration(new OnlineBookDecoration(
                mContext, LinearLayoutManager.VERTICAL, 30, getResources().getColor(R.color.white)));
        getRecommData();
        checkCss();
        CheckWifiLoginTask.checkWifi(new CheckWifiLoginTask.ICheckWifiCallBack() {
            @Override
            public void portalNetWork(int isLogin) {
                APPLog.e("portalNetWork isLogin", isLogin);
                if (isLogin == 0) {
                    if (!isfinish && ActivityUtils.isContextExisted(OnlieBookCityActivity.this)) {
                        TestActivity.startWeb(OnlieBookCityActivity.this);
                    }
                }
            }
        });

    }

    /**
     * 检查css文件
     */
    protected void checkCss() {
        ConfigManager configManager = new ConfigManager(getApplication());
        UpdateCss updateCss = new UpdateCss(configManager);
        updateCss.execute(getApplication());
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_rl://返回
                this.onBackPressed();
                break;
            case R.id.my_acont_tv://账户
                if (ToolUtils.getIntence().hasLogin(this)) {
                    getDDUserInfor(true, true);
                }
                break;
            case R.id.search_rl://搜索
                startActivity(new Intent(this, SearchBookActivity.class));
                break;
            case R.id.online_fl://分类
                startActivity(new Intent(this, AllCatetoryActivity.class));
                break;
            case R.id.online_zy://租阅
                LeaseActivity.startLeaseActivity(this, "租阅");
                break;
            case R.id.online_cxs://畅销榜
                RankingListActivity.startRankingList(this, online_cxs.getText().toString(), "1");
                break;
            case R.id.online_xsb://新书榜
                RankingListActivity.startRankingList(this, online_xsb.getText().toString(), "2");
                break;
            case R.id.online_rsb://热搜榜
                RankingListActivity.startRankingList(this, online_rsb.getText().toString(), "6");
                break;
            case R.id.emty_rl_net://刷新
                getRecommData();
                break;
            default:
                break;
        }
    }

    @Override
    public void Success(String result, String code) {
        super.Success(result, code);
        if (code.equals(Connector.getInstance().getMonthlyChannelListNotify)) {
            //获取到信息进入界面
            Intent intent = new Intent(this, MyActivity.class);
            startActivity(intent);
        }
    }

    List<Media> recomlist;

    private void getRecommData() {
        progress_net_hitn.setVisibility(View.VISIBLE);
        emty_rl_net.setVisibility(View.GONE);
        setListNetAdapter(false);
        int sysVersion = share.getInt("sysVersion");
        boolean is = sysVersion == 0;
        if (is || getCurrentVersion() != sysVersion) {
            share.setCache("sysVersion", getCurrentVersion());
            if (getCurrentVersion() != sysVersion && sysVersion <= 73) {
                is = true;
            }
        }
        if (is || JsonData.getInstance().isCanrequest(this) || recomlist == null || recomlist.size() == 0) {
            RecommendEbookdeal deal = new RecommendEbookdeal(new ProgressSubscriber(recomListener, this), getTokenValue());
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
            JsonData.getInstance().setMediaStr(OnlieBookCityActivity.this, recomlist);
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
        recomlist.addAll(JsonData.getInstance().getMedias(OnlieBookCityActivity.this));
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
        int cha = (start + 8) - sales.size();
        if (cha > 0) {
            start -= cha;
        }
        return sales.subList(start, start + 8);
    }

    private void setNetAdapter() {
        progress_net_hitn.setVisibility(View.GONE);
        if (recomlist == null) recomlist = new ArrayList<>();
        emty_rl_net.setVisibility((recomlist.size() == 0) ? View.VISIBLE : View.GONE);

        if (adapter != null) {
            adapter.notifyDataSetChanged();
        } else {
            adapter = new NetBookPageRecyclerAdapter(recomlist, this, 1);
            recyclerView.setAdapter(adapter);
            adapter.setOnItemClickLIstener(new OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Media item = recomlist.get(position);
                    Intent intent = new Intent(OnlieBookCityActivity.this, EbookDetailActivity.class);
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

    /**
     * 获得当前版本
     */
    private int getCurrentVersion() {
        try {
            PackageManager manager = getPackageManager();
            PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
            int version = info.versionCode;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        /**
         * 返回时对分类筛选缓存做删除
         */
        try {
            FileUtils.getInstance().writeFile(FileUtils.getInstance().getCacheMksPath() + Connector.getInstance().mediaCategory, "");
        } catch (IOException e) {
            e.printStackTrace();
        }
        ToolUtils.getIntence().clearLoginUserData();

    }
}
