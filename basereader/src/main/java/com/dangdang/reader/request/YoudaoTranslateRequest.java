package com.dangdang.reader.request;

import android.os.Handler;
import android.os.Message;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.dangdang.reader.domain.YoudaoTransResult;
import com.dangdang.zframework.network.RequestConstant;
import com.dangdang.zframework.network.command.OnCommandListener;

/**
 * Created by Yhyu on 2015/8/18.
 */
public class YoudaoTranslateRequest extends BaseStringRequest {
    private String keyword;
    private Handler handler;

    @Override
    public String getAction() {
        return "";
    }

    public YoudaoTranslateRequest(Handler handler) {
        this.handler = handler;
    }

    public void setParamater(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public String getUrl() {
        StringBuilder buff = new StringBuilder("http://fanyi.youdao.com/openapi.do?" +
                "keyfrom=dangdang&key=1623290604" +
                "&type=data&doctype=json&version=1.1&q=");
        buff.append(encode(keyword));
        return buff.toString();
    }

    @Override
    public void appendParams(StringBuilder buff) {

    }

    @Override
    public RequestConstant.HttpMode getHttpMode() {
        return RequestConstant.HttpMode.GET;
    }

    @Override
    protected void onRequestSuccess(OnCommandListener.NetResult netResult, JSONObject jsonObject) {
        YoudaoTransResult result = parse(jsonObject);
        if (result == null) {
            dealRequestDataFail();
        } else {
            dealRequestDataSuccess(result);
        }

    }

    public Object setResponseExpCode(String str)
            throws JSONException {
        JSONObject jsonO = JSON.parseObject(str);
        expCode.statusCode = jsonO.getString("errorCode");
        if ("0".equals(expCode.statusCode)) {
            success = true;
        } else {
            success = false;
            expCode.errorCode = expCode.statusCode;
            expCode.errorMessage = "翻译失败";
            result.setExpCode(expCode);
        }
        return jsonO;
    }

    @Override
    protected void onRequestFailed(OnCommandListener.NetResult netResult, JSONObject jsonObject) {
        dealRequestDataFail();
    }

    private YoudaoTransResult parse(JSONObject dataJson) {
        if (dataJson == null) {
            return null;
        }
        try {
            YoudaoTransResult result = new YoudaoTransResult();
            result.setQuery(dataJson.getString("query"));
            JSONArray translation = dataJson.getJSONArray("translation");
            if (translation != null) {
                int size = translation.size();
                for (int i = 0; i < size; ++i) {
                    result.addTranslation(translation.getString(i));
                }
            }
            JSONObject basic = dataJson.getJSONObject("basic");
            if (basic != null) {
                result.setPhonetic(basic.getString("phonetic"));
                JSONArray explains = basic.getJSONArray("explains");
                if (explains != null) {
                    int size = explains.size();
                    for (int i = 0; i < size; ++i) {
                        result.addBasicExplains(explains.getString(i));
                    }
                }
            }

            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void dealRequestDataSuccess(YoudaoTransResult youdaoTransResult) {
        if (handler != null) {
            Message msg = handler.obtainMessage(RequestConstants.MSG_WHAT_REQUEST_DATA_SUCCESS);
            result.setResult(youdaoTransResult);
            msg.obj = result;
            handler.sendMessage(msg);
        }
    }

    private void dealRequestDataFail() {
        if (handler != null) {
            Message msg = handler.obtainMessage(RequestConstants.MSG_WHAT_REQUEST_DATA_FAIL);
            result.setExpCode(expCode);
            result.setResult(null);
            msg.obj = result;
            handler.sendMessage(msg);
        }
    }
}
