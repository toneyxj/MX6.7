package com.moxi.bookstore.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.Html;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dangdang.reader.dread.util.DrmWrapUtil;
import com.dangdang.reader.moxiUtils.BrodcastUtils;
import com.moxi.bookstore.BookstoreApplication;
import com.moxi.bookstore.R;
import com.moxi.bookstore.base.BookStoreBaseActivity;
import com.moxi.bookstore.bean.AppendShoppingCart;
import com.moxi.bookstore.bean.Cart;
import com.moxi.bookstore.bean.CertificateData;
import com.moxi.bookstore.bean.Data;
import com.moxi.bookstore.bean.EbookDetailData;
import com.moxi.bookstore.bean.LoginUserData;
import com.moxi.bookstore.bean.MakeOrderData;
import com.moxi.bookstore.bean.Message.MediaDetail;
import com.moxi.bookstore.bean.Status;
import com.moxi.bookstore.db.EbookDB;
import com.moxi.bookstore.db.SacnReadFileUtils;
import com.moxi.bookstore.db.TableConfig;
import com.moxi.bookstore.db.TableOperate;
import com.moxi.bookstore.http.HttpManager;
import com.moxi.bookstore.http.HttpService;
import com.moxi.bookstore.http.deal.CancelStoreDeal;
import com.moxi.bookstore.http.deal.CartListDeal;
import com.moxi.bookstore.http.deal.Certificatedeal;
import com.moxi.bookstore.http.deal.DoStoreDeal;
import com.moxi.bookstore.http.deal.FreeObtenMedia;
import com.moxi.bookstore.http.deal.MakeOrderDeal;
import com.moxi.bookstore.http.deal.ParamsMap;
import com.moxi.bookstore.http.deal.SubEbookDetailData;
import com.moxi.bookstore.http.entity.BaseDeal;
import com.moxi.bookstore.http.listener.HttpOnNextListener;
import com.moxi.bookstore.http.subscribers.ProgressSubscriber;
import com.moxi.bookstore.modle.BookStoreFile;
import com.moxi.bookstore.modle.getEbookOrderFlowV2_Data;
import com.moxi.bookstore.request.NetUtil;
import com.moxi.bookstore.request.RequestUtils;
import com.moxi.bookstore.request.ReuestKeyValues;
import com.moxi.bookstore.request.dowload.DownloadTask;
import com.moxi.bookstore.request.dowload.DownloadTask1;
import com.moxi.bookstore.request.json.Connector;
import com.moxi.bookstore.utils.MD5;
import com.moxi.bookstore.utils.StartUtils;
import com.moxi.bookstore.utils.ToolUtils;
import com.mx.mxbase.constant.APPLog;
import com.mx.mxbase.interfaces.ClickBackListener;
import com.mx.mxbase.utils.GlideUtils;
import com.mx.mxbase.utils.RegHtml;
import com.mx.mxbase.utils.StringUtils;
import com.mx.mxbase.utils.ToastUtils;
import com.mx.mxbase.view.MxTextView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import rx.Observable;
import rx.Subscriber;

import static com.moxi.bookstore.utils.ToolUtils.getIntence;

