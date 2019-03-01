package com.moxi.bookreader.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by King on 2017/10/31.
 */

public class BookList implements Serializable {

    /**
     * code : 0
     * msg : ok
     * result : [{"createtime":"2017-10-30 11:34:53","id":11,"imgPath":"http://moxibook.oss-cn-shenzhen.aliyuncs.com/成长启迪/imgs/1024-勇敢少年.jpg","name":"1024-勇敢少年","path":"http://moxibook.oss-cn-shenzhen.aliyuncs.com/成长启迪/1024-勇敢少年.pdf"},{"createtime":"2017-10-30 11:34:53","id":12,"imgPath":"http://moxibook.oss-cn-shenzhen.aliyuncs.com/成长启迪/imgs/1549-中华典故.jpg","name":"1549-中华典故","path":"http://moxibook.oss-cn-shenzhen.aliyuncs.com/成长启迪/1549-中华典故.pdf"},{"createtime":"2017-10-30 11:34:52","id":8,"imgPath":"http://moxibook.oss-cn-shenzhen.aliyuncs.com/成长启迪/imgs/973-政治家成长小启迪.jpg","name":"973-政治家成长小启迪","path":"http://moxibook.oss-cn-shenzhen.aliyuncs.com/成长启迪/973-政治家成长小启迪.pdf"},{"createtime":"2017-10-30 11:34:52","id":9,"imgPath":"http://moxibook.oss-cn-shenzhen.aliyuncs.com/成长启迪/imgs/974-军事家成长小启迪.jpg","name":"974-军事家成长小启迪","path":"http://moxibook.oss-cn-shenzhen.aliyuncs.com/成长启迪/974-军事家成长小启迪.pdf"},{"createtime":"2017-10-30 11:34:52","id":10,"imgPath":"http://moxibook.oss-cn-shenzhen.aliyuncs.com/成长启迪/imgs/1023-英雄少年.jpg","name":"1023-英雄少年","path":"http://moxibook.oss-cn-shenzhen.aliyuncs.com/成长启迪/1023-英雄少年.pdf"}]
     */

    private int code;
    private String msg;
    private List<BookDetail> result;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<BookDetail> getResult() {
        return result;
    }

    public void setResult(List<BookDetail> result) {
        this.result = result;
    }

    public static class BookDetail {
        /**
         * createtime : 2017-10-30 11:34:53
         * id : 11
         * imgPath : http://moxibook.oss-cn-shenzhen.aliyuncs.com/成长启迪/imgs/1024-勇敢少年.jpg
         * name : 1024-勇敢少年
         * path : http://moxibook.oss-cn-shenzhen.aliyuncs.com/成长启迪/1024-勇敢少年.pdf
         */

        private String createtime;
        private int id;
        private String imgPath;
        private String name;
        private String path;

        public String getCreatetime() {
            return createtime;
        }

        public void setCreatetime(String createtime) {
            this.createtime = createtime;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getImgPath() {
            return imgPath;
        }

        public void setImgPath(String imgPath) {
            this.imgPath = imgPath;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }
    }
}
