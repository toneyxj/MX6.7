package com.dangdang.reader.dread.core.base;

import com.dangdang.reader.dread.core.epub.GalleryView.OnGalleryPageChangeListener;

import android.content.Context;

public abstract class BaseEpubPageView extends BasePageView implements IEpubPageView {

	public BaseEpubPageView(Context context) {
		super(context);
	}

	public void updatePageStyle(){
		
	}
	
	public void setOnGalleryPageChangeListener(OnGalleryPageChangeListener l){
		
	}
	
	
}
