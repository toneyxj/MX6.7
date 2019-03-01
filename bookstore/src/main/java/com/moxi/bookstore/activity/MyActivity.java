
package com.moxi.bookstore.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.moxi.bookstore.R;
import com.moxi.bookstore.adapter.BoughtGvAdapter;
import com.moxi.bookstore.adapter.StoreGvAdapter;
import com.moxi.bookstore.base.BookStoreBaseActivity;
import com.moxi.bookstore.bean.BoughtBookData;
import com.moxi.bookstore.bean.Message.MediaDetail;
import com.moxi.bookstore.bean.StoreBook;
import com.moxi.bookstore.bean.StoreUpData;
import com.moxi.bookstore.bean.UserInfo;
import com.moxi.bookstore.bean.UserInfoData;
import com.moxi.bookstore.db.EbookDB;
import com.moxi.bookstore.db.TableConfig;
import com.moxi.bookstore.db.TableOperate;
import com.moxi.bookstore.http.BoughtListDeal;
import com.moxi.bookstore.http.HttpManager;
import com.moxi.bookstore.http.deal.StoreListDeal;
import com.moxi.bookstore.http.deal.UserInfoDeal;
import com.moxi.bookstore.http.listener.HttpOnNextListener;
import com.moxi.bookstore.http.subscribers.ProgressSubscriber;
import com.moxi.bookstore.request.json.Connector;
import com.moxi.bookstore.utils.StartUtils;
import com.moxi.bookstore.utils.ToolUtils;
import com.mx.mxbase.constant.APPLog;
//import com.onyx.android.sdk.device.DeviceInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

public class MyActivity extends BookStoreBaseActivity implements AdapterView.OnItemClickListener, View.OnTouchListener, View.OnClickListener {
    @Bind(R.id.like_gv)
    GridView like_gv;
    @Bind(R.id.buy_gv)
    GridView buy_gv;
    @Bind(R.id.book_ico)
    ImageView book;
    @Bind(R.id.store_num_tv)
    TextView storeNum;
    @Bind(R.id.bought_num_tv)
    TextView boughtNum;


    @Bind(R.id.book_title_tv)
    TextView book_title_tv;
    @Bind(R.id.author_tv)
    TextView author_tv;
    @Bind(R.id.pubTime_tv)
    TextView pubTime_tv;
    @Bind(R.id.time_tv)
    TextView time_tv;
    @Bind(R.id.publischer_tv)
    TextView publischer_tv;
    @Bind(R.id.progress_tv)
    TextView progress_tv;
    @Bind(R.id.emty1_tv)
    TextView emty1_tv;
    @Bind(R.id.item_ly)
    LinearLayout item_ly;

    @Bind(R.id.mainbalence_tv)
    TextView mainbalence;
    @Bind(R.id.subbalence_tv)
    TextView subbalence;
    @Bind(R.id.acount_group_pb)
    ProgressBar acount_group_pb;
    @Bind(R.id.like_group_pb)
    ProgressBar like_group_pb;
    @Bind(R.id.buy_group_pb)
    ProgressBar buy_group_pb;

    @Bind(R.id.acount_group_rl)
    RelativeLayout acount_group_rl;
    @Bind(R.id.like_reflash)
    View like_reflash;
    @Bind(R.id.buy_reflash)
    View buy_reflash;
    @Bind(R.id.acount_reflash)
    View acount_reflash;

    EbookDB lastbooke;
    StoreGvAdapter gvAdapter1;
    BoughtGvAdapter gvAdapter2;
    List<StoreBook> sblist;
    List<MediaDetail> bblist;
    List<EbookDB> dBookList;
    String token;
    int GridviewItemHeight;

    public static final int CARTL_RES=1;
    public static final int EBDETAIL_RES=2;
    private int buyBookNumber=0;
    @Override
    protected int getMainContentViewId() {
        return R.layout.activity_my;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        sblist=new ArrayList<>();
        bblist=new ArrayList<>();
        like_gv.setOnItemClickListener(this);
        buy_gv.setOnItemClickListener(this);
        like_gv.setOnTouchListener(this);
        buy_gv.setOnTouchListener(this);
        acount_reflash.setOnClickListener(this);
        like_reflash.setOnClickListener(this);
        buy_reflash.setOnClickListener(this);

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    private void getToken() {
        if (ToolUtils.getIntence().hasLogin(this)){
            if (token==null||!ToolUtils.getIntence().getToken(this).getToken().equals(token)){
                token=ToolUtils.getIntence().getToken(this).getToken();
                startMyActivity();
            }
        }else {
            this.finish();
        }
    }

    private void startMyActivity(){
        dBookList= TableOperate.getInstance().query(TableConfig.TABLE_NAME,TableConfig.E_LASTREADTIME +" Desc");
        buyBookNumber=0;
//        if (sblist.size()==0)
            getStoreBooks();
//        if (bblist.size()==0)
            getBoughtBooks();

        // initViewHy();
        getAcountData();
    }

    private void initViewHy() {
        APPLog.e("dBookList:"+dBookList.size());
        if (null!=dBookList&0<dBookList.size()){
            lastbooke=dBookList.get(0);
            book_title_tv.setText(lastbooke.name);
            author_tv.setText("作者: "+lastbooke.author);
            pubTime_tv.setText("出版时间: "+ToolUtils.getIntence().dateToStr1(lastbooke.publishtime));
            time_tv.setText("下载时间: "+ToolUtils.getIntence().dateToStr1(lastbooke.downloadtime));
            String pub=lastbooke.publisher;
            if (null==pub||pub.equals(""))
                pub="不详";
            publischer_tv.setText("出版社: "+pub);

            progress_tv.setText(ToolUtils.getIntence().getEbookProgress(lastbooke.progress));



            item_ly.setVisibility(View.VISIBLE);
        }else
            emty1_tv.setVisibility(View.VISIBLE);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            book.measure(0,0);
            GridviewItemHeight=book.getHeight();
        }
    }

