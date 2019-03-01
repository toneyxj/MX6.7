package com.mx.user.application;

import com.mx.mxbase.base.BaseApplication;

/**
 * Created by Archer on 16/8/31.
 */
public class UserApplication extends BaseApplication {

    public static UserApplication userApplication;
    private String flagStu = "";

    public void setFlagStu(String flagStu) {
        this.flagStu = flagStu;
    }

    public String getFlagStu() {
        return flagStu;
    }

    public static UserApplication getInstance() {
        if (userApplication == null) {
            userApplication = new UserApplication();
        }
        return userApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
