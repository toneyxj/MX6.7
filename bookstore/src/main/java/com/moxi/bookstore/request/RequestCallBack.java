package com.moxi.bookstore.request;

/**
 * Created by Administrator on 2016/2/17.
 */
public interface RequestCallBack {
    /*
	 *
	 * 请求成功回调
	 */
    public void onSuccess(String result, String code);

    /**
     *  请求失败回调
     * @param code 请求标识
     * @param showFail 是否显示失败界面
     * @param failCode 失败原因；0数据请求失败，1未知错误，2网络未连接
     * @param msg 错误描述
     */
    public void onFail(String code, boolean showFail, int failCode, String msg,String result);
}
