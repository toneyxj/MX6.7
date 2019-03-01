package com.moxi.bookstore.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2016/9/21.
 * 促销
 */
public class Promotion implements Parcelable {
    private Float promotionPrice;
    private Integer stockStatus;
    private Double lowestPcPrice;
    private Double lowestPrice;
    private Double originalPrice;
    private String promotionAlias;
    private Long promotionEndTime;
    private Long promotionLeftTime;
    private String promotionName;
    private String promotionPic;
    private Long promotionStartTime;
    private Integer promotionType;
    private Double salePrice;
    private String promotionDesc;
    private PromotionPrams promotionPrams;

    public Float getPromotionPrice() {
        return promotionPrice;
    }

    public void setPromotionPrice(Float promotionPrice) {
        this.promotionPrice = promotionPrice;
    }

    public Integer getStockStatus() {
        return stockStatus;
    }

    public void setStockStatus(Integer stockStatus) {
        this.stockStatus = stockStatus;
    }

    public Double getLowestPcPrice() {
        return lowestPcPrice;
    }

    public void setLowestPcPrice(Double lowestPcPrice) {
        this.lowestPcPrice = lowestPcPrice;
    }

    public Double getLowestPrice() {
        return lowestPrice;
    }

    public void setLowestPrice(Double lowestPrice) {
        this.lowestPrice = lowestPrice;
    }

    public Double getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(Double originalPrice) {
        this.originalPrice = originalPrice;
    }

    public String getPromotionAlias() {
        return promotionAlias;
    }

    public void setPromotionAlias(String promotionAlias) {
        this.promotionAlias = promotionAlias;
    }

    public Long getPromotionEndTime() {
        return promotionEndTime;
    }

    public void setPromotionEndTime(Long promotionEndTime) {
        this.promotionEndTime = promotionEndTime;
    }

    public Long getPromotionLeftTime() {
        return promotionLeftTime;
    }

    public void setPromotionLeftTime(Long promotionLeftTime) {
        this.promotionLeftTime = promotionLeftTime;
    }

    public String getPromotionName() {
        return promotionName;
    }

    public void setPromotionName(String promotionName) {
        this.promotionName = promotionName;
    }

    public String getPromotionPic() {
        return promotionPic;
    }

    public void setPromotionPic(String promotionPic) {
        this.promotionPic = promotionPic;
    }

    public Long getPromotionStartTime() {
        return promotionStartTime;
    }

    public void setPromotionStartTime(Long promotionStartTime) {
        this.promotionStartTime = promotionStartTime;
    }

    public Integer getPromotionType() {
        return promotionType;
    }

    public void setPromotionType(Integer promotionType) {
        this.promotionType = promotionType;
    }

    public Double getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(Double salePrice) {
        this.salePrice = salePrice;
    }

    public String getPromotionDesc() {
        return promotionDesc;
    }

    public void setPromotionDesc(String promotionDesc) {
        this.promotionDesc = promotionDesc;
    }

    public PromotionPrams getPromotionPrams() {
        return promotionPrams;
    }

    public void setPromotionPrams(PromotionPrams promotionPrams) {
        this.promotionPrams = promotionPrams;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.promotionPrice);
        dest.writeValue(this.stockStatus);
        dest.writeValue(this.lowestPcPrice);
        dest.writeValue(this.lowestPrice);
        dest.writeValue(this.originalPrice);
        dest.writeString(this.promotionAlias);
        dest.writeValue(this.promotionEndTime);
        dest.writeValue(this.promotionLeftTime);
        dest.writeString(this.promotionName);
        dest.writeString(this.promotionPic);
        dest.writeValue(this.promotionStartTime);
        dest.writeValue(this.promotionType);
        dest.writeValue(this.salePrice);
        dest.writeString(this.promotionDesc);
        dest.writeParcelable(this.promotionPrams, flags);
    }

    public Promotion() {
    }

    protected Promotion(Parcel in) {
        this.promotionPrice = (Float) in.readValue(Float.class.getClassLoader());
        this.stockStatus = (Integer) in.readValue(Integer.class.getClassLoader());
        this.lowestPcPrice = (Double) in.readValue(Double.class.getClassLoader());
        this.lowestPrice = (Double) in.readValue(Double.class.getClassLoader());
        this.originalPrice = (Double) in.readValue(Double.class.getClassLoader());
        this.promotionAlias = in.readString();
        this.promotionEndTime = (Long) in.readValue(Long.class.getClassLoader());
        this.promotionLeftTime = (Long) in.readValue(Integer.class.getClassLoader());
        this.promotionName = in.readString();
        this.promotionPic = in.readString();
        this.promotionStartTime = (Long) in.readValue(Long.class.getClassLoader());
        this.promotionType = (Integer) in.readValue(Integer.class.getClassLoader());
        this.salePrice = (Double) in.readValue(Double.class.getClassLoader());
        this.promotionDesc = in.readString();
        this.promotionPrams = in.readParcelable(PromotionPrams.class.getClassLoader());
    }

    public static final Parcelable.Creator<Promotion> CREATOR = new Parcelable.Creator<Promotion>() {
        @Override
        public Promotion createFromParcel(Parcel source) {
            return new Promotion(source);
        }

        @Override
        public Promotion[] newArray(int size) {
            return new Promotion[size];
        }
    };
}

