package com.mx.user.model;

import com.mx.mxbase.model.BaseModel;

import java.util.List;

/**
 * Created by Archer on 16/11/8.
 */
public class StudentModel extends BaseModel {
    private List<StudentWeed> result;

    public List<StudentWeed> getResult() {
        return result;
    }

    public void setResult(List<StudentWeed> result) {
        this.result = result;
    }

    public class StudentWeed {
        private int id;				/* ID主键 */
        private String name;			/* 用户姓名 */
        private String mobile;			/* 手机号码（作为登录用户名） */
        private String password;		/* 密码 */
        private int type;			/* 用户类型：0-学生，1-老师 */
        //        private Long subjectId;			/* 用户所教授的课程ID==>外键，引用T_Subject的sub_id（必须type为1该字段才有值） */
        private String school;			/* 所在学校 */
        private String grade;			/* 所在年级 */
        private String parentMobile;	/* 父母电话 */
        private String email;			/* 邮箱 */
        private String headimg;			/* 用户头像名称（存储在客户端，返回给客户端控制显示） */
        private int online;			/* 用户是否在线：0-离线，1-在线 */
        private String lasttime;			/* 用户最后一次请求服务器的时间 */

//        private Subject subject;		/* 用户关联的科目对象，是用户类型为1的老师所教授的科目 */

//        public Subject getSubject() {
//            return subject;
//        }
//
//        public void setSubject(Subject subject) {
//            this.subject = subject;
//        }

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

        public Integer getType() {
            return type;
        }

        public void setType(Integer type) {
            this.type = type;
        }

//        public Long getSubjectId() {
//            return subjectId;
//        }
//
//        public void setSubjectId(Long subjectId) {
//            this.subjectId = subjectId;
//        }

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

        public Integer getOnline() {
            return online;
        }

        public void setOnline(Integer online) {
            this.online = online;
        }

        public String getLasttime() {
            return lasttime;
        }

        public void setLasttime(String lasttime) {
            this.lasttime = lasttime;
        }

    }

    /**
     * 科目实体
     *
     * @author ZhangWei
     */
    public class Subject {
        private Long id;        //ID主键
        private String name;    //科目名称

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

}
