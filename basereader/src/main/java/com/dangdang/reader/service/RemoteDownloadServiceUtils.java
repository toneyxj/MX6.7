package com.dangdang.reader.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.dangdang.reader.DDApplication;
import com.dangdang.zframework.log.LogM;
import com.dangdang.zframework.network.download.Download;
import com.dangdang.zframework.network.download.DownloadManagerFactory.DownloadModule;
import com.dangdang.zframework.network.download.IDownloadHandle;
import com.dangdang.zframework.network.download.IDownloadManager.DownloadExp;
import com.dangdang.zframework.network.download.IDownloadManager.DownloadInfo;
import com.dangdang.zframework.network.download.IDownloadManager.IDownloadListener;

import java.lang.ref.WeakReference;
import java.util.Hashtable;
import java.util.Map;

public class RemoteDownloadServiceUtils implements IDownloadHandle {
	private static Messenger mServiceMessenger;
	protected static IncomingHandler mHandler;
	protected static Messenger mClientMessenger;
	private static RemoteDownloadServiceUtils downloadServiceUtils;
	private Context mContext;
	private static Map<Class<?>, IDownloadListener> mListeners = new Hashtable<Class<?>, IDownloadListener>();

	private RemoteDownloadServiceUtils(Context context) {
		initService(context);
	}

	public synchronized static RemoteDownloadServiceUtils getInstance(Context context) {
		if (downloadServiceUtils == null) {
			downloadServiceUtils = new RemoteDownloadServiceUtils(context);
		}
		return downloadServiceUtils;
	}

	private void initService(Context context) {
		this.mContext = context;
		mHandler = new IncomingHandler(this);
		mClientMessenger = new Messenger(mHandler);
		Intent intent = new Intent(
				RemoteDownLoadService.REMOTE_DOWNLOAD_SERVICE_ACTION);
		intent.setPackage(DDApplication.getApplication().getPackageName());
		context.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
	}
	
	private void dealMsg(Message msg){		
		DownloadInfo info = (DownloadInfo) msg.getData()
				.getSerializable(
						RemoteDownLoadService.PARMA_DOWNLOAD_INFO);
		if(info == null)
			return;
		IDownloadListener listener = getListener(info.mModule.getClass());
		if(listener == null)
			return;
		
		switch (msg.what) {
		case RemoteDownLoadService.FILE_TOTAL_SIZE:
			listener.onFileTotalSize(info);
			break;
		case RemoteDownLoadService.DOWNLOAD_START:
			listener.onDownloading(info);
			break;
		case RemoteDownLoadService.DOWNLOAD_PAUSE:
			listener.onPauseDownload(info);
			break;
		case RemoteDownLoadService.DOWNLOAD_ING:
			listener.onDownloading(info);
			break;
		case RemoteDownLoadService.DOWNLOAD_FINISH:
			listener.onDownloadFinish(info);
			break;
		case RemoteDownLoadService.DOWNLOAD_FAILED:{
			DownloadExp exp = (DownloadExp) msg.getData()
					.getSerializable(
							RemoteDownLoadService.PARMA_EXP_INFO);
			listener.onDownloadFailed(info, exp);
		}
			break;
		default:
			break;
		}
	}
	
	private static class IncomingHandler extends Handler {
		private final WeakReference<RemoteDownloadServiceUtils> mFragmentView;

		IncomingHandler(RemoteDownloadServiceUtils view) {
			this.mFragmentView = new WeakReference<RemoteDownloadServiceUtils>(view);
		}

		@Override
		public void handleMessage(Message msg) {
			RemoteDownloadServiceUtils service = mFragmentView.get();
			if (service != null) {
				super.handleMessage(msg);
				try {
					service.dealMsg(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static IDownloadListener getListener(Class<?> moduleKey) {
		return mListeners == null ? null : mListeners.get(moduleKey);
	}

	private static ServiceConnection mConnection = new ServiceConnection() {

		public void onServiceConnected(ComponentName className, IBinder service) {
			LogM.l("onServiceConnected");
			mServiceMessenger = new Messenger(service);
			try {
				Message msg = Message.obtain();
				msg.what = RemoteDownLoadService.MSG_REPLY_TO_MESSENGER;
				msg.replyTo = mClientMessenger;
				mServiceMessenger.send(msg);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}

		public void onServiceDisconnected(ComponentName className) {
			mServiceMessenger = null;
		}
	};

	private void startIntentDownload(DownloadModule downloadModule,
			Download download) {
		Intent intent = new Intent(
				RemoteDownLoadService.REMOTE_DOWNLOAD_SERVICE_ACTION);
		Bundle bundle = new Bundle();
		bundle.putSerializable(RemoteDownLoadService.PARMA_DOWNLOAD_INFO,
				download);
		bundle.putInt(RemoteDownLoadService.PARMA_DOWNLOAD_TYPE,
				RemoteDownLoadService.DOWNLOAD_START);
		bundle.putSerializable(RemoteDownLoadService.PARMA_MODULE_INFO,
				downloadModule);
		intent.putExtras(bundle);
		intent.setPackage(DDApplication.getApplication().getPackageName());
		mContext.startService(intent);
	}

	public void pauseAll() {

	}

	public void registerDownloadListener(Class<?> moduleKey,
			IDownloadListener downloadListener) {
		if (moduleKey == null || downloadListener == null) {
			throw new NullPointerException("[IDownloadListener not null]");
		}
		mListeners.put(moduleKey, downloadListener);

	}

	public void unRegisterDownloadListener(Class<?> moduleKey) {
		if (moduleKey != null) {
			mListeners.remove(moduleKey);
		}
	}

	@Override
	public void startDownload(DownloadModule downloadModule, Download download,
			Object... params) {
		startIntentDownload(downloadModule, download);
	}

	@Override
	public void pauseDownload(DownloadModule downloadModule, Download download,
			Object... params) {
		Intent intent = new Intent(
				RemoteDownLoadService.REMOTE_DOWNLOAD_SERVICE_ACTION);
		Bundle bundle = new Bundle();
		bundle.putSerializable(RemoteDownLoadService.PARMA_DOWNLOAD_INFO,
				download);
		bundle.putInt(RemoteDownLoadService.PARMA_DOWNLOAD_TYPE,
				RemoteDownLoadService.DOWNLOAD_PAUSE);
		bundle.putSerializable(RemoteDownLoadService.PARMA_MODULE_INFO,
				downloadModule);
		intent.putExtras(bundle);
		mContext.startService(intent);
	}

	@Override
	public void resumeDownload(DownloadModule downloadModule,
			Download download, Object... params) {
		startIntentDownload(downloadModule, download);
	}
}
