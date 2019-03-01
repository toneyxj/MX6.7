package com.dangdang.reader.dread.holder;

import java.util.HashSet;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Rect;

import com.dangdang.reader.dread.config.PageType;
import com.dangdang.reader.dread.data.GallaryData;
import com.dangdang.reader.dread.format.IndexRange;
import com.dangdang.reader.dread.jni.InteractiveBlockHandler.InteractiveBlock;
import com.dangdang.zframework.log.LogM;
import com.dangdang.zframework.utils.BitmapUtil;

import java.util.Arrays;

public class PageBitmap {

	/**
	 * 页面类型
	 */
	private HashSet<PageType> pageType = new HashSet<PageType>();
	private IndexRange pageRange;
	private Bitmap bitmap;
	private GallaryData[] gallarys;
	private Rect mVideoRect;
	private List<InteractiveBlock> mlistInteractiveBlocks;

	
	public PageBitmap(){
	}

	public PageBitmap(HashSet<PageType> pageType, IndexRange pageRange, Bitmap bitmap) {
		super();
		this.pageType = pageType;
		this.pageRange = pageRange;
		this.bitmap = bitmap;
	}

	public Bitmap getBitmap() {
		return bitmap;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

	public HashSet<PageType> getPageType() {
		return pageType;
	}

	public void setPageType(HashSet<PageType> pageType) {
		this.pageType = pageType;
	}

	public IndexRange getPageRange() {
		return pageRange;
	}

	public void setPageRange(IndexRange pageRange) {
		this.pageRange = pageRange;
	}
	
	public GallaryData[] getGallarys() {
		return gallarys;
	}

	public void setGallarys(GallaryData[] gallarys) {
		this.gallarys = gallarys;
	}
	
	public void setVideoRect(Rect rect){
		this.mVideoRect = rect;
	}
	public Rect getVideoRect(){
		return mVideoRect;
	}
	public List<InteractiveBlock> getListInteractiveBlocks() {
		return mlistInteractiveBlocks;
	}

	public void setListInteractiveBlocks(List<InteractiveBlock> listInteractiveBlocks) {
		this.mlistInteractiveBlocks = listInteractiveBlocks;
	}
	public void free(){
		BitmapUtil.recycle(bitmap);
        gallarys=null;
        mVideoRect=null;
	}
	
	public boolean isShowHeader(){
		return !PageType.isNoHeader(getPageType());
	}
	
	public boolean isShowFooter(){
		return !PageType.isNoFooter(getPageType());
	}
	
	public boolean hasGallary(){
		return PageType.isGallary(getPageType()) && hasGallaryData();
	}
	
	public boolean hasGallaryData(){
		return gallarys != null && gallarys.length > 0;
	}
	public boolean hasVideo(){
		return mVideoRect!=null;
	}
	public boolean hasInteractiveBlock(){
		return mlistInteractiveBlocks!=null;
	}
	
	public boolean isUseable(){
		return BitmapUtil.isAvailable(bitmap);
	}
	
	protected void printLog(String log){
		LogM.i(getClass().getSimpleName(), log);
	}

    @Override
    public String toString() {
        return "PageBitmap{" +
                "pageType=" + pageType +
                ", pageRange=" + pageRange +
                ", bitmap=" + bitmap +
                ", gallarys=" + Arrays.toString(gallarys) +
                ", mVideoRect=" + mVideoRect +
                '}';
    }
}
