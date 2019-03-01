package com.dangdang.reader.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

import com.dangdang.reader.request.UpdateFollowBookListRequest;
import com.dangdang.zframework.plugin.AppUtil;

/**
 * 追书管理类，用于追更和取消追更时通知服务器
 * @author xiaruri
 *
 */
public class FollowBookManager {

	/**
	 * static 
	 */
	private static final String KEY_FOLLOW = "KEY_FOLLOW";
	private static final String KEY_UNFOLLOW = "KEY_UNFOLLOW";
	private static final String MEDIAID_SEPARATOR = "_";
	
	/**
	 * data
	 */
	private static FollowBookManager mFollowBookManager;
	private SharedPreferences mSp;
	private Editor mEditor;
	private Context mContext;
	
	private FollowBookManager(Context context){
		init(context);
	}
	
	private void init(Context context) {
		mSp = context.getSharedPreferences("ddoriginal_follow_book", Context.MODE_PRIVATE);
		mContext = context.getApplicationContext();
		mEditor = mSp.edit();
	}
	
	public void updateFollowBookList2Server(){
		List<String> followMediaIdList = getFollowMediaIdList();
		if(followMediaIdList != null && followMediaIdList.size() > 0){
			UpdateFollowBookListRequest followRewuest = new UpdateFollowBookListRequest(mContext, 1, followMediaIdList);
			AppUtil.getInstance(mContext).getRequestQueueManager().sendRequest(followRewuest, UpdateFollowBookListRequest.class.getSimpleName());
		}
		
		List<String> unfollowMediaIdList = getUnFollowMediaIdList();
		if(unfollowMediaIdList != null && unfollowMediaIdList.size() > 0){
			UpdateFollowBookListRequest unfollowRewuest = new UpdateFollowBookListRequest(mContext, 0, unfollowMediaIdList);
			AppUtil.getInstance(mContext).getRequestQueueManager().sendRequest(unfollowRewuest, UpdateFollowBookListRequest.class.getSimpleName());
		}
	}
	
	public static FollowBookManager getInstance(Context context){
		if(mFollowBookManager == null){
			mFollowBookManager = new FollowBookManager(context);
		}
		return mFollowBookManager;
	}
	
	public void setFollowMediaIdList(List<String> mediaIdList){
		String mediaIdStr = getMediaIdStr(mediaIdList);
		mEditor.putString(KEY_FOLLOW, mediaIdStr);
		mEditor.commit();
	}
	
	public List<String> getFollowMediaIdList(){
		List<String> mediaIdList = new ArrayList<String>();
		String mediaIdStr = mSp.getString(KEY_FOLLOW, "");
		if(TextUtils.isEmpty(mediaIdStr)){
			return mediaIdList;
		}
		String[] mediaIdArray = mediaIdStr.split(MEDIAID_SEPARATOR);
		Collections.addAll(mediaIdList, mediaIdArray);
		
		return mediaIdList;
	}
	
	public void setUnFollowMediaIdList(List<String> mediaIdList){
		String mediaIdStr = getMediaIdStr(mediaIdList);
		mEditor.putString(KEY_UNFOLLOW, mediaIdStr);
		mEditor.commit();
	}
	
	public List<String> getUnFollowMediaIdList(){
		List<String> mediaIdList = new ArrayList<String>();
		String mediaIdStr = mSp.getString(KEY_UNFOLLOW, "");
		if(TextUtils.isEmpty(mediaIdStr)){
			return mediaIdList;
		}
		String[] mediaIdArray = mediaIdStr.split(MEDIAID_SEPARATOR);
		Collections.addAll(mediaIdList, mediaIdArray);
		
		return mediaIdList;
	}
	
	public String getMediaIdStr(List<String> mediaIdList){
		if(mediaIdList == null || mediaIdList.size() <= 0){
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < mediaIdList.size(); i++) {
			if(i == (mediaIdList.size() - 1)){
				sb.append(mediaIdList.get(i));
			} else{
				sb.append(mediaIdList.get(i));
				sb.append(MEDIAID_SEPARATOR);
			}
		}
		return sb.toString();
	}
	
}
