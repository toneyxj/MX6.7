package com.moxi.bookstore.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.dangdang.reader.moxiUtils.BrodcastUtils;
import com.moxi.bookstore.db.TableOperate;

/**
 * 更新书籍信息
 * Created by Administrator on 2016/11/29.
 */
public class RefureshBookInodrmationBrodcastrecever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
//        if (intent.getAction()
//                .equals("RefureshBookInodrmationBrodcastrecever")) {
            String id=intent.getStringExtra(BrodcastUtils.ID);
            String progress=intent.getStringExtra(BrodcastUtils.PROGRESS);
            //获得当前阅读的id
            TableOperate.getInstance().updataTime(id,progress);
//        }
    }
}
