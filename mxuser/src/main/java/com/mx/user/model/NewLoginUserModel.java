package com.mx.user.model;

import com.mx.mxbase.model.BaseModel;

/**
 * Created by Archer on 2016/12/22.
 */
public class NewLoginUserModel extends BaseModel {
    private NewLoginUser result;

    public NewLoginUser getResult() {
        return result;
    }

    public void setResult(NewLoginUser result) {
        this.result = result;
    }

    public class NewLoginUser {
        private int id;				/* ID主键 */
        private String name;			/* 用户姓名 */
        private String mobile;			/* 手机号码（作为登录用户名） */
        private String password;		/* 密码 */
        private int type;			/* 用户类型：0-学生，1-老师 */
        private String school;			/* 所在学校 */
        private String grade;			/* 所在年级 */
        private String parentMobile;	/* 父母电话 */
        private String email;			/* 邮箱 */
        private String headimg;			/* 用户头像名称（存储在客户端，返回给客户端控制显示） */
        private int online;			/* 用户是否在线：0-离线，1-在线 */
        private String lasttime;			/* 用户最后一次请求服务器的时间 */
        private int lesson;			/* 用户是否在上课：0-未上课，1-在上课（必须usr_type为1该字段才有值） */

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getSchool() {
            return school;
        }

        public void setSchool(String school) {
            this.school = school;
        }

        public String getGrade() {
            return grade;
        }

        public void setGrade(String grade) {
            this.grade = grade;
        }

        public String getParentMobile() {
            return parentMobile;
        }

        public void setParentMobile(String parentMobile) {
            this.parentMobile = parentMobile;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getHeadimg() {
            return headimg;
        }

        public void setHeadimg(String headimg) {
            this.headimg = headimg;
        }

        public int getOnline() {
            return online;
        }

        public void setOnline(int online) {
            this.online = online;
        }

        public String getLasttime() {
            return lasttime;
        }

        public void setLasttime(String lasttime) {
            this.lasttime = lasttime;
        }

        public int getLesson() {
            return lesson;
        }

        public void setLesson(int lesson) {
            this.lesson = lesson;
        }
    }
}
