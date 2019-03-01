package com.dangdang.reader.request;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.alibaba.fastjson.JSONObject;
import com.dangdang.reader.Constants;
import com.dangdang.reader.dread.util.DrmWrapUtil;
import com.dangdang.reader.personal.domain.ShelfBook;
import com.dangdang.zframework.network.command.OnCommandListener;
import com.dangdang.zframework.utils.StringUtil;

/**
 * Created by Yhyu on 2015/5/21.
 */
public class GetCertificateRequest extends BaseStringRequest {

	private String pid;
	private String ref;
	private Handler handler;
	private ShelfBook mBook;

	public GetCertificateRequest(String pid, String ref, Handler handler) {
		this.pid = pid;
		this.ref = ref;
		this.handler = handler;
	}

	public void setShelfBook(ShelfBook book) {
		mBook = book;
	}
	@Override
	public String getAction() {
		return "getCertificate";
	}

	@Override
	public void appendParams(StringBuilder buff) {
        if (!StringUtil.isEmpty(ref)) {
            buff.append("&refAction=");
            buff.append(ref);
        }
        buff.append("&mediaId=");
        buff.append(pid);
        buff.append("&publicKey=");
        buff.append(DrmWrapUtil.getPublicKey());
        buff.append("&refAction=browse");
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
