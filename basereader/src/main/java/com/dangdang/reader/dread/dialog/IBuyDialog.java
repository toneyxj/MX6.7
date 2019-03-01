package com.dangdang.reader.dread.dialog;

import com.dangdang.reader.dread.format.part.PartBuyInfo;

/**
 * Created by liuboyu on 2015/1/14.
 */
public interface IBuyDialog {
    void setBuyInfo(PartBuyInfo info, boolean isPre);

    void setBalanceInfo(int main, int sub,int type);
}
