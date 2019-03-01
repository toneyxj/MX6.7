package com.moxi.bookstore.request;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.moxi.bookstore.R;
import com.moxi.bookstore.config.UserInfomation;
import com.mx.mxbase.constant.APPLog;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.SocketTimeoutException;
import java.util.List;

/**
 * Created by Administrator on 2016/2/17.
 */
public class HttpPostRequest extends AsyncTask<String, Void, String> {
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
    public HttpPostRequest(Context context, RequestCallBack back,
                           List<ReuestKeyValues> valuePairs, String code, String Url, boolean showFail,boolean showHide) {
        this.back = back;
        this.valuePairs = valuePairs;
        this.code = code;
        this.Url = Url;
        this.context = new WeakReference<>(context);
        this.showFail = showFail;
        this.showHide=showHide;
    }

    @Override
    protected String doInBackground(String... arg0) {
        String result = "";//数据请求返回值
        if (!NetUtil.checkNetworkInfo(getcontext())) {
            return getString(R.string.request_no_network);
        }
        OkHttpClient okHttpClient = new OkHttpClient();
        FormEncodingBuilder builder = new FormEncodingBuilder();
        for (int i = 0; i < valuePairs.size(); i++) {
            ReuestKeyValues value = valuePairs.get(i);
            APPLog.e(value.key, value.value);
            builder.add(value.key, value.value);
        }
        //添加数据请求url路径
        Request request = null;
        if (!UserInfomation.getInstance().session_id.equals("")) {
            String session_id = UserInfomation.getInstance().session_id;
            request = new Request.Builder()
                    .url(Url)
                    .addHeader("Cookie", session_id)
                    .post(builder.build())
                    .build();
        } else {
            request = new Request.Builder()
                    .url(Url)
                    .post(builder.build())
                    .build();
        }


        Response response = null;
        try {
            response = okHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                result = response.body().string();
                saveSession(response);
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
    private void saveSession(Response response){
        // 取得sessionid.
        if (UserInfomation.getInstance().session_id.equals("")) {
            String cookieval = response.header("set-cookie");
            String sessionid = null;
            if (cookieval != null) {
                sessionid = cookieval.substring(0, cookieval.indexOf(";"));
                UserInfomation.getInstance().session_id = sessionid;
            }
        }
    }
    @Override
    protected void onPostExecute(String result) {// 在doInBackground执行完成后系统会自动调用，result是返回值
        APPLog.e(Url, "返回结果" + result);
        String msg = "";
        int msgCode = 0;
        //如果应用已退出那么就直接返回不进行后续操作
        if (getcontext()==null)return;

        if (result == null || result.trim().equals("")) {
            msg = getString(R.string.request_fail);
            msgCode = 0;
        } else if (result.equals(getString(R.string.request_overtime))) {
            msg =result;
            msgCode = 1;
        } else if (result.equals(getString(R.string.request_no_network))) {
            msg = result;
            msgCode = 2;
        }
        JSONObject object;
        try {
            object = new JSONObject(result);
            String status = object.getString("code");
            if (!status.equals("0")) {// 不等于1代表数据请求不成功
                msg = object.getString("msg");
                msgCode = 0;
            } else {
                back.onSuccess(result, code);// 请求成功接口
                return;
            }
        } catch (JSONException e) {
            if (msg.equals("")) {
                msg=getString(R.string.request_unknown);
                msgCode = 1;
            }
        }
        back.onFail(code, showFail, msgCode, msg, result);
        if (!msg.equals("") && !showFail&&showHide&&getcontext()!=null) {
            Toast.makeText(getcontext(), msg, Toast.LENGTH_SHORT).show();
        }

    }
}
