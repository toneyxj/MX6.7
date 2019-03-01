package com.dangdang.reader.dread.function;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.dangdang.reader.base.WebBrowserActivity;
import com.dangdang.reader.dread.config.ReadConfig;
import com.dangdang.reader.dread.core.base.BaseReaderApplicaion;

public class ToBrowserFunction extends MFunctionImpl {

	public ToBrowserFunction(BaseReaderApplicaion app) {
		super(app);
	}

	@Override
	protected void runFunction(Object... params) {
		
		try {
			if(params.length > 0){
				String url = (String) params[0];
				if(TextUtils.isEmpty(url)){
					return;
				}
				startBrowser(url);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	private void startBrowser(String url) {
		Context context = getReaderApp().getContext();
		Intent intent = new Intent(context, WebBrowserActivity.class);
		intent.putExtra(WebBrowserActivity.KEY_URL, url);
		intent.putExtra(WebBrowserActivity.KEY_FULLSCREEN, ReadConfig.getConfig().isFullScreen());
		context.startActivity(intent);
	}
}
