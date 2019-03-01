package com.dangdang.reader.dread.data;

public class PartReadInfo extends ReadInfo {
    private String saleId;
    private String bookDesc;
    private String bookAuthor;
    private String bookCover;
    private String  bookCategories;
    private boolean isShelf;
    private boolean isFollow;
    private boolean isSupportFull;//是否支持全本购买
    private boolean isFull;//是否支持全本购买
    private int indexOrder;
    private int targetChapterId;
    private  boolean isTimeFree;//是否限免
    private boolean isBoughtChapter;//是否购买过原创。自动添加书架
    private boolean isAutoBuy;

    public boolean isAutoBuy() {
        return isAutoBuy;
    }

    public void setIsAutoBuy(boolean isAutoBuy) {
        this.isAutoBuy = isAutoBuy;
    }

    public boolean isBoughtChapter() {
        return isBoughtChapter;
    }

    public void setIsBoughtChapter(boolean isBoughtChapter) {
        this.isBoughtChapter = isBoughtChapter;
    }

    public boolean isTimeFree() {
        return isTimeFree;
    }

    public void setIsTimeFree(boolean isTimeFree) {
        this.isTimeFree = isTimeFree;
    }

    public int getTargetChapterId() {
        return targetChapterId;
    }

    public void setTargetChapterId(int targetChapterId) {
        this.targetChapterId = targetChapterId;
    }

    public boolean isShelf() {
        return isShelf;
    }

    public void setIsShelf(boolean isShelf) {
        this.isShelf = isShelf;
    }

    @Override
    public String getBookCover() {
        return bookCover;
    }

    public void setBookCover(String bookCover) {
        this.bookCover = bookCover;
    }

    public String getSaleId() {
        return saleId;
    }

    public void setSaleId(String saleId) {
        this.saleId = saleId;
    }

    public String getBookDesc() {
        return bookDesc;
    }

    public void setBookDesc(String bookDesc) {
        this.bookDesc = bookDesc;
    }

    public String getBookAuthor() {
        return bookAuthor;
    }

    public void setBookAuthor(String bookAuthor) {
        this.bookAuthor = bookAuthor;
    }

    public String getBookCategories() {
        return bookCategories;
    }

    public void setBookCategories(String bookCategories) {
        this.bookCategories = bookCategories;
    }

    public boolean isFollow() {
        return isFollow;
    }

    public void setIsFollow(boolean isFollow) {
        this.isFollow = isFollow;
    }

    public int getIndexOrder() {
        return indexOrder;
    }

    public void setIndexOrder(int indexOrder) {
        this.indexOrder = indexOrder;
    }

    public boolean isSupportFull() {
        return isSupportFull;
    }

    public void setIsSupportFull(boolean isSupportFull) {
        this.isSupportFull = isSupportFull;
    }

    public boolean isFull() {
        return isFull;
    }

    public void setIsFull(boolean isFull) {
        this.isFull = isFull;
    }
}
