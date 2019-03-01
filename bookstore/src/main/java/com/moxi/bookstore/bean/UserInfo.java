package com.moxi.bookstore.bean;

/**
 * Created by Administrator on 2016/11/14.
 */

public class UserInfo {
    private Long lotTimes;

    private Long mainBalance;

    private int mainBalanceIOS;

    private String nickname;

    private Long subBalance;

    private int subBalanceIOS;

    private String userImgUrl;

    public Long getLotTimes() {
        return lotTimes;
    }

    public void setLotTimes(Long lotTimes) {
        this.lotTimes = lotTimes;
    }

    public Long getMainBalance() {
        return mainBalance;
    }

    public void setMainBalance(Long mainBalance) {
        this.mainBalance = mainBalance;
    }

    public int getMainBalanceIOS() {
        return mainBalanceIOS;
    }

    public void setMainBalanceIOS(int mainBalanceIOS) {
        this.mainBalanceIOS = mainBalanceIOS;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Long getSubBalance() {
        return subBalance;
    }

    public void setSubBalance(Long subBalance) {
        this.subBalance = subBalance;
    }

    public int getSubBalanceIOS() {
        return subBalanceIOS;
    }

    public void setSubBalanceIOS(int subBalanceIOS) {
        this.subBalanceIOS = subBalanceIOS;
    }

    public String getUserImgUrl() {
        return userImgUrl;
    }

    public void setUserImgUrl(String userImgUrl) {
        this.userImgUrl = userImgUrl;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "lotTimes=" + lotTimes +
                ", mainBalance=" + mainBalance +
                ", mainBalanceIOS=" + mainBalanceIOS +
                ", nickname='" + nickname + '\'' +
                ", subBalance=" + subBalance +
                ", subBalanceIOS=" + subBalanceIOS +
                ", userImgUrl='" + userImgUrl + '\'' +
                '}';
    }
}
