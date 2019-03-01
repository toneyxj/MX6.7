package com.dangdang.reader.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.net.Uri;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.dangdang.zframework.log.LogM;
import com.dangdang.zframework.utils.UiUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;


public class SystemLib {
    // screen locked tag
    public static final String LOCK_TAG = "LOCK_FULL";

    /**
     * @param context
     * @return WakeLock
     */
    public static final WakeLock getFullLock(Context context) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        WakeLock fullLock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.FULL_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, LOCK_TAG);
        return fullLock;
    }

	/*public static final Paint getDefaltPaint() {
        Paint paint = new Paint();
		paint.setTypeface(Typeface.SANS_SERIF);
		paint.setFlags(Paint.ANTI_ALIAS_FLAG);
		return paint;
	}*/

    public static final int getFontOffset(Paint paint, int textSize) {
        paint.setTextSize(textSize);
        return getFontOffset(paint);
    }

    public static final int getFontOffset(Paint paint) {
        FontMetrics fontMetrics = paint.getFontMetrics();
        int fontHeight = (int) Math.ceil(fontMetrics.bottom - fontMetrics.top);
        int fontOffetY = (int) (fontHeight - paint.getTextSize()) >> 1;
        return fontOffetY;
    }

    public static final void showTip(Context context, int resID) {
        showTip(context, context.getResources().getString(resID));
    }

    public static long sLastTime;

    public static final void showTip(Context context, String text) {
        if (TextUtils.isEmpty(text)) {
            return;
        }
        if (System.currentTimeMillis() - sLastTime < 3000) {
            return;
        }
        sLastTime = System.currentTimeMillis();
        UiUtil.showToast(context, text);
//		Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
//		toast.setGravity(Gravity.CENTER, 0, 0);

//		toast.show();
    }

    public static int stringToInt(String text, int dfault) {
        if (text == null) {
            return dfault;
        }
        text = text.trim();
        if (text.length() == 0) {
            return dfault;
        }
        int value = dfault;
        try {
            value = Integer.parseInt(text);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return dfault;
        }
        return value;
    }

    public static int stringToInt(String text) {
        return stringToInt(text, 0);
    }

//	public static final

    public static final long[] convertStringArrayToLongArray(String[] array) {
        if (array == null) {
            return null;
        }
        long[] lArray = new long[array.length];
        try {
            for (int i = 0; i < lArray.length; ++i) {
                LogM.d("convertStringArrayToLongArray:", i + ":" + array[i]);
                lArray[i] = Long.parseLong(array[i]);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return null;
        }
        return lArray;
    }

    public static final String getNChar(String text, int n) {
        if (text == null || text.length() < n) {
            return text;
        }
        StringBuffer sb = new StringBuffer();
        int half = n / 2;
        sb.append(text.substring(0, half));
        sb.append("...");
        int size = text.length();
        sb.append(text.substring(size - half, size));
        return sb.toString();
    }

    public static final String filter(String text, char filter, char replace) {
        if (text == null) {
            return null;
        }
        char[] chs = text.toCharArray();
        for (int i = 0; i < chs.length; ++i) {
            if (chs[i] == filter) {
                chs[i] = replace;
            }
        }
        return new String(chs);
    }

    public static String getTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String time = sdf.format(new Date());
        return time;
    }

    /**
     * 保存图片成功后，更新系统图库
     *
     * @param context
     * @param file
     */
    public static void updateSystemGallery(Context context, File file) {
        if (context == null || file == null)
            return;
        try {// 其次把文件插入到系统图库
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    file.getAbsolutePath(), file.getName(), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            // 最后通知图库更新
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file.getAbsolutePath()));
            intent.putExtra("is_system_broadcast",true);
            context.sendBroadcast(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
