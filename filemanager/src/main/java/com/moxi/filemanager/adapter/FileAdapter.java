package com.moxi.filemanager.adapter;

import android.content.Context;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.moxi.filemanager.R;
import com.moxi.filemanager.model.FileModel;
import com.mx.mxbase.adapter.BAdapter;
import com.mx.mxbase.utils.GlideUtils;

import java.util.List;

/**
 * Created by Administrator on 2016/8/29.
 */
public class FileAdapter extends BAdapter<FileModel> {
    private boolean showAelect=false;
    private int width;
    private int height;
    private int showStyle;

    public void setShowAelect(boolean showAelect) {
        this.showAelect = showAelect;
        notifyDataSetChanged();
    }

    public FileAdapter(Context context, List<FileModel> list,int width,int height,boolean showAelect,int showStyle) {
        super(context, list);
        this.width=width;
        this.height=height;
        this.showAelect=showAelect;
        this.showStyle=showStyle;
    }

    @Override
    public int getContentView() {
        if (showStyle==0) {
            return R.layout.adapter_file;
        }else {
            return R.layout.adapter_one_file;
        }
    }

    @Override
    public void onInitView(View view, int position, boolean firstAdd) {
        ViewHolder holder;
        if (firstAdd) {
            holder = new ViewHolder();
            holder.all_layout = (RelativeLayout) view.findViewById(R.id.all_layout);
            holder.show_select = (ImageView) view.findViewById(R.id.show_select);
            holder.show_image = (ImageView) view.findViewById(R.id.show_image);
            holder.file_name = (TextView) view.findViewById(R.id.file_name);

            if (showStyle==0) {
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.all_layout.getLayoutParams();
                params.width = width;
                params.height = height;
                holder.all_layout.setLayoutParams(params);

                holder.show_image.getLayoutParams().width = width - 10;
                holder.show_image.getLayoutParams().height = height - 10;
            }else {
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.all_layout.getLayoutParams();
                params.height = height/2;
                holder.all_layout.setLayoutParams(params);

                int imageheight= height/2-10;
                holder.show_image.getLayoutParams().width = (int) (imageheight*0.8);
                holder.show_image.getLayoutParams().height = imageheight;
            }

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        FileModel been = getItem(position);
        if (been.getFileType()==1){//图片
            GlideUtils.getInstance().locatonPic(context,holder.show_image,been.file.getAbsolutePath());
        }else {
            holder.show_image.setImageResource(been.ShowDrawableId());
        }

        if (showAelect){
            holder.show_select.setVisibility(View.VISIBLE);
            if (been.isSelect){
                holder.show_select.setImageResource(R.mipmap.have_select);
            }else{
                holder.show_select.setImageResource(R.mipmap.non_select);
            }
        }else{
            holder.show_select.setVisibility(View.INVISIBLE);
        }
//        holder.file_name.setText(been.getFileName());
        elipseEnd( holder.file_name,been.getFileName());
    }
    private void elipseEnd(TextView textView, String value) {
        TextPaint paint = textView.getPaint();

        int bufferWidth = (int) paint.getTextSize() * 3;//缓冲区长度，空出两个字符的长度来给最后的省略号及图片
        // 计算出1行文字所能显示的长度
        int availableTextWidth;
        if (showStyle==0) {
             availableTextWidth = width*2 - bufferWidth;
        }else {
            availableTextWidth = (width*4-height/2-8)*2 - bufferWidth;
        }
        // 根据长度截取出剪裁后的文字
        String ellipsizeStr = (String) TextUtils.ellipsize(value, paint, availableTextWidth, TextUtils.TruncateAt.END);
        textView.setText(ellipsizeStr);
    }
    public class ViewHolder {
        RelativeLayout all_layout;
        ImageView show_select;
        ImageView show_image;
        TextView file_name;
    }
    public void updateFileName(int position, GridView listView) {
        int visibleFirstPosi = listView.getFirstVisiblePosition();
        int visibleLastPosi = listView.getLastVisiblePosition();
        if (position >= visibleFirstPosi && position <= visibleLastPosi) {
            View view = listView.getChildAt(position - visibleFirstPosi);
            TextView file_name = (TextView) view.findViewById(R.id.file_name);
            file_name.setText(getList().get(position).getFileName());
        }
    }

    public void updateSelect(int position, GridView listView) {
        int visibleFirstPosi = listView.getFirstVisiblePosition();
        int visibleLastPosi = listView.getLastVisiblePosition();
        if (position >= visibleFirstPosi && position <= visibleLastPosi) {

            View view = listView.getChildAt(position - visibleFirstPosi);
            ImageView select_image = (ImageView) view.findViewById(R.id.show_select);

            boolean isTrue=getList().get(position).isSelect;
            if (isTrue) {
                select_image.setImageResource(R.mipmap.have_select);
            } else {
                select_image.setImageResource(R.mipmap.non_select);
            }
        }
    }
}
