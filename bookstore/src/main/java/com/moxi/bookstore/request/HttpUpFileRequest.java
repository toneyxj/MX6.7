package com.moxi.bookstore.request;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.moxi.bookstore.R;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 上传多张图片
 * Created by Administrator on 2016/2/17.
 */
public class HttpUpFileRequest extends AsyncTask<String, Void, String> {
    private RequestCallBack back;// 传入接口
    private List<ReuestKeyValues> valuePairs;// 请求参数
    private String code;// 请求返回码
    private String Url;// 请求URL地址;
    private String result = "";//数据请求返回值
    private List<ReuestKeyValues> filePaths;
    private ProgressDialog dialog;
    private String title;
    private String hint;
    private final WeakReference<Context> context;// 上下文
    private boolean showFail;

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
     * @param hint       提示信息
     * @param title      提示标题
     * @param filePaths  文件集合
     */
    public HttpUpFileRequest(Context context, RequestCallBack back,
                             List<ReuestKeyValues> valuePairs, String code, String Url,
                             List<ReuestKeyValues> filePaths, String title, String hint, boolean showFail) {
        this.back = back;
        this.valuePairs = valuePairs;
        this.code = code;
        this.Url = Url;
        this.context = new WeakReference<>(context);
        this.filePaths = filePaths;
        this.title = title;
        this.hint = hint;
        this.showFail = showFail;
    }
    private String getString(int id){
        if (getcontext()!=null){
            return getcontext().getString(id);
        }
        return "";
    }
    @Override
    protected String doInBackground(String... arg0) {
        if (!NetUtil.checkNetworkInfo(getcontext())) {
            return getString(R.string.request_no_network);
        }
        final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");

        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setConnectTimeout(10, TimeUnit.MINUTES);
        okHttpClient.setReadTimeout(10, TimeUnit.MINUTES);
        okHttpClient.setWriteTimeout(10, TimeUnit.MINUTES);

        MultipartBuilder builder = new MultipartBuilder().type(MultipartBuilder.FORM);
        //添加请求参数值
        if (valuePairs != null && valuePairs.size() != 0) {
            for (int i = 0; i < valuePairs.size(); i++) {
                ReuestKeyValues kv = valuePairs.get(i);
                Log.e(kv.key, kv.value);
                builder.addFormDataPart(kv.key, kv.value);
            }
        }
        //添加文件
        //遍历paths中所有图片绝对路径到builder，并约定key如“upload”作为后台接受多张图片的key
        for (ReuestKeyValues path : filePaths) {
            Log.e(path.key, path.value);
            builder.addFormDataPart(path.key, path.value, RequestBody.create(MEDIA_TYPE_PNG, new File(path.value)));
        }
        RequestBody requestBody = builder.build();
        //添加数据请求url路径
        final Request request = new Request.Builder()
                .url(Url)
                .post(ProgressHelper.addProgressRequestListener(requestBody, progressRequestListener))
                .build();
        Response response = null;
        try {
            response = okHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                result = response.body().string();
            }
        } catch (SocketTimeoutException e){
            result=getString(R.string.request_overtime);
        }catch (IOException e) {
            result="";
        }
        return result;

    }

    ProgressRequestBody.ProgressRequestListener progressRequestListener = new ProgressRequestBody.ProgressRequestListener() {
        @Override
        public void onRequestProgress(long bytesWritten, long contentLength, boolean done) {
//            Log.e("TAG", "bytesWrite:" + bytesWritten);
//            Log.e("TAG", "contentLength" + contentLength);
//            Log.e("TAG", (100 * bytesWritten) / contentLength + " % done ");
//            Log.e("TAG", "done:" + done);
//            Log.e("TAG", "================================");
            if (!done) {
                int dowladSize = (int) ((100 * bytesWritten) / contentLength);
                if (dialog!=null&&dialog.isShowing())
                dialog.setProgress(dowladSize);
            }
        }
    };

    @Override
    protected void onPostExecute(String result) {// 在doInBackground执行完成后系统会自动调用，result是返回值
        if (dialog!=null&&dialog.isShowing())
        dialog.dismiss();

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
        back.onFail(code, showFail, msgCode, msg,result);
        if (!msg.equals("") && !showFail&&getcontext()!=null) {
            Toast.makeText(getcontext(), msg, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onPreExecute() {// 执行后台耗时操作前执行
        if (getcontext()!=null)
        dialog = NetUtil.getDialog(getcontext(), null, title, hint, null);
    }
}

