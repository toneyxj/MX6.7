package com.dangdang.reader.request;

import android.os.Handler;
import android.os.Message;

import com.alibaba.fastjson.JSONObject;
import com.dangdang.reader.domain.CloudReadProgress;
import com.dangdang.reader.utils.DangdangConfig;
import com.dangdang.zframework.network.command.OnCommandListener;

/**
 * Created by liupan on 2015/7/24.
 * 获取云端阅读进度
 */
public class GetBookCloudReadProgressRequest extends BaseStringRequest {
    public static String ACTION = "getBookCloudSyncReadProgressInfo";
    private String mProductId;
    private Handler mHandler;

    public GetBookCloudReadProgressRequest(String productId, Handler handler) {
        mProductId = productId;
        mHandler = handler;
    }

    @Override
    public String getAction() {
        return ACTION;
    }

    @Override
    public String getPost() {
        return "&productId=" + mProductId;
    }

//    @Override
//    public String getUrl() {
//        return DangdangConfig.SERVER_MOBILE_BOOK_CLOUD_API2_URL+"action="+getAction();
////        return "http://10.255.223.227:8090/mobile/api2.do?action="+getAction();
////        return "http://10.255.223.131/mobile/api2.do?action=" + getAction();
////        return "http://192.168.132.73:8080/mobile/api2.do?action="+getAction();
//    }

    @Override
    public void appendParams(StringBuilder buff) {
    }

    @Override
    protected void onRequestSuccess(OnCommandListener.NetResult netResult, JSONObject jsonObject) {
        CloudReadProgress cloudProgress = parseData(jsonObject);
        if (cloudProgress == null) {
            dealRequestDataFail();
        } else {
            result.setResult(cloudProgress);
            Message msg = mHandler.obtainMessage(RequestConstants.MSG_WHAT_REQUEST_DATA_SUCCESS, result);
            mHandler.sendMessage(msg);
        }
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

    private CloudReadProgress parseData(JSONObject jsonObject) {
        /**
         * {"data":{"bookReadingProgress":{"chaptersIndex":1,"characterIndex":907,"clientOperateTime":950298564822,"productId":1900603852,"progressId":4},
         * "currentDate":"2014-05-26 14:31:27","systemDate":"1401085887200"},"status":{"code":0},"systemDate":1401085887199}
         */
        if (jsonObject == null) {
            return null;
        }
        try {
            CloudReadProgress cloudProgress = null;
            JSONObject progressJson = jsonObject.getJSONObject("bookReadingProgress");
            if (progressJson != null) {
//                cloudProgress = JSON.parseObject(progressJson.toString(), CloudReadProgress.class);
                cloudProgress = new CloudReadProgress();

                cloudProgress.setProductId(progressJson.getString("productId"));
                cloudProgress.setChapterIndex(progressJson.getInteger("chaptersIndex"));
                cloudProgress.setElementIndex(progressJson.getInteger("characterIndex"));
                cloudProgress.setClientOperateTime(progressJson.getLong("clientOperateTime"));
                cloudProgress.setStartTime(progressJson.getLong("startTime"));
                cloudProgress.setEndTime(progressJson.getLong("endTime"));
            }
            return cloudProgress;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
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
