package com.moxi.haierc.model;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.Serializable;

/**
 * Created by xj on 2017/6/15.
 */

public class BookStoreFile extends DataSupport implements Serializable {
    @Column(unique = true)
    public long id;
    /**
     * 阅读文件路径
     */
    public String filePath;
    /**
     * 阅读文件图片路径
     */
    public String photoPath;
    /**
     * 阅读文件索引值
     */
    public long _index;

    /**
     * 文件全拼
     */
    public String fullPinyin;

    /**
     * 是否是当当图书
     * @param isDdBook
     */
    public int isDdBook;

    public String bookImageUrl;
    public String progress;

    public int getIsDdBook() {
        return isDdBook;
    }

    public void setIsDdBook(int isDdBook) {
        this.isDdBook = isDdBook;
    }

    public String getBookImageUrl() {
        return bookImageUrl;
    }

    public void setBookImageUrl(String bookImageUrl) {
        this.bookImageUrl = bookImageUrl;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public long get_index() {
        return _index;
    }

    public void set_index(long _index) {
        this._index = _index;
    }

    public int isDdBook() {
        return isDdBook;
    }

    public void setDdBook(int ddBook) {
        isDdBook = ddBook;
    }

    public void setFullPinyin(String fullPinyin) {
        this.fullPinyin = fullPinyin;
    }

    public String getFullPinyin() {
        return fullPinyin;
    }

    public File getFile(){
        File file=new File(filePath);
        return file;
    }
    public String getName(){
        File file=getFile();
        return file.getName();
    }

    @Override
    public String toString() {
        return "BookStoreFile{" +
                "id=" + id +
                ", filePath='" + filePath + '\'' +
                ", photoPath='" + photoPath + '\'' +
                ", _index=" + _index +
                ", fullPinyin=" + fullPinyin +
                '}';
    }
}
