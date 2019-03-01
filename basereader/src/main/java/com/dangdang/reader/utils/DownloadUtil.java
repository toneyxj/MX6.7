package com.dangdang.reader.utils;

import java.io.File;
import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.HashMap;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.widget.RemoteViews;

import com.dangdang.reader.R;
import com.dangdang.reader.service.ApkDownload;
import com.dangdang.reader.service.RemoteDownloadServiceUtils;
import com.dangdang.zframework.log.LogM;
import com.dangdang.zframework.network.download.IDownloadManager;
import com.dangdang.zframework.network.download.DownloadManagerFactory.DownloadModule;
import com.dangdang.zframework.network.download.IDownloadManager.DownloadExp;
import com.dangdang.zframework.network.download.IDownloadManager.DownloadInfo;
import com.dangdang.zframework.network.download.IDownloadManager.IDownloadListener;
import com.dangdang.zframework.utils.MathExtendUtil;
import com.dangdang.zframework.utils.UiUtil;

public class DownloadUtil {

	private Context mContext;
	private MyDownloadListener mListener;
	private RemoteDownloadServiceUtils mUtil;
	private DownloadModule mModule = new DownloadModule("apk");
	private HashMap<String, ApkDownload> mMap = new HashMap<String, ApkDownload>();
	private Handler mHandler;

	private static DownloadUtil util = null;

	// 通知栏相关
	private Notification mNotify;
	private long fileSize;
	private NotificationManager mNotifyMgr;
	private int mNotifyId = R.drawable.icon;
	private long mPreProgress = 0;

	private DownloadUtil(Context context) {
		mContext = context.getApplicationContext();
		mHandler = new MyHandler(this);
		startDownloadService();
		mNotifyMgr = (NotificationManager) mContext
				.getSystemService(Context.NOTIFICATION_SERVICE);
	}

	public synchronized static DownloadUtil getInstance(Context context) {
		if (util == null) {
			util = new DownloadUtil(context);
		}
		return util;
	}

	public void addDownloadTask(final String url, final String path,
			final boolean isShow) {
		
		if (mMap.containsKey(url)) {
			ApkDownload down = mMap.get(url);
			down.setShow(isShow);
			if(isShow)
				sendNotifyOfDownloading();
			return;
		}
		mHandler.postDelayed(new Runnable() {
			public void run() {
				startDownload(url, path, isShow);
			}
		}, 1000);
	}

	private void startDownloadService() {
		mUtil = RemoteDownloadServiceUtils.getInstance(mContext);
		mListener = new MyDownloadListener();
		mUtil.registerDownloadListener(DownloadModule.class, mListener);
	}

	private void startDownload(String url, String path, boolean isShow) {
		ApkDownload down = new ApkDownload(mModule);
		down.setUrl(url);
		down.setLocalFile(path);
		down.setShow(isShow);
		mMap.put(down.getUrl(), down);
		mUtil.startDownload(mModule, down);
		if(isShow)
			sendNotifyOfDownloading();
	}

	class MyDownloadListener implements IDownloadListener {

		@Override
		public void onDownloading(DownloadInfo info) {
			// TODO Auto-generated method stub
			ApkDownload down = mMap.get(info.url);
			if (down == null)
				return;
			if (info.progress == null) {
				if (down.isShow()) {
					sendNotifyOfDownloading();
				}
			} else {
				if (down.isShow()) {
					updateProgress(info.progress);
				} else
					mNotifyMgr.cancel(mNotifyId);
			}
		}

		@Override
		public void onPauseDownload(DownloadInfo info) {
			// TODO Auto-generated method stub
			LogM.l("on pause download " + info.url);
		}

		@Override
		public void onDownloadFinish(DownloadInfo info) {
			// TODO Auto-generated method stub
			LogM.l("on download finish " + info.url);
			mMap.remove(info.url);
			installApk(info.file);
		}

