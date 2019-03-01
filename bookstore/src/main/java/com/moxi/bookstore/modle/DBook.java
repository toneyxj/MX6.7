package com.moxi.bookstore.modle;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/10/9.
 */
public class DBook extends DataSupport implements Serializable {
    @Column(unique = true)
    public long id;

    public Long saleId;
    public String name;
    public String author;
    public String publisher;
    public String iconUrl;
    public String type;//所屬種類
    public String filePath;
    public Long publishtime;
    public Long downloadtime;
    public Long lastreadtime;
    public Long chartcount;
    public int pagecount;//章節數
    public int flag;//0 试读 1全本
    public byte[] key;//证书key
    public String progress;//阅读进程

    public DBook(long id, Long saleId, String name,
                 String author, String publisher, String iconUrl,
                 String type, String filePath, Long publishtime,
                 Long downloadtime, Long lastreadtime, Long chartcount,
                 int pagecount, int flag, byte[] key, String progress) {
        this.id = id;
        this.saleId = saleId;
        this.name = name;
        this.author = author;
        this.publisher = publisher;
        this.iconUrl = iconUrl;
        this.type = type;
        this.filePath = filePath;
        this.publishtime = publishtime;
        this.downloadtime = downloadtime;
        this.lastreadtime = lastreadtime;
        this.chartcount = chartcount;
        this.pagecount = pagecount;
        this.flag = flag;
        this.key = key;
        this.progress = progress;
    }

    public DBook( Long saleId, String name,
                 String author, String publisher, String iconUrl,
                 String type, String filePath, Long publishtime,
                 Long downloadtime, Long lastreadtime, Long chartcount,
                 int pagecount, int flag, byte[] key, String progress) {

        this.saleId = saleId;
        this.name = name;
        this.author = author;
        this.publisher = publisher;
        this.iconUrl = iconUrl;
        this.type = type;
        this.filePath = filePath;
        this.publishtime = publishtime;
        this.downloadtime = downloadtime;
        this.lastreadtime = lastreadtime;
        this.chartcount = chartcount;
        this.pagecount = pagecount;
        this.flag = flag;
        this.key = key;
        this.progress = progress;
    }
}
