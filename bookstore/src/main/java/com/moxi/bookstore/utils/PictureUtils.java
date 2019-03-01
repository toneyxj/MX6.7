package com.moxi.bookstore.utils;

import android.graphics.Bitmap;

import com.moxi.bookstore.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

/**
 * 图片工具类
 * Created by Administrator on 2016/9/12.
 */
public class PictureUtils {
    public static final String imagePath="https://gss0.bdstatic.com/5eR1dDebRNRTm2_p8IuM_a/res/r/image/2016-09-11/23f0ce3fbffe49bbf568d7dbf3adabf4.jpg";
    /**
     * 获得 DisplayImageOptions;
     *
     * @return DisplayImageOptions
     */
    public static DisplayImageOptions getoptions() {
        @SuppressWarnings("deprecation")
        DisplayImageOptions options = new DisplayImageOptions.Builder()
//				.showImageOnLoading(R.drawable.ic_launcher)
                .showImageForEmptyUri(R.mipmap.ic_launcher)
                .showImageOnFail(R.mipmap.ic_launcher)
                .cacheInMemory(true)//设置下载的图片是否缓存在内存中
                .cacheOnDisc(true)//设置下载的图片是否缓存在SD卡中
                .resetViewBeforeLoading(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new FadeInBitmapDisplayer(300)).build();
        return options;
    }
    /**
     * 获得 DisplayImageOptions;
     *
     * @param round
     *            设置圆角度 360为圆型图片
     * @return DisplayImageOptions
     */
    public static DisplayImageOptions getoptions(int round) {

        DisplayImageOptions options = new DisplayImageOptions.Builder()
//				.showImageOnLoading(R.drawable.default_poto)
                .showImageForEmptyUri(R.mipmap.ic_launcher)
                .showImageOnFail(R.mipmap.ic_launcher).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true)
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                .displayer(new RoundedBitmapDisplayer(round)).build();
        return options;
    }
}
