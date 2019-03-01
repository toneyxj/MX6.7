package com.moxi.bookstore.request;

import android.content.Context;
import android.os.AsyncTask;

import com.moxi.bookstore.R;
import com.mx.mxbase.constant.APPLog;
import com.mx.mxbase.utils.ToastUtils;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2016/2/17.
 */
public class HttpGetRequest extends AsyncTask<Object, Void, String> {
    private RequestCallBack back;// 传入接口
    private List<ReuestKeyValues> valuePairs;// 请求参数
    private String code;// 请求返回码
    private String Url;// 请求URL地址;
    private final WeakReference<Context> context;// 上下文
    private boolean showFail;
    private boolean showHide;

    public Context getcontext() {
        final Context context = this.context.get();
        if (context == null) {
            return null;
        }
        return context;
    }

    /**
     * 请求构造方法
     *
     * @param context    上下文内容
     * @param back       接口用于得到返回值
     * @param valuePairs 请求参数
     * @param code       返回标记code
     * @param Url        请求链接
     */
    public HttpGetRequest(Context context, RequestCallBack back,
                          List<ReuestKeyValues> valuePairs, String code, String Url, boolean showFail, boolean showHide) {
        this.back = back;
        this.valuePairs = valuePairs;
        this.code = code;
        this.Url = Url;
        this.context = new WeakReference<>(context);
        this.showFail = showFail;
        this.showHide = showHide;
    }

    @Override
    protected String doInBackground(Object... v) {
        String result = "";//数据请求返回值
        if (!NetUtil.checkNetworkInfo(getcontext())) {
                return getString(R.string.request_no_network);
        }
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setConnectTimeout(10, TimeUnit.SECONDS);
        okHttpClient.setWriteTimeout(10, TimeUnit.SECONDS);
        okHttpClient.setReadTimeout(30, TimeUnit.SECONDS);
        //添加数据请求url路径
        String RUrl = RequestUtils.getGetUrl(valuePairs, Url);
        APPLog.e("GET请求路径", RUrl);
        Request request;
            request = new Request.Builder()
                    .url(RUrl)
                    .build();
        Response response = null;
        try {
            response = okHttpClient
                    .newCall(request)
                    .execute();
            if (response.isSuccessful()) {
                result = response.body().string();
//                saveSession(response);
            }
        } catch (SocketTimeoutException e) {
            result = getString(R.string.request_overtime);
        } catch (IOException e) {
            result = "";
        }
        return result;

    }
    private String getString(int id){
        if (getcontext()!=null){
            return getcontext().getString(id);
        }
        return "";
    }

    @Override
    protected void onPostExecute(String result) {// 在doInBackground执行完成后系统会自动调用，result是返回值
        APPLog.e(result);
        String msg = "";
        //如果应用已退出那么就直接返回不进行后续操作
        if (getcontext()==null)return;

        int msgCode = 0;
        if (result == null || result.trim().equals("")) {
            msg = getString(R.string.request_fail);
            msgCode = 0;
        } else if (result.equals(getString(R.string.request_overtime))) {
            msg = result;
            msgCode = 1;
        } else if (result.equals(getString(R.string.request_no_network))) {
            msg = result;
            msgCode = 2;
        }

        JSONObject object;
        try {
            object = new JSONObject(result);
            JSONObject status = object.getJSONObject("status");
            int _code=status.getInt("code");
            if (_code!=0) {// 不等于1代表数据请求不成功
                msg = status.getString("message");
                msgCode = _code;
            } else {
                back.onSuccess(result, code);// 请求成功接口
                return;
            }
        } catch (JSONException e) {
            if (msg.equals("")) {
                msg = getString(R.string.request_unknown);
                msgCode = 1;
            }
        }
        back.onFail(code, showFail, msgCode, msg,result);
        if (!msg.equals("") && !showFail && showHide&&getcontext()!=null) {
            ToastUtils.getInstance().showToastShort(msg);
        }
    }
}
