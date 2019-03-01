package com.moxi.bookstore.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.moxi.bookstore.R;
import com.moxi.bookstore.base.BookStoreBaseActivity;
import com.moxi.bookstore.bean.AliyPayData;
import com.moxi.bookstore.bean.DeleteShoppingCart;
import com.moxi.bookstore.bean.UserInfoData;
import com.moxi.bookstore.bean.VirtualPayMentData;
import com.moxi.bookstore.dialog.BalenceCheckDialog;
import com.moxi.bookstore.dialog.CompletePayDialog;
import com.moxi.bookstore.dialog.PayMethodsDialog;
import com.moxi.bookstore.http.HttpManager;
import com.moxi.bookstore.http.HttpService;
import com.moxi.bookstore.http.deal.AliyPayDeal;
import com.moxi.bookstore.http.deal.OrderFlowDeal;
import com.moxi.bookstore.http.deal.ParamsMap;
import com.moxi.bookstore.http.deal.UserInfoDeal;
import com.moxi.bookstore.http.deal.VirtualPayDeal;
import com.moxi.bookstore.http.entity.BaseDeal;
import com.moxi.bookstore.http.listener.HttpOnNextListener;
import com.moxi.bookstore.http.subscribers.ProgressSubscriber;
import com.moxi.bookstore.interfacess.BalenceCheckListener;
import com.moxi.bookstore.interfacess.MyClick;
import com.mx.mxbase.utils.GlideUtils;
import com.mx.mxbase.constant.APPLog;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;

import butterknife.Bind;
import rx.Observable;
import rx.Subscriber;

/**
 * 支付页面，二维码5分钟后更新一次
 */
public class PayActivityWithAliy extends BookStoreBaseActivity implements MyClick,BalenceCheckListener {

    @Bind(R.id.QR_code_wv)
    WebView wv;
    @Bind(R.id.bookName_tv)
    TextView title;
    @Bind(R.id.orderid_tv)
    TextView orderid_tv;
    @Bind(R.id.price_tv)
    TextView price;
    @Bind(R.id.prefprice_tv)
    TextView prefprice;
    @Bind(R.id.QR_code_iv)
    ImageView QR_iv;
    @Bind(R.id.body_rl)
    RelativeLayout body;
    @Bind(R.id.timer_tv)
    TextView timer;
    @Bind(R.id.error_body)
    View error_body;

    int totaltime=300,flag;

    String productIds,Url,permentId,orderId,payable,total,key,token,productArray,deviceNo;
    double preferPrice=0f;
    String bookName;
    PayMethodsDialog payMethodsDialog;
    CompletePayDialog completePayDialog;
    @Override
    protected int getMainContentViewId() {
        return R.layout.activity_pay;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        orderId=getIntent().getStringExtra("orderId");
        total=getIntent().getStringExtra("total");
        token=getIntent().getStringExtra("token");
        payable=getIntent().getStringExtra("payable");
        key=getIntent().getStringExtra("key");
        productIds=getIntent().getStringExtra("productIds");
        bookName=getIntent().getStringExtra("bookName");
        productArray=getIntent().getStringExtra("productArray");
        deviceNo= getIntent().getStringExtra("deviceNo");
        cartId=getIntent().getStringExtra("CartId");
        flag=getIntent().getIntExtra("FLAG",0);
        double money=Double.valueOf(payable);
        double ctotal=Double.valueOf(total);
        APPLog.e(money);
        APPLog.e(ctotal);
        if (money<ctotal){
            preferPrice=ctotal-money;
            total=String.valueOf(money);
            APPLog.e(preferPrice);
        }
        initVew();
        initDialog();

    }


    private void initVew() {

        WebSettings settings=wv.getSettings();
        settings.setJavaScriptEnabled(true);
        wv.addJavascriptInterface(new GetQrcodeFormWeb(), "local_obj");
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        wv.setWebViewClient(new MyWebViewClient());
    }

