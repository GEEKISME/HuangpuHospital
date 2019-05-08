package com.biotag.huangpuhospital.javabean;

public class SinglePlaceAssetBean {
    private String placeStored;
    private int    totalAsset;
    private int    assetChecked;
    private int    assetUnchecked;

    public String getPlaceStored() {
        return placeStored;
    }

    public void setPlaceStored(String placeStored) {
        this.placeStored = placeStored;
    }

    public int getTotalAsset() {
        return totalAsset;
    }

    public void setTotalAsset(int totalAsset) {
        this.totalAsset = totalAsset;
    }

    public int getAssetChecked() {
        return assetChecked;
    }

    public void setAssetChecked(int assetChecked) {
        this.assetChecked = assetChecked;
    }

    public int getAssetUnchecked() {
        return assetUnchecked;
    }

    public void setAssetUnchecked(int assetUnchecked) {
        this.assetUnchecked = assetUnchecked;
    }
}
