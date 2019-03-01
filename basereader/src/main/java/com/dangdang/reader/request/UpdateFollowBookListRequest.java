package com.dangdang.reader.request;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import android.content.Context;

import com.alibaba.fastjson.JSONObject;
import com.dangdang.reader.utils.FollowBookManager;
import com.dangdang.zframework.network.command.OnCommandListener.NetResult;

/**
 * 更新追更列表接口请求器
 * @author xiaruri
 *
 */
public class UpdateFollowBookListRequest extends BaseStringRequest {
	
	private static final String ACTION = "getUpdateCustomerSubscribe";
	
	private int operationType;							// 操作表示： 1：追更；0：取消追更	
	private List<String> mediaIdList;					// 书的id列表
	private String mediaId;								// 书的id, 支持批量，以下划线分隔，例：  123333_223333_123334
	private FollowBookManager mFollowBookManager;
	private Context mContext;
	
	/**
	 * 
	 * @param operationType		 	操作表示： 1：追更；0：取消追更	
	 * @param mediaIdList			书的id列表
	 */
	public UpdateFollowBookListRequest(Context context, int operationType, List<String> mediaIdList){
		super();
		mContext = context;
		this.operationType  = operationType;
		this.mediaIdList = mediaIdList;
		setToMainThread(false);
		setEncode("utf-8");
		
		initMediaId();
	}
	
	private void initMediaId() {
		mFollowBookManager = FollowBookManager.getInstance(mContext);
		if(mediaIdList == null || mediaIdList.size() == 0){
			if(operationType == 0){
				mediaId = mFollowBookManager.getMediaIdStr(mFollowBookManager.getUnFollowMediaIdList());
			} else{
				mediaId = mFollowBookManager.getMediaIdStr(mFollowBookManager.getFollowMediaIdList());
			}
			return;
		}
		
		List<String> saveMediaIdList = null;
		if(operationType == 0){
			saveMediaIdList = mFollowBookManager.getUnFollowMediaIdList();
		} else{
			saveMediaIdList = mFollowBookManager.getFollowMediaIdList();
		}
		
		for (String saveMediaId : saveMediaIdList) {
			if(!mediaIdList.contains(saveMediaId)){
				mediaIdList.add(saveMediaId);
			}
		}
		mediaId = mFollowBookManager.getMediaIdStr(mediaIdList);
		
		filterSaveMediaIdList();
	}
	
	/**
	 * 如果添加追更，则过滤下存储的未追更的mediaIdlist，反之一样
	 */
	private void filterSaveMediaIdList(){
		if(mediaIdList == null || mediaIdList.size() == 0){
			return;
		}
		
		List<String> saveMediaIdList = null;
		if(operationType == 0){
			saveMediaIdList = mFollowBookManager.getFollowMediaIdList();
		} else{
			saveMediaIdList = mFollowBookManager.getUnFollowMediaIdList();
		}
		
		if(saveMediaIdList == null || saveMediaIdList.size() == 0){
			return;
		}
		
		for (int i = (saveMediaIdList.size() -1); i >= 0; i--) {
			if(mediaIdList.contains(saveMediaIdList.get(i))){
				saveMediaIdList.remove(i);
			}
		}
		
		if(operationType == 0){
			mFollowBookManager.setFollowMediaIdList(saveMediaIdList);
		} else{
			mFollowBookManager.setUnFollowMediaIdList(saveMediaIdList);
		}
	}

	@Override
	public String getAction() {
		return ACTION;
	}

	@Override
	public void appendParams(StringBuilder buff) {
	}
	
	@Override
	public String getPost() {
		StringBuilder buff = new StringBuilder();
		buff.append("&operationType=").append(operationType);
		buff.append("&appId=" + 1);
		buff.append("&mediaId=").append(encode(mediaId));
		
		return buff.toString();
	}

	@Override
	protected void onRequestSuccess(NetResult netResult, JSONObject jsonObject) {
		if(isGetDataSuccess(expCode)){
			dealRequestSuccess();
		} else{
			dealRequestFail();
		}
	}

	@Override
	protected void onRequestFailed(NetResult netResult, JSONObject jsonObject) {
		dealRequestFail();
	}
	
	private void dealRequestSuccess(){
		if(operationType == 0){
			mFollowBookManager.setUnFollowMediaIdList(null);
		} else{
			mFollowBookManager.setFollowMediaIdList(null);
		}
	}
	
	private void dealRequestFail(){
		if(operationType == 0){
			mFollowBookManager.setUnFollowMediaIdList(mediaIdList);
		} else{
			mFollowBookManager.setFollowMediaIdList(mediaIdList);
		}
	}
	
	private boolean isGetDataSuccess(ResultExpCode expCode){
		if (expCode != null && expCode.statusCode != null && expCode.statusCode.equals("0")) {
			return true;
		}
		return false;
	}

}
