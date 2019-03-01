package com.dangdang.reader.cloud;

import org.json.JSONArray;
import org.json.JSONObject;

import com.dangdang.reader.dread.data.ReadInfo;
import com.dangdang.zframework.log.LogM;
import com.dangdang.zframework.utils.StringUtil;

/**
 * 书签、笔记列表转成json
 *
 * @author luxu
 */
public class CloudSyncConvert {


	public final static String KEY_PRODUCTD = "productId";
	public final static String KEY_CHAPTERINDEX = "chaptersIndex";
	public final static String KEY_ELEMENTINDEX = "characterIndex";
	public final static String KEY_OPERATETIME = "clientOperateTime";
	public final static String KEY_STARTTIME = "startTime";
	public final static String KEY_ENDTIME = "endTime";


	/**
	 * @param productId
	 * @param chapterIndex
	 * @param elementIndex
	 * @param clientOperateTime 服务端要求是秒
	 * @param startTime
	 * @param endTime           @return
	 */
	public static String convertProgress(String productId, int chapterIndex, int elementIndex, long clientOperateTime, long startTime, long endTime) {
		/**
		 * [{"chaptersIndex":1,"characterIndex":1230,"productId":1900101406," "clientOperateTime":1399967465}]
		 *
		 */
		String progress = "";
		JSONArray jsonArrs = new JSONArray();
		JSONObject jsonObj = new JSONObject();
		try {
			jsonObj.put(KEY_PRODUCTD, productId);
			jsonObj.put(KEY_CHAPTERINDEX, chapterIndex);
			jsonObj.put(KEY_ELEMENTINDEX, elementIndex);
			jsonObj.put(KEY_OPERATETIME, clientOperateTime);
			jsonObj.put(KEY_STARTTIME, startTime);
			jsonObj.put(KEY_ENDTIME, endTime);
			jsonArrs.put(jsonObj);

			progress = jsonArrs.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return progress;
	}

	public static long[] parseStartEndTimeFromReadTimeInfo(String readTimeInfo) {
		LogM.d("CloudSyncConvert parse: " + readTimeInfo);
		long startTime = 0;
		long endTime = 0;
		JSONObject jsonObj = null;
		if (readTimeInfo != null) {
			try {
				jsonObj = new JSONObject(readTimeInfo);
				startTime = jsonObj.optLong(ReadInfo.JSONK_READ_START_TIME, 0);
				endTime = jsonObj.optLong(ReadInfo.JSONK_READ_END_TIME, 0);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		if (startTime == 0)
			startTime = System.currentTimeMillis();
		return new long[]{startTime, endTime};
	}

	public static String writeStartEndTimeToReadTimeInfo(String readTimeInfo, long startTime, long endTime) {
		try {
			JSONObject jsonObj = null;
			long oldEndTime = 0, oldStartTime = 0;
			if (!StringUtil.isEmpty(readTimeInfo)) {
				jsonObj = new JSONObject(readTimeInfo);
				oldStartTime = jsonObj.optLong(ReadInfo.JSONK_READ_START_TIME, 0);
				oldEndTime = jsonObj.optLong(ReadInfo.JSONK_READ_END_TIME, 0);
			} else {
				jsonObj = new JSONObject();
			}

			if (oldStartTime == 0 && startTime != 0)
				jsonObj.put(ReadInfo.JSONK_READ_START_TIME, startTime);
			else if (oldStartTime != 0 && startTime == 0)
				jsonObj.put(ReadInfo.JSONK_READ_START_TIME, oldStartTime);
			else
				jsonObj.put(ReadInfo.JSONK_READ_START_TIME, Math.min(startTime, oldStartTime));
			if (oldEndTime == 0 && endTime != 0)
				jsonObj.put(ReadInfo.JSONK_READ_END_TIME, endTime);
			else if (oldEndTime != 0 && endTime == 0)
				jsonObj.put(ReadInfo.JSONK_READ_END_TIME, oldEndTime);
			else
				jsonObj.put(ReadInfo.JSONK_READ_END_TIME, Math.min(endTime, oldEndTime));
			LogM.d("CloudSyncConvert write: " + jsonObj.toString());
			return jsonObj.toString();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return null;
	}
}
