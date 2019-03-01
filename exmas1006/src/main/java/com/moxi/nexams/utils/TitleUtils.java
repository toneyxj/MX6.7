package com.moxi.nexams.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.TextView;

import com.moxi.nexams.activity.ChoiceTestActivity;
import com.moxi.nexams.activity.ClassicalChineseReadActivity;
import com.moxi.nexams.activity.MXErrorExamsActivity;
import com.moxi.nexams.activity.MultipleChoiceActivity;
import com.moxi.nexams.activity.OtherTestActivity;
import com.moxi.nexams.activity.SevenChoiceFiveActivity;
import com.moxi.nexams.model.papermodel.PaperModelDesc;
import com.mx.mxbase.constant.Constant;
import com.mx.mxbase.utils.Base64Utils;
import com.mx.mxbase.utils.MXUamManager;
import com.mx.mxbase.utils.Toastor;
import com.mx.mxbase.view.AlertDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;

/**
 * 试卷统一跳转类
 * Created by Archer on 2017/1/13.
 */
public class TitleUtils {

    /**
     * 设置题目信息
     *
     * @param title 题目信息
     * @param view  题目显示对象
     */
    public static void setTestTitle(String title, TextView view, Context context) {
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

    /**
     * 跳转到目标界面
     *
     * @param mainTitle  题目类型标题
     * @param paperDb    试卷db文件名称
     * @param ppsId      ppsid
     * @param paperTitle 试卷名称
     * @param position   当前试卷第几类型题
     * @param listPaper  所有类型题
     */
    public static void moveToActivity(Context context, boolean isHistory, String mainTitle, int paperDb,
                                      int ppsId, String paperTitle, int position, List<PaperModelDesc> listPaper) {
        Intent details = new Intent();
        if (mainTitle.indexOf("七选五") >= 0) {
            details.setClass(context, SevenChoiceFiveActivity.class);
        } else if (mainTitle.indexOf("多项选择题") >= 0 || mainTitle.indexOf("双项选择题") >= 0) {
            details.setClass(context, MultipleChoiceActivity.class);
        } else if (mainTitle.indexOf("古代诗歌阅读") >= 0) {
            details.setClass(context, OtherTestActivity.class);
        } else if (mainTitle.indexOf("现代文阅读") >= 0 || mainTitle.indexOf("完形填空") >= 0
                || mainTitle.indexOf("阅读理解") >= 0 || mainTitle.indexOf("对话或独白") >= 0
                || mainTitle.indexOf("阅读") >= 0) {
            details.setClass(context, ClassicalChineseReadActivity.class);
        } else if (mainTitle.indexOf("非选择题") >= 0) {
            details.setClass(context, OtherTestActivity.class);
        } else if (mainTitle.indexOf("单项选择") >= 0 || mainTitle.indexOf("选择") >= 0
                || mainTitle.indexOf("短对话") >= 0 || mainTitle.indexOf("语言文字运用") >= 0) {
            details.setClass(context, ChoiceTestActivity.class);
        } else {
            details.setClass(context, OtherTestActivity.class);
        }
        details.putExtra("paper_index", paperDb);
        details.putExtra("pps_id", ppsId);
        details.putExtra("test_type", mainTitle);
        details.putExtra("paper_title", paperTitle);
        details.putExtra("test_type_index", position);
        details.putExtra("test_list_types", (Serializable) listPaper);
        details.putExtra("test_is_history", isHistory);
        context.startActivity(details);
    }

    public static void submitPaper(final Context context, final String papId, final SubmitCallBack submitCallBack) {
        new AlertDialog(context).builder().setTitle("提示").setMsg("确认提交?").setCancelable(false).setNegativeButton("确定", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OkHttpUtils.post().url(Constant.SUBMIT_EXAMS_RESULT).addParams("appSession", MXUamManager.queryUser(context)).
                        addParams("copId", papId).build().connTimeOut(10000).execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        submitCallBack.onFail();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            int code = jsonObject.optInt("code", -1);
                            if (code == 0) {
                                Toastor.showToast(context, "提交成功");
                            } else {
                                submitCallBack.onFail();
                                Toastor.showToast(context, "提交失败");
                            }
                            submitCallBack.onSuccess();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            submitCallBack.onFail();
                        }
                    }
                });
            }
        }).setPositiveButton("取消", new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        }).show();
    }

    public interface SubmitCallBack {
        void onSuccess();

        void onFail();
    }
}
