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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.moxi.bookstore.R;
import com.moxi.bookstore.adapter.BookChanelAdapter;
import com.moxi.bookstore.base.BookStoreBaseActivity;
import com.moxi.bookstore.bean.ChanelData;
import com.moxi.bookstore.bean.Media;
import com.moxi.bookstore.bean.Sale;
import com.moxi.bookstore.interfacess.OnFlingListener;
import com.moxi.bookstore.modle.mediaCategory.LeaseRequestModel;
import com.moxi.bookstore.modle.mediaCategory.MonthlyChannel;
import com.moxi.bookstore.modle.mediaCategory.ZYCategotyModel;
import com.moxi.bookstore.request.ReuestKeyValues;
import com.moxi.bookstore.request.json.Connector;
import com.moxi.bookstore.request.json.JsonAnalysis;
import com.moxi.bookstore.utils.ToolUtils;
import com.moxi.bookstore.view.HSlidableListView;
import com.moxi.bookstore.view.add.VipInformationAddView;
import com.mx.mxbase.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * 租阅列表页面
 */
public class LeaseActivity extends BookStoreBaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener, OnFlingListener {
    /**
     * @param context
     * @param title   标题
     */
    public static void startLeaseActivity(Context context, String title) {
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        Intent intent = new Intent(context, LeaseActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Bind(R.id.back_rl)
    RelativeLayout back_rl;
    @Bind(R.id.channel_tv)
    TextView channel_tv;
    @Bind(R.id.search_rl)
    RelativeLayout search_rl;

    @Bind(R.id.filtrate_category)
    TextView filtrate_category;


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
    int totalSize, pageCount = 0, courrentPage = 0;
    private List<Sale> lisData = new ArrayList<>();
    private List<Sale> pageData = new ArrayList<>();
    BookChanelAdapter booksadapter;
    /**
     * 请求数集合
     */
    private LeaseRequestModel requestModel;
    private ZYCategotyModel categotyModel;

    //添加listFooter
    private VipInformationAddView vipInformationAddView;
    private int pageSize=5;

    @Override
    protected int getMainContentViewId() {
        return R.layout.activity_lease;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        Bundle bundle = savedInstanceState;
        if (bundle == null) {
            bundle = getIntent().getExtras();
            requestModel = new LeaseRequestModel();
        } else {
            requestModel = (LeaseRequestModel) bundle.getSerializable("model");
            categotyModel = (ZYCategotyModel) bundle.getSerializable("categotyModel");
        }
        title = bundle.getString("title");


        //获取传递数据完成
        back_rl.setOnClickListener(this);
        search_rl.setOnClickListener(this);
        channel_tv.setText(title);

        last_page.setOnClickListener(this);
        next_page.setOnClickListener(this);
        reflash_tv.setOnClickListener(this);

        initVipInformationAddView(null,false);
        booksadapter = new BookChanelAdapter(this);
        booklist_lv.setAdapter(booksadapter);

        booklist_lv.setOnItemClickListener(this);
        booklist_lv.setOnFlingListener(this);
        filtrate_category.setOnClickListener(this);
        token=getTokenValue();

        getMonthlyChannelData(true);
    }

    private void getSaleData() {
        List<ReuestKeyValues> valuePairs = new ArrayList<>();
        valuePairs.add(new ReuestKeyValues("action", requestModel.action));
        valuePairs.add(new ReuestKeyValues("category", requestModel.getCategory()));
        valuePairs.add(new ReuestKeyValues("channelType", requestModel.getCategory()));
        valuePairs.add(new ReuestKeyValues("vipOnly", requestModel.vipOnly));
        valuePairs.add(new ReuestKeyValues("dimension", requestModel.dimension));
        valuePairs.add(new ReuestKeyValues("startIndex", requestModel.startIndex));
        valuePairs.add(new ReuestKeyValues("level", requestModel.level));
        valuePairs.add(new ReuestKeyValues("start", String.valueOf(requestModel.getStart())));
        valuePairs.add(new ReuestKeyValues("end", String.valueOf(requestModel.getEnd())));
        valuePairs.add(getthisToken());
        getData(valuePairs, Connector.getInstance().mediaCategoryLeaf, Connector.getInstance().url, true, "");

    }
    private void getMonthlyChannelData(boolean is){
        if (StringUtils.isNull(getTokenValue())){
            initVipInformationAddView(null,true);
            if (lisData.size()==0) {
                setFiltrateCategory();
            }
            return;
        }
        getDDUserInfor(is,false);
    }

    /**
     * 初始化数据
     * @param channel
     */
    private void  initVipInformationAddView(MonthlyChannel channel,boolean isRequest){
        if (vipInformationAddView==null){
            vipInformationAddView=new VipInformationAddView(this, new VipInformationAddView.ClickVipListener() {
                @Override
                public void onClickVip() {
                    if (!vipInformationAddView.isRequest()){
                        getMonthlyChannelData(true);
                    }else if (!ToolUtils.getIntence().hasLogin(LeaseActivity.this)){
                        resumeRefuresh=true;
                    }else {//跳转vip购买界面
                        if (ToolUtils.getIntence().showBindingDDUser(LeaseActivity.this)) {
                            startActivityForResult(new Intent(LeaseActivity.this, GetVipActivity.class), 11);
                        }
                    }
                }
            });
            booklist_lv.addHeaderView(vipInformationAddView);
        }
        vipInformationAddView.setInit(channel,isRequest);
    }

    @Override
    public void Success(String result, String code) {
        if (code.equals(Connector.getInstance().mediaCategoryLeaf)) {
            ChanelData subject = JsonAnalysis.getInstance().getChanelData(result);
            if (requestModel.currentPage == 0) {
                courrentPage = 0;
                lisData.clear();
            } else if (subject.getSaleList().size() > pageSize) {
                courrentPage++;
            }
            lisData.addAll(subject.getSaleList());
            body_ll.setVisibility(View.VISIBLE);
            error_body.setVisibility(View.GONE);
            totalSize = subject.getTotal();
            getPageCount();
            initAdapter();
        }else if (code.equals(Connector.getInstance().getMonthlyChannelList)){
            initVipInformationAddView(JsonAnalysis.getInstance().getMonthlyChannel(result),true);
            if (lisData.size()==0) {
                setFiltrateCategory();
            }
        }
    }

    @Override
    public void fail(String code) {
        if (code.equals(Connector.getInstance().mediaCategoryLeaf)) {
            if (null == lisData || lisData.size() == 0 || requestModel.currentPage == 0) {
                body_ll.setVisibility(View.GONE);
                error_body.setVisibility(View.VISIBLE);
            }
        }else if (code.equals(Connector.getInstance().getMonthlyChannelList)){
            if (lisData.size()==0) {
                setFiltrateCategory();
            }
        }
    }

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
        outState.putSerializable("model", requestModel);
        outState.putSerializable("categotyModel", categotyModel);
    }

    @Override
    public void onActivityRestoreInstanceState(Bundle savedInstanceState) {

    }

    private void initAdapter() {
        if (courrentPage < 0) courrentPage = 0;
        int dataPage = lisData.size() / pageSize;
        dataPage += lisData.size() % pageSize == 0 ? 0 : 1;
        if (courrentPage > dataPage) {
            courrentPage = dataPage - 1;
        }
        pageData.clear();
        int start = courrentPage * pageSize;
        if ((courrentPage + 1) == pageCount) {
            pageData.addAll(lisData.subList(start, lisData.size()));
        } else {
            pageData.addAll(lisData.subList(start, start + pageSize));
        }
        Glide.clear(booklist_lv);
        booksadapter.setData(pageData);
        courent_page_tv.setText(String.valueOf(courrentPage + 1) + "/" + pageCount);
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
            case R.id.filtrate_category:
                LeaseCategoryActivity.startLeaseCategory(this, categotyModel);
                break;
            case R.id.last_page:
                goLastPage();
                break;
            case R.id.next_page:
                goNextPage();
                break;
            case R.id.reflash_tv:
                getMonthlyChannelData(true);
                break;
            case R.id.get_vip://购买vip

                break;
            default:
                break;
        }
    }

