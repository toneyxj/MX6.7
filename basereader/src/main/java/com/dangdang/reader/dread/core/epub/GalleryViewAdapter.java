package com.dangdang.reader.dread.core.epub;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.dangdang.reader.dread.jni.BaseJniWarp;
import com.dangdang.reader.dread.jni.DrmWarp;
import com.dangdang.zframework.log.LogM;

public class GalleryViewAdapter extends BaseAdapter {
	private List<String> list;
	private List<String> listImgText;
	private Context context;
	private byte[][] mBitmapData;
	private boolean mSaveBitmapData;

	public GalleryViewAdapter(Context context, List<String> list,
			boolean saveBitmapData) {
		this.context = context;
		this.list = list;
		mSaveBitmapData = saveBitmapData;
		if (mSaveBitmapData && list != null && list.size() > 0) {
			mBitmapData = new byte[list.size()][];
		}
	}

	@Override
	public int getCount() {
		return list == null ? 0 : list.size();
	}

	@Override
	public Object getItem(int position) {
		return list == null ? null : list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public byte[] getBitmapData(int position) {
		return mBitmapData == null ? null : mBitmapData[position];
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		GalleryImageView imageView = null;
		if (convertView == null) {
			imageView = new GalleryImageView(context);
		} else {
			imageView = (GalleryImageView) convertView;
		}
		imageView.setTag(position);
		String url = list.get(position);
		loadImage(imageView, url, position);
		return imageView;
	}

	private void loadImage(ImageView imageView, String url, int index) {
		Bitmap bmp = getBitmapWithUrl(url, index);
		if (bmp == null) {
			printLogE(" loadImage bmp == null ");
			return;
		} else {
			imageView.setImageBitmap(bmp);
		}
	}

	private Bitmap getBitmapWithUrl(String url, int index) {
		if (url == null || url.equals("")) {
			return null;
		}
		DrmWarp drmWarp = DrmWarp.getInstance();
		drmWarp.deCryptPic(url, getEBookType());
		byte[] deData = drmWarp.getDeCryptAfterData();
		if (deData != null && deData.length > 0) {
			try {
				/*
				 * CompressFormat imgFormat =
				 * FileFormat.getCompressFormat(deData, CompressFormat.PNG);
				 */
				Bitmap bitmap = BitmapFactory.decodeByteArray(deData, 0,
						deData.length);
				if (mSaveBitmapData && mBitmapData != null) {
					mBitmapData[index] = deData;
				}
				return bitmap;
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
				return null;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

	private int getEBookType() {
		int eType = BaseJniWarp.BOOKTYPE_DD_DRM_EPUB;
		try {
			eType = ReaderAppImpl.getApp().getReadInfo().getEBookType();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return eType;
	}

	public void clear() {
		mBitmapData = null;
		if (list != null && list.size() > 0) {
			list = null;
		}
	}

	protected void printLog(String log) {
		LogM.i(getClass().getSimpleName(), log);
	}

	protected void printLogE(String log) {
		LogM.e(getClass().getSimpleName(), log);
	}

}
