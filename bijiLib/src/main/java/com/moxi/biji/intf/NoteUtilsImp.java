package com.moxi.biji.intf;

import android.app.Activity;

import com.moxi.biji.mdoel.BiJiModel;

/**
 * Created by Administrator on 2019/2/27.
 */

public interface NoteUtilsImp {
    /**
     * 获得笔记转换后的文本类型
     */
    void sendText(final BiJiModel model, final BackImp imp);
    boolean isLogin(Activity activity);
}
