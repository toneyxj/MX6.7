package com.moxi.bookstore.activity;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.dangdang.reader.MXModel.CiBa.CiBaModel;
import com.dangdang.reader.MXModel.CiBa.hanyu.HanyuMode;
import com.moxi.bookstore.R;
import com.moxi.bookstore.base.BookStoreBaseActivity;
import com.moxi.bookstore.request.RequestUtils;
import com.moxi.bookstore.request.ReuestKeyValues;
import com.moxi.bookstore.request.json.JsonAnalysis;
import com.moxi.bookstore.utils.MD5;
import com.moxi.bookstore.view.BottomLineTextview;
import com.mx.mxbase.base.MyApplication;
import com.mx.mxbase.constant.APPLog;
import com.mx.mxbase.utils.DeviceUtil;
import com.mx.mxbase.utils.StringUtils;
import com.mx.mxbase.utils.ToastUtils;
import com.mx.mxbase.view.MxTextView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import okhttp3.Call;

public class JinShanCiBaActivity extends BookStoreBaseActivity implements View.OnClickListener {
    @Override
    protected int getMainContentViewId() {
        return R.layout.activity_jin_shan_ci_ba;
    }
    @Bind(R.id.quit)
    Button quit;
    @Bind(R.id.show_text)
    MxTextView show_text;
    @Bind(R.id.add_view_item)
    LinearLayout add_view_item;
    @Bind(R.id.to_ciba)
    ImageButton to_ciba;
    @Bind(R.id.copy)
    ImageButton copy;


    @Bind(R.id.to_zh)
    BottomLineTextview to_zh;
    @Bind(R.id.to_en)
    BottomLineTextview to_en;
    @Bind(R.id.to_ko)
    BottomLineTextview to_ko;
    @Bind(R.id.to_ja)
    BottomLineTextview to_ja;
    @Bind(R.id.to_es)
    BottomLineTextview to_es;
    @Bind(R.id.to_de)
    BottomLineTextview to_de;
    @Bind(R.id.to_fr)
    BottomLineTextview to_fr;


    /**
     * 传入翻译项目
     */
    String word;
    private BottomLineTextview currentView;

    private Map<String, String> translates = new HashMap<>();
    private boolean isgetData = false;
    boolean isFirst=true;
    boolean isChinese=false;
    private HanyuMode hanyuMode;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        getWindow().getAttributes().width = (int) (MyApplication.ScreenWidth * 0.8);
        if (savedInstanceState != null) {
            finish();
            return;
        }
        word = getIntent().getExtras().getString("word");
        if (StringUtils.isNull(word)) {
            finish();
            return;
        }
        quit.setOnClickListener(this);
        to_ciba.setOnClickListener(this);
        copy.setOnClickListener(this);

        to_zh.setOnClickListener(toListener);
        to_en.setOnClickListener(toListener);
        to_ko.setOnClickListener(toListener);
        to_ja.setOnClickListener(toListener);
        to_es.setOnClickListener(toListener);
        to_de.setOnClickListener(toListener);
        to_fr.setOnClickListener(toListener);
        show_text.setOnClickListener(this);
        currentView = to_en;

        setTextShow();
    }

    View.OnClickListener toListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                if (isgetData) {
                    ToastUtils.getInstance().showToastShort("翻译中...");
                    return;
                }
                if (v == currentView) return;
                if (currentView != null) currentView.setDrawLine(false);
                currentView = (BottomLineTextview) v;
                currentView.setDrawLine(true);
                setTextShow();
            } catch (Exception e) {

            }
        }
    };
