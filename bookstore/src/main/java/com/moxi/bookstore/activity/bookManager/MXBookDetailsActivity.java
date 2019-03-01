package com.moxi.bookstore.activity.bookManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moxi.bookstore.R;
import com.moxi.bookstore.modle.bookManager.NetBookDetailsModel;
import com.mx.mxbase.base.BaseActivity;
import com.mx.mxbase.constant.Constant;
import com.mx.mxbase.utils.GlideUtils;
import com.mx.mxbase.view.AlertDialog;

import butterknife.Bind;

/**
 * Created by Archer on 16/8/3.
 */
public class MXBookDetailsActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.ll_book_details_back)
    LinearLayout llBack;
    @Bind(R.id.tv_book_details_author)
    TextView tvAuthor;
    @Bind(R.id.tv_book_details_buy)
    TextView tvBuy;
    @Bind(R.id.tv_book_details_download)
    TextView tvDownload;
    @Bind(R.id.tv_book_details_name)
    TextView tvName;
    @Bind(R.id.tv_book_details_original_author)
    TextView tvOriginalAuthor;
    @Bind(R.id.tv_book_details_price)
    TextView tvPrice;
    @Bind(R.id.tv_book_details_type_name)
    TextView tvTypeName;
    @Bind(R.id.tv_book_details_desc)
    TextView tvDesc;
    @Bind(R.id.img_book_details_cover)
    ImageView imgCover;

    private NetBookDetailsModel.NetBookDetails details;


    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
            case 200:
                tvDownload.setText("点击下载");
                break;
            case 201:
                tvDownload.setText("正在下载");
                break;
            default:
                break;
        }
    }

    @Override
    protected int getMainContentViewId() {
        return R.layout.mx_activity_book_details;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        init();
    }

    /**
     * 初始化视图
     */
    private void init() {
        //设置监听事件
        llBack.setOnClickListener(this);
        //获取传递数据
        Intent preIntent = this.getIntent();
        NetBookDetailsModel netBookDetailsModel = (NetBookDetailsModel) preIntent.getSerializableExtra("net_book_details");
        try {
            details = netBookDetailsModel.getResult();
            setViewValues();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setViewValues() {
        tvAuthor.setText(details.getAuthor());
        tvName.setText(details.getName());
        tvOriginalAuthor.setText("原作者：" + details.getOriginalName());
        tvPrice.setText("价格：" + details.getPrice());
        tvTypeName.setText("分类：" + details.getBookTypeName());
//        ImageLoaderManager.getInstance().loadImageUrl(imgCover, Constant.HTTP_HOST + details.getCoverImage());
        GlideUtils.getInstance().loadImage(this,imgCover,Constant.HTTP_HOST + details.getCoverImage());

        tvDesc.setText(details.getDesc());
        //设置点击事件
        tvBuy.setOnClickListener(this);
        tvDownload.setOnClickListener(this);
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
    public void onActivityDestroyed(Activity activity) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_book_details_back:
                this.finish();
                break;
            case R.id.tv_book_details_download:
//                new LoadAsyncTask(Environment.getExternalStorageDirectory() + Constant.FILE_PATH, details.getName() + details.getSaveFile().
//                        substring(details.getSaveFile().lastIndexOf(".")), getHandler()).execute(Constant.DOWNLOAD_BOOK_BY_ID + details.getId());
                break;
            case R.id.tv_book_details_buy:
                new AlertDialog(this).builder().setCancelable(false).setTitle("手机扫描购买：" +
                        details.getName()).setImg(Constant.BOOK_PAY_QRCODE + details.getId()).show();
                break;
            default:
                break;
        }
    }
}
