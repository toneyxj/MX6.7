package com.mx.main.model;

import com.mx.mxbase.model.BaseModel;

/**
 * Created by Archer on 16/10/19.
 */
public class UserModel extends BaseModel {
    private MXUserInfo result;

    public MXUserInfo getResult() {
        return result;
    }

    public void setResult(MXUserInfo result) {
        this.result = result;
    }

    public class MXUserInfo{
        private String regTime;
        private int sex;
        private int id;
        private String mobile;
        private String name;
        private String email;

        public String getRegTime() {
            return regTime;
        }

        public void setRegTime(String regTime) {
            this.regTime = regTime;
        }

        public int getSex() {
            return sex;
        }

        public void setSex(int sex) {
            this.sex = sex;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }
}