private List<ReuestKeyValues> initvaluePairs(){
    String timestamp = String.valueOf(System.currentTimeMillis());
    String auth_user = "key_hair";
    String auth_key = "dPiQcddsVjnR9G";
    String client = "2";
    String uuid = DeviceUtil.getDeviceSerial();

    String sign = MD5.stringToMD5(client + uuid + timestamp + auth_user + auth_key + word).substring(0, 16);

    List<ReuestKeyValues> valuePairs = new ArrayList<>();
    valuePairs.add(new ReuestKeyValues("client", client));
    valuePairs.add(new ReuestKeyValues("uuid", uuid));
    valuePairs.add(new ReuestKeyValues("timestamp", timestamp));
    valuePairs.add(new ReuestKeyValues("auth_user", auth_user));
    valuePairs.add(new ReuestKeyValues("sign", sign));
    valuePairs.add(new ReuestKeyValues("q", word));
    return valuePairs;
}
    private void getInfo() {
        if (currentView == null) return;
        setHint("正在翻译...");
        isgetData = true;

        List<ReuestKeyValues> valuePairs=initvaluePairs();
        valuePairs.add(new ReuestKeyValues("c", "trans"));
        valuePairs.add(new ReuestKeyValues("to", currentView.getTag().toString()));
        String RUrl = RequestUtils.getGetUrl(valuePairs, "http://ifanyi.iciba.com");
        OkHttpUtils.post().url(RUrl).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                errorShow();
            }

            @Override
            public void onResponse(String response, int id) {
                APPLog.e("onResponse",response);
                isgetData = false;
                if (isfinish || JinShanCiBaActivity.this.isFinishing()) return;
                try {
                    CiBaModel model = JsonAnalysis.getInstance().getCiBaModel(response);
                    if (StringUtils.isNull(model.out)) {
                        if (isFirst){
                            currentView=to_zh;
                        }
                        translates.put(model.to,word);
                    }else {
                        translates.put(model.to,model.out);
                    }
                    isChinese=(model.from!=null&&model.from.contains("zh"));
                    isFirst=false;
                    setTextShow();
                }catch ( Exception e){
                    errorShow();
                }
            }
        });
    }
    private void errorShow(){
        if (isfinish || JinShanCiBaActivity.this.isFinishing()) return;
        setHint("点击重试...");
        isgetData = false;
        if (isFirst){
            currentView.setDrawLine(true);
        }
    }
    //中文翻译
    private void getZHCN() {
        if (currentView == null) return;
        setHint("正在翻译...");
        isgetData = true;

        List<ReuestKeyValues> valuePairs=initvaluePairs();
        valuePairs.add(new ReuestKeyValues("c", "hanyu"));
        String RUrl = RequestUtils.getGetUrl(valuePairs, "http://ifanyi.iciba.com");
        APPLog.e(RUrl);
        OkHttpUtils.post().url(RUrl).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                erroeShow();
            }

            @Override
            public void onResponse(String response, int id) {
                APPLog.e("onResponse",response);
                isgetData = false;
                if (isfinish || JinShanCiBaActivity.this.isFinishing()) return;
                try {
                    translates.put(currentView.getTag().toString(),word);
                    hanyuMode=JsonAnalysis.getInstance().getHanyuModel(response);
                    setTextShow();
                }catch (Exception e){
                    erroeShow();
                }

            }
        });
    }
    private void erroeShow(){
        if (isfinish || JinShanCiBaActivity.this.isFinishing()) return;
        setHint("点击重试...");
        isgetData = false;
        if (isFirst){
            currentView.setDrawLine(true);
        }
    }

    private void setHint(String value){
        show_text.setSourceText(value);
        show_text.setTag(false);
    }

    private void setTextShow( ) {
        String text = translates.get(currentView.getTag().toString());
        if (StringUtils.isNull(text)||(isChinese&&currentView.getTag().toString().equals("zh")&&hanyuMode==null)) {
            if (isChinese&&currentView.getTag().toString().equals("zh")){//传入文字是中文，进行中文翻译
                getZHCN();
            }else {
                getInfo();
            }
        } else {
            if (currentView==to_zh||currentView==to_en){
                show_text.getLayoutParams().height=show_text.getHeight()+10;
            }
            if ((isChinese&&currentView.getTag().toString().equals("zh")&&hanyuMode!=null)){
                show_text.setSourceText(hanyuMode.getHanyuValue(word));
            }else {
                show_text.setSourceText(text);
            }
            show_text.setTag(true);
            currentView.setDrawLine(true);
        }

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
         getWindow().getDecorView().invalidate();
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("word", word);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_PAGE_UP) {
            show_text.flipOver(false);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_PAGE_DOWN) {
            show_text.flipOver(true);
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.quit:
                this.finish();
                break;
            case R.id.show_text:
                try {
                    if (show_text.getHint().toString().equals("点击重试...")){
                        setTextShow();
                    }
                }catch (Exception e){

                }
                break;
            case R.id.to_ciba:
                try {
                    Intent intent;
                    intent = new Intent("android.intent.action.VIEW");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("word", word);
                    intent.setComponent(new ComponentName("com.kingsoft", "com.kingsoft.WordDetailActivity"));
                    startActivity(intent);
                    this.finish();
                } catch (Exception e) {
                    e.printStackTrace();
                    ToastUtils.getInstance().showToastShort("请确认该模块是否安装！");
                }
                this.finish();
                break;
            case R.id.copy:
                if (show_text == null || show_text.getText() == null) {
                    ToastUtils.getInstance().showToastShort("无法复制文本");
                    return;
                }
                if (!(Boolean) show_text.getTag()) {
                    ToastUtils.getInstance().showToastShort("无复制内容");
                    return;
                }
                //获取剪贴板管理器：
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                // 创建普通字符型ClipData
                ClipData mClipData = ClipData.newPlainText("text", show_text.getText().toString());
                // 将ClipData内容放到系统剪贴板里。
                cm.setPrimaryClip(mClipData);
                ToastUtils.getInstance().showToastShort("复制成功");
                break;
            default:
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.finish();
    }
}
