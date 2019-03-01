package com.dangdang.reader.request;

import android.os.Bundle;
import android.os.Handler;

import com.alibaba.fastjson.JSONObject;
import com.dangdang.zframework.network.command.OnCommandListener.NetResult;

/**
 * Created by liuboyu on 2014/11/29.
 */
public class GetAllChapterByMediaIdRequest extends BaseStringRequest {

    private String pId;
    private int start;
    private int count;
    private Handler mHandler;
    private int pickStart, pickCount;
    private IDirCallback callback;

    public GetAllChapterByMediaIdRequest(String pId, int start, int count,
                                         IDirCallback callback, int pickStart, int pickCount) {
        super();
        this.pId = pId;
        this.start = start;
        this.count = count;
        this.callback = callback;
        this.pickStart = pickStart;
        this.pickCount = pickCount;
        setToMainThread(false);
    }

//    @Override
//    public String getUrl() {
//        String url = "http://e.dangdang.com/media/api.go?action=getAllChapterByMediaId&mediaId=" + pId;
//        return url;
//    }

    @Override
    public String getAction() {
        return "getAllChapterByMediaId";
    }

    @Override
    public void appendParams(StringBuilder buff) {
//        buff.append("&mediaId=");
//        buff.append(pId);
    }

    @Override
    public String getPost() {
        StringBuilder buff = new StringBuilder();

        buff.append("&mediaId=");
        buff.append(pId);
        if (count != 0) {
            buff.append("&start=");
            buff.append(start);
            buff.append("&count=");
            buff.append(count);
        }

        return buff.toString();
    }

    @Override
    protected void onRequestSuccess(NetResult netResult,
                                    JSONObject jsonObject) {
//        Message msg = new Message();
//        msg.arg1 = StringUtil.parseInt(expCode.statusCode, -1);
//        msg.what = ChapterLoadService.GET_CHAPTER_LIST_MSG;
//        if (msg.arg1 == 0) {
//            msg.obj = jsonObject;
//        } else {
//            msg.obj = expCode;
//        }
        Bundle data = new Bundle();
        data.putInt("start", start);
        data.putInt("count", count);
        data.putString("mediaId", pId);
        data.putInt("pickStart", pickStart);
        data.putInt("pickCount", pickCount);
//        msg.setData(data);
        if (callback != null) {
            callback.onSuccess(jsonObject, data);
        }
//        mHandler.sendMessage(msg);
    }

    @Override
    protected void onRequestFailed(NetResult netResult, JSONObject jsonObject) {
        if (callback != null) {
            callback.onFailed(-1, "onRequestFailed");
        }
    }

    public interface IDirCallback {
        void onSuccess(JSONObject jsonResult, Bundle data);

        void onFailed(int errorCode, String msg);
    }

}
