package com.mycompany.faceedge.facesyncservice.Order;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class Order {

    private String orderID;
    private int status;

    private String tenantID;
    private int userType;



    private String userID;

    private String image;


    private String enterStationLineCode;
    private String enterStationCode;


    private String exitStationLineCode;
    private String exitStationCode;



    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;



    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }



    public String getTenantID() {
        return tenantID;
    }

    public void setTenantID(String tenantID) {
        this.tenantID = tenantID;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }



    public String getEnterStationCode() {
        return enterStationCode;
    }

    public void setEnterStationCode(String enterStationCode) {
        this.enterStationCode = enterStationCode;
    }

    public String getEnterStationLineCode() {
        return enterStationLineCode;
    }

    public void setEnterStationLineCode(String enterStationLineCode) {
        this.enterStationLineCode = enterStationLineCode;
    }

    public String getExitStationLineCode() {
        return exitStationLineCode;
    }

    public void setExitStationLineCode(String exitStationLineCode) {
        this.exitStationLineCode = exitStationLineCode;
    }

    public String getExitStationCode() {
        return exitStationCode;
    }

    public void setExitStationCode(String exitStationCode) {
        this.exitStationCode = exitStationCode;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }


}
