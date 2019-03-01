package com.dangdang.reader.utils;

import android.text.TextUtils;
import android.widget.ImageView;

import com.dangdang.reader.R;
import com.dangdang.zframework.network.image.ImageManager;

public class ResourceManager {

	public ResourceManager(){
		init();
	}
	
	private void init(){
	}
	
	public void setCover(ImageView view, String path, int type){
		if(TextUtils.isEmpty(path)){
			setDefaultCover(view, type);
			return;
		}
		switch(type){
		case 0:
			ImageManager.getInstance().dislayImage("file://" + path, view, R.drawable.android_pdf_default);
			break;
		case 1:
			ImageManager.getInstance().dislayImage("file://" + path, view, R.drawable.bg_epub);
			break;
		}
	}
	
	private void setDefaultCover(ImageView view, int type){
		switch(type){
		case 0:
			view.setImageResource(R.drawable.android_pdf_default);
			break;
		case 1:
			view.setImageResource(R.drawable.bg_epub);
		}
	}
}
