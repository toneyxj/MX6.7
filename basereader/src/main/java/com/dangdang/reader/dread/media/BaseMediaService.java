package com.dangdang.reader.dread.media;

import java.io.File;
import java.io.IOException;

import com.dangdang.reader.dread.holder.MediaHolder.MediaType;
import com.dangdang.reader.dread.jni.BaseJniWarp;
import com.dangdang.reader.dread.media.FileEntry.FileType;
import com.dangdang.reader.dread.media.StreamOverHttp.PrepareListener;
import com.dangdang.zframework.log.LogM;

/**
 * @author luxu
 */
public class BaseMediaService {

	protected String mPath;
	private StreamOverHttp mServer;
	private MediaListener mMediaListener;

	private final String localUrl = "http://127.0.0.1:"
			+ StreamOverHttp.SERVER_PORT;

	public BaseMediaService() {
	}

	public void openServer(String innerPath, String path, MediaType type,
			FileType fileType, int bookType, PrepareListener l)
			throws IOException {

		final String nowPath = path;
		if (!nowPath.equals(mPath) || mServer == null) {
			mPath = path;
			FileEntry fe = new FileEntry();
			fe.f = new File(mPath);
			fe.mimeType = type.getMimeType();
			fe.setInnerPath(innerPath);
			fe.setType(fileType);
			fe.setBookType(bookType);
			if (fe.mimeType.equals(MediaType.Video.getMimeType())
					&& bookType != BaseJniWarp.BOOKTYPE_THIRD_EPUB) {
				mServer = new VideoStreamOverHttp(fe, null);
			} else {
				mServer = new MyStreamOverHttp(fe, null);
			}
			mServer.startServer();
		} else {
			printLog(" openServer: already open ");
			if (l != null) {
				l.prepareFinish(false);
			}
		}
		mServer.setPrepareListener(l);
	}

	public void closeServer() {
		if (mServer != null) {
			mServer.close();
			mServer = null;
		}
	}

	public String getPath() {
		return mPath;
	}

	public String getLocalServerPath() {
		File file = new File(mPath);
		String localServer = localUrl + File.separator + file.getName();
		return localServer;
	}

	public MediaListener getMediaListener() {
		return mMediaListener;
	}

	public void setMediaListener(MediaListener l) {
		this.mMediaListener = l;
	}

	protected void printLog(String msg) {
		LogM.i(getClass().getSimpleName(), msg);
	}

	protected void printLogE(String msg) {
		LogM.e(getClass().getSimpleName(), msg);
	}

	public interface MediaListener {

		public void onDuration(int duration);

	}

}
