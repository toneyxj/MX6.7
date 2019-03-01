package com.moxi.bookstore.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.moxi.bookstore.R;
import com.moxi.bookstore.adapter.BoughtGvAdapter;
import com.moxi.bookstore.adapter.HistoryGvAdapter;
import com.moxi.bookstore.adapter.StoreGvAdapter;
import com.moxi.bookstore.base.BookStoreBaseActivity;
import com.moxi.bookstore.bean.BoughtBookData;
import com.moxi.bookstore.bean.Message.MediaDetail;
import com.moxi.bookstore.bean.StoreBook;
import com.moxi.bookstore.db.EbookDB;
import com.moxi.bookstore.db.TableConfig;
import com.moxi.bookstore.db.TableOperate;
import com.moxi.bookstore.http.BoughtListDeal;
import com.moxi.bookstore.http.HttpManager;
import com.moxi.bookstore.http.listener.HttpOnNextListener;
import com.moxi.bookstore.http.subscribers.ProgressSubscriber;
import com.moxi.bookstore.interfacess.OnFlingListener;
import com.moxi.bookstore.utils.StartUtils;
import com.moxi.bookstore.view.HSlidableGridView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

import static com.moxi.bookstore.R.id.item_title;

//import com.onyx.android.sdk.device.DeviceInfo;

