package com.dangdang.reader.request;

import android.os.Handler;
import android.os.Message;

import com.alibaba.fastjson.JSONObject;
import com.dangdang.reader.utils.DangdangConfig;
import com.dangdang.zframework.network.command.OnCommandListener;

/**
 * Created by liupan on 2015/7/23.
 * 提交阅读进度到云端
 */
public class UpdateBookCloudReadProgressRequest extends BaseStringRequest {
    public static String ACTION = "updateBookCloudSyncReadProgressInfo";
    private String mProgressInfo;
    private Handler mHandler;

    public UpdateBookCloudReadProgressRequest(String progressInfo, Handler handler) {
        mProgressInfo = progressInfo;
        mHandler = handler;
    }

    @Override
    public String getAction() {
        return ACTION;
    }

    @Override
    public String getPost() {//adef31566e580ee3f3a532806027d130   bf6e18963fcc7152b81e83725967e123
        return  "&progressInfo=" + mProgressInfo;
    }

//    @Override
//    public String getUrl() {
//        return DangdangConfig.SERVER_MOBILE_BOOK_CLOUD_API2_URL+"action="+getAction();
////        return "http://10.255.223.227:8090/mobile/api2.do?action="+getAction();
////        return "http://10.255.223.131/mobile/api2.do?action="+getAction();
////        return "http://192.168.132.73:8080/mobile/api2.do?action="+getAction();
//    }

    @Override
    public void appendParams(StringBuilder buff) {
    }

    @Override
    protected void onRequestSuccess(OnCommandListener.NetResult netResult, JSONObject jsonObject) {
        if(mHandler != null){
            Message msg = mHandler.obtainMessage(RequestConstants.MSG_WHAT_REQUEST_DATA_SUCCESS);
            result.setResult(jsonObject);
            msg.obj = result;
            mHandler.sendMessage(msg);
        }
    }

    @Override
    protected void onRequestFailed(OnCommandListener.NetResult netResult, JSONObject jsonObject) {
        if(mHandler != null){
            Message msg = mHandler.obtainMessage(RequestConstants.MSG_WHAT_REQUEST_DATA_FAIL);
            result.setExpCode(expCode);
            msg.obj = result;
            mHandler.sendMessage(msg);
        }
    }
}
