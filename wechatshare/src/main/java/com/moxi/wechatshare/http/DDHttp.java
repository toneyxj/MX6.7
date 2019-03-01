package com.moxi.wechatshare.http;

import android.util.Log;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by along on 2017/8/31.
 */

public class DDHttp {

    private static OkHttpClient okHttpClient = null;

    private static OkHttpClient getOkHttpClientInstance(){
        if (okHttpClient == null){
            okHttpClient = new OkHttpClient();
        }
        return okHttpClient;
    }

    public static void get(String url,final HttpCallBack httpCallBack){

        final Request request = new Request.Builder().url(url).build();
        Call httpCall = getOkHttpClientInstance().newCall(request);
        httpCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                httpCallBack.onFaild(-1,call.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Log.d("dd","onResponse ===>" + result);
                try{
                    JSONObject jsonObject = new JSONObject(result);
                    int code = jsonObject.optInt("error",-10000);
                    if (code == 101){
                        httpCallBack.onSuccess(result);
                    }else if (code == 104){
                        httpCallBack.onSuccess(result);
                    }else{
                        httpCallBack.onFaild(-2,result);
                    }
                }catch (Exception e){
                    httpCallBack.onFaild(-1,e.getMessage());
                }
            }
        });
    }

    public static void getFromHtml(String url,final HttpCallBack httpCallBack){
        final Request request = new Request.Builder().url(url).build();
        Call httpCall = getOkHttpClientInstance().newCall(request);
        httpCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                httpCallBack.onFaild(-1,call.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Log.d("dd","onResponse ===>" + result);
                httpCallBack.onSuccess(result);
            }
        });
    }

    public static void get(String url,Map param,final HttpCallBack httpCallBack){
        if (param != null && param.size()>0) {
            if (!param.isEmpty()) {
                StringBuffer sb = new StringBuffer();
                boolean isFirst = true;
                Iterator iter = param.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    if (isFirst) {
                        isFirst = false;
                        if (url.contains("?")) {
                            sb.append("&" + entry.getKey() + "=" + entry.getValue());
                        } else {
                            sb.append("?" + entry.getKey() + "=" + entry.getValue());
                        }
                    } else {
                        sb.append("&" + entry.getKey() + "=" + entry.getValue());
                    }
                }
                String params = sb.toString();
                url = url + params;
            }
        }
        final Request request = new Request.Builder().url(url).build();
        Call httpCall = getOkHttpClientInstance().newCall(request);
        httpCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                httpCallBack.onFaild(-1,call.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                try{
                    JSONObject jsonObject = new JSONObject(result);
                    int code = jsonObject.optInt("error",-10000);
                    if (code == 101){
                        httpCallBack.onSuccess(result);
                    }else if (code == 104){
                        httpCallBack.onSuccess(result);
                    }else{
                        httpCallBack.onFaild(-2,result);
                    }
                }catch (Exception e){
                    httpCallBack.onFaild(-1,"http get faild =======>" + e.getMessage());
                }
            }
        });
    }

    public static void get(String url,Map param,Map header,final HttpCallBack httpCallBack){
        if (param != null && param.size()>0) {
            if (!param.isEmpty()) {
                StringBuffer sb = new StringBuffer();
                boolean isFirst = true;
                Iterator iter = param.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    if (isFirst) {
                        isFirst = false;
                        if (url.contains("?")) {
                            sb.append("&" + entry.getKey() + "=" + entry.getValue());
                        } else {
                            sb.append("?" + entry.getKey() + "=" + entry.getValue());
                        }
                    } else {
                        sb.append("&" + entry.getKey() + "=" + entry.getValue());
                    }
                }
                String params = sb.toString();
                url = url + params;
            }
        }

        final Request.Builder builder = new Request.Builder();
        /**HTTP GET ADD HEADER*/
        Iterator iter = header.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            builder.addHeader(String.valueOf(entry.getKey()),String.valueOf(entry.getValue()));
        }
        builder.url(url);
        final Request request = builder.build();
        Call httpCall = getOkHttpClientInstance().newCall(request);
        httpCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                httpCallBack.onFaild(-1,call.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                try{
                    JSONObject jsonObject = new JSONObject(result);
                    int code = jsonObject.optInt("error",-10000);
                    if (code == 101){
                        httpCallBack.onSuccess(result);
                    }else if (code == 104){
                        httpCallBack.onSuccess(result);
                    }else{
                        httpCallBack.onFaild(-2,result);
                    }
                }catch (Exception e){
                    httpCallBack.onFaild(-1,e.getMessage());
                }
            }
        });
    }

