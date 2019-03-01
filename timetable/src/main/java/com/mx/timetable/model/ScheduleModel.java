package com.mx.timetable.model;


import com.mx.mxbase.model.BaseModel;

import java.io.Serializable;

/**
 * 课程表model
 * Created by Archer on 16/8/9.
 */
public class ScheduleModel extends BaseModel implements Serializable {
    private boolean chosen;
    private String desc;

    public boolean isChosen() {
        return chosen;
    }

    public void setChosen(boolean chosen) {
        this.chosen = chosen;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
