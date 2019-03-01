package com.moxi.bookstore.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2016/10/12.
 * 购买book实体
 */
public class BoughtBook implements Parcelable {

    private String authorPenName;

    private Long boughtId;

    private Long custId;

    private Long mediaId;

    private String mediaTitle;

    private Double payMainPrice;

    private Double paySubPrice;

    private Long saleId;

    private Long updateTime;

    private Integer wholeFlag;
    private Double otherPrice;
    private String mediaCoverPic;


    public String getAuthorPenName() {
        return authorPenName;
    }

    public void setAuthorPenName(String authorPenName) {
        this.authorPenName = authorPenName;
    }

    public Long getBoughtId() {
        return boughtId;
    }

    public void setBoughtId(Long boughtId) {
        this.boughtId = boughtId;
    }

    public Long getCustId() {
        return custId;
    }

    public void setCustId(Long custId) {
        this.custId = custId;
    }

    public Long getMediaId() {
        return mediaId;
    }

    public void setMediaId(Long mediaId) {
        this.mediaId = mediaId;
    }

    public String getMediaTitle() {
        return mediaTitle;
    }

    public void setMediaTitle(String mediaTitle) {
        this.mediaTitle = mediaTitle;
    }

    public Double getPayMainPrice() {
        return payMainPrice;
    }

    public void setPayMainPrice(Double payMainPrice) {
        this.payMainPrice = payMainPrice;
    }

    public Double getPaySubPrice() {
        return paySubPrice;
    }

    public void setPaySubPrice(Double paySubPrice) {
        this.paySubPrice = paySubPrice;
    }

    public Long getSaleId() {
        return saleId;
    }

    public void setSaleId(Long saleId) {
        this.saleId = saleId;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getWholeFlag() {
        return wholeFlag;
    }

    public void setWholeFlag(Integer wholeFlag) {
        this.wholeFlag = wholeFlag;
    }

    public Double getOtherPrice() {
        return otherPrice;
    }

    public void setOtherPrice(Double otherPrice) {
        this.otherPrice = otherPrice;
    }

    public String getMediaCoverPic() {
        return mediaCoverPic;
    }

    public void setMediaCoverPic(String mediaCoverPic) {
        this.mediaCoverPic = mediaCoverPic;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.authorPenName);
        dest.writeValue(this.boughtId);
        dest.writeValue(this.custId);
        dest.writeValue(this.mediaId);
        dest.writeString(this.mediaTitle);
        dest.writeValue(this.payMainPrice);
        dest.writeValue(this.paySubPrice);
        dest.writeValue(this.saleId);
        dest.writeValue(this.updateTime);
        dest.writeValue(this.wholeFlag);
        dest.writeValue(this.otherPrice);
        dest.writeString(this.mediaCoverPic);
    }

    public BoughtBook() {
    }

    protected BoughtBook(Parcel in) {
        this.authorPenName = in.readString();
        this.boughtId = (Long) in.readValue(Long.class.getClassLoader());
        this.custId = (Long) in.readValue(Long.class.getClassLoader());
        this.mediaId = (Long) in.readValue(Long.class.getClassLoader());
        this.mediaTitle = in.readString();
        this.payMainPrice = (Double) in.readValue(Double.class.getClassLoader());
        this.paySubPrice = (Double) in.readValue(Double.class.getClassLoader());
        this.saleId = (Long) in.readValue(Long.class.getClassLoader());
        this.updateTime = (Long) in.readValue(Long.class.getClassLoader());
        this.wholeFlag = (Integer) in.readValue(Integer.class.getClassLoader());
        this.otherPrice = (Double) in.readValue(Double.class.getClassLoader());
        this.mediaCoverPic = in.readString();
    }

    public static final Parcelable.Creator<BoughtBook> CREATOR = new Parcelable.Creator<BoughtBook>() {
        @Override
        public BoughtBook createFromParcel(Parcel source) {
            return new BoughtBook(source);
        }

        @Override
        public BoughtBook[] newArray(int size) {
            return new BoughtBook[size];
        }
    };
}