//    public static void post(String url,String jsonParam,final HttpCallBack httpCallBack){
//
//        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
//        final Request.Builder builder = new Request.Builder();
//        RequestBody body = RequestBody.create(JSON, jsonParam);
//        builder.post(body);
//        builder.url(url);
//        final Request request = builder.build();
//        Call httpCall = getOkHttpClientInstance().newCall(request);
//        httpCall.enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                httpCallBack.onFaild(-1,call.toString());
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                String result = response.body().string();
//                try{
//                    JSONObject jsonObject = new JSONObject(result);
//                    int code = jsonObject.optInt("error",-10000);
//                    if (code == 101){
//                        httpCallBack.onSuccess(result);
//                    }else if (code == 104){
//                        httpCallBack.onSuccess(result);
//                    }else{
//                        httpCallBack.onFaild(-2,result);
//                    }
//                }catch (Exception e){
//                    httpCallBack.onFaild(-1,e.getMessage());
//                }
//            }
//        });
//    }

    public static void post(String url,Map param,final HttpCallBack httpCallBack){

        final Request.Builder builder = new Request.Builder();

        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        /**HTTP POST ADD PARAM*/
        Iterator paramIter = param.entrySet().iterator();
        while (paramIter.hasNext()) {
            Map.Entry entry = (Map.Entry) paramIter.next();
            formBodyBuilder.add(String.valueOf(entry.getKey()),String.valueOf(entry.getValue()));
        }

        FormBody body = formBodyBuilder.build();
        builder.post(body);
        builder.url(url);
        final Request request = builder.build();
        Call httpCall = getOkHttpClientInstance().newCall(request);
        httpCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                httpCallBack.onFaild(-1,call.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                try{
                    JSONObject jsonObject = new JSONObject(result);
                    int code = jsonObject.optInt("error",-10000);
                    if (code == 101){
                        httpCallBack.onSuccess(result);
                    }else if (code == 104){
                        httpCallBack.onSuccess(result);
                    }else{
                        httpCallBack.onFaild(-2,result);
                    }
                }catch (Exception e){
                    httpCallBack.onFaild(-1,e.getMessage());
                }
            }
        });
    }

    public static void post(String url,Map param,Map header,final HttpCallBack httpCallBack){

        final Request.Builder builder = new Request.Builder();
        /**HTTP POST ADD HEADER*/
        Iterator iter = header.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            builder.addHeader(String.valueOf(entry.getKey()),String.valueOf(entry.getValue()));
        }

        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        /**HTTP POST ADD PARAM*/
        Iterator paramIter = param.entrySet().iterator();
        while (paramIter.hasNext()) {
            Map.Entry entry = (Map.Entry) paramIter.next();
            formBodyBuilder.add(String.valueOf(entry.getKey()),String.valueOf(entry.getValue()));
        }

        FormBody body = formBodyBuilder.build();
        builder.post(body);
        builder.url(url);
        final Request request = builder.build();
        Call httpCall = getOkHttpClientInstance().newCall(request);
        httpCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                httpCallBack.onFaild(-1,call.toString());
            }

            /**
             *
             * @param call
             * @param response
             * @throws IOException
             */
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                try{
                    JSONObject jsonObject = new JSONObject(result);
                    int code = jsonObject.optInt("error",-10000);
                    if (code == 101){
                        httpCallBack.onSuccess(result);
                    }else if (code == 104){
                        httpCallBack.onSuccess(result);
                    }else{
                        httpCallBack.onFaild(-2,result);
                    }
                }catch (Exception e){
                    httpCallBack.onFaild(-1,e.getMessage());
                }
            }
        });
    }

    /**
     *上传文件
     * @param actionUrl 接口地址
     * @param paramsMap 参数
     * @param httpCallBack 回调
     */
    public static void postLoadFile(String actionUrl, HashMap<String, Object> paramsMap, final HttpCallBack httpCallBack) {
        try {
            final MediaType stream = MediaType.parse("application/octet-stream");
            //补全请求地址
            final String requestUrl = actionUrl;
            final MultipartBody.Builder builder = new MultipartBody.Builder();
            //设置类型
            builder.setType(MultipartBody.FORM);
            //追加参数
            for (final String key : paramsMap.keySet()) {
                Object object = paramsMap.get(key);
                if (!(object instanceof File)) {
                    builder.addFormDataPart(key, object.toString());
                } else {
                    File file = (File) object;
                    // TODO: 2017/9/23 图片裁剪
//                    File ccFile = ImageCutSizeUtil.start(file);

                    builder.addFormDataPart(key, file.getName(), RequestBody.create(stream, file));
//                    upFileHttp(requestUrl,builder,httpCallBack);

//                    new ImageCutSizeUtil(new ImageCutSizeUtil.ImageCutCallBack() {
//                        @Override
//                        public void onSuccess(File file) {
//                            if (file == null){
//                            }else{
//                            }
//                            builder.addFormDataPart(key, file.getName(), RequestBody.create(stream, file));
//
//                            upFileHttp(requestUrl,builder,httpCallBack);
//                        }
//                    }).start(file);



//                    if (ccFile == null){
//                        DDLog.d("图片裁剪失败");
//                    }else{
//
//                        DDLog.d("图片裁剪成功====>" + ccFile.getAbsolutePath());
//                    }
//                    builder.addFormDataPart(key, ccFile.getName(), RequestBody.create(stream, ccFile));
                }
            }
            upFileHttp(requestUrl,builder,httpCallBack);
        } catch (Exception e) {

        }
    }

    private static void upFileHttp(String requestUrl, MultipartBody.Builder builder, final HttpCallBack httpCallBack){

        //创建RequestBody
        RequestBody body = builder.build();
        //创建Request
        final Request request = new Request.Builder().url(requestUrl).post(body).build();
        //单独设置参数 比如读取超时时间
        final Call call = getOkHttpClientInstance().newBuilder().writeTimeout(50, TimeUnit.SECONDS).build().newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                httpCallBack.onFaild(-1,"文件上传失败:" + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String result = response.body().string();
                httpCallBack.onSuccess(result);
//                try{
//                    JSONObject jsonObject = new JSONObject(result);
//                    int code = jsonObject.optInt("error",-10000);
//                    if (code == 101){
//                        httpCallBack.onSuccess(result);
//                    }else if (code == 104){
//                        httpCallBack.onSuccess(result);
//                    }else{
//                        httpCallBack.onFaild(-2,result);
//                    }
//                }catch (Exception e){
//                    httpCallBack.onFaild(-1,e.getMessage());
//                }

            }
        });

    }

}
