package com.moxi.bookstore.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.moxi.bookstore.R;
import com.moxi.bookstore.adapter.BookChanelAdapter;
import com.moxi.bookstore.base.BookStoreBaseActivity;
import com.moxi.bookstore.bean.ChanelData;
import com.moxi.bookstore.bean.Media;
import com.moxi.bookstore.bean.Sale;
import com.moxi.bookstore.dialog.PageSkipActivity;
import com.moxi.bookstore.http.HttpManager;
import com.moxi.bookstore.http.deal.SubChaneldeal;
import com.moxi.bookstore.http.deal.SubChannelByPrice;
import com.moxi.bookstore.http.listener.HttpOnNextListener;
import com.moxi.bookstore.http.subscribers.ProgressSubscriber;
import com.moxi.bookstore.interfacess.OnFlingListener;
import com.moxi.bookstore.view.HSlidableListView;
import com.mx.mxbase.constant.APPLog;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

public class ChanelActivity extends BookStoreBaseActivity implements AdapterView.OnItemClickListener,
        OnFlingListener, View.OnClickListener {
    @Bind(R.id.saleTop_tv)
    TextView saletop;
    @Bind(R.id.pubtimeTop_tv)
    TextView timetop;
    @Bind(R.id.priceTop_tv)
    RelativeLayout priceTop;
    @Bind(R.id.top_iv)
    ImageView top_iv;
    @Bind(R.id.bottom_iv)
    ImageView bottom_iv;
    @Bind(R.id.tv1)
    TextView tv1;
    @Bind(R.id.booklist_lv)
    HSlidableListView bookslv;
    @Bind(R.id.title)
    TextView catetory_title;
    @Bind(R.id.channel_tv)
    TextView chanel;

    @Bind(R.id.skip_page)
    RelativeLayout skip_page;
    @Bind(R.id.totle_page_tv)
    TextView totle_page_tv;
    @Bind(R.id.courent_page_tv)
    TextView courent_page_tv;

    @Bind(R.id.last_page)
    ImageButton last_page;
    @Bind(R.id.next_page)
    ImageButton next_page;
    @Bind(R.id.error_body)
    View error_body;
    @Bind(R.id.body_ll)
    LinearLayout body;

    private final static int SALE_SORT = 1;//销量
    private final static int NEWEST_SORT = 2;//最新
    private final static int LOW_PRICE_SORT = 3;//价格升序
    private final static int HIGH_PRICE_SORT = 4;//价格降序
    final static String NORMAL_COLOR = "#424242";
    int courrenSort = SALE_SORT;
    boolean priceflag = false;

    BookChanelAdapter booksadapter;
    List<Sale> data, pageData;
    String type, group, chanelname;
    int resultCount, pageCount = 0, courrentPage = 1, requestPage = 1;
    private int pageitem=4;

    @Override
    protected int getMainContentViewId() {
        return R.layout.activity_chanel;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            Intent intent = getIntent();
            type = intent.getStringExtra("chaneltype");
            APPLog.e("channelType" + type);
            group = intent.getStringExtra("group");
            chanelname = intent.getStringExtra("chaneltitle");
        } else {
            courrentPage = savedInstanceState.getInt("courrentPage");
            type = savedInstanceState.getString("type");
            group = savedInstanceState.getString("group");
            chanelname = savedInstanceState.getString("chanelname");
        }

        initView();
    }

    private void initView() {
        catetory_title.setText(group);
        chanel.setText(chanelname);
        bookslv.setDivider(null);
        booksadapter = new BookChanelAdapter(this);
        bookslv.setAdapter(booksadapter);
        bookslv.setOnItemClickListener(this);
        bookslv.setOnFlingListener(this);
        last_page.setOnClickListener(this);
        next_page.setOnClickListener(this);
        saletop.setOnClickListener(this);
        timetop.setOnClickListener(this);
        priceTop.setOnClickListener(this);
        skip_page.setOnClickListener(this);
        error_body.setVisibility(View.GONE);
    }

    @Override
    public void onActivityStarted(Activity activity) {
        if (null == data) {
            saletop.setTextColor(Color.BLACK);
            getSaleData("dd_sale", requestPage);
        }
        if (null == pageData)
            pageData = new ArrayList<>();
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
        outState.putInt("courrentPage", courrentPage);
        outState.putString("type", type);
        outState.putString("group", group);
        outState.putString("chanelname", chanelname);
    }

    @Override
    public void onActivityRestoreInstanceState(Bundle savedInstanceState) {

    }

    /**
     * @param dimension dd_sale 销量
     *                  newest 最新
     */
    private void getSaleData(String dimension, int rqPage) {
        int start = (requestPage - 1) * 100;
        int end = start + 99;
        SubChaneldeal chaneldeal = new SubChaneldeal(new ProgressSubscriber(onNextListener, this), type,
                dimension, start, end,getTokenValue());
        HttpManager manager = HttpManager.getInstance();
        manager.doHttpDeal(chaneldeal);
        showDialog("加载中...");
    }

    HttpOnNextListener onNextListener = new HttpOnNextListener<ChanelData>() {
        @Override
        public void onNext(ChanelData subject) {
            if (isfinish) return;
            hideDialog();
            if (1 < requestPage) {
                if (null != subject.getSaleList()) {
                    data.addAll(subject.getSaleList());
                    goNextPage();
                } else
                    showDialog("到底了");
                return;
            }
            data = subject.getSaleList();
            if (null == data || data.size() == 0)
                return;
            APPLog.e("pagecount:" + data.size());
            body.setVisibility(View.VISIBLE);
//            if (null != pageData)
//                pageData.clear();
//            int  index=data.size()>=4?4:data.size();
//            if (index<0)index=0;
//            for (int i = 0; i < index; i++) {
//                pageData.add(data.get(i));
//            }
            resultCount = subject.getTotal();
            getPageCount();
//            booksadapter.setData(data);
            courrentPage = 1;
//            courent_page_tv.setText(courrentPage + "/");
            initAdapter();
        }

        @Override
        public void onError() {
            if (isfinish) return;
            hideDialog();
            if (null == data || data.size() == 0) {
                body.setVisibility(View.GONE);
                error_body.setVisibility(View.VISIBLE);
            }
        }
    };

    public void goBack(View v) {
        finish();
    }

    public void searchBook(View v) {
        startActivity(new Intent(ChanelActivity.this, SearchBookActivity.class));
    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Media item = pageData.get(position).getMediaList().get(0);
        if (item.getMediaType() == 2) {
            Intent intent = new Intent(ChanelActivity.this, EbookDetailActivity.class);
            intent.putExtra("saleId", item.getSaleId());
            intent.putExtra("booktitle", item.getTitle());
            intent.putExtra("lowestprice", item.getLowestPrice());
            intent.putExtra("saleprice", item.getSalePrice());
            //intent.putExtra("Media",item);
            startActivity(intent);
        } else {
            ToastUtil("设备不支持：" + item.getMediaType());
        }
    }

    private void getPageCount() {
        pageCount = resultCount / pageitem;
        if (0 < resultCount % pageitem)
            pageCount++;
        totle_page_tv.setText(pageCount + "");
    }

    @Override
    public void onLeftFling() {
        goNextPage();
    }

    @Override
    public void onRightFling() {
        goLastPage();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.skip_page:
                if (pageCount > 0)
                    PageSkipActivity.startPageSkip(this, courrentPage-1, data.size()/pageitem);
                break;
            case R.id.last_page:
                goLastPage();
                break;
            case R.id.next_page:
                goNextPage();
                break;
            case R.id.saleTop_tv:
                doSort(SALE_SORT);
                break;
            case R.id.pubtimeTop_tv:
                doSort(NEWEST_SORT);
                break;
            case R.id.priceTop_tv:
                priceflag = !priceflag;
                if (priceflag)
                    doSort(HIGH_PRICE_SORT);
                else
                    doSort(LOW_PRICE_SORT);
                break;
        }
    }

    public void doreflash(View v) {
        getSaleData("dd_sale", requestPage);
    }

    private void goLastPage() {

        if (courrentPage > 1) {
            courrentPage--;
            initAdapter();
        } else
            ToastUtil("已经是首页!");

    }

    private void goNextPage() {
        if (data.size() > courrentPage * pageitem) {
            courrentPage++;
            initAdapter();
        } else {
            if (data.size() < resultCount) {
                // TODO: 2016/11/10  请求下一100条数据
                requestPage++;
                getNextPageRequest();
            } else
                ToastUtil("已经是最后一页!");
        }

    }
    private void initAdapter(){
        if (courrentPage<1)courrentPage=1;
        int dataPage=data.size()/pageitem;
        dataPage+=data.size()%pageitem==0?0:1;
        if (courrentPage>dataPage){
            courrentPage=dataPage;
        }
        pageData.clear();
        for (int i = (courrentPage - 1) * pageitem; i < (courrentPage * pageitem > data.size() ? data.size() : courrentPage * pageitem); i++) {
            APPLog.e("initAdapter",i + ":" + resultCount);
            pageData.add(data.get(i));
        }
        Glide.clear(bookslv);
        booksadapter.setData(pageData);
        courent_page_tv.setText(courrentPage + "/");
    }

    private void getNextPageRequest() {
        switch (courrenSort) {
            case SALE_SORT:
                getSaleData("dd_sale", requestPage);
                break;
            case NEWEST_SORT:
                getSaleData("newest", requestPage);
                break;
            case LOW_PRICE_SORT:
                getSaleDataByPrice(0, requestPage);
                break;
            case HIGH_PRICE_SORT:
                getSaleDataByPrice(2, requestPage);
                break;
        }
    }

    private void getSaleDataByPrice(int order, int rqPage) {
        int start = (rqPage - 1) * 100;
        int end = start + 99;
        SubChannelByPrice deal = new SubChannelByPrice(new ProgressSubscriber(onNextListener, this),
                type, order, start, end);
        HttpManager manager = HttpManager.getInstance();
        manager.doHttpDeal(deal);
        showDialog("加载中...");
    }

    private void doSort(int sort) {
        courrenSort = sort;
        requestPage = 1;

        clearSortState();
        switch (courrenSort) {
            case SALE_SORT:
                saletop.setTextColor(Color.BLACK);
                getSaleData("dd_sale", requestPage);
                break;
            case NEWEST_SORT:
                timetop.setTextColor(Color.BLACK);
                getSaleData("newest", requestPage);
                break;
            case LOW_PRICE_SORT:
                tv1.setTextColor(Color.BLACK);
                bottom_iv.setVisibility(View.VISIBLE);
                getSaleDataByPrice(0, requestPage);
                break;
            case HIGH_PRICE_SORT:
                tv1.setTextColor(Color.BLACK);
                top_iv.setVisibility(View.VISIBLE);
                getSaleDataByPrice(2, requestPage);
                break;
        }
    }

    private void clearSortState() {

        saletop.setTextColor(Color.parseColor(NORMAL_COLOR));
        tv1.setTextColor(Color.parseColor(NORMAL_COLOR));
        timetop.setTextColor(Color.parseColor(NORMAL_COLOR));
        top_iv.setVisibility(View.INVISIBLE);
        bottom_iv.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_PAGE_UP) {//上一页
            goLastPage();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_PAGE_DOWN) {//下一页
            goNextPage();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 10) {
            //页面跳转返回
            int page=data.getIntExtra("page",0);
            courrentPage=page+1;
            initAdapter();
        }
    }
}
