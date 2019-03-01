package com.moxi.bookstore.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.moxi.bookstore.R;
import com.moxi.bookstore.adapter.CatetoryAdapter;
import com.moxi.bookstore.base.BookStoreBaseActivity;
import com.moxi.bookstore.bean.CatetoryChanle;
import com.moxi.bookstore.bean.CatetoryChanleItem;
import com.moxi.bookstore.bean.CatetoryData;
import com.moxi.bookstore.http.HttpManager;
import com.moxi.bookstore.http.deal.Subcatetorydeal;
import com.moxi.bookstore.http.listener.HttpOnNextListener;
import com.moxi.bookstore.http.subscribers.ProgressSubscriber;
import com.moxi.bookstore.interfacess.ChanelInterf;
import com.moxi.bookstore.request.json.Connector;
import com.moxi.bookstore.utils.ToolUtils;
import com.mx.mxbase.constant.APPLog;
import com.mx.mxbase.view.SildeFrameLayout;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.Bind;

public class AllCatetoryActivity extends BookStoreBaseActivity implements ChanelInterf, SildeFrameLayout.SildeEventListener {


    @Bind(R.id.move_layout)
    SildeFrameLayout move_layout;
    @Bind(R.id.catetory_list)
    ExpandableListView catetoryExpGrid;
    @Bind(R.id.page_text)
    TextView page_text;
    @Bind(R.id.error_body)
    View errorbody;

    CatetoryData catetoryData;
    private int totalPage=0;

    @Override
    protected int getMainContentViewId() {
        return R.layout.activity_all_catetory;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        errorbody.setVisibility(View.GONE);
        //屏蔽group点击
        catetoryExpGrid.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return true;
            }
        });
        //去掉箭头
        catetoryExpGrid.setGroupIndicator(null);
        catetoryExpGrid.setDivider(null);
        catetoryExpGrid.setCacheColorHint(0x000000);
        catetoryExpGrid.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_MOVE)
                    return true;
                else
                    return false;
            }
        });
        move_layout.setListener(this);
        if (null == catetoryData) {
            getAllcatetory();
        }
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

    //请求分类数据
    public void getAllcatetory() {
        Subcatetorydeal catetorydeal = new Subcatetorydeal(new ProgressSubscriber(simpleOnNextListener, this));
        HttpManager manager = HttpManager.getInstance();
        manager.doHttpDeal(catetorydeal);
        showDialog("加载中...");
    }

    //   回调一一对应
    List<CatetoryChanle> data;
    List<CatetoryChanle> middleDatas = new ArrayList<>();
    private CatetoryAdapter adapter;
    HttpOnNextListener simpleOnNextListener = new HttpOnNextListener<CatetoryData>() {
        @Override
        public void onNext(CatetoryData subjects) {
            if (isfinish) return;
            hideDialog();
            catetoryData = subjects;
            data = subjects.getCatetoryList().get(0).getCatetoryList();
            data = reOrderData();
            catetoryExpGrid.setVisibility(View.VISIBLE);
            errorbody.setVisibility(View.GONE);
            initPageIndex();

            APPLog.e("已封装：\n" + subjects.getCatetoryList().get(0).getName());
        }

        @Override
        public void onError() {
            if (isfinish) return;
            hideDialog();
            catetoryExpGrid.setVisibility(View.GONE);
            errorbody.setVisibility(View.VISIBLE);
        }

    };

    //finish activity
    public void goBack(View v) {
        finish();
    }

    public void searchBook(View v) {
        startActivity(new Intent(AllCatetoryActivity.this, SearchBookActivity.class));
    }

    public void doreflash(View v) {
        getAllcatetory();
    }

    public void goMyActivity(View v) {
        if (ToolUtils.getIntence().hasLogin(this)) {
            getDDUserInfor(true, true);
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

    @Override
    public void onActivityDestroyed(Activity activity) {

    }


    @Override
    public void goChanelPage(String group, CatetoryChanleItem item) {
        Intent intent = new Intent(AllCatetoryActivity.this, ChanelActivity.class);
        intent.putExtra("group", group);
        intent.putExtra("chaneltitle", item.getName());
        intent.putExtra("chaneltype", item.getCode());
        startActivity(intent);
    }

    private List<CatetoryChanle> reOrderData() {

        List<CatetoryChanle> list = new ArrayList<>();
        list.add(data.get(4));
        list.add(data.get(6));
        list.add(data.get(5));
        list.add(data.get(2));
        list.add(data.get(1));
        list.add(data.get(0));
        CatetoryChanle sh = data.get(3);
        List<CatetoryChanleItem> itemList = sh.getCatetoryList();
        Iterator<CatetoryChanleItem> it = itemList.iterator();
        while (it.hasNext()) {
            String code = it.next().getCode();
            if ("LXGX".equals(code))
                it.remove();
            else if ("YCTJ".equals(code))
                it.remove();
            else if ("YEZJ".equals(code))
                it.remove();
            else if ("QZJY".equals(code))
                it.remove();
        }

        list.add(sh);
        list.add(data.get(7));
        list.add(data.get(8));
        return list;

    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_PAGE_UP) {//上一页
            onSildeEventLeft();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_PAGE_DOWN) {//下一页
            onSildeEventRight();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    private int pageindex = 0;

    @Override
    public void onSildeEventLeft() {
        if (pageindex <= 0) {
            return;
        }
        pageindex--;
        initPageIndex();
    }

    @Override
    public void onSildeEventRight() {
        if (pageindex >=(totalPage-1)) {
            return;
        }
        pageindex++;
        initPageIndex();
    }

    private void initPageIndex() {
        middleDatas.clear();
        int start = pageindex == 0 ? 0 : 5;
        int end = pageindex == 0 ? 5 : data.size();
        middleDatas.addAll(data.subList(start, end));
        if (adapter == null) {
            adapter = new CatetoryAdapter(AllCatetoryActivity.this, middleDatas, AllCatetoryActivity.this);
            catetoryExpGrid.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
        for (int i = 0; i < middleDatas.size(); i++) {
            catetoryExpGrid.expandGroup(i);
        }
        totalPage=data.size()/5;
        totalPage+=data.size()%5>0?1:0;
        page_text.setText(String.valueOf(pageindex + 1) + "/"+totalPage);
    }
}
