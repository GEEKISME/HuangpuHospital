package com.biotag.huangpuhospital.javabean;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Keep;

@Entity
public class SingleItemInfoBean {
    @Id
    private long id; //系统id
    private int inventoryId;//盘点id
    private int assetId;//资产id
    private String assetsSerialNum;//资产编号
    private String assetsName;//资产名字
    private String datePurchased;//购置日期
    private int departmentId;//部门id
    private String departmentName;//部门名称
    private String placeStored;//储藏场所
    private String keeper;//保管人
    private int checkResult; //  checkResult 是 1 表示已盘点，2 表示还没有盘点
    private String checkDate;//盘点日期

    @Keep
    public SingleItemInfoBean(long id, int inventoryId, int assetId, String assetsSerialNum, String assetsName,
                              String datePurchased, int departmentId, String departmentName, String placeStored,
                              String keeper, int checkResult, String checkDate) {
        this.id = id;
        this.inventoryId = inventoryId;
        this.assetId = assetId;
        this.assetsSerialNum = assetsSerialNum;
        this.assetsName = assetsName;
        this.datePurchased = datePurchased;
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.placeStored = placeStored;
        this.keeper = keeper;
        this.checkResult = checkResult;
        this.checkDate = checkDate;
    }

    public SingleItemInfoBean(){}


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(int inventoryId) {
        this.inventoryId = inventoryId;
    }

    public int getAssetId() {
        return assetId;
    }

    public void setAssetId(int assetId) {
        this.assetId = assetId;
    }

    public String getAssetsSerialNum() {
        return assetsSerialNum;
    }

    public void setAssetsSerialNum(String assetsSerialNum) {
        this.assetsSerialNum = assetsSerialNum;
    }

    public String getAssetsName() {
        return assetsName;
    }

    public void setAssetsName(String assetsName) {
        this.assetsName = assetsName;
    }

    public String getDatePurchased() {
        return datePurchased;
    }

    public void setDatePurchased(String datePurchased) {
        this.datePurchased = datePurchased;
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getPlaceStored() {
        return placeStored;
    }

    public void setPlaceStored(String placeStored) {
        this.placeStored = placeStored;
    }

    public String getKeeper() {
        return keeper;
    }

    public void setKeeper(String keeper) {
        this.keeper = keeper;
    }

    public int getCheckResult() {
        return checkResult;
    }

    public void setCheckResult(int checkResult) {
        this.checkResult = checkResult;
    }

    public String getCheckDate() {
        return checkDate;
    }

    public void setCheckDate(String checkDate) {
        this.checkDate = checkDate;
    }


    @Override
    public String toString() {
        return "SingleItemInfoBean{" + "id=" + id + ", inventoryId=" + inventoryId + ", assetId=" + assetId + ", " +
                "assetsSerialNum='" + assetsSerialNum + '\'' + ", assetsName='" + assetsName + '\'' + ", " +
                "datePurchased='" + datePurchased + '\'' + ", departmentId=" + departmentId + ", departmentName='" +
                departmentName + '\'' + ", placeStored='" + placeStored + '\'' + ", keeper='" + keeper + '\'' + ", " +
                "checkResult=" + checkResult + ", checkDate='" + checkDate + '\'' + '}';
    }
}
