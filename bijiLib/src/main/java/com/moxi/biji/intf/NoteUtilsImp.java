package com.moxi.biji.intf;

import android.support.v4.app.FragmentActivity;

import com.moxi.biji.mdoel.BiJiModel;

import java.util.List;

/**
 * Created by Administrator on 2019/2/27.
 */

public interface NoteUtilsImp {
    /**
     * 获得笔记转换后的文本类型
     */
    void sendNote(final BiJiModel model, final BackImp imp);
    <T>void deleteNote(List<T> lists,SucessImp sucessImp);
    boolean isLogin(FragmentActivity activity);
}
