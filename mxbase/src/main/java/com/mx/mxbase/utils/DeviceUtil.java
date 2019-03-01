package com.mx.mxbase.utils;

import android.os.Build;

import java.lang.reflect.Method;

/**
 * Created by Archer on 16/9/27.
 */
public class DeviceUtil {

    /**
     * 获取设备serial
     *
     * @return
     */
    public static String getDeviceSerial( ) {
//        String path="data/sn";
//        String str  = "unknow";
//        try {
//            BufferedReader br=new BufferedReader(new FileReader(path));
//            str = br.readLine().trim();
//            br.close();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        String str= Build.SERIAL;
        if (StringUtils.isNull(str)){
            str="unknow";
        }
        return str;
    }
    private static String getAndroidOsSystemProperties(String key) {
        String ret = "";
        try {
            Method systemProperties_get = Class.forName("android.os.SystemProperties").getMethod("get", String.class);
            if(!systemProperties_get.isAccessible()){
                systemProperties_get.setAccessible(true);
            }
            ret = (String) systemProperties_get.invoke(null, key);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(null == ret){
            ret = "";
        }
        return ret;
    }
    /**
     * 是否为合法手机号
     *
     * @param mobile 手机号
     * @return
     */
    public static boolean isMobile(String mobile) {
        if (StringUtils.isNull(mobile)){
            return false;
        }
        if (mobile.length()!=11){
            return false;
        }
        if (!mobile.substring(0,1).equals("1")){
            return false;
        }
        return true;

//        boolean flag = false;
//        if (mobile.length() == 0) {
//            return false;
//        }
//        String[] mobiles = mobile.split(",");
//        int len = mobiles.length;
//        if (len == 1) {
//            return Pattern.matches("^((13[0-9])|(14[5,7,9])|(15[^4,\\D])|(17[0,1,3,5-8])|(18[0-9]))\\d{8}$", mobile);
//        } else {
//            for (int i = 0; i < len; i++) {
//                if (isMobile(mobiles[i])) {
//                    flag = true;
//                } else {
//                    flag = false;
//                }
//            }
//        }
//        return flag;
    }
}
