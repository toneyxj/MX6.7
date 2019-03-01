package com.mx.exams.model;

import org.litepal.crud.DataSupport;

/**
 * Created by Archer on 16/10/14.
 */
public class DBExamsModel extends DataSupport {
    public String baocunid;
    public String examsDetails;
    public String subjectId;
    public String subjectName;
    public String cos_sem_id;

    public String getCos_sem_id() {
        return cos_sem_id;
    }

    public void setCos_sem_id(String cos_sem_id) {
        this.cos_sem_id = cos_sem_id;
    }

    public String getBaocunid() {
        return baocunid;
    }

    public void setBaocunid(String baocunid) {
        this.baocunid = baocunid;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getExamsDetails() {
        return examsDetails;
    }

    public void setExamsDetails(String examsDetails) {
        this.examsDetails = examsDetails;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }
}
