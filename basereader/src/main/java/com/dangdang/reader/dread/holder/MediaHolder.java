package com.dangdang.reader.dread.holder;

import java.io.File;

/**
 * @author luxu
 */
public class MediaHolder {

	private File mLastFile;

	public MediaHolder() {
	}

	public String getMediaPath(MediaType type, String innerPath, String bookFile) {

		File mediaDir = new File(bookFile).getParentFile();
		mLastFile = new File(mediaDir, getMediaName(type, innerPath));

		return mLastFile.toString();
		/*
		 * File tmpFile = DangdangFileManager.getRootDir(mContext); return new
		 * File(tmpFile, "00010.mp3").toString();
		 */
	}

	public String getMediaName(MediaType type, String innerPath) {
		return innerPath.hashCode() + ".dd";
	}

	public void deleteLast() {
		try {
			if (mLastFile != null && mLastFile.exists()) {
				mLastFile.delete();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static enum MediaType {

		Audio, Video;

		public String getMimeType() {
			return Audio == this ? "audio/mpeg" : "video/mp4";
		}

	}

}
