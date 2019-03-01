package com.moxi.bookstore.bean;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by Administrator on 2016/9/21.
 * salelist 下的media
 */
public class Media implements Parcelable{

    private Long authorId;

    private String authorPenname;

    private Integer avgStarLevel;

    private String categoryIds;

    private String categorys;
    /**
     *判断当前书是否是租阅书
     */
    private boolean channelHall;

    private Integer chapterCnt;

    private Integer commentNumber;

    private String coverPic;

    private String descs;

    private String editorRecommend;

    private Integer isFull;

    private Integer isStore;

    private Double lowestPrice;

    private Long mediaId;

    private Integer mediaType;

    private Double originalPrice;

    private Long paperBookId;

    private Integer paperBookPrice;

    private Integer price;

    private Integer priceUnit;
    private Integer promotionId;

    private List<Promotion> promotionList ;

    private String recommandWords;

    private Long saleId;

    private Double salePrice;

    private Integer shelfStatus;

    private String subTitle;

    private String title;
    public String vipMediaPic;

    public static final Creator<Media> CREATOR = new Creator<Media>() {
        @Override
        public Media createFromParcel(Parcel in) {
            return new Media(in);
        }

        @Override
        public Media[] newArray(int size) {
            return new Media[size];
        }
    };

    public Integer getPromotionId() {
        return promotionId;
    }

