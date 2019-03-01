package com.moxi.bookstore.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.moxi.bookstore.R;
import com.moxi.bookstore.adapter.BookChanelAdapter;
import com.moxi.bookstore.base.BookStoreBaseActivity;
import com.moxi.bookstore.bean.ChanelData;
import com.moxi.bookstore.bean.Media;
import com.moxi.bookstore.bean.Sale;
import com.moxi.bookstore.http.HttpManager;
import com.moxi.bookstore.http.deal.SubRankingDeal;
import com.moxi.bookstore.http.listener.HttpOnNextListener;
import com.moxi.bookstore.http.subscribers.ProgressSubscriber;
import com.moxi.bookstore.interfacess.OnFlingListener;
import com.moxi.bookstore.requestModel.RankingListModel;
import com.moxi.bookstore.view.HSlidableListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * 排行榜列表界面
 */
public class RankingListActivity extends BookStoreBaseActivity implements View.OnClickListener ,AdapterView.OnItemClickListener,OnFlingListener {
    /**
     * @param context
     * @param title    标题
     * @param rankType 榜单类型
     */
    public static void startRankingList(Context context, String title, String rankType) {
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putString("rankType", rankType);
        Intent intent = new Intent(context, RankingListActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Bind(R.id.back_rl)
    RelativeLayout back_rl;
    @Bind(R.id.channel_tv)
    TextView channel_tv;
    @Bind(R.id.search_rl)
    RelativeLayout search_rl;

    @Bind(R.id.money_group)
    RadioGroup money_group;
    @Bind(R.id.rank_group)
    RadioGroup rank_group;

    @Bind(R.id.body_ll)
    LinearLayout body_ll;

    @Bind(R.id.booklist_lv)
    HSlidableListView booklist_lv;

    @Bind(R.id.last_page)
    ImageButton last_page;
    @Bind(R.id.courent_page_tv)
    TextView courent_page_tv;
    @Bind(R.id.next_page)
    ImageButton next_page;

    //无数据布局
    @Bind(R.id.error_body)
    RelativeLayout error_body;
    @Bind(R.id.reflash_tv)
    TextView reflash_tv;


    private String title;
    /**
     * 榜单类型 1 畅销榜 2 新书榜 3 飙升榜 4 热评榜 5 阅读榜 6 热搜榜 7 听书榜 8 租阅榜
     */
    private String rankType;
    int totalSize, pageCount = 0, courrentPage = 0;
    private List<Sale> lisData=new ArrayList<>();
    private List<Sale> pageData=new ArrayList<>();
    BookChanelAdapter booksadapter;
    /**
     * 请求数集合
     */
    private RankingListModel rankingListModel;

    private int pageSize=4;
    @Override
    protected int getMainContentViewId() {
        return R.layout.activity_ranking_list;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        Bundle bundle = savedInstanceState;
        if (bundle == null) {
            bundle = getIntent().getExtras();
        }
        title = bundle.getString("title");
        rankType = bundle.getString("rankType");

        //获取传递数据完成
        back_rl.setOnClickListener(this);
        search_rl.setOnClickListener(this);
        channel_tv.setText(title);

        money_group.setOnCheckedChangeListener(moneyListener);
        rank_group.setOnCheckedChangeListener(rankListener);

        rankingListModel=new RankingListModel();
        rankingListModel.rankType=rankType;

        last_page.setOnClickListener(this);
        next_page.setOnClickListener(this);
        reflash_tv.setOnClickListener(this);

        booksadapter = new BookChanelAdapter(this);
        booklist_lv.setAdapter(booksadapter);
        booklist_lv.setOnItemClickListener(this);
        booklist_lv.setOnFlingListener(this);

        if (!rankType.equals("1")){
            money_group.setVisibility(View.INVISIBLE);
        }
        getSaleData();
    }

    /**
     * 切换付费与否
     */
    RadioGroup.OnCheckedChangeListener moneyListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.pay_money://付费
                    rankingListModel.setPayType("1");
                    getSaleData();
                    break;
                case R.id.free_money://免费
                    rankingListModel.setPayType("2");
                    getSaleData();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 切换筛选日期
     */
    RadioGroup.OnCheckedChangeListener rankListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.month_rank://月榜
                    rankingListModel.setTimeDimension("3");
                    getSaleData();
                    break;
                case R.id.week_rank://周榜
                    rankingListModel.setTimeDimension("2");
                    getSaleData();
                    break;
                case R.id.day_rank://日榜
                    rankingListModel.setTimeDimension("1");
                    getSaleData();
                    break;
                default:
                    break;
            }
        }
    };
    private void getSaleData() {
        SubRankingDeal chaneldeal = new SubRankingDeal(new ProgressSubscriber(httpOnNextListener, this),rankingListModel);
        HttpManager manager = HttpManager.getInstance();
        manager.doHttpDeal(chaneldeal);
        showDialog("加载中...");
    }

    HttpOnNextListener httpOnNextListener=new HttpOnNextListener<ChanelData>() {
        @Override
        public void onNext(ChanelData subject) {
            if (isfinish) return;
            hideDialog();
            if (rankingListModel.currentPage==0){
                courrentPage=0;
                lisData.clear();
            }else if (subject.getSaleList().size()>pageSize){
                courrentPage++;
            }
            lisData.addAll(subject.getSaleList());
            body_ll.setVisibility(View.VISIBLE);
            error_body.setVisibility(View.GONE);
            totalSize = subject.getTotal();
            getPageCount();
            initAdapter();
        }

        @Override
        public void onError() {
            if (isfinish) return;
            hideDialog();
            if (null == lisData || lisData.size() == 0||rankingListModel.currentPage==0) {
                body_ll.setVisibility(View.GONE);
                error_body.setVisibility(View.VISIBLE);
            }
        }
    };
    private void getPageCount() {
        pageCount = totalSize / pageSize;
        if (0 < totalSize % pageSize)
            pageCount++;
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
        outState.putString("title", title);
        outState.putString("rankType", rankType);
    }

    @Override
    public void onActivityRestoreInstanceState(Bundle savedInstanceState) {

    }

    private void initAdapter(){
        if (courrentPage<0)courrentPage=0;
        int dataPage=lisData.size()/pageSize;
        dataPage+=lisData.size()%pageSize==0?0:1;
        if (courrentPage>dataPage){
            courrentPage=dataPage-1;
        }
        pageData.clear();
        int start=courrentPage * pageSize;
        if ((courrentPage+1)==pageCount){
            pageData.addAll(lisData.subList(start,lisData.size()));
        }else{
            pageData.addAll(lisData.subList(start,start+pageSize));
        }
        Glide.clear(booklist_lv);
        booksadapter.setData(pageData);
        courent_page_tv.setText(String.valueOf(courrentPage+1) + "/"+pageCount);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_rl://返回
                this.onBackPressed();
                break;
            case R.id.search_rl://搜索
                startActivity(new Intent(this, SearchBookActivity.class));
                break;
            case R.id.last_page:
                goLastPage();
                break;
            case R.id.next_page:
                goNextPage();
                break;
            case R.id.reflash_tv:
                getSaleData();
                break;
            default:
                break;
        }
    }
    private void goLastPage() {
        if (courrentPage > 0) {
            courrentPage--;
            initAdapter();
        } else
            ToastUtil("已经是首页!");

    }

    private void goNextPage() {
        if (lisData.size()> (courrentPage+1) * pageSize) {
            courrentPage++;
            initAdapter();
        } else {
            if (lisData.size() < totalSize) {
                //请求下一100条数据
                rankingListModel.currentPage+=1;
                getSaleData();
            } else
                ToastUtil("已经是最后一页!");
        }

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
    protected void onDestroy() {
        super.onDestroy();
        lisData.clear();
        pageData.clear();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Media item = pageData.get(position).getMediaList().get(0);
        if (item.getMediaType() == 2) {
            Intent intent = new Intent(RankingListActivity.this, EbookDetailActivity.class);
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

    @Override
    public void onLeftFling() {
        goNextPage();
    }

    @Override
    public void onRightFling() {
        goLastPage();
    }
}
