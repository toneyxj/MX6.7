package com.dangdang.reader.statis;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dangdang.reader.db.DDClickStatisHelper;
import com.dangdang.reader.utils.AccountManager;
import com.dangdang.reader.utils.ConfigManager;
import com.dangdang.reader.utils.DangdangConfig;
import com.dangdang.zframework.log.LogM;
import com.dangdang.zframework.utils.DRUiUtility;

/**
 * @author luxu
 */
public class DDClickStatisService {

	private static DDClickStatisService mStatisService = null;

	private DDClickStatisHelper mHelper;
	private Context mContext;

	private String mDeviceSn;
	private String mChannelId;
	private String mClientVersionNo;
	// private String mClientOsInfo;
	private String mOSVersion;

	private Object mLock = new Object();
	private List<String> mList = Collections
			.synchronizedList(new ArrayList<String>());

	public DDClickStatisService(Context context) {
		mHelper = new DDClickStatisHelper(context);
		mContext = context;

		init();
	}

	public synchronized static DDClickStatisService getStatisService(Context context) {
		if (mStatisService == null) {
			mStatisService = new DDClickStatisService(
					context.getApplicationContext());
		}
		return mStatisService;
	}

	private void init() {
		ConfigManager cfm = new ConfigManager(mContext);
		mDeviceSn = URLEncoder.encode(cfm.getDeviceId());
		mChannelId = URLEncoder.encode(cfm.getChannelId());
		mClientVersionNo = URLEncoder.encode(cfm.getVersionName());
		mOSVersion = cfm.getOSVersion();
		// mClientOsInfo = getClientOsInfo(cfm.getOSVersion());
	}

