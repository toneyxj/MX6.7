package com.dangdang.reader.request;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.alibaba.fastjson.JSONObject;
import com.dangdang.reader.utils.DangdangConfig;
import com.dangdang.zframework.network.command.OnCommandListener.NetResult;

/**
 * 提交书签、笔记列表到云端
 * @author baoguangshuai
 */
public class UpdateBookCloudReadInfoRequest extends BaseStringRequest {
	public static String ACTION = "updateBookCloudSyncReadInfo";

	private String markInfo;
	private String noteInfo;
	private long versionTime;
	private Handler handler;
	private String referType;

//    @Override
//    public String getUrl() {
//        return DangdangConfig.SERVER_MOBILE_BOOK_CLOUD_API2_URL+"action="+getAction();
////        return "http://10.255.223.227:8090/mobile/api2.do?action="+getAction();
////        return "http://10.255.223.131/mobile/api2.do?action="+getAction();
////        return "http://192.168.132.73:8080/mobile/api2.do?action="+getAction();
//    }

    @Override
	public String getPost() {
		StringBuilder buff = new StringBuilder();
//        buff.append("&token=adef31566e580ee3f3a532806027d130");
		buff.append("&markInfo=");
		buff.append(markInfo);
		buff.append("&noteInfo=");
		buff.append(noteInfo);
		buff.append("&versionTime=");
		buff.append(versionTime);
		buff.append("&referType=");
		buff.append(referType);

		return buff.toString();
	}
	

	public UpdateBookCloudReadInfoRequest(Handler handler,String markInfo,String noteInfo,long versionTime, String referType) {
		this.handler = handler;
		this.referType = referType;
		this.markInfo = markInfo;
		this.noteInfo = noteInfo;
		this.versionTime = versionTime;
	}
	
	
	@Override
	public String getAction() {
		 
		return ACTION;
	}

	@Override
	public void appendParams(StringBuilder buff) {
//		buff.append("&markInfo=");
//		buff.append(markInfo);
//		buff.append("&noteInfo=");
//		buff.append(noteInfo);
//		buff.append("&versionTime=");
//		buff.append(versionTime);
	}

	public void setParamater(String markInfo, String noteInfo, long versionTime) {
		//this.token = token;
		this.markInfo = markInfo;
		this.noteInfo = noteInfo;
		this.versionTime = versionTime;
	}


	@Override
	protected void onRequestSuccess(NetResult netResult,
			com.alibaba.fastjson.JSONObject jsonObject) {
		// TODO Auto-generated method stub
		if (handler != null) {
			Message msg = handler
					.obtainMessage(RequestConstants.MSG_WHAT_REQUEST_DATA_SUCCESS);
			result.setResult(jsonObject);
			msg.obj = result;
            Bundle bundle = new Bundle();
            bundle.putString("markInfo",markInfo);
            bundle.putString("noteInfo",noteInfo);
            msg.setData(bundle);
			handler.sendMessage(msg);
		}
	}

	@Override
	protected void onRequestFailed(NetResult netResult, JSONObject jsonObject) {
		// TODO Auto-generated method stub
		if (handler != null) {
			Message msg = handler
					.obtainMessage(RequestConstants.MSG_WHAT_REQUEST_DATA_FAIL);
			result.setExpCode(getExpCode());
			msg.obj = result;
			handler.sendMessage(msg);
		}
	}
	
}
