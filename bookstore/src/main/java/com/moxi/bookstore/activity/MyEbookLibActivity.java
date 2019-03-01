package com.moxi.bookstore.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.dangdang.reader.dread.StartRead;
import com.dangdang.reader.dread.util.DrmWrapUtil;
import com.dangdang.zframework.utils.ConfigManager;
import com.moxi.bookstore.R;
import com.moxi.bookstore.adapter.MyLibGvAdapter;
import com.moxi.bookstore.adapter.SearchMediaGvAdapter;
import com.moxi.bookstore.base.BookStoreBaseActivity;
import com.moxi.bookstore.bean.CertificateData;
import com.moxi.bookstore.bean.ChanelData;
import com.moxi.bookstore.bean.Message.RecommendData;
import com.moxi.bookstore.bean.Sale;
import com.moxi.bookstore.db.EbookDB;
import com.moxi.bookstore.db.TableConfig;
import com.moxi.bookstore.db.TableOperate;
import com.moxi.bookstore.http.HttpManager;
import com.moxi.bookstore.http.deal.Certificatedeal;
import com.moxi.bookstore.http.deal.SubChaneldeal;
import com.moxi.bookstore.http.listener.HttpOnNextListener;
import com.moxi.bookstore.http.subscribers.ProgressSubscriber;
import com.moxi.bookstore.utils.MXUamManager;
import com.mx.mxbase.constant.APPLog;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import rx.Subscriber;

/**
 * Created by Administrator on 2016/9/25.
 */
public class MyEbookLibActivity extends BookStoreBaseActivity implements AdapterView.OnItemClickListener {
    @Bind(R.id.books_gv)
    GridView books_gv;//本地books
    @Bind(R.id.net_books_gv)
    GridView net_books_gv;//网络推荐books

    List<EbookDB> eblist;
    List<Sale> recommlist;
    SearchMediaGvAdapter adapter;
    MyLibGvAdapter myLibGvAdapter;
    private int  GridviewItemHeight=0;
    @Override
    protected int getMainContentViewId() {
        return R.layout.activity_ebok_lib;
    }
    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

        eblist=new ArrayList<>();
        initListener();
        String str= MXUamManager.queryDDToken(this);
        APPLog.e("token:"+str);
    }

    private void initListener() {
        books_gv.setOnItemClickListener(this);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (0<eblist.size()){
            GridviewItemHeight=books_gv.getHeight()/2;
            myLibGvAdapter=new MyLibGvAdapter(this,eblist,GridviewItemHeight);
            books_gv.setAdapter(myLibGvAdapter);
        }
    }

    @Override
    public void onActivityStarted(Activity activity) {
        if (null==recommlist) {
            // getRecommenddata();
        }
    }

    @Override
    public void onActivityResumed(Activity activity) {
        APPLog.e("ebookcount:"+eblist.size());
        if (null==myLibGvAdapter)
            return;

        if (0<eblist.size()) {
            eblist.clear();
            List<EbookDB> list = TableOperate.getInstance().queryAll(TableConfig.TABLE_NAME);
            for (EbookDB book:list) {
                eblist.add(book);
            }
            myLibGvAdapter.setData(eblist);
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



    public void goBack(View v){finish();}
    public void searchBook(View v){startActivity(new Intent(MyEbookLibActivity.this,SearchBookActivity.class));}
    public void goMore(View v){
        startActivity(new Intent(this,AllCatetoryActivity.class));
    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        EbookDB book=eblist.get(position);
        openEbook(book);

    }
    private void openEbook(EbookDB dbook){
        APPLog.e("openEbool:"+dbook.name);
        StartRead.ReadParams readParams=new StartRead.ReadParams();
        readParams.setBookAuthor(dbook.author);
        readParams.setBookName(dbook.name);
        readParams.setBookId(dbook.saleId+"");
        readParams.setBookCertKey(DrmWrapUtil.getPartBookCertKey(dbook.key));
        readParams.setBookCover(dbook.getIconUrl());
        readParams.setBookFile(dbook.filePath);
        readParams.setBookDir(dbook.filePath);
        readParams.setBookDesc(dbook.bookdesc);
        readParams.setBookType(2);
        readParams.setIsBought(dbook.flag==0);
        StartRead read=new StartRead();
        read.startRead(this,readParams);
    }
}
