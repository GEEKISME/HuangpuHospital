package com.biotag.huangpuhospital.javabean;

public class AssetSummaryBean {
    private String placeStored;
    private int    total;
    private int    checkedNum;
    private int    uncheckedNum;

    public String getPlaceStored() {
        return placeStored;
    }

    public void setPlaceStored(String placeStored) {
        this.placeStored = placeStored;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getCheckedNum() {
        return checkedNum;
    }

    public void setCheckedNum(int checkedNum) {
        this.checkedNum = checkedNum;
    }

    public int getUncheckedNum() {
        return uncheckedNum;
    }

    public void setUncheckedNum(int uncheckedNum) {
        this.uncheckedNum = uncheckedNum;
    }
}
