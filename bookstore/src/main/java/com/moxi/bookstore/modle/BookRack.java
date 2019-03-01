package com.moxi.bookstore.modle;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * 书架书籍
 * Created by Administrator on 2016/9/12.
 */
public class BookRack extends DataSupport implements Serializable {
    @Column(unique = true)
    public long id;
    /**
     * 书的id
     */
    public String bookId;
    /**
     * 保存路径
     */
    public String savePath;
    /**
     * 保存图片路径
     */
    public String imagePath;
    /**
     * 书名
     */
    public String bookName;
    /**
     * 阅读进度
     */
    public String progress;
    /**
     *最近阅读时间
     */
    public String recentlyReaderTime;
    /**
     * 书标签
     */
    public int lable;

    public BookRack() {
    }

    public BookRack(String bookId, String savePath, String imagePath, String bookName, String progress, int lable) {

        this.bookId = bookId;
        this.savePath = savePath;
        this.imagePath = imagePath;
        this.bookName = bookName;
        this.progress = progress;
        this.lable = lable;
    }
}
