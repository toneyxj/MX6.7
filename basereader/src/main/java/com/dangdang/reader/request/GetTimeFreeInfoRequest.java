package com.dangdang.reader.request;

import android.os.Handler;
import android.os.Message;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dangdang.reader.domain.TimeFreeInfo;
import com.dangdang.zframework.network.command.OnCommandListener;

/**
 * Created by liupan on 2015/8/6.
 * 获取限免信息接口
 */
public class GetTimeFreeInfoRequest extends BaseStringRequest {
    public static String ACTION = "getTimeFreeInfo";
    private String mMediaId;
    private Handler mHandler;

    public GetTimeFreeInfoRequest(String mediaId, Handler handler) {
        mMediaId = mediaId;
        mHandler = handler;
    }

    @Override
    public String getAction() {
        return ACTION;
    }

    @Override
    public String getPost() {
        return "&mediaId=" + mMediaId;
    }

    @Override
    public void appendParams(StringBuilder buff) {

    }

    @Override
    protected void onRequestSuccess(OnCommandListener.NetResult netResult, JSONObject jsonObject) {
        TimeFreeInfo timeFreeInfo = parseData(jsonObject);
        if (timeFreeInfo == null) {
            dealRequestDataFail();
        } else {
            result.setResult(timeFreeInfo);
            Message msg = mHandler.obtainMessage(RequestConstants.MSG_WHAT_REQUEST_DATA_SUCCESS, result);
            mHandler.sendMessage(msg);
        }
    }

    private TimeFreeInfo parseData(JSONObject jsonObject) {
        if (jsonObject == null) {
            return null;
        }
        try {
            return JSON.parseObject(jsonObject.toString(), TimeFreeInfo.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onRequestFailed(OnCommandListener.NetResult netResult, JSONObject jsonObject) {
        if (mHandler != null) {
            Message msg = mHandler.obtainMessage(RequestConstants.MSG_WHAT_REQUEST_DATA_FAIL);
            result.setExpCode(expCode);
            msg.obj = result;
            mHandler.sendMessage(msg);
        }
    }

    private void dealRequestDataFail() {
        if (mHandler != null) {
            Message msg = mHandler.obtainMessage(RequestConstants.MSG_WHAT_REQUEST_DATA_FAIL);
            result.setExpCode(expCode);
            msg.obj = result;
            mHandler.sendMessage(msg);
        }
    }
}