    private void goLastPage() {
        if (isDialogShow()) return;
        if (courrentPage > 0) {
            courrentPage--;
            initAdapter();
        } else
            ToastUtil("已经是首页!");

    }

    private void goNextPage() {
        if (isDialogShow()) return;
        if (lisData.size() > (courrentPage + 1) * pageSize) {
            courrentPage++;
            initAdapter();
        } else {
            if (lisData.size() < totalSize) {
                //请求下一100条数据
                requestModel.currentPage += 1;
                getSaleData();
            } else
                ToastUtil("已经是最后一页!");
        }

    }
    private boolean resumeRefuresh=false;
    private String token=null;

    @Override
    protected void onResume() {
        super.onResume();
        if (resumeRefuresh){
            getMonthlyChannelData(false);
            resumeRefuresh=false;
        }else if (token!=null&&!token.equals(getTokenValue())){
            //token有改变有登录
            token=getTokenValue();
            getMonthlyChannelData(false);
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
        if (position==0)return;
        position--;
        Media item = pageData.get(position).getMediaList().get(0);
        if (item.getMediaType() == 2) {
            Intent intent = new Intent(LeaseActivity.this, EbookDetailActivity.class);
            intent.putExtra("saleId", item.getSaleId());
            intent.putExtra("booktitle", item.getTitle());
            intent.putExtra("lowestprice", item.getLowestPrice());
            intent.putExtra("saleprice", item.getSalePrice());
            //intent.putExtra("Media",item);
            startActivity(intent);
            resumeRefuresh=true;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 10) {
                categotyModel = (ZYCategotyModel) data.getSerializableExtra("model");
                if ((categotyModel == null && !requestModel.getCategory().equals("DZS"))) {
                    setFiltrateCategory();
                } else if (categotyModel != null) {//不等于空的情况
                    if (categotyModel.catetoryList != null && categotyModel.catetoryList.size() > 0) {
                        if (!requestModel.getCategory().equals(categotyModel.catetoryList.get(0).code)) {
                            initBackdata();
                        }
                    } else {
                        if (!requestModel.getCategory().equals(categotyModel.code)) {
                            initBackdata();
                        }
                    }

                }
            }else if (requestCode==11){//返回刷新界面
                getMonthlyChannelData(false);
            }
        }
    }
    private void initBackdata(){
        getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setFiltrateCategory();
            }
        },500);
    }

    private void setFiltrateCategory() {
        String value = "全部";
        courrentPage=0;
        if (categotyModel != null) {
            value = categotyModel.name + " ♦ ";
            if (categotyModel.catetoryList != null && categotyModel.catetoryList.size() > 0) {
                value += categotyModel.catetoryList.get(0).name;
                //赋值给请求参数
                requestModel.setCategory(categotyModel.catetoryList.get(0).code);
            } else {
                value += "全部";
                requestModel.setCategory(categotyModel.code);
            }
        } else {
            requestModel.setCategory("DZS");
        }
        filtrate_category.setText(value);
        getSaleData();
    }
}
