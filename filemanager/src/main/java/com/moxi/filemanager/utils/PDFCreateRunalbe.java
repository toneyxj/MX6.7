package com.moxi.filemanager.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.moxi.filemanager.adapter.PdfBackground;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.util.List;

import static com.moxi.filemanager.adapter.PdfBackground.BORDER_WIDTH;

/**
 * Created by xj on 2017/9/6.
 */

public class PDFCreateRunalbe implements Runnable {
    private List<String> dirs;
    private PDFCreateListener listener;
    private String pdfName;
    private static final String hitnTxt = "pdf文件生成进度：";
    private boolean isFinish = false;
    private boolean isExport = false;//导出中
    private int width = 880;
    private int height = 1240;

    public boolean isExport() {
        return isExport;
    }

    public void setFinish(boolean finish) {
        isFinish = finish;
        if (isFinish) {
            handler.removeCallbacksAndMessages(null);
            listener = null;
        }
    }

    /**
     * pdf文件创建过程
     *
     * @param dirs     导出文件集合
     * @param pdfName  pdf文件名
     * @param listener pdf文件生成监听
     */
    public PDFCreateRunalbe(List<String> dirs, String pdfName, PDFCreateListener listener) {
        this.dirs = dirs;
        this.pdfName = pdfName;
        this.listener = listener;
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (null == listener) return;
            switch (msg.what) {
                case 0:
                    listener.onFail(msg.obj.toString());
                    break;
                case 1:
                    listener.onProgressHitn(msg.obj.toString());
                    break;
                case 2:
                    break;
                case 3:
                    listener.onFinish();
                    break;
                case 4:
                    break;
                default:
                    break;

            }
        }
    };

    @Override
    public void run() {
        //开始转换
        isExport = true;
        Message msg = new Message();
        msg.what = 1;
        msg.obj = hitnTxt + "1/" + String.valueOf(dirs.size());
        handler.sendMessage(msg);

        Document document = new Document();
//设置pdf背景
        PdfWriter writer = null;
        try {
            writer = PdfWriter.getInstance(document, new FileOutputStream(pdfName));
            //设置背景
            PdfBackground event = new PdfBackground();
            writer.setPageEvent(event);

            document.open();
            for (int i = 0; i < dirs.size(); i++) {
                if (isFinish) break;

                document.newPage();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                Bitmap bitmap = decodeSampledBitmapFromResource(dirs.get(i), width, height, false);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                Image img = Image.getInstance(stream.toByteArray());
                //设置图片缩放到A4纸的大小
                img.scaleToFit(PageSize.A4.getWidth() - BORDER_WIDTH * 2, PageSize.A4.getHeight() - BORDER_WIDTH * 2);
//                 设置图片的显示位置（居中）
                img.setAbsolutePosition((PageSize.A4.getWidth() - img.getScaledWidth()) / 2, (PageSize.A4.getHeight() - img.getScaledHeight()) / 2);
                document.add(img);

                bitmap.recycle();
                stream.close();

                //处理后通知更新
                Message msg1 = new Message();
                msg1.what = 1;
                msg1.obj = hitnTxt + String.valueOf(i + 1) + "/" + String.valueOf(dirs.size());
                handler.sendMessage(msg1);
            }
            document.close();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
            document.close();
            isExport = false;
            if (writer != null)
                writer.close();
            Message msg2 = new Message();
            msg2.what = 0;
            msg2.obj = "pdf文件生成出错";
            handler.sendMessage(msg2);
            return;
        }
        //结束转换
        isExport = false;
        handler.sendEmptyMessage(3);
    }

    /**
     * 根据计算的inSampleSize，得到压缩后图片
     *
     * @param pathName
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    private Bitmap decodeSampledBitmapFromResource(String pathName,
                                                   int reqWidth, int reqHeight, boolean isMax) {
        Bitmap bitmap = null;
        try {
            // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(pathName, options);
            // 调用上面定义的方法计算inSampleSize�?
            options.inSampleSize = calculateInSampleSize(options, reqWidth,
                    reqHeight, isMax);
            // 使用获取到的inSampleSize值再次解析图�?
            options.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeFile(pathName, options);
        } catch (Exception e) {
        }
        return bitmap;
    }

    /**
     * 计算inSampleSize，用于压缩图�?
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    private int calculateInSampleSize(BitmapFactory.Options options,
                                      int reqWidth, int reqHeight, boolean isMax) {
        // 源图片的宽度
        int width = options.outWidth;
        int height = options.outHeight;
        int inSampleSize = 1;
        if (isMax) {
            if (width > reqWidth && height > reqHeight) {
                // 计算出实际宽度和目标宽度的比
                int widthRatio = Math.round((float) width / (float) reqWidth);
                int heightRatio = Math.round((float) height / (float) reqHeight);
                inSampleSize = Math.max(widthRatio, heightRatio);
            }
        } else {
            if (width > reqWidth || height > reqHeight) {
                // 计算出实际宽度和目标宽度的比
                int widthRatio = Math.round((float) width / (float) reqWidth);
                int heightRatio = Math.round((float) height / (float) reqHeight);
                inSampleSize = Math.max(widthRatio, heightRatio);
            }
        }
        return inSampleSize;
    }

    /**
     * pdf文件装换
     */
    public interface PDFCreateListener {
        void onFinish();

        void onFail(String msg);

        void onProgressHitn(String hitn);

    }
}