    @Override
    public void onActivityStarted(Activity activity) {
        //getAcountData();
        if (flag==ChargeActivity.CHARGE)
            doPay();
        else
            getAcountData();

    }

    Long subbalance=-1L,mainbalance;
    private void getAcountData() {
        UserInfoDeal deal=new UserInfoDeal(new ProgressSubscriber(acountListener,this),token);
        HttpManager manger=HttpManager.getInstance();
        manger.doHttpDeal(deal);
        showDialog("获取铃铛...");

    }
    HttpOnNextListener acountListener=new HttpOnNextListener<UserInfoData>() {
        @Override
        public void onNext(UserInfoData o) {
            hideDialog();
            subbalance=o.getUserInfo().getSubBalance();
            mainbalance=o.getUserInfo().getMainBalance();
            APPLog.e("getacount success:"+subbalance);
            checkBalence();
        }

        @Override
        public void onError() {
            hideDialog();
            APPLog.e("getAcountData fail");
           // checkBalence();
            showToast("加载失败，请重试");
        }
    };

    /**
     * 判断当前用户铃铛数是否足够
     */
    BalenceCheckDialog bd;
    long intd;
    private void checkBalence(){


        double d=Double.valueOf(payable);
         intd= (long) (d*100);
        long totalbalence=subbalance+mainbalance;
        APPLog.e(String.valueOf(totalbalence)+":"+String.valueOf(intd));

        if (null==bd) {
            bd = BalenceCheckDialog.creat(PayActivityWithAliy.this,false);
            bd.setListener(this);
        }
        if (visibale&&!bd.isShowing()) {
            hideDialog();
            bd.showBalence(mainbalance, subbalance, String.valueOf(intd));
        }
        bd.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                APPLog.e("dialog listener:"+keyCode);

                if (keyCode==KeyEvent.KEYCODE_BACK){
                    PayActivityWithAliy.this.finish();
                }
                return true;
            }
        });
        /*if (totalbalence>=intd){
            if (null==bd) {
                bd = BalenceCheckDialog.creat(this);
                bd.setListener(this);
            }
            if (visibale&&!bd.isShowing())
                bd.showBalence(mainbalance,subbalance,String.valueOf(intd));

        }else{
            APPLog.e("铃铛太少");
            doPay();
        }*/

    }

    /**
     * 铃铛支付或扫描支付对话框选择
     */
    @Override
    public void payWithBalence() {
        // TODO: 2016/11/17 铃铛购买处理
        //balencePay();
        bd.hideBalence();
        goBalencePay();
    }

    //跳转到铃铛支付页面
    private void goBalencePay() {
        Intent bit=new Intent(this,BalencePayActivity.class);
        bit.putExtra("productArray",productArray);
        bit.putExtra("key",key);
        bit.putExtra("token",token);
        bit.putExtra("deviceNo",deviceNo);
        bit.putExtra("title",bookName);
        bit.putExtra("payable",intd);
        bit.putExtra("subbalance",subbalance);
        bit.putExtra("mainbalance",mainbalance);
        startActivity(bit);
    }

    @Override
    public void payWithWx() {
        bd.hideBalence();
        doPay();
    }

    @Override
    public void finshPay() {
        bd.hideBalence();
        finish();
    }

    @Override
    public void goCharge() {
        bd.hideBalence();
        startActivity(new Intent(this,ChargeActivity.class));
        finish();
    }

    @Override
    public void payWithAliy() {

    }


    private void balencePay(){
        long time=System.currentTimeMillis();
        VirtualPayDeal vpd=new VirtualPayDeal(new ProgressSubscriber(virtalpayListener,this),
                productArray,key,String.valueOf(time),token,deviceNo);
        HttpManager manager=HttpManager.getInstance();
        manager.doHttpDeal(vpd);
        showDialog("铃铛支付...");
        showToast("铃铛支付");
    }

    HttpOnNextListener virtalpayListener=new HttpOnNextListener<VirtualPayMentData>() {
        @Override
        public void onNext(VirtualPayMentData obj) {
            hideDialog();
            APPLog.e("铃铛支付成功");
            showToast("支付成功");
            if (null!=completePayDialog)
                completePayDialog.show();
        }

        @Override
        public void onError() {
            hideDialog();
            showToast("支付失败");
            APPLog.e("铃铛支付失败");
            if (null!=completePayDialog)
                completePayDialog.show();
        }
    };

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {
        stoptimer=true;
        if (null!=payMethodsDialog&& payMethodsDialog.isShowing()){
            payMethodsDialog.dismiss();
            payMethodsDialog=null;
        }
        if (null!=bd){
            bd.hideBalence();
        }
        APPLog.e("关闭payactivity");
        finish();
    }

    @Override
    public void onActivityStopped(Activity activity) {}

    @Override
    public void onActivitySaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {

    }

    public void doreflash(View v){
        doPay();
    }
    private void showErrorBody(){
        error_body.setVisibility(View.VISIBLE);

    }
    @Override
    public void onActivityRestoreInstanceState(Bundle savedInstanceState) {

    }


    private void doPay(){
        body.setVisibility(View.GONE);
        APPLog.e("dopay-orderId:"+orderId);
        APPLog.e("dopay-productIds:"+productIds);
        APPLog.e("permentId:"+permentId);
        APPLog.e("paytotal:"+total);
        APPLog.e("payable:"+payable);
        APPLog.e("token:"+token);

        AliyPayDeal payDeal=new AliyPayDeal(new ProgressSubscriber(payListener,this),orderId,payable,
                productIds,permentId,token);
        HttpManager manager=HttpManager.getInstance();
        manager.doHttpDeal(payDeal);
        showDialog("加载支付方式...");
    }

    private HttpOnNextListener payListener=new HttpOnNextListener<AliyPayData>() {
        @Override
        public void onNext(AliyPayData data) {
            //Url=data.getRedirectUrl();
            hideDialog();
            String body=data.getEmbedCode();
            APPLog.e(total+"获取支付页面地址:"+body);
            wv.loadDataWithBaseURL(null, body, "text/html", "utf-8", null);
            //wv.loadUrl(Url);

        }

        @Override
        public void onError() {
            hideDialog();
            APPLog.e("获取支付页面地址 fail");
            error_body.setVisibility(View.VISIBLE);
           ToastUtil("加载失败");
        }
    };
    @Override
    public void onActivityDestroyed(Activity activity) {
    }

    @Override
    public void done(int flag) {
        if(flag==MyClick.PAYMETHOD){
            if (payMethodsDialog.getSelected()) {
                payMethodsDialog.dismiss();
                wv.loadUrl("javascript:window.local_obj.showSource('<head>'+"
                        + "document.getElementsByTagName('html')[0].innerHTML+'</head>');");

            }else
                ToastUtil("请选择支付方式");
        }else if(flag==MyClick.COMPLETE) {
            completePayDialog.dismiss();
            if (iscompleteandgocarlist) {
                OrderFlowDeal ordeal=new OrderFlowDeal(new ProgressSubscriber(orfListener,PayActivityWithAliy.this),
                        deviceNo,productIds.replace("\"",""),token);
                HttpManager.getInstance().doHttpDeal(ordeal);

            }else
                showQr.sendEmptyMessageDelayed(1,2000);
            showDialog("提交结果...");
        }
    }
    HttpOnNextListener orfListener=new HttpOnNextListener(){
        @Override
        public void onNext(Object obj) {
            APPLog.e("未购买"+obj.toString());
            showQr.sendEmptyMessageDelayed(1, 2000);
        }

        @Override
        public void onError() {
            APPLog.e("购买成功");
            delFormCatlist();
        }
    };
    String cartId;
    private void delFormCatlist() {
        final HashMap<String, Object> params = new ParamsMap(this);
        //params.put("action", "deleteShoppingCart");
        //params.put("cartId", "1609271123042346");
        APPLog.e("cartId:"+cartId);
        params.put("cartId", cartId);
        params.put("productIds", productIds.replace("\"",""));
        params.put("token",token);
        HttpManager.getInstance().doHttpDeal(new BaseDeal() {
            @Override
            public Observable getObservable(HttpService methods) {
                return methods.deleteShoppingCart(params);
            }

            @Override
            public Subscriber getSubscirber() {
                return new ProgressSubscriber(
                        new HttpOnNextListener<DeleteShoppingCart>() {
                            @Override
                            public void onError() {

                                showQr.sendEmptyMessageDelayed(1, 2000);
                            }

                            @Override
                            public void onNext(DeleteShoppingCart o) {
                                APPLog.e("删除购物车成功!");
                                showQr.sendEmptyMessageDelayed(1, 0);
                            }

                        }, PayActivityWithAliy.this);
            }

        });

    }

    class MyWebViewClient extends WebViewClient {
        /**
         * 处理https请求的时候，需要加上下面这个代码（接受证书），不然有的手机无法请求https（例如：ml）
         */
        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler,
                                       SslError error) {
            handler.proceed();// 接受所有网站的证书
            APPLog.e("MyWebViewClient", "接受证书");
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            APPLog.e("开始回调url:"+url);
            if(url.startsWith("alipays://platformapi/startApp?")){
                APPLog.e("拦截");
                return false;
            }
            if(url.startsWith("https://gw.tenpay.com/gateway/pay.htm?")){
                //这里面就是支付成功，以后的操作  ,这里面我们是调到支付成界面

               // payMethodsDialog.show();
                APPLog.e("支付成功");
                Intent intent=new Intent();
                intent.setAction(EbookDetailActivity.REFLASH);
                sendBroadcast(intent);
            }
            view.loadUrl(url);
            return true;
        }
        @Override
        public void onPageFinished( WebView view, String url) {
           APPLog.e("MyWebViewClient","onPageFinished---url="+url);

            //if (url.startsWith("https://wx.tenpay.com/cgi-bin/mmpayweb-bin/paydetail?")){
            if(url.startsWith("https://excashier.alipay.com/standard/auth.htm?")){
                APPLog.e("加载二维码");
                hideDialog();
                showDialog("生成支付二维码...");
                /*if (visibale) {
                    payMethodsDialog.show();
                }*/
                wv.loadUrl("javascript:window.local_obj.showSource('<head>'+"
                        + "document.getElementsByTagName('html')[0].innerHTML+'</head>');");
            }
            if (url.startsWith("https://tfsimg.alipay.com/images/mobilecodec/")){
                wv.loadUrl("javascript:window.local_obj.showSource('<head>'+"
                        + "document.getElementsByTagName('html')[0].innerHTML+'</head>');");
            }
            super.onPageFinished(view,url);
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            hideDialog();
            //加载失败
            APPLog.e("webView 请求失败");
            error_body.setVisibility(View.VISIBLE);
            super.onReceivedError(view, request, error);
        }

        @Override
        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
            hideDialog();
            //加载失败
            APPLog.e("webView 请求失败");
            error_body.setVisibility(View.VISIBLE);
            super.onReceivedHttpError(view, request, errorResponse);
        }
    }



    @Override
    public void onBackPressed() {
        wv.destroy();

        if(null!=completePayDialog)
            completePayDialog.show();
        else
            finish();
    }

    private void initDialog(){
        payMethodsDialog =new PayMethodsDialog(PayActivityWithAliy.this,this);
        //completePayDialog=new CompletePayDialog(PayActivityWithAliy.this,PayActivityWithAliy.this,total);

    }
    boolean iscompleteandgocarlist =false;
    public void cartList(View v) {
        if (!iscompleteandgocarlist) {
            if (null != completePayDialog) {
                completePayDialog.show();
                iscompleteandgocarlist=true;
                return;
            }
        }
        Intent cartIt=new Intent();
        cartIt.setClass(this,CartActivity.class);
        startActivity(cartIt);
    }
    public void goBack(View v){
        if (null!=completePayDialog)
        completePayDialog.show();
        else
            finish();
    }

    final class GetQrcodeFormWeb {
        @JavascriptInterface
        public void showSource(String html) {
            Document doc= Jsoup.parse(html);
            /*Elements elements= doc.select("img,qrcode").attr("id","QRcode");
            for (Element e:elements) {
                String src=e.attr("src");
                Message msg=Message.obtain();
                msg.obj=src;
                msg.what=2;
                showQr.sendMessage(msg);
            }*/
            Elements element= doc.select("input#J_qrImgUrl");
            for (Element e:element) {
                APPLog.e("value"+e.attr("value"));
                wv.loadUrl(e.attr("value"));
            }
            Elements element2= doc.select("img");
            for (Element e:element2) {
                APPLog.e("img"+e.attr("src"));
                Message msg=Message.obtain();
                msg.obj=e.attr("src");
                msg.what=2;
                showQr.sendMessage(msg);
            }
        }
    }

    String qr_src;
    Handler showQr=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what==1){
                completePayDialog.dismiss();
                finish();

                if (iscompleteandgocarlist){
                    Intent cartIt=new Intent();
                    cartIt.setClass(PayActivityWithAliy.this,CartActivity.class);
                    startActivity(cartIt);
                }
            }else if (msg.what==2){
                 String src = (String) msg.obj;

                totaltime=301;
               // if (src.startsWith("https://wx.tenpay.com/cgi-bin/mmpayweb-bin/getpayqrcode?")) {
                if(src.startsWith("https://tfsimg.alipay.com/images/mobilecodec")){
                    title.setText(bookName);
                    qr_src=src;
                    orderid_tv.setText("编号:" + orderId);
                    price.setText("¥ " + total);
                    prefprice.setText("已优惠 ¥ "+String.format("%.2f", preferPrice));
                    if (visibale) {
                        QR_iv.setClickable(true);
                        GlideUtils.getInstance().loadGreyImage(PayActivityWithAliy.this, QR_iv,R.mipmap.loading_ico,R.mipmap.qr_error, src);
                    }else
                        QR_iv.setClickable(false);
                    error_body.setVisibility(View.GONE);
                    body.setVisibility(View.VISIBLE);
                    wv.setVisibility(View.GONE);
                    // TODO: 2016/10/25 开始计时
                    timeRun.run();

                }
            }
            hideDialog();
        }
    };
    public void qrReflash(View v){
        APPLog.e(qr_src);
        ToastUtil("刷新二维码");
        QR_iv.setImageResource(R.mipmap.loading_ico);
        GlideUtils.getInstance().loadGreyImage(PayActivityWithAliy.this, QR_iv,R.mipmap.loading_ico,R.mipmap.qr_error, qr_src);
    }

    boolean stoptimer=false;
    Handler timehandle=new Handler(){
        @Override
        public void handleMessage(Message msg) {
           if (msg.what==3) {
               APPLog.e("totaltime:" + totaltime);
               if (totaltime > 0) {
                   if (!stoptimer) {
                       totaltime--;
                       timer.setText("时效: " + totaltime + "s");
                       timeRun.run();
                   } else
                       APPLog.e("stopTimer");
               } else {
                   APPLog.e("刷新");
                   body.setVisibility(View.GONE);
                   doPay();
                   timer.setText("");
               }
           }
            super.handleMessage(msg);
        }
    };

    Runnable timeRun=new Runnable() {
        @Override
        public void run() {
            if (totaltime>=0) {
                timehandle.sendEmptyMessageDelayed(3, 1000);
            }
        }
    };



}
