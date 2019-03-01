package com.dangdang.reader.dread.view;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.ScrollView;

import com.alibaba.fastjson.JSON;
import com.dangdang.reader.MXModel.CiBa.hanyu.HanyuMode;
import com.dangdang.reader.R;
import com.dangdang.reader.dread.config.ReadConfig;
import com.dangdang.reader.dread.core.epub.GlobalWindow.IFloatingOperation;
import com.dangdang.reader.dread.data.BookNote;
import com.dangdang.reader.view.MyPopupWindow;
import com.dangdang.zframework.utils.UiUtil;
import com.dangdang.zframework.view.DDTextView;
import com.mx.mxbase.constant.APPLog;
import com.mx.mxbase.utils.DensityUtil;
import com.mx.mxbase.utils.DeviceUtil;
import com.mx.mxbase.utils.StringUtils;
import com.mx.mxbase.view.MxTextView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

public class FloatingWindow {

    private Context mContext;
    private PopupWindow mWindow;
    private View mParent;
    private View mContentView;
    private int mCurrentX;
    private int mCurrentY;
    private int mShowAtPosX;
    private int mShowAtPosY;
    private boolean mIsPdf = false;
    private View mSeperatorView;
    private View mColorsView;
    private boolean mIsDirectionUp = false;
    private int mDrawlineColorViewHeight = 0;

    private IFloatingOperation mOperCallback;

    private int mDrawLineColor = BookNote.NOTE_DRAWLINE_COLOR_RED;
    private int mCurSelectDrawLineColorID = R.id.read_fw_drawline_color_red;

