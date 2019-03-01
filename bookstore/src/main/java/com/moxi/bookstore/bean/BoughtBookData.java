package com.moxi.bookstore.bean;

import com.moxi.bookstore.bean.Message.MediaDetail;

import java.util.List;

/**
 * Created by Administrator on 2016/10/12.
 */
public class BoughtBookData extends Data {

    private Integer hseNextPage;
    private List<MediaDetail> mediaList;

    public Integer getHseNextPage() {
        return hseNextPage;
    }

    public void setHseNextPage(Integer hseNextPage) {
        this.hseNextPage = hseNextPage;
    }

    public List<MediaDetail> getMediaList() {
        return mediaList;
    }

    public void setMediaList(List<MediaDetail> mediaList) {
        this.mediaList = mediaList;
    }
}
