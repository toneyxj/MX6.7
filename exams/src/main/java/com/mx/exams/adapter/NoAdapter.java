package com.mx.exams.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.mx.exams.R;
import com.mx.exams.cache.ACache;
import com.mx.exams.model.ExamsDetails;
import com.mx.exams.utils.MxgsaTagHandler;
import com.mx.mxbase.adapter.BAdapter;
import com.mx.mxbase.utils.Base64Utils;
import com.mx.mxbase.utils.Log;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Archer on 16/9/29.
 */
public class NoAdapter extends BAdapter<ExamsDetails> {


    public NoAdapter(Context context, List<ExamsDetails> list) {
        super(context, list);
    }

    class ViewHolder {

    }

    @Override
    public int getContentView() {
        return R.layout.mx_recycler_write_home_item;
    }

    @Override
    public void onInitView(View view, final int position, boolean firstAdd) {
        TextView tvTitle = (TextView) view.findViewById(R.id.tv_write_home_work_title);
        RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.radio_group_write_home);
        RadioButton rbtn1 = (RadioButton) view.findViewById(R.id.radio_answer_1);
        RadioButton rbtn2 = (RadioButton) view.findViewById(R.id.radio_answer_2);
        RadioButton rbtn3 = (RadioButton) view.findViewById(R.id.radio_answer_3);
        RadioButton rbtn4 = (RadioButton) view.findViewById(R.id.radio_answer_4);
        String getStr = "cchid" + getList().get(position).getCchId() + "position" + position + "type" + getList().get(position).getType() + "dif" + getList().get(position).getDifficulty();
        String result = ACache.get(context).getAsString(getStr);
        if (result != null) {
            switch (result.toUpperCase()) {
                case "A":
                    rbtn1.setChecked(true);
                    break;
                case "B":
                    rbtn2.setChecked(true);
                    break;
                case "C":
                    rbtn3.setChecked(true);
                    break;
                case "D":
                    rbtn4.setChecked(true);
                    break;
                default:
                    break;
            }
        }
        radioGroup.setTag(position);
        rbtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateAchace("A", position);
            }
        });
        rbtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateAchace("B", position);
            }
        });
        rbtn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateAchace("C", position);
            }
        });
        rbtn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateAchace("D", position);
            }
        });
        setTitle(position, tvTitle);
    }

    /**
     * 更新缓存答案
     *
     * @param a
     * @param position
     */
    private void updateAchace(String a, int position) {
        String cacheStr = "cchid" + getList().get(position).getCchId() + "position" + position + "type" + getList().get(position).getType() + "dif" + getList().get(position).getDifficulty();
        Log.e("缓存key And value", cacheStr + "---" + a);
        ACache.get(context).put(cacheStr, a);
    }

    /**
     * @param position
     * @param view
     */
    private void setTitle(int position, TextView view) {
        String title = getList().get(position).getTitle();
        Pattern p = Pattern.compile("<img[^>]+src\\s*=\\s*['\"](\\s*)(data:image/)\\S+(base64,)([^'\"]+)['\"][^>]*>");
        Matcher m = p.matcher(title);
        while (m.find()) {
            String str = m.group(4);
            title = title.replace(m.group(),"#@M#@X@" + str + "#@M#@X@");
        }
        String titleResult = title;
        if (titleResult.indexOf("#@M#@X@") > 0) {
            String[] s = titleResult.split("#@M#@X@");
            view.setText(position + 1 + "、");
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
            view.setText(Html.fromHtml((position + 1) + "、" + getList().get(position).getTitle(), null, new MxgsaTagHandler(context)));
        }
    }

//    /**
//     * 设置题目value
//     *
//     * @param position 当点页面序号
//     * @param view     显示题目目标view
//     */
//    private void setTitleValue(int position, TextView view) {
//        String head = "<sub><img align=\"middle\" src=\"data:image/png;base64,";
//        String behind = "\" /></sub>";
//        String title = getList().get(position).getTitle();
//        Log.e("zhiqian----", title + "");
//        String temp = title.replace("\\\"", "\"");
//        Log.e("tihuan----", temp + "");
//        if (title.indexOf(head) > 0 && title.indexOf(behind) > 0) {
//            String[] s = title.split(head + "|" + behind);
//            view.setText(position + 1 + "、");
//            for (int j = 0; j < s.length; j++) {
//                if (j % 2 == 0) {
//                    view.append(Html.fromHtml(s[j]));
//                } else {
//                    Bitmap bitmap = Base64Utils.base64ToBitmap(s[j]);
//                    ImageSpan imgSpan = new ImageSpan(context, bitmap);
//                    SpannableString spanString = new SpannableString("icon");
//                    spanString.setSpan(imgSpan, 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                    view.append(spanString);
//                }
//            }
//        } else {
//            view.setText(Html.fromHtml((position + 1) + "、" + getList().get(position).getTitle(), null, new MxgsaTagHandler(context)));
//        }
//    }
}
