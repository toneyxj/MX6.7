package com.dangdang.reader.dread.util;

import android.text.TextUtils;

import com.dangdang.reader.dread.jni.DrmWarp;
import com.mx.mxbase.constant.APPLog;

import java.net.URLEncoder;

/**
 * drm证书相关所用工具
 * Created by Yhyu on 2015/5/21.
 */
public class DrmWrapUtil {
    /**
     * 获取当前设备的公钥，已经经过url编码
     * @return
     */
    public static String getPublicKey(){
        DrmWarp drmWarp = DrmWarp.getInstance();
        int success = drmWarp.getPublicKeyN();
        String pubKey = "";
        if (success != DrmWarp.FAILED) {
            pubKey = drmWarp.getPublicKey();
        }
        /*try {
            pubKey = URLEncoder.encode(pubKey, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        return pubKey;
    }

    /**
     * 将证书转换为byte数组
     * @param str
     * @return
     */
    public static byte[] getPartBookCertKey(String str) {
    	byte[] key;
    	if(TextUtils.isEmpty(str))
    		key = new byte[1];
    	else
    		key = str.getBytes();
        DrmWarp drmWarp = DrmWarp.getInstance();
       int drawpass= drmWarp.enCrypt(key);
        APPLog.e("getPartBookCertKey",drawpass);
        return drmWarp.getEnCryptData();
    }
}
