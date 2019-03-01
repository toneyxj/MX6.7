package com.moxi.bookstore.bean;

import java.util.List;

/**
 * Created by Administrator on 2016/11/22.
 * 充值界面数据
 */

public class ChargeData extends Data {
    private List<ChargeInfor> activityInfos;

    public List<ChargeInfor> getActivityInfos() {
        return activityInfos;
    }

    public void setActivityInfos(List<ChargeInfor> activityInfos) {
        this.activityInfos = activityInfos;
    }
}
