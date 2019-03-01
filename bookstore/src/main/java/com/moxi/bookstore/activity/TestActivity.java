package com.moxi.bookstore.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.moxi.bookstore.R;
import com.mx.mxbase.constant.APPLog;
import com.mx.mxbase.utils.StringUtils;

public class TestActivity extends Activity {
    /**
     * @param context
     */
    public static void startWeb(Context context) {
        Intent inLi = new Intent(context, TestActivity.class);
//        inLi.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(inLi);
    }

    private WebView webView;
    private ProgressBar progressBar;
    private LinearLayout ll_base_back;
    private TextView tv_base_mid_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test1);
        ll_base_back = (LinearLayout) findViewById(R.id.ll_base_back);
        tv_base_mid_title = (TextView) findViewById(R.id.tv_base_mid_title);
        ll_base_back.setVisibility(View.VISIBLE);

        tv_base_mid_title.setText("WiFi认证");
        ll_base_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        webView = (WebView) findViewById(R.id.webview);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);//进度条

//        webView.loadUrl("http://52.80.9.199/portal?res=notyet&auth_port=54476&auth=3Lj7uC9a9yjVbDRuauXaW5doeoYC23bhN8LYY7EakAs0X2BA4kj9Wi4JLr03ry%2fwB3FZllnuOno2dQhG7PwU9Y270IYT5Ljn6%2fekZaaCWhdVPkrZhVONw8r5ZzWneDqhOsT5r9GppXtJ3D%2bj5NTs9SNfmY4FczJvIDr%2f1hw7NcJNIHHzwkQvaQjQOfCGnrWyTQWMN3wAUa1DcFSMu9%2bQhTfxVUY0FY6F4Dm1WR%2bFqLlZR27fEPU2rC%2bASwJOtueg4NymJ0eD8VLKpEnXvv4gROksZPPpxFxGGAiM7NzW3MoobBSDWJSAFkM1lrI8JNdD&userurl=http%3a%2f%2fofflintab.firefoxchina.cn%2f");//加载url
        webView.loadUrl("http://www.baidu.com");//加载url


        webView.addJavascriptInterface(this, "android");//添加js监听 这样html就能调用客户端
        webView.setWebChromeClient(webChromeClient);
        webView.setWebViewClient(webViewClient);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);//允许使用js

        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);//不使用缓存，只从网络获取数据.

        // 设置可以支持缩放
        webSettings.setSupportZoom(true);
        // 设置出现缩放工具
        webSettings.setBuiltInZoomControls(true);

        webSettings.setDisplayZoomControls(true);
        //扩大比例的缩放
        webSettings.setUseWideViewPort(true);
        //自适应屏幕
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setLoadWithOverviewMode(true);

    }

    //WebViewClient主要帮助WebView处理各种通知、请求事件
    private WebViewClient webViewClient = new WebViewClient() {
        @Override
        public void onPageFinished(WebView view, String url) {//页面加载完成
            progressBar.setVisibility(View.GONE);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {//页面开始加载
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            APPLog.e("ansen", "拦截url:" + url);
            if (url.equals("http://www.google.com/")) {
                Toast.makeText(TestActivity.this, "国内不能访问google,拦截该url", Toast.LENGTH_LONG).show();
                return true;//表示我已经处理过了
            }
            return super.shouldOverrideUrlLoading(view, url);
        }

    };

    //WebChromeClient主要辅助WebView处理Javascript的对话框、网站图标、网站title、加载进度等
    private WebChromeClient webChromeClient = new WebChromeClient() {
        //不支持js的alert弹窗，需要自己监听然后通过dialog弹窗
        @Override
        public boolean onJsAlert(WebView webView, String url, String message, JsResult result) {
            AlertDialog.Builder localBuilder = new AlertDialog.Builder(webView.getContext());
            localBuilder.setMessage(message).setPositiveButton("确定", null);
            localBuilder.setCancelable(false);
            localBuilder.create().show();

            //注意:
            //必须要这一句代码:result.confirm()表示:
            //处理结果为确定状态同时唤醒WebCore线程
            //否则不能继续点击按钮
            result.confirm();
            return true;
        }

        //获取网页标题
        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            if (!StringUtils.isNull(title)){
                tv_base_mid_title.setText(title);
            }
            APPLog.e("ansen", "网页标题:" + title);
        }

        //加载进度回调
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            progressBar.setProgress(newProgress);
        }
    };

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack(); // goBack()表示返回webView的上一页面
            return;
        }
        super.onBackPressed();
    }

    /**
     * JS调用android的方法
     *
     * @param str
     * @return
     */
    @JavascriptInterface //仍然必不可少
    public void getClient(String str) {
        APPLog.e("ansen", "html调用客户端:" + str);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //释放资源
        webView.destroy();
        webView = null;
    }
}
