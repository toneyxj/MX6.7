package com.moxi.bookstore.config;

/**
 * Created by Administrator on 2016/9/12.
 */
public class UserInfomation {
    // 初始化类实列
    private static UserInfomation instatnce = null;

    /**
     * 获得软键盘弹出类实列
     *
     * @return 返回初始化实列
     */
    public static UserInfomation getInstance() {
        if (instatnce == null) {
            synchronized (UserInfomation.class) {
                if (instatnce == null) {
                    instatnce = new UserInfomation();
                }
            }
        }
        return instatnce;
    }
    public String session_id="";
}
