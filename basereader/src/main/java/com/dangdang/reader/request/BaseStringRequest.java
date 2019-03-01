package com.dangdang.reader.request;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.dangdang.reader.DDApplication;
import com.dangdang.reader.R;
import com.dangdang.reader.utils.DangdangConfig;
import com.dangdang.reader.utils.Utils;
import com.dangdang.zframework.network.RequestConstant.HttpMode;
import com.dangdang.zframework.network.command.OnCommandListener;
import com.dangdang.zframework.network.command.OnCommandListener.NetResult;
import com.dangdang.zframework.network.command.Request;
import com.dangdang.zframework.network.command.StringRequest;

import java.net.URLEncoder;


/**
 * Created by liuboyu on 2014/11/29.
 */
public abstract class BaseStringRequest extends StringRequest {

    protected boolean success = true;
    protected ResultExpCode expCode;
    protected String TAG;
    protected long mTime;
    protected RequestResult result;
    protected String data;

    public BaseStringRequest(int timeOut) {
        super(timeOut, null);
        init();
    }

    public BaseStringRequest() {
        super(30000, null);
        init();
    }


    private void init() {
        TAG = this.getClass().getName();
        setOnCommandListener(mCommandListener);
        initRequestResult();
    }

    private void initRequestResult() {
        result = new RequestResult();
        result.setAction(getAction());
    }

    public String getUrl() {
        StringBuilder buff = new StringBuilder(DangdangConfig.SERVER_MEDIA_API2_URL);
        buff.append("action=");
        buff.append(getAction());
        appendParams(buff);
        setUrl(buff.toString());
        return buff.toString();
    }

    @Override
    public HttpMode getHttpMode() {
        return HttpMode.POST;
    }

    public abstract String getAction();

    public abstract void appendParams(StringBuilder buff);

    public Object setResponseExpCode(String str) throws JSONException {
        data = str;
        JSONObject jsonO = JSON.parseObject(str);
        JSONObject statusJson = jsonO.getJSONObject("status");
        if (statusJson == null) {
            return null;
        }
        String statusCode = statusJson.getString("code");
        expCode.statusCode = statusCode;
        if ("0".equals(statusCode)) {
            success = true;
            jsonO = jsonO.getJSONObject("data");
        } else {
            success = false;
            expCode.errorCode = statusJson.getString("code");
            expCode.errorMessage = statusJson.getString("message");
            result.setExpCode(expCode);
        }
        setServerTime(jsonO);

        return jsonO;
    }

    protected void setServerTime(JSONObject json) {
        try {
            if (json.containsKey("data"))
                json = json.getJSONObject("data");
            mTime = json.getLong("systemDate");
            Utils.serverTime = mTime;
            Utils.localTime = System.currentTimeMillis();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected long getServerTime() {
        return mTime;
    }

    public boolean isSuccess() {
        return success;
    }

    public ResultExpCode getExpCode() {
        return expCode;
    }

    private OnCommandListener<String> mCommandListener = new OnCommandListener<String>() {
        @Override
        public void onSuccess(String data, NetResult netResult) {
            expCode = new ResultExpCode();
            try {
                JSONObject responseJson = (JSONObject) setResponseExpCode(data);
                if (isSuccess()) {
                    onRequestSuccess(netResult, responseJson);
                } else {
                    onRequestFailed(netResult, responseJson);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailed(NetResult netResult) {
            expCode = new ResultExpCode();
            expCode.errorCode = ResultExpCode.ERRORCODE_NONET;
            expCode.errorMessage = DDApplication.getApplication().getString(R.string.error_no_net);
            result.setExpCode(expCode);
            onRequestFailed(netResult, null);
        }
    };

    protected String encode(String str) {
        try {
            return URLEncoder.encode(str, "utf-8");
        } catch (Exception e) {
            return "";
        }
    }

    protected int getErrCode(String code) {
        try {
            return Integer.valueOf(code);
        } catch (Exception e) {
            return Request.HTTP_FAIL_UNKNOW;
        }
    }

    @Override
    public boolean isTrustAllHost() {
        return true;
//        return !DangdangConfig.isOnLineEnv();
    }

    /**
     * 接口请求成功回调
     *
     * @param netResult
     * @param jsonObject
     */
    protected abstract void onRequestSuccess(NetResult netResult, JSONObject jsonObject);

    /**
     * 接口请求失败
     *
     * @param netResult
     * @param jsonObject
     */
    protected abstract void onRequestFailed(NetResult netResult, JSONObject jsonObject);
}
