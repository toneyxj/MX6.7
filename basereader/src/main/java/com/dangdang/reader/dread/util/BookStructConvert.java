package com.dangdang.reader.dread.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.dangdang.reader.dread.data.ReadInfo;
import com.dangdang.reader.dread.format.Book;
import com.dangdang.reader.dread.format.Book.BaseNavPoint;
import com.dangdang.reader.dread.format.Chapter;
import com.dangdang.reader.dread.format.txt.TxtBook.TxtNavPoint;
import com.dangdang.reader.dread.format.txt.TxtChapter;
import com.dangdang.reader.dread.jni.ChaterInfoHandler;
import com.dangdang.zframework.log.LogM;

public class BookStructConvert {

	public final static String K_ChapterList = "chapters";
	public final static String K_Path = "path";
	public final static String K_ChapterArrs = "carrs";
	public final static String K_ChapterName = "chaptername";
	public final static String K_StartByte = "startbyte";
	public final static String K_EndByte = "endbyte";

	public static byte[] convertBookToData(final Book book) {

		if (book == null || !book.hasChapterList()) {
			return null;
		}
		byte[] datas = null;

		// datas = convertBookToDataForJson(book);
		// datas = convertBookToDataForSerializable(book);

		SeriBook seriBook = new SeriBook();
		seriBook.setChapters(book.getChapterList());
		seriBook.setFileSize(book.getFileSize());

		datas = convertObjectToByte(seriBook);

		return datas;
	}

	private static byte[] convertBookToDataForJson(final Book book) {
		byte[] datas = null;
		try {
			final List<Chapter> chapters = book.getChapterList();
			final JSONObject bookJsonObj = new JSONObject();
			final JSONObject jsonArray = buildChaptersToJsonArray(chapters);
			bookJsonObj.put(K_ChapterList, jsonArray);

			datas = bookJsonObj.toString().getBytes();

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return datas;
	}

	private static JSONObject buildChaptersToJsonArray(
			final List<Chapter> chapters) throws JSONException {

		final JSONObject chaptersJson = new JSONObject();
		final JSONArray jsonArray = new JSONArray();
		chaptersJson.put(K_Path, chapters.get(0).getPath());

		TxtChapter txtChapter = null;
		JSONObject jsonObj = null;
		for (Chapter chapter : chapters) {
			txtChapter = (TxtChapter) chapter;
			jsonObj = new JSONObject();
			// jsonObj.put(K_Path, txtChapter.getPath());
			jsonObj.put(K_ChapterName, txtChapter.getChapterName());
			jsonObj.put(K_StartByte, txtChapter.getStartByte());
			jsonObj.put(K_EndByte, txtChapter.getEndByte());

			jsonArray.put(jsonObj);
		}
		chaptersJson.put(K_ChapterArrs, jsonArray);

		return chaptersJson;
	}

	public static SeriBook dataToBookStruct(byte[] bookStructData) {

		if (bookStructData == null || bookStructData.length == 0) {
			LogM.i("BookStruct", " dataToBookStruct in null ");
			return null;
		}
		// dataToBookStructForJson(bookStructData, readInfo);

		return (SeriBook) convertByteToObject(bookStructData);
		// return dataToBookStructForSerializable(bookStructData);
	}

	private static void dataToBookStructForJson(byte[] bookStructData,
			ReadInfo readInfo) {
		try {
			final String jsonStr = new String(bookStructData);
			final JSONObject bookJsonObj = new JSONObject(jsonStr);
			final JSONObject chapterStructJson = bookJsonObj
					.getJSONObject(K_ChapterList);
			final String path = chapterStructJson.getString(K_Path);
			final JSONArray jsonArray = chapterStructJson
					.getJSONArray(K_ChapterArrs);
			JSONObject jsonObj = null;

			final List<Chapter> chapterList = new ArrayList<Chapter>();
			final List<BaseNavPoint> navPointList = new ArrayList<BaseNavPoint>();
			TxtChapter txtChapter = null;
			TxtNavPoint txtNavPoint = null;
			for (int i = 0, len = jsonArray.length(); i < len; i++) {
				jsonObj = jsonArray.getJSONObject(i);
				txtChapter = new TxtChapter();

				// txtChapter.setPath(jsonObj.optString(K_Path));
				txtChapter.setPath(path);
				txtChapter.setChapterName(jsonObj.optString(K_ChapterName));
				txtChapter.setStartByte(jsonObj.optInt(K_StartByte));
				txtChapter.setEndByte(jsonObj.optInt(K_EndByte));

				chapterList.add(txtChapter);

				if (!TextUtils.isEmpty(txtChapter.getChapterName())) {
					txtNavPoint = new TxtNavPoint();
					txtNavPoint.setPath(txtChapter.getPath());
					txtNavPoint.setName(txtChapter.getChapterName());
					txtNavPoint.setStartByte(txtChapter.getStartByte());
					txtNavPoint.setEndByte(txtChapter.getEndByte());

					navPointList.add(txtNavPoint);
				}
			}

			readInfo.setChapterList(chapterList);
			readInfo.setNavPointList(navPointList);

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public static byte[] convertObjectToByte(Object object) {

		byte[] datas = null;
		ByteArrayOutputStream outPutSteam = null;
		ObjectOutputStream objOutStream = null;
		try {
			outPutSteam = new ByteArrayOutputStream();
			objOutStream = new ObjectOutputStream(outPutSteam);
			objOutStream.writeObject(object);
			datas = outPutSteam.toByteArray();

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			closeStream(outPutSteam);
			closeStream(objOutStream);
		}

		return datas;
	}

	public static Object convertByteToObject(byte[] seriDatas) {
		Object seriObj = null;
		InputStream inStream = null;
		ObjectInputStream objInStream = null;
		try {
			final byte[] datas = seriDatas;
			inStream = new ByteArrayInputStream(datas);
			objInStream = new ObjectInputStream(inStream);
			seriObj = objInStream.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeStream(inStream);
			closeStream(objInStream);
		}
		return seriObj;
	}

	public static void closeStream(Closeable stream) {
		try {
			if (stream != null) {
				stream.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static class SeriBook implements Serializable {

		private static final long serialVersionUID = 1L;

		private long mFileSize = 0;
		private List<Chapter> mChapters;

		public long getFileSize() {
			return mFileSize;
		}

		public void setFileSize(long fileSize) {
			this.mFileSize = fileSize;
		}

		public List<Chapter> getChapters() {
			return mChapters;
		}

		public void setChapters(List<Chapter> chapters) {
			this.mChapters = chapters;
		}

	}
	
	
	/**
	 * 排版结果序列化
	 * 
	 * @author luxu
	 */
	public static class ComposingSeriBook implements Serializable {

		private static final long serialVersionUID = 1L;

		private int mPageCount = 0;
		private Map<Chapter, ChaterInfoHandler> mPageInfoCache = null;

		public Map<Chapter, ChaterInfoHandler> getPageInfoCache() {
			return mPageInfoCache;
		}

		public void setPageInfoCache(
				Map<Chapter, ChaterInfoHandler> pageInfoCache) {
			this.mPageInfoCache = pageInfoCache;
		}

		public int getPageCount() {
			return mPageCount;
		}

		public void setPageCount(int mPageCount) {
			this.mPageCount = mPageCount;
		}

	}
}
