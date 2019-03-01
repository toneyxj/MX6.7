package com.dangdang.reader.dread.format.part;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;

public class IBoughtAndDownHandleImp implements IBoughtAndDownHandle {
	private static IBoughtAndDownHandleImp mBoughtAndDownHandleImp;
	private static IChapterLoader mChapterLoader;

	private IBoughtAndDownHandleImp() {
	}

	public synchronized static IBoughtAndDownHandleImp getInstance() {
		if (mBoughtAndDownHandleImp == null) {
			mBoughtAndDownHandleImp = new IBoughtAndDownHandleImp();
		}
		return mBoughtAndDownHandleImp;
	}

	public static IChapterLoader getChapterLoader() {
		return mChapterLoader;
	}

	@Override
	public void getChapterList(String pId, int start, int count,
			Handler handler, int pickStart, int pickCount) {
		if (mChapterLoader != null) {
			mChapterLoader.getChapterList(pId, start, count, handler,
					pickStart, pickCount);
		}
	}

	@Override
	public void downloadChapter(String pId, String chapterId, Handler handler) {
		if (mChapterLoader != null) {
			mChapterLoader.downloadChapter(pId, chapterId, handler);
		}
	}

	@Override
	public void buyChapter(String pId, String chapterId, Handler handler) {
		if (mChapterLoader != null) {
			mChapterLoader.buyChapter(pId, chapterId, handler);
		}
	}

	@Override
	public void init(Context context) {
		Intent intent = new Intent("BindChapterLoadService");
		context.getApplicationContext().bindService(intent, mConn,
				Context.BIND_AUTO_CREATE);
	}

	private static ServiceConnection mConn = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			if (service instanceof IChapterLoader)
				mChapterLoader = (IChapterLoader) service;
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {

		}
	};
}
