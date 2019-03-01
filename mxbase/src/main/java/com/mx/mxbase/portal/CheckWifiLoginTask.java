package com.mx.mxbase.portal;

import android.os.AsyncTask;

import com.mx.mxbase.constant.APPLog;
import com.mx.mxbase.constant.Constant;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by xj on 2018/8/22.
 */

public class CheckWifiLoginTask extends AsyncTask<Integer,Integer,Integer> {


    private ICheckWifiCallBack mCallBack;


    public CheckWifiLoginTask (ICheckWifiCallBack mCallBack){
        super();
        this.mCallBack=mCallBack;
    }


    @Override
    protected Integer doInBackground(Integer... params) {
        return isWifiSetPortal();
    }

    @Override
    protected void onPostExecute(Integer result) {
        if (mCallBack != null) {
            mCallBack.portalNetWork(result);
        }
    }

    /**
     * 验证当前wifi是否需要Portal验证
     * @return
     */
    private Integer isWifiSetPortal() {
        String mWalledGardenUrl =  Constant.HTTP_HOST+"/app/adSite/204";
//        String mWalledGardenUrl = "http://www.google.cn/generate_204";
        // 设置请求超时
        int WALLED_GARDEN_SOCKET_TIMEOUT_MS = 10000;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(mWalledGardenUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setInstanceFollowRedirects(false);
            urlConnection.setConnectTimeout(WALLED_GARDEN_SOCKET_TIMEOUT_MS);
            urlConnection.setReadTimeout(WALLED_GARDEN_SOCKET_TIMEOUT_MS);
            urlConnection.setUseCaches(false);
            urlConnection.getInputStream();
            APPLog.e("urlConnection.getResponseCode()",urlConnection.getResponseCode());
            // 判断返回状态码是否是204
            boolean is= urlConnection.getResponseCode()!=204;
            return is?0:1;
        } catch (IOException e) {
               e.printStackTrace();
            return 2;
        } finally {
            if (urlConnection != null) {
                //释放资源
                urlConnection.disconnect();
            }
        }
    }

    /**
     * 检测Wifi 是否需要portal 认证
     * @param callBack
     */
    public static void checkWifi(ICheckWifiCallBack callBack){
        new CheckWifiLoginTask(callBack).execute();
    }

    public interface ICheckWifiCallBack{
        /**
         *
         * @param type 0:代表网络被劫持，1代表网络请求成功，2代表网络请求出错
         */
        void portalNetWork(int type);
    }

}
