package com.moxi.biji.mdoel;

import java.io.Serializable;

/**
 * Created by Administrator on 2019/2/28.
 */

public class BiJiModel implements Serializable{
    /**
     *同步文件名
     */
    private String noteBook;
    /**
     *同步标题
     */
    private String title;
    /**
     *同步内容
     */
    private String content;
    /**
     *分析的内容类型，1文字，2图片
     */
    private int shareType;
    /**
     *sdk分类 1：印象笔记
     */
    private  int sdkType;


    public static BiJiModel builder(){
        return new BiJiModel();
    }

    public String getNoteBook() {
        return noteBook;
    }
    /**
     *同步文件名
     */
    public BiJiModel setNoteBook(String noteBook) {
        this.noteBook = noteBook;
        return this;
    }

    public String getTitle() {
        return title;
    }
    /**
     *同步标题
     */
    public BiJiModel setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getContent() {
        return content;
    }
    /**
     *同步内容
     */
    public BiJiModel setContent(String content) {
        this.content = content;
        return this;
    }

    public int getShareType() {
        return shareType;
    }
    /**
     *分析的内容类型，1文字，2图片
     */
    public BiJiModel setShareType(int shareType) {
        this.shareType = shareType;
        return this;
    }

    public int getSdkType() {
        return sdkType;
    }
    /**
     *sdk分类 1：印象笔记
     */
    public BiJiModel setSdkType(int sdkType) {
        this.sdkType = sdkType;
        return this;
    }
}
