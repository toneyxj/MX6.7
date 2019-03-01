package com.moxi.haierc.model;

import java.io.Serializable;

/**
 * Created by King on 2017/12/21.
 */

public class PasswordClickModel implements Serializable {
    private String itemName;
    private boolean isSelected;
    private boolean showCheckBox;

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isShowCheckBox() {
        return showCheckBox;
    }

    public void setShowCheckBox(boolean showCheckBox) {
        this.showCheckBox = showCheckBox;
    }
}
