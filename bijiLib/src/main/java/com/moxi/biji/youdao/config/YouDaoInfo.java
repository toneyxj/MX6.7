package com.moxi.biji.youdao.config;

import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;

public class YouDaoInfo {
    private static YouDaoInfo instance;

    public static YouDaoInfo getInstance() {
        if (instance == null) {
            synchronized (YouDaoInfo.class) {
                if (instance == null)
                    instance = new YouDaoInfo();
            }
        }
        return instance;
    }

    private String code=null;
    private String accessToken=null;
    private String state="112";
    private String backUrl="http://120.25.193.163/app/adSite/getAdImage/2/json";
    private OAuthAccessor accessor;

    public String getCode() {
        return code;
    }
    public boolean isNullCode(){
        return code==null||code.equals("");
    }
    public boolean isNullAccessToken(){
        return accessToken==null||accessToken.equals("");
    }
    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getState() {
        return state;
    }

    public String getBackUrl() {
        return backUrl;
    }

    public OAuthAccessor getAccessor(){
        if (accessor==null){
            OAuthConsumer consumer=new OAuthConsumer(backUrl,URLUtils.consumerKey,URLUtils.consumerSecret,null);
            accessor=new OAuthAccessor(consumer);
            accessor.accessToken=accessToken;
        }
        return accessor;
    }
}
