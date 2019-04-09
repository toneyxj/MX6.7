package com.moxi.biji.intf;

import com.evernote.client.android.type.NoteRef;

import java.util.List;

/**
 * 处理事件同步事件回调
 * Created by Administrator on 2019/2/28.
 */
public interface BackImp {
    /**
     * 开始
     */
    void start();

    /**
     * 结束
     */
    void result();

    /**
     * 去重复
     * @param refs
     * @param title
     */
    void removeRepeat(List<NoteRef> refs, String title);

    /**
     * 错误
     */
    void error(Exception e);
}
