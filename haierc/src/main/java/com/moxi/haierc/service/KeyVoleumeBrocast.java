package com.moxi.haierc.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;

import com.moxi.haierc.util.ToastVolumeUtils;
import com.mx.mxbase.constant.APPLog;
import com.mx.mxbase.utils.StartActivityUtils;

import xj.systemfunc.Tools;

/**
 * Created by xj on 2018/11/16.
 */

public class KeyVoleumeBrocast extends BroadcastReceiver {

    private static long time=0;
    private static String action="";
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(StartActivityUtils.plus)||StartActivityUtils.minus.equals(intent.getAction())) {
            if (!Tools.ishandwritestate()) {
                if (action.equals("")) {
                    action=intent.getAction();
                    time=System.currentTimeMillis();
                }else {
                    long curtime = System.currentTimeMillis();
                    if (Math.abs(curtime-time)<200&&!intent.getAction().equals(action)){
                        ToastVolumeUtils.getInstance(context).hide();
                        return;
                    }
                }
                AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                APPLog.e("currentVolume",currentVolume);
                if (StartActivityUtils.minus.equals(intent.getAction())){
                    //down
                    currentVolume-=1;
                }else {
                    //up,加音量
                    currentVolume+=1;
                }

                if (currentVolume<0)currentVolume=0;
                if (currentVolume>15)currentVolume=15;
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, AudioManager.FLAG_PLAY_SOUND);
                ToastVolumeUtils.getInstance(context).showToast(currentVolume);
            }
        }
    }
}
