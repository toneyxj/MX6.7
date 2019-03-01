package com.moxi.wechatshare.callback;

import android.graphics.Bitmap;

/**
 * Created by along on 2017/12/19.
 */

public interface QrcodeCallBack {

    public void callBack(Bitmap bitmap);
    void backFail(String msg);

}
