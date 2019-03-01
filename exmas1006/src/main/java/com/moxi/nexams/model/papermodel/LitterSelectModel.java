package com.moxi.nexams.model.papermodel;

import java.util.List;

/**
 * Created by Archer on 2017/1/11.
 */
public class LitterSelectModel {
    private PaperDetailsModel pdm;
    private List<DetailsTestModel> listOption;

    public PaperDetailsModel getPdm() {
        return pdm;
    }

    public void setPdm(PaperDetailsModel pdm) {
        this.pdm = pdm;
    }

    public List<DetailsTestModel> getListOption() {
        return listOption;
    }

    public void setListOption(List<DetailsTestModel> listOption) {
        this.listOption = listOption;
    }
}
