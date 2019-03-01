package com.dangdang.reader.store;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by xiaruri on 2015/8/17.
 */
public class StoreUtil {

	private static StoreUtil mStoreUtil;
	private SharedPreferences spf;
	private static final String STORE_PREFRENCE = "store_prefrence";
	private static final String STORE_TITLE = "store_title";
	private static final String STORE_PREADDRESS = "store_peraddress";
	private static final String STORE_ADDRESS = "store_address2";//5.4新修改为2
	private Drawable mStoreDetailBgDrawable;
	private AtomicInteger num = new AtomicInteger(0);
	private Object lock = new Object();

	private StoreUtil() {
	}

	public static StoreUtil getInstance() {
		if (mStoreUtil == null) {
			synchronized (StoreUtil.class) {
				mStoreUtil = new StoreUtil();
			}

		}
		return mStoreUtil;
	}

	public void release(boolean exit) {
		if (mStoreDetailBgDrawable instanceof BitmapDrawable) {
			Bitmap bitmap = ((BitmapDrawable) mStoreDetailBgDrawable)
					.getBitmap();
			if (bitmap != null && !bitmap.isRecycled())
				bitmap.recycle();
		}
		mStoreDetailBgDrawable = null;
		num.set(0);
		if (exit)
			mStoreUtil = null;
	}

//	public void setStoreDetailBgDrawable(View view, Context context) {
//		synchronized (lock) {
//			num.addAndGet(1);
//			try {
//				view.setBackgroundDrawable(getStoreDetailBgDrawable(context));
//				return;
//			} catch (Exception e) {
//				e.printStackTrace();
//			} catch (OutOfMemoryError e) {
//				e.printStackTrace();
//			}
//			view.setBackgroundColor(0XFF282C33);
//		}
//	}


//	private Drawable getStoreDetailBgDrawable(Context context) {
//		try {
//			if (mStoreDetailBgDrawable == null) {
//				final BitmapFactory.Options options = new BitmapFactory.Options();
//				options.inPreferredConfig = Bitmap.Config.RGB_565;
//				Bitmap bitmap = BitmapFactory.decodeResource(
//						context.getResources(),
//						R.drawable.bg_store_book_detail, options);
//				mStoreDetailBgDrawable = new BitmapDrawable(
//						context.getResources(), bitmap);
//			}
//			return mStoreDetailBgDrawable;
//		} catch (Exception e) {
//			e.printStackTrace();
//		} catch (OutOfMemoryError e) {
//			e.printStackTrace();
//		}
//		return new ColorDrawable(0XFF282C33);
//	}

	public String getTitle(Context mContext) {
		spf = mContext.getSharedPreferences(STORE_PREFRENCE,
				Context.MODE_PRIVATE);
		String title = "";
		title = spf.getString(STORE_TITLE, "推荐, 出版, 原创, 特价, 分类, 榜单");
		return title;
	}

	public void setTitle(Context mContext, String title) {
		spf = mContext.getSharedPreferences(STORE_PREFRENCE,
				Context.MODE_PRIVATE);
		Editor et = spf.edit();
		et.putString(STORE_TITLE, title);
		et.commit();
	}




}
