package com.moxi.bookstore.request;

import android.os.AsyncTask;

import com.mx.mxbase.constant.APPLog;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 获取微信网页支付图片使用方式
  new GetHtmlCode(url, new GetHtmlCode.HtmlCodeBack() {
@Override
public void onBack(boolean Sucess, String result) {
hideDialog();
APPLog.e("返回结果="+result);
}
}).execute();
 * Created by Administrator on 2016/10/13.
 */
public class GetHtmlCode extends AsyncTask<String, Void, String> {
    private String url;
    private HtmlCodeBack back;

    /**
     * 获取微信网页支付图片
     * @param url html网页路径
     * @param back 返回接口
     */
    public GetHtmlCode(String url, HtmlCodeBack back) {
        this.url = url;
        this.back = back;
        APPLog.e("start task url="+url);
    }
    /** */
    /**
     * 通过网站域名URL获取该网站的源码
     *
     * @param url
     * @return String
     * @throws Exception
     */
    private String getURLSource(URL url) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5 * 1000);
        InputStream inStream = conn.getInputStream();  //通过输入流获取html二进制数据
        byte[] data = readInputStream(inStream);        //把二进制数据转化为byte字节数据
        String htmlSource = new String(data);
        APPLog.e("html:"+htmlSource);
        return htmlSource;
    }
    /** */
    /**
     * 把二进制流转化为byte字节数组
     *
     * @param instream
     * @return byte[]
     * @throws Exception
     */
    private byte[] readInputStream(InputStream instream) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1204];
        int len = 0;
        while ((len = instream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        instream.close();
        return outStream.toByteArray();
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            URL qequestUrl=new URL(url);
            try {
//                String code="<html/><img class=\"qrcode\" alt=\"二维码\" id=\"QRcode\" src=\"https://wx.tenpay.com/cgi-bin/mmpayweb-bin/getpayqrcode?uuid=43684ce764374c\"> 就是这么忽视我uvsdvs <img class=\"qrcode\" alt=\"二维码22\" id=\"QRcode\" src=\"https://wx.tenpay.com/cgi-bin/mmpayweb-bin/getpayqrcode?uuid=43684ce764374c\"> ";
                String sqlit="<(img|IMG)(.*?)(/>|></img>|>)";
                String code= getURLSource(qequestUrl);
                APPLog.e("打印数据"+code);
                Pattern p = Pattern.compile(sqlit);
                Matcher m = p.matcher(code);
                int index=0;
                while (m.find()){
                    String reslut=m.group();
                    APPLog.e("reslut:"+reslut);
                    if (reslut.contains("alt=\"二维码\"")||reslut.contains("id=\"QRcode\"")){
                        //开始匹配<img />标签中的src
                        Pattern p_src = Pattern.compile("(src|SRC)=(\"|\')(.*?)(\"|\')");
                        Matcher m_src = p_src.matcher(reslut);
                        if (m_src.find()) {
                            String str_src = m_src.group(3);

                            APPLog.e("background:"+str_src);
                            return str_src;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (back==null)return;
        if (s==null){
            back.onBack(false,s);
        }else {
            back.onBack(true,s);
            APPLog.e("call back");
        }
    }

    public interface HtmlCodeBack {
        public void onBack(boolean Sucess, String result);
    }
}
