package com.thundashop.core.pmsmanager;

import com.thundashop.core.common.DataCommon;

import java.util.Date;

public class PmsLog extends DataCommon {
    public Date dateEntry = new Date();
    public String logText = "";
    public String userId = "";
    public String bookingId = "";
    public String bookingItemType = "";
    public String bookingItemId = "";
    public String roomId = "";
    public String tag = "";
    public boolean includeAll = false;
    public String logType;
    
    public String userName = "";
    public String roomName = "";

    @Override
    public String toString() {
        return "PmsLog{" +
                "id='" + id + '\'' +
                ", storeId='" + storeId + '\'' +
                ", rowCreatedDate=" + rowCreatedDate +
                ", lastModified=" + lastModified +
                ", lastModifiedByUserId='" + lastModifiedByUserId + '\'' +
                ", dateEntry=" + dateEntry +
                ", logText='" + logText + '\'' +
                ", userId='" + userId + '\'' +
                ", bookingId='" + bookingId + '\'' +
                ", bookingItemType='" + bookingItemType + '\'' +
                ", bookingItemId='" + bookingItemId + '\'' +
                ", roomId='" + roomId + '\'' +
                ", tag='" + tag + '\'' +
                ", includeAll=" + includeAll +
                ", logType='" + logType + '\'' +
                ", userName='" + userName + '\'' +
                ", roomName='" + roomName + '\'' +
                '}';
    }
}
