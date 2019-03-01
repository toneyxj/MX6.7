package com.dangdang.reader.request;

import android.os.Handler;
import android.os.Message;

import com.dangdang.reader.DDApplication;
import com.dangdang.reader.R;
import com.dangdang.zframework.network.RequestConstant.HttpMode;
import com.dangdang.zframework.network.command.OnCommandListener;
import com.dangdang.zframework.network.command.StringRequest;



/**
 * 获取html地址
 * Created by xiaruri on 2014/11/29.
 */
public class GetHtmlDataStringRequest extends StringRequest {

	protected boolean success = true;
	private String url;
	private Handler handler;

	public GetHtmlDataStringRequest(String url, Handler handler) {
		super(null);
		this.url = url;
		this.handler = handler;

		setOnCommandListener(mCommandListener);
	}

	public  String getUrl() {
		if(!url.contains("?")){
			url = url + "?";
		}
		return url;
	}
	
	@Override
	public HttpMode getHttpMode() {
		return HttpMode.GET;
	}

	private OnCommandListener<String> mCommandListener = new OnCommandListener<String>() {
		@Override
		public void onSuccess(final String data, NetResult netResult) {
			dealSuccess(data);
		}

		@Override
		public void onFailed(NetResult netResult) {
			ResultExpCode expCode = new ResultExpCode();
			expCode.errorCode = ResultExpCode.ERRORCODE_NONET;
			expCode.errorMessage = DDApplication.getApplication().getString(R.string.error_no_net);
			dealFail(expCode);
		}
	};
	
	private void dealSuccess(String data){
		if(handler == null){
			return;
		}
		Message msg = handler.obtainMessage(RequestConstants.MSG_WHAT_GET_HTML_DATA_SUCCESS);
		msg.obj = data;
		handler.sendMessage(msg);
	}

	private void dealFail(ResultExpCode expCode){
		if(handler == null){
			return;
		}
		Message msg = handler.obtainMessage(RequestConstants.MSG_WHAT_GET_HTML_DATA_FAIL);
		msg.obj = expCode;
		handler.sendMessage(msg);
	}

}
