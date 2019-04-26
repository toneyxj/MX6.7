package com.dangdang.reader.moxiUtils.share;

import android.text.TextUtils;

import com.dangdang.reader.dread.config.ReadConfig;
import com.dangdang.reader.dread.core.epub.ReaderAppImpl;
import com.dangdang.reader.dread.data.BookNote;
import com.dangdang.reader.dread.data.BookNoteDataWrapper;
import com.dangdang.reader.dread.format.BaseReadInfo;
import com.dangdang.reader.dread.format.Book;
import com.dangdang.reader.dread.format.Chapter;
import com.dangdang.reader.dread.jni.BaseJniWarp;
import com.mx.mxbase.utils.FileUtils;
import com.mx.mxbase.utils.StringUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by xiajun on 2019/4/25.
 */

public class YouDaoContent implements ContentBuilderInterface<BookNoteDataWrapper> {
    /**
     * 保存笔记记录到文件-防止bundle过大而出现报错问题
     * @param list 数据列表
     * @param bj
     * @return
     */
    @Override
    public void getContent(final List<BookNoteDataWrapper> list, final Object bj, final ShareCallBack onShare) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean is=false;
                String wj = null;
                Book mBook = (Book) bj;
                StringBuilder builder=new StringBuilder();
                for (BookNoteDataWrapper wrapper : list) {
                    //章节名
                    String chapterName = wrapper.chapterName;
                    //笔记具体数据
                    BookNote bookNote = wrapper.data;
                    if (TextUtils.isEmpty(chapterName)) {
                        Chapter chapter = mBook.getChapter(wrapper.chapterIndex);
                        Book.BaseNavPoint nPoint = mBook.getNavPoint(chapter);
                        if (nPoint != null) {
                            chapterName = nPoint.lableText;
                        }
                    }
                    if (wrapper.data != null) {
                        //时间
                        String time=convertText(transferLongToDate(bookNote.noteTime));
                        builder.append(ContentUtils.getSingtime(time));
                        //标记内容
                        String content=convertText(bookNote.sourceText);
                        builder.append(ContentUtils.getSingContent(content));
                        if (bookNote.noteText != null && !"".equals(bookNote.noteText)) {
                            //笔记标注内容
                            String comment = bookNote.noteText;
                            builder.append(ContentUtils.getWriteNote(comment));
                        }
                        builder.append(ContentUtils.getMinLine());
                    } else {
                        //章节标题
                        chapterName = convertText(chapterName);
                        builder.append(ContentUtils.getChapter(chapterName));
                    }
                }
                builder.append(ContentUtils.getMaxLine());
                builder.append(ContentUtils.getBottomSource("网上书城"));

                //保存临时文件文件到本地sd卡
                try {
                    wj= StringUtils.getSDPath()+"wssclswj";
                    is= FileUtils.getInstance().writeFile(wj,builder.toString());
                } catch (IOException e) {
                    is=false;
                    e.printStackTrace();
                }
                if(onShare!=null)onShare.shareSavePath(is,wj);

            }
        }).start();

    }
    private String convertText(String text) {
        boolean chineseConvert = ReadConfig.getConfig().getChineseConvert();
        BaseReadInfo readInfo = ReaderAppImpl.getApp().getReadInfo();
        if (readInfo != null) {
            chineseConvert = chineseConvert && readInfo.isSupportConvert();
        }
        return chineseConvert ? BaseJniWarp.ConvertToGBorBig5(text, 0) : text;
    }

    /**
     * 把long 转换成 日期 再转换成String类型
     */
    public String transferLongToDate( Long millSec) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(millSec);
        return sdf.format(date);
    }

}

