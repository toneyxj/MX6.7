package com.dangdang.reader.dread.jni;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Rect;

import com.dangdang.reader.dread.data.GallaryData;
import com.dangdang.zframework.log.LogM;

public class ImageGalleryHandler {
	
	private Rect mImageRect;
	private Rect mPointsRect;
	private Rect mGalleryRect;
	private boolean mHasImgDesc = false;
	private int mCount;
	private List<String> mImgPaths;
	private List<String> mImgTexts;
	private int mImgBgColor;
	
	public void setGalleryImageRect(double dLeft, double dTop, double dRight, double dBottom){
		printLog(" setGallaryImageRect " + dLeft + "," + dTop + "," + dRight + "," + dBottom);
		mImageRect = new Rect();
		mImageRect.left = (int) dLeft;
		mImageRect.top = (int) dTop;
		mImageRect.right = (int) dRight;
		mImageRect.bottom = (int) dBottom;
	}
	
	public void setGalleryRect(double dLeft, double dTop, double dRight, double dBottom){
		printLog(" setGalleryRect " + dLeft + "," + dTop + "," + dRight + "," + dBottom);
		mGalleryRect = new Rect();
		mGalleryRect.left = (int) dLeft;
		mGalleryRect.top = (int) dTop;
		mGalleryRect.right = (int) dRight;
		mGalleryRect.bottom = (int) dBottom;
	}

	public void setGalleryScrollRect(double dLeft, double dTop, double dRight, double dBottom){
		printLog(" setGallaryScrollRect " + dLeft + "," + dTop + "," + dRight + "," + dBottom);
		mPointsRect = new Rect(); 
		mPointsRect.left = (int) dLeft;
		mPointsRect.top = (int) dTop;
		mPointsRect.right = (int) dRight;
		mPointsRect.bottom = (int) dBottom;
	}
	
	public void setGalleryHasText(boolean bHasText){
		printLog(" setGallaryHasText " + bHasText);
		mHasImgDesc = bHasText;
	}
	
	public void setGalleryImgBgColor(int nColor) {
		mImgBgColor = nColor;
	}
	
	public void setGalleryImageCount(int nImageCount){
		printLog(" setGallaryImageCount " + nImageCount);
		mCount = nImageCount;
	}
	
	public void addGalleryImageItem(String strImageFile, String strImgText){
		printLog(" addGallaryImageFile " + strImageFile);
		if(mImgPaths == null){
			mImgPaths = new ArrayList<String>();
		}
		mImgPaths.add(strImageFile);
		
		if(mImgTexts == null){
			mImgTexts = new ArrayList<String>();
		}
		mImgTexts.add(strImgText);
	}
	
	public Rect getImageRect() {
		return mImageRect;
	}

	public Rect getPointsRect() {
		return mPointsRect;
	}
	
	public Rect getGalleryRect(){
		return mGalleryRect;
	}

	public boolean isHasImgDesc() {
		return mHasImgDesc;
	}

	public int getCount() {
		return mCount;
	}

	public List<String> getImgPaths() {
		return mImgPaths;
	}
	
	public GallaryData[] getGallerys(){
		if(mImgPaths == null || mImgPaths.size() == 0){
			return null;
		}
		GallaryData[] gals = new GallaryData[1];
		GallaryData gal = new GallaryData();
		gal.setCount(mCount);
		gal.setFiles(mImgPaths.toArray(new String[mImgPaths.size()]));
		gal.setImgTexts(mImgTexts.toArray(new String[mImgTexts.size()]));
		gal.setHasImgDesc(mHasImgDesc);
		gal.setImageRect(mImageRect);
		gal.setPointsRect(mPointsRect);
		gal.setGalleryRect(mGalleryRect);
		gal.setImgBgColor(mImgBgColor);
		gals[0] = gal;
		
		return gals;
	}

	protected void printLog(String log){
		LogM.i(getClass().getSimpleName(), log);
	}
	
}