//import com.onyx.android.sdk.device.DeviceInfo;
//ae2f0673a555cab7e70e369606b2f5f8bcc1ff07
public class EbookDetailActivity extends BookStoreBaseActivity {

    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.book_ico)
    ImageView book_ico;
    @Bind(R.id.book_title_tv)
    TextView book_title_tv;
    @Bind(R.id.author_tv)
    TextView author_tv;
    @Bind(R.id.time_tv)
    TextView time_tv;
    @Bind(R.id.publischer_tv)
    TextView publischer_tv;
    @Bind(R.id.pubTime_tv)
    TextView pubTime_tv;
    @Bind(R.id.low_price_tv)
    TextView low_price_tv;
    @Bind(R.id.org_price_tv)
    TextView org_price_tv;
    @Bind(R.id.btn1_tv)
    TextView btn1_tv; //试读
    @Bind(R.id.btn2_tv)
    TextView btn2_tv;//全本下载
    @Bind(R.id.abstract_tv)
    MxTextView abstract_tv;
    @Bind(R.id.body_ll)
    LinearLayout body_ll;
    @Bind(R.id.free_tv)
    TextView free_tv;//免费
    @Bind(R.id.price_ll)
    LinearLayout price_group;
    @Bind(R.id.freedownload_tv)
    TextView freedownload_tv;//freebook下载

    @Bind(R.id.join_vip)
    TextView join_vip;//购买vip

    @Bind(R.id.error_body)
    View errorbody;
    @Bind(R.id.unsupport_tv)
    TextView unsupport_tv;
    @Bind(R.id.btns_rl)
    RelativeLayout btns_rl;
    @Bind(R.id.like_tv)
    TextView like_tv;//收藏
    @Bind(R.id.addcarlist_tv)
    TextView addcarlist_tv;//加入购物车

    final static String REFLASH = "EbookDetail.reflash";
    public final static int NOMORE = 0;
    public final static int BOUGHT = 1;
    MediaDetail data;
    String booktitle, pubkey, deviceNo, certificate;
    String token = "";
    String productArray;
    int isfull = 0, flag;
    //    Double lowestprice, saleprice;
    EbookDB dbook;
    List<EbookDB> querylist0, querylist1;
    long saleId;
    byte[] bookkey;
    int DownFlag = 0;//0为下载试读 1为下载全本

    @Override
    protected int getMainContentViewId() {
        return R.layout.activity_ebook_detail;
    }


    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            booktitle = savedInstanceState.getString("booktitle");
            saleId = savedInstanceState.getLong("saleId", 0L);
//            lowestprice = savedInstanceState.getDouble("lowestprice", -1d);
//            saleprice = savedInstanceState.getDouble("saleprice", -1d);
            flag = savedInstanceState.getInt("FLAG", 0);
        } else {
            booktitle = getIntent().getStringExtra("booktitle");
            saleId = getIntent().getLongExtra("saleId", 0L);
//            lowestprice = getIntent().getDoubleExtra("lowestprice", -1d);
//            saleprice = getIntent().getDoubleExtra("saleprice", -1d);
            flag = getIntent().getIntExtra("FLAG", 0);
        }
        initView();
        pubkey = DrmWrapUtil.getPublicKey();
        //ConfigManager cm=new ConfigManager(this);
        // deviceNo = cm.getDeviceId();
        deviceNo = BookstoreApplication.getDeviceNO();
        findInDB();
