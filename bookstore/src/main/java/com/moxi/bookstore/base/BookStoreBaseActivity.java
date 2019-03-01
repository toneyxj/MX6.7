package com.moxi.bookstore.base;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.Toast;

import com.moxi.bookstore.BookstoreApplication;
import com.moxi.bookstore.R;
import com.moxi.bookstore.request.HttpGetRequest;
import com.moxi.bookstore.request.HttpPostRequest;
import com.moxi.bookstore.request.RequestCallBack;
import com.moxi.bookstore.request.ReuestKeyValues;
import com.moxi.bookstore.request.json.Connector;
import com.moxi.bookstore.utils.ToolUtils;
import com.mx.mxbase.base.BaseActivity;
import com.mx.mxbase.dialog.HitnDialog;
import com.mx.mxbase.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/12.
 */
public abstract class BookStoreBaseActivity extends BaseActivity implements RequestCallBack {
    Toast toast;

    public void ToastUtil(String str) {
        if (null == toast) {
            toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        }
        toast.setText(str);
        toast.show();
    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    /**
     * 提示dialog
     */
    public HitnDialog dialog;
    public boolean isfinish = false;
    private AsyncTask asyncTask;
    private int requestTotal = 0;

    /**
     * get请求方式
     *
     * @param valuePairs
     * @param code
     * @param Url
     * @param show
     */
    public void getData(
            List<ReuestKeyValues> valuePairs, String code, String Url, boolean show, String hitn) {
        hitn = (hitn == null || hitn.equals("")) ? getString(R.string.data_loding) : hitn;
        if (valuePairs == null) valuePairs = new ArrayList<>();
        if (show) {
            showDialog(hitn);
        }
        asyncTask = new HttpGetRequest(this, this, valuePairs, code, Url, false, show);
        requestTotal++;
        asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void hideDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        dialog = null;
    }

    /**
     * 显示dialog
     */
    public void showDialog(String content) {
        if (visibale) {
            if (dialog == null) {
                dialog = dialogShowOrHide(true, content);
            }
            if (dialog != null && !dialog.isShowing()) {
                dialog.show();
            }
        }
    }


    public ReuestKeyValues getthisToken() {
        String token = getTokenValue();
        if (token==null)token="";
        return new ReuestKeyValues("token", token);

    }

    public String getTokenValue() {
        return ToolUtils.getIntence().getTokenStr(this);
    }

    /**
     * post请求方式
     *
     * @param valuePairs
     * @param code
     * @param Url
     * @param show
     */
    public void PostData(
            List<ReuestKeyValues> valuePairs, String code, String Url, boolean show, String hitn) {
        hitn = (hitn == null || hitn.equals("")) ? getString(R.string.data_loding) : hitn;
        if (valuePairs == null) valuePairs = new ArrayList<>();
        if (show) {
            showDialog(hitn);
        }
        requestTotal++;
        asyncTask = new HttpPostRequest(this, this, valuePairs, code, Url, false, show);
        asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
    public void getUserBuyBooks(boolean is){
        if (!StringUtils.isNull(getTokenValue())) {
            List<ReuestKeyValues> valuePairs = new ArrayList<>();
            valuePairs.add(new ReuestKeyValues("action", "myProperty"));
            valuePairs.add(new ReuestKeyValues("returnType", "json"));
            valuePairs.add(new ReuestKeyValues("deviceType", "Android"));
            valuePairs.add(getthisToken());
            getData(valuePairs,Connector.getInstance().myProperty, Connector.getInstance().url, is, "");
        }
    }

    @Override
    public void onSuccess(String result, String code) {
        hideDialog();
        requestTotal--;
        try {
            if (!isfinish)
                Success(result, code);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onFail(String code, boolean showFail, int failCode, String msg, String result) {
        if (isfinish) return;
        if ((failCode == ToolUtils.noEfficacy||(msg!=null&&msg.contains("未登录")))&&!code.equals(Connector.getInstance().getMonthlyChannelList)) {
            ToolUtils.getIntence().startDDUserBind(BookstoreApplication.getContext());
        }
        requestTotal--;
        hideDialog();
        fail(code);
    }

    /**
     * 检查获得当当用户包月信息
     */
    public void getDDUserInfor(boolean show,boolean notifiy){
        if (!StringUtils.isNull(getTokenValue())) {
            List<ReuestKeyValues> valuePairs = new ArrayList<>();
            valuePairs.add(new ReuestKeyValues("action", "getMonthlyChannelList"));
            valuePairs.add(new ReuestKeyValues("returnType", "json"));
            valuePairs.add(new ReuestKeyValues("deviceType", "Android"));
            valuePairs.add(new ReuestKeyValues("channelId", "30000"));
            valuePairs.add(new ReuestKeyValues("clientVersionNo", "5.9.0"));
            valuePairs.add(new ReuestKeyValues("serverVersionNo", "1.2.1"));
            valuePairs.add(new ReuestKeyValues("permanentId", "20171030022433915544270373781612768"));
            valuePairs.add(new ReuestKeyValues("deviceSerialNo", "db74b5e9998e155f105b8e2fca2da103"));
            valuePairs.add(new ReuestKeyValues("macAddr", "f0%3A43%3A47%3A1b%3A02%3Aa0"));
            valuePairs.add(new ReuestKeyValues("resolution", "1080*1794"));
            valuePairs.add(new ReuestKeyValues("clientOs", "4.1"));
            valuePairs.add(new ReuestKeyValues("platformSource", "DDDS-P"));
            valuePairs.add(getthisToken());
            getData(valuePairs,notifiy?Connector.getInstance().getMonthlyChannelListNotify:Connector.getInstance().getMonthlyChannelList
                    , Connector.getInstance().url, show, "");
        }
    }

    public void Success(String result, String code) {

    }


    public void fail(String code) {

    }

    public boolean visibale = true;

    @Override
    protected void onStop() {
        visibale = false;
        super.onStop();
    }

    @Override
    protected void onPause() {
        visibale = false;
        super.onPause();
    }

    @Override
    protected void onResume() {
        visibale = true;
        super.onResume();
    }


    public boolean isDialogShow() {
        return (dialog != null && dialog.isShowing());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isfinish = true;
        hideDialog();
        if (asyncTask != null && !asyncTask.isCancelled()) {
            asyncTask.cancel(true);
        }
    }


}
