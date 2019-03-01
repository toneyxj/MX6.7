package com.dangdang.reader.request;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.dangdang.reader.Constants;
import com.dangdang.reader.dread.util.DrmWrapUtil;
import com.dangdang.reader.personal.domain.ShelfBook;
import com.dangdang.reader.personal.domain.ShelfBook.TryOrFull;
import com.dangdang.zframework.network.command.OnCommandListener;
import com.dangdang.zframework.utils.ConfigManager;
import com.dangdang.zframework.utils.StringUtil;

/**
 * Created by Yhyu on 2015/5/21.
 */
public class GetPublishedCertificateRequest extends BaseStringRequest {

	private String ref;
	private Handler handler;
	private ShelfBook mBook;
	private Context mContext;

	public GetPublishedCertificateRequest(Context context, String ref, Handler handler,
			ShelfBook book) {
		mContext = context;
		this.ref = ref;
		this.handler = handler;
		mBook = book;
	}

	@Override
	public String getAction() {
		return "getPublishedCertificate";
	}

	@Override
	public void appendParams(StringBuilder buff) {
		if (!StringUtil.isEmpty(ref)) {
			buff.append("&refAction=");
			buff.append(ref);
		}
		buff.append("&mediaId=");
		buff.append(mBook.getMediaId());		
		buff.append("&key=");
		buff.append(DrmWrapUtil.getPublicKey());
		buff.append("&deviceNo=");
		String no = new ConfigManager(mContext).getDeviceId();
		buff.append(no);
		buff.append("&isFull=");
		if(mBook.getTryOrFull() == TryOrFull.TRY)
			buff.append(0);
		else
			buff.append(1);
		buff.append("&refAction=browse");
		//包月书籍添加频道id信息
		if (mBook.getTryOrFull() == TryOrFull.MONTH_FULL && !TextUtils.isEmpty(mBook.getBookJson())) {
			String channelId;
			try {
				org.json.JSONObject obj = new org.json.JSONObject(mBook.getBookJson());
				channelId = obj.optString(Constants.MONTHLY_CHANNEL_ID, "");
			} catch (Exception e) {
				channelId = "";
			}
			if (!TextUtils.isEmpty(channelId)) {
				buff.append("&mediaChannelId=").append(channelId);
			}
		}
	}

	@Override
	protected void onRequestSuccess(OnCommandListener.NetResult netResult,
			JSONObject jsonObject) {
		Message msg = Message.obtain();
		String cert = jsonObject.getString("certificate");
		msg.what = Constants.MSG_WHAT_GETCERT_SUCCESS;
		result.setResult(cert);
		msg.obj = result;
		if (mBook != null) {
			Bundle bundle = new Bundle();
			bundle.putSerializable("book", mBook);
			msg.setData(bundle);
		}
		handler.sendMessage(msg);
	}

	@Override
	protected void onRequestFailed(OnCommandListener.NetResult netResult, JSONObject jsonObject) {
		Message msg = Message.obtain();
		msg.what = Constants.MSG_WHAT_GETCERT_FAILED;
		msg.obj = result;
		if (mBook != null) {
			Bundle bundle = new Bundle();
			bundle.putSerializable("book", mBook);
			msg.setData(bundle);
		}
		handler.sendMessage(msg);
	}
}