//        getToken();
        APPLog.e("imei:" + getIntence().getIMEINo(this));
        APPLog.e("sn:" + getIntence().getDeviceNo(this));
    }

    private void initView() {
        join_vip.setOnClickListener(clickListener);
        title.setText(booktitle);
        errorbody.setVisibility(View.GONE);
        initReceiver();

    }

    LoginUserData userdata;

    private void getToken() {
        /**初始化获取token*/
        token = getTokenValue();
        if (StringUtils.isNull(token)) token = "";
    }

    @Override
    public void onActivityStarted(Activity activity) {
    }

    private void updataInf() {
        APPLog.e("data_detail", data.toString());
        body_ll.setVisibility(View.VISIBLE);
        if (BOUGHT == flag) {//已购买
            price_group.setVisibility(View.INVISIBLE);
        }
        if (0 == data.getIsSupportDevice()) { //排除不支持
            unsupport_tv.setVisibility(View.VISIBLE);
            btns_rl.setVisibility(View.GONE);
            freedownload_tv.setVisibility(View.GONE);
            //like_tv.setClickable(false);
            //addcarlist_tv.setClickable(false);
        } else if (1 == data.getFreeBook()) {//免费书
            price_group.setVisibility(View.GONE);
            btns_rl.setVisibility(View.GONE);
            freedownload_tv.setVisibility(View.VISIBLE);
            free_tv.setVisibility(View.VISIBLE);
            addcarlist_tv.setClickable(false);
            freedownload_tv.setText(querylist1.size() == 0 ? "下载" : "阅读");
        }
        ToolUtils mIntence = getIntence();
        body_ll.setVisibility(View.VISIBLE);
        GlideUtils.getInstance().loadGreyImage(this, book_ico, data.getCoverPic());
        book_title_tv.setText(data.getTitle());
        title.setText(data.getTitle());
        author_tv.setText("作者: " + data.getAuthorPenname());
        time_tv.setText("更新时间: " + mIntence.dateToStr1(data.getPublishDate()));
        String publisher = data.getPublisher();
        if (null == publisher || TextUtils.isEmpty(publisher))
            publisher = "不详";
        publischer_tv.setText("出版社: " + publisher);
        pubTime_tv.setText("出版年月: " + mIntence.dateToStr1(data.getPublishDate()));
        org_price_tv.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);

        low_price_tv.setText("电子书:￥" + data.getSalePrice());
        org_price_tv.setText("纸书标价:￥" + mIntence.formatPrice(data.getOriginalPrice()));

        if (data.getOriginalPrice() == null || data.getOriginalPrice() == 0) {
            org_price_tv.setVisibility(View.INVISIBLE);
        }
        getHandler().post(new Runnable() {
            @Override
            public void run() {
                String abstr = RegHtml.delHTMLTag(data.getDescs(), "style");
                abstr = RegHtml.delHTMLTag(abstr, "img");

                abstr = Html.fromHtml(abstr).toString();
                //赋值
                abstract_tv.setSourceText(abstr);
            }
        });
        if (getFullBook()) {
            if (querylist1.size() > 0)
                btn2_tv.setText("阅读");
            else
                btn2_tv.setText("下载");
            addcarlist_tv.setText(" 已购买");
            addcarlist_tv.setClickable(false);
            btn1_tv.setVisibility(View.GONE);
        }
        //租阅书籍，无租阅权限
        if (data.isChannelHall() && !data.getIsChannelMonth().equals(1)) {
            join_vip.setVisibility(View.VISIBLE);
        } else {
            join_vip.setVisibility(View.GONE);
        }
        if (1 == data.getMediaType() || 4 == data.getMediaType()) {
            addcarlist_tv.setClickable(false);
        } else
            addcarlist_tv.setClickable(true);

        if (1 == data.getIsStore()) {//已收藏
            like_tv.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.fivor_ico), null, null, null);
            like_tv.setText(" 已收藏");
        }
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.join_vip:
                    if (!getIntence().hasLogin(EbookDetailActivity.this)) {
                    } else {//跳转vip购买界面
                        if (ToolUtils.getIntence().showBindingDDUser(EbookDetailActivity.this)) {
                            startActivityForResult(new Intent(EbookDetailActivity.this, GetVipActivity.class), 11);
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private boolean getFullBook() {
        //已购买:1 == data.getIsWholeAuthority()
        return 1 == data.getIsWholeAuthority() || (data.getIsChannelMonth().equals(1) && data.isChannelHall());
    }

    @Override
    public void onActivityResumed(Activity activity) {
        getToken();
        getEbookDetail();
    }

    @Override
    public void onActivityPaused(Activity activity) {
        body_ll.setVisibility(View.GONE);
    }

    @Override
    public void onActivityStopped(Activity activity) {
        setResult(MyActivity.EBDETAIL_RES);
    }

    @Override
    public void onActivitySaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outState.putString("booktitle", booktitle);
        outState.putLong("saleId", saleId);
//        outState.putDouble("lowestprice", lowestprice);
//        outState.putDouble("saleprice", saleprice);
        outState.putInt("FLAG", flag);
    }

    @Override
    public void onActivityRestoreInstanceState(Bundle savedInstanceState) {

    }

    private void getEbookDetail() {
        SubEbookDetailData detailDeal = new SubEbookDetailData(new ProgressSubscriber(
                ebookDetailListener, this), saleId, token);
        HttpManager manager = HttpManager.getInstance();
        manager.doHttpDeal(detailDeal);
        showDialog("加载中...");
    }

    private HttpOnNextListener ebookDetailListener = new HttpOnNextListener<EbookDetailData>() {
        @Override
        public void onNext(EbookDetailData subject) {
            hideDialog();
            errorbody.setVisibility(View.GONE);
            body_ll.setVisibility(View.VISIBLE);
            data = subject.getMediaSale().getMediaList().get(0);
            APPLog.e("detail:" + (null == data));
            if (null != data) {
                APPLog.e("detail:", data.toString());
                updataInf();
                APPLog.e("mediaId:" + data.getMediaId());
                APPLog.e("saleId:" + data.getSaleId());
                productArray = "[{\"productId\":\"" + data.getMediaId() + "\",\"saleId\":\"" + saleId + "\",\"cId\":\"\"}]";
            }

        }

        @Override
        public void onError() {
            hideDialog();
            body_ll.setVisibility(View.GONE);
            errorbody.setVisibility(View.VISIBLE);
            ToastUtil("未找到该书籍");
        }
    };

    public void goBack(View v) {
        finish();
    }

    public void allDownload(View v) {
        DownFlag = 1;
        if (0 != querylist1.size()) {
            // open ebook
            if (StartUtils.OpenDDRead(EbookDetailActivity.this, querylist1.get(0).getFilePath()))
                return;
            StartUtils.OpenDDRead(EbookDetailActivity.this, querylist1.get(0));
            return;
        }
        btn2_tv.setClickable(false);
        if (getFullBook()) {
            APPLog.e("token:" + token);
            isfull = 1;
            btn2_tv.setClickable(false);
            btn2_tv.setText("下载...");
            initDBook(data, isfull);
            getCertData(isfull);
        } else {
            if (getIntence().hasLogin(this)) {
                getToken();
                makeOrder();
            } else
                btn2_tv.setClickable(true);
        }
    }

    public void doreflash(View v) {
        getEbookDetail();
    }

    public void tryDownload(View v) {
        try {
            if (null != querylist0 && querylist0.size() > 0) {
                if (StartUtils.OpenDDRead(EbookDetailActivity.this, querylist0.get(0).getFilePath()))
                    return;
                StartUtils.OpenDDRead(EbookDetailActivity.this, querylist0.get(0));
                return;
            }
            isfull = 0;
            btn1_tv.setClickable(false);
            btn1_tv.setText("下载...");
            initDBook(data, isfull);
            getCertData(isfull);
        } catch (Exception e) {

        }

    }

    private void faileDownlaod(String filePath) {
        if (isfinish) return;
//        try {
//            File file = new File(filePath);
//            if (file.exists()) {
//                file.delete();
//            }
//        } catch (Exception e) {
//        }
        hideDialog();
        dialogShowOrHide(false, "下载中...");
        btn2_tv.setClickable(true);
        initShowTxt();
    }

    private String totalSizeValue = null;
    DownloadTask.DownloadListener downloadFileListener = new DownloadTask.DownloadListener() {
        @Override
        public void onDownloadFail(final String filePath, final long totalSize) {
            getHandler().post(new Runnable() {
                @Override
                public void run() {
                    if (isfinish) return;
                    //删除下载书籍
                    faileDownlaod(filePath);
                    String hitn = ToolUtils.getIntence().getDownloadHitn(EbookDetailActivity.this, totalSize);
                    showToast(hitn);//错误提示
                }});
        }

        @Override
        public void onDownloadSucces(String filePath) {
            getHandler().post(new Runnable() {
                @Override
                public void run() {
                    if (isfinish) return;
                    dialogShowOrHide(false, "");
                    hideDialog();
                    btn2_tv.setClickable(true);
                    dcomplite = true;
                    insertDB();
                }
            });
        }

        @Override
        public void onDownloadStop(final String filePath) {
            getHandler().post(new Runnable() {
                @Override
                public void run() {
                    if (isfinish) return;
                    faileDownlaod(filePath);
                }});
        }

        @Override
        public void onDownloadProgress(String filePath, final long currentSize, final long totalSize) {
            getHandler().post(new Runnable() {
                @Override
                public void run() {
                    if (isfinish || downloadTask.isEndDownload()) return;
                    if (totalSizeValue == null) {
                        totalSizeValue = Formatter.formatFileSize(EbookDetailActivity.this, totalSize);
                    }
                    int progress = (int) ((currentSize * 100) / totalSize);
                    dialogText("文件大小：" + totalSizeValue + "\t进度：" + progress + "%");
                }
            });

        }
    };

    public void freeDownload(View v) {
        try {
            if (0 != querylist1.size() && querylist1.size() > 0) {
                // open ebook
                if (StartUtils.OpenDDRead(EbookDetailActivity.this, querylist1.get(0).getFilePath()))
                    return;
                StartUtils.OpenDDRead(EbookDetailActivity.this, querylist1.get(0));
                return;
            }
            APPLog.e("token:" + token);
            if (getIntence().hasLogin(this)) {
                getToken();
                if (0 == data.getIsWholeAuthority()) {//没有全部权限
                    getFreeMedia();
                    return;
                }
                isfull = 2;
                freedownload_tv.setClickable(false);
                freedownload_tv.setText("下载...");
                initDBook(data, 1);
                getCertData(1);
                // ToastUtil("免费下载");
            }
        } catch (Exception e) {
        }

    }

    public void flashPage(View v) {
        getEbookDetail();
    }

    boolean dcomplite = false;

    private HttpOnNextListener certificateListener = new HttpOnNextListener<CertificateData>() {
        @Override
        public void onNext(CertificateData certdata) {
//            hideDialog();
            certificate = certdata.getCertificate();//10003
            APPLog.e("getcertificate:" + certificate);
            bookkey = DrmWrapUtil.getPartBookCertKey(certificate);
//            insertDB();
            SDDownload(isFullCertData);
        }

        @Override
        public void onError() {
            hideDialog();
            if (0 == isfull) {
                btn1_tv.setText("试读");
                btn1_tv.setClickable(true);
            } else {
                btn2_tv.setClickable(true);
                btn2_tv.setText("阅读");
            }
            APPLog.e("certificate fail");
        }
    };

    private HttpOnNextListener freeObtenListener = new HttpOnNextListener<Data>() {
        @Override
        public void onNext(Data time) {
            hideDialog();
            isfull = 2;
            freedownload_tv.setClickable(false);
            freedownload_tv.setText("下载...");
            initDBook(data, 1);
            getCertData(1);
            ToastUtil("免费下载");
        }

        @Override
        public void onError() {
            hideDialog();
            APPLog.e("freedobten fail");
        }
    };
    private HttpOnNextListener addFavorListener = new HttpOnNextListener() {
        @Override
        public void onNext(Object o) {
            like_tv.setClickable(true);
            hideDialog();
            if (1 == data.getIsStore()) {
                ToastUtil("取消成功");
                like_tv.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.favor_nomal), null, null, null);
                like_tv.setText("收藏");
            } else {
                ToastUtil("收藏成功");
                like_tv.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.fivor_ico), null, null, null);
                like_tv.setText(" 已收藏");
            }
        }

        @Override
        public void onError() {
            like_tv.setClickable(true);
            hideDialog();
            APPLog.e("addfavor fail");
        }
    };

    public void addCarlist(View v) {
        if (1 == data.getIsWholeAuthority()) {
            ToastUtil("已购买，无法添加");
            return;
        }
        if (1 == data.getFreeBook()) {
            ToastUtil("免费书，无法添加");
            return;
        }
        if (getIntence().hasLogin(this)) {
            getToken();
            getCartId();
        }
        //addCarlist();
    }

    public void addFavor(View v) {
        if (0 == data.getIsSupportDevice()) {
            ToastUtil("设备不支持，无法收藏");
            return;
        }
        like_tv.setClickable(false);
        if (getIntence().hasLogin(this)) {
            getToken();
            HttpManager manager = HttpManager.getInstance();

            if (1 == data.getIsStore()) {
                CancelStoreDeal cancelStoreDeal = new CancelStoreDeal(new ProgressSubscriber(addFavorListener, this), saleId + "", token);
                manager.doHttpDeal(cancelStoreDeal);
                showDialog("加载中... ");
            } else {
                DoStoreDeal storeDeal = new DoStoreDeal(new ProgressSubscriber(addFavorListener, EbookDetailActivity.this),
                        saleId + "", token);

                manager.doHttpDeal(storeDeal);
                showDialog("加载中...");
            }
        }
    }

    public void cartList(View v) {
        if (ToolUtils.getIntence().hasLogin(this)) {
            getDDUserInfor(true, true);
        }
    }

    @Override
    public void Success(String result, String code) {
        super.Success(result, code);
        if (code.equals(Connector.getInstance().getMonthlyChannelListNotify)) {
            //获取到信息进入界面
            Intent cartIt = new Intent();
            cartIt.setClass(this, CartActivity.class);
            startActivity(cartIt);
        }
    }

    private void addCarlist() {
        if (0 == data.getIsSupportDevice()) {
            ToastUtil("设备不支持，无法添加购物车");

        }
        final HashMap<String, Object> params = new ParamsMap(this);
        params.put("action", "appendShoppingCart");
        params.put("productArray", "[{\"productId\":\"" + data.getMediaId() + "\",\"saleId\":\"" + saleId + "\",\"cId\":\"\"}]");
        params.put("cartId", cartId);
        params.put("token", token);
        HttpManager manager = HttpManager.getInstance();
        manager.doHttpDeal(new BaseDeal() {
            @Override
            public Observable getObservable(HttpService methods) {
                return methods.appendShoppingCart(params);
            }

            @Override
            public Subscriber getSubscirber() {
                return new ProgressSubscriber(
                        new HttpOnNextListener<AppendShoppingCart>() {
                            @Override
                            public void onNext(AppendShoppingCart o) {
                                hideDialog();
                                ToastUtil("加入购物车完成");
                                addcarlist_tv.setClickable(false);
                            }

                            @Override
                            public void onError() {
                                hideDialog();
                            }
                        }, EbookDetailActivity.this);
            }
        });
        showDialog("加载中...");
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        unregisterReceiver(receiver);
//        if (null != downLoadpd)
//            downLoadpd.onCanceldownLoad();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        DeviceInfo.currentDevice.showSystemStatusBar(this);
    }

    private void initDBook(MediaDetail data, int f) {

        dbook = new EbookDB();
        dbook.saleId = data.getSaleId();
        dbook.name = data.getTitle();
        dbook.author = data.getAuthorPenname();
        dbook.publisher = data.getPublisher();
        dbook.iconUrl = data.getCoverPic();
        dbook.type = data.getCategory();

        dbook.publishtime = data.getPublishDate();
        dbook.lastreadtime = 0L;
        dbook.chartcount = data.getWordCnt();
        dbook.flag = f;
        dbook.bookdesc = data.getDescs();
        if (null == data.getChapterCnt())
            data.setChapterCnt(3);
        dbook.pagecount = data.getChapterCnt();
        dbook.progress = "0";

    }

    //数据库查找
    private void findInDB() {

        querylist0 = TableOperate.getInstance().query(TableConfig.TABLE_NAME, saleId, 0);

        querylist1 = TableOperate.getInstance().query(TableConfig.TABLE_NAME, saleId, 1);
        judjeBookSave(querylist0);
        judjeBookSave(querylist1);
    }

    private void judjeBookSave(List<EbookDB> querylist) {
        if (querylist.size() > 0) {
            File file = new File(querylist.get(0).filePath);
            if (!file.exists() || !file.canRead() || file.length() == 0) {
                StringUtils.deleteFile(file);
                TableOperate.getInstance().delete(TableConfig.TABLE_NAME, TableConfig.E_FILEPATHMD5, MD5.stringToMD5(file.getAbsolutePath()));
                querylist.clear();
            }
        }
    }

    //试读下载
    private DownloadTask1 downloadTask;

    private void SDDownload(int isFull) {
        if (downloadTask != null && downloadTask.isAlive()) {//线程正在运行
            return;
        } else if (!NetUtil.checkNetworkInfo(this)) {
            ToastUtils.getInstance().showToastShort("请检查网络连接");
        } else {//构建新的下载
            dialogShowOrHide(true, "下载中...", new ClickBackListener() {
                @Override
                public void onHitnBackground() {
                    if (downloadTask != null && downloadTask.isAlive()) {//线程正在运行
//                        downloadTask.setListener(null);
                        downloadTask.setEndDownload(true);
//                        hideDialog();
                        return;
                    }
                }
            });
            List<ReuestKeyValues> valuePairs = new ArrayList<>();
            valuePairs.add(new ReuestKeyValues("action", "downloadMediaWhole"));
            valuePairs.add(new ReuestKeyValues("mediaId", String.valueOf(saleId)));
            valuePairs.add(new ReuestKeyValues("isFull", String.valueOf(isFull)));
            valuePairs.add(new ReuestKeyValues("deviceType", "Android"));
            valuePairs.add(new ReuestKeyValues("platformSource", "DDDS-P"));
            valuePairs.add(getthisToken());
            String RUrl = RequestUtils.getGetUrl(valuePairs, Connector.getInstance().url);
            APPLog.e("download-RUrl", RUrl);
            downloadTask = new DownloadTask1(RUrl, data.getTitle(), downloadFileListener);
            downloadTask.run();
        }
    }

    private int isFullCertData = -1;

    //获取certificate
    private void getCertData(int isFull) {
        if (downloadTask != null && downloadTask.isAlive()) {//线程正在运行
            return;
        } else if (!NetUtil.checkNetworkInfo(this)) {
            ToastUtils.getInstance().showToastShort("请检查网络连接");
            return;
        } else {//构建新的下载
            dialogShowOrHide(true, "下载中...", new ClickBackListener() {
                @Override
                public void onHitnBackground() {
                    if (downloadTask != null && downloadTask.isAlive()) {//线程正在运行
                        downloadTask.setEndDownload(true);
                        return;
                    }
                }
            });
        }
        if (isFull != isFullCertData || StringUtils.isNull(certificate)) {
            APPLog.e("pubkey=" + pubkey + ":" + data.getIsFull());
            Subscriber subscriber = new ProgressSubscriber(certificateListener, this);
            Certificatedeal certificatedeal = new Certificatedeal(subscriber, pubkey, deviceNo, saleId, isFull, token);
            HttpManager manager = HttpManager.getInstance();
            manager.doHttpDeal(certificatedeal);
            isFullCertData = isFull;
        } else {
            SDDownload(isfull);
        }
//        showDialog("下载中...");
    }

    private void getFreeMedia() {
        FreeObtenMedia obtenMediadeal = new FreeObtenMedia(new ProgressSubscriber(freeObtenListener, this), token, saleId);
        HttpManager manager = HttpManager.getInstance();
        manager.doHttpDeal(obtenMediadeal);
        showDialog("下载中...");
    }

    /**
     * 更新按钮提示文字
     */
    private String initShowTxt() {
        String filename = String.copyValueOf(booktitle.toCharArray());
        filename = ToolUtils.getIntence().downloadPathSpil(filename);
        File dloadFile = new File(TableConfig.E_DOWNLOAD_DIR, filename + ".epub");
        boolean isDownloadSucess = dloadFile.exists() && dcomplite;
        if (0 == isfull) {
            btn1_tv.setClickable(true);
            btn1_tv.setText(isDownloadSucess ? "试读" : "试读");
        } else if (2 == isfull) {
            freedownload_tv.setClickable(true);
            freedownload_tv.setText(isDownloadSucess ? "阅读" : "下载");
        } else {
            btn2_tv.setClickable(true);
            btn2_tv.setText((getFullBook() && isDownloadSucess) ? "阅读" : "下载");
        }
        return filename;
    }

    //更新数据库
    private void insertDB() {
        APPLog.e("bookkey:" + bookkey == null);
        if (null == bookkey || null == booktitle)
            return;
        String filename = initShowTxt();

        dbook.filePath = TableConfig.E_DOWNLOAD_DIR + File.separator + filename + ".epub";
        if (!(new File(dbook.filePath)).exists()) return;

        dbook.downloadtime = System.currentTimeMillis();
        dbook.key = certificate;
        APPLog.e("bookflag :" + dbook.flag);
        TableOperate.getInstance().insert(TableConfig.TABLE_NAME, dbook);

        BookStoreFile file = new BookStoreFile();
        file.filePath = dbook.filePath;
        file.photoPath = dbook.iconUrl;
        SacnReadFileUtils.getInstance(this).saveMode(file);

        sendMsg();
        hideDialog();
        APPLog.e("save sherlfbook to db");

        findInDB();//更新完再查询一次
    }

    private void getCartId() {
        HashMap<String, Object> pramers = new ParamsMap(this);
        //pramers.put("action", "listShoppingCart");
        pramers.put("deviceType", "Android");
        pramers.put("token", token);
        CartListDeal IdDeal = new CartListDeal(new ProgressSubscriber(CartIdListener, this), pramers);
        HttpManager manager = HttpManager.getInstance();
        manager.doHttpDeal(IdDeal);
        showDialog("加载中...");
    }

    String cartId;
    private HttpOnNextListener CartIdListener = new HttpOnNextListener<Cart>() {
        @Override
        public void onNext(Cart cart) {
            if (isfinish) return;
            hideDialog();
            cartId = cart.getCartId();
            APPLog.e("详情cartId:" + cartId);
            addCarlist();
        }

        @Override
        public void onError() {
            if (isfinish) return;
            hideDialog();
            APPLog.e("getCartId failed");
        }
    };

    private void makeOrder() {
        if (ToolUtils.getIntence().showBindingDDUser(this)) {
            MakeOrderDeal makeOrderDeal = new MakeOrderDeal(new ProgressSubscriber(orderListener, this),
                    data.getSaleId() + "", token, deviceNo, false);
            HttpManager manager = HttpManager.getInstance();
            manager.doHttpDeal(makeOrderDeal);
            showDialog("创建订单...");
        }
    }

    String orderId, total, key, payable;

    private HttpOnNextListener orderListener = new HttpOnNextListener<MakeOrderData>() {
        @Override
        public void onNext(MakeOrderData data) {
            if (isfinish) return;
            hideDialog();
            btn2_tv.setClickable(true);
            Status status = data.getResult().getStatus();
            if (null != status) {
                ToastUtil(status.getMessage());
            }
            getEbookOrderFlowV2_Data result = data.getResult().getEbookOrderFlowV2.data;
//            OrderResult result = data.getResult().getSubmitOrder().getData().getResult();
            if (null != result) {
//                orderId = result.getOrder_id();
                total = String.valueOf(result.totalPrice / 100.0f);
                payable = String.valueOf(result.payable / 100.0f);
                key = result.key;
//                total = result.getTotal();
//                payable = result.getPayable();
//                key = data.getResult().getSubmitOrder().getData().getKey();
                APPLog.e("makeOrder success: " + orderId);
                APPLog.e("payable:" + payable);
                APPLog.e("deviceNo:" + deviceNo);
                APPLog.e("orderkey:" + key);
                //checkBalence();
                if (visibale)
                    goPay();
            } else {
                showToast("购买失败,请重试");
                hideDialog();
                APPLog.e("makeorder fail");
            }

        }

        @Override
        public void onError() {
            if (isfinish) return;
            APPLog.e("onerror makeorder fail");
            hideDialog();
            btn2_tv.setClickable(true);
        }
    };


    private void initReceiver() {
        IntentFilter filter = new IntentFilter(REFLASH);
        registerReceiver(receiver, filter);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isfinish) return;
            APPLog.e("reflash data");
            getEbookDetail();
        }
    };

    private void goPay() {
        if (null != data) {
//            Intent intent = new Intent(this, PayActivity.class);
//            // Intent intent =new Intent(this,PayActivityWithAliy.class);
//            intent.putExtra("orderId", orderId);
//            intent.putExtra("total", total);
//            intent.putExtra("payable", payable);
//            intent.putExtra("key", key);
//            intent.putExtra("token", token);
//            intent.putExtra("productIds", data.getSaleId() + "");
//            intent.putExtra("bookName", data.getTitle());
//            intent.putExtra("productArray", productArray);
//            intent.putExtra("deviceNo", deviceNo);
//            startActivity(intent);
            PayActivity.startPayActivity(this, data.getSaleId() + "", payable, total, key, token, productArray, deviceNo, data.getTitle());
        }
    }

    private void sendMsg() {
        Intent intent = new Intent(BrodcastUtils.readBrodcast);
//        intent.putExtra(BrodcastUtils.PROGRESS,data.progress);
        intent.putExtra(BrodcastUtils.PROGRESS, String.valueOf(dbook.getProgress()));
        intent.putExtra(BrodcastUtils.ID, dbook.getSaleId() + "");
        sendBroadcast(intent);
    }

    @Override
    protected void onDestroy() {
//        downloadListener.removeCallbacksAndMessages(null);
        super.onDestroy();
        if (downloadTask != null && downloadTask.isAlive()) {
            downloadTask.setEndDownload(true);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        if (requestCode == 11) {//返回刷新界面
            getEbookDetail();
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_PAGE_UP) {
            abstract_tv.flipOver(false);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_PAGE_DOWN) {
            abstract_tv.flipOver(true);
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }
}
