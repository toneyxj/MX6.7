package com.dangdang.reader.moxiUtils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.mx.mxbase.utils.StringUtils;
import com.mx.mxbase.utils.ToastUtils;

/**
 * 跳转界面查询字典
 * Created by xj on 2017/9/11.
 */

public class ToCheckDirctoryUtils {

    /**
     * 字典查询
     * @param context 当前上下文
     * @param checkStr 查询文字
     */
    public static void startDict( Context context,String checkStr) {
        checkStr=checkStr.trim();
        if (StringUtils.isNull(checkStr)) {
            ToastUtils.getInstance().showToastShort("输入不能为空");
            return;
        }
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setComponent(new ComponentName("com.onyx.dict", "com.onyx.dict.activity.DictMainActivity"));

        intent.putExtra(Intent.ACTION_SEARCH, checkStr);
        context.startActivity(intent);

    }

}
