package com.dangdang.reader.dread.format.part;

/**
 * 分章阅读的购买信息
 * Created by Yhyu on 2015/5/19.
 */
public class PartBuyInfo {

    private int chapterId;
    private int chapterPrice;
    private String chapterTitle;
    private String mediaId;
    private int wordCnt;
    private int mainBalance;
    private int subBalance;

    //full
    private String saleId;
    private String saleName;
    private int salePrice;
    private int isSupportFullBuy;
    private long mediaWordCnt;

    public int getMainBalance() {
        return mainBalance;
    }

    public void setMainBalance(int mainBalance) {
        this.mainBalance = mainBalance;
    }

    public int getSubBalance() {
        return subBalance;
    }

    public void setSubBalance(int subBalance) {
        this.subBalance = subBalance;
    }

    public int getChapterId() {
        return chapterId;
    }

    public void setChapterId(int chapterId) {
        this.chapterId = chapterId;
    }

    public int getChapterPrice() {
        return chapterPrice;
    }

    public void setChapterPrice(int chapterPrice) {
        this.chapterPrice = chapterPrice;
    }

    public String getChapterTitle() {
        return chapterTitle;
    }

    public void setChapterTitle(String chapterTitle) {
        this.chapterTitle = chapterTitle;
    }

    public String getMediaId() {
        return mediaId;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }

    public int getWordCnt() {
        return wordCnt;
    }

    public void setWordCnt(int wordCnt) {
        this.wordCnt = wordCnt;
    }

    public String getSaleId() {
        return saleId;
    }

    public void setSaleId(String saleId) {
        this.saleId = saleId;
    }

    public String getSaleName() {
        return saleName;
    }

    public void setSaleName(String saleName) {
        this.saleName = saleName;
    }

    public int getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(int salePrice) {
        this.salePrice = salePrice;
    }

    public int getIsSupportFullBuy() {
        return isSupportFullBuy;
    }

    public void setIsSupportFullBuy(int isSupportFullBuy) {
        this.isSupportFullBuy = isSupportFullBuy;
    }

    public long getMediaWordCnt() {
        return mediaWordCnt;
    }

    public void setMediaWordCnt(long mediaWordCnt) {
        this.mediaWordCnt = mediaWordCnt;
    }

    @Override
    public String toString() {
        return "BuyInfo{" +
                "chapterId='" + chapterId + '\'' +
                ", chapterPrice=" + chapterPrice +
                ", chapterTitle='" + chapterTitle + '\'' +
                ", mediaId='" + mediaId + '\'' +
                ", wordCnt=" + wordCnt +
                ", mainBalance=" + mainBalance +
                ", subBalance=" + subBalance +
                ", saleId='" + saleId + '\'' +
                ", saleName='" + saleName + '\'' +
                ", salePrice=" + salePrice +
                ", isSupportFullBuy=" + isSupportFullBuy +
                ", mediaWordCnt=" + mediaWordCnt +
                '}';
    }
}
