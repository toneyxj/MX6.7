package com.moxi.nexams.model.papermodel;

import java.io.Serializable;

/**
 * 试卷model
 * Created by Archer on 2017/1/9.
 */
public class PaperModelDesc implements Serializable {
    private int ppsId;
    private String ppsMainTitle;
    private String ppsDeputyTitle;
    private int total;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPpsId() {
        return ppsId;
    }

    public void setPpsId(int ppsId) {
        this.ppsId = ppsId;
    }

    public String getPpsMainTitle() {
        return ppsMainTitle;
    }

    public void setPpsMainTitle(String ppsMainTitle) {
        this.ppsMainTitle = ppsMainTitle;
    }

    public String getPpsDeputyTitle() {
        return ppsDeputyTitle;
    }

    public void setPpsDeputyTitle(String ppsDeputyTitle) {
        this.ppsDeputyTitle = ppsDeputyTitle;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
