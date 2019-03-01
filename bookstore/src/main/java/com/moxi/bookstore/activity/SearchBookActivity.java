package com.moxi.bookstore.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.moxi.bookstore.R;
import com.moxi.bookstore.adapter.HistoryAdapter;
import com.moxi.bookstore.adapter.SearchMediaGvAdapter;
import com.moxi.bookstore.base.BookStoreBaseActivity;
import com.moxi.bookstore.bean.SearchMedia;
import com.moxi.bookstore.bean.SearchMediaData;
import com.moxi.bookstore.db.DBHistoryUtils;
import com.moxi.bookstore.http.HttpManager;
import com.moxi.bookstore.http.deal.SubSearchMediadeal;
import com.moxi.bookstore.http.listener.HttpOnNextListener;
import com.moxi.bookstore.http.subscribers.ProgressSubscriber;
import com.moxi.bookstore.interfacess.OnFlingListener;
import com.moxi.bookstore.modle.SearchHistory;
import com.moxi.bookstore.view.HSlidableGridView;
import com.mx.mxbase.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

import static com.moxi.bookstore.R.id.booklist_lv;

public class SearchBookActivity extends BookStoreBaseActivity implements AdapterView.OnItemClickListener,
        OnFlingListener,View.OnClickListener {

    @Bind(R.id.back_rl)
    TextView back_rl;
    @Bind(R.id.keyword_ed)
    EditText keyword_ed;
    @Bind(R.id.result_count_tv)
    TextView result_num;
    @Bind(R.id.body_ll)
    LinearLayout body;
    @Bind(R.id.books_gv)
    HSlidableGridView books_gv;
    @Bind(R.id.emty_rl)
    RelativeLayout emty_rl;
    @Bind(R.id.hitn_no_data)
    TextView hitn_no_data;
    @Bind(R.id.totle_page_tv)
    TextView totle_page_tv;
    @Bind(R.id.last_page)
    ImageButton last_page;
    @Bind(R.id.next_page)
    ImageButton next_page;
    @Bind(R.id.error_body)
    View errorbody;
    @Bind(R.id.history_gridview)
    GridView history_gridview;

    String keyword;
    List<SearchMedia> listData=new ArrayList<>();
    /**
     * 添加进入布局list
     */
    List<SearchMedia> listAddData=new ArrayList<>();

    SearchMediaGvAdapter adapter;
    /**
     * 当前显示页数
     */
    private int CurrentIndex = 0;
    /**
     * 总页数
     */
    private int totalIndex = 1;

    private int  GridviewItemHeight=0;
    @Override
    protected int getMainContentViewId() {
        return R.layout.activity_search_book;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        initView();
        initListener();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            GridviewItemHeight=books_gv.getHeight()/3;
            adapter=new SearchMediaGvAdapter(this,listAddData,GridviewItemHeight);
            books_gv.setAdapter(adapter);
        }
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    private void initView() {
        keyword_ed.clearFocus();
//        history_gridview.setOnItemClickListener(historyItemClickListener);

        keyword_ed.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                showHistory(hasFocus);
            }
        });

        errorbody.setVisibility(View.GONE);
        back_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void initListener() {
        last_page.setOnClickListener(this);
        next_page.setOnClickListener(this);
        books_gv.setOnFlingListener(this);
        books_gv.setOnItemClickListener(this);
        keyword_ed.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode== KeyEvent.KEYCODE_ENTER){
                    keyword=keyword_ed.getText().toString();

                    showKeyboard(false);
                    if (!keyword.equals("")) {
                        long id=0l;
                        if (histories.size()!=0)id=histories.get(histories.size()-1).id;
                        DBHistoryUtils.addData(keyword,id);
                        doSearch();
                    }
                    return true;
                }
                return false;
            }
        });
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

    public void goBack(View v){finish();}
    public void doreflash(View v){doSearch();}
    private void doSearch(){

        if (TextUtils.isEmpty(keyword_ed.getText())){
            ToastUtil("请输入关键字");
            return;
        }
        SubSearchMediadeal deal=new SubSearchMediadeal(new ProgressSubscriber(searchlistener,this),keyword);
        HttpManager manager=HttpManager.getInstance();
        manager.doHttpDeal(deal);
        showDialog("加载中...");
    }

    HttpOnNextListener searchlistener=new HttpOnNextListener<SearchMediaData>() {
        @Override
        public void onNext(SearchMediaData obj) {
            hideDialog();
            removePaperMedia(obj.getSearchMediaPaperList());
           int resultCount=listData.size();
            CurrentIndex=0;
            if (listData.size()>0){
                body.setVisibility(View.VISIBLE);
                emty_rl.setVisibility(View.GONE);
                setListData();
                result_num.setText("("+resultCount+")");
            }else {
                body.setVisibility(View.GONE);
                emty_rl.setVisibility(View.VISIBLE);
                hitn_no_data.setText("未搜索到相关图书！");
            }

        }

        @Override
        public void onError() {
            hideDialog();
            errorbody.setVisibility(View.VISIBLE);
            body.setVisibility(View.GONE);
        }
    };
    private void setListData(){
        final int  pageSize=12;
        //计算页数
        totalIndex = listData.size() / pageSize;
        totalIndex += listData.size() % pageSize == 0 ? 0 : 1;

        //计算当前页数
        if (CurrentIndex > totalIndex - 1) {
            CurrentIndex = totalIndex - 1;
        }
        if (CurrentIndex<0)CurrentIndex=0;
        if (totalIndex==0)totalIndex=1;

        if (listData.size() == 0) {
            adapterItems(listData);
        } else if (totalIndex - 1 == CurrentIndex) {
            adapterItems(listData.subList(CurrentIndex * pageSize, listData.size()));
        } else {
            adapterItems(listData.subList(CurrentIndex * pageSize, (CurrentIndex + 1) * pageSize));
        }
        setShowText();
    }
    private void adapterItems(List<SearchMedia> List) {
        listAddData.clear();
        listAddData.addAll(List);
        if (adapter == null) {
            adapter=new SearchMediaGvAdapter(this,listAddData,GridviewItemHeight);
            books_gv.setAdapter(adapter);
        } else {
            Glide.clear(books_gv);
            adapter.notifyDataSetChanged();
        }
    }
    @Override
    public void onActivityDestroyed(Activity activity) {    }

    private void removePaperMedia(List<SearchMedia> data){
        if (null!=data&&data.size()>0){
            listData.clear();
            List<SearchMedia> list=new ArrayList<>();
            for (int i=0;i< data.size();i++) {
                 SearchMedia media=data.get(i);
                if (null!=media.getMediaId()&&"2".equals(media.getMedType())){
                    list.add(media);
                }
            }
            listData.addAll(list);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (history_gridview.getVisibility()==View.VISIBLE)return;

        SearchMedia media=listAddData.get(position);
        Intent intent=new Intent(SearchBookActivity.this,EbookDetailActivity.class);
        intent.putExtra("saleId",Long.valueOf(media.getSaleId()));

        intent.putExtra("booktitle",media.getTitle());
        intent.putExtra("lowestprice",Double.valueOf(media.getLowestPrice()));
        intent.putExtra("saleprice",Double.valueOf(media.getSalePrice()));
        //intent.putExtra("Media",item);
        startActivity(intent);
    }

    private void setShowText(){
        totle_page_tv.setText(String.valueOf(CurrentIndex + 1) + "/" + totalIndex);
    }

    @Override
    public void onLeftFling() {
        if (CurrentIndex >= totalIndex - 1) {
            return;
        } else {
            CurrentIndex++;
            setListData();
        }
    }

    @Override
    public void onRightFling() {
        if (CurrentIndex > 0 && (CurrentIndex <= totalIndex - 1)) {
            CurrentIndex--;
            setListData();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.last_page:
                onRightFling();
                break;
            case R.id.next_page:
                onLeftFling();
                break;
        }
    }
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if ( keyCode == KeyEvent.KEYCODE_PAGE_UP) {
            //上一页
            onRightFling();
            return true;
        } else if ( keyCode == KeyEvent.KEYCODE_PAGE_DOWN) {
            //下一页
            onLeftFling();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }
    private List<SearchHistory> histories=new ArrayList<>();
    private HistoryAdapter historyAdapter=null;
    private void showHistory(boolean is){

        if (is){
            //显示
            history_gridview.setVisibility(View.VISIBLE);
            histories.clear();
            histories.addAll(DBHistoryUtils.getHistorys());
            if (historyAdapter==null){
                historyAdapter=new HistoryAdapter(SearchBookActivity.this,histories);
                historyAdapter.setClickListener(historyItemClickListener);
                history_gridview.setAdapter(historyAdapter);
            }else {
                historyAdapter.notifyDataSetChanged();
            }

            if (emty_rl.getVisibility()!=View.VISIBLE&&listData.size()==0){
                emty_rl.setVisibility(View.VISIBLE);
                body.setVisibility(View.GONE);
                hitn_no_data.setText("还没有搜索内容快去搜索吧！");
            }
        }else{
            getHandler().sendEmptyMessageDelayed(10,100);
        }
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        if (msg.what==10){
            history_gridview.setVisibility(View.GONE);
        }
    }

    AdapterView.OnItemClickListener historyItemClickListener=new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            DBHistoryUtils.UpdateTime(histories.get(position).id);
            keyword=histories.get(position).searchContent;
            keyword_ed.setText(keyword);
            if (!keyword.equals(""))
                doSearch();
            showKeyboard(false);
        }
    };
    /**
     * 对软键盘的弹出与隐藏进行处理
     *
     * @param isShow
     */
    protected void showKeyboard(boolean isShow) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (isShow) {
            if (getCurrentFocus() == null) {
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            } else {
                imm.showSoftInput(getCurrentFocus(), 0);
            }
        } else {
            if (getCurrentFocus() != null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
        showHistory(isShow);
    }
    /**
     * 点击其它地方关闭软键盘
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {

            // 获得当前得到焦点的View，一般情况下就是EditText（特殊情况就是轨迹求或者实体案件会移动焦点）
            View v = getCurrentFocus();

            if (StringUtils.isShouldHideInput(v, ev)) {
                StringUtils.closeIMM(this, v.getWindowToken());
                showHistory(false);
            }else {
                showHistory(true);
            }
        }
        return super.dispatchTouchEvent(ev);
    }
}
