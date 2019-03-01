package com.dangdang.reader.request;

import android.os.Handler;
import android.os.Message;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangdang.reader.dread.data.FontDomain;
import com.dangdang.reader.utils.DangdangConfig;
import com.dangdang.zframework.network.RequestConstant;
import com.dangdang.zframework.network.command.OnCommandListener;
import com.dangdang.zframework.utils.FileUtil;
import com.dangdang.zframework.utils.MD5Util;
import com.dangdang.zframework.utils.StringUtil;

import java.util.ArrayList;

/**
 * Created by liuboyu on 2014/12/23.
 * 包含免费和已购字体
 */
public class MultiGetMyFontListRequest extends BaseStringRequest {

	private static final String ACTION_FLAG_FREE = "block";
	private static final String ACTION_FLAG_USERFONTLIST = "userFontList";

	/**
	 * userFontList parameter
	 */
	private String token;

	private Handler handler;
	private String defaultFontName;

	public MultiGetMyFontListRequest(Handler handler, String token, String defaultFontName) {
		super(10000);
		this.handler = handler;
		this.token = token;
		this.defaultFontName = defaultFontName;
	}

	public String getUrl() {
		StringBuilder buff = new StringBuilder(DangdangConfig.SERVER_MOBILE_API2_URL);
		buff.append("action=");
		buff.append(getAction());
		appendParams(buff);
		setUrl(buff.toString());
		return buff.toString();
	}

	@Override
	public String getPost() {
		StringBuilder buff = new StringBuilder();
		buff.append("field=");
		buff.append("{\"noDependActions\"" + ":[");
		appendGetFreeFontListParams(buff);
		appendGetUserFontListParams(buff);
		buff.append("]}");
		return buff.toString();
	}

	private void appendGetFreeFontListParams(StringBuilder buff) {
		buff.append("{");
		buff.append("\"action\":\"" + ACTION_FLAG_FREE + "\",");
		buff.append("\"params\":{" + "\"returnType\":\"json\"," + "\"code\":\"").append(getFreeFontCode()).append("\"}");
		buff.append("},");
	}

	private void appendGetUserFontListParams(StringBuilder buff) {
		buff.append("{");
		buff.append("\"action\":\"" + ACTION_FLAG_USERFONTLIST + "\",");
		buff.append("\"params\":{" + "\"deviceType\":\"Android\"," + "\"token\":\"").append(token).append("\",").append("\"pageNum\":1,").append("\"pageSize\":5000}");
		buff.append("}");
	}

	private String getFreeFontCode() {
		if (DangdangConfig.isDevelopEnv()) {
			return "freefont";
		}
		return "AndroidV4_freefont";
	}

	@Override
	public String getAction() {
		return "multiActionV2";
	}

	@Override
	public RequestConstant.HttpMode getHttpMode() {
		return RequestConstant.HttpMode.POST;
	}

	@Override
	public void appendParams(StringBuilder buff) {
	}

	@Override
	public RequestConstant.HttpType getHttpType() {
		return RequestConstant.HttpType.HTTP;
	}

	@Override
	protected void onRequestSuccess(OnCommandListener.NetResult netResult, JSONObject jsonObject) {
		JSONObject result = jsonObject.getJSONObject("result");
		if (result != null) {
			ArrayList<FontDomain> fontList = handleGetFreeFontList(result.getJSONObject(ACTION_FLAG_FREE));
			fontList.addAll(handleGetUserFontList(result.getJSONObject(ACTION_FLAG_USERFONTLIST)));
			Message msg = handler.obtainMessage(RequestConstants.MSG_WHAT_REQUEST_DATA_SUCCESS);
			msg.obj = fontList;
			handler.sendMessage(msg);
		} else {
			Message msg = handler.obtainMessage(RequestConstants.MSG_WHAT_REQUEST_DATA_FAIL);
			msg.obj = expCode;
			handler.sendMessage(msg);
		}
	}

	private ArrayList<FontDomain> handleGetFreeFontList(JSONObject jsonResult) {
		ArrayList<FontDomain> freeFontList = new ArrayList<FontDomain>();
		if (jsonResult == null)
			return freeFontList;
		try {
			int statusCode = jsonResult.getInteger("statusCode");
			if (statusCode == 0) {
				JSONArray result = jsonResult.getJSONArray("productList");
				for (int i = 0; i < result.size(); i++) {
					FontDomain font = result.getObject(i, FontDomain.class);
					font.setProductId(MD5Util.getMD5Str(font.getDownloadURL()));
					font.freeBook = true;
					if (!StringUtil.isEmpty(defaultFontName) && defaultFontName.equals(font.getProductname()))
						continue;
					freeFontList.add(font);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return freeFontList;
	}

	private ArrayList<FontDomain> handleGetUserFontList(JSONObject jsonResult) {
		ArrayList<FontDomain> userFontList = new ArrayList<FontDomain>();
		try {
			int statusCode = jsonResult.getJSONObject("status").getInteger("code");
			if (statusCode == 0) {
				JSONArray result = jsonResult.getJSONObject("data").getJSONArray("ebookList");
				for (int i = 0; i < result.size(); i++) {
					JSONObject item = result.getJSONObject(i);
					FontDomain font = new FontDomain();
					font.setImageURL(item.getString("cover"));
					font.setFontSize(FileUtil.formatFileSize(item.getInteger("translator")));
					font.setSalePrice(FileUtil.converYuan(item.getFloatValue("price")));
					font.setProductId(item.getString("productId"));
					font.setProductname(item.getString("bookName"));
					font.setBought(true);
					userFontList.add(font);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return userFontList;
	}

	@Override
	protected void onRequestFailed(OnCommandListener.NetResult netResult, JSONObject jsonObject) {
		Message msg = handler.obtainMessage(1);
		handler.sendMessage(msg);
	}

}
