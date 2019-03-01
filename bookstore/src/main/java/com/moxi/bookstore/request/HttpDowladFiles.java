package com.moxi.bookstore.request;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;


/**
 * Created by Administrator on 2016/2/25.
 */
public class HttpDowladFiles extends AsyncTask<String, Void, String> implements NetUtil.ClickProgressButton {
    private DowloadCallBack back;// 传入接口
    private String title;// 显示dialog标题
    private String hint;// 提示文字
    private String buttonTxt;// 按钮文字
    private String dowloadUrl;// 请求URL地址;
    private String backUrl;// 返回保存Sd卡地址
    private Context context;// 上下文
    private ProgressDialog dialog;

    /**
     * 请求构造方法
     *
     * @param context    上下文内容
     * @param back       下载完成回调
     * @param dowloadUrl 请求链接
     * @param backUrl    返回保存Sd卡地址
     */
    public HttpDowladFiles(Context context, DowloadCallBack back, String dowloadUrl, String backUrl, String title, String hint, String buttonTxt) {
        this.back = back;
        this.dowloadUrl = dowloadUrl;
        this.context = context;
        this.backUrl = backUrl;
        this.title = title;
        this.hint = hint;
        this.buttonTxt = buttonTxt;
    }

    @Override
    protected String doInBackground(String... arg0) {
        String result = "";//返回值
        if (!NetUtil.checkNetworkInfo(context)) {
            return "网络未连接";
        }

        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setConnectTimeout(10, TimeUnit.MINUTES);
        okHttpClient.setReadTimeout(10, TimeUnit.MINUTES);
        okHttpClient.setWriteTimeout(10, TimeUnit.MINUTES);
        //添加数据请求url路径
        final Request request = new Request.Builder()
                .url(dowloadUrl)
                .build();
        //包装Response使其支持进度回调
        Response response = null;
        try {
            response = ProgressHelper.addProgressResponseListener(okHttpClient, listener).newCall(request).execute();
            if (response.isSuccessful()) {
                InputStream input = response.body().byteStream();
                result = "下载成功";
                result= writeFile(backUrl,input,result);
            }
        } catch (IOException e) {
            result = "";
        }
        return result;

    }

    ProgressResponseBody.ProgressResponseListener listener = new ProgressResponseBody.ProgressResponseListener() {
        @Override
        public void onResponseProgress(long bytesRead, long contentLength, boolean done) {
//            Log.e("TAG", "bytesWrite:" + bytesRead);
//            Log.e("TAG", "contentLength" + contentLength);
//            Log.e("TAG", (100 * bytesRead) / contentLength + " % done ");
//            Log.e("TAG", "done:" + done);
//            Log.e("TAG", "================================");
            if (!done) {
                int dowladSize = (int) ((100 * bytesRead) / contentLength);
                dialog.setProgress(dowladSize);
            }
        }
    };

    private String writeFile(String savePath, InputStream input,String result) {
        File file = null;
        OutputStream output = null;
        try {
            file = creatSDFile(savePath);
            output = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int len=0;
            while (((len=input.read(buffer)) != -1)) {
                output.write(buffer,0,len);
            }
            output.flush();
        } catch (IOException e) {
            result= "保存失败";
        } finally {
            try {
                input.close();
                output.close();
            } catch (IOException e) {
                result= "保存失败";
            }
        }
        return result;
    }

    /**
     * 在SD卡上创建文件
     *
     * @throws IOException
     */
    private File creatSDFile(String fileName) throws IOException {
        File file = new File(fileName);
        if (file.exists()) {
            //删除以前的文件
            deleteFile(file);
        }
        file.createNewFile();
        return file;
    }
    // 将SD卡文件删除
    public  void deleteFile(File file) {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            if (file.exists()) {
                if (file.isFile()) {
                    file.delete();
                }
                // 如果它是一个目录
                else if (file.isDirectory()) {
                    // 声明目录下所有的文件 files[];
                    File files[] = file.listFiles();
                    for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
                        deleteFile(files[i]); // 把每个文件 用这个方法进行迭代
                    }
                }
                file.delete();
            }
        }
    }
    @Override
    protected void onPostExecute(String result) {// 在doInBackground执行完成后系统会自动调用，result是返回值
        dialog.dismiss();
        String msg = result;
        if (result == null || result.trim().equals("")) {
            msg = "哦哦！下载失败";
        } else if (result.equals("网络未连接")) {
            msg = "网络未连接！请检查网络";
        } else if(result.equals("下载成功")){
            back.dowladSucess(backUrl);
        }
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPreExecute() {// 执行后台耗时操作前执行
        dialog = NetUtil.getDialog(context, this, title, hint, buttonTxt);
    }

    @Override
    public void onClickProgress(DialogInterface dialog, int which) {
        dialog.dismiss();
        back.stopDowlad(this, backUrl);
    }

    public interface DowloadCallBack {
        /*
         *
         * 请求成功回调
         */
        public void dowladSucess(String resultPath);

        /**
         * 取消下载
         */
        public void stopDowlad(HttpDowladFiles dowlad, String resultPath);
    }
}