    public void setPromotionId(Integer promotionId) {
        this.promotionId = promotionId;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public String getAuthorPenname() {
        return authorPenname;
    }

    public void setAuthorPenname(String authorPenname) {
        this.authorPenname = authorPenname;
    }

    public Integer getAvgStarLevel() {
        return avgStarLevel;
    }

    public void setAvgStarLevel(Integer avgStarLevel) {
        this.avgStarLevel = avgStarLevel;
    }

    public String getCategoryIds() {
        return categoryIds;
    }

    public void setCategoryIds(String categoryIds) {
        this.categoryIds = categoryIds;
    }

    public String getCategorys() {
        return categorys;
    }

    public void setCategorys(String categorys) {
        this.categorys = categorys;
    }

    public boolean isChannelHall() {
        return channelHall;
    }

    public void setChannelHall(boolean channelHall) {
        this.channelHall = channelHall;
    }

    public Integer getChapterCnt() {
        return chapterCnt;
    }

    public void setChapterCnt(Integer chapterCnt) {
        this.chapterCnt = chapterCnt;
    }

    public Integer getCommentNumber() {
        return commentNumber;
    }

    public void setCommentNumber(Integer commentNumber) {
        this.commentNumber = commentNumber;
    }

    public String getCoverPic() {
        return coverPic;
    }

    public void setCoverPic(String coverPic) {
        this.coverPic = coverPic;
    }

    public String getDescs() {
        return descs;
    }

    public void setDescs(String descs) {
        this.descs = descs;
    }

    public String getEditorRecommend() {
        return editorRecommend;
    }

    public void setEditorRecommend(String editorRecommend) {
        this.editorRecommend = editorRecommend;
    }

    public Integer getIsFull() {
        return isFull;
    }

    public void setIsFull(Integer isFull) {
        this.isFull = isFull;
    }

    public Integer getIsStore() {
        return isStore;
    }

    public void setIsStore(Integer isStore) {
        this.isStore = isStore;
    }

    public Double getLowestPrice() {
        return lowestPrice;
    }

    public void setLowestPrice(Double lowestPrice) {
        this.lowestPrice = lowestPrice;
    }

    public Long getMediaId() {
        return mediaId;
    }

    public void setMediaId(Long mediaId) {
        this.mediaId = mediaId;
    }

    public Integer getMediaType() {
        return mediaType;
    }

    public void setMediaType(Integer mediaType) {
        this.mediaType = mediaType;
    }

    public Double getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(Double originalPrice) {
        this.originalPrice = originalPrice;
    }

    public Long getPaperBookId() {
        return paperBookId;
    }

    public void setPaperBookId(Long paperBookId) {
        this.paperBookId = paperBookId;
    }

    public Integer getPaperBookPrice() {
        return paperBookPrice;
    }

    public void setPaperBookPrice(Integer paperBookPrice) {
        this.paperBookPrice = paperBookPrice;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getPriceUnit() {
        return priceUnit;
    }

    public void setPriceUnit(Integer priceUnit) {
        this.priceUnit = priceUnit;
    }

    public List<Promotion> getPromotionList() {
        return promotionList;
    }

    public void setPromotionList(List<Promotion> promotionList) {
        this.promotionList = promotionList;
    }

    public String getRecommandWords() {
        return recommandWords;
    }

    public void setRecommandWords(String recommandWords) {
        this.recommandWords = recommandWords;
    }

    public Long getSaleId() {
        return saleId;
    }

    public void setSaleId(Long saleId) {
        this.saleId = saleId;
    }

    public Double getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(Double salePrice) {
        this.salePrice = salePrice;
    }

    public Integer getShelfStatus() {
        return shelfStatus;
    }

    public void setShelfStatus(Integer shelfStatus) {
        this.shelfStatus = shelfStatus;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.authorId);
        dest.writeString(this.authorPenname);
        dest.writeValue(this.avgStarLevel);
        dest.writeString(this.categoryIds);
        dest.writeString(this.categorys);
        dest.writeByte(this.channelHall ? (byte) 1 : (byte) 0);
        dest.writeValue(this.chapterCnt);
        dest.writeValue(this.commentNumber);
        dest.writeString(this.coverPic);
        dest.writeString(this.descs);
        dest.writeString(this.editorRecommend);
        dest.writeValue(this.isFull);
        dest.writeValue(this.isStore);
        dest.writeValue(this.lowestPrice);
        dest.writeValue(this.mediaId);
        dest.writeValue(this.mediaType);
        dest.writeValue(this.originalPrice);
        dest.writeValue(this.paperBookId);
        dest.writeValue(this.paperBookPrice);
        dest.writeValue(this.price);
        dest.writeValue(this.priceUnit);
        dest.writeValue(this.promotionId);
        dest.writeTypedList(this.promotionList);
        dest.writeString(this.recommandWords);
        dest.writeValue(this.saleId);
        dest.writeValue(this.salePrice);
        dest.writeValue(this.shelfStatus);
        dest.writeString(this.subTitle);
        dest.writeString(this.title);
        dest.writeString(this.vipMediaPic);
    }

    public Media() {
    }

    protected Media(Parcel in) {
        this.authorId = (Long) in.readValue(Long.class.getClassLoader());
        this.authorPenname = in.readString();
        this.avgStarLevel = (Integer) in.readValue(Integer.class.getClassLoader());
        this.categoryIds = in.readString();
        this.categorys = in.readString();
        this.channelHall = in.readByte() != 0;
        this.chapterCnt = (Integer) in.readValue(Integer.class.getClassLoader());
        this.commentNumber = (Integer) in.readValue(Integer.class.getClassLoader());
        this.coverPic = in.readString();
        this.descs = in.readString();
        this.editorRecommend = in.readString();
        this.isFull = (Integer) in.readValue(Integer.class.getClassLoader());
        this.isStore = (Integer) in.readValue(Integer.class.getClassLoader());
        this.lowestPrice = (Double) in.readValue(Double.class.getClassLoader());
        this.mediaId = (Long) in.readValue(Long.class.getClassLoader());
        this.mediaType = (Integer) in.readValue(Integer.class.getClassLoader());
        this.originalPrice = (Double) in.readValue(Double.class.getClassLoader());
        this.paperBookId = (Long) in.readValue(Long.class.getClassLoader());
        this.paperBookPrice = (Integer) in.readValue(Integer.class.getClassLoader());
        this.price = (Integer) in.readValue(Integer.class.getClassLoader());
        this.priceUnit = (Integer) in.readValue(Integer.class.getClassLoader());
        this.promotionId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.promotionList = in.createTypedArrayList(Promotion.CREATOR);
        this.recommandWords = in.readString();
        this.saleId = (Long) in.readValue(Long.class.getClassLoader());
        this.salePrice = (Double) in.readValue(Double.class.getClassLoader());
        this.shelfStatus = (Integer) in.readValue(Integer.class.getClassLoader());
        this.subTitle = in.readString();
        this.title = in.readString();
        this.vipMediaPic = in.readString();
    }
}