    /**
     * 我的收藏
     */
    private void getStoreBooks(){
        StoreListDeal storeListDeal=new StoreListDeal(new ProgressSubscriber(storeListener,this)
                ,token,"");
        HttpManager manager=HttpManager.getInstance();
        manager.doHttpDeal(storeListDeal);
        like_group_pb.setVisibility(View.VISIBLE);
    }

    /**
     * 已购买书籍
     */
    private void getBoughtBooks(){
        String id="";
        if (bblist.size()!=0) id= String.valueOf(bblist.get(bblist.size() - 1).getMediaAuthorityId());
        BoughtListDeal boughtlistdeal=new BoughtListDeal(new ProgressSubscriber(boughtlistListener,this),
                token,id);
        HttpManager manager=HttpManager.getInstance();
        manager.doHttpDeal(boughtlistdeal);
        buy_group_pb.setVisibility(View.VISIBLE);
    }

    HttpOnNextListener storeListener=new HttpOnNextListener<StoreUpData>() {
        @Override
        public void onNext(StoreUpData data) {
            if (isfinish)return;
            like_group_pb.setVisibility(View.GONE);
            List<StoreBook> list=data.getStoreUpList();
            int size=0;
            if (null!=list)
                size=list.size();
            storeNum.setText("已收藏 ( "+size+" )");
            if (0<size){
                sblist.clear();
                for (StoreBook book:list) {
                    sblist.add(book);
                }
                list.clear();
                if (4<sblist.size())
                    list=sblist.subList(0,4);
                else
                    list=sblist;
                gvAdapter1=new StoreGvAdapter(MyActivity.this,list,334);
                like_gv.setAdapter(gvAdapter1);
                like_gv.setVisibility(View.VISIBLE);
                like_reflash.setVisibility(View.GONE);
            }
        }

        @Override
        public void onError() {
            if (isfinish)return;
            like_group_pb.setVisibility(View.GONE);
            like_reflash.setVisibility(View.VISIBLE);
        }
    };
    HttpOnNextListener boughtlistListener=new HttpOnNextListener<BoughtBookData>() {
        @Override
        public void onNext(BoughtBookData data) {
            if (isfinish)return;
            buy_group_pb.setVisibility(View.GONE);
            List<MediaDetail> list=data.getMediaList();

            int size=0;
            if (null!=list)
                size=list.size();
            if (buyBookNumber==0) {
                boughtNum.setText((size >= 100 ? "已购买 ( " + 99 + "+ )" : "已购买 ( " + size + " )"));
                if (size>=100) {
                    getUserBuyBooks(false);//获得当前购买数
                }else {
                    buyBookNumber=size;
                }
            }else {
                boughtNum.setText("已购买 ( " + buyBookNumber + " )");
            }
            if (0<size) {
                bblist.clear();
                for (MediaDetail book : list) {
                    bblist.add(book);
                }
                list.clear();
                if (4 < bblist.size())
                    list = bblist.subList(0, 4);
                else
                    list = bblist;
                gvAdapter2 = new BoughtGvAdapter(MyActivity.this, list, 334);
                buy_gv.setAdapter(gvAdapter2);
                buy_gv.setVisibility(View.VISIBLE);
                buy_reflash.setVisibility(View.GONE);
            }
        }

        @Override
        public void onError() {
            if (isfinish)return;
            buy_group_pb.setVisibility(View.GONE);
            buy_reflash.setVisibility(View.VISIBLE);
        }
    };