	private String getClientOsInfo(String osVersion) {
		JSONObject jsonObj = new JSONObject();
		try {
			String resolution = URLEncoder.encode(DRUiUtility.getScreenWith()
					+ "*" + DRUiUtility.getScreenHeight());
			String clientOs = URLEncoder.encode(osVersion);
			jsonObj.put(StatisConstant.OsVersion, clientOs);
			jsonObj.put(StatisConstant.Resolution, resolution);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return jsonObj.toString();
	}

	public boolean addStatis(int pageId, StatisEventId eventId,
			String pageInfo, long pageStayTime, String linkUrl,
			String expandField) {
		try {
			long currTime = new Date().getTime();
			JSONObject json = oneStatisJson(currTime, pageId, eventId,
					pageInfo, pageStayTime, linkUrl, expandField);
			if (json != null) {
				insertData(json.toString());
				return true;
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}

	private JSONObject oneStatisJson(long time, int pageId,
			StatisEventId event, String pageInfo, long pageStayTime,
			String linkUrl, String expandField) {
		if (time <= 0) {
			printLogE(" oneStatisJson time = " + time);
			return null;
		}
		JSONObject jsonObj = new JSONObject();
		try {

			String deviceSn = mDeviceSn;
			String token = "";
			AccountManager actManager = new AccountManager(mContext);
			if (actManager.checkTokenValid()) {
				token = actManager.getToken();
			}
			String deviceType = DangdangConfig.ParamsType.getDeviceType();
			String channelId = mChannelId;
			String clientVersionNo = mClientVersionNo;
			String clientOsInfo = getClientOsInfo(mOSVersion);

			jsonObj.put(StatisConstant.ActionTime, time);
			jsonObj.put(StatisConstant.DeviceSerialNo, deviceSn);
			jsonObj.put(StatisConstant.Token, token);
			jsonObj.put(StatisConstant.PageId, pageId);
			jsonObj.put(StatisConstant.EventId, event.getEventId());
			jsonObj.put(StatisConstant.EventName, event.getEventName());
			jsonObj.put(StatisConstant.DeviceType, deviceType);
			jsonObj.put(StatisConstant.ClientVersionNo, clientVersionNo);
			jsonObj.put(StatisConstant.ClientOs, clientOsInfo);
			jsonObj.put(StatisConstant.PageInfo, pageInfo);
			jsonObj.put(StatisConstant.PageStayTime, pageStayTime);
			jsonObj.put(StatisConstant.ChannelId, channelId);
			jsonObj.put(StatisConstant.LinkUrl, linkUrl);
			jsonObj.put(StatisConstant.ExpandField, expandField);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return jsonObj;
	}

	public void insertData(String data) {
		mList.add(data);
		if (mList.size() > 10) {
			List<String> list = new ArrayList<String>();
			list.addAll(mList);
			mList.clear();
			insertData(list);
		}
	}

	public void pushData() {
		try {
			if (!mList.isEmpty()) {
				List<String> list = new ArrayList<String>();
				list.addAll(mList);
				mList.clear();
				insertData(list);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 写数据库
	 * 
	 * @param ob
	 */
	protected void insertData(List<String> list) {
		synchronized (mLock) {
			SQLiteDatabase db = mHelper.getWritableDatabase();
			for (String str : list) {
				try {
					String insert = "INSERT INTO "
							+ DDClickStatisHelper.DB_TABLE + " ("
							+ DDClickStatisHelper.COLUMN_UPLOADSTATUS + ", "
							+ DDClickStatisHelper.COLUMN_DATA
							+ ") values (?,?)";

					final String uploadStatus = String
							.valueOf(DDClickStatisHelper.UPLOADSTATUS_NO);
					Object[] args = new Object[] { uploadStatus, str };
					db.execSQL(insert, args);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			closeSqliteDb(db);
		}
	}

	public JSONArray getDataAndUpdateStatus(int status) {
		synchronized (mLock) {
			JSONArray array = new JSONArray();
			SQLiteDatabase sqLiteDatabase = null;
			Cursor cursor = null;
			try {
				sqLiteDatabase = mHelper.getWritableDatabase();
				String sql = "SELECT * FROM " + DDClickStatisHelper.DB_TABLE;
				cursor = sqLiteDatabase.rawQuery(sql, null);

				JSONObject ob = null;
				while (cursor.moveToNext()) {
					String str = cursor.getString(cursor
							.getColumnIndex(DDClickStatisHelper.COLUMN_DATA));
					ob = new JSONObject(str);
					array.put(ob);
				}
				updateStatus(sqLiteDatabase, status);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				closeCursor(cursor);
				closeSqliteDb(sqLiteDatabase);
			}
			return array;
		}
	}

	public void deleteUploaddata() {
		synchronized (mLock) {
			SQLiteDatabase db = mHelper.getWritableDatabase();
			try {
				String delete = " DELETE FROM " + DDClickStatisHelper.DB_TABLE
						+ " where " + DDClickStatisHelper.COLUMN_UPLOADSTATUS
						+ " = ? ";
				db.execSQL(delete, new String[] { String
						.valueOf(DDClickStatisHelper.UPLOADSTATUS_YES) });

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				closeSqliteDb(db);
			}
		}
	}

	private void updateStatus(SQLiteDatabase db, int psatus) {
		String satus = String.valueOf(DDClickStatisHelper.UPLOADSTATUS_NO);
		String motify = String.valueOf(DDClickStatisHelper.UPLOADSTATUS_YES);
		if (psatus == DDClickStatisHelper.UPLOADSTATUS_YES) {
			satus = String.valueOf(DDClickStatisHelper.UPLOADSTATUS_YES);
			motify = String.valueOf(DDClickStatisHelper.UPLOADSTATUS_NO);
		}
		String sql = " update " + DDClickStatisHelper.DB_TABLE + " set "
				+ DDClickStatisHelper.COLUMN_UPLOADSTATUS + " = ? where "
				+ DDClickStatisHelper.COLUMN_UPLOADSTATUS + " = ? ";
		db.execSQL(sql,
				new String[] { String.valueOf(satus), String.valueOf(motify) });
	}

	/**
	 * 开始上传psatus = 1 上传失败psatus = 0
	 * 
	 * @param psatus
	 */
	public void updateStatus(int psatus) {
		synchronized (mLock) {
			SQLiteDatabase db = mHelper.getWritableDatabase();
			try {
				updateStatus(db, psatus);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				closeSqliteDb(db);
			}
		}
	}

	protected void closeCursor(Cursor cursor) {
		try {
			if (cursor != null) {
				cursor.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void closeSqliteDb(SQLiteDatabase db) {
		if (db != null) {
			try {
				db.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	protected void closeDbHelper() {
		if (mHelper != null) {
			mHelper.close();
		}
	}

	protected void printLog(String log) {
		LogM.i(getClass().getSimpleName(), log);
	}

	protected void printLogE(String log) {
		LogM.e(getClass().getSimpleName(), log);
	}

}
