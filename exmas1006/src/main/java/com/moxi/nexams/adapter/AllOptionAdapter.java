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
import com.moxi.nexams.model.papermodel.LitterSelectModel;
import com.moxi.nexams.utils.MxgsaTagHandler;
import com.moxi.nexams.view.WarpLayoutOnClickListener;
import com.moxi.nexams.view.WarpLinearLayout;
import com.mx.mxbase.utils.Base64Utils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Archer on 16/8/10.
 */
public class AllOptionAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<LitterSelectModel> listOptions;
    private WarpLayoutOnClickListener onClickListener;
    private ACache aCache;
    private String[] results;

    public void setOnClickListener(WarpLayoutOnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public AllOptionAdapter(Context context, List<LitterSelectModel> listOptions) {
        this.context = context;
        this.listOptions = listOptions;
        results = new String[listOptions.size()];
        aCache = ACache.get(context);
    }

    public void setData(List<LitterSelectModel> listOptions) {
        this.listOptions = listOptions;
        this.notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.mx_recycler_view_item_all_option, null);
        return new AllOptionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        setTitle((position + 1) + "、" + listOptions.get(position).getPdm().getPsjTitle() + "\n", ((AllOptionViewHolder) holder).tvIndex);
        ((AllOptionViewHolder) holder).warpLinearLayout.removeAllViews();
        for (int i = 0; i < listOptions.get(position).getListOption().size(); i++) {
            final View add = LayoutInflater.from(context).inflate(R.layout.mx_recycler_choice_option, null);
            TextView tvChoiceValue = (TextView) add.findViewById(R.id.tv_choice_value);
            TextView tvChoiceDesc = (TextView) add.findViewById(R.id.tv_choice_desc);
            final ImageView imgChoice = (ImageView) add.findViewById(R.id.img_choice_value);
            String temp = listOptions.get(position).getPdm().getPsParentId();
            String answer = aCache.getAsString(temp + "psj_id_" + position);
            if (answer != null) {
                results[position] = answer;
                if (answer.equals(i + "")) {
                    imgChoice.setImageResource(R.mipmap.mx_img_check_box_chosed);
                } else {
                    imgChoice.setImageResource(R.mipmap.mx_img_check_box_normal);
                }
            } else {
                results[position] = "";
                imgChoice.setImageResource(R.mipmap.mx_img_check_box_normal);
            }
            switch (i) {
                case 0:
                    tvChoiceValue.setText("A");
                    break;
                case 1:
                    tvChoiceValue.setText("B");
                    break;
                case 2:
                    tvChoiceValue.setText("C");
                    break;
                case 3:
                    tvChoiceValue.setText("D");
                    break;
                case 4:
                    tvChoiceValue.setText("E");
                    break;
                case 5:
                    tvChoiceValue.setText("F");
                    break;
                case 6:
                    tvChoiceValue.setText("G");
                    break;
                default:
                    tvChoiceValue.setText("H");
                    break;
            }
            add.setTag(i);
            if (onClickListener != null) {
                add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int temp = (int) add.getTag();
                        results[position] = temp + "";
                        imgChoice.setImageResource(R.mipmap.mx_img_check_box_chosed);
                        onClickListener.onClickListener(add, position, temp, results);
                    }
                });
            }
            setTitle(listOptions.get(position).getListOption().get(i).getDesc(), tvChoiceDesc);
            ((AllOptionViewHolder) holder).warpLinearLayout.addView(add);
        }
    }

    @Override
    public int getItemCount() {
        return listOptions == null ? 0 : listOptions.size();
    }

    class AllOptionViewHolder extends RecyclerView.ViewHolder {
        TextView tvIndex;
        WarpLinearLayout warpLinearLayout;

        public AllOptionViewHolder(View itemView) {
            super(itemView);
            tvIndex = (TextView) itemView.findViewById(R.id.tv_index);
            warpLinearLayout = (WarpLinearLayout) itemView.findViewById(R.id.wrap_linear_layout);
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
