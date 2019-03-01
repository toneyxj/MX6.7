package com.moxi.remotefilemanager.filedata;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.PowerManager;
import android.util.Log;

import com.moxi.remotefilemanager.service.AndroidWebServer;

/**
 * Created by King on 2017/9/7.
 */

public class RemoteFileService {
    public static RemoteFileService remoteFileService;
    private static boolean isStarted = false;
    private AndroidWebServer androidWebServer;

    public static RemoteFileService getInstance() {
        if (remoteFileService == null) {
            remoteFileService = new RemoteFileService();
        }
        return remoteFileService;
    }

    public boolean startRemoteFileService(Context context) {
        if (!isStarted) {
            try {
                androidWebServer = new AndroidWebServer(8089, context);
                androidWebServer.start();
                isStarted = true;
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean stopAndroidWebServer() {
        if (isStarted && androidWebServer != null) {
            androidWebServer.stop();
            isStarted = false;
            return true;
        }
        isStarted = false;
        return false;
    }

    public String getIpAccess(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        int ipAddress = wifiManager.getConnectionInfo().getIpAddress();
        final String formatedIpAddress = String.format("%d.%d.%d.%d", (ipAddress & 0xff), (ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
        return "http://" + formatedIpAddress + ":8089";
    }

    public boolean isConnectedInWifi(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        NetworkInfo networkInfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected()
                && wifiManager.isWifiEnabled() && networkInfo.getTypeName().equals("WIFI")) {
            return true;
        }
        return false;
    }
    private PowerManager.WakeLock wakeLock;

    public void setPower(Context context) {
        PowerManager pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK
                | PowerManager.ON_AFTER_RELEASE, getClass()
                .getCanonicalName());
        if (null != wakeLock) {
            wakeLock.acquire();
        }
    }
    public void closePower(){
        if (wakeLock != null&&wakeLock.isHeld()){
            wakeLock.release();
        }
        wakeLock=null;
    }
}
