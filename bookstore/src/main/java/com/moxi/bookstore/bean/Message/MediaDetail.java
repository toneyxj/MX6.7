package com.moxi.bookstore.bean.Message;


import android.os.Parcel;
import android.os.Parcelable;
import com.moxi.bookstore.bean.Media;


/**
 * Created by Administrator on 2016/9/22.
 * 电子书详情数据
 */
public class MediaDetail extends Media implements Parcelable{
    Long publishDate;
    Integer canBorrow;
    String category;
    String audioAuthor;
    String deviceTypeCodes;
    Long fileSize;
    Integer freeBook;
    Long freeFileSize;
    /**
     * 判断当前是否有租阅权限
     */
    Integer isChannelMonth;
    Integer isChapterAuthority;
    Integer isFreePlanMedia;
    Integer isSupportDevice;
    Integer isWholeAuthority;
    String isbn;
    String paperMediaProductId;
    String publisher;
    Float score;
    Long wordCnt;
    Integer duration;
    Integer isSupportFullBuy;
    String needBuy;
    Long mediaAuthorityId;//书籍编号

    public Long getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(Long publishDate) {
        this.publishDate = publishDate;
    }

    public Integer getCanBorrow() {
        return canBorrow;
    }

    public void setCanBorrow(Integer canBorrow) {
        this.canBorrow = canBorrow;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAudioAuthor() {
        return audioAuthor;
    }

    public void setAudioAuthor(String audioAuthor) {
        this.audioAuthor = audioAuthor;
    }

    public String getDeviceTypeCodes() {
        return deviceTypeCodes;
    }

    public void setDeviceTypeCodes(String deviceTypeCodes) {
        this.deviceTypeCodes = deviceTypeCodes;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public Integer getFreeBook() {
        return freeBook;
    }

    public void setFreeBook(Integer freeBook) {
        this.freeBook = freeBook;
    }

    public Long getFreeFileSize() {
        return freeFileSize;
    }

    public void setFreeFileSize(Long freeFileSize) {
        this.freeFileSize = freeFileSize;
    }

    public Integer getIsChannelMonth() {
        return isChannelMonth;
    }

    public void setIsChannelMonth(Integer isChannelMonth) {
        this.isChannelMonth = isChannelMonth;
    }

    public Integer getIsChapterAuthority() {
        return isChapterAuthority;
    }

    public void setIsChapterAuthority(Integer isChapterAuthority) {
        this.isChapterAuthority = isChapterAuthority;
    }

    public Integer getIsFreePlanMedia() {
        return isFreePlanMedia;
    }

    public void setIsFreePlanMedia(Integer isFreePlanMedia) {
        this.isFreePlanMedia = isFreePlanMedia;
    }

    public Integer getIsSupportDevice() {
        return isSupportDevice;
    }

    public void setIsSupportDevice(Integer isSupportDevice) {
        this.isSupportDevice = isSupportDevice;
    }

    public Integer getIsWholeAuthority() {
        return isWholeAuthority;
    }

    public void setIsWholeAuthority(Integer isWholeAuthority) {
        this.isWholeAuthority = isWholeAuthority;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getPaperMediaProductId() {
        return paperMediaProductId;
    }

    public void setPaperMediaProductId(String paperMediaProductId) {
        this.paperMediaProductId = paperMediaProductId;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public Float getScore() {
        return score;
    }

    public void setScore(Float score) {
        this.score = score;
    }

    public Long getWordCnt() {
        return wordCnt;
    }

    public void setWordCnt(Long wordCnt) {
        this.wordCnt = wordCnt;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getIsSupportFullBuy() {
        return isSupportFullBuy;
    }

    public void setIsSupportFullBuy(Integer isSupportFullBuy) {
        this.isSupportFullBuy = isSupportFullBuy;
    }

    public String getNeedBuy() {
        return needBuy;
    }

    public void setNeedBuy(String needBuy) {
        this.needBuy = needBuy;
    }

    public Long getMediaAuthorityId() {
        return mediaAuthorityId;
    }

    public void setMediaAuthorityId(Long mediaAuthorityId) {
        this.mediaAuthorityId = mediaAuthorityId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeValue(this.publishDate);
        dest.writeValue(this.canBorrow);
        dest.writeString(this.category);
        dest.writeString(this.audioAuthor);
        dest.writeString(this.deviceTypeCodes);
        dest.writeValue(this.fileSize);
        dest.writeValue(this.freeBook);
        dest.writeValue(this.freeFileSize);
        dest.writeValue(this.isChannelMonth);
        dest.writeValue(this.isChapterAuthority);
        dest.writeValue(this.isFreePlanMedia);
        dest.writeValue(this.isSupportDevice);
        dest.writeValue(this.isWholeAuthority);
        dest.writeString(this.isbn);
        dest.writeString(this.paperMediaProductId);
        dest.writeString(this.publisher);
        dest.writeValue(this.score);
        dest.writeValue(this.wordCnt);
        dest.writeValue(this.duration);
        dest.writeValue(this.isSupportFullBuy);
        dest.writeString(this.needBuy);
        dest.writeLong(this.mediaAuthorityId);
    }

    public MediaDetail() {
    }

    protected MediaDetail(Parcel in) {
        super(in);
        this.publishDate = (Long) in.readValue(Long.class.getClassLoader());
        this.canBorrow = (Integer) in.readValue(Integer.class.getClassLoader());
        this.category = in.readString();
        this.audioAuthor = in.readString();
        this.deviceTypeCodes = in.readString();
        this.fileSize = (Long) in.readValue(Long.class.getClassLoader());
        this.freeBook = (Integer) in.readValue(Integer.class.getClassLoader());
        this.freeFileSize = (Long) in.readValue(Long.class.getClassLoader());
        this.isChannelMonth = (Integer) in.readValue(Integer.class.getClassLoader());
        this.isChapterAuthority = (Integer) in.readValue(Integer.class.getClassLoader());
        this.isFreePlanMedia = (Integer) in.readValue(Integer.class.getClassLoader());
        this.isSupportDevice = (Integer) in.readValue(Integer.class.getClassLoader());
        this.isWholeAuthority = (Integer) in.readValue(Integer.class.getClassLoader());
        this.isbn = in.readString();
        this.paperMediaProductId = in.readString();
        this.publisher = in.readString();
        this.score = (Float) in.readValue(Float.class.getClassLoader());
        this.wordCnt = (Long) in.readValue(Long.class.getClassLoader());
        this.duration = (Integer) in.readValue(Integer.class.getClassLoader());
        this.isSupportFullBuy = (Integer) in.readValue(Integer.class.getClassLoader());
        this.needBuy = in.readString();
        this.mediaAuthorityId = in.readLong();
    }

    public static final Creator<MediaDetail> CREATOR = new Creator<MediaDetail>() {
        @Override
        public MediaDetail createFromParcel(Parcel source) {
            return new MediaDetail(source);
        }

        @Override
        public MediaDetail[] newArray(int size) {
            return new MediaDetail[size];
        }
    };

    @Override
    public String toString() {
        return "MediaDetail{" +
                "publishDate=" + publishDate +
                ", canBorrow=" + canBorrow +
                ", category='" + category + '\'' +
                ", audioAuthor='" + audioAuthor + '\'' +
                ", deviceTypeCodes='" + deviceTypeCodes + '\'' +
                ", fileSize=" + fileSize +
                ", freeBook=" + freeBook +
                ", freeFileSize=" + freeFileSize +
                ", isChannelMonth=" + isChannelMonth +
                ", isChapterAuthority=" + isChapterAuthority +
                ", isFreePlanMedia=" + isFreePlanMedia +
                ", isSupportDevice=" + isSupportDevice +
                ", isWholeAuthority=" + isWholeAuthority +
                ", isbn='" + isbn + '\'' +
                ", paperMediaProductId='" + paperMediaProductId + '\'' +
                ", publisher='" + publisher + '\'' +
                ", score=" + score +
                ", wordCnt=" + wordCnt +
                ", duration=" + duration +
                ", isSupportFullBuy=" + isSupportFullBuy +
                ", needBuy='" + needBuy + '\'' +
                ", mediaAuthorityId=" + mediaAuthorityId +
                '}';
    }
}
