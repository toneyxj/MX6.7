package com.dangdang.reader.dread.format;

import android.graphics.Point;
import android.graphics.Rect;

import com.dangdang.reader.dread.jni.BaseJniWarp.EPoint;
import com.dangdang.reader.dread.jni.BaseJniWarp.ERect;

public class NativeStructConvert {

	
	public static EPoint convertPoint(Point p){
		
		EPoint ep = new EPoint();
		ep.x = p.x;
		ep.y = p.y;
		
		return ep;
	}
	
	public static Rect convertRect(ERect r){
		
		Rect er = new Rect();
		er.left = (int) r.left;
		er.top = (int) r.top;
		er.right = (int) r.right;
		er.bottom = (int) r.bottom;
		
		return er;
	}
	
	public static Rect[] convertRects(ERect... rs){
		if(rs == null) return null;
		
		Rect[] rects = new Rect[rs.length];
		for(int i = 0, len = rs.length; i < len; i++){
			rects[i] = convertRect(rs[i]);
		}
		return rects;
	}
	
	
}
