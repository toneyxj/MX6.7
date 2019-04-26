package com.moxi.biji.youdao;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Html;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.moxi.biji.R;
import com.moxi.biji.youdao.config.URLUtils;
import com.moxi.biji.youdao.config.YouDaoInfo;
import com.mx.mxbase.constant.APPLog;

import org.json.JSONObject;

public class OauthActivity extends Activity {

    public static void start(Activity context, String url) {
        APPLog.e("OauthActivity-url",url);
        Intent intent = new Intent(context, OauthActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        intent.putExtras(bundle);
        context.startActivityForResult(intent,10);
    }

    private ViewGroup main_layout;
    private WebView webView;

    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) savedInstanceState = getIntent().getExtras();
        url = savedInstanceState.getString("url");
        setContentView(R.layout.activity_oauth);
        initview();
        initWebView();
        setWebViewClient();
        webView.loadUrl(url);
    }

    private void initview() {
        main_layout = (ViewGroup) findViewById(R.id.main_layout);
        webView = new WebView(this);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        webView.setLayoutParams(params);
        main_layout.addView(webView);
    }

    private void initWebView() {
//  WebSettings webSettings = webView.getSettings();
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);//设置js可以直接打开窗口，如window.open()，默认为false
        webView.getSettings().setJavaScriptEnabled(true);//是否允许执行js，默认为false。设置true时，会提醒可能造成XSS漏洞
        webView.addJavascriptInterface(new InJavaScriptLocalObj(), "local_obj");
        webView.getSettings().setSupportZoom(true);//是否可以缩放，默认true
        webView.getSettings().setDisplayZoomControls(false); //隐藏原生的缩放控件
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDefaultZoom(WebSettings.ZoomDensity.CLOSE);
        webView.getSettings().setUseWideViewPort(true);//设置此属性，可任意比例缩放。大视图模式
        webView.getSettings().setLoadWithOverviewMode(true);//和setUseWideViewPort(true)一起解决网页自适应问题
        webView.getSettings().setAppCacheEnabled(true);//是否使用缓存
        webView.getSettings().setDomStorageEnabled(true);//DOM Storage
    }

    private void setWebViewClient() {
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                //想在页面开始加载时有操作，在这添加
                super.onPageStarted(view, url, favicon);

                if (url.contains(YouDaoInfo.getInstance().getBackUrl())) {
                    try {
                        url = url.replace(" ", "");
                        String[] values = url.split("\\?");
                        if (values.length == 2) {
                            String end = values[1];
                            String[] ends = end.split("&");
                            String state = getCode("state=", ends);
                            String code = getCode("code=", ends);

                            if (null != state || null != code) {
                                if (state.equals(YouDaoInfo.getInstance().getState())) {
                                    YouDaoInfo.getInstance().setCode(code);
                                    webView.loadUrl(URLUtils.getAccessTokenUrl());
                                    return;
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                //想在页面加载结束时有操作，在这添加
                super.onPageFinished(view, url);
                APPLog.e("onPageFinished-url",url);
                if (url.startsWith(URLUtils.AccessTokenURL)){
                    // 获取页面内容
                    view.loadUrl("javascript:window.local_obj.showSource("
                            + "document.getElementsByTagName('html')[0].innerHTML);");
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                //返回值是true的时候WebView打开，为false则系统浏览器或第三方浏览器打开。如果要下载页面中的游戏或者继续点击网页中的链接进入下一个网页的话，重写此方法下，不然就会跳到手机自带的浏览器了，而不继续在你这个webview里面展现了
                return true;
            }

            @Override

            public void onReceivedError(WebView view, int errorCode,

                                        String description, String failingUrl) {
                //想在收到错误信息的时候，执行一些操作，走此方法
                OauthActivity.this.setResult(RESULT_OK);
                OauthActivity.this.onBackPressed();
            }
        });
    }

    private String getCode(String code, String[] values) {

        for (String value : values) {
            if (value.contains(code)) {
                return value.replace(code, "");
            }
        }
        return null;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("url", url);
    }

    @Override
    protected void onResume() {
        super.onResume();
        webView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        webView.onPause();
        //它会暂停所有webview的layout，parsing，javascripttimer。降低CPU功耗。
        webView.pauseTimers();
        //恢复pauseTimers状态
        webView.resumeTimers();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        main_layout.removeAllViews();
        webView.destroy();
    }
    public final class InJavaScriptLocalObj
    {
        @JavascriptInterface
        public void showSource(String html) {
            try {
                html=splitAndFilterString(html);

                int start=html.lastIndexOf("{");
                int end=html.lastIndexOf("}");
                if (end==0)return;
                end++;
                String value=html.substring(start,end);
                APPLog.e(value);
                JSONObject jsonObject=new JSONObject(value);
                String accessToken= jsonObject.getString("accessToken");
                YouDaoInfo.getInstance().setAccessToken(accessToken);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        OauthActivity.this.setResult(RESULT_OK);
                        OauthActivity.this.onBackPressed();
                    }
                });

            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }
    private String splitAndFilterString(String input) {
        APPLog.e("splitAndFilterString",Html.fromHtml(input));
        return Html.fromHtml(input).toString();
    }
}
