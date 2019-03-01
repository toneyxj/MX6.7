package com.moxi.haierc.messenger;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.text.TextUtils;

import com.moxi.haierc.model.LockPassWord;
import com.mx.mxbase.utils.GsonTools;
import com.mx.mxbase.utils.SharePreferceUtil;
import com.mx.mxbase.utils.StringUtils;

/**
 * Created by King on 2017/12/18.
 */

public class MessengerService extends Service {
    private static final int MSG_SUM = 0x110;

    private Messenger mMessenger = new Messenger(new Handler() {
        @Override
        public void handleMessage(Message msgfromClient) {
            Message msgToClient = Message.obtain(msgfromClient);//返回给客户端的消息
            switch (msgfromClient.what) {
                case MSG_SUM:
                    msgToClient.what = MSG_SUM;
                    try {
                        Bundle bundle = new Bundle();
                        String obj;
                        if (msgfromClient.getData()==null){
                            obj="lock_device_of_write_node";
                        }else {
                            String tag=msgfromClient.getData().getString("tag");
                            if (StringUtils.isNull(tag)){
                                obj="lock_device_of_write_node";
                            }else {
                                obj = tag;
                            }
                        }
                        String writeNote = SharePreferceUtil.getInstance(MessengerService.this).getString(obj);
                        String devicePsw = SharePreferceUtil.getInstance(MessengerService.this).getString("lock_device_info");
                        if (!TextUtils.isEmpty(writeNote)) {
                            LockPassWord lockDevice = GsonTools.getPerson(devicePsw, LockPassWord.class);
                            LockPassWord lockWrite = GsonTools.getPerson(writeNote, LockPassWord.class);
                            lockWrite.setPassword(lockDevice.getPassword());
                            bundle.putSerializable("password", GsonTools.obj2json(lockWrite));
                        } else {
                            if (!TextUtils.isEmpty(devicePsw)) {
                                LockPassWord lockPassWord = GsonTools.getPerson(devicePsw, LockPassWord.class);
                                lockPassWord.setIsOpen(0);
                                bundle.putSerializable("password", GsonTools.obj2json(lockPassWord));
                            } else {
                                bundle.putSerializable("password", "");
                            }
                        }
                        msgToClient.setData(bundle);
                        msgfromClient.replyTo.send(msgToClient);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
            }

            super.handleMessage(msgfromClient);
        }
    });

    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }

}

