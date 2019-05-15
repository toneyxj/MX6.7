package com.moxi.biji.youdao.config;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

public class URLUtils {
    //    private static final String BASEURL = "http://sandbox.note.youdao.com/oauth";//测试
    private static final String BASEURL = "https://note.youdao.com";//正式
    public static final String consumerKey = "5804d38111a3ddf42b69c728213838ee";
    public static final String consumerSecret = "172dd649a0f064c1c56106a7d6cf5d7b";

    /**
     * 获取用户信息
     */
    public static final String User_url=BASEURL+"/yws/open/user/get.json";
    /**
     * 上传图片
     */
    public static final String UPLOAD_PHOTO=BASEURL+"/yws/open/resource/upload.json";
    /**
     * 创建笔记本
     */
    public static final String createBook=BASEURL+"/yws/open/notebook/create.json";
    /**
     * 获得所有笔记本
     */
    public static final String getAllBook=BASEURL+"/yws/open/notebook/all.json";
    /**
     * 获得笔记本下面的所有笔记
     */
    public static final String getAllBooknotes=BASEURL+"/yws/open/notebook/list.json";
    /**
     * 创建笔记
     */
    public static final String createNote=BASEURL+"/yws/open/note/create.json";

    /**
     * 获取AccessToken
     */
    public static final String AccessTokenURL=BASEURL+"/oauth/access2";
//
    /**
     * 获得code值得url-也是授权url
     * @return codeurl
     */
    public static String getCodeUrl() {
        String value = BASEURL + "/oauth/authorize2?client_id="+consumerKey+"&response_type=code&state="+YouDaoInfo.getInstance().getState()+"&display=mobile&code=&redirect_uri=";

        return value+getUrlEncode();
    }

    public static String getAccessTokenUrl(){
        return AccessTokenURL+"?client_id="+consumerKey+"&client_secret="+consumerSecret+"&grant_type=authorization_code&redirect_uri="+getUrlEncode()+"&code="+YouDaoInfo.getInstance().getCode();
    }
    private static String getUrlEncode(){
        String str="";
        try {
             str = URLEncoder.encode(YouDaoInfo.getInstance().getBackUrl(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return str;
    }


    public static String getImageHtml(List<String> values){
        StringBuilder builder=new StringBuilder();
        for (String va:values){
            builder.append(getOneImageHtml(va));
        }
        return builder.toString();
    }

    public static String getOneImageHtml(String scr){
        return "<img src=\""+scr+"\">";
    }
}
