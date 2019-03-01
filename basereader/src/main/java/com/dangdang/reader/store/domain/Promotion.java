package com.dangdang.reader.store.domain;

import java.io.Serializable;

/**
 * Created by liupan on 2016/5/13.
 */
public class Promotion implements Serializable {

    /**
     * originalPrice : 63.84
     * promotionEndTime : 1464537600000
     * promotionLeftTime : 10134198000
     * promotionPic : http://img4.ddimg.cn/00035/pic/xsq_b.png
     * promotionPrice : 14.37
     * promotionStartTime : 1454403402000
     * promotionType : 102
     * stockStatus : 0
     */

    private float originalPrice;
    private float promotionPrice;
    private long promotionEndTime;
    private long promotionLeftTime;
    private String promotionPic;
    private long promotionStartTime;
    private int promotionType;
    private int stockStatus;

    public void setOriginalPrice(float originalPrice) {
        this.originalPrice = originalPrice;
    }

    public void setPromotionEndTime(long promotionEndTime) {
        this.promotionEndTime = promotionEndTime;
    }

    public void setPromotionLeftTime(long promotionLeftTime) {
        this.promotionLeftTime = promotionLeftTime;
    }

    public void setPromotionPic(String promotionPic) {
        this.promotionPic = promotionPic;
    }

    public void setPromotionPrice(float promotionPrice) {
        this.promotionPrice = promotionPrice;
    }

    public void setPromotionStartTime(long promotionStartTime) {
        this.promotionStartTime = promotionStartTime;
    }

    public void setPromotionType(int promotionType) {
        this.promotionType = promotionType;
    }

    public void setStockStatus(int stockStatus) {
        this.stockStatus = stockStatus;
    }

    public double getOriginalPrice() {
        return originalPrice;
    }

    public long getPromotionEndTime() {
        return promotionEndTime;
    }

    public long getPromotionLeftTime() {
        return promotionLeftTime;
    }

    public String getPromotionPic() {
        return promotionPic;
    }

    public double getPromotionPrice() {
        return promotionPrice;
    }

    public long getPromotionStartTime() {
        return promotionStartTime;
    }

    public int getPromotionType() {
        return promotionType;
    }

    public int getStockStatus() {
        return stockStatus;
    }
}
