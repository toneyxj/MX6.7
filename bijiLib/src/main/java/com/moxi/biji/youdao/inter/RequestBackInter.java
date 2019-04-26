package com.moxi.biji.youdao.inter;

public interface RequestBackInter {
    void onStart(int code);
    void onSucess(Object value, int code);
    void onFail(Exception e, int code);
}
