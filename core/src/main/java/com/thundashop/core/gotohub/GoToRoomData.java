package com.thundashop.core.gotohub;

import com.thundashop.core.common.DataCommon;
import com.thundashop.core.gotohub.dto.RatePlan;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class GoToRoomData {
    public String goToRoomTypeCode="-1";
    public String bookingEngineTypeId="";
    public String name="";
    public String description="";
    public String about="";
    public int numberOfUnits;
    public String roomCategory="";
    public String sizeMeasurement="";
    public String nonSmoking="";
    public int maxGuest=1;
    public int numberOfAdults;
    public int numberOfChildren;
    public String status="";
    public List<String> images = new ArrayList<String>();
    public List<RatePlan> ratePlans = new ArrayList<RatePlan>();
    public boolean newRoomPriceSystem=false;

}
