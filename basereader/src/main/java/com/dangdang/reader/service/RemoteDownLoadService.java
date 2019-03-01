package com.dangdang.reader.service;

import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.text.TextUtils;

import com.dangdang.reader.domain.ShelfDownload;
import com.dangdang.reader.utils.DangdangConfig;
import com.dangdang.reader.wxapi.DownloadDbProvider;
import com.dangdang.zframework.log.LogM;
import com.dangdang.zframework.network.download.Download;
import com.dangdang.zframework.network.download.DownloadManager;
import com.dangdang.zframework.network.download.DownloadManagerFactory;
import com.dangdang.zframework.network.download.DownloadManagerFactory.DownloadModule;
import com.dangdang.zframework.network.download.IDownloadManager.DownloadExp;
import com.dangdang.zframework.network.download.IDownloadManager.DownloadInfo;
import com.dangdang.zframework.network.download.IDownloadManager.IDownloadListener;

import java.io.Serializable;
import java.lang.ref.SoftReference;

public class RemoteDownLoadService extends Service {
	public static final String REMOTE_DOWNLOAD_SERVICE_ACTION = DangdangConfig.ParamsType.mPagekageName+".dowloadservice";//com.dangdang.reader
	public static final Uri uri = Uri.parse("content://"
			+ DownloadDbProvider.AUTHORITY + "/" + ShelfDownload.TABLE_NAME);

	public static final String PARMA_MODULE_INFO = "parma_module_info";
	public static final String PARMA_DOWNLOAD_INFO = "parma_download_info";
	public static final String PARMA_DOWNLOAD_TYPE = "parma_download_type";
	public static final String PARMA_EXP_INFO = "parma_exp_info";
	
	public static Messenger mReplyToMessenger;
	private static ServiceIncomingHandler mHandler;
	public Messenger mMessenger;
	public static final int MSG_REPLY_TO_MESSENGER = 0;
	public static final int DOWNLOAD_START = 1;
	public static final int DOWNLOAD_ING = 2;
	public static final int DOWNLOAD_PAUSE = 3;
	public static final int DOWNLOAD_FINISH = 4;
	public static final int DOWNLOAD_FAILED = 5;
	public static final int FILE_TOTAL_SIZE = 6;

	@Override
	public void onCreate() {
		LogM.l("RemoteDownLoadService onCreate");
		mHandler = new ServiceIncomingHandler(this);
		mMessenger = new Messenger(mHandler);
	}

	@Override
	public void onStart(Intent intent, int startId) {
		if (intent != null && intent.getExtras() != null) {
			Download download = (Download) intent
					.getSerializableExtra(PARMA_DOWNLOAD_INFO);
			DownloadModule downloadModule = (DownloadModule) intent
					.getSerializableExtra(PARMA_MODULE_INFO);
			int downloadType = intent.getIntExtra(PARMA_DOWNLOAD_TYPE, 0);
			progressDownload(downloadType, downloadModule, download);
		}
	}

