package com.dangdang.reader.dread.holder;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.dangdang.reader.R;
import com.dangdang.reader.dread.config.ReadConfig;
import com.dangdang.zframework.log.LogM;
import com.dangdang.zframework.utils.BitmapUtil;
import com.dangdang.zframework.utils.DRUiUtility;

public class GlobalResource {

	private static Bitmap magnifGlassBmp;
	private static Bitmap mBatteryBmp;
	private static Bitmap mBrownTextureBitmap;
	private static Bitmap mGrayTextureBitmap;
	private static Bitmap mParchmentBitmap;

	public static Bitmap getMagnifClassBmp(Context context) {
		if (!isAvailable(magnifGlassBmp)) {
			try {
				magnifGlassBmp = BitmapFactory.decodeResource(
						context.getResources(), R.drawable.magnifying_glass);
			} catch (Throwable e) {
				pringLogE(" create magnifGlassBmp error: " + e);
			}
		}
		return magnifGlassBmp;
	}

	public static Bitmap getBatteryBmp(Context context) {
		if (!isAvailable(mBatteryBmp)) {
			try {
				mBatteryBmp = BitmapFactory.decodeResource(context.getResources(),
						R.drawable.battery);
			} catch (Throwable e) {
				pringLogE(" create mBatteryBmp error: " + e);
			}
		}
		return mBatteryBmp;
	}

	public static Bitmap getBrownTextureBitmap() {
		if (!isAvailable(mBrownTextureBitmap)) {
			Bitmap temp = DRUiUtility.getUiUtilityInstance()
					.getBitmapByRsource(R.drawable.brown_texture_bg);
			mBrownTextureBitmap = Bitmap.createScaledBitmap(temp, DRUiUtility.getScreenWith(), DRUiUtility.getScreenHeight(), true);
//			recycle(temp);
		}
		return mBrownTextureBitmap;
	}

	public static Bitmap getGrayTextureBitmap() {
		if (!isAvailable(mGrayTextureBitmap)) {
			mGrayTextureBitmap = DRUiUtility.getUiUtilityInstance()
					.getBgBitmap(ReadConfig.GRAY_TEXTURE_FILE_NAME);
		}
		return mGrayTextureBitmap;
	}
	public static Bitmap getmParchmentBitmap() {
		if (!isAvailable(mParchmentBitmap)) {
			Bitmap temp = DRUiUtility.getUiUtilityInstance()
					.getBitmapByRsource(R.drawable.bg_parchment);
			mParchmentBitmap = Bitmap.createScaledBitmap(temp, DRUiUtility.getScreenWith(), DRUiUtility.getScreenHeight(), true);
//			recycle(temp);
		}
		return mParchmentBitmap;
	}

	public static void clear() {
		recycle(magnifGlassBmp);
		magnifGlassBmp = null;

		recycle(mBatteryBmp);
		mBatteryBmp = null;

		recycle(mBrownTextureBitmap);
		mBrownTextureBitmap = null;

		recycle(mGrayTextureBitmap);
		mGrayTextureBitmap = null;
	}

	public static boolean isAvailable(Bitmap bmp) {
		return BitmapUtil.isAvailable(bmp);
	}

	public static void recycle(Bitmap bmp) {
		BitmapUtil.recycle(bmp);
	}
	
	public static void pringLogE(String log){
		LogM.e(GlobalResource.class.getSimpleName(), log);
	}

}
