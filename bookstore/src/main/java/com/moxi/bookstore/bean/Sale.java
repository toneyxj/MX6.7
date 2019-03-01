package com.moxi.bookstore.bean;


import java.util.List;

/**
 * Created by Administrator on 2016/9/21.
 * salelist 下的item
 */
public class Sale {
    private Integer isStore;

    private Integer isSupportFullBuy;

    private List<Media> mediaList ;

    private Integer saleId;
    private Integer price;
    private Integer type;

    public Integer getIsStore() {
        return isStore;
    }

    public void setIsStore(Integer isStore) {
        this.isStore = isStore;
    }

    public Integer getIsSupportFullBuy() {
        return isSupportFullBuy;
    }

    public void setIsSupportFullBuy(Integer isSupportFullBuy) {
        this.isSupportFullBuy = isSupportFullBuy;
    }

    public List<Media> getMediaList() {
        return mediaList;
    }

    public void setMediaList(List<Media> mediaList) {
        this.mediaList = mediaList;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getSaleId() {
        return saleId;
    }

    public void setSaleId(Integer saleId) {
        this.saleId = saleId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Sale{" +
                "isStore=" + isStore +
                ", isSupportFullBuy=" + isSupportFullBuy +
                ", mediaList=" + mediaList +
                ", saleId=" + saleId +
                ", price=" + price +
                ", type=" + type +
                '}';
    }
}
