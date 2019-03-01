package com.dangdang.reader.dread.adapter;

import android.content.Context;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RadioButton;

import com.dangdang.reader.dread.data.FontDomain;
import com.dangdang.zframework.log.LogM;
import com.dangdang.zframework.view.DDImageView;
import com.dangdang.zframework.view.DDTextView;
import com.mx.mxbase.utils.GlideUtils;

import java.util.List;


/**
 * Created by liuboyu on 2015/5/22.
 */
public abstract class BaseFontAdapter extends BaseAdapter {
    protected Context mContext;
    protected List<FontDomain> mFonts;
    protected ListView mListView;

    public BaseFontAdapter(Context context, List<FontDomain> fonts, ListView listView) {
        this.mContext = context;
        this.mFonts = fonts;
        this.mListView = listView;

    }

    @Override
    public int getCount() {
        return mFonts.size();
    }

    @Override
    public Object getItem(int position) {
        return mFonts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder {
        View fontNameParent;
        DDTextView fontNameView;
        DDImageView fontNameImg;
        DDTextView fileSizeView;
        DDTextView priceView;
        RadioButton radioView;
        View rightContainer;
        View downloadContainer;
        View buyView;
        DDImageView downloadView;
        DDTextView progressView;
    }

    protected void printLog(String msg) {
        LogM.d(getClass().getSimpleName(), msg);
    }

    protected void setImage(ViewHolder mHolder, String url) {
        if (url != null) {
            mHolder.fontNameView.setVisibility(View.GONE);
            mHolder.fontNameImg.setVisibility(View.VISIBLE);
//            ImageManager.getInstance().dislayImage(url, mHolder.fontNameImg, mDrawableListener);
            GlideUtils.getInstance().loadImage(mContext,mHolder.fontNameImg,url);
        } else {
            mHolder.fontNameView.setVisibility(View.VISIBLE);
            mHolder.fontNameImg.setVisibility(View.GONE);
        }
    }
//
//    protected SimpleImageLoadingListener mDrawableListener = new SimpleImageLoadingListener() {
//        @Override
//        public void onLoadingStarted(String imageUri, View view) {
//        }
//
//        @Override
//        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
//        }
//
//        @Override
//        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//            /*printLog("callback url:" + imageUri);
//            if (imageUri == null || mListView == null)
//                return;
//            LinearLayout parent = (LinearLayout) mListView.findViewWithTag(imageUri);
//            if (parent == null)
//                return;
//            ImageView imageView = (ImageView) parent.findViewById(R.id.fragment_font_item_name_img);
//            DDTextView textView = (DDTextView) parent.findViewById(R.id.fragment_font_item_name);
//            textView.setVisibility(View.GONE);
//            imageView.setVisibility(View.VISIBLE);
//            imageView.setImageBitmap(loadedImage);*/
//        }
//    };
}
