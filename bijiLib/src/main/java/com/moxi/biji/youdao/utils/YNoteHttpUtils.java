package com.moxi.biji.youdao.utils;

import com.moxi.biji.BijiUtils;
import com.moxi.biji.mdoel.BiJiModel;
import com.moxi.biji.youdao.config.URLUtils;
import com.moxi.biji.youdao.config.YouDaoInfo;
import com.moxi.biji.youdao.inter.RequestBackInter;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import net.oauth.OAuthAccessor;
import net.oauth.OAuthException;
import net.oauth.OAuthMessage;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class YNoteHttpUtils {
    private static final String HEARDCODE = "Authorization";

    /**
     * 获取用户信息 返回code值为：1
     *
     * @param backInter
     */
    public static void getUser(final RequestBackInter backInter) {
        try {
            if (backInter != null) backInter.onStart(1);
            OkHttpUtils.get()
                    .addHeader(HEARDCODE, getAuthorizationHeader(URLUtils.User_url, OAuthMessage.GET, null, YouDaoInfo.getInstance().getAccessor()))
                    .url(URLUtils.User_url).build().execute(new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    if (backInter != null) backInter.onFail(e, 1);
                }

                @Override
                public void onResponse(String response, int id) {
                    if (backInter != null) backInter.onSucess(response, 1);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static final String TAG = "createBook";

    /**
     * 创建笔记本 返回code值为：2
     *
     * @param backInter
     */
    public static void createBook(final RequestBackInter backInter, final String bookName) {
        try {
            if (backInter != null) backInter.onStart(2);
            OkHttpUtils.post()
                    .addHeader(HEARDCODE, getAuthorizationHeader(URLUtils.createBook, OAuthMessage.POST, null, YouDaoInfo.getInstance().getAccessor()))
                    .addParams("name", bookName)
                    .url(URLUtils.createBook).build().execute(new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    if (backInter != null) backInter.onFail(e, 2);
                    e.printStackTrace();
                }

                @Override
                public void onResponse(String response, int id) {
                    if (backInter != null) backInter.onSucess(response, 2);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取所有笔记本
     *
     * @param backInter
     */
    public static void allBook(final RequestBackInter backInter) {
        try {
            if (backInter != null) backInter.onStart(3);
            OkHttpUtils.post()
                    .addHeader(HEARDCODE, getAuthorizationHeader(URLUtils.getAllBook, OAuthMessage.POST, null, YouDaoInfo.getInstance().getAccessor()))
                    .url(URLUtils.getAllBook).build().execute(new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    if (backInter != null) backInter.onFail(e, 3);
                    e.printStackTrace();
                }

                @Override
                public void onResponse(String response, int id) {
                    if (backInter != null) backInter.onSucess(response, 3);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取所有笔记本下面的所有笔记
     *
     * @param backInter
     */
    public static void allBooknotes(final RequestBackInter backInter, String notebook) {
        try {
            if (backInter != null) backInter.onStart(4);
            OkHttpUtils.post()
                    .addHeader(HEARDCODE, getAuthorizationHeader(URLUtils.getAllBooknotes, OAuthMessage.POST, null, YouDaoInfo.getInstance().getAccessor()))
                    .addParams("notebook", notebook)
                    .url(URLUtils.getAllBooknotes).build().execute(new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    if (backInter != null) backInter.onFail(e, 4);
                    e.printStackTrace();
                }

                @Override
                public void onResponse(String response, int id) {
                    if (backInter != null) backInter.onSucess(response, 4);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 上传图片
     *
     * @param backInter
     */
    public static void upPhoto(final RequestBackInter backInter, BiJiModel model, String bookPath, String notePath, File... photo) throws Exception {
        backInter.onStart(6);
        List<String> filePath = new ArrayList<>();
        for (File file : photo) {
            String fp = uploadImage(file);
            if (!fp.equals("")) {
                JSONObject obj = new JSONObject(fp);
                String url = obj.getString("url");
                filePath.add(url);
            }
        }
        if (filePath.size()==0){
            backInter.onFail(new Exception("上传图片失败！"),6);
            return ;
        }
        String value= URLUtils.getImageHtml(filePath);
        crateBooknotes(backInter,model,bookPath,notePath,value);
    }



    /**
     * 上传图片
     *
     * @return 新图片的路径
     * @throws IOException
     */
    public static String uploadImage(File file) throws IOException {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS).build();
        RequestBody image = RequestBody.create(MediaType.parse("image/png"), file);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getAbsolutePath(), image)
                .build();
        Request request = new Request.Builder()
                .addHeader(HEARDCODE, getAuthorizationHeader(URLUtils.UPLOAD_PHOTO, OAuthMessage.POST, null, YouDaoInfo.getInstance().getAccessor()))
                .url(URLUtils.UPLOAD_PHOTO)
                .post(requestBody)
                .build();
        Response response = okHttpClient.newCall(request).execute();
        String body=response.body().string();
        if (response.isSuccessful()) {
            return body;
        } else {
            return "";
        }

    }

    /**
     * 创建笔记
     *
     * @param backInter
     */
    public static void crateBooknotes(final RequestBackInter backInter, BiJiModel model, String bookPath, String notePath) {
        crateBooknotes(backInter,model,bookPath,notePath,null);
    }
    public static void crateBooknotes(final RequestBackInter backInter, BiJiModel model, String bookPath, String notePath,String content) {
        try {
            if (backInter != null) backInter.onStart(5);
            MultipartBody.Builder builder = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("title", model.getTitle())
                    .addFormDataPart("create_time", String.valueOf(System.currentTimeMillis() / 1000));
            if (null==content){
                builder.addFormDataPart("content", BijiUtils.readFile(model.getContent()));
            }else {
                builder.addFormDataPart("content", content);
            }
            if (notePath == null) {
                builder.addFormDataPart("notebook", bookPath);
            } else {
                builder.addFormDataPart("path", notePath);
            }
            Request request = new Request.Builder()
                    .url(URLUtils.createNote)
                    .addHeader(HEARDCODE, getAuthorizationHeader(URLUtils.createNote, OAuthMessage.POST, null, YouDaoInfo.getInstance().getAccessor()))
                    .post(builder.build())
                    .build();

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS).build();

            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    if (backInter != null) backInter.onFail(e, 5);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        if (backInter != null) backInter.onSucess(response.body().string(), 5);
                    } else {
                        if (backInter != null)
                            backInter.onFail(new Exception(response.body().string()), 5);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            if (backInter != null) backInter.onFail(e, 5);
        }
    }


    /**
     * Get the OAuth authorization header for the given url, parameters and
     * accessor.
     *
     * @param url
     * @param parameters
     * @param accessor
     * @return
     * @throws IOException
     */
    private static String getAuthorizationHeader(String url, String method,
                                                 Map<String, String> parameters, OAuthAccessor accessor)
            throws IOException {
        OAuthMessage message = null;
        try {
            message = accessor.newRequestMessage(method,
                    url, parameters == null ? null : parameters.entrySet());
            return message.getAuthorizationHeader(null);
        } catch (OAuthException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }
}
