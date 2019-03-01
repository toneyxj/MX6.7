package com.moxi.wechatshare;

import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.moxi.wechatshare.callback.QrcodeCallBack;
import com.moxi.wechatshare.http.DDHttp;
import com.moxi.wechatshare.http.HttpCallBack;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by along on 2017/12/15.
 */

public class WechatShareTool {

    private int qrcodeWith = 400;
    private int qrcodeheight = 400;

    private String baseUrl = "http://topsirbook.haiyunclass.com";
    private String url = baseUrl + "/app/fileHtml/upLoadImageText";

    QrcodeCallBack qrcodeCallBack;
//    Activity activity;
    private Handler handler=new Handler();

    public WechatShareTool(QrcodeCallBack qrcodeCallBack){
        this.qrcodeCallBack = qrcodeCallBack;
//        this.activity = activity;
    }

    public void setQrcodeWith(int qrcodeWith){
        this.qrcodeWith = qrcodeWith;
    }

    public void setQrcodeHeight(int qrcodeheight){
        this.qrcodeheight = qrcodeheight;
    }

    /**
     *
     * @param appSession 用户标识
     * @param textContent 发布的内容
     * @param files 发布的图片，可多张图片
     */
    public void start(String appSession, String textContent, File[] files){

        // TODO: 2017/12/15 验证数据
        HashMap<String, Object> param = new HashMap<>();
        if (appSession != null){
            if (!"".equals(appSession)){
                param.put("appSession",appSession);
            }
        }
        if (textContent != null){
            if (!"".equals(textContent)){
                param.put("textContent",textContent);
            }
        }
        if (files != null){
            for (int i = 0;i<files.length;i++){
                param.put("files",files[i]);
            }
        }

        Log.d("mx","mx http start....");
        DDHttp.postLoadFile(url, param, new HttpCallBack() {
            @Override
            public void onSuccess(String result) {
                // TODO: 2017/12/15 数据解析 {"code":0,"msg":"success","result":"/app/fileHtml/detail/23"}
                Log.d("mx","onSuccess result===>" + result);
                final String urlAddress = baseUrl + parseData(result);
                Log.d("mx","url===>" + urlAddress);
                if (!urlAddress.equals("")){
//                    activity.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Bitmap bitmap = generateBitmap(urlAddress,qrcodeWith,qrcodeheight);
//                            qrcodeCallBack.callBack(bitmap);
//                        }
//                    });
                   final Bitmap bitmap = generateBitmap(urlAddress,qrcodeWith,qrcodeheight);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            qrcodeCallBack.callBack(bitmap);
                        }
                    });
                }

            }

            @Override
            public void onFaild(int code, final String message) {
                Log.d("mx","onFaild result===>" + message);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        qrcodeCallBack.backFail(message);
                    }
                });
            }
        });
    }

    private String parseData(String jsonData) {
        String resultStr = "";
        try{
            JSONObject jsonObject = new JSONObject(jsonData);
            int code = jsonObject.optInt("code",-1);
            if (code == 0){
                resultStr = jsonObject.optString("result","");
            }else{
                return resultStr;
            }
            return resultStr;
        }catch (Exception e){
            resultStr = "";
            return resultStr;
        }
    }

    private Bitmap generateBitmap(String content, int width, int height) {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Map<EncodeHintType, String> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        try {
            BitMatrix encode = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height, hints);
            int[] pixels = new int[width * height];
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    if (encode.get(j, i)) {
                        pixels[i * width + j] = 0x00000000;
                    } else {
                        pixels[i * width + j] = 0xffffffff;
                    }
                }
            }
            return Bitmap.createBitmap(pixels, 0, width, width, height, Bitmap.Config.RGB_565);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }

}