public class MoreActivity extends BookStoreBaseActivity implements AdapterView.OnItemClickListener,
        OnFlingListener, View.OnClickListener {
    @Bind(R.id.books_gv)
    HSlidableGridView books_gv;
    @Bind(R.id.search)
    ImageButton search;
    @Bind(R.id.title)
    TextView title_tv;
    @Bind(item_title)
    TextView item_title_tv;
    @Bind(R.id.item_ico)
    ImageView item_ico;
    @Bind(R.id.totle_page_tv)
    TextView totle_page_tv;
    @Bind(R.id.last_page)
    ImageButton last_page;
    @Bind(R.id.next_page)
    ImageButton next_page;

    public static final int HISTORY = 1;
    public static final int LIKE = 2;
    public static final int BUY = 3;

    private int flag, GridviewItemHeight;
    List<EbookDB> historyList;
    List<StoreBook> sblist;
    List<MediaDetail> bblist;
    StoreGvAdapter gvAdapter1;
    BoughtGvAdapter gvAdapter2;
    HistoryGvAdapter gvAdapter3;
    List<Object> templist;
    int CurrentIndex = 0, totalIndex;
    private int buyBookNumber;
    private int pageCount=12;
    private int bookHeight=390;


    @Override
    protected int getMainContentViewId() {
        return R.layout.activity_more;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        if (savedInstanceState != null) finish();
        flag = getIntent().getIntExtra("FLAG", 1);
        books_gv.setOnItemClickListener(this);
        books_gv.setOnFlingListener(this);
        last_page.setOnClickListener(this);
        next_page.setOnClickListener(this);
        search.setOnClickListener(this);
        templist = new ArrayList<>();
        flagWithData();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isFinish", true);
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    private void flagWithData() {
        String title = "", item_title = "";
        int ico = R.mipmap.history_ico;

        switch (flag) {
            case HISTORY:
                historyList = TableOperate.getInstance().query(TableConfig.TABLE_NAME, TableConfig.E_LASTREADTIME + " Desc");
                title = "阅读历史";
                item_title = "已阅读(" + historyList.size() + ")";
                ico = R.mipmap.history_ico;
                initIndex(historyList);
                initTempData(historyList);
                showHistory(templist);
                break;
            case LIKE:
                sblist = getIntent().getParcelableArrayListExtra("DATA");
                title = "我的收藏";
                item_title = "已收藏(" + sblist.size() + ")";
                ico = R.mipmap.fivor_ico;
                initIndex(sblist);
                initTempData(sblist);
                showStore(templist);
                break;
            case BUY:
                bblist = getIntent().getParcelableArrayListExtra("DATA");
                buyBookNumber = getIntent().getIntExtra("buyBookNumber", bblist.size());
                title = "我的购买";
                item_title = "已购买(" + buyBookNumber + ")";
                ico = R.mipmap.bought_ico;
                initIndex(bblist);
                initTempData(bblist);
                showBought(templist);
                break;
        }
        title_tv.setText(title);
        item_title_tv.setText(item_title);
        item_ico.setImageDrawable(getResources().getDrawable(ico));
        setShowText();
    }

    private void initTempData(List<?> list) {
        if (0 < templist.size())
            templist.clear();
        if (null == list || 0 == list.size()) {
            // TODO: 2016/10/18 空数据处理
            return;
        }
        int size;
        if (list.size() > pageCount) {
            size = pageCount;
        } else
            size = list.size();
        for (int i = 0; i < size; i++) {

            templist.add(list.get(i));
        }
    }

    private void updataTemp(List<?> list) {
        int size = list.size();
        int cunt = (CurrentIndex + 1) * pageCount;
        if (0 < templist.size())
            templist.clear();
        for (int i = CurrentIndex * pageCount; i < (cunt > list.size() ? size : cunt); i++) {
            templist.add(list.get(i));
        }
    }

    @Override
    public void onActivityResumed(Activity activity) {
        freshAdapter();
//        DeviceInfo.currentDevice.showSystemStatusBar(this);
    }

    private void freshAdapter() {
        switch (flag) {
            case HISTORY:
                if (gvAdapter3!=null)
                gvAdapter3.notifyDataSetChanged();
                break;
            case BUY:
                if (gvAdapter2!=null)
                gvAdapter2.notifyDataSetChanged();
                break;
            case LIKE:
                if (gvAdapter1!=null)
                gvAdapter1.notifyDataSetChanged();
                break;
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
    }

    @Override
    public void onActivityStopped(Activity activity) {
        setResult(MyActivity.EBDETAIL_RES);
    }

    @Override
    public void onActivitySaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
    }

    @Override
    public void onActivityRestoreInstanceState(Bundle savedInstanceState) {
    }

    public void goBack(View v) {
        finish();
    }

    private void showHistory(List<?> list) {
        gvAdapter3 = new HistoryGvAdapter(this, list, bookHeight);
        books_gv.setAdapter(gvAdapter3);
        if (gvAdapter3==null) {
            gvAdapter3 = new HistoryGvAdapter(this, list, bookHeight);
        }else {
            Glide.clear(books_gv);
            gvAdapter3.notifyDataSetChanged();
        }

    }

    private void showStore(List<?> list) {
        if (gvAdapter1==null) {
            gvAdapter1 = new StoreGvAdapter(this, list, bookHeight);
            books_gv.setAdapter(gvAdapter1);
        }else {
            Glide.clear(books_gv);
            gvAdapter1.notifyDataSetChanged();
        }

    }

    private void showBought(List<?> list) {
        if (gvAdapter2==null) {
            gvAdapter2 = new BoughtGvAdapter(this, list, bookHeight);
            books_gv.setAdapter(gvAdapter2);
        }else {
            Glide.clear(books_gv);
            gvAdapter2.notifyDataSetChanged();
        }
    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    private void initIndex(List list) {
        final int pageSize = pageCount;
        //计算页数
        int size = flag == BUY ? buyBookNumber : list.size();
        totalIndex = size / pageSize;
        totalIndex += size % pageSize == 0 ? 0 : 1;

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        long mediaId;
        String bookname;
        Intent intent;
        switch (flag) {
            case HISTORY:
                EbookDB book = (EbookDB) templist.get(position);
                if (StartUtils.OpenDDRead(this, book.getFilePath())) return;
                StartUtils.OpenDDRead(this, book);
                break;
            case LIKE:
                StoreBook item = (StoreBook) templist.get(position);
                mediaId = item.getProductId();
                bookname = item.getBookName();
                intent = new Intent(this, EbookDetailActivity.class);
                intent.putExtra("saleId", mediaId);
                intent.putExtra("booktitle", bookname);
                double price = 0f;
                if (item.getPrice().equals("免费")) {
                    price = 0;
                } else
                    price = Double.valueOf(item.getPrice().replace("￥", ""));
                intent.putExtra("lowestprice", price);
                intent.putExtra("saleprice", price);
                startActivityForResult(intent, MyActivity.EBDETAIL_RES);
                break;
            case BUY:
                MediaDetail item1 = (MediaDetail) templist.get(position);
                mediaId = item1.getMediaId();
                bookname = item1.getTitle();
                intent = new Intent(this, EbookDetailActivity.class);
                intent.putExtra("saleId", mediaId);
                intent.putExtra("booktitle", bookname);
                intent.putExtra("FLAG", EbookDetailActivity.BOUGHT);
                startActivity(intent);
                break;
        }
//        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search:
                startActivity(new Intent(this,SearchBookActivity.class));
                break;
            case R.id.last_page:
                onRightFling();
                break;
            case R.id.next_page:
                onLeftFling();
                break;
        }
    }

    @Override
    public void onLeftFling() {
        if (CurrentIndex >= totalIndex - 1) {
            return;
        } else {
            if (flag == BUY && (CurrentIndex + 2) * pageCount > bblist.size() && buyBookNumber > bblist.size()) {
                getBoughtBooks();
            } else {
                if (!isNoAdd)
                    CurrentIndex++;

                isNoAdd = false;
                setListData();
            }
        }
    }

    @Override
    public void onRightFling() {
        if (CurrentIndex > 0 && (CurrentIndex <= totalIndex - 1)) {
            CurrentIndex--;
            setListData();
            // TODO: 2016/11/9 kk
            freshAdapter();
        }
    }

    private void setListData() {

        //计算当前页数
        if (CurrentIndex > totalIndex - 1) {
            CurrentIndex = totalIndex - 1;
        }
        if (CurrentIndex < 0) CurrentIndex = 0;
        if (totalIndex == 0) totalIndex = 1;

        switch (flag) {
            case HISTORY:
                updataTemp(historyList);
                showHistory(templist);
                break;
            case LIKE:
                updataTemp(sblist);
                showStore(templist);
                break;
            case BUY:
                updataTemp(bblist);
                showBought(templist);
                break;
        }
        setShowText();
    }

    private void setShowText() {
        totle_page_tv.setText(String.valueOf(CurrentIndex + 1) + "/" + totalIndex);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_PAGE_UP) {//上一页
            onRightFling();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_PAGE_DOWN) {//下一页
            onLeftFling();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        DeviceInfo.currentDevice.showSystemStatusBar(this);
    }


    /**
     * 已购买书籍
     */
    private void getBoughtBooks() {
        if (bblist.size() == 0) return;
        BoughtListDeal boughtlistdeal = new BoughtListDeal(new ProgressSubscriber(boughtlistListener, this),
                getTokenValue(), String.valueOf(bblist.get(bblist.size() - 1).getMediaAuthorityId()));
        HttpManager manager = HttpManager.getInstance();
        manager.doHttpDeal(boughtlistdeal);
        showDialog("加载书籍");
    }

    private boolean isNoAdd = false;
    HttpOnNextListener boughtlistListener = new HttpOnNextListener<BoughtBookData>() {
        @Override
        public void onNext(BoughtBookData data) {
            if (isfinish) return;
            hideDialog();
            int size = bblist.size();
            isNoAdd = size < pageCount;
            bblist.addAll(data.getMediaList());
            if (data.getMediaList().size()<=pageCount) {
//                ToastUtils.getInstance().showToastShort("图书获取已到底");
                buyBookNumber=bblist.size();
//                item_title_tv.setText("已购买(" + buyBookNumber + ")");
                initIndex(bblist);
            }
            onLeftFling();
        }

        @Override
        public void onError() {
            if (isfinish) return;
            hideDialog();
            showToast("加载失败");
        }
    };

}