    public FloatingWindow(Context context, View parent) {
        mContext = context;
        mParent = parent;

        mContentView = View.inflate(mContext, R.layout.read_floatingwindow,
                null);
        mWindow = new MyPopupWindow(mContentView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        mSeperatorView = mContentView.findViewById(R.id.read_fw_seperator);
        mColorsView = mContentView.findViewById(R.id.read_fw_drawline_colors);
        mContentView.findViewById(R.id.read_fw_copy).setOnClickListener(
                mClickListener);
        mContentView.findViewById(R.id.read_fw_drawline).setOnClickListener(
                mClickListener);
        mContentView.findViewById(R.id.read_check_dir).setOnClickListener(
                mClickListener);
        mContentView.findViewById(R.id.read_fw_delete).setOnClickListener(
                mClickListener);
        mContentView.findViewById(R.id.read_fw_share).setOnClickListener(
                mClickListener);
        mContentView.findViewById(R.id.read_fw_note).setOnClickListener(
                mClickListener);
        mContentView.findViewById(R.id.read_fw_drawline_color_yellow).setOnClickListener(
                mClickListener);
        mContentView.findViewById(R.id.read_fw_drawline_color_green).setOnClickListener(
                mClickListener);
        mContentView.findViewById(R.id.read_fw_drawline_color_blue).setOnClickListener(
                mClickListener);
        mContentView.findViewById(R.id.read_fw_drawline_color_pink).setOnClickListener(
                mClickListener);
        mContentView.findViewById(R.id.read_fw_drawline_color_red).setOnClickListener(
                mClickListener);

        setCurSelectDrawLineColorID(mCurSelectDrawLineColorID);

        int colorsHeight = UiUtil.dip2px(mContext, 28);
        int seperatorHeight = UiUtil.dip2px(mContext, (float) 16.5);
        mDrawlineColorViewHeight = colorsHeight + seperatorHeight;
    }

    public void show(int currentX, int currentY, int x, int y, boolean direction) {
        setBackground(direction);
        mShowAtPosX = x;
        mShowAtPosY = y;
        mCurrentX = currentX;
        mCurrentY = currentY;
        if (mColorsView.getVisibility() == View.VISIBLE && !mIsDirectionUp) {
            y -= mDrawlineColorViewHeight;
        }

        mWindow.showAtLocation(mParent, Gravity.NO_GRAVITY, x, y);
        getZHCN();
    }

    private void setBackground(boolean direction) {
        View view = mContentView.findViewById(R.id.read_fw_bglayout);
        APPLog.e("修改了标记界面");
//		if (direction) {
//			view.setBackgroundResource(R.drawable.reader_note_arrow_up_empty);
//		} else {
//			view.setBackgroundResource(R.drawable.reader_note_arrow_down_empty);
//		}
        mIsDirectionUp = direction;
    }

    /**
     * @param isShowDelete false 显示删除按钮
     */
    public void setDrawLineOrDelete(boolean isShowDelete) {
        if (mIsPdf) {
            return;
        }

        DDTextView drawLineView = (DDTextView) mContentView.findViewById(R.id.read_fw_drawline);
        View seperatorView = mContentView.findViewById(R.id.read_fw_seperator);
        View drawlinecolorsView = mContentView.findViewById(R.id.read_fw_drawline_colors);
        if (isShowDelete) {
//            drawLineView.setTextColor(Color.WHITE);
            seperatorView.setVisibility(View.GONE);
            drawlinecolorsView.setVisibility(View.GONE);
            drawLineView.setEnabled(true);
        } else {
//            drawLineView.setTextColor(Color.RED);
            seperatorView.setVisibility(View.GONE);
            drawlinecolorsView.setVisibility(View.GONE);
            drawLineView.setEnabled(true);
        }
    }

    public void initIsPdf(boolean isPdf) {
        mIsPdf = isPdf;
        if (isPdf) {
            mContentView.findViewById(R.id.read_fw_drawline).setVisibility(View.GONE);
            mContentView.findViewById(R.id.read_fw_delete).setVisibility(View.GONE);
            mContentView.findViewById(R.id.read_fw_note).setVisibility(View.GONE);

            mContentView.findViewById(R.id.read_fw_firstdivide).setVisibility(View.GONE);

            mColorsView.setVisibility(View.GONE);
            mSeperatorView.setVisibility(View.GONE);
        }
    }

    public void hide() {
        if (mWindow != null) {
            mWindow.dismiss();
        }
    }

    public boolean isShowing() {
        return mWindow != null && mWindow.isShowing();
    }

    public boolean isShowDelete() {
        View view = mContentView.findViewById(R.id.read_fw_drawline_colors);
        return view.getVisibility() == View.VISIBLE;
    }

    public void setFloatingOperation(IFloatingOperation l) {
        mOperCallback = l;
        mOperCallback.onSetCurDrawLineColor(mDrawLineColor);
    }

    final OnClickListener mClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mOperCallback == null) {
                return;
            }
            int i = v.getId();
            if (i == R.id.read_fw_copy) {
                mOperCallback.onCopy();

            } else if (i == R.id.read_fw_drawline) {
//				setCurSelectDrawLineColorIDByColor(mDrawLineColor);
                APPLog.e("布局隐藏改造");
//				mSeperatorView.setVisibility(View.VISIBLE);
//				mColorsView.setVisibility(View.VISIBLE);
//				((TextView) mContentView.findViewById(R.id.read_fw_drawline)).setTextColor(Color.RED);
                /**替换画线代码*/
//				if (!mIsDirectionUp) {
//					mWindow.update(0, mShowAtPosY - mDrawlineColorViewHeight, -1, -1);
//				}
//
//				mOperCallback.onMarkSelected(true, "", mDrawLineColor, false);
                /**替换画线代码结束*/
                /**添加删除*/
                mOperCallback.onDelete();
            } else if (i == R.id.read_fw_delete) {
                mOperCallback.onDelete();
            } else if (i == R.id.read_check_dir) {//字典查询
                mOperCallback.chackDir();
            } else if (i == R.id.read_fw_note) {
                mOperCallback.onNote(!isShowDelete());

            } else if (i == R.id.read_fw_drawline_color_yellow) {
                setDrawLineColor(BookNote.NOTE_DRAWLINE_COLOR_YELLOW);
                mOperCallback.onMarkSelected(false, "", mDrawLineColor, true);

            } else if (i == R.id.read_fw_drawline_color_green) {
                setDrawLineColor(BookNote.NOTE_DRAWLINE_COLOR_GREEN);
                mOperCallback.onMarkSelected(false, "", mDrawLineColor, true);

            } else if (i == R.id.read_fw_drawline_color_blue) {
                setDrawLineColor(BookNote.NOTE_DRAWLINE_COLOR_BLUE);
                mOperCallback.onMarkSelected(false, "", mDrawLineColor, true);

            } else if (i == R.id.read_fw_drawline_color_pink) {
                setDrawLineColor(BookNote.NOTE_DRAWLINE_COLOR_PINK);
                mOperCallback.onMarkSelected(false, "", mDrawLineColor, true);

            } else if (i == R.id.read_fw_drawline_color_red) {
                setDrawLineColor(BookNote.NOTE_DRAWLINE_COLOR_RED);
                mOperCallback.onMarkSelected(false, "", mDrawLineColor, true);

            } else if (i == R.id.read_fw_share) {//分享
                mOperCallback.onShare();
            } else {
            }
//			if (v.getId() != R.id.read_fw_drawline) {
            hide();
//			}
        }
    };

    private void setSelectedColorViewAndCancelOther(int id) {
        mContentView.findViewById(mCurSelectDrawLineColorID).setSelected(false);
        mContentView.findViewById(id).setSelected(true);
        mCurSelectDrawLineColorID = id;
    }

    public int getDrawLineColor() {
        return mDrawLineColor;
    }

    public void setDrawLineColor(int drawLineColor) {
        APPLog.e("setDrawLineColor", drawLineColor);
        this.mDrawLineColor = drawLineColor;
        if (mOperCallback != null)
            mOperCallback.onSetCurDrawLineColor(drawLineColor);
        setCurSelectDrawLineColorIDByColor(mDrawLineColor);
        ReadConfig config = ReadConfig.getConfig();
        config.setNoteDrawlineColor(drawLineColor);
    }

    public void setCurSelectDrawLineColorID(int id) {
        setSelectedColorViewAndCancelOther(id);
    }

    public void setCurSelectDrawLineColorIDByColor(int color) {
        if (color == BookNote.NOTE_DRAWLINE_COLOR_YELLOW)
            setCurSelectDrawLineColorID(R.id.read_fw_drawline_color_yellow);
        else if (color == BookNote.NOTE_DRAWLINE_COLOR_GREEN)
            setCurSelectDrawLineColorID(R.id.read_fw_drawline_color_green);
        else if (color == BookNote.NOTE_DRAWLINE_COLOR_BLUE)
            setCurSelectDrawLineColorID(R.id.read_fw_drawline_color_blue);
        else if (color == BookNote.NOTE_DRAWLINE_COLOR_PINK)
            setCurSelectDrawLineColorID(R.id.read_fw_drawline_color_pink);
        else if (color == BookNote.NOTE_DRAWLINE_COLOR_RED)
            setCurSelectDrawLineColorID(R.id.read_fw_drawline_color_red);
    }

    //中文翻译
    private void getZHCN() {
        if (mOperCallback == null) return;
        String word = mOperCallback.getWord();
        if (StringUtils.isNull(word)) return;

        String timestamp = String.valueOf(System.currentTimeMillis());
        String auth_user = "key_hair";
        String auth_key = "dPiQcddsVjnR9G";
        String client = "2";
        String uuid = DeviceUtil.getDeviceSerial();
        String sign = stringToMD5(client + uuid + timestamp + auth_user + auth_key + word).substring(0, 16);

        Map<String, String> maps = new HashMap<>();
        maps.put("client", client);
        maps.put("uuid", uuid);
        maps.put("timestamp", timestamp);
        maps.put("auth_user", auth_user);
        maps.put("sign", sign);
        maps.put("q", word);
        maps.put("c", "hanyu");
        String RUrl = getGetUrl(maps, "http://ifanyi.iciba.com");
        APPLog.e(RUrl);
        OkHttpUtils.post().url(RUrl).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
            }

            @Override
            public void onResponse(String response, int id) {
                APPLog.e("onResponse", response);
                if (mWindow == null || !mWindow.isShowing()) return;
                try {
                    HanyuMode hanyuMode = JSON.parseObject(response, HanyuMode.class);
                    String res=hanyuMode.getHanyuValue("");
                    if (!StringUtils.isNull(res)){
                        int width= ((ScrollView)mContentView.findViewById(R.id.pop_width)).getWidth();
                        if (width<=0){
                            width= DensityUtil.dip2px(mContext,(125)*5);
                        }
                        ((View)mContentView.findViewById(R.id.read_ciba)).setVisibility(View.VISIBLE);
                        MxTextView mxTextView= (MxTextView)mContentView.findViewById(R.id.ciba_hanzi);
                        mxTextView.setVisibility(View.VISIBLE);
                        mxTextView.getLayoutParams().width=width;
                        mxTextView.setSourceText(res);
                    }
                }catch (Exception e){

                }

            }
        });
    }

    /**
     * 获得get请求方法URL拼接
     *
     * @param pairs 数据集
     * @param url   请求路径
     * @return 返回数据拼接后的结果
     */
    private String getGetUrl(Map<String, String> pairs, String url) {
        for (Map.Entry<String,String> map:pairs.entrySet()){

        }
        StringBuffer buffer = new StringBuffer();
        buffer.append(url);
        if (pairs != null && pairs.size() != 0) {
            buffer.append("?");
            int i = 0;
            for (Map.Entry<String, String> entry : pairs.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (i != 0) {
                    buffer.append("&");
                }
                buffer.append(key);
                buffer.append("=");
                buffer.append(value);
                i++;
            }
        }
        return buffer.toString();
    }

    /**
     * 将字符串转成MD5值
     *
     * @param string 需要转换的字符串
     * @return 字符串的MD5值
     */
    private String stringToMD5(String string) {
        byte[] hash;

        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10)
                hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }

        return hex.toString();
    }
}
