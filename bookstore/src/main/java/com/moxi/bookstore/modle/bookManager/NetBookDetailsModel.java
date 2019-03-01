package com.moxi.bookstore.modle.bookManager;


import com.mx.mxbase.model.BaseModel;

import java.io.Serializable;

/**
 * 书籍详情model
 * Created by Archer on 16/8/4.
 */
public class NetBookDetailsModel extends BaseModel implements Serializable {
    private NetBookDetails result;

    public NetBookDetails getResult() {
        return result;
    }

    public void setResult(NetBookDetails result) {
        this.result = result;
    }

    public class NetBookDetails implements Serializable {
        private String addTime;//添加时间
        private String author;//作者
        private String bookTypeId;//书籍类型id
        private String bookTypeName;//书籍类型名称
        private String coverImage;//书籍缩略图
        private String desc;//书籍描述
        private String id;//书籍id
        private String name;//书籍名称
        private String originalName;//原作者
        private String price;//价格
        private String saveFile;//保存路径
        private String state;//状态
        private String uploadFile;//上传路径

        public String getAddTime() {
            return addTime;
        }

        public void setAddTime(String addTime) {
            this.addTime = addTime;
        }

        public String getBookTypeId() {
            return bookTypeId;
        }

        public void setBookTypeId(String bookTypeId) {
            this.bookTypeId = bookTypeId;
        }

        public String getBookTypeName() {
            return bookTypeName;
        }

        public void setBookTypeName(String bookTypeName) {
            this.bookTypeName = bookTypeName;
        }

        public String getCoverImage() {
            return coverImage;
        }

        public void setCoverImage(String coverImage) {
            this.coverImage = coverImage;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSaveFile() {
            return saveFile;
        }

        public void setSaveFile(String saveFile) {
            this.saveFile = saveFile;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getUploadFile() {
            return uploadFile;
        }

        public void setUploadFile(String uploadFile) {
            this.uploadFile = uploadFile;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public String getOriginalName() {
            return originalName;
        }

        public void setOriginalName(String originalName) {
            this.originalName = originalName;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }
    }
}
