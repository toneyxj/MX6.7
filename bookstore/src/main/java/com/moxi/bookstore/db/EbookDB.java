package com.moxi.bookstore.db;

/**
 * Created by Administrator on 2016/9/25.
 * 电子书存储对象
 */
public class EbookDB {
    public Long saleId;
    public String name;
    public String author;
    public String publisher;
    public String iconUrl;
    public String type;//所屬種類
    public String filePath;
    public String filePathMd5;
    public Long publishtime;
    public Long downloadtime;
    public Long lastreadtime;
    public Long chartcount;
    public int pagecount;//章節數
    public int flag;//0 试读 1全本
    public String key;//证书key
    public String progress;//阅读进程
    public String bookdesc;//简介
    public Double lowestprice;
    public Double orgprice;
    public Long getSaleId() {
        return saleId;
    }

    public void setSaleId(Long saleId) {
        this.saleId = saleId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Long getDownloadtime() {
        return downloadtime;
    }

    public void setDownloadtime(Long downloadtime) {
        this.downloadtime = downloadtime;
    }

    public Long getLastreadtime() {
        return lastreadtime;
    }

    public void setLastreadtime(Long lastreadtime) {
        this.lastreadtime = lastreadtime;
    }

    public Long getChartcount() {
        return chartcount;
    }

    public void setChartcount(Long chartcount) {
        this.chartcount = chartcount;
    }

    public int getPagecount() {
        return pagecount;
    }

    public void setPagecount(int pagecount) {
        this.pagecount = pagecount;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public Long getPublishtime() {
        return publishtime;
    }

    public void setPublishtime(Long publishtime) {
        this.publishtime = publishtime;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getProgress() {
        return progress;
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }

    public String getBookdesc() {
        return bookdesc;
    }

    public void setBookdesc(String bookdesc) {
        this.bookdesc = bookdesc;
    }

    public Double getLowestprice() {
        return lowestprice;
    }

    public void setLowestprice(Double lowestprice) {
        this.lowestprice = lowestprice;
    }

    public Double getOrgprice() {
        return orgprice;
    }

    public void setOrgprice(Double orgprice) {
        this.orgprice = orgprice;
    }

    @Override
    public String toString() {
        return "EbookDB{" +
                "saleId=" + saleId +
                ", name='" + name + '\'' +
                ", author='" + author + '\'' +
                ", publisher='" + publisher + '\'' +
                ", iconUrl='" + iconUrl + '\'' +
                ", type='" + type + '\'' +
                ", filePath='" + filePath + '\'' +
                ", filePathMd5='" + filePathMd5 + '\'' +
                ", publishtime=" + publishtime +
                ", downloadtime=" + downloadtime +
                ", lastreadtime=" + lastreadtime +
                ", chartcount=" + chartcount +
                ", pagecount=" + pagecount +
                ", flag=" + flag +
                ", key='" + key + '\'' +
                ", progress='" + progress + '\'' +
                ", bookdesc='" + bookdesc + '\'' +
                ", lowestprice=" + lowestprice +
                ", orgprice=" + orgprice +
                '}';
    }
}
