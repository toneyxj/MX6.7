package com.moxi.haierc.model;

import com.mx.mxbase.model.BaseModel;

/**
 * Created by mua on 2018/1/16.
 */

public class Advertisement extends BaseModel {

    /**
     * result : {"extLink":null,"imageUrl":"/adimage/1_20170710170106_274_265.jpg"}
     */

    private ResultBean result;

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public static class ResultBean {
        /**
         * extLink : null
         * imageUrl : /adimage/1_20170710170106_274_265.jpg
         */

        private String extLink;
        private String imageUrl;

        public String getExtLink() {
            return extLink;
        }

        public void setExtLink(String extLink) {
            this.extLink = extLink;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }
    }
}