		@Override
		public void onFileTotalSize(DownloadInfo info) {
			// TODO Auto-generated method stub
			fileSize = info.progress.total;
			LogM.l("on total size " + info.progress.toString());
			ApkDownload down = mMap.get(info.url);
			if (down != null) {
				down.setFileSize(info.progress.total);
			}
		}

		@Override
		public void onDownloadFailed(DownloadInfo info, DownloadExp exp) {
			// TODO Auto-generated method stub
			mMap.remove(info.url);
			mNotifyMgr.cancel(mNotifyId);
			if (exp != null)
				UiUtil.showToast(mContext, "下载失败 " + exp.errMsg);
			else
				UiUtil.showToast(mContext, "下载失败");
		}
	}

	private void installApk(File file) {
		mNotifyMgr.cancel(mNotifyId);
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file),
				"application/vnd.android.package-archive");
		mContext.startActivity(intent);
	}

	private void sendNotifyOfDownloading() {

		if(mNotify != null)
			return;
		
		String ticker = mContext.getString(R.string.app_name);
		mNotify = new Notification();
		mNotify.icon = R.drawable.icon;
		mNotify.flags = Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
		mNotify.tickerText = ticker;

		RemoteViews remoteView = new RemoteViews(mContext.getPackageName(),
				R.layout.book_upgrade_msg);
		remoteView.setProgressBar(R.id.msg_upgrade_progressbar, 100, 1, false);
		if (fileSize <= 0)
			remoteView.setTextViewText(R.id.msg_upgrade_progress_text, "");
		else
			remoteView.setTextViewText(
					R.id.msg_upgrade_progress_text,
					"0MB / "
							+ MathExtendUtil.divide(String.valueOf(fileSize),
									String.valueOf(1024 * 1024)) + "MB");
		remoteView.setTextViewText(R.id.msg_upgrade_progress_percent, "0.00%");
		remoteView.setTextViewText(R.id.msg_upgrade_apptitle, ticker);
		remoteView.setTextViewText(R.id.msg_upgrade_download_text,
				mContext.getString(R.string.downloadstatus_downloading));

		mNotify.contentView = remoteView;

		mNotifyMgr.notify(mNotifyId, mNotify);
	}

	private void updateProgress(IDownloadManager.Progress progress) {
		String tmp = MathExtendUtil.divide(progress.progress, 1024 * 1024)
				+ "MB / " + MathExtendUtil.divide(progress.total, 1024 * 1024)
				+ "MB";
		float f = 0;
		try {
			f = progress.progress * 100f / progress.total;
		} catch (Exception e) {
		}
		DecimalFormat df = new DecimalFormat("0.00");

		int tmpP = f < 1 ? 2 : (int) f;
		String tmpPercent = df.format(f) + "%";

		if (tmpP == 100 || tmpP % 5 == 0) {
			if (mNotify == null) {
				fileSize = progress.total;
				sendNotifyOfDownloading();
			}
			if (mNotify != null && mPreProgress != tmpP) {
				mNotify.contentView.setProgressBar(
						R.id.msg_upgrade_progressbar, 100, tmpP, false);
				mNotify.contentView.setTextViewText(
						R.id.msg_upgrade_progress_text, tmp);
				mNotify.contentView.setTextViewText(
						R.id.msg_upgrade_progress_percent, tmpPercent);
				mNotify.tickerText = null;
				mNotifyMgr.notify(mNotifyId, mNotify);
			}
			mPreProgress = tmpP;
		}
	}

	private static class MyHandler extends Handler {
		private final WeakReference<DownloadUtil> mFragmentView;

		MyHandler(DownloadUtil view) {
			this.mFragmentView = new WeakReference<DownloadUtil>(view);
		}

		@Override
		public void handleMessage(Message msg) {
			DownloadUtil service = mFragmentView.get();
			if (service != null) {
				super.handleMessage(msg);
				try {

				} catch (Exception e) {

				}
			}
		}
	}
}
