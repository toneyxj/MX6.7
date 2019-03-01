package com.dangdang.reader.dread.jni;

import android.graphics.Bitmap;

import com.dangdang.zframework.log.LogM;


public class StringRenderHandler {

	private int mWidth;
	private int mHeight;
	private int[] mColors;
	
	public void setRenderWidth(int width){
		mWidth = width;
		printLog(" setRenderWidth " + width);
	}
	
	public void setRenderHeight(int height){
		mHeight = height;
		printLog(" setRenderHeight " + height);
	}
	
	public void setColors(int[] colors){
		mColors = colors;
		if(colors != null){
			printLog(" setColors " + colors.length);
		}
	}
	
	public Bitmap getBitmap(){
		Bitmap bitmap = null;
		if(mColors != null && mWidth != 0 && mHeight != 0){
			try {
				bitmap = Bitmap.createBitmap(mColors, mWidth, mHeight, Bitmap.Config.ARGB_8888);
				float width = mWidth;
				float height = mHeight;
				if (mHeight > 4096) {
					float scale = height / (float)4096.0;
					width = width / scale;
					height = (float)4096.0;
					Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, (int)width, (int)height, false);
					if (scaledBitmap != bitmap) {
						bitmap.recycle();
						bitmap = scaledBitmap;
					}
				}
			} catch (Throwable e) {
				LogM.e(" SRH newbmp error " + e);
				System.gc();
				System.gc();
			}
		}
		return bitmap;
	}

	protected void printLog(String log){
		//LogM.i(getClass().getSimpleName(), log);
	}
	
}
