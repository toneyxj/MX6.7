package com.mx.mxbase.interfaces;

import com.mx.mxbase.model.LocationBookInfo;

/**
 * Created by xj on 2018/8/8.
 */

public interface LocationInfoListener  {
    /**
     * 读取到的数据信息
     * @param info
     */
    void onBackInfo(LocationBookInfo info);
}