	private void progressDownload(int downloadType,
			DownloadModule downloadModule, Download download) {
		if(download == null || TextUtils.isEmpty(download.getDownloadUrl()))
			return;
		DownloadManagerFactory mFactory = DownloadManagerFactory.getFactory();
		DownloadManager mDownloadMgr = (DownloadManager) mFactory
				.create(downloadModule);
		if (mDownloadMgr.getDownloadListener(downloadModule.getClass()) == null) {
			mDownloadMgr.registerDownloadListener(downloadModule.getClass(),
					new ServiceDownloadListener());
		}
		switch (downloadType) {
		case DOWNLOAD_START:
			mDownloadMgr.startDownload(download);
			break;

		case DOWNLOAD_PAUSE:
			mDownloadMgr.pauseDownload(download);
			break;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mMessenger.getBinder();
	}

	class ServiceDownloadListener implements IDownloadListener {

		@Override
		public void onDownloading(DownloadInfo info) {
			sendToHandler(DOWNLOAD_ING, info);
		}

		@Override
		public void onPauseDownload(DownloadInfo info) {
			sendToHandler(DOWNLOAD_PAUSE, info);
		}

		@Override
		public void onDownloadFinish(DownloadInfo info) {
			sendToHandler(DOWNLOAD_FINISH, info);
		}

		@Override
		public void onFileTotalSize(DownloadInfo info) {
			sendToHandler(DOWNLOAD_START, info);
		}

		@Override
		public void onDownloadFailed(DownloadInfo info, DownloadExp exp) {
			sendToHandler(DOWNLOAD_FAILED, info, exp);
		}

		private void sendToHandler(int what, DownloadInfo info, Object... params) {
			Message message = Message.obtain();
			message.what = what;
			Bundle data = new Bundle();
			data.putSerializable(PARMA_DOWNLOAD_INFO, info);
			if(params != null && params.length > 0){
				Object obj = params[0];
				if(obj instanceof DownloadExp)
					data.putSerializable(PARMA_EXP_INFO, (Serializable) obj);
			}
			message.setData(data);
			mHandler.sendMessage(message);
		}
	}

	static class ServiceIncomingHandler extends Handler {
		private SoftReference<Context> reference;

		public ServiceIncomingHandler(Context context) {
			reference = new SoftReference<Context>(context);
		}

		@Override
		public void handleMessage(Message msg) {
			Message message;
			switch (msg.what) {
			case MSG_REPLY_TO_MESSENGER:
				mReplyToMessenger = msg.replyTo;
				break;
			case FILE_TOTAL_SIZE:
				updateToatalSize(msg);
				message = Message.obtain(null, FILE_TOTAL_SIZE, 0, 0);
				message.setData(msg.getData());
				replySend(message);
				break;
			case DOWNLOAD_START:
				message = Message.obtain(null, DOWNLOAD_START, 0, 0);
				message.setData(msg.getData());
				replySend(message);
				break;
			case DOWNLOAD_ING:
				message = Message.obtain(null, DOWNLOAD_ING, 0, 0);
				message.setData(msg.getData());
				replySend(message);
				break;
			case DOWNLOAD_PAUSE:
				updateStatus(msg);
				message = Message.obtain(null, DOWNLOAD_PAUSE, 0, 0);
				message.setData(msg.getData());
				replySend(message);
				break;
			case DOWNLOAD_FINISH:
				updateStatus(msg);
				message = Message.obtain(null, DOWNLOAD_FINISH, 0, 0);
				message.setData(msg.getData());
				replySend(message);
				break;
			case DOWNLOAD_FAILED:
				updateStatus(msg);
				message = Message.obtain(null, DOWNLOAD_FAILED, 0, 0);
				message.setData(msg.getData());
				replySend(message);
				break;
			}
		}

		private void updateToatalSize(Message msg) {
			DownloadInfo pauseInfo = (DownloadInfo) msg.getData()
					.getSerializable(PARMA_DOWNLOAD_INFO);
			ContentValues pauseValues = new ContentValues();
			pauseValues.put(ShelfDownload.TOTALSIZE, pauseInfo.progress.total);
			updateDatabase(pauseInfo, pauseValues);
		}

		private void updateStatus(Message msg) {
			DownloadInfo info = (DownloadInfo) msg.getData().getSerializable(
					PARMA_DOWNLOAD_INFO);
			ContentValues pauseValues = new ContentValues();
			pauseValues.put(ShelfDownload.STATUS, info.status.getStatus());
			updateDatabase(info, pauseValues);
		}

		private void updateDatabase(DownloadInfo info,
				ContentValues updateValues) {
			Context context = reference.get();
			if (context != null) {
				ContentResolver contentResolver = context.getContentResolver();
				String where = ShelfDownload.INDENTITY_ID + "=?";
				// TODO
				contentResolver.update(uri, updateValues, where,
						new String[] {info.url});
			}
		}

		private void replySend(Message message) {
			try {
				mReplyToMessenger.send(message);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

}
