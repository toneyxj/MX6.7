package com.moxi.nexams.model.papermodel;

import java.io.Serializable;

/**
 * 试卷详情
 * Created by Archer on 2017/1/9.
 */
public class PaperDetailsModel implements Serializable {
    private int psjId;
    private String psjTitle;
    private String psjOption;
    private String psjAnswer;
    private String psjAnalysis;
    private String psParentId;

    public int getPsjId() {
        return psjId;
    }

    public void setPsjId(int psjId) {
        this.psjId = psjId;
    }

    public String getPsjTitle() {
        return psjTitle;
    }

    public void setPsjTitle(String psjTitle) {
        this.psjTitle = psjTitle;
    }

    public String getPsjOption() {
        return psjOption;
    }

    public String getPsParentId() {
        return psParentId;
    }

    public void setPsParentId(String psParentId) {
        this.psParentId = psParentId;
    }

    public void setPsjOption(String psjOption) {
        this.psjOption = psjOption;
    }

    public String getPsjAnswer() {
        return psjAnswer;
    }

    public void setPsjAnswer(String psjAnswer) {
        this.psjAnswer = psjAnswer;
    }

    public String getPsjAnalysis() {
        return psjAnalysis;
    }

    public void setPsjAnalysis(String psjAnalysis) {
        this.psjAnalysis = psjAnalysis;
    }
}
