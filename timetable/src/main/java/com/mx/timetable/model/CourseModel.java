package com.mx.timetable.model;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * Created by Archer on 16/8/9.
 */
public class CourseModel extends DataSupport implements Serializable {
    private int courseId;
    private String courseName;
    private String desc;
    private int courseRes;
    private int coursePressRes;
    private boolean chosen;


    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public boolean isChosen() {
        return chosen;
    }

    public int getCourseRes() {
        return courseRes;
    }

    public void setCourseRes(int courseRes) {
        this.courseRes = courseRes;
    }

    public void setChosen(boolean chosen) {
        this.chosen = chosen;
    }

    public int getCoursePressRes() {
        return coursePressRes;
    }

    public void setCoursePressRes(int coursePressRes) {
        this.coursePressRes = coursePressRes;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
