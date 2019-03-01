package com.moxi.bookstore.adapter;

import android.content.Context;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.moxi.bookstore.R;
import com.moxi.bookstore.modle.SearchBookModel;
import com.mx.mxbase.adapter.BAdapter;
import com.mx.mxbase.base.MyApplication;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/8/29.
 */
public class FileAdapter extends BAdapter<SearchBookModel> {
    private Map<String,SearchBookModel> addedFiles;
    private int width;
    private SettingSearchBookListener listener;

    public FileAdapter(Context context, List<SearchBookModel> list, Map<String,SearchBookModel>  addedFiles,SettingSearchBookListener listener) {
        super(context, list);
        this.width = (int) (((MyApplication.ScreenWidth - 144) / 6)*0.8);
        this.addedFiles=addedFiles;
        this.listener=listener;
    }

    public Map<String, SearchBookModel> getAddedFiles() {
        return addedFiles;
    }

    @Override
    public int getContentView() {
        return R.layout.adapter_file;
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
            holder.show_image.getLayoutParams().height = width;

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        SearchBookModel been = getItem(position);

        holder.show_select.setVisibility(View.VISIBLE);
        SearchBookModel model=addedFiles.get(been.filePath);
        if (model!=null) {
            if (position==0&&model.addType!=0){
                holder.show_select.setImageResource(R.mipmap.have_select);
            }else if(model.addType==0){
                holder.show_select.setImageResource(R.mipmap.have_select);
            }else {
                holder.show_select.setImageResource(R.mipmap.non_select);
            }
        } else {
            holder.show_select.setImageResource(R.mipmap.non_select);
        }

         elipseEnd(holder.file_name, been.getName());
        holder.all_layout.setTag(position);
        holder.show_select.setTag(position);

        holder.all_layout.setOnClickListener(this);
        holder.show_select.setOnClickListener(this);
    }

    private void elipseEnd(TextView textView, String value) {
        TextPaint paint = textView.getPaint();

        int bufferWidth = (int) paint.getTextSize() * 3;//缓冲区长度，空出两个字符的长度来给最后的省略号及图片
        // 计算出1行文字所能显示的长度
        int availableTextWidth = width - bufferWidth;
        // 根据长度截取出剪裁后的文字
        String ellipsizeStr = (String) TextUtils.ellipsize(value, paint, width, TextUtils.TruncateAt.END);
        textView.setText(ellipsizeStr);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (listener==null)return;
        int tag= (int) v.getTag();
        SearchBookModel path=getList().get(tag);
        switch (v.getId()){
            case R.id.all_layout:
                listener.onClickItem(path,tag);
                break;
            case R.id.show_select:
                listener.onClickSelect(path,tag);
                break;
            default:
                break;
        }
    }

    public class ViewHolder {
        RelativeLayout all_layout;
        ImageView show_select;
        ImageView show_image;
        TextView file_name;
    }

    public void updateSelect(int position, GridView listView) {
        int visibleFirstPosi = listView.getFirstVisiblePosition();
        int visibleLastPosi = listView.getLastVisiblePosition();
        if (position >= visibleFirstPosi && position <= visibleLastPosi) {

            View view = listView.getChildAt(position - visibleFirstPosi);
            ImageView show_select = (ImageView) view.findViewById(R.id.show_select);

            SearchBookModel model = addedFiles.get(getList().get(position).filePath);
            if (model!=null) {
                if (position==0&&model.addType!=0){
                    show_select.setImageResource(R.mipmap.have_select);
                }else if(model.addType==0){
                    show_select.setImageResource(R.mipmap.have_select);
                }else {
                    show_select.setImageResource(R.mipmap.non_select);
                }
            } else {
                show_select.setImageResource(R.mipmap.non_select);
            }
        }
    }

    public void updateSelect(GridView listView){
        int visibleFirstPosi = listView.getFirstVisiblePosition();
        int visibleLastPosi = listView.getLastVisiblePosition();
        for (int position = 0; position < getCount(); position++) {
            if (position >= visibleFirstPosi && position <= visibleLastPosi) {
                View view = listView.getChildAt(position - visibleFirstPosi);
                ImageView show_select = (ImageView) view.findViewById(R.id.show_select);

                SearchBookModel model = addedFiles.get(getList().get(position).filePath);
                if (model!=null) {
                    if (position==0&&model.addType!=0){
                        show_select.setImageResource(R.mipmap.have_select);
                    }else if(model.addType==0){
                        show_select.setImageResource(R.mipmap.have_select);
                    }else {
                        show_select.setImageResource(R.mipmap.non_select);
                    }
                } else {
                    show_select.setImageResource(R.mipmap.non_select);
                }
            }
        }

    }
    public interface SettingSearchBookListener{

        void onClickItem(SearchBookModel model,int postion);
        void onClickSelect(SearchBookModel model,int position);
    }
}
