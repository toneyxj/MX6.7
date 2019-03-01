package com.moxi.haierc.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.mx.mxbase.constant.APPLog;
import com.mx.mxbase.constant.PhotoConfig;

//import com.onyx.android.sdk.data.cms.OnyxCmsCenter;
//import com.onyx.android.sdk.data.cms.OnyxMetadata;
//import com.onyx.android.sdk.data.cms.OnyxThumbnail;
//import com.onyx.android.sdk.data.util.RefValue;

/**
 * Created by xj on 2017/12/25.
 */

public class ReadManagerPicUtils {
    private static ReadManagerPicUtils instatnce = null;
//    private Map<String, OnyxMetadata> mapMetdatas = null;

    public static ReadManagerPicUtils getInstance() {
        if (instatnce == null) {
            synchronized (ReadManagerPicUtils.class) {
                if (instatnce == null) {
                    instatnce = new ReadManagerPicUtils();
                }
            }
        }
        return instatnce;
    }
//
//    private synchronized Map<String, OnyxMetadata> getMapMetdatas() {
//        if (mapMetdatas == null) {
//            mapMetdatas = new HashMap<>();
//        }
//        return mapMetdatas;
//    }
//
//    public void clearMetdata() {
//        if (mapMetdatas != null) {
//            mapMetdatas.clear();
//            mapMetdatas = null;
//        }
//    }

    private void addpic(final Context context, final String path) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                APPLog.e("addpic-path", path);
                OnlyXMetadataManager onlyXMetadataManager = new OnlyXMetadataManager();
                onlyXMetadataManager.initOnlyXMetadataManager(context, path);
            }
        }).start();
    }

    private void initReadManagerPicUtils(Context context) {
//        final Cursor cursor = context.getContentResolver().query(OnyxMetadata.CONTENT_URI, null, null, null, null);
//        if (cursor == null) {
//            return;
//        }
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while (cursor.moveToNext()) {
//                    OnyxMetadata item = OnyxMetadata.Columns.readColumnData(cursor);
//                    if (item == null) {
//                        break;
//                    }
//                    APPLog.e("item.getName", item.getName());
//                    getMapMetdatas().put(item.getName(), item);
//                }
//                if (cursor != null) {
//                    cursor.close();
//                }
//            }
//        }).start();

    }

    private String getFileMD5(Context context, String filePath) {
//        if (null == mapMetdatas && getMapMetdatas().size() == 0) {
//            initReadManagerPicUtils(context);
//        }
//        File file = new File(filePath);
//        OnyxMetadata data = mapMetdatas.get(file.getName());
//        if (data != null) {
//            return data.getMD5();
//        }
        return null;
    }

    public void setLocationBookPic(Context context, ImageView view, String filePath) {
        try {
            String md5 = getFileMD5(context, filePath);
//            OnyxMetadata onyxMetadata = OnyxCmsCenter.getMetadata(context, filePath);
//            RefValue<Bitmap> refValue = new RefValue<Bitmap>();
//            if (md5 == null && OnyxCmsCenter.getThumbnail(context,
//                    onyxMetadata, OnyxThumbnail.ThumbnailKind.Middle, refValue)) {
//                setImage(refValue.getValue(), view);
//            } else if (md5 != null && OnyxCmsCenter.getThumbnailByMD5(context, md5, OnyxThumbnail.ThumbnailKind.Middle, refValue)) {
//                setImage(refValue.getValue(), view);
//            } else {
                view.setImageResource(PhotoConfig.getSources(filePath));
//            }
//            if (md5 == null) {
//                ReadManagerPicUtils.getInstance().addpic(context, filePath);
//            }
        } catch (Exception e) {
            e.printStackTrace();
            view.setImageResource(PhotoConfig.getSources(filePath));
        }
    }

    private void setImage(Bitmap bitmap, ImageView view) {
        if (bitmap != null && bitmap.getWidth() > 0 && bitmap.getHeight() > 0) {
            view.setImageBitmap(bitmap);
        }
    }
}
