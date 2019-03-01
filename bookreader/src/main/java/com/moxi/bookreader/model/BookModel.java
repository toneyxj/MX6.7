package com.moxi.bookreader.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by King on 2017/10/31.
 */

public class BookModel implements Serializable {


    /**
     * code : 0
     * msg : success
     * result : [{"count":5,"c_id":8,"c_createtime":"2017-10-30 14:06:29","c_name":"成长启迪","c_img_path":"http://moxibook.oss-cn-shenzhen.aliyuncs.com/coveImg/成长启迪.png"},{"count":26,"c_id":11,"c_createtime":"2017-10-30 11:06:29","c_name":"国学今读","c_img_path":"http://moxibook.oss-cn-shenzhen.aliyuncs.com/coveImg/国学今读.png"},{"count":5,"c_id":16,"c_createtime":"2017-10-30 11:06:29","c_name":"童话故事","c_img_path":"http://moxibook.oss-cn-shenzhen.aliyuncs.com/coveImg/童话故事.png"},{"count":1,"c_id":13,"c_createtime":"2017-10-30 11:06:29","c_name":"名人传记","c_img_path":"http://moxibook.oss-cn-shenzhen.aliyuncs.com/coveImg/名人传记.png"},{"count":5,"c_id":18,"c_createtime":"2017-10-30 11:06:29","c_name":"兴趣课堂","c_img_path":"http://moxibook.oss-cn-shenzhen.aliyuncs.com/coveImg/兴趣课堂.png"},{"count":10,"c_id":10,"c_createtime":"2017-10-30 11:06:29","c_name":"儿童百科","c_img_path":"http://moxibook.oss-cn-shenzhen.aliyuncs.com/coveImg/儿童百科.png"},{"count":3,"c_id":15,"c_createtime":"2017-10-30 11:06:29","c_name":"十万个为什么","c_img_path":"http://moxibook.oss-cn-shenzhen.aliyuncs.com/coveImg/十万个为什么.png"},{"count":5,"c_id":20,"c_createtime":"2017-10-30 11:06:29","c_name":"自然科学","c_img_path":"http://moxibook.oss-cn-shenzhen.aliyuncs.com/coveImg/自然科学.png"},{"count":9,"c_id":12,"c_createtime":"2017-10-30 11:06:29","c_name":"科普系列","c_img_path":"http://moxibook.oss-cn-shenzhen.aliyuncs.com/coveImg/科普系列.png"},{"count":22,"c_id":17,"c_createtime":"2017-10-30 11:06:29","c_name":"小故事","c_img_path":"http://moxibook.oss-cn-shenzhen.aliyuncs.com/coveImg/小故事.png"},{"count":2,"c_id":9,"c_createtime":"2017-10-30 11:06:29","c_name":"读本系列","c_img_path":"http://moxibook.oss-cn-shenzhen.aliyuncs.com/coveImg/读本系列.png"},{"count":8,"c_id":14,"c_createtime":"2017-10-30 11:06:29","c_name":"趣味乐园","c_img_path":"http://moxibook.oss-cn-shenzhen.aliyuncs.com/coveImg/趣味乐园.png"},{"count":4,"c_id":19,"c_createtime":"2017-10-30 11:06:29","c_name":"悦读越好","c_img_path":"http://moxibook.oss-cn-shenzhen.aliyuncs.com/coveImg/悦读越好.png"}]
     */

    private int code;
    private String msg;
    private List<BookBean> result;

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

    public List<BookBean> getResult() {
        return result;
    }

    public void setResult(List<BookBean> result) {
        this.result = result;
    }

    public static class BookBean {
        /**
         * count : 5
         * c_id : 8
         * c_createtime : 2017-10-30 14:06:29
         * c_name : 成长启迪
         * c_img_path : http://moxibook.oss-cn-shenzhen.aliyuncs.com/coveImg/成长启迪.png
         */

        private int count;
        private int c_id;
        private String c_createtime;
        private String c_name;
        private String c_img_path;

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public int getC_id() {
            return c_id;
        }

        public void setC_id(int c_id) {
            this.c_id = c_id;
        }

        public String getC_createtime() {
            return c_createtime;
        }

        public void setC_createtime(String c_createtime) {
            this.c_createtime = c_createtime;
        }

        public String getC_name() {
            return c_name;
        }

        public void setC_name(String c_name) {
            this.c_name = c_name;
        }

        public String getC_img_path() {
            return c_img_path;
        }

        public void setC_img_path(String c_img_path) {
            this.c_img_path = c_img_path;
        }
    }
}
