package com.moxi.bookstore.bean;

import com.moxi.bookstore.bean.Message.MediaDetail;

import java.util.List;

/**
 * Created by Administrator on 2016/9/22.
 */
public class MediaSale {
    Integer isStroe;
    List<MediaDetail> mediaList;
    Long saleId;
    Integer shelfStatus;
    Integer type;

    public Integer getShelfStatus() {
        return shelfStatus;
    }

    public void setShelfStatus(Integer shelfStatus) {
        this.shelfStatus = shelfStatus;
    }

    public Integer getIsStroe() {
        return isStroe;
    }

    public void setIsStroe(Integer isStroe) {
        this.isStroe = isStroe;
    }

    public List<MediaDetail> getMediaList() {
        return mediaList;
    }

    public void setMediaList(List<MediaDetail> mediaList) {
        this.mediaList = mediaList;
    }

    public Long getSaleId() {
        return saleId;
    }

    public void setSaleId(Long saleId) {
        this.saleId = saleId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