    @Override
    public void Success(String result, String code) {
        super.Success(result, code);
        if (code.equals(Connector.getInstance().myProperty)){
            try {
                JSONObject object=new JSONObject(result);
                JSONObject data=object.getJSONObject("data");
                buyBookNumber=data.getInt("bookCount");
                boughtNum.setText("已购买 ( " + buyBookNumber + " )");
                if (clickMoreBuy){
                    goMoreBuyed(null);
                }
                clickMoreBuy=false;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onActivityResumed(Activity activity) {
//        DeviceInfo.currentDevice.showSystemStatusBar(this);
        getToken();

    }

    private void getAcountData() {
        UserInfoDeal deal=new UserInfoDeal(new ProgressSubscriber(acountListener,this),token);
        HttpManager manger=HttpManager.getInstance();
        manger.doHttpDeal(deal);
        acount_group_pb.setVisibility(View.VISIBLE);
    }

    HttpOnNextListener acountListener=new HttpOnNextListener<UserInfoData>() {
        @Override
        public void onNext(UserInfoData o) {
            if (isfinish)return;
            acount_group_pb.setVisibility(View.GONE);
            UserInfo user=o.getUserInfo();
            updataAcounr(user);
        }

        @Override
        public void onError() {
            if (isfinish)return;
            acount_group_rl.setVisibility(View.GONE);
            acount_group_pb.setVisibility(View.GONE);
            acount_reflash.setVisibility(View.VISIBLE);
        }
    };

    private void updataAcounr(UserInfo user) {
        mainbalence.setText("金铃铛 : "+user.getMainBalance());
        subbalence.setText("银铃铛 : "+user.getSubBalance());
        acount_group_rl.setVisibility(View.VISIBLE);
        acount_reflash.setVisibility(View.GONE);
    }

    public void goCharge(View v){
        if (ToolUtils.getIntence().showBindingDDUser(this)) {
            startActivityForResult(new Intent(this, ChargeActivity.class), ChargeActivity.CHARGE);
        }
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

    public void goBack(View v){
        finish();
    }
    public void cartList(View v) {
        Intent cartIt=new Intent();
        cartIt.setClass(this,CartActivity.class);
        startActivityForResult(cartIt,CARTL_RES);

    }
    public void goAcount(View v){
       /* Intent it=new Intent(this,AcountActivity.class);
        it.putExtra("token",token);
        startActivity(it);*/
    }
    /**
     * 继续阅读
     * @param v
     */
    public void keepRead(View v){
        if (StartUtils.OpenDDRead(this,lastbooke.getFilePath()))return;
        StartUtils.OpenDDRead(this,lastbooke);
    }

    /**
     * 历史阅读
     * @param v
     */
    public void readHistory(View v){
        Intent intent=new Intent(this,MoreActivity.class);
        intent.putExtra("FLAG",MoreActivity.HISTORY);
        startActivity(intent);
    }
private boolean clickMoreBuy=false;
    public void goMoreBuyed(View v){
        if (buyBookNumber==0){
            getUserBuyBooks(true);
            clickMoreBuy=true;
            return;
        }
        Intent intent=new Intent(this,MoreActivity.class);
        intent.putExtra("FLAG",MoreActivity.BUY);
        intent.putExtra("buyBookNumber",buyBookNumber);
        intent.putParcelableArrayListExtra("DATA", (ArrayList<? extends Parcelable>) bblist);
        startActivity(intent);
    }

    public void goMoreLiked(View v){
        Intent intent=new Intent(this,MoreActivity.class);
        intent.putExtra("FLAG",MoreActivity.LIKE);
        intent.putParcelableArrayListExtra("DATA", (ArrayList<? extends Parcelable>) sblist);
        startActivityForResult(intent,MyActivity.EBDETAIL_RES);
    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        APPLog.e(parent.getId()+"id");
        long mediaId=0L;
        String bookname="";
        double lowprice=0L,orgprice=0L;
        int flag=EbookDetailActivity.NOMORE;
        if (R.id.like_gv==parent.getId()){
            StoreBook item=sblist.get(position);
            mediaId=item.getProductId();
            bookname=item.getBookName();
            double price=0f;
            if (item.getPrice().equals("免费")){
                price=0;
            }else
                price=Double.valueOf(item.getPrice().replace("￥",""));
            lowprice=price;
            orgprice=price;
        }else if (R.id.buy_gv==parent.getId()){
            APPLog.e("buy_Gv");
            flag=EbookDetailActivity.BOUGHT;
            MediaDetail item=bblist.get(position);
            mediaId=item.getMediaId();
            bookname=item.getTitle();
            lowprice=0l;
            orgprice=0l;
        }

        Intent intent=new Intent(MyActivity.this,EbookDetailActivity.class);
        intent.putExtra("saleId",mediaId);
        intent.putExtra("booktitle",bookname);
        intent.putExtra("lowestprice",lowprice);
        intent.putExtra("saleprice",orgprice);
        intent.putExtra("FLAG",flag);
        startActivityForResult(intent,EBDETAIL_RES);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction()==MotionEvent.ACTION_MOVE)
            return true;
        else
            return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

       getStoreBooks();
       getBoughtBooks();
       getAcountData();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        DeviceInfo.currentDevice.showSystemStatusBar(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.acount_reflash:
                getAcountData();
                break;
            case R.id.like_reflash:
                getStoreBooks();
                break;
            case R.id.buy_reflash:
                getBoughtBooks();
                break;
        }
    }
}
