package com.moxi.nexams.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.moxi.nexams.R;
import com.moxi.nexams.cache.ACache;
import com.moxi.nexams.model.papermodel.DetailsTestModel;
import com.moxi.nexams.utils.MxgsaTagHandler;
import com.mx.mxbase.interfaces.OnItemClickListener;
import com.mx.mxbase.utils.Base64Utils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Archer on 16/8/10.
 */
public class ChoiceOptionAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<DetailsTestModel> listOption;
    private int checked = -1;
    private OnItemClickListener onItemClickListener;
    private int ppsId, psjId;
    private ACache aCache;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public ChoiceOptionAdapter(Context context, int psjId, List<DetailsTestModel> listOption, int ppsId) {
        this.context = context;
        this.listOption = listOption;
        this.ppsId = ppsId;
        this.psjId = psjId;
        aCache = ACache.get(context);
        if (aCache.getAsString(ppsId + "psj_id_" + psjId) != null) {
            checked = Integer.parseInt(aCache.getAsString(ppsId + "psj_id_" + psjId));
        }
    }

    public void setData(List<DetailsTestModel> listOption) {
        this.listOption = listOption;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.mx_recycler_choice_option, parent, false);
        return new ChoiceNewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        switch (position) {
            case 0:
                ((ChoiceNewViewHolder) holder).tvOptionValue.setText("A");
                break;
            case 1:
                ((ChoiceNewViewHolder) holder).tvOptionValue.setText("B");
                break;
            case 2:
                ((ChoiceNewViewHolder) holder).tvOptionValue.setText("C");
                break;
            case 3:
                ((ChoiceNewViewHolder) holder).tvOptionValue.setText("D");
                break;
            default:
                ((ChoiceNewViewHolder) holder).tvOptionValue.setText("E");
                break;
        }
        if (position == checked) {
            ((ChoiceNewViewHolder) holder).imgChecked.setImageResource(R.mipmap.mx_img_check_box_chosed);
        } else {
            ((ChoiceNewViewHolder) holder).imgChecked.setImageResource(R.mipmap.mx_img_check_box_normal);
        }
        setTitle(listOption.get(position).getDesc(), ((ChoiceNewViewHolder) holder).tvOptionDesc);
        if (onItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClickListener.onItemClick(holder.itemView, position);
                    checked = position;
                    ChoiceOptionAdapter.this.notifyDataSetChanged();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return listOption == null ? 0 : listOption.size();
    }

    class ChoiceNewViewHolder extends RecyclerView.ViewHolder {
        TextView tvOptionValue;
        TextView tvOptionDesc;
        ImageView imgChecked;

        public ChoiceNewViewHolder(View itemView) {
            super(itemView);
            tvOptionValue = (TextView) itemView.findViewById(R.id.tv_choice_value);
            tvOptionDesc = (TextView) itemView.findViewById(R.id.tv_choice_desc);
            imgChecked = (ImageView) itemView.findViewById(R.id.img_choice_value);
        }
    }


    /**
     * 设置题目信息
     *
     * @param title 题目信息
     * @param view  题目显示对象
     */
    private void setTitle(String title, TextView view) {
        Pattern p = Pattern.compile("<img[^>]+src\\s*=\\s*['\"](\\s*)(data:image/)\\S+(base64,)([^'\"]+)['\"][^>]*>");
        Matcher m = p.matcher(title);
        while (m.find()) {
            String str = m.group(4);
            title = title.replace(m.group(), "#@M#@X@" + str + "#@M#@X@");
        }
        String titleResult = title;
        if (titleResult.indexOf("#@M#@X@") > 0) {
            String[] s = titleResult.split("#@M#@X@");
            view.setText("");
            for (int j = 0; j < s.length; j++) {
                if (j % 2 == 0) {
                    view.append(Html.fromHtml(s[j]));
                } else {
                    Bitmap bitmap = Base64Utils.base64ToBitmap(s[j]);
                    ImageSpan imgSpan = new ImageSpan(context, bitmap);
                    SpannableString spanString = new SpannableString("icon");
                    spanString.setSpan(imgSpan, 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    view.append(spanString);
                }
            }
        } else {
            view.setText(Html.fromHtml(title, null, new MxgsaTagHandler(context)));
        }
    }
}
