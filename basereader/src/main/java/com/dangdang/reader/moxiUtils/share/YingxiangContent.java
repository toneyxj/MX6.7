package com.dangdang.reader.moxiUtils.share;

import android.text.TextUtils;

import com.dangdang.reader.R;
import com.dangdang.reader.dread.config.ReadConfig;
import com.dangdang.reader.dread.core.epub.ReaderAppImpl;
import com.dangdang.reader.dread.data.BookNote;
import com.dangdang.reader.dread.data.BookNoteDataWrapper;
import com.dangdang.reader.dread.format.BaseReadInfo;
import com.dangdang.reader.dread.format.Book;
import com.dangdang.reader.dread.format.Chapter;
import com.dangdang.reader.dread.jni.BaseJniWarp;
import com.moxi.biji.DDBook.ContentUtils;
import com.moxi.biji.intf.ContentBuilderInterface;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.dangdang.zframework.network.NetCheck.mContext;

/**
 * 印象笔记装换
 * Created by Administrator on 2019/3/1.
 */
public class YingxiangContent implements ContentBuilderInterface<BookNoteDataWrapper> {
    @Override
    public String getContent(List<BookNoteDataWrapper> list, Object bj) {
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
                    String comment = mContext.getString(R.string.booknote)
                            + "：" + bookNote.noteText;
                    builder.append(ContentUtils.getWriteNote(comment));
                }
                builder.append(ContentUtils.getMinLine());
            } else {
                //章节标题
                chapterName = convertText(chapterName);
                builder.append(ContentUtils.getChapter(chapterName));
            }
        }

        return builder.toString();
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
