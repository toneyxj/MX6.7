package com.dangdang.reader.dread.media;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.dangdang.reader.dread.jni.EpubWrap;
import com.dangdang.reader.dread.media.FileEntry.FileType;

public class VideoStreamOverHttp extends MyStreamOverHttp {
	private EpubWrap mDRWrap;
	private final int minUnit = 64 * 1024;// 64K
	private final int unitLen = 5 * minUnit;//
	static {
		System.loadLibrary("ddlayoutkit");
	}

	public VideoStreamOverHttp(FileEntry f, String forceMimeType)
			throws IOException {
		super(f, forceMimeType);
		mDRWrap = new EpubWrap();
	}

	public void prepare(FileEntry fileEntry) {
		if (fileEntry.getType() == FileType.FileInner) {
			EpubWrap mDrwrap = new EpubWrap();
			final String innerPath = fileEntry.getInnerPath();
			final String localPath = fileEntry.f.getAbsolutePath();
			final String localFilePath = localPath + "d";
			mDrwrap.saveFileToDisk(innerPath, localFilePath);
			File file = new File(localFilePath);
			if (file.exists() && file.length() > 0) {
				File desFile = new File(localPath);
				copy(file, desFile);
			}
		}
	}

	public void copy(File surFile, File desFile) {
		InputStream fis = null;
		OutputStream fos = null;
		try {
			fis = new BufferedInputStream(new FileInputStream(surFile));
			fos = new BufferedOutputStream(new FileOutputStream(desFile));
			byte[] buf = new byte[unitLen];
			int i;
			while ((i = fis.read(buf)) != -1) {
				byte[] tmpDestBytes = new byte[i];
				if (i < buf.length) {
					byte[] temp = correctBufSize(buf, i);
					mDRWrap.decryptMedia(temp, tmpDestBytes);
				} else {
					mDRWrap.decryptMedia(buf, tmpDestBytes);
				}
				fos.write(tmpDestBytes, 0, i);
			}
			buf = null;
		} catch (Exception e) {
		} finally {
			try {
				fis.close();
				fos.close();
				if (surFile.exists()) {
					surFile.delete();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}

	private byte[] correctBufSize(byte[] buf, int i) {
		byte[] temp = new byte[i];
		System.arraycopy(buf, 0, temp, 0, i);
		return temp;
	}
}
