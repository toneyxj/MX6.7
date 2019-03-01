package com.moxi.bookstore.bean;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2016/10/12.
 * 收藏book实体
 */
public class StoreBook implements Parcelable {

    private String authorName;
    private String bookName;
    private String coverPic;
    private Boolean ebook;
    private String editorRecommend;
    private Integer isSupportFullBuy;
    private String mediaType;
    private String price;
    private Integer priceUnit;
    private String publisher;
    private Long productId;

    private Long storeDateLong;

    private String title;


    public String getAuthorName() {
        return authorName;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getEditorRecommend() {
        return editorRecommend;
    }

    public void setEditorRecommend(String editorRecommend) {
        this.editorRecommend = editorRecommend;
    }

    public Integer getIsSupportFullBuy() {
        return isSupportFullBuy;
    }

    public void setIsSupportFullBuy(Integer isSupportFullBuy) {
        this.isSupportFullBuy = isSupportFullBuy;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public Integer getPriceUnit() {
        return priceUnit;
    }

    public void setPriceUnit(Integer priceUnit) {
        this.priceUnit = priceUnit;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getCoverPic() {
        return coverPic;
    }

    public void setCoverPic(String coverPic) {
        this.coverPic = coverPic;
    }

    public Boolean getEbook() {
        return ebook;
    }

    public void setEbook(Boolean ebook) {
        this.ebook = ebook;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getStoreDateLong() {
        return storeDateLong;
    }

    public void setStoreDateLong(Long storeDateLong) {
        this.storeDateLong = storeDateLong;
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
        dest.writeString(this.authorName);
        dest.writeString(this.bookName);
        dest.writeString(this.coverPic);
        dest.writeValue(this.ebook);
        dest.writeString(this.editorRecommend);
        dest.writeValue(this.isSupportFullBuy);
        dest.writeString(this.mediaType);
        dest.writeString(this.price);
        dest.writeValue(this.priceUnit);
        dest.writeString(this.publisher);
        dest.writeValue(this.productId);
        dest.writeValue(this.storeDateLong);
        dest.writeString(this.title);
    }

    public StoreBook() {
    }

    protected StoreBook(Parcel in) {
        this.authorName = in.readString();
        this.bookName = in.readString();
        this.coverPic = in.readString();
        this.ebook = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.editorRecommend = in.readString();
        this.isSupportFullBuy = (Integer) in.readValue(Integer.class.getClassLoader());
        this.mediaType = in.readString();
        this.price = in.readString();
        this.priceUnit = (Integer) in.readValue(Integer.class.getClassLoader());
        this.publisher = in.readString();
        this.productId = (Long) in.readValue(Long.class.getClassLoader());
        this.storeDateLong = (Long) in.readValue(Long.class.getClassLoader());
        this.title = in.readString();
    }

    public static final Creator<StoreBook> CREATOR = new Creator<StoreBook>() {
        @Override
        public StoreBook createFromParcel(Parcel source) {
            return new StoreBook(source);
        }

        @Override
        public StoreBook[] newArray(int size) {
            return new StoreBook[size];
        }
    };
}
