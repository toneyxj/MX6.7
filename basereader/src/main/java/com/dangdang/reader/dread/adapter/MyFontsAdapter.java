package com.dangdang.reader.dread.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.RadioButton;

import com.dangdang.reader.R;
import com.dangdang.reader.dread.data.FontDomain;
import com.dangdang.reader.dread.font.FontListHandle;
import com.dangdang.zframework.network.download.DownloadConstant;
import com.dangdang.zframework.view.DDImageView;
import com.dangdang.zframework.view.DDTextView;

import java.util.List;

public class MyFontsAdapter extends BaseFontAdapter {
    private String tryAgain;
    private OnCheckedChangeListener checkedListener;
    private View.OnClickListener mClickListener;

    public MyFontsAdapter(Context context, List<FontDomain> fonts, ListView listView) {
        super(context, fonts, listView);
        tryAgain = context.getString(R.string.try_again);
    }

    public void setListener(OnCheckedChangeListener cl, View.OnClickListener l) {
        this.checkedListener = cl;
        this.mClickListener = l;
    }

    public final static String TAG_RIGHT_CONTAINER = "tag_position=";

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View mCacheView = null;
        ViewHolder mHolder = null;
        if (convertView == null) {
            mCacheView = View.inflate(mContext, R.layout.fragment_font_item, null);
            mHolder = new ViewHolder();
            mHolder.fontNameParent = mCacheView.findViewById(R.id.fragment_font_item_name_parent);
            mHolder.fontNameView = (DDTextView) mCacheView.findViewById(R.id.fragment_font_item_name);
            mHolder.fontNameImg = (DDImageView) mCacheView.findViewById(R.id.fragment_font_item_name_img);
            mHolder.fileSizeView = (DDTextView) mCacheView.findViewById(R.id.fragment_font_item_filesize);
            mHolder.priceView = (DDTextView) mCacheView.findViewById(R.id.fragment_font_item_price);
            mHolder.radioView = (RadioButton) mCacheView.findViewById(R.id.fragment_font_item_radiobtn);
            mHolder.rightContainer = mCacheView.findViewById(R.id.fragment_font_item_right_container);
            mHolder.downloadContainer = mCacheView.findViewById(R.id.fragment_font_item_download_container);
            mHolder.buyView = mCacheView.findViewById(R.id.fragment_font_item_buy_btn);
            mHolder.downloadView = (DDImageView) mCacheView.findViewById(R.id.fragment_font_item_download_view);
            mHolder.progressView = (DDTextView) mCacheView.findViewById(R.id.fragment_font_item_download_progress);

            mCacheView.setTag(mHolder);
        } else {
            mCacheView = convertView;
            mHolder = (ViewHolder) mCacheView.getTag();
        }
        FontDomain font = mFonts.get(position);
        mHolder.fontNameView.setText(font.getProductname());

        mHolder.fileSizeView.setText(font.getFontSize());

        mHolder.downloadContainer.setTag(font.getProductId());
        mHolder.rightContainer.setTag(TAG_RIGHT_CONTAINER + position);
        mHolder.buyView.setVisibility(View.GONE);
        mHolder.downloadView.setOnClickListener(mClickListener);//下载点击
        mHolder.downloadView.setTag(position);
        mHolder.radioView.setOnCheckedChangeListener(checkedListener);//字体选择
        mHolder.radioView.setTag(position);
        mHolder.radioView.setTag(R.id.fragment_font_item_radiobtn, font.getProductId());
        //已购
        mHolder.fileSizeView.setVisibility(View.VISIBLE);
        mHolder.priceView.setVisibility(View.GONE);
        if (font.getProductId().equals(FontListHandle.DEFAULT_PRODUCTID)) {
            mHolder.downloadContainer.setVisibility(View.GONE);

            mHolder.fontNameImg.setVisibility(View.GONE);
            mHolder.radioView.setVisibility(View.VISIBLE);
            mHolder.fileSizeView.setVisibility(View.GONE);
        } else {
            processDownload(mCacheView, mHolder.radioView, mHolder.downloadContainer, mHolder.downloadView, mHolder.progressView, font);
        }
        isRadioChecked(mHolder.radioView, font);

        String url = font.getImageURL();
        mHolder.fontNameParent.setTag(url);
        setImage(mHolder, url);
        return mCacheView;
    }

    public void setDatas(List<FontDomain> datas) {
        if (mFonts != null) {
            mFonts.clear();
            FontDomain defaultFt = new FontDomain();
            defaultFt.setProductname(FontListHandle.getHandle(mContext).getPresetDefaultFontName());
            defaultFt.productId = FontListHandle.DEFAULT_PRODUCTID;
            mFonts.add(defaultFt);
            mFonts.addAll(datas);
        }        
    }

    private void isRadioChecked(RadioButton radioView, FontDomain font) {
        if (mDefaultFontIdentity.equals(font.getProductId())) {
            radioView.setChecked(true);
        } else {
            radioView.setChecked(false);
        }
    }

    private String mDefaultFontIdentity = "";

    public void setDefaultFontIdentity(String defaultFontIdentity) {
        this.mDefaultFontIdentity = defaultFontIdentity;
    }

    private void processDownload(View cacheView, RadioButton radioView, View downloadContainer, DDImageView downloadView, DDTextView progressView,
                                 FontDomain font) {
        DownloadConstant.Status status = font.status;
        if (status == DownloadConstant.Status.FINISH) {
            cacheView.setOnClickListener(mCheckListener);
            radioView.setVisibility(View.VISIBLE);
            downloadContainer.setVisibility(View.GONE);
        } else {
            radioView.setVisibility(View.GONE);
            downloadContainer.setVisibility(View.VISIBLE);
            setDownloadStatus(downloadView, progressView, font);
        }
    }

    private void setDownloadStatus(DDImageView downloadView, DDTextView progressView, FontDomain font) {
        int percent = font.percent();
        DownloadConstant.Status status = font.status;
        String text = "";
        if (status == DownloadConstant.Status.FAILED) {
            text = tryAgain;
        } else if (status == DownloadConstant.Status.PENDING) {
            text = mContext.getString(R.string.downloadstatus_waito);
        } else if (font.progress > 0) {
            text = percent + "%";
        } else {
            text = "";
        }
        progressView.setText(text);
        setDownloadStatus(downloadView, status);
    }

    private View.OnClickListener mCheckListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            RadioButton radioView = (RadioButton) v.findViewById(R.id.fragment_font_item_radiobtn);
            radioView.setChecked(!radioView.isChecked());
        }
    };

    public void setDownloadStatus(DDImageView downloadView, DownloadConstant.Status status) {
        switch (status) {
            case DOWNLOADING:
            case RESUME:
                downloadView.setImageResource(R.drawable.font_pause);
                break;
            case UNSTART:
            case FAILED:
                downloadView.setImageResource(R.drawable.font_download);
                break;
            case PAUSE:
            case PENDING:
            case FINISH:
                downloadView.setImageResource(R.drawable.font_download);
                break;
        }
    }
}
