package com.moxi.bookstore.request;

import java.util.List;

/**
 * Created by Administrator on 2016/2/17.
 */
public class RequestUtils {
    /**
     * 获得get请求方法URL拼接
     * @param pairs 数据集
     * @param url 请求路径
     * @return 返回数据拼接后的结果
     */
    public static String getGetUrl(List<ReuestKeyValues> pairs,String url){
        StringBuffer buffer=new StringBuffer();
        buffer.append(url);
        if (pairs!=null&&pairs.size()!=0){
            buffer.append("?");
            for (int i = 0; i < pairs.size(); i++) {
                ReuestKeyValues pair=pairs.get(i);
                if (i!=0){
                    buffer.append("&");
                }
                buffer.append(pair.key);
                buffer.append("=");
                buffer.append(pair.value);
            }
        }
        return buffer.toString();
    }
}
