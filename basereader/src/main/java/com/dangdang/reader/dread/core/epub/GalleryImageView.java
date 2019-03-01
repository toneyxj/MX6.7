package com.dangdang.reader.dread.core.epub;

import android.content.Context;
import android.graphics.Bitmap;

import com.dangdang.reader.dread.format.pdf.BitmapHolder;
import com.dangdang.zframework.view.DDImageView;

public class GalleryImageView extends DDImageView {
	private BitmapHolder mBitmapHolder;

	public GalleryImageView(Context context) {
		super(context);
		init();
	}

	private void init() {
		mBitmapHolder = new BitmapHolder();
	}

	@Override
	public void setImageBitmap(Bitmap bm) {
		if (bm != null) {
			mBitmapHolder.setBm(bm);
		}
		//printLog(" Detached gallery setImageBitmap " + bm);
		super.setImageBitmap(bm);
	}

	public void releaseBitmap() {
		mBitmapHolder.drop();
	}

}
