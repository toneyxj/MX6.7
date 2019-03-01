package com.mx.exams.model;

/**
 * Created by zhengdelong on 16/9/29.
 */

public class ExaModel {

//    "address": "黑龙江卷",
//            "createtime": "2016-09-26 10:09:48",
//            "id": 208,
//            "isMemberDone": 0,
//            "recommend": 0,
//            "secId": 3,
//            "semId": 21,
//            "state": 0,
//            "subId": 1,
//            "subjectName": "语文",
//            "title": "2014-2015学年度黑龙江省绥化市三校第一学期高二期中联考",
//            "type": "文理综合",
//            "updatetime": "2016-09-26 10:09:48"

    private String address;
    private String createtime;
    private long id;
    private int isMemberDone;
    private int recommend;
    private long secId;
    private long semId;
    private int state;
    private long subId;
    private long cobId;
    private String subjectName;
    private String title;
    private String type;// 1  为试卷，
    private String updatetime;
    private String pdfUrl;

    public String getPdfUrl() {
        return pdfUrl;
    }

    public void setPdfUrl(String pdfUrl) {
        this.pdfUrl = pdfUrl;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getIsMemberDone() {
        return isMemberDone;
    }

    public void setIsMemberDone(int isMemberDone) {
        this.isMemberDone = isMemberDone;
    }

    public int getRecommend() {
        return recommend;
    }

    public void setRecommend(int recommend) {
        this.recommend = recommend;
    }

    public long getSecId() {
        return secId;
    }

    public void setSecId(long secId) {
        this.secId = secId;
    }

    public long getSemId() {
        return semId;
    }

    public void setSemId(long semId) {
        this.semId = semId;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public long getSubId() {
        return subId;
    }

    public void setSubId(long subId) {
        this.subId = subId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(String updatetime) {
        this.updatetime = updatetime;
    }

    public long getCobId() {
        return cobId;
    }

    public void setCobId(long cobId) {
        this.cobId = cobId;
    }
}
