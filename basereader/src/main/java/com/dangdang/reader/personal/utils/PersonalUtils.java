package com.dangdang.reader.personal.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

import com.dangdang.reader.Constants;
import com.dangdang.zframework.utils.MemoryStatus;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PersonalUtils {
	
	/**
	 * 将对应格式的图片保存成文件
	 * 
	 * @param bm
	 *            图片bitmap
	 * @param file
	 *            保存图片文件
	 * @param format
	 *            保存格式
	 * @param quality
	 *            保存质量
	 */
	public static void saveFile(final Bitmap bm, File file, CompressFormat format,
			int quality) {
		OutputStream outStream = null;
		ByteArrayOutputStream baos = null;
		try {
			if (bm != null && bm.isRecycled()) {
				return;
			}
			baos = new ByteArrayOutputStream();
			if(bm != null){
				bm.compress(format, quality, baos);
				byte[] data = baos.toByteArray();
				
				if (MemoryStatus.hasAvailable(data.length, 500000)
						|| MemoryStatus.hasMemAvailable()) {
					outStream = new FileOutputStream(file);
					outStream.write(data, 0, data.length);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeStream(outStream);
			closeStream(baos);
		}
	}
	
	private static void closeStream(Closeable stream) {
		if (stream != null) {
			try {
				stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	

	public static boolean checkTime() {

		boolean result = false;
		String date = "8:00-22:00";
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		String currentDate = sdf.format(new Date());

		String[] dateArr = date.split("-");
		try {
			Date currDate = sdf.parse(currentDate);// 当前时间
			Date startDate = sdf.parse(dateArr[0]);//开始时间
			Date endDate = sdf.parse(dateArr[1]);//结束时间
			if (currDate.after(startDate) && currDate.before(endDate)) {
				result = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;

	}
	
	public static void refreshUserInfo(Context context){
		if (context == null) {
			return;
		}
		Intent intent = new Intent();
		intent.setAction(Constants.ACTION_REFRESH_USER_INFO);
		context.sendBroadcast(intent);
	}

//	public static void refreshShelfBuyList(Context context, ArrayList<StoreEBook> dataList){
//		if (context == null || dataList == null || dataList.size() == 0) {
//			return;
//		}
//		Intent intent = new Intent();
//		intent.setAction(Constants.ACTION_REFRESH_SHELF_BUY_LIST);
//		intent.putExtra("buy_book_list", dataList);
//		context.sendBroadcast(intent);
//	}

	/**
	 * 设置用户头像的bitmap
	 */
	public static Bitmap getUserHeadPortraitsBitmap(String path) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.RGB_565;
		options.inPurgeable = true;
		options.inInputShareable = true;
		return BitmapFactory.decodeFile(path, options);
	}
	
	
	public static  Bitmap createRoundConerImage(Bitmap source)  
	    {  
		 int w = source.getWidth(), h = source.getHeight();

		 Bitmap rounder = Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888);
		 Canvas canvas = new Canvas(rounder);   

		 Paint xferPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//		 xferPaint.setColor(Color.RED);
		 canvas.drawCircle(w / 2, w / 2, w / 2, xferPaint);  
		 xferPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));  
	     canvas.drawBitmap(source, 0, 0, xferPaint);  
		 return rounder;
	    }  

}
