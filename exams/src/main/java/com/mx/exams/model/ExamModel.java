package com.mx.exams.model;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.Serializable;

/**
 * Created by Archer on 16/8/12.
 */
public class ExamModel extends DataSupport implements Serializable {
    private String examName;//试卷名称
    private String writeDate;//答题日期
    private String examDesc;//考试描述
    private String examSubjects;//考试科目
    private String examPoint;//考试分数
    private String examState;//试卷完成度
    private String examPage;//试卷页数
    private String examFileJson;//试卷文件

    public void setExamPage(String examPage) {
        this.examPage = examPage;
    }

    public String getExamPage() {
        return examPage;
    }

    public String getExamSubjects() {
        return examSubjects;
    }

    public void setExamSubjects(String examSubjects) {
        this.examSubjects = examSubjects;
    }

    public String getExamName() {
        return examName;
    }

    public void setExamName(String examName) {
        this.examName = examName;
    }

    public String getWriteDate() {
        return writeDate;
    }

    public void setWriteDate(String writeDate) {
        this.writeDate = writeDate;
    }

    public String getExamDesc() {
        return examDesc;
    }

    public void setExamDesc(String examDesc) {
        this.examDesc = examDesc;
    }

    public String getExamState() {
        return examState;
    }

    public void setExamState(String examState) {
        this.examState = examState;
    }

    public String getExamPoint() {
        return examPoint;
    }

    public void setExamPoint(String examPoint) {
        this.examPoint = examPoint;
    }

    public String getExamFileJson() {
        return examFileJson;
    }

    public void setExamFileJson(String examFileJson) {
        this.examFileJson = examFileJson;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
