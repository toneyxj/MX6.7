package com.dangdang.reader.view;

import android.view.View;
import android.widget.PopupWindow;

public class MyPopupWindow extends PopupWindow {

	public MyPopupWindow(View parent, int width, int height) {
		// TODO Auto-generated constructor stub
		super(parent, width, height);
	}

	@Override
	public void showAtLocation(View parent, int gravity, int x, int y){
		try{
			super.showAtLocation(parent, gravity, x, y);			
		}catch(Throwable e){
			e.printStackTrace();
		}
	}
	
	@Override
	public void showAsDropDown(View anchor){
		try{
			super.showAsDropDown(anchor);
		}catch(Throwable e){
			e.printStackTrace();
		}
	}
	
	@Override
	public void showAsDropDown(View anchor, int xoff, int yoff){
		try{
			super.showAsDropDown(anchor, xoff, yoff);
		}catch(Throwable e){
			e.printStackTrace();
		}
	}
	
	@Override
	public void showAsDropDown(View anchor, int xoff, int yoff, int gravity){
		try{
			super.showAsDropDown(anchor, xoff, yoff, gravity);
		}catch(Throwable e){
			e.printStackTrace();
		}
	}
}
