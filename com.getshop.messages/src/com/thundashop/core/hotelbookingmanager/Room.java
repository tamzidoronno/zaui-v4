package com.thundashop.core.hotelbookingmanager;
import com.thundashop.core.common.DataCommon;
import java.util.List;

public class Room extends DataCommon {
    List<BookedDate> bookedDates;
    public String roomType;
    public String currentCode;
    public String roomName;
    
    //Identify what lock is connected to this room.
    public String lockId;
}