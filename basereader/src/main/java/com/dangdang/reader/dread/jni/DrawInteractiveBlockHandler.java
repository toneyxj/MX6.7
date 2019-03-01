package com.dangdang.reader.dread.jni;

import android.graphics.Bitmap;

import com.dangdang.zframework.log.LogM;

public class DrawInteractiveBlockHandler {

	private int mWidth;
	private int mHeight;
	private int[] mColors;
	
	public void setRenderWidth (int nWidth) {
		this.mWidth = nWidth;
	}
	
	public void setRenderHeight (int nHeight) {
		this.mHeight = nHeight;
	}
	
	public int getRenderWidth () {
		return this.mWidth;
	}
	
	public int getRenderHeight () {
		return this.mHeight;
	}
	
	public void setColors(int[] colors){
		mColors = colors;
		if(colors != null){
		}
	}
	
	public Bitmap getBitmap(){
		Bitmap bitmap = null;
		if(mColors != null && mWidth != 0 && mHeight != 0){
			try {
				bitmap = Bitmap.createBitmap(mColors, mWidth, mHeight, Bitmap.Config.ARGB_8888);
			} catch (Throwable e) {
				LogM.e(" SRH newbmp error " + e);
				System.gc();
				System.gc();
			}
		}
		return bitmap;
	}

}
