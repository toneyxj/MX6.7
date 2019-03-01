package com.dangdang.reader.dread.core.epub;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class InteractiveBlockViewAdapter extends GalleryViewAdapter {

	private Bitmap mBmp;
	private Context context;

	
	public InteractiveBlockViewAdapter(Context context) {
		super(context, null, false);
		// TODO Auto-generated constructor stub
		this.context = context;
	}


	public Bitmap getBmp() {
		return mBmp;
	}


	public void setBmp(Bitmap mBmp) {
		this.mBmp = mBmp;
	}

	public int getCount() {
		return 1;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		GalleryImageView imageView = null;
		if (convertView == null) {
			imageView = new GalleryImageView(context);
		} else {
			imageView = (GalleryImageView) convertView;
		}
		imageView.setImageBitmap(mBmp);
		imageView.setTag(position);
		return imageView;
	}

}
