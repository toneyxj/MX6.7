package com.dangdang.reader.dread.media;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import com.dangdang.reader.dread.holder.MediaHolder.MediaType;
import com.dangdang.reader.dread.jni.BaseJniWarp;
import com.dangdang.reader.dread.jni.EpubWrap;
import com.dangdang.reader.dread.media.FileEntry.FileType;

/**
 * @author luxu
 */
public class MyStreamOverHttp extends StreamOverHttp {

	public MyStreamOverHttp(FileEntry f, String forceMimeType)
			throws IOException {
		super(f, forceMimeType);
	}

	public void prepare(FileEntry fileEntry) {
		if (fileEntry.getType() == FileType.FileInner) {
			EpubWrap mDrwrap = new EpubWrap();
			final String innerPath = fileEntry.getInnerPath();
			final String localPath = fileEntry.f.getAbsolutePath();
			mDrwrap.saveFileToDisk(innerPath, localPath);
		}
	}

	@Override
	protected HttpSession createHttpSession(Socket accept, FileEntry f) {
		return new MyHttpSession(accept, f.getBookType());
	}

	protected class MyHttpSession extends HttpSession {

		private int mBookType;

		MyHttpSession(Socket s) {
			super(s);
		}

		MyHttpSession(Socket s, int bookType) {
			super(s);
			mBookType = bookType;
		}

		protected InputStream openRandomAccessInputStream(FileEntry file) {
			try {
				printLog(" openRandomAccessInputStream " + file.f);//
				// return new RandomAccessInputStream(file.f);
				InputStream inStream = null;
				if (mBookType == BaseJniWarp.BOOKTYPE_THIRD_EPUB
						|| file.mimeType.equals(MediaType.Video.getMimeType())) {
					inStream = new RandomAccessInputStream(file.f);
				} else {
					inStream = new MyRandomAccessInputStream(file.f);
				}
				return inStream;
			} catch (Exception e) {
				printLogE(e.toString());
			}
			return null;
		}

	}

}
