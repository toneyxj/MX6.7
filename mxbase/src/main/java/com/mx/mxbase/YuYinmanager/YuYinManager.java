package com.mx.mxbase.YuYinmanager;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.mx.mxbase.constant.APPLog;

/**
 * Created by xiajun on 2019/5/10.
 */

public class YuYinManager {
    private Activity context;
    private YuYinCallBack callBack;
    private long time=0;

    public void setCallBack(YuYinCallBack callBack) {
        this.callBack = callBack;
    }

    public YuYinManager(Activity context, YuYinCallBack callBack) {
        this.context = context;
        this.callBack = callBack;
        //初始化广播监听
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MsgConfig.speek_send_error);
        intentFilter.addAction(MsgConfig.speek_send_over);
        //当网络发生变化的时候，系统广播会发出值为android.net.conn.CONNECTIVITY_CHANGE这样的一条广播
        context.registerReceiver(receiver, intentFilter);
    }

    public void SendYuYinMsg(String msg) {
        if (!mBond){
            if (callBack!=null)callBack.onYuYinFail("语音服务绑定失败");
            bindServiceInvoked();
            return;
        }else if (msg==null||msg.trim().equals("")){
            if (callBack!=null)callBack.onYuYinFail("无可阅读内容");
            return;
        }
        Message clientMessage = Message.obtain();
        clientMessage.what = MsgConfig.sendMsg;
        Bundle bundle = new Bundle();
        bundle.putString("data", msg);
        clientMessage.setData(bundle);
        try {
            serverMessenger.send(clientMessage);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止语音播放
     */
    public boolean stopYuYinMsg() {
        long cur=System.currentTimeMillis();
        if ((cur-time)<1000)return false;
        time=cur;
        if (!mBond){
            if (callBack!=null)callBack.onYuYinFail("语音服务绑定失败");
            bindServiceInvoked();
            return false;
        }
        Message clientMessage = Message.obtain();
        clientMessage.what = MsgConfig.speekStop;
        try {
            serverMessenger.send(clientMessage);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 链接成功
     */
    private boolean mBond = false;

    private Messenger serverMessenger;

    private ServiceConnection conn = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //连接成功
            serverMessenger = new Messenger(service);
            APPLog.e("YuYinManager", "服务连接成功");
            mBond = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serverMessenger = null;
            mBond = false;
        }
    };

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (callBack == null) return;
            String what = intent.getAction();
            if (what.equals(MsgConfig.speek_send_over)) {
                callBack.onYuYinOver();
            } else if (what.equals(MsgConfig.speek_send_error)) {
                callBack.onYuYinFail(intent.getStringExtra("error"));
            } else if (what.equals(MsgConfig.speek_send_start)) {
                callBack.onYuYinStart();
            }

        }
    };

    public void bindServiceInvoked() {
        try {
            APPLog.e("bindServiceInvoked");
            Intent intent = new Intent();
            intent.setAction("com.baidu.yuyinhecheng.YuYinService");
            context.startService(intent);
            context.bindService(intent, conn, Context.BIND_AUTO_CREATE);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void onDestroy() {
        if (mBond) {
            stopYuYinMsg();
            context.unbindService(conn);
        }
        context.unregisterReceiver(receiver);
    }
}
